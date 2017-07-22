package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.util.Tuple;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by CYF on 2017/7/15.
 */
public class EnhancedAlphaMinerView {
    public Petrinet makePetriNet(Set<Tuple> resultSet, Set<Character> start, Set<Character> end) {
        Petrinet resultNet = PetrinetFactory.newPetrinet("Result Petri Net");

        int num = 1;

        Place startPlace = resultNet.addPlace("p" + num++);

        //transition map
        //key = name
        Map<Character, Transition> transitionMap = new HashMap<>();

        //add transition into net for every start transition
        for (char each : start) {
            Transition t = resultNet.addTransition(each + "");
            transitionMap.put(each, t);
            resultNet.addArc(startPlace, t);
        }
        //add transition into net for tuple
        for (Tuple each : resultSet) {
            //middle place
            Place p = resultNet.addPlace("p" + num++);
            for (char c : each.getLeftPart()) {
                if (!transitionMap.keySet().contains(c)) {
                    Transition t = resultNet.addTransition(c + "");
                    transitionMap.put(c, t);
                }
                //add arc from left transition to middle place
                resultNet.addArc(transitionMap.get(c), p);
                //add arc from middle place to right transition
                for (char inC : each.getRightPart()) {
                    if (transitionMap.keySet().contains(inC)) {
                        resultNet.addArc(p, transitionMap.get(inC));
                    } else {
                        Transition t = resultNet.addTransition(inC + "");
                        transitionMap.put(inC, t);
                        resultNet.addArc(p, t);
                    }
                }
            }
        }
        //add transition into net for end place
        Place endPlace = resultNet.addPlace("p" + num++);
        for (char each : end) {
            resultNet.addArc(transitionMap.get(each), endPlace);
        }

        return resultNet;
    }

    public void addLostPlaces(Set<int[]> PVSa, Petrinet ori) {
        Collection<Transition> all = ori.getTransitions();
        Collection<Place> placeCollection = ori.getPlaces();
        int placeCnt = placeCollection.size();
        Map<String, Transition> tansitionMap = new HashMap<>();

        char reach_char = 'A';
        for (Transition transition : all) {
            if(transition.getLabel().charAt(0)!='i') {
                tansitionMap.put(transition.getLabel().charAt(0) + "", transition);
                reach_char = transition.getLabel().charAt(0);
                System.out.println("reach_char = " + reach_char);
            }
        }
        for (Transition transition : all) {
            if(transition.getLabel().charAt(0)=='i') {
                tansitionMap.put((char)(++reach_char)+"", transition);
//                System.out.println("reach_char+(1) = " + (++reach_char));
//                reach_char++;
            }
        }

        for (String key:tansitionMap.keySet()){
            System.out.print(key);
        }
        System.out.println();
        for (int[] each : PVSa) {
            System.out.println("Pvsa:");
            for (int i = 0; i < each.length; i++) {
                System.out.print(each[i]+"\t");
            }
            System.out.println("-----------");
            Place place = ori.addPlace("p" + placeCnt++);
            boolean leftAdd = false;
            boolean rightAdd = false;
            for (int i = 0; i < each.length - 1; i++) {
                char label = (char) ('A' + i);
                if (each[i] == 1) {
                    System.out.println("label+\"\" = " + label+"");
                    ori.addArc(tansitionMap.get(label + ""), place);
                    leftAdd = true;
                } else if (each[i] == -1) {
                    ori.addArc(place, tansitionMap.get(label + ""));
                    rightAdd = true;
                }
            }
            if (!(leftAdd&&rightAdd)){
                for (PetrinetEdge e:ori.getInEdges(place)) {
                    System.out.println(e.getLabel());
                    ori.removeEdge(e);
                }
                for (PetrinetEdge e:ori.getOutEdges(place)) {
                    System.out.println(e.getLabel());
                    ori.removeEdge(e);
                }
                System.out.println("place.getLabel() = " + place.getLabel());
                ori.removePlace(place);
                placeCnt--;
            }
        }
    }
}

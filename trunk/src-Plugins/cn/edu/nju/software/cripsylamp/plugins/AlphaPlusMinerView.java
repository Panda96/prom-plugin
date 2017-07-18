package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.util.ThreeTransitionTuple;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import java.util.*;

/**
 * Created by CYF on 2017/7/18.
 */
public class AlphaPlusMinerView {

    public void addLostTransitions(Petrinet net, Set<ThreeTransitionTuple> transitionSet){
        Collection<Transition> transitions = net.getTransitions();
        Map<String,Transition> transitionsMap = new HashMap<>();
        for (Transition each:transitions) {
            transitionsMap.put(each.getLabel(),each);
        }

        for (ThreeTransitionTuple each: transitionSet) {
            char left = each.getLeft(),right = each.getRight(),middle = each.getLoop1Char();
            Transition t;
            if(transitionsMap.containsKey(middle+"")){
                t =transitionsMap.get(middle+"");
            }else {
                t=net.addTransition(middle+"");
                transitionsMap.put(middle+"",t);
            }
            Transition leftTransition = transitionsMap.get(left+"");
            Transition rightTransition = transitionsMap.get(right+"");
            Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> leftToRightEdges = net.getOutEdges(leftTransition);
            Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> rightFromLeftEdges = net.getInEdges(rightTransition);
            Set<PetrinetNode>leftToPlace = new HashSet();
            for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> eachEdge:leftToRightEdges) {
                leftToPlace.add(eachEdge.getTarget());
            }
            Set<PetrinetNode>rightFromPlace = new HashSet<>();
            for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> eachEdge:rightFromLeftEdges) {
                rightFromPlace.add(eachEdge.getSource());
            }
            leftToPlace.removeAll(rightFromPlace);
            for(PetrinetNode eachPlace:leftToPlace){
                net.addArc((Place)eachPlace,t);
                net.addArc(t,(Place)eachPlace);
            }
        }
    }
}

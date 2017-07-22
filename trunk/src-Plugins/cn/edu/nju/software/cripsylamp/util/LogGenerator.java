package cn.edu.nju.software.cripsylamp.util;

import cn.edu.nju.software.cripsylamp.beans.Trace;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

import java.util.*;

/**
 * Created by CYF on 2017/7/21.
 */
public class LogGenerator {
    public Trace generateWlog(Petrinet petrinet) {
        List<Place> startPlaces = new ArrayList<>();
        for (Place p : petrinet.getPlaces()) {
            if (petrinet.getInEdges(p).size() == 0) {
                startPlaces.add(p);
            }
        }
        return null;
    }

    public List<String> fromPlaceToTransition(Place p, List<String> ori, Petrinet net, Map<String, TransitionWrapper> transMap, String lastStr) {
        for (PetrinetEdge edge : net.getInEdges(p)) {
            //选择关系
            Map<String, TransitionWrapper> transMapNew = new HashMap<>();
            for (String key : transMap.keySet()) {
                transMapNew.put(key, transMap.get(key).clone());
            }

            if (net.getInEdges((Transition) edge.getTarget()).size() == 1) {
                String nextStr = lastStr + ((Transition) edge.getTarget()).getLabel();
//                fromTransitionToPlace()
            } else {
                TransitionWrapper wrapper = transMapNew.get(((Transition) edge.getTarget()).getLabel());
                if (wrapper != null) {
                    wrapper.addInTokens();
                    wrapper.addBeforeSequence(lastStr);
                    if (wrapper.hasFullIn()) {

                    } else {

                    }
                }
            }
        }
        return null;
    }


    public List<String> fromTransitionToPlace(Transition t, List<String> ori, Petrinet net) {
        return null;
    }

    public Trace generateLog(Petrinet petrinet, Trace trace) {
        Set<Transition> newTransitions = new HashSet<>();
        List<EachTrace> tracesList = new ArrayList<>();
        for (String e : trace.getTraces().values()) {
            tracesList.add(new EachTrace(e));
        }
        int nameNum = 0;
        for (Transition each : petrinet.getTransitions()) {
            if (each.getLabel().startsWith("i") || each.isInvisible()) {
                newTransitions.add(each);
            } else {
                nameNum++;
            }
        }

        for (Transition each : newTransitions) {
            List<PetrinetNode> lastPlaces = new ArrayList<>();
            List<PetrinetNode> nextPlaces = new ArrayList<>();
            List<Pair<PetrinetNode, PetrinetNode>> placePairs = new ArrayList<>();
            for (PetrinetEdge edge : petrinet.getInEdges(each)) {
                lastPlaces.add((PetrinetNode) edge.getSource());
            }
            for (PetrinetEdge edge : petrinet.getOutEdges(each)) {
                nextPlaces.add((Place) edge.getTarget());
            }
            Set<PetrinetNode> nodes = new HashSet<>();
            for (PetrinetNode nextOne : nextPlaces) {
                for (PetrinetNode lastOne : lastPlaces) {
                    boolean isRedo = compareNextWithLast(each, lastOne, petrinet, new ArrayList<PetrinetNode>());
                    if (isRedo) {
                        placePairs.add(new Pair<PetrinetNode, PetrinetNode>(lastOne, nextOne));
                    }
                }

            }

        }
        return null;
    }

    public boolean compareNextWithLast(PetrinetNode node, PetrinetNode compareLastNode, Petrinet net, List<PetrinetNode> hasMet) {
        if (node == compareLastNode) {
            return true;
        }

        List<PetrinetNode> nodeList = new ArrayList<>();
        for (PetrinetEdge edge : net.getOutEdges(node)) {
            if (!hasMet.contains((PetrinetNode) edge.getTarget()))//后面没有访问过的节点 避免循环
                nodeList.add((PetrinetNode) edge.getTarget());
        }
        if (nodeList.size() == 0) {     //end place
            return false;
        }
        boolean nextSame = false;
        for (PetrinetNode n : nodeList) {
            if (n == compareLastNode) {//相同
                return true;
            } else {
                //不相同 将该节点加入访问过得节点 继续访问后面的节点
                List<PetrinetNode> met = new ArrayList<PetrinetNode>() {
                    {
                        addAll(hasMet);
                        add(n);
                    }
                };
                nextSame = nextSame || compareNextWithLast(n, compareLastNode, net, met);
                if (nextSame == true)//遇到相同节点
                    return true;
            }
        }
        return nextSame;//一定是false
    }

    public static Pair<Trace, Petrinet> SimpleTraceGenerator(Trace trace, Petrinet petrinet) {
        //new transitions to store invisible transitions which are named
        List<Transition> newTransitions = new ArrayList<>();
        //trace list to store new trace with named invisible transitions
        List<EachTrace> traceList = new ArrayList<>();
        for (String t : trace.getTraces().values()) {
            traceList.add(new EachTrace(t));
        }
        List<EachTrace> resultTrace = new ArrayList<EachTrace>();
//        resultTrace.addAll(traceList);
//        int nameNum = 0;
//        for (Transition each : petrinet.getTransitions()) {
//            if (each.getLabel().startsWith("i") || each.isInvisible()) {
//                newTransitions.add(each);//not invisible transitions
//            } else {
//                nameNum++;
//            }
//        }
        //generate a new petrinet with all transitions named
        Pair<Petrinet, Integer> netPair = petrinetProducer(petrinet);
        Petrinet addNamePetrinet = netPair.getFirst();
        int visibleNum = netPair.getSecond();
        for (Transition t : addNamePetrinet.getTransitions()) {
            if (t.getLabel().charAt(0) >= 'A' + visibleNum) {
                newTransitions.add(t);
                System.out.println("t.getLabel() = " + t.getLabel());
            }
        }
        for (int i = 0; i < newTransitions.size(); i++) {
            String name = newTransitions.get(i).getLabel();

            Set<String> nextTransitions = new HashSet<>();
            Set<String> lastTransitions = new HashSet<>();

            for (PetrinetEdge e : addNamePetrinet.getInEdges(newTransitions.get(i))) {
                for (PetrinetEdge ein : addNamePetrinet.getInEdges((DirectedGraphNode) e.getSource())) {
                    lastTransitions.add(((Transition) ein.getSource()).getLabel());
                }
            }
            for (PetrinetEdge e : addNamePetrinet.getOutEdges(newTransitions.get(i))) {
                for (PetrinetEdge eout : addNamePetrinet.getOutEdges(((DirectedGraphNode) e.getTarget()))) {
                    nextTransitions.add(((Transition) eout.getTarget()).getLabel());
                }
            }
            for (String last : lastTransitions) {
                for (String next : nextTransitions) {
                    String beforeChange = last.charAt(0) + "" + next.charAt(0) + "";
                    String afterChange = last.charAt(0) + "" + name + next.charAt(0) + "";
                    for (EachTrace toChange : traceList) {
                        if (toChange.getTraceString().contains(beforeChange)) {
                            String changeSeq = toChange.getTraceString().replace(beforeChange, afterChange);
                            resultTrace.add(new EachTrace(changeSeq));
                            toChange.access();
                        }
                    }
                    for (EachTrace et : traceList) {
                        if (!et.isHasAcessed()) {
                            resultTrace.add(et);
                        }
                    }
                    traceList = new ArrayList<>();
                    traceList.addAll(resultTrace);
                    resultTrace = new ArrayList<>();
                }
                if (nextTransitions.size() == 0) {
                    String beforeChange = last.charAt(0) + "";
                    String afterChange = last.charAt(0) + "" + name;
                    for (EachTrace toChange : traceList) {
                        if (toChange.getTraceString().endsWith(beforeChange)) {
                            String changeSeq = toChange.getTraceString().replace(beforeChange, afterChange);
                            resultTrace.add(new EachTrace(changeSeq));
                            toChange.access();
                        }
                    }
                    for (EachTrace et : traceList) {
                        if (!et.isHasAcessed()) {
                            resultTrace.add(et);
                        }
                    }
                    traceList = new ArrayList<EachTrace>();
                    traceList.addAll(resultTrace);
                    resultTrace = new ArrayList<>();
                }
            }
            if (lastTransitions.size() == 0) {
                for (String next:nextTransitions) {
                    String beforeChange = next.charAt(0) + "";
                    String afterChange = name+next.charAt(0)+"" ;
                    for (EachTrace toChange : traceList) {
                        if (toChange.getTraceString().startsWith(beforeChange)) {
                            String changeSeq = toChange.getTraceString().replace(beforeChange, afterChange);
                            resultTrace.add(new EachTrace(changeSeq));
                            toChange.access();
                        }
                    }
                }
                for (EachTrace et : traceList) {
                    if (!et.isHasAcessed()) {
                        resultTrace.add(et);
                    }
                }
                traceList = new ArrayList<EachTrace>();
                traceList.addAll(resultTrace);
                resultTrace = new ArrayList<>();
            }
        }
        for (EachTrace res : traceList) {
            System.out.println("res = " + res.getTraceString());
        }

        Trace result = new Trace(traceList, visibleNum);

        return new Pair<>(result, addNamePetrinet);
    }

    public static Pair<Petrinet, Integer> petrinetProducer(Petrinet petrinet) {
        Petrinet result = new PetrinetImpl("enhanced Petrinet");
        Map<Transition, Transition> transMap = new HashMap<>();
        Map<Place, Place> placeMap = new HashMap<>();
        //add visible transitions
        for (Transition each : petrinet.getTransitions()) {
            if (!each.isInvisible()) {
                Transition newTrans = result.addTransition(each.getLabel());
                transMap.put(each, newTrans);
            }
        }
        //add all places
        for (Place each : petrinet.getPlaces()) {
            Place newPlace = result.addPlace(each.getLabel());
            placeMap.put(each, newPlace);
        }
        //calculate visible transition num
        int visibleNum = result.getTransitions().size();
        int nameNum = visibleNum;
        //add invisible transitions
        //add name for each
        for (Transition each : petrinet.getTransitions()) {
            if (each.isInvisible()) {
                String name = (char) ('A' + nameNum) + "";
                nameNum++;
                Transition t = result.addTransition(name);
                transMap.put(each, t);
            }
        }
        //add arcs
        for (Transition each : petrinet.getTransitions()) {
            Transition resultTrans = transMap.get(each);
            for (PetrinetEdge e : petrinet.getOutEdges(each)) {
                Place outPlace = (Place) e.getTarget();
                Place resultPlace = placeMap.get(outPlace);
                result.addArc(resultTrans, resultPlace);
            }
            for (PetrinetEdge e : petrinet.getInEdges(each)) {
                Place inPlace = (Place) e.getSource();
                Place resultPlace = placeMap.get(inPlace);
                result.addArc(resultPlace, resultTrans);
            }
        }
        return new Pair<>(result, visibleNum);
    }
}

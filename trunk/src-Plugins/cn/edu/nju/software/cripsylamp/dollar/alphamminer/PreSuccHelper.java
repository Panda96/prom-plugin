package cn.edu.nju.software.cripsylamp.dollar.alphamminer;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by CYF on 2017/7/24.
 */
public class PreSuccHelper {
    public static HashSet<Place> getPrePlaces(Petrinet petrinet, Transition transition) {
        HashSet<Place> _pre = new HashSet<>();
        petrinet.getInEdges(transition).forEach(petrinetEdge -> _pre.add((Place) petrinetEdge.getSource()));
        return _pre;
    }

    public static HashSet<Place> getSuccPlaces(Petrinet petrinet, Transition transition) {
        HashSet<Place> _succ = new HashSet<>();
        petrinet.getOutEdges(transition).forEach(petrinetEdge -> _succ.add((Place) petrinetEdge.getTarget()));
        return _succ;
    }

    public static HashSet<Transition> getPreTransitions(Petrinet petrinet, Place place) {
        HashSet<Transition> _pre = new HashSet<>();
        petrinet.getInEdges(place).forEach(petrinetEdge -> _pre.add((Transition) petrinetEdge.getSource()));
        return _pre;
    }

    public static HashSet<Transition> getSuccTransitions(Petrinet petrinet, Place place) {
        HashSet<Transition> _succ = new HashSet<>();
        petrinet.getOutEdges(place).forEach(petrinetEdge -> _succ.add((Transition) petrinetEdge.getTarget()));
        return _succ;
    }
}

package cn.edu.nju.software.cripsylamp.logRepair;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;

import java.util.*;

/**
 * @author keenan on 25/10/2017
 */
public class Table2Petrinet {
    /**
     * transfer adjacency list to Petri Net
     *
     * @param table adjacency list
     * @return Petri Net
     */
    public Petrinet tranfer2net(List<ConnectItem> table) {
        int p_cnt = 0;
        Petrinet petrinet = PetrinetFactory.newPetrinet("petri");
        Map<String, Transition> stringTransitionMap = new HashMap<>();
        Map<Transition, String> transitionStringMap = new HashMap<>();
        Map<Transition, List<Set<Transition>>> transitionSetListMap = new HashMap<>();

        for (ConnectItem item : table) {
            printSet(item.getTaskSet());

            // 对于没有前驱任务的任务集合T0，
            // 对于其中的每一个任务t，
            // 创建一个库所p，创建一个名字是t的变迁，创建一条弧连接

            // 任务集合中的t
            List<Transition> currentTransitions = new ArrayList<>();
            // 后继
            Set<Transition> succTransitions = new HashSet<>();
            if (item.getPrecursor() == null || item.getPrecursor().isEmpty()) {
                for (String s : item.getTaskSet()) {
                    Place place = petrinet.addPlace((p_cnt++) + "");
                    Transition transition = petrinet.addTransition(s);
                    currentTransitions.add(transition);
                    stringTransitionMap.put(s, transition);
                    transitionStringMap.put(transition, s);
                    petrinet.addArc(place, transition);
                }
            } else {
                for (String s : item.getTaskSet()) {
                    Transition transition = stringTransitionMap.get(s);
                    if (transition == null) {
                        transition = petrinet.addTransition(s);
                        stringTransitionMap.put(s, transition);
                        transitionStringMap.put(transition, s);
                    }
                    currentTransitions.add(transition);
                }
            }

            // 对于没有后继任务的任务集合Tn，对于其中的每一个任务t，创建一个库所p，连接tp
            if (item.getSuccessor() == null || item.getSuccessor().isEmpty()) {
                for (String s : item.getTaskSet()) {
                    Transition transition = stringTransitionMap.get(s);
                    if (transition == null) {
                        transition = petrinet.addTransition(s);
                        stringTransitionMap.put(s, transition);
                        transitionStringMap.put(transition, s);
                    }
                    Place place = petrinet.addPlace((p_cnt++) + "");
                    petrinet.addArc(transition, place);
                }
                continue;
            }

            // 计算m
            HashSet<HashSet<String>> succ = item.getSuccessor();
            int m = 1;
            for (HashSet<String> each : succ) {
                m *= each.size();
            }

            System.out.println("m = [" + m + "]");

            // 第一次添加m个库所
            List<Place> places = new ArrayList<>();
            for (Transition cur : currentTransitions) {
                System.out.println(hasCreated(succ, stringTransitionMap));
                if (!hasCreated(succ, stringTransitionMap)) {
                    for (int i = 0; i < m; i++) {
                        Place newPlace = petrinet.addPlace((p_cnt++) + "");
                        places.add(newPlace);
                        petrinet.addArc(cur, newPlace);
                    }

                    List<List<String>> lists = new ArrayList<>();
                    List<Integer> index = new ArrayList<>();
                    for (HashSet<String> strings : succ) {
                        lists.add(new ArrayList<>(strings));
                        index.add(0);
                    }

                    // 创建新变迁，把m个库所连到新变迁
                    for (Place place : places) {
                        for (int i = 0; i < lists.size(); i++) {
                            String s = lists.get(i).get(index.get(i));
                            Transition transition = stringTransitionMap.get(s);
                            if (transition == null) {
                                transition = petrinet.addTransition(s);
                                transitionSetListMap.put(transition, new ArrayList<>());
                                stringTransitionMap.put(s, transition);
                                transitionStringMap.put(transition, s);
                            }
                            petrinet.addArc(place, transition);
                            succTransitions.add(transition);
                        }


                        for (int i = index.size() - 1; i >= 0; i--) {
                            if (index.get(i) == lists.get(i).size() - 1) {
                                index.set(i, 0);
                            } else {
                                index.set(i, index.get(i) + 1);
                                break;
                            }
                        }
                    }

                    // t->t_next
                    for (Transition succTransition : succTransitions) {
                        List<Set<Transition>> tranSetList = transitionSetListMap.get(succTransition);
                        if (tranSetList.isEmpty()) {
                            for (Transition current : currentTransitions) {
                                Set<Transition> init = new HashSet<>();
                                init.add(current);
                                tranSetList.add(init);
                            }
                        } else {
                            List<Set<Transition>> newTransitionSetList = new ArrayList<>();
                            for (Set<Transition> transitionSet : tranSetList) {
                                for (Transition current : currentTransitions) {
                                    Set<Transition> newTransitionSet = new HashSet<>();
                                    newTransitionSet.addAll(transitionSet);
                                    newTransitionSet.add(current);
                                    newTransitionSetList.add(newTransitionSet);
                                }
                            }
                            transitionSetListMap.put(succTransition, newTransitionSetList);
                        }
                    }
                } else {
                    Set<Transition> existedSucc = getExistedTransitions(succ, stringTransitionMap);
                    for (Transition existedT : existedSucc) {
                        for (Transition preT : existedT.getVisiblePredecessors()) {
                            // 并发
                            if (isParallel(table, transitionStringMap.get(cur), transitionStringMap.get(preT))) {
                                int newPlaceNum = getSuccSize(preT, petrinet);
                                Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> transitionEdgeCollection = petrinet.getOutEdges(preT);
                                List<Place> prePlaces = new ArrayList<>();
                                List<Place> newPlaces = new ArrayList<>();
                                for (int i = 0; i < newPlaceNum; i++) {
                                    Place p = petrinet.addPlace((p_cnt++) + "");
                                    newPlaces.add(p);
                                    petrinet.addArc(cur, p);
                                }
                                for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : transitionEdgeCollection) {
                                    Place suc = (Place) edge.getTarget();
                                    prePlaces.add(suc);
                                }

//                                assert newPlaces.size() != prePlaces.size();

                                for (int i = 0; i < prePlaces.size(); i++) {
                                    Place preP = prePlaces.get(i);
                                    Place curP = newPlaces.get(i);
                                    for (PetrinetEdge e : petrinet.getOutEdges(preP)) {
                                        petrinet.addArc(curP, (Transition) e.getTarget());
                                    }
                                }

                                List<Set<Transition>> tmp = transitionSetListMap.get(existedT);
                                List<Set<Transition>> newList = new ArrayList<>();
                                for (Set<Transition> set : tmp) {
                                    if (set.contains(preT)) {
                                        Set<Transition> newSet = new HashSet<>(set);
                                        newSet.remove(preT);
                                        newSet.add(cur);
                                        newList.add(newSet);
                                    } else {
                                        newList.add(set);
                                    }
                                }
                                transitionSetListMap.put(existedT, newList);
                            } else {
                                // 非并发
                                List<Place> samePlaceList = getSamePlaces(preT, existedT, petrinet);
                                for (Place p : samePlaceList) {
                                    petrinet.addArc(cur, p);
                                }

                                List<Set<Transition>> tmp = transitionSetListMap.get(existedT);
                                for (Set<Transition> set : tmp) {
                                    if (set.contains(preT)) {
                                        set.add(cur);
                                    }
                                }
                            }
                        }
                    }
                }
            }
//            printMap(stringTransitionMap);
//            for (int i = 0; i < m; i++) {
//                places.add(petrinet.addPlace((p_cnt++) + ""));
//            }
//
//            List<List<String>> lists = new ArrayList<>();
//            List<Integer> index = new ArrayList<>();
//            for (HashSet<String> strings : succ) {
//                lists.add(new ArrayList<>(strings));
//                index.add(0);
//            }
//
//            // 创建新变迁，把m个库所连到新变迁
//            for (Place place : places) {
//                for (int i = 0; i < lists.size(); i++) {
//                    String s = lists.get(i).get(index.get(i));
//                    Transition transition = stringTransitionMap.get(s);
//                    if (transition == null) {
//                        transition = petrinet.addTransition(s);
//                        transitionSetListMap.put(transition, new ArrayList<>());
//                        stringTransitionMap.put(s, transition);
//                    }
//                    petrinet.addArc(place, transition);
//                    succTransitions.add(transition);
//                }
//
//
//                for (int i = index.size() - 1; i >= 0; i--) {
//                    if (index.get(i) == lists.get(i).size() - 1) {
//                        index.set(i, 0);
//                    } else {
//                        index.set(i, index.get(i) + 1);
//                        break;
//                    }
//                }
//            }
//
//            // t->t_next
//            for (Transition succTransition : succTransitions) {
//                List<Set<Transition>> tranSetList = transitionSetListMap.get(succTransition);
//                if (tranSetList.isEmpty()) {
//                    for (Transition cur : currentTransitions) {
//                        Set<Transition> init = new HashSet<>();
//                        init.add(cur);
//                        tranSetList.add(init);
//                    }
//                } else {
//                    List<Set<Transition>> newTransitionSetList = new ArrayList<>();
//                    for (Set<Transition> transitionSet : tranSetList) {
//                        for (Transition cur : currentTransitions) {
//                            Set<Transition> newTransitionSet = new HashSet<>();
//                            newTransitionSet.addAll(transitionSet);
//                            newTransitionSet.add(cur);
//                            newTransitionSetList.add(newTransitionSet);
//                        }
//                    }
//                    transitionSetListMap.put(succTransition, newTransitionSetList);
//                }
//            }
        }

        return petrinet;
    }


    private boolean hasCreated(HashSet<HashSet<String>> succ, Map<String, Transition> stringTransitionMap) {
        printMap(stringTransitionMap);
        // 假设全部未创建或全都已经创建
        for (HashSet<String> ss : succ) {
            for (String s : ss) {
                if (stringTransitionMap.get(s) != null) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private Set<Transition> getExistedTransitions(HashSet<HashSet<String>> succ, Map<String, Transition> stringTransitionMap) {
        Set<Transition> transitions = new HashSet<>();
        for (HashSet<String> set : succ) {
            for (String s : set) {
                if (stringTransitionMap.get(s) == null) {
                    continue;
                } else {
                    transitions.add(stringTransitionMap.get(s));
                }
            }
        }

        return transitions;
    }

    private boolean isParallel(List<ConnectItem> table, String t1, String t2) {
        for (ConnectItem connectItem : table) {
            if (connectItem == null || connectItem.getSuccessor() == null || connectItem.getSuccessor().isEmpty()) {
                continue;
            }
            for (HashSet<String> stringHashSet : connectItem.getSuccessor()) {
                if (stringHashSet.contains(t1) && stringHashSet.contains(t2) && !t1.equals(t2)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int getSuccSize(Transition t, Petrinet petrinet) {
        return petrinet.getOutEdges(t).size();
    }

    private List<Place> getSamePlaces(Transition pre, Transition suc, Petrinet petrinet) {
        Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> transitionEdgeCollectionOut = petrinet.getOutEdges(pre);
        List<Place> sucPlaces = new ArrayList<>();
        for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : transitionEdgeCollectionOut) {
            sucPlaces.add((Place) e.getTarget());
        }

        Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> transitionEdgeCollectionIn = petrinet.getInEdges(suc);
        List<Place> prePlaces = new ArrayList<>();
        for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : transitionEdgeCollectionIn) {
            prePlaces.add((Place) e.getSource());
        }

        List<Place> samePlaces = new ArrayList<>();

        for (Place eachSuc : sucPlaces) {
            if (prePlaces.contains(eachSuc)) {
                samePlaces.add(eachSuc);
            }
        }

        return samePlaces;
    }

    private void printSet(Set<String> set) {
        System.out.println("printSet: ");
        for (String e : set) {
            System.out.print(e.toString() + "\t");
        }
        System.out.println();
    }

    private void printMap(Map<String, Transition> transitionMap) {
        System.out.println("printMap: ");
        for (Map.Entry<String, Transition> entry : transitionMap.entrySet()) {
            System.out.println("map: " + entry.getKey() + "\t" + entry.getValue().getLabel());
        }
    }
}

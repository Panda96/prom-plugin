package cn.edu.nju.software.cripsylamp.logRepair;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;

import java.util.*;

/**
 * transfer adjacency list to Petri Net
 *
 * @author keenan on 21/10/2017
 */
@SuppressWarnings("Duplicates")
public class Table2Petrinet {
    /**
     * transfer adjacency list to Petri Net
     *
     * @param table adjacency list
     * @return Petri Net
     */
    public Petrinet tranfer2net(List<ConnectItem> table, List<LoopStructure> loopStructures) {
        int p_cnt = 0;
        Petrinet petrinet = PetrinetFactory.newPetrinet("petri");
        Map<String, Transition> stringTransitionMap = new HashMap<>();
        Map<Transition, String> transitionStringMap = new HashMap<>();
        Map<Transition, List<Set<Transition>>> transitionSetListMap = new HashMap<>();

        for (ConnectItem item : table) {
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
        }


        // 找循环
        for (LoopStructure loop : loopStructures) {
            String new_Task = loop.getNewTask();

            String first = new_Task.substring(0, 1);
            Transition first_transition = stringTransitionMap.get(first);
            if (first_transition == null) {
                first_transition = petrinet.addTransition(first);
                stringTransitionMap.put(first, first_transition);
            }
            String last = new_Task.substring(new_Task.length() - 1, new_Task.length());
            Transition last_transition = stringTransitionMap.get(last);
            if (last_transition == null) {
                last_transition = petrinet.addTransition(last);
                stringTransitionMap.put(last, last_transition);
            }

            Transition previous = first_transition;
            for (int i = 1; i < new_Task.length(); i++) {
                String label = String.valueOf(new_Task.charAt(i));
                Transition transition = stringTransitionMap.get(label);
                if (transition == null) {
                    transition = petrinet.addTransition(label);
                    stringTransitionMap.put(label, transition);
                }

                Place place = petrinet.addPlace((p_cnt++) + "");
                petrinet.addArc(previous, place);
                petrinet.addArc(place, transition);
                previous = transition;
            }

            // first 和 pre
            Set<String> pre_parallels = findParallel(loop.getPreTask(), table);
            Set<Transition> pre_para_transitions = new HashSet<>();
            for (String pre_parallel : pre_parallels) {
                Transition transition = stringTransitionMap.get(pre_parallel);
                if (transition == null) {
                    continue;
                } else {
                    pre_para_transitions.add(transition);
                }
            }

            Set<Place> pre_para_post_places = findPostPlaces(pre_para_transitions, petrinet);
            for (Place place : pre_para_post_places) {
                petrinet.addArc(place, first_transition);
            }

            // last 和 post
            Set<String> post_paralles = findParallel(loop.getPostTask(), table);
            Set<Transition> post_para_transitions = new HashSet<>();
            for (String post_parallel : post_paralles) {
                Transition transition = stringTransitionMap.get(post_parallel);
                if (transition == null) {
                    continue;
                } else {
                    post_para_transitions.add(transition);
                }
            }

            Set<Place> post_para_pre_places = findPrePalces(post_para_transitions, petrinet);
            for (Place place : post_para_pre_places) {
                petrinet.addArc(last_transition, place);
            }


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

    /**
     * 寻找并发
     *
     * @param a
     * @param table
     * @return
     */
    private Set<String> findParallel(Set<String> a, List<ConnectItem> table) {
        Set<String> parallels = new HashSet<>();
        for (int i = 0; i < table.size(); i++) {
            ConnectItem connectItem = table.get(i);
            HashSet<HashSet<String>> succ = connectItem.getSuccessor();
            HashSet<HashSet<String>> pre = connectItem.getPrecursor();
            if (succ != null) {

                for (HashSet<String> set : succ) {
                    for (String s : set) {
                        if (a.contains(s)) {
                            parallels.addAll(set);
                        }
                    }
                }
            }
            if (pre != null) {
                for (HashSet<String> set : pre) {
                    for (String s : set) {
                        if (a.contains(s)) {
                            parallels.addAll(set);
                        }
                    }
                }
            }
        }
        return parallels;
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

    private Set<Place> findPostPlaces(Set<Transition> transitions, Petrinet petrinet) {
        Set<Place> places = new HashSet<>();
        for (Transition transition : transitions) {
            Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> transitionEdgeCollection = petrinet.getOutEdges(transition);
            for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : transitionEdgeCollection) {
                places.add((Place) edge.getTarget());
            }
        }

        return places;
    }

    private Set<Place> findPrePalces(Set<Transition> transitions, Petrinet petrinet) {
        Set<Place> places = new HashSet<>();
        for (Transition transition : transitions) {
            Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> transitionEdgeCollection = petrinet.getInEdges(transition);
            for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : transitionEdgeCollection) {
                places.add((Place) edge.getSource());
            }
        }
        return places;
    }

    private void printMap(Map<String, Transition> transitionMap) {
        System.out.println("printMap: ");
        for (Map.Entry<String, Transition> entry : transitionMap.entrySet()) {
            System.out.println("map: " + entry.getKey() + "\t" + entry.getValue().getLabel());
        }
    }
}

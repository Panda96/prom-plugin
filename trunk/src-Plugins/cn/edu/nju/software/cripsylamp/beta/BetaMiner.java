package cn.edu.nju.software.cripsylamp.beta;

import cn.edu.nju.software.cripsylamp.beans.Trace;
import cn.edu.nju.software.cripsylamp.plugins.EnhancedAlphaMiner;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class BetaMiner {
    private SuccSeqs SuccessiveSequences = new SuccSeqs();
    private InterSeqs IntersectionalSequences = new InterSeqs();
    private OrderRelation OrderingRelations = new OrderRelation();
    public ArrayList Transitions = new ArrayList();
    public ArrayList FirstTransitions = new ArrayList();
    public ArrayList LastTransitions = new ArrayList();
    public ArrayList PlacesArcs = new ArrayList();

    @Plugin(
            name = "Beta Miner Plugin",
            parameterLabels = {"XLog"},
            returnLabels = {"Petrinet"},
            returnTypes = {Petrinet.class},
            userAccessible = true,
            help = "Beta Miner Plugin"
    )
    @UITopiaVariant(
            affiliation = "nju.software",
            author = "Y.F.Cheng & Q.H.Zhou",
            email = "151250206@smail.nju.edu.cn"
    )
    public Petrinet mine(UIPluginContext context, XLog log) {
        Petrinet result = mineIt(log);

        Map<String, String> map = new HashMap<>();
        int i = 0;
        for (XTrace xEvents : log) {
            String trace = "";
            for (XEvent xEvent : xEvents) {
                String name = xEvent.getAttributes().get("concept:name").toString();
                trace += name;
            }
            map.put(i++ + "", trace.trim());
        }

        Trace trace = new Trace(map);
        result = EnhancedAlphaMiner.findLostPlaces(result, trace);
        return result;
    }

    private Petrinet mineIt(XLog log) {
        this.counting(log);
        this.ordering();
        this.mining3();
        Petrinet petrinet = PetrinetFactory.newPetrinet("Beta Petri Net");

        for (int i = 0; i < Transitions.size(); i++) {
            petrinet.addTransition((String) Transitions.get(i));
        }

        Place psrc = petrinet.addPlace("psource");

        for (int i = 0; i < FirstTransitions.size(); i++) {
            Transition transition = null;
            for (Transition t : petrinet.getTransitions()) {
                if (t.getLabel().equals(FirstTransitions.get(i))) {
                    transition = t;
                }
            }
            petrinet.addArc(psrc, transition);
        }

        Place psink = petrinet.addPlace("psink");

        for (int i = 0; i < LastTransitions.size(); i++) {
            Transition transition = null;
            for (Transition t : petrinet.getTransitions()) {
                if (t.getLabel().equals(LastTransitions.get(i))) {
                    transition = t;
                }
            }
            petrinet.addArc(transition, psink);
        }

        for (int i = 0; i < PlacesArcs.size(); i++) {
            PredSucc2 ps = (PredSucc2) this.PlacesArcs.get(i);
            Place p = petrinet.addPlace("p" + (i + 1));

            for (int j = 0; j < ps.predecessor.size(); j++) {
                Transition transition = null;
                for (Transition t : petrinet.getTransitions()) {
                    if (t.getLabel().equals(ps.predecessor.get(j))) {
                        transition = t;
                    }
                }
                petrinet.addArc(transition, p);
            }

            for (int j = 0; j < ps.successor.size(); j++) {
                Transition transition = null;
                for (Transition t : petrinet.getTransitions()) {
                    if (t.getLabel().equals(ps.successor.get(j))) {
                        transition = t;
                    }
                }
                petrinet.addArc(p, transition);
            }
        }
        return petrinet;
    }


    private void counting(XLog log) {
        this.SuccessiveSequences.clear();
        this.IntersectionalSequences.clear();
        this.Transitions.clear();
        this.FirstTransitions.clear();
        this.LastTransitions.clear();
        String task = "";
        String type = "";
        ArrayList tasks_started = new ArrayList();
        ArrayList tasks_completed = new ArrayList();
        ArrayList tasks_ps = new ArrayList();

//        int np = log.numberOfInstances();
        int np = log.size();


        for (int i = 0; i < np; ++i) {
            XTrace pi = log.get(i);
            tasks_started.clear();
            tasks_completed.clear();
            tasks_ps.clear();
            boolean isFirst = true;

            for (int j = 0; j < pi.size(); ++j) {
                try {
                    XEvent xEvent = pi.get(j);
                    task = xEvent.getAttributes().get("concept:name").toString();
                    type = xEvent.getAttributes().get("lifecycle:transition").toString();

                    if (!this.Transitions.contains(task)) {
                        this.Transitions.add(task);
                    }

                    if (isFirst && !this.FirstTransitions.contains(task)) {
                        this.FirstTransitions.add(task);
                    }

                    isFirst = false;
                    if (j == pi.size() - 1 && !this.LastTransitions.contains(task)) {
                        this.LastTransitions.add(task);
                    }

                    int k;
                    PredSucc2 ps;
                    if (!type.equalsIgnoreCase("start")) {
                        if (type.equalsIgnoreCase("complete")) {
                            tasks_started.remove(task);
                            tasks_completed.add(task);

                            for (k = tasks_ps.size() - 1; k >= 0; --k) {
                                ps = (PredSucc2) tasks_ps.get(k);
                                if (ps.successor.contains(task)) {
                                    tasks_ps.remove(k);
                                }
                            }
                        }
                    } else {
                        for (k = 0; k < tasks_started.size(); ++k) {
                            String t = (String) tasks_started.get(k);
                            this.IntersectionalSequences.addCount(t, task);
                        }

                        tasks_started.add(task);
                        if (tasks_completed.size() > 0) {
                            tasks_ps.add(new PredSucc2(tasks_completed));
                            tasks_completed.clear();
                        }

                        for (k = 0; k < tasks_ps.size(); ++k) {
                            ps = (PredSucc2) tasks_ps.get(k);
                            ps.successor.add(task);

                            for (int l = 0; l < ps.predecessor.size(); ++l) {
                                String t = (String) ps.predecessor.get(l);
                                this.SuccessiveSequences.addCount(t, task);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void ordering() {
        this.OrderingRelations.clear();

        for (int i = 0; i < this.Transitions.size(); ++i) {
            String task_pred = (String) this.Transitions.get(i);

            for (int j = 0; j < this.Transitions.size(); ++j) {
                int rel = 0;
                String task_succ = (String) this.Transitions.get(j);
                if (this.IntersectionalSequences.getCount(task_pred, task_succ) > 0) {
                    rel = 2;
                } else if (this.SuccessiveSequences.getCount(task_pred, task_succ) > 0) {
                    rel = 1;
                }

                this.OrderingRelations.setRel(task_pred, task_succ, rel);
            }
        }

    }

    private void mining3() {
        ArrayList pa_temp = new ArrayList();
        PredSuccUtil psu = new PredSuccUtil(this.OrderingRelations);

        for (int i = 0; i < this.Transitions.size(); ++i) {
            String task_pred = (String) this.Transitions.get(i);

            for (int j = 0; j < this.Transitions.size(); ++j) {
                String task_succ = (String) this.Transitions.get(j);
                int rel = this.OrderingRelations.getRel(task_pred, task_succ);
                if (rel == 1) {
                    pa_temp.clear();
                    boolean isInserted = false;

                    int n;
                    PredSucc2 ps;
                    for (n = this.PlacesArcs.size() - 1; n >= 0; --n) {
                        PredSucc2 ps_temp = new PredSucc2();
                        ps_temp.predecessor.add(task_pred);
                        ps_temp.successor.add(task_succ);
                        ps = (PredSucc2) this.PlacesArcs.get(n);
                        int m;
                        String t;
                        if (ps.predecessor.contains(task_pred)) {
                            if (ps.successor.contains(task_succ)) {
                                isInserted = true;
                            } else {
                                for (m = 0; m < ps.predecessor.size(); ++m) {
                                    t = (String) ps.predecessor.get(m);
                                    if (!t.equals(task_pred) && this.OrderingRelations.getRel(t, task_succ) == 1) {
                                        ps_temp.predecessor.add(t);
                                    }
                                }

                                for (m = 0; m < ps.successor.size(); ++m) {
                                    t = (String) ps.successor.get(m);
                                    if (this.OrderingRelations.getRel(t, task_succ) != 2) {
                                        ps_temp.successor.add(t);
                                    }
                                }

                                if (ps_temp.predecessor.size() == ps.predecessor.size() && ps_temp.successor.size() == ps.successor.size() + 1) {
                                    ps.successor.add(task_succ);
                                    isInserted = true;
                                } else if ((ps_temp.predecessor.size() != 1 || ps_temp.successor.size() != 1) && !psu.isContained(ps_temp, pa_temp)) {
                                    pa_temp.add(ps_temp);
                                }
                            }
                        } else if (ps.successor.contains(task_succ)) {
                            for (m = 0; m < ps.successor.size(); ++m) {
                                t = (String) ps.successor.get(m);
                                if (!t.equals(task_succ) && this.OrderingRelations.getRel(task_pred, t) == 1) {
                                    ps_temp.successor.add(t);
                                }
                            }

                            for (m = 0; m < ps.predecessor.size(); ++m) {
                                t = (String) ps.predecessor.get(m);
                                if (this.OrderingRelations.getRel(t, task_pred) != 2) {
                                    ps_temp.predecessor.add(t);
                                }
                            }

                            if (ps_temp.predecessor.size() == ps.predecessor.size() + 1 && ps_temp.successor.size() == ps.successor.size()) {
                                ps.predecessor.add(task_pred);
                                isInserted = true;
                            } else if ((ps_temp.predecessor.size() != 1 || ps_temp.successor.size() != 1) && !psu.isContained(ps_temp, pa_temp)) {
                                pa_temp.add(ps_temp);
                            }
                        } else {
                            for (m = 0; m < ps.successor.size(); ++m) {
                                t = (String) ps.successor.get(m);
                                if (this.OrderingRelations.getRel(task_pred, t) == 1) {
                                    ps_temp.successor.add(t);
                                }
                            }

                            for (m = 0; m < ps.predecessor.size(); ++m) {
                                t = (String) ps.predecessor.get(m);
                                if (this.OrderingRelations.getRel(t, task_pred) != 2) {
                                    ps_temp.predecessor.add(t);
                                }
                            }

                            for (m = 0; m < ps.predecessor.size(); ++m) {
                                t = (String) ps.predecessor.get(m);
                                if (this.OrderingRelations.getRel(t, task_succ) != 1) {
                                    ps_temp.predecessor.remove(t);
                                }
                            }

                            for (m = 0; m < ps.successor.size(); ++m) {
                                t = (String) ps.successor.get(m);
                                if (this.OrderingRelations.getRel(t, task_succ) == 2) {
                                    ps_temp.successor.remove(t);
                                }
                            }

                            if (ps_temp.predecessor.size() == ps.predecessor.size() + 1 && ps_temp.successor.size() == ps.successor.size() + 1) {
                                ps.predecessor.add(task_pred);
                                ps.successor.add(task_succ);
                                isInserted = true;
                            } else if ((ps_temp.predecessor.size() != 1 || ps_temp.successor.size() != 1) && !psu.isContained(ps_temp, pa_temp)) {
                                pa_temp.add(ps_temp);
                            }
                        }
                    }

                    if (!isInserted) {
                        PredSucc2 ps_temp = new PredSucc2();
                        ps_temp.predecessor.add(task_pred);
                        ps_temp.successor.add(task_succ);
                        if (!psu.isContained(ps_temp, pa_temp)) {
                            pa_temp.add(ps_temp);
                        }
                    }

                    n = pa_temp.size();
                    if (n > 0) {
                        for (int m = 0; m < n; ++m) {
                            ps = (PredSucc2) pa_temp.get(m);
                            if (!psu.isContained(ps, this.PlacesArcs)) {
                                this.PlacesArcs.add(ps);
                            }
                        }
                    }
                }
            }
        }

    }

}

package cn.edu.nju.software.cripsylamp.beta;

import cn.edu.nju.software.cripsylamp.beans.Trace;
import cn.edu.nju.software.cripsylamp.plugins.InvarientMiner;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import java.util.HashMap;
import java.util.Map;

public class EnhancedBetaMiner {
    @Plugin(
            name = "Enhanced Beta Miner Plugin",
            parameterLabels = {"XLog", "Petrinet"},
            returnLabels = {"Petrinet"},
            returnTypes = {Petrinet.class},
            userAccessible = true,
            help = "Enhanced Beta Miner Plugin"
    )
    @UITopiaVariant(
            affiliation = "nju.software",
            author = "Y.F.Cheng & Q.H.Zhou",
            email = "151250206@smail.nju.edu.cn"
    )
    @PluginVariant(requiredParameterLabels = {0, 1})
    public Petrinet mine(UIPluginContext context, XLog log, Petrinet petrinet) {
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

        Trace trace = new Trace(map);//initial trace

        petrinet = InvarientMiner.findLostPlaces(petrinet, trace);
        return petrinet;
    }
}

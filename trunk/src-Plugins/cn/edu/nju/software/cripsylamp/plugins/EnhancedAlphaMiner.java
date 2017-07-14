package cn.edu.nju.software.cripsylamp.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

public class EnhancedAlphaMiner {
        @Plugin(
                name = "Enhanced Alpha Miner Plugin",
                parameterLabels = {"Log"},
//                parameterLabels = { "Log", "Classifier",
//                        "Parameters" },
//                returnLabels = { "Petrinet", "Marking"  },
                returnLabels = {"String"},
//                returnTypes = { Petrinet.class,
//                        Marking.class },
                returnTypes = { String.class },
                userAccessible = true,
                help = "Enhanced Alpha Miner Plugin"
//                ,
//                categories = {
//                        PluginCategory.Discovery }
        )
        @UITopiaVariant(
                affiliation = "My company", 
                author = "My name", 
                email = "My e-mail address"
        )

        @PluginVariant(requiredParameterLabels = { 0 })
        public static String helloWorld(PluginContext context,XLog log) {

            return "Hello World"+log.getGlobalEventAttributes().get(0).getKey()+log.getGlobalTraceAttributes().get(0).getKey();
        }
}
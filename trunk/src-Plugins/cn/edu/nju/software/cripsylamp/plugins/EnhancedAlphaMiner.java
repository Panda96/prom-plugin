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
            returnLabels = {"String"},
            returnTypes = {String.class},
            userAccessible = true,
            help = "Enhanced Alpha Miner Plugin"
    )
    @UITopiaVariant(
            affiliation = "nju.software",
            author = "Y.F.Cheng & Q.H.Zhou",
            email = "151250206@smail.nju.edu.cn"
    )

    @PluginVariant(requiredParameterLabels = {0})
    public static String helloWorld(PluginContext context, XLog log) {

        return "Hello World" + log.getGlobalEventAttributes().get(0).getKey() + log.getGlobalTraceAttributes().get(0).getKey();
    }
}
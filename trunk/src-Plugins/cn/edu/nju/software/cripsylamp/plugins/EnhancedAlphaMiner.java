package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.beans.Trace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;

public class EnhancedAlphaMiner {
    @Plugin(
            name = "Enhanced Alpha Miner Plugin",
            parameterLabels = {"Traces"},
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

    public static String helloWorld(UIPluginContext context, Trace traces) {

        return "Hello World";
    }
}
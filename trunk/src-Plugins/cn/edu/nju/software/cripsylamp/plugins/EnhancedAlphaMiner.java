package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.algorithmhelper.SimpleAlphaMinerHelper;
import cn.edu.nju.software.cripsylamp.beans.Trace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

/**
 * Created by keenan on 15/07/2017.
 */
@SuppressWarnings("Duplicates")
public class EnhancedAlphaMiner {
    @Plugin(
            name = "Enhanced Alpha Miner Plugin",
            parameterLabels = {"Traces"},
            returnLabels = {"Petrinet"},
            returnTypes = {Petrinet.class},
            userAccessible = true,
            help = "Enhanced Alpha Miner Plugin"
    )
    @UITopiaVariant(
            affiliation = "nju.software",
            author = "Y.F.Cheng & Q.H.Zhou",
            email = "151250206@smail.nju.edu.cn"
    )

    @SuppressWarnings("Duplicates")
    public Petrinet enhancedAlphaMiner(UIPluginContext context, Trace traces) {
        Petrinet basic = SimpleAlphaMinerHelper.getPetrinetMinedByAlpha(traces);
        return InvarientMiner.findLostPlaces(basic, traces);
    }


}
package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.algorithmhelper.SimpleAlphaMinerHelper;
import cn.edu.nju.software.cripsylamp.beans.Trace;
import cn.edu.nju.software.cripsylamp.util.ThreeTransitionTuple;
import cn.edu.nju.software.cripsylamp.util.Tuple;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import java.util.*;

/**
 * Created by CYF on 2017/7/18.
 */

public class AlphaPlusMiner {
    @Plugin(
            name = "Alpha Plus Miner Plugin",
            parameterLabels = {"Traces"},
            returnLabels = {"Petrinet"},
            returnTypes = {Petrinet.class},
            userAccessible = true,
            help = "Alpha Plus Miner Plugin"
    )
    @UITopiaVariant(
            affiliation = "nju.software",
            author = "Y.F.Cheng & Q.H.Zhou",
            email = "151250206@smail.nju.edu.cn"
    )


    public Petrinet AlphaPlusMiner(UIPluginContext context, Trace traces) {
        Set<Character> Tlog = new HashSet<>();
        Set<Character> L1L = new HashSet<>();
        for (String each : traces.getTraces().values()) {
            char last = ' ';
            for (int i = 0; i < each.length(); i++) {
                char sign = each.charAt(i);
                Tlog.add(sign);
                if (last == sign) {
                    L1L.add(last);
                }
                last = sign;
            }
        }
        Set<Character> Tleft = new HashSet<>(Tlog);
        Tleft.removeAll(L1L);

        Set<ThreeTransitionTuple> transitionLoop1Set = new HashSet<>();
        for (char each : L1L) {
            char left = ' ',right = ' ';
            boolean hasMet = false;
            for (String str : traces.getTraces().values()) {
                for (int i = 0; i < str.length(); i++) {
                    char thisChar = str.charAt(i);
                    if (thisChar != each && (!hasMet)) {
                        left = thisChar;
                    } else if (thisChar != each && hasMet) {
                        right = thisChar;
                        transitionLoop1Set.add(new ThreeTransitionTuple(left,each,right));
                        break;
                    } else if (thisChar == each) {
                        hasMet = true;
                    }
                }
            }
        }

        Trace traceWithoutL1L = traces.removeT(L1L);

        Petrinet alphaResult = SimpleAlphaMinerHelper.getPetrinetMinedByAlpha(traceWithoutL1L);
        AlphaPlusMinerView view = new AlphaPlusMinerView();
        view.addLostTransitions(alphaResult,transitionLoop1Set);
        return alphaResult;
    }
}

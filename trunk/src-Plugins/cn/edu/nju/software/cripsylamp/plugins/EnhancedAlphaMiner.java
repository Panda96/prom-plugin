package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.beans.Trace;
import cn.edu.nju.software.cripsylamp.util.Tuple;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by keenan on 15/07/2017.
 */
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

    public static Petrinet enhancedAlphaMiner(UIPluginContext context, Trace traces) {
        Set<Character> tSet = new HashSet<>();
        Set<Character> start = new HashSet<>();
        Set<Character> end = new HashSet<>();

        for (String trace : traces.getTraces().values()) {
            for (int i = 0; i < trace.length(); i++) {
                Character sign = trace.charAt(i);
                if (i == 0) {
                    start.add(sign);
                } else if (i == trace.length() - 1) {
                    end.add(sign);
                }

                tSet.add(sign);
            }
        }

        Set<Tuple> Xw = new HashSet<>();
        for (String trace : traces.getTraces().values()) {
            for (int i = 0; i < trace.length() - 1; i++) {
                char a = trace.charAt(i);
                char b = trace.charAt(i + 1);

                Set<Character> left = new HashSet<Character>() {
                    {
                        add(a);
                    }
                };
                Set<Character> right = new HashSet<Character>() {
                    {
                        add(b);
                    }
                };

                Tuple tuple = new Tuple(left, right);

                boolean flag = false;

                for (Tuple t : Xw) {
                    if (t.equals(tuple)) {
                        Xw.remove(t);
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    Xw.add(tuple);
                }
            }
        }

        boolean differentResult = true;

        while (differentResult) {
            differentResult = false;
            Set<Tuple> temp = new HashSet<>();
            Iterator<Tuple> iterator = Xw.iterator();
            while (iterator.hasNext()) {
                Tuple tuple = iterator.next();

                for (Tuple each : Xw) {
                    if (tuple.leftEquals(each)) {
                        if (!traces.belongs2SameTrace(tuple.getRightPart(), each.getRightPart())) {
                            temp.add(tuple.rightUnion(each));
                            differentResult = true;
                            continue;
                        }
                    }
                }

                for (Tuple each : Xw) {
                    if (tuple.rightEquals(each)) {
                        if (!traces.belongs2SameTrace(tuple.getLeftPart(), each.getLeftPart())) {
                            temp.add(tuple.leftUnion(each));
                            differentResult = true;
                            continue;
                        }
                    }
                }
            }

            Set<Tuple> tmp2 = new HashSet<>(temp);

            for (Tuple outT : Xw) {
                boolean containflag = false;
                for (Tuple inT : temp) {
                    if (inT.contain(outT)) {
                        containflag = true;
                        break;
                    }
                }
                if (!containflag) {
                    tmp2.add(outT);
                }
            }

            Xw = tmp2;
        }

        EnhancedAlphaMinerView view = new EnhancedAlphaMinerView();
        return view.makePetriNet(Xw, start, end);
    }

}
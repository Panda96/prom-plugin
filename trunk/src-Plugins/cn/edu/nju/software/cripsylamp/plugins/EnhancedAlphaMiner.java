package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.algorithmhelper.SimpleAlphaMinerHelper;
import cn.edu.nju.software.cripsylamp.beans.Trace;
import cn.edu.nju.software.cripsylamp.util.MatrixCalculator;
import cn.edu.nju.software.cripsylamp.util.MatrixSolver;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
        return findLostPlaces(traces);
    }

    public static Petrinet findLostPlaces(Trace traces) {
        Petrinet basic = SimpleAlphaMinerHelper.getPetrinetMinedByAlpha(traces);

        Set<Character> start = new HashSet<>();

        for (String trace : traces.getTraces().values()) {
            for (int i = 0; i < trace.length(); i++) {
                Character sign = trace.charAt(i);
                if (i == 0) {
                    start.add(sign);
                }
            }
        }

        int[][] matrix = MatrixCalculator.transformNet2Matrix(basic);

        Set<int[]> t_tmp = MatrixCalculator.rightCalculate(matrix);
        Set<int[]> t_invariant = new HashSet<>();

        for (int[] each : t_tmp) {
            int tmp = 0;
            for (char c : start) {
                tmp |= each[c >= 'A' ? c - 'A' : c - 'a'];
            }

            boolean allPositive = true;
            for (int i : each) {
                if (i < 0) {
                    allPositive = false;
                    break;
                }
            }
            if (tmp == 1 && allPositive) {
                t_invariant.add(each);
            }
        }

        Set<int[]> t_avail = generateAvail(traces, t_invariant);
        t_invariant.removeAll(t_avail);

        Set<int[]> pvs;
        if (t_avail.isEmpty()) {
            pvs = new HashSet<>();
        } else {
            pvs = MatrixSolver.solve(
                    (MatrixCalculator.matrixSet2Array(t_avail)));
        }

        int[][] non_avail = MatrixCalculator.matrixSet2Array(t_invariant);
        if (non_avail == null) {
            return basic;
        }

        for (int[] each : pvs) {
            positiveFirst(each);
        }

        Set<int[]> pvsa = new HashSet<>();
        for (int[] each : pvs) {
            if (!MatrixCalculator.checkZero(non_avail, each)) {
                pvsa.add(each);
            }
        }

        EnhancedAlphaMinerView view = new EnhancedAlphaMinerView();
        view.addLostPlaces(pvsa, basic);

        return basic;
    }

    private static Set<int[]> generateAvail(Trace trace, Set<int[]> Tiw) {
        Set<int[]> avail = new HashSet<>();
        Collection<String> traces = trace.getTraces().values();

        for (int[] each : Tiw) {
            String tmp = "";
            for (int i = 0; i < each.length - 1; i++) {
                if (each[i] == 1)
                    tmp += (char) ('A' + i);
            }
            for (String eachTrace : traces) {
                if (isInTrace(eachTrace, tmp)) {
                    avail.add(each);
                    break;
                }
            }
        }

        return avail;
    }

    private static boolean isInTrace(String trace, String tmp) {
        if (trace.length() != tmp.length()) {
            return false;
        } else {
            for (char each : tmp.toCharArray()) {
                if (!trace.contains(each + "")) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void positiveFirst(int[] a) {
        boolean posiFirst = true;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == -1) {
                posiFirst = false;
                break;
            } else if (a[i] == 1) {
                break;
            }
        }
        if (!posiFirst)
            for (int i = 0; i < a.length; i++) {
                a[i] = -a[i];
            }
    }
}
package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.beans.Trace;
import cn.edu.nju.software.cripsylamp.util.MatrixCalculator;
import cn.edu.nju.software.cripsylamp.util.MatrixSolver;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import java.util.*;

public class InvarientMiner {
    @Plugin(
            name = "Invarient Miner Plugin",
            parameterLabels = {"Petrinet", "Trace"},
            returnLabels = {"Petrinet"},
            returnTypes = {Petrinet.class},
            userAccessible = true,
            help = "Invarient Miner Plugin"
    )
    @UITopiaVariant(
            affiliation = "nju.software",
            author = "Y.F.Cheng & Q.H.Zhou",
            email = "151250206@smail.nju.edu.cn"
    )
    @PluginVariant(requiredParameterLabels = {0, 1})
    public Petrinet enhancedAlphaMiner(UIPluginContext context, Petrinet basic, Trace traces) {
        return findLostPlaces(basic, traces);
    }

    public static Petrinet findLostPlaces(Petrinet basic, Trace traces) {
        Set<Character> start = new HashSet<>();

        for (String trace : traces.getTraces().values()) {
            for (int i = 0; i < trace.length(); i++) {
                Character sign = trace.charAt(i);
                System.out.print(sign);
                if (i == 0) {
                    start.add(sign);
                }
            }
            System.out.println();
        }

        int[][] matrix = MatrixCalculator.transformNet2Matrix(basic);

        Set<int[]> t_tmp = MatrixCalculator.rightCalculate(matrix);
        Set<int[]> t_invariant = new HashSet<>();

        System.out.println("matrix");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j]+"\t");
            }
            System.out.println();
        }

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

        System.out.println("before remove");
        for (int[]each:t_invariant) {
            for (int i = 0; i < each.length; i++) {
                System.out.print(each[i]);
            }
            System.out.println();
        }

        Set<int[]> t_avail = generateAvail(traces, t_invariant);
        t_invariant.removeAll(t_avail);

        System.out.println("t_avail");
        for (int[]each:t_avail) {
            for (int i = 0; i < each.length; i++) {
                System.out.print(each[i]);
            }
            System.out.println();
        }

        System.out.println("after remove");
        for (int[]each:t_invariant) {
            for (int i = 0; i < each.length; i++) {
                System.out.print(each[i]);
            }
            System.out.println();
        }
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

        Set<int[]> pvsa = new HashSet<>();
        for (int[] each : pvs) {
            if (!MatrixCalculator.checkZero(non_avail, each)) {
                pvsa.add(each);
            }
        }

        System.out.println("Pvsa");
        for (int[] each:pvsa){
            for (int i = 0; i < each.length; i++) {
                System.out.print(each[i]);
            }
            System.out.println();
        }
        // 修正PVSa
//        pvsa = modifyPVSa(pvsa);

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


    private static boolean isValidPVSa(int[] pv) {
        // 1
        int pOne = -1;
        // -1
        int nOne = -1;

        for (int i = 0; i < pv.length; i++) {
            int e = pv[i];
            if (e != 0 || e != 1 || e != -1) {
                return false;
            }
            if (e == 0) {
                continue;
            }
            if (e == 1) {
                if (pOne != -1) {
                    return false;
                } else {
                    pOne = i;
                    continue;
                }
            } else if (e == -1) {
                if (nOne != -1) {
                    return false;
                } else {
                    nOne = i;
                    continue;
                }
            }
        }

        if (pOne != -1 && nOne != -1) {
            return true;
        } else {
            return false;
        }
    }

    private static int[] addTwoArray(int[] a, int[] b) {
        if (a.length != b.length) {
            return null;
        }
        int length = a.length;
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    private static int[] subTwoArray(int[] a, int[] b) {
        if (a.length != b.length) {
            return null;
        }
        int length = a.length;
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }
}

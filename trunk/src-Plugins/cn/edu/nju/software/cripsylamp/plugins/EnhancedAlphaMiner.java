package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.beans.Trace;
import cn.edu.nju.software.cripsylamp.util.MatrixCalculator;
import cn.edu.nju.software.cripsylamp.util.MatrixSolver;
import cn.edu.nju.software.cripsylamp.util.Tuple;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import java.util.Collection;
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

    public Petrinet enhancedAlphaMiner(UIPluginContext context, Trace traces) {
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

        Petrinet basic = view.makePetriNet(Xw, start, end);

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

//        for (int[] each : t_invariant) {
//            for (int i : each) {
//                System.out.print(i + "\t");
//            }
//            System.out.println();
//        }
        Set<int[]> t_avail = generateAvail(traces, t_invariant);
        t_invariant.removeAll(t_avail);

//        for (int[] each : t_avail) {
//            for (int i : each) {
//                System.out.print(i + "\t");
//            }
//            System.out.println();
//        }
//
//
//        System.out.println("====");
//
//        for (int[] each : t_invariant) {
//            for (int i : each) {
//                System.out.print(i + "\t");
//            }
//            System.out.println();
//        }

        Set<int[]> pvs = MatrixSolver.solve(
                (MatrixCalculator.matrixSet2Array(t_avail)));

//        int avaiSize = t_avail.size();
//        int miniSize = basic.getTransitions().size() + 1 - avaiSize;
//        Set<int[]> minimalPvs = new HashSet<>();
//        for (int[] each : pvs) {
//            if (permitIn(minimalPvs, each)) {
//                minimalPvs.add(each);
//            }
//            if (minimalPvs.size() == miniSize) {
//                break;
//            }
//        }

////        MatrixSolver.Rank(MatrixCalculator.matrixSet2Array(minimalPvs));
//        System.out.println(minimalPvs.isEmpty());
//        for (int[] each : minimalPvs) {
//            for (int i : each) {
//                System.out.print(i + "\t");
//            }
//            System.out.println();
//        }
//
//        // 化为上三角
//        for (int[] each : minimalPvs) {
//            Set<int[]> all = new HashSet<>();
//            all.addAll(minimalPvs);
//            all.remove(each);
//            for (int i = 0; i < minimalPvs.size(); i++)
//                for (int[] a : all) {
//                    int[] sum = addTwoArray(each, a);
//                    if (judgeOne(sum)) {
//                        if (numOfZeros(sum) > numOfZeros(each)) {
//                            replaceAll(each, sum);
//                        }
//                    }
//
//                    sum = differenceTwoArray(each, a);
//                    if (judgeOne(sum)) {
//                        if (numOfZeros(sum) > numOfZeros(each)) {
//                            replaceAll(each, sum);
//                        }
//                    }
//                }
//        }

//        System.out.println("--------------");
//        System.out.println(minimalPvs.isEmpty());
//
//        for (int[] each : minimalPvs) {
//            for (int i : each) {
//                System.out.print(i + "\t");
//            }
//            System.out.println();
//        }

//        for (int[] each : pvs) {
//            for (int i : each) {
//                System.out.print(i + " ");
//            }
//            System.out.println();
//        }
//        Set<int[]> p_tmp = MatrixCalculator.leftCalculate(matrix);
//        Set<int[]> p_invariant = new HashSet<>();
//        for (int[] each : p_tmp) {
//            if (each[each.length - 1] != 1) {
//                continue;
//            }
//            boolean allPositive = true;
//            for (int i : each) {
//                if (i < 0) {
//                    allPositive = false;
//                    break;
//                }
//            }
//            if (allPositive) {
//                p_invariant.add(each);
//            }
//        }

        int[][] non_avail = MatrixCalculator.matrixSet2Array(t_invariant);
        if (non_avail == null) {
            return basic;
        }

//        for (int i = 0; i < non_avail.length; i++) {
//            for (int j = 0; j < non_avail[i].length; j++) {
//                System.out.print(non_avail[i][j] + "\t");
//            }
//            System.out.println();
//        }

        for (int[] each : pvs) {
            positiveFirst(each);
        }

        Set<int[]> pvsa = new HashSet<>();
        for (int[] each : pvs) {
            if (!MatrixCalculator.checkZero(non_avail, each)) {
                pvsa.add(each);
            }
        }

//        Set<int[]> pvse = new HashSet<>();
//        pvse.addAll(pvsa);
//        pvse.addAll(MatrixCalculator.array2MatrixSet(matrix));

//        System.out.println("=================");
//        for (int[] each : pvse) {
//            for (int i : each) {
//                System.out.print(i + " ");
//            }
//            System.out.println();
//        }

        view.addLostPlaces(pvsa, basic);

        return basic;
    }

    private Set<int[]> generateAvail(Trace trace, Set<int[]> Tiw) {
        Set<int[]> avail = new HashSet<>();
//        Set<int[]> navail = new HashSet<>();
        Collection<String> traces = trace.getTraces().values();

        for (int[] each : Tiw) {
            String tmp = "";
//            boolean hasAdd=false;
            for (int i = 0; i < each.length - 1; i++) {
                if (each[i] == 1)
                    tmp += (char) ('A' + i);
            }
            for (String eachTrace : traces) {
                if (isInTrace(eachTrace, tmp)) {
                    avail.add(each);
//                    hasAdd=true;
                    break;
                }
            }
//            if(!hasAdd){
//                navail.add(each);
//            }
        }
//        int[][] availArray = new int[avail.size()][avail.iterator().next().length];
//        Set<int[]> PVS = MatrixCalculator.leftCalculate(availArray);

        return avail;
    }

    private boolean isInTrace(String trace, String tmp) {
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

//    private void printLogs(Petrinet net) {
//        Collection<Transition> transitions = net.getTransitions();
//        Collection<Place> places = net.getPlaces();
//
//        Set<Transition> startT = new HashSet<>();
//        Set<Transition> endT = new HashSet<>();
//
//        for (Transition t : transitions) {
//            if (t.getVisiblePredecessors().isEmpty() || t.getVisiblePredecessors() == null) {
//                startT.add(t);
//            } else if (t.getVisibleSuccessors().isEmpty() || t.getVisibleSuccessors() == null) {
//                endT.add(t);
//            }
//        }
//
//        for (Transition t : startT) {
//            Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outEdges = net.getOutEdges(t);
//
//        }
//
//    }
//
//
//    private Trace generateWLog(Set<Tuple> Xw, Set<Tuple> start, Set<Tuple> end) {
//        Map<String, String> gTrace = new HashMap<>();
//        int keySig = 1;
//        for (Tuple tuple : start) {
//            List<String> gLog = findLog(Xw, tuple, end);
//            if (!gLog.isEmpty()) {
//                for (String log : gLog) {
//                    gTrace.put("" + (keySig++), log);
//                }
//            }
//        }
//        return new Trace(gTrace);
//    }
//
//    private List<String> findLog(Set<Tuple> Xw, Tuple startTuple, Set<Tuple> end) {
//
//
//        return new ArrayList<>();
//    }

    private boolean permitIn(Set<int[]> pool, int[] data) {
        if (pool.size() == 0) {
            return true;
        }
        Set<int[]> set1 = new HashSet<>();
        Set<int[]> set2 = new HashSet<>();
        Set<int[]> setAll = new HashSet<>();
        set1.addAll(pool);
        setAll.addAll(pool);
        int i = 1;
        while (i <= pool.size()) {
            for (int[] each : set1) {
                for (int[] pooleach : pool) {
                    int[] sum = addTwoArray(each, pooleach);
                    if (judgeSame(sum, data)) {
                        return false;
                    }
                    if (judgeOne(sum)) {
                        set2.add(sum);
                        setAll.add(sum);
                    }
                    sum = differenceTwoArray(each, sum);
                    if (judgeSame(sum, data)) {
                        return false;
                    }
                    if (judgeOne(sum)) {
                        set2.add(sum);
                        setAll.add(sum);
                    }
                    sum = differenceTwoArray(sum, each);
                    if (judgeSame(sum, data)) {
                        return false;
                    }
                    if (judgeOne(sum)) {
                        set2.add(sum);
                        setAll.add(sum);
                    }
                }
            }
            set1 = set2;
            set2 = new HashSet<>();
            i++;
        }
        return true;
    }

    private int[] addTwoArray(int[] a1, int[] a2) {
        int[] result = new int[a1.length];
        for (int i = 0; i < a1.length; i++) {
            result[i] = a1[i] + a2[i];
        }
        return result;
    }

    private boolean judgeOne(int[] tmp) {
        for (int each : tmp) {
            if (each > 1 || each < -1) {
                return false;
            }
        }
        return true;
    }

    private int[] differenceTwoArray(int[] a1, int[] a2) {
        int[] result = new int[a1.length];
        for (int i = 0; i < a1.length; i++) {
            result[i] = a1[i] - a2[i];
        }
        return result;
    }

    private boolean judgeSame(int[] a1, int[] a2) {
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }
        return true;
    }

    private int numOfZeros(int[] a) {
        int sum = 0;
        for (int i : a) {
            if (i == 0) {
                sum++;
            }
        }

        return sum;
    }

    private void replaceAll(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] = b[i];
        }
    }

    private void positiveFirst(int[] a) {
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
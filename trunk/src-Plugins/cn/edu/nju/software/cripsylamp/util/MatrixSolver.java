package cn.edu.nju.software.cripsylamp.util;

import java.util.*;

/**
 * Created by keenan on 16/07/2017.
 */
public class MatrixSolver {

    /**
     * 增广矩阵
     *
     * @param a
     * @param b
     * @return
     */
    private static int[][] augmented_mat(int[][] a, int[] b) {
        int[][] matrix = new int[a.length][a[0].length + 1];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                matrix[i][j] = a[i][j];
            }

            matrix[i][a[0].length] = b[i];
        }

        return matrix;
    }

    /**
     * 行交换
     *
     * @param matrix
     * @param i
     * @param j
     */
    private static void swap_row(int[][] matrix, int i, int j) {
        int m = matrix.length;

        if (i >= m || j >= m) {
            return;
        } else {
            for (int k = 0; k < matrix[0].length; k++) {
                int tmp = matrix[i][k];
                matrix[i][k] = matrix[j][k];
                matrix[j][k] = tmp;
            }
        }
    }

    private static List<Integer> trape_mat(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;

        List<Integer> main_factor = new ArrayList<>();
        int main_col = 0;

        while (main_col < n && main_factor.size() < m) {
            if (main_col == n) {
                break;
            }

            int first_row = main_factor.size();
            while (main_col < n) {
                List<Integer> new_col = new ArrayList<>();
                for (int i = first_row; i < m; i++) {
                    new_col.add(matrix[i][main_col]);
                }

                List<Integer> not_zeros = new ArrayList<>();
                for (int i = 0; i < new_col.size(); i++) {
                    if (new_col.get(i) > 0) {
                        not_zeros.add(i);
                    }
                }

                Collections.sort(not_zeros);

                if (not_zeros.size() == 0) {
                    main_col += 1;
                    break;
                } else {
                    main_factor.add(main_col);
                    int index = not_zeros.get(0);
                    if (index != 0) {
                        swap_row(matrix, first_row, first_row + index);
                    }

                    if (first_row < m - 1) {
                        for (int k = first_row + 1; k < m; k++) {
                            int times = (matrix[k][main_col]) / (matrix[first_row][main_col]);
                            for (int i = 0; i < n; i++) {
                                matrix[k][i] = matrix[k][i] - times * matrix[first_row][i];
                            }
                        }
                    }
                    main_col += 1;
                    break;
                }
            }
        }

        return main_factor;
    }

    private static void back_solve(int[][] matrix, List<Integer> main_factor) {
        if (main_factor.size() == 0) {
            return;
        }

        int n = matrix[0].length;

        if (main_factor.get(main_factor.size() - 1) == n - 1) {
            return;
        }

        for (int i = main_factor.size() - 1; i >= 0; i--) {
            int factor = matrix[i][main_factor.get(i)];
            for (int k = 0; k < n; k++) {
                matrix[i][k] /= factor;
            }

            for (int j = 0; j < i; j++) {
                int times = matrix[j][main_factor.get(i)];
                for (int h = 0; h < n; h++) {
                    matrix[j][h] -= (times * matrix[i][h]);
                }
            }
        }
    }

    private static List<Integer> print_result(int[][] matrix, List<Integer> main_factor) {
        List<Integer> free_var = new ArrayList<>();
        int n = matrix[0].length;
        for (int i = 0; i < n - 1; i++) {
            if (!main_factor.contains(i)) {
                free_var.add((i));
            }
        }
        return free_var;
    }


    public static Set<int[]> solve(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = 0;
        }
        int[][] matrix = augmented_mat(a, b);
        List<Integer> main_factor = trape_mat(matrix);
        back_solve(matrix, main_factor);
        System.out.println("Matrix");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
        System.out.println("===");
        List<Integer> free_var = print_result(matrix, main_factor);

        Set<int[]> result = new HashSet<>();
        int[] eachResult;
        for (int i = 0; i < free_var.size(); i++) {
            System.out.print(free_var.get(i) + "\t");
        }
        System.out.println();
        for (int i = 0; i < main_factor.size(); i++) {
            System.out.print(main_factor.get(i) + "\t");
        }
        System.out.println();
        for (int i : free_var) {
            eachResult = new int[matrix[0].length];
            for (int j : free_var) {
                if (j == i) {
                    eachResult[j] = -1;
                } else {
                    eachResult[j] = 0;
                }
            }

            for (int j : main_factor) {
//                if (j == i)
//                    continue;
                eachResult[j] = 0;
                int row = allZeroExcept(j, matrix);
                for (int each : free_var) {
                    eachResult[j] -= eachResult[each] * matrix[row][each];
                }
//                System.out.print(j+""+eachResult[j]+"\n");
            }
            result.add(eachResult.clone());
//            for (int k = 0; k < matrix[0].length; k++) {
//                int rNum = allZeroExcept(k, matrix);
//                if (rNum == -1) {
//                    continue;
//                } else {
//                    System.out.println(rNum);
//                    for (int c = 0; c < matrix[0].length; c++) {
//                        if (c == k) {
//                            continue;
//                        }
//                        eachResult[k] -= eachResult[c] * matrix[rNum][c];
//                    }
//                }
//            }
//            result.add(eachResult.clone());
            for (int j = 0; j < eachResult.length; j++) {
                System.out.print(eachResult[j] + "\t");
            }
            System.out.println();
        }


//        return (result);
        return adjustResult(result);
    }

    private static Set<int[]> adjustResult(Set<int[]> preResult) {
        Set<int[]> result = new HashSet<>();
        for (int[] e : preResult) {
            if (onePositiveOneNegative(e)) {
                result.add(e);
            } else {
                int preLength = result.size();
                for (int[] a : preResult) {
                    int[] tmp1 = Arrays.copyOf(e, e.length);
                    int[] tmp2 = Arrays.copyOf(e, e.length);
                    for (int i = 0; i < a.length; i++) {
                        tmp1[i] += a[i];
                        tmp2[i] -= a[i];
                    }
                    if (onePositiveOneNegative(tmp1)) {
                        result.add(tmp1);
                        break;
                    } else if (onePositiveOneNegative(tmp2)) {
                        result.add(tmp2);
                        break;
                    }
                }
                ;
                if (preLength == result.size()) {
                    result.add(e);
                }
            }
        }

        for (int[] each : result) {
            positiveFirst(each);
        }
        result = removeSame(result);
        System.out.println("adjust result");
        for (int[] each : result) {
            for (int i = 0; i < each.length; i++) {
                System.out.print(each[i] + "\t");
            }
            System.out.println();
        }

        return result;
    }

    private static Set<int[]> removeSame(Set<int[]> oriSet) {
        Set<int[]> resultSet = new HashSet<>();
        if (oriSet==null||oriSet.size()<=1){
            return oriSet;
        }
        List<CompareIntSet> compareList = new ArrayList<>();
        for (int[] each:oriSet) {
            CompareIntSet e = new CompareIntSet(each);
            compareList.add(e);
        }
        for (int i = 0; i <compareList.size(); i++) {
            if (compareList.get(i).hasSame){
                continue;
            }
            for (int j = i+1; j < compareList.size(); j++) {
                if (compareList.get(j).hasSame){
                    continue;
                }
                int [] seti = compareList.get(i).getIntSet();
                int [] setj = compareList.get(j).getIntSet();
                boolean same = true;
                for (int k = 0; k < seti.length; k++) {
                    if (seti[k]!=setj[k]){
                        same = false;
                    }
                }
                if (same){
                    compareList.get(j).setHasSame(true);
                }
            }
            resultSet.add(compareList.get(i).getIntSet());
        }
        return resultSet;
    }

    private static boolean onePositiveOneNegative(int[] numbers) {
        int positive = 0, negative = 0;
        for (int i = 0; i < numbers.length - 2; i++) {
            if (numbers[i] == 1) {
                positive++;
            } else if (numbers[i] == -1) {
                negative++;
            } else if (numbers[i] != 0) {
                return false;
            }
        }
        for (int i = numbers.length - 2; i < numbers.length; i++) {
            if (numbers[i] != 0) {
                return false;
            }
        }
        return (positive == 1) && (negative == 1);
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

    private static int allZeroExcept(int cNum, int[][] matrix) {
//        int cnt = 0;
//        for (int j = 0; j < matrix.length; j++) {
//            if (matrix[j][cNum] != 0) {
//                cnt++;
//            }
//        }
//
//        if (cnt != 1) {
//            return -1;
//        } else {
        for (int j = 0; j < matrix.length; j++) {
            if (matrix[j][cNum] != 0) {
                return j;
            }
        }
//        }
        return -1;
    }

    public static void main(String[] args) {
//        int[][] a = {
//                {-1, 1, 1, 0, 0, 0, 0, 0, 0},
//                {0, -1, 0, 1, 0, 0, 0, 0, 0},
//                {0, -1, 0, 0, 1, 0, 0, 0, 0},
//                {0, 0, -1, 0, 0, 1, 0, 0, 0},
//                {0, 0, 0, -1, 0, 0, 1, 0, 0},
//                {0, 0, 0, 0, -1, 0, 1, 0, 0},
//                {0, 0, 0, 0, 0, -1, 0, 1, 0},
//                {0, 0, 0, 0, 0, 0, -1, -1, 1},
//                {1, 0, 0, 0, 0, 0, 0, 0, -1}
//        };
//        a = MatrixCalculator.T_matrix(a);

        int[][] a = {
                {1, 0, 0, 0, 1, 1, 0, 0, 1},
                {0, 1, 0, 1, 1, 0, 1, 0, 1},
                {0, 0, 1, 0, 1, 0, 0, 1, 1}
        };

        MatrixSolver matrixSolver = new MatrixSolver();
        matrixSolver.solve(a);
    }


}
//0	1	0	-1	0	0	0	0	0	0
//1	1	1	0	-1	0	0	0	0	0
//1	0	0	0	0	-1	0	0	0	0
//0	1	0	0	0	0	-1	0	0	0
//0	0	1	0	0	0	0	-1	0	0
//1	1	1	0	0	0	0	0	-1	0
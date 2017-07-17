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
            System.out.println("error: out of index...");
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

            System.out.println("main_col: " + main_col);
            int first_row = main_factor.size();
            while (main_col < n) {
//                new_col = sigma[first_row:, main_col]
//                Map<Integer, Integer> new_col = new HashMap<>();
                List<Integer> new_col = new ArrayList<>();
                for (int i = first_row; i < m; i++) {
                    System.out.println(i + " -  " + matrix[i][main_col]);
//                    new_col.put(i, matrix[i][main_col]);
                    new_col.add(matrix[i][main_col]);

                }

                printList("new Cols: ", new ArrayList<Integer>());

                //   not_zeros = np.where(new_col > 0)[0]
                List<Integer> not_zeros = new ArrayList<>();
                for (int i = 0; i < new_col.size(); i++) {
                    if (new_col.get(i) > 0) {
                        not_zeros.add(i);
                    }
                }

                Collections.sort(not_zeros);

                printList("not_zeros", not_zeros);

                if (not_zeros.size() == 0) {
                    main_col += 1;
                    break;
                } else {
                    main_factor.add(main_col);
                    int index = not_zeros.get(0);
                    System.out.println("index: " + index);
                    if (index != 0) {
                        printMatirx(matrix);

                        swap_row(matrix, first_row, first_row + index);
                        System.out.println("swap i = " + first_row + " with j = " + (first_row + index));

                    }

                    printMatirx(matrix);
                    if (first_row < m - 1) {
                        for (int k = first_row + 1; k < m; k++) {
                            System.out.println("(matrix[first_row][main_col] :" + (matrix[first_row][main_col]));
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
            System.out.println("wrong main_fatcor...");
            return;
        }

        int m = matrix.length, n = matrix[0].length;

        if (main_factor.get(main_factor.size() - 1) == n - 1) {
            System.out.println("No answer...");
            return;
        }

        for (int i = m - 1; i >= main_factor.size() - 1; i--) {
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
        int m = matrix.length, n = matrix[0].length;
        String[] result = new String[n - 1];
        for (int i = 0; i < n - 1; i++) {
            if (!main_factor.contains(i)) {
//                result[i] = "x_" + (i + 1) + "(free var)";
                free_var.add((i));
//            } else {
//                int row_of_maini = main_factor.indexOf(i);
//                result[i] = "" + matrix[row_of_maini][n - 1];
//                for (int j = i + 1; j < n - 1; j++) {
//                    int ratio = matrix[row_of_maini][j];
//                    if (ratio > 0) {
//                        result[i] = result[i] + "-" + ratio + "x_" + (j + 1);
//                    } else if (ratio < 0) {
//                        result[i] = result[i] + "+" + (-ratio) + "x_" + (j + 1);
//                    }
//                }
//            }
            }
//        }

//        for (int i = 0; i < n - 1; i++) {
//            System.out.println("x_" + (i + 1) + " = " + result[i]);
//        }
        }
        return free_var;
    }


    public static Set<int[]> solve(int[][] a) {
        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = 0;
        }
        int[][] matrix = augmented_mat(a, b);
        System.out.println("增广矩阵为： ");
        printMatirx(matrix);

        List<Integer> main_factor = trape_mat(matrix);
        back_solve(matrix, main_factor);
        System.out.println("方程的简化阶梯矩阵为： ");
        printMatirx(matrix);

        System.out.println("方程的主元列为： ");
        for (int i = 0; i < main_factor.size(); i++) {
            System.out.print(main_factor.get(i) + "\t");
        }

        System.out.println();
        List<Integer> free_var = print_result(matrix, main_factor);

        Set<int[]> result = new HashSet<>();
        int[] eachResult = new int[matrix[0].length];
        for (int i : free_var) {
            for (int j : free_var) {
                if (j == i) {
                    eachResult[j] = -1;
                } else {
                    eachResult[j] = 0;
                }
            }
            for (int j = 0; j < matrix.length; j++) {
                eachResult[j] = 0;
                for (int k = matrix.length; k < eachResult.length; k++) {
                    eachResult[j] -= eachResult[k] * matrix[j][k];
                }
            }
            result.add(eachResult.clone());
        }
        for (int[] each : result) {
            for (int i = 0; i < each.length - 1; i++) {
                System.out.print(each[i] + "\t");
            }
            System.out.println();
        }

        return result;

    }

    private static void printMatirx(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j] + "\t");
            }
            System.out.println();
        }
    }

    private static void printList(String info, List<Integer> a) {
        System.out.print(info + " :   ");
        for (int i = 0; i < a.size(); i++) {
            System.out.print(a.get(i) + "\t");
        }
        System.out.println();
        ;
    }

    public static void main(String[] args) {
        int[][] a = {
                {1, 0, 0, 0, 1, 1, 0, 0, 1},
                {0, 1, 0, 1, 1, 0, 1, 0, 1},
                {0, 0, 1, 0, 1, 0, 0, 1, 1}
        };

        int[] b = {0, 0, 0};

        MatrixSolver matrixSolver = new MatrixSolver();
//        matrixSolver.solve(a, b);
    }


}

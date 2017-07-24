package cn.edu.nju.software.cripsylamp.util;

import java.util.ArrayList;

public class Solver {
    private int[][] mat = null;
    private ArrayList<int[]> ans = null;

    public void print(int[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            int[] _m = mat[i];
            for (int j = 0; j < _m.length; j++) {
                System.out.print(_m[j]);
                System.out.print(",");
            }
            System.out.println();
        }
    }

    private int swap_times = 0;
    private int[] var_order;
    private int[] temp;
    private int var_start_idx;

    public static ArrayList<int[]> solve(int[][] matrix) {
        return solve(matrix, "T");
    }

    public static ArrayList<int[]> solve(int[][] matrix, boolean is_t) {
        return solve(matrix, is_t ? "T" : "P");
    }

    public static ArrayList<int[]> solve(int[][] matrix, String type) {
        Solver _s = new Solver();
        type = type.toUpperCase();
        return _s.solve(matrix, type == "T" ? 0 : type == "P" ? 1 : -1);
    }

    private Solver() {

    }

    private ArrayList<int[]> solve(int[][] matrix, int type) {
        if (matrix.length == 0) {
            return null;
        }

        if (type == 0) {
            mat = copy(matrix);
        } else if (type == 1) {
            mat = translate(matrix);
        } else {
            return null;
        }

        //print(mat);


		/*
         * 首先使用高斯消元法对矩阵进行初等变化转为上三角矩阵
		 */
        int row_num = mat.length, col_num = mat[0].length;

        //初始化变量
        ans = new ArrayList<int[]>();
        swap_times = 0;
        var_order = new int[col_num];
        for (int i = 0; i < col_num; i++) {
            var_order[i] = i;
        }


        int r = 0;
        out_loop:
        for (; r < row_num; r++) {

            //System.out.println("-----row:"+ r +"-------");

            while (true) {
                //如果mat[r][r]=0，则找一行r2>r的且mat[r2][r]!=0,并且交换这两行
                //如果没有找到r2，说明高斯消元不能进行到底。返回 null
                if (mat[r][r] == 0) {
                    int r2 = -1;
                    for (int j = r + 1; j < row_num; j++) {
                        if (mat[j][r] != 0) {
                            r2 = j;
                            break;
                        }
                    }
                    if (r2 < 0) {
                        if (r < col_num - swap_times - 1) {
                            swapColumToLast(r);
                            continue;
                        } else {
							/*
							 * 执行到这里说明从r行开始之后的行全部为0.直接跳出最外层循环
							 */
                            break out_loop;
                        }

                    } else {
						/*
						 * 交换r和r2两行
						 */
                        swapRow(r, r2);
                    }
                } else {
                    if (r == row_num - 1) {
                        System.out.print("!something may be wrong.\n last row is not all zero\n");
                    }
                    break;
                }
            }

            for (int j = r + 1; j < row_num; j++) {
                double d = (double) mat[j][r] / (double) mat[r][r];
                for (int k = r; k < col_num; k++) {
                    mat[j][k] -= mat[r][k] * d;
                    int _check = mat[j][k];
                    if (_check != 0 && _check != 1 && _check != -1) {
                        System.out.println("出现中间不为0,-1,1的情况");
                    }
                }
            }

            int mr = mat[r][r];
            for (int j = r; j < col_num; j++) {
                mat[r][j] /= mr;
            }
            //print(mat);
        }

        //System.out.println("--------------");

        //print(mat);

        //System.out.println("last row:" + (r-1));

		/*
		 * 然后
		 * r-2是因为在之前的循环结束后，r-1是最后一行。现在要从最后一行的上一行开始计合并。
		 */
        int last_r = r - 1;
        for (int r2 = last_r; r2 >= 0; r2--) {
            for (int j = r; j < col_num; j++) {
                mat[r2][j] = -mat[r2][j];
            }
            if (r2 < last_r) {
				/*
				 * 如果不是消元的最后一行，则把下面的行的结果合到该行中。
				 */
                for (int r3 = r2 + 1; r3 <= last_r; r3++) {
                    int _t = -mat[r2][r3];
                    for (int j = r; j < col_num; j++) {
                        mat[r2][j] += _t * mat[r3][j];
                    }
                    mat[r2][r3] = 0;
                }

            }
        }


        //System.out.println("--------------");

        //print(mat);

        if (r >= col_num) {
            System.err.println("全为0...");
            return null;
        }
		/*
		 * 然后将 col_num - r 个数量的不确定未知量取0，1排列组合
		 */
        //System.out.println("unknown var:" + (col_num - r));

		/*
		 * 使用深度优先搜索得到所有排列组合
		 */
        temp = new int[col_num];
        var_start_idx = r;
        calc(r, r, col_num);


        return ans;

    }

    private void swapColumToLast(int colum) {
        int c2 = mat[0].length - swap_times - 1;
        swap_times++;
        for (int i = 0; i < mat.length; i++) {
            int tmp = mat[i][c2];
            mat[i][c2] = mat[i][colum];
            mat[i][colum] = tmp;
        }
        int tmp = var_order[colum];
        var_order[colum] = var_order[c2];
        var_order[c2] = tmp;
    }

    private void swapRow(int r1, int r2) {
        int c_n = mat[0].length;
        for (int i = 0; i < c_n; i++) {
            int tmp = mat[r2][i];
            mat[r2][i] = mat[r1][i];
            mat[r1][i] = tmp;
        }
    }

    private void calc(int start, int cur, int end) {
        if (cur >= end) {

            for (int r = var_start_idx - 1; r >= 0; r--) {
                temp[r] = 0;
                for (int i = var_start_idx; i < end; i++) {
                    temp[r] += mat[r][i] * temp[i];
                }
                if (temp[r] != 0 && temp[r] != 1) {
                    return;
                }
            }

            boolean _all_zero = true;
            for (int i = 0; i < end; i++) {
                if (temp[i] == 1) {
                    _all_zero = false;
                    break;
                }
            }
            if (_all_zero)
                return;
            int[] _n = new int[end];
			/*
			 * 交换回来
			 */
            for (int i = 0; i < end; i++) {
                _n[var_order[i]] = temp[i];
            }
            ans.add(_n);
        } else {
            temp[cur] = 0;
            calc(start, cur + 1, end);
            temp[cur] = 1;
            calc(start, cur + 1, end);
        }
    }

    private int[][] translate(int[][] matrix) {
        if (matrix.length == 0)
            return new int[0][0];

        int r_n = matrix.length, c_n = matrix[0].length;
        int[][] rtn = new int[c_n][r_n];
        for (int i = 0; i < r_n; i++) {
            for (int j = 0; j < c_n; j++) {
                rtn[j][i] = matrix[i][j];
            }
        }

        return rtn;
    }

    private int[][] copy(int[][] matrix) {
        if (matrix.length == 0)
            return new int[0][0];
        int r_n = matrix.length, c_n = matrix[0].length;
        int[][] rtn = new int[r_n][c_n];
        for (int i = 0; i < r_n; i++) {
            for (int j = 0; j < c_n; j++) {
                rtn[i][j] = matrix[i][j];
            }
        }
        return rtn;
    }

    public static void main(String[] args) {
        int[][] a = {
                {-1, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, -1, -1, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, -1, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, -1, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, -1, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, -1, 0, 0},
                {0, 0, 0, 0, 1, 1, 0, -1, 0},
                {0, 0, 0, 0, 0, 0, 1, -1, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, -1}
        };

		/*
		 * Solver类有一个静态函数 solve.
		 * 第一个参数是关联矩阵。第二个参数指明计算T还是P不变量，
		 * 第二个参数如果不指定是计算T不变量，如果指定可以是字符串"T"或"P"或者bool变量，true代表计算T变量false代表计算P不变量
		 * 返回是1和0的数组，数组长度等于变量的数量，跟本档中计算的不变量含义一致。
		 */

        ArrayList<int[]> ans = Solver.solve(a);
        // ans = Solver.solve(a, "T");  ans = Solver.solve(a, true); 都可以
        System.out.println("T不变量：");
        for (int i = 0; i < ans.size(); i++) {
            int[] _t = ans.get(i);
            for (int j = 0; j < _t.length; j++) {
                System.out.print(_t[j]);
                System.out.print(',');
            }
            System.out.println("\n");
        }

        ans = Solver.solve(a, "P");
        //ans = Solver.solve(a, false); 也可以
        System.out.println("P不变量：");
        for (int i = 0; i < ans.size(); i++) {
            int[] _t = ans.get(i);
            for (int j = 0; j < _t.length; j++) {
                System.out.print(_t[j]);
                System.out.print(',');
            }
            System.out.println("\n");
        }
    }
}

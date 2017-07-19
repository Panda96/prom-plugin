package cn.edu.nju.software.cripsylamp.util;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import java.util.*;

/**
 * Created by CYF and keenan on 2017/7/16.
 */
public class MatrixCalculator {
    public static int[][] transformNet2Matrix(Petrinet net) {
        Collection<Transition> transitionCollection = net.getTransitions();
        Collection<Place> placeCollection = net.getPlaces();
        Iterator<Place> placeIterator = placeCollection.iterator();
        //xSize
        int pSize = placeCollection.size();
        //ySize
        int tSize = transitionCollection.size() + 1;
        int[][] resultMatrix = new int[pSize][tSize];

        //initialize result matrix
        for (int x = 0; x < pSize; x++) {
            for (int y = 0; y < tSize; y++) {
                resultMatrix[x][y] = 0;
            }
        }
//        for (Transition t:transitionCollection) {
//            int yPosition = t.getLabel().charAt(0)-'A';
//            Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> arcCollection = net.getInEdges(t);
//
//        }

        //xPosision means line number which represent the position of every place
        int xPosition = 0;
        Place p;
        while (placeIterator.hasNext()) {
            p = placeIterator.next();
            //(t,p)-> 1
            Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> arcCollection = net.getInEdges(p);
            for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> each : arcCollection) {
                PetrinetNode t = each.getSource();
                int yPosition = t.getLabel().charAt(0) >= 'A' && t.getLabel().charAt(0) <= 'Z' ? t.getLabel().charAt(0) - 'A' : t.getLabel().charAt(0) - 'a';
                resultMatrix[xPosition][yPosition] = 1;
            }
            if (arcCollection.size() == 0) {
                resultMatrix[xPosition][tSize - 1] = 1;
            }
            //(p,t)-> -1
            arcCollection = net.getOutEdges(p);
            for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> each : arcCollection) {
                PetrinetNode t = each.getTarget();
                int yPosition = t.getLabel().charAt(0) >= 'A' && t.getLabel().charAt(0) <= 'Z' ? t.getLabel().charAt(0) - 'A' : t.getLabel().charAt(0) - 'a';
                resultMatrix[xPosition][yPosition] = -1;
            }
            if (arcCollection.size() == 0) {
                resultMatrix[xPosition][tSize - 1] = -1;
            }
            //next place
            xPosition++;
        }
//        for(int x = 0; x<resultMatrix.length;x++){
//            for(int y=0;y<resultMatrix[0].length;y++){
//                System.out.print(resultMatrix[x][y]+"   ");
//            }
//            System.out.println();
//        }
        return resultMatrix;
    }

    public static Set<int[]> leftCalculate(int[][] right) {
        if (right == null) {
            return new HashSet<>();
        }
        int[] each = new int[right.length];
        for (int i = 0; i < each.length; i++) {
            each[i] = 0;
        }
        return traverseLeftCalc(each, 0, right);
    }

    private static Set<int[]> traverseLeftCalc(int[] left, int position, int[][] right) {
        Set<int[]> result = new HashSet<>();
        if (position == left.length) {
            int num = 0;
            for (int y = 0; y < right[0].length; y++) {
                for (int x = 0; x < left.length; x++) {
                    num += left[x] * right[x][y];
                }
                if (num != 0) {
                    return result;
                }
            }
            result.add(left.clone());
            return result;
        }
        result.addAll(traverseLeftCalc(left.clone(), position + 1, right));
        left[position] = 1;
        result.addAll(traverseLeftCalc(left.clone(), position + 1, right));
        left[position] = -1;
        result.addAll(traverseLeftCalc(left.clone(), position + 1, right));
        return result;
    }

    public static Set<int[]> rightCalculate(int[][] left) {
        Set<int[]> all = new HashSet<>();
        if (left == null) {
            return all;
        }
        int[] each = new int[left[0].length];
        for (int i = 0; i < each.length; i++) {
            each[0] = 0;
        }

        all = traverseRightCal(left, each, 0);
        return all;
    }

    private static Set<int[]> traverseRightCal(int[][] left, int[] right, int pos) {
        Set<int[]> tmp = new HashSet<>();

        if (pos == right.length) {
            int sum = 0;
            for (int i = 0; i < left.length; i++) {
                for (int j = 0; j < left[0].length; j++) {
                    sum += (left[i][j] * right[j]);
                }
                if (sum != 0) {
                    return tmp;
                }
            }

            tmp.add(right.clone());
            return tmp;
        }

        tmp.addAll(traverseRightCal(left, right.clone(), pos + 1));
        right[pos] = 1;
        tmp.addAll(traverseRightCal(left, right.clone(), pos + 1));
        right[pos] = -1;
        tmp.addAll(traverseRightCal(left, right.clone(), pos + 1));
        return tmp;
    }


    public static int[][] matrixSet2Array(Set<int[]> set) {
        if (set.isEmpty()) {
            System.out.println("MatrixCalculator.matrixSet2Array - set is empty");
            return null;
        }
        Iterator<int[]> iterator = set.iterator();
        int[] first = iterator.next();
        int[][] result = new int[set.size()][first.length];

        int line = 0;
        for (int[] each : set) {
            for (int i = 0; i < each.length; i++) {
                result[line][i] = each[i];
            }
            line++;
        }

        return result;
    }

    public static Set<int[]> array2MatrixSet(int[][] matrix) {
        Set<int[]> result = new HashSet<>();

        for (int i = 0; i < matrix.length; i++) {
            result.add(matrix[i]);
        }

        return result;
    }

    public static int[][] T_matrix(int[][] matrix) {
        int[][] result = new int[matrix[0].length][matrix.length];
        for (int x = 0; x < matrix[0].length; x++) {
            for (int y = 0; y < matrix.length; y++) {
                result[x][y] = matrix[y][x];
            }
        }
        return result;
    }

    public static boolean checkZero(int[][] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            int sum = 0;
            for (int j = 0; j < a[0].length; j++) {
                sum += (a[i][j] * b[j]);
            }

            if (sum != 0) {
                return false;
            }
        }
        return true;
    }

    private static double[][] UpTri(double[][] tmp) {
        double[][] Matrix = new double[tmp[0].length][tmp[0].length];
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < tmp[0].length; j++) {
                Matrix[i][j] = tmp[i][j];
            }
        }
        for (int i = tmp.length; i < Matrix.length; i++) {
            for (int j = 0; j < Matrix[0].length; j++) {
                Matrix[i][j] = 0;
            }
        }
        int Count = 1, n = Matrix[0].length;
        while (Count < n) {
            for (int N = n - 1; N >= Count; N--) {
                double z;
                if (Matrix[Count - 1][Count - 1] != 0) {
                    z = Matrix[N][Count - 1] / Matrix[Count - 1][Count - 1];
                } else {
                    for (int i = 0; i < n; i++) {
                        Matrix[Count - 1][i] += Matrix[N][i];
                    }
                    z = Matrix[N][Count - 1] / Matrix[Count - 1][Count - 1];
                }
                for (int i = 0; i < n; i++) {
                    Matrix[N][i] = Matrix[N][i] - Matrix[Count - 1][i] * z;
                }
            }
            Count++;
        }

        return Matrix;
    }

    private static double[][] intArray2Double(int[][] array) {
        double[][] result = new double[array.length][array[0].length];
        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[0].length; y++) {
                result[x][y] = (double) array[x][y];
            }
        }
        return result;
    }

    public static Set<int[]> calcAnswerForMatrix(Set<int[]> right) {
        Set<int[]> result = new HashSet<>();

        // 化为上三角
        double[][] upMatrix = UpTri(intArray2Double(T_matrix(matrixSet2Array(right))));

//        int numOfNotZeroLine = 0,lineSizelength=0;
        ArrayList<double[]> notZeroList = new ArrayList<>();
        for (double[] each : upMatrix) {
//            lineSizelength = each.length;
            if (numOfZeros(each) != each.length && (!Double.isNaN(each[0]))) {
//                numOfNotZeroLine++;
                notZeroList.add(each.clone());
            }
        }

        int numOfNotZeroLine = notZeroList.size();
        int freedomVariantNum = notZeroList.get(0).length - notZeroList.size();

        for (int i = 0; i < numOfNotZeroLine; i++) {
            for (int j = i + 1; j < numOfNotZeroLine; j++) {
                if (getFirstOnePosition(notZeroList.get(i)) > getFirstOnePosition(notZeroList.get(j))) {
                    changeTwoArray(notZeroList.get(i), notZeroList.get(j));
                }
            }
        }

        for (int i = 0; i < notZeroList.size(); i++) {
            for (int j = 0; j < notZeroList.get(0).length; j++) {
                System.out.print(notZeroList.get(i)[j] + "\t");
            }
            System.out.println();
        }
        processNotZeroLine(notZeroList, numOfNotZeroLine);

        for (int i = 0; i < notZeroList.size(); i++) {
            for (int j = 0; j < notZeroList.get(0).length; j++) {
                System.out.print(notZeroList.get(i)[j] + "\t");
            }
            System.out.println();
        }

        int[] eachResult = new int[notZeroList.get(0).length];
        for (int i = numOfNotZeroLine; i < notZeroList.get(0).length; i++) {
            for (int j = numOfNotZeroLine; j < notZeroList.get(0).length; j++) {
                if (i == j) {
                    eachResult[j] = 1;
                } else {
                    eachResult[j] = 0;
                }
            }
            for (int n = 0; n < numOfNotZeroLine; n++) {
                eachResult[n] = 0;
                double[] varientLine = notZeroList.get(n);
                for (int m = numOfNotZeroLine; m < freedomVariantNum; m++) {
                    eachResult[n] += -1 * eachResult[m] * varientLine[m];
                }
            }
//            }
            result.add(eachResult.clone());
        }

        System.out.println("result");
        for (int[] each:result) {
            for (int i :each) {
                System.out.print(i+"\t");
            }
            System.out.println();
        }
        return null;
    }

    private static int getFirstOnePosition(double[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0) {
                return i;
            }
        }
        return a.length - 1;
    }

    private static double numOfZeros(double[] a) {
        double sum = 0;
        for (double i : a) {
            if (i == 0) {
                sum++;
            }
        }

        return sum;
    }

    private static void changeTwoArray(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            double tmp = a[i];
            a[i] = b[i];
            b[i] = tmp;
        }
    }

    private static void processNotZeroLine(ArrayList<double[]> tmp, int colNum) {
        for (int i = tmp.size() - 2; i >= 0; i--) {
            for (int j = i + 1; j < colNum; j++) {
                if (tmp.get(i)[j] != 0) {
                    reduceToZero(tmp.get(i), tmp.get(j), j);
                }
            }
        }
    }

    private static void reduceToZero(double[] a1, double[] a2, int position) {
        double tmp = a1[position] / a2[position];
        for (int i = 0; i < a1.length; i++) {
            a1[i] -= a2[i] * tmp;
        }
    }

    public static void main(String[] args) {
//        int[][] array={
//            {-1,0,0,0,0,0,0,0,1,0},
//            {1,-1,-1,0,0,0,0,0,0,0},
//            {1,0,0,-1,0,0,0,0,0,0},
//            {0,1,0,0,-1,0,0,0,0,0},
//            {0,0,1,0,0,-1,0,0,0,0},
//            {0,0,0,1,0,0,-1,0,0,0},
//            {0,0,0,0,1,1,0,-1,0,0},
//            {0,0,0,0,0,0,1,-1,0,0},
//            {0,0,0,0,0,0,0,1,-1,0}};
        int[][] array = {
                {1, 0, 0, 0, 1, 1, 0, 0, 1, 0},
                {0, 1, 0, 1, 1, 0, 1, 0, 1, 0},
                {0, 0, 1, 0, 1, 0, 0, 1, 1, 0}
        };

        Set<int[]> result = calcAnswerForMatrix(array2MatrixSet(T_matrix(array)));
//        for (int[] each:result) {
//            for (int i:each) {
//                System.out.print(i+"\t");
//            }
//            System.out.println();
//        }
    }
}

package cn.edu.nju.software.cripsylamp.util;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by CYF on 2017/7/16.
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
                int yPosition = t.getLabel().charAt(0) > 'A' && t.getLabel().charAt(0) < 'Z' ? t.getLabel().charAt(0) - 'A' : t.getLabel().charAt(0) - 'a';
                resultMatrix[xPosition][yPosition] = 1;
            }
            if (arcCollection.size() == 0) {
                resultMatrix[xPosition][tSize - 1] = 1;
            }
            //(p,t)-> -1
            arcCollection = net.getOutEdges(p);
            for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> each : arcCollection) {
                PetrinetNode t = each.getTarget();
                int yPosition = t.getLabel().charAt(0) > 'A' && t.getLabel().charAt(0) < 'Z' ? t.getLabel().charAt(0) - 'A' : t.getLabel().charAt(0) - 'a';
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
        Set<int[]> result = new HashSet<>();
        int[] each = new int[right.length];
        for (int i = 0; i < each.length; i++) {
            each[i] = 0;
        }
        return traverseLeftCalc(each,0,right);
    }

    private static Set<int[]> traverseLeftCalc(int[] left, int position, int[][] right) {
        Set<int[]> result = new HashSet<>();
        if (position == left.length) {
            if(left[left.length-1]!=1){
                return result;
            }
            for(int x:left){
                if(x<0){
                    return result;
                }
            }
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

//    public static void main(String[] args){
//        int[][] array={
//            {-1,0,0,0,0,0,0,0,1},
//            {1,-1,-1,0,0,0,0,0,0},
//            {1,0,0,-1,0,0,0,0,0},
//            {0,1,0,0,-1,0,0,0,0},
//            {0,0,1,0,0,-1,0,0,0},
//            {0,0,0,1,0,0,-1,0,0},
//            {0,0,0,0,1,1,0,-1,0},
//            {0,0,0,0,0,0,1,-1,0},
//            {0,0,0,0,0,0,0,1,-1}};
//        Set<int[]> result = leftCalculate(array);
//        for (int[] each:result) {
//            for (int i:each) {
//                System.out.print(i+"\t");
//            }
//            System.out.println();
//        }
//    }
    /**
     *
     1	1	0	1	1	0	1	0	1
     1	0	1	0	0	1	0	1	1
     */
}

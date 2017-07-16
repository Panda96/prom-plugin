package cn.edu.nju.software.cripsylamp.util;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by CYF on 2017/7/16.
 */
public class Petrinet2Matrix {
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
                int yPosition = t.getLabel().charAt(0) - 'A';
                resultMatrix[xPosition][yPosition] = 1;
            }
            if (arcCollection.size() == 0) {
                resultMatrix[xPosition][tSize - 1] = 1;
            }
            //(p,t)-> -1
            arcCollection = net.getOutEdges(p);
            for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> each : arcCollection) {
                PetrinetNode t = each.getTarget();
                int yPosition = t.getLabel().charAt(0) - 'A';
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
}

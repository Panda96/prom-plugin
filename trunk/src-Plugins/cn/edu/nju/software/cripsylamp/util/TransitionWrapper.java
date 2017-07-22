package cn.edu.nju.software.cripsylamp.util;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by CYF on 2017/7/21.
 */
public class TransitionWrapper implements Cloneable {
    private Transition transition;
    //    private Petrinet parent;
    private int inTokens;
    private int inSize;
    private List<String> beforeSequence;
//    private

    public TransitionWrapper(Transition transition, Petrinet petrinet) {
        this.transition = transition;
//        this.parent = petrinet;
        this.inSize = petrinet.getInEdges(transition).size();
        this.inTokens = 1;
        this.beforeSequence = new ArrayList<>();
    }

    public TransitionWrapper(Transition transition, int inSize, int inTokens) {
        this.transition = transition;
        this.inSize = inSize;
        this.inTokens = inTokens;
        this.beforeSequence = new ArrayList<>();
    }

    public void addInTokens() {
        this.inTokens++;
    }

    public boolean hasFullIn() {
        if (this.inTokens == this.inSize) {
            return true;
        } else {
            return false;
        }
    }

    public TransitionWrapper clone() {
        return new TransitionWrapper(this.transition, this.inSize, this.inTokens);
    }

    public void setInTokens(int inTokens) {
        this.inTokens = inTokens;
    }

    public void addBeforeSequence(String seq) {
        this.beforeSequence.add(seq);
    }

    public List<String> mixBeforeSequence() {
        String last = "";
        int[] positions = new int[beforeSequence.size()];
        for (int i = 0; i < beforeSequence.size(); i++) {
            positions[i]=0;
        }
        List<String> mixList = mix(positions,last);
        return mixList;
    }

    public List<String> mix(int[] positions, String last) {
        List<String> list = new ArrayList<>();
        String next;
        boolean notAllEmpy=false;
        for (int i = 0; i < this.beforeSequence.size(); i++) {
            int[] nextPosition = positions.clone();
            if (nextPosition[i] < this.beforeSequence.get(i).length()) {
                notAllEmpy = true;
                next = last + (this.beforeSequence.get(i).charAt(nextPosition[i]++) + "");
                list.addAll(mix(nextPosition, next));
            }else{
                continue;
            }
        }
        if(!notAllEmpy){
            list.add(last);
        }
        return list;
    }
    
//    public static void main(String[] args){
//        TransitionWrapper wrapper = new TransitionWrapper(null,5,5);
//        wrapper.addBeforeSequence("ABC");
//        wrapper.addBeforeSequence("DE");
//        wrapper.addBeforeSequence("123");
//        wrapper.addBeforeSequence("456");
//        List<String> mixresult=wrapper.mixBeforeSequence();
//        for (int i=0;i<20;i++) {
//            System.out.println(mixresult.get(i));
//        }
//    }
}

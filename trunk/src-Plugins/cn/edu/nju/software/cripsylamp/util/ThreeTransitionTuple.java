package cn.edu.nju.software.cripsylamp.util;

/**
 * Created by CYF on 2017/7/18.
 */
public class ThreeTransitionTuple {
    private char left;
    private char right;

    private char loop1Char;

    public ThreeTransitionTuple(char left, char loop1Char, char right) {
        this.left = left;
        this.right = right;
        this.loop1Char = loop1Char;
    }


    public char getLeft() {
        return left;
    }

    public char getRight() {
        return right;
    }

    public char getLoop1Char() {
        return loop1Char;
    }

}

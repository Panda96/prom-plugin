package cn.edu.nju.software.cripsylamp.util;

/**
 * Created by CYF on 2017/7/25.
 */
public class CompareIntSet {
    int[] intSet;
    boolean hasSame;

    public CompareIntSet(int[] intSet) {
        this.intSet = intSet;
        hasSame = false;
    }

    public boolean isHasSame() {
        return hasSame;
    }

    public void setHasSame(boolean hasSame) {
        this.hasSame = hasSame;
    }

    public int[] getIntSet() {
        return intSet;
    }
}

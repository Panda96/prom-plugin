package cn.edu.nju.software.cripsylamp.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by keenan on 15/07/2017.
 */
public class Tuple {

    private Set<Character> leftPart;

    private Set<Character> rightPart;

    private boolean using;

    public Tuple(Set<Character> leftPart, Set<Character> rightPart) {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        this.using = true;
    }

    public Set<Character> getLeftPart() {
        return leftPart;
    }

    public void setLeftPart(Set<Character> leftPart) {
        this.leftPart = leftPart;
    }

    public Set<Character> getRightPart() {
        return rightPart;
    }

    public void setRightPart(Set<Character> rightPart) {
        this.rightPart = rightPart;
    }

    public boolean isUsing() {
        return using;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Tuple)) return false;

        Tuple tuple = (Tuple) o;

        if (leftPart.size() != tuple.leftPart.size()) return false;
        boolean llrr = true, lrrl = true;
        for (char each : leftPart) {
            if (!(tuple.getLeftPart().contains(each))) {
                llrr = false;
                break;
            }
        }

        for (char each : rightPart) {
            if (!(tuple.getRightPart().contains(each))) {
                llrr = false;
                break;
            }
        }

        for (char each : leftPart) {
            if (!((Tuple) o).rightPart.contains(each)) {
                lrrl = false;
            }
        }

        for (char each : rightPart) {
            if (!((Tuple) o).leftPart.contains(each)) {
                lrrl = false;
            }
        }

        return llrr || lrrl;
    }

    public boolean leftEquals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple)) return false;

        Tuple tuple = (Tuple) o;

        if (leftPart.size() != tuple.leftPart.size()) return false;
        for (Character each : leftPart) {
            if (!(tuple.getLeftPart().contains(each))) {
                return false;
            }
        }

        return true;
    }

    public boolean rightEquals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple)) return false;

        Tuple tuple = (Tuple) o;

        if (rightPart.size() != tuple.rightPart.size()) return false;
        for (Character each : rightPart) {
            if (!(tuple.getRightPart().contains(each))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        String res = "({";

        for (char c :
                leftPart) {
            res += c + ",";
        }

        res += "}, {";
        for (char c :
                rightPart) {
            res += c + ",";
        }

        res += "})";

        return res;
    }

    public Tuple leftUnion(Tuple b) {
        Set<Character> left = new HashSet<Character>() {
            {
                addAll(leftPart);
                addAll(b.leftPart);
            }
        };
        Set<Character> right = new HashSet<>(rightPart);

        return new Tuple(left, right);
    }

    public Tuple rightUnion(Tuple b) {
        Set<Character> right = new HashSet<Character>() {
            {
                addAll(rightPart);
                addAll(b.rightPart);
            }
        };
        Set<Character> left = new HashSet<>(leftPart);

        return new Tuple(left, right);
    }

    public boolean contain(Tuple tuple) {
        return this.leftPart.containsAll((tuple.leftPart)) && this.rightPart.containsAll((tuple.rightPart));
    }

    @Override
    public int hashCode() {
        int result = 0;

        for (char each : leftPart) {
            result += Math.pow(each, 2);
        }

        result *= 17;

        for (char each : rightPart) {
            result += each;
        }
        return result;
    }
}

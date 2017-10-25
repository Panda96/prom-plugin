package cn.edu.nju.software.cripsylamp.logRepair;

import java.util.HashSet;
import java.util.Iterator;

/**
 * @author keenan on 25/10/2017
 */
@SuppressWarnings("Duplicates")
public class ConnectItem {
    private HashSet<String> task_set;
    private HashSet<HashSet<String>> precursor;
    private HashSet<HashSet<String>> successor;

    public ConnectItem() {
    }

    public ConnectItem(HashSet<String> task_set, HashSet<HashSet<String>> precursor,
                       HashSet<HashSet<String>> successor) {
        this.task_set = task_set;
        this.precursor = precursor;
        this.successor = successor;
    }

    public void setTaskSet(HashSet<String> task_set) {
        this.task_set = task_set;
    }

    public void setPrecursor(HashSet<HashSet<String>> precursor) {
        this.precursor = precursor;
    }

    public void setSuccessor(HashSet<HashSet<String>> successor) {
        this.successor = successor;
    }

    public HashSet<String> getTaskSet() {
        return task_set;
    }

    public HashSet<HashSet<String>> getPrecursor() {
        return precursor;
    }

    public HashSet<HashSet<String>> getSuccessor() {
        return successor;
    }

    public void addPrecursor(HashSet<String> p) {
        boolean canAdd = true;
        if (task_set.equals(p)) {
            canAdd = false;
        } else {
            for (Iterator<HashSet<String>> it = successor.iterator(); it.hasNext(); ) {
                HashSet<String> s = it.next();
                if (s.equals(p)) {
                    canAdd = false;
                    break;
                }
            }
        }

        if (canAdd) precursor.add(p);
    }

    public void addSuccessor(HashSet<String> s) {
        boolean canAdd = true;
        if (task_set.equals(s)) {
            canAdd = false;
        } else {
            for (Iterator<HashSet<String>> it = precursor.iterator(); it.hasNext(); ) {
                HashSet<String> p = it.next();
                if (s.equals(p)) {
                    canAdd = false;
                    break;
                }
            }
        }

        if (canAdd) precursor.add(s);
    }

}

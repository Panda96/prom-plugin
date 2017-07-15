package cn.edu.nju.software.cripsylamp.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by keenan on 15/07/2017.
 */
public class Main {

    public static void main(String[] args) {
        Set<Integer> a = new HashSet<Integer>() {
            {
                for (int i = 0; i < 20; i++) {
                    add(i);
                }
            }
        };

        Iterator<Integer> integerIterator = a.iterator();

        while (integerIterator.hasNext()) {
            int i = integerIterator.next();

            System.out.println(i);

            a.remove(new Integer(i));

            System.out.println(a);
        }
    }
}

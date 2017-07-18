package cn.edu.nju.software.cripsylamp.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by keenan on 14/07/2017.
 */
public class Trace {
    Map<String, String> traces;

    public Map<String, String> getTraces() {
        return traces;
    }

    public void setTraces(Map<String, String> traces) {
        this.traces = traces;
    }

    public Trace(Map<String, String> traces) {

        this.traces = traces;
    }

    public boolean belongs2SameTrace(Set<Character> a, Set<Character> b) {
        if (a.containsAll(b) && b.containsAll(a)) {
            return true;
        }

        for (char outchar : a) {
            for (char inchar : b) {
                if (outchar == inchar) {
                    continue;
                }
                if (charBelongs2SameTrace(outchar, inchar)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean charBelongs2SameTrace(char c1, char c2) {
        for (String each : traces.values()) {
            if (each.contains(c1 + "") && each.contains(c2 + "")) {
                return true;
            }
        }
        return false;
    }

    public Trace removeT(Set<Character> t){
        Map<String,String> resultTraces = new HashMap<>();
        for(String key:this.traces.keySet()){
            String value = this.traces.get(key);
            for (char each:t) {
                value = value.replace(each+"","");
            }
            resultTraces.put(key,value);
        }
        return new Trace(resultTraces);
    }
}

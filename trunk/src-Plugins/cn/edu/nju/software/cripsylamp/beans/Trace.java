package cn.edu.nju.software.cripsylamp.beans;

import java.util.Map;

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
}

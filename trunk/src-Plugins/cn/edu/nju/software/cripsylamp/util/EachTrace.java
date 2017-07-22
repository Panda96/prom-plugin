package cn.edu.nju.software.cripsylamp.util;

/**
 * Created by CYF on 2017/7/22.
 */
public class EachTrace {
    private String traceString;
    private boolean hasAcessed;

    public EachTrace(String str){
        this.traceString = str;
        this.hasAcessed = false;
    }

    public void access(){
        this.hasAcessed = true;
    }


    public String getTraceString() {
        return traceString;
    }

    public boolean isHasAcessed() {
        return hasAcessed;
    }

}

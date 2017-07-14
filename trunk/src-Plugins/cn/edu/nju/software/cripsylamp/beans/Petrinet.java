package cn.edu.nju.software.cripsylamp.beans;

import cn.edu.nju.software.cripsylamp.util.ResultMessage;

/**
 * Created by CYF on 2017/7/14.
 */
public class Petrinet {
    private Place start;
    private Transition addT;

    public Petrinet(Place startPlace){
        start = startPlace;
    }

    /**
     * 向petrinet中添加t*
     * @param t
     * @return resultmessage
     */
    public ResultMessage addT(Transition t){
        Component c = findLastComponent();
        if(c.getClass()==Place.class) {
            c.addComponent(t);
            return ResultMessage.Success;
        }else
            return ResultMessage.LastComponentNotPlace;
    }

    /**
     * 找到最后一个place
     * @return Component
     */
    public Component findLastComponent(){
        Component c = start;
        while(c.next.size()!=0){
            c = c.next.get(0);
        }
        return c;
    }

    /**
     * 返回petrinet的向量表示
     * @return object
     */
    public Object describeAsPlaceVectorSet(){
        return null;
    }

    /**
     * 返回petrinet的工作日志
     * @return Object
     */
    public Object getWorkLog(){
        return null;
    }

}

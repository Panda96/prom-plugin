package cn.edu.nju.software.cripsylamp.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keenan on 14/07/2017.
 */
public class Component {
    /**
     * 名字
     */
    protected String name;
    /**
     * 接下来的元素
     */
    protected List<Component> next;

    public Component(String name) {
        this.name = name;
        next = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Component> getNext() {
        return next;
    }

    /**
     * 新增后继元素
     *
     * @param component 后继元素
     */
    public void addComponent(Component component) {
        next.add(component);
    }
}

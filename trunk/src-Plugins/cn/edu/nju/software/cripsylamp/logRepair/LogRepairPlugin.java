package cn.edu.nju.software.cripsylamp.logRepair;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author keenan on 25/10/2017
 */
public class LogRepairPlugin {
    @org.processmining.framework.plugin.annotations.Plugin(
            name = "Log Repair Plugin",
            parameterLabels = {},
            returnLabels = {"Petrinet"},
            returnTypes = {Petrinet.class},
            userAccessible = true,
            help = "Log Repair Plugin"
    )
    @UITopiaVariant(
            affiliation = "nju.software",
            author = "Y.F.Cheng & Q.H.Zhou",
            email = "151250206@smail.nju.edu.cn"
    )


    public Petrinet logRepairPlugin(UIPluginContext context) {
        ArrayList<ConnectItem> connectItems = new ArrayList<>();

        // 第一项
        HashSet<String> cur_1 = new HashSet<>();
        cur_1.add("A");
        HashSet<HashSet<String>> pre_1 = null;
        HashSet<String> succ_1_1 = new HashSet<>();
        succ_1_1.add("B");
        succ_1_1.add("C");
        HashSet<String> succ_1_2 = new HashSet<>();
        succ_1_2.add("D");
        HashSet<HashSet<String>> succ_1 = new HashSet<>();
        succ_1.add(succ_1_1);
        succ_1.add(succ_1_2);
        connectItems.add(new ConnectItem(cur_1, pre_1, succ_1));

        // 第二项
        HashSet<String> cur_2 = new HashSet<>();
        cur_2.add("B");
        cur_2.add("C");
        HashSet<HashSet<String>> pre_2 = new HashSet<>();
        HashSet<String> pre_2_1 = new HashSet<>();
        pre_2_1.add("A");
        pre_2.add(pre_2_1);
        HashSet<String> succ_2_1 = new HashSet<>();
        succ_2_1.add("E");
        HashSet<HashSet<String>> succ_2 = new HashSet<>();
        succ_2.add(succ_2_1);
        connectItems.add(new ConnectItem(cur_2, pre_2, succ_2));

        // 第三项
        HashSet<String> cur_3 = new HashSet<>();
        cur_3.add("D");
        HashSet<HashSet<String>> pre_3 = new HashSet<>();
        HashSet<String> pre_3_1 = new HashSet<>();
        pre_3_1.add("A");
        pre_3.add(pre_3_1);
        HashSet<HashSet<String>> succ_3 = new HashSet<>();
        HashSet<String> succ_3_1 = new HashSet<>();
        succ_3_1.add("E");
        succ_3.add(succ_3_1);
        connectItems.add(new ConnectItem(cur_3, pre_3, succ_3));

        // 第四项
        HashSet<String> cur_4 = new HashSet<>();
        cur_4.add("E");
        HashSet<HashSet<String>> pre_4 = new HashSet<>();
        HashSet<String> pre_4_1 = new HashSet<>();
        pre_4_1.add("B");
        pre_4_1.add("C");
        HashSet<String> pre_4_2 = new HashSet<>();
        pre_4_2.add("D");
        pre_4.add(pre_4_1);
        pre_4.add(pre_4_2);
        HashSet<HashSet<String>> succ_4 = null;
        connectItems.add(new ConnectItem(cur_4, pre_4, succ_4));

        // loop
        List<LoopStructure> loopStructures = new ArrayList<>();

        LoopStructure loopStructure1 = new LoopStructure();
        String new_Task_1 = "ab";
        HashSet<String> pre_Task_1 = new HashSet<>();
        pre_Task_1.add("B");
        HashSet<String> post_Task_1 = new HashSet<>();
        post_Task_1.add("C");
        loopStructure1.setNewTask(new_Task_1);
        loopStructure1.setPre_task(pre_Task_1);
        loopStructure1.setPost_task(post_Task_1);

        LoopStructure loopStructure2 = new LoopStructure();
        String new_Task_2 = "cd";
        HashSet<String> pre_Task_2 = new HashSet<>();
        pre_Task_2.add("D");
        HashSet<String> post_Task_2 = new HashSet<>();
        post_Task_2.add("E");
        loopStructure2.setNewTask(new_Task_2);
        loopStructure2.setPre_task(pre_Task_2);
        loopStructure2.setPost_task(post_Task_2);

        loopStructures.add(loopStructure1);
        loopStructures.add(loopStructure2);

        Table2Petrinet table2Petrinet = new Table2Petrinet();
        Petrinet petrinet = table2Petrinet.tranfer2net(connectItems, loopStructures);

        return petrinet;
    }

    public static void main(String[] args) {
        LogRepairPlugin plugin = new LogRepairPlugin();
        Petrinet petrinet = plugin.logRepairPlugin(null);
        Collection<Transition> transitions = petrinet.getTransitions();
        Collection<Place> places = petrinet.getPlaces();

        for (Transition transition : transitions) {
            System.out.print(transition.getLabel() + "\t");
        }

        for (Place place : places) {
            System.out.print(place.getLabel() + "\t");
        }
    }
}

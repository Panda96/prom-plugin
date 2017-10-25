package cn.edu.nju.software.cripsylamp.logRepair;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import java.io.*;
import java.util.*;

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
        String ADJ_LIST="adjacency_list.txt";
        String LOOP="loop.txt";

        List<String> adj_content = readFile(ADJ_LIST);
        List<ConnectItem> connectItems = parseConnectItem(adj_content);

        List<String> loop_content = readFile(LOOP);
        List<LoopStructure> loopStructures = parseLoopStructure(loop_content);

        Table2Petrinet table2Petrinet = new Table2Petrinet();
        Petrinet petrinet = table2Petrinet.tranfer2net(connectItems, loopStructures);

        return petrinet;
    }

    /**
     * 读文件
     *
     * @param path
     * @return
     */
    private List<String> readFile(String path) {
        File file = new File(path);
        List<String> content = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                content.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content;
    }

    /**
     * 解析邻接表
     *
     * @param content
     * @return
     */
    private List<ConnectItem> parseConnectItem(List<String> content) {
        List<ConnectItem> connectItems = new ArrayList<>();

        for (int i = 1; i < content.size(); i++) {
            ConnectItem connectItem = new ConnectItem();

            String line = content.get(i);
            System.out.println("line = " + line);
            String[] components = line.split("\t");

            HashSet<String> cur = new HashSet<>(Arrays.asList(components[0].split(" ")));
            connectItem.setTaskSet(cur);

            HashSet<HashSet<String>> pre = new HashSet<>();
            String[] pres = components[1].split(" ");
            for (String pres_item : pres) {
                pre.add(new HashSet<>(Arrays.asList(pres_item.split(","))));
            }
            connectItem.setPrecursor(pre);

            HashSet<HashSet<String>> succ = new HashSet<>();
            String[] succs = components[2].split(" ");
            for (String succs_item : succs) {
                succ.add(new HashSet<>(Arrays.asList(succs_item.split(","))));
            }
            connectItem.setSuccessor(succ);
            connectItems.add(connectItem);
        }

        return connectItems;
    }

    /**
     * 解析循环结构
     *
     * @param content
     * @return
     */
    private List<LoopStructure> parseLoopStructure(List<String> content) {
        List<LoopStructure> loopStructures = new ArrayList<>();

        for (int i = 1; i < content.size(); i++) {
            LoopStructure loopStructure = new LoopStructure();
            String line = content.get(i);
            System.out.println(line);
            String[] components = line.split("\t");

            loopStructure.setNewTask(components[0]);
            String[] pres = components[1].split(" ");
            HashSet<String> pre = new HashSet<>(Arrays.asList(pres));
            String[] succs = components[2].split(" ");
            HashSet<String> succ = new HashSet<>(Arrays.asList(succs));
            loopStructure.setPre_task(pre);
            loopStructure.setPost_task(succ);
            loopStructures.add(loopStructure);
        }
        return loopStructures;
    }
}

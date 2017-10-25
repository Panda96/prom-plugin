package cn.edu.nju.software.cripsylamp.logRepair;

/**
 * @author keenan on 25/10/2017
 */
public class ConnectList {
    //    ArrayList<ConnectItem> List = new ArrayList<ConnectItem>();
//
//    private static void updatePrecursor(HashSet<String> task_set, HashSet<String> suc) {
//        //add suc to the successor of the record whose task_set is task_set
//        int index = getIndex(task_set);
//        ConnectItem item = List.get(index);
//        item.addSuccessor(suc);
//
//    }
//
//
//    private static void updateSuccessor(HashSet<String> task_set, HashSet<String> suc) {
//        //add suc to the successor of the record whose task_set is task_set
//        int index = getIndex(task_set);
//        ConnectItem item = List.get(index);
//        item.addSuccessor(suc);
//
//    }
//
//
//    private static void addItemAfter(HashSet<String> task_set, ConnectItem item) {
//        //add item after task_set
//        HashSet<String> item_set = item.getTaskSet();
//        if(!isIn(item_set)){
//            for(int i=0; i< List.size(); i++){
//                ConnectItem line = List.get(i);
//                if(line.getTaskSet().equals(task_set)){
//                    if(i<List.size()-1){//this set is not the last
//                        List.add(i+1, item);
//                        break;
//                    }else{//this set is the last
//                        List.add(item);
//                        break;
//                    }
//                }
//            }
//        }
//
//    }
//
//    private static void addItemBefore(HashSet<String> task_set, ConnectItem item) {
//        //add item before task_set
//        HashSet<String> item_set = new HashSet<String>();
//        if(!isIn(item_set)){//item has not yet been included
//            for(int i=0; i< List.size(); i++){
//                ConnectItem line = List.get(i);
//                if(line.getTaskSet().equals(task_set)){
//                    List.add(i, item);
//                    break;
//                }
//            }
//        }
//
//    }
//
//
//    private static void addItem(ConnectItem item) {
//        HashSet<String> item_set = item.getTaskSet();
//        if(List!=null){
//            if(!isIn(item_set)) {
//                List.add(item);
//            }
//        }
//
//    }
//
//    public static ArrayList<HashSet<String>> getPrecursor(HashSet<String> set){
//        //so that u can find its precursors
//        ArrayList<HashSet<String>> Pre= new ArrayList<HashSet<String>>();//size=0 means not in the list
//        if(List!=null){
//            for(int i=0; i<List.size(); i++){
//                ConnectItem line = List.get(i);
//                if(line.getSuccessor().contains(set)){
//                    HashSet<String> pre=List.get(i).getTaskSet();
//                    Pre.add(pre);
//                }
//            }
//        }
//
//        return Pre;
//    }
//
//    private static int getIndex(HashSet<String> task_set) {
//        int index = -1;//-1 means not in the list
//        for(int i=0; i<List.size(); i++){
//            ConnectItem line = List.get(i);
//            if(line.getTaskSet().equals(task_set)){
//                index = i;
//                break;
//            }
//        }
//        return index;
//    }
//
//    private static boolean isIn(HashSet<String> set) {
//        boolean in = false;
//        for(int i=0; i< List.size(); i++){
//            ConnectItem line = List.get(i);
//            if(line.getTaskSet().equals(set)){
//                in = true;
//                break;
//            }
//        }
//        return in;
//    }
}

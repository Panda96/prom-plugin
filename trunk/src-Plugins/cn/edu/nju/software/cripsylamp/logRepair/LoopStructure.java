package cn.edu.nju.software.cripsylamp.logRepair;

import java.util.HashSet;

public class LoopStructure {//include number, new_t, re_t, pre_t, post_t
	private int index;
	private String new_task;
	private HashSet<String> re_task;
	private HashSet<String> pre_task=new HashSet<String>();
	private HashSet<String> post_task=new HashSet<String>();
	
	public int getIndex(){
		return index;
	}
	public String getNewTask(){
		return new_task;
	}
	public HashSet<String> getReTask(){
		return re_task;
	}
	public HashSet<String> getPreTask(){
		return pre_task;
	}
	public HashSet<String> getPostTask(){
		return post_task;
	}
	    
	public void setIndex(int index){
		this.index=index;
	}
	public void setNewTask(String new_task){
		this.new_task=new_task;
	}
	public void setReTask(HashSet<String> re_task){
		this.re_task=re_task;
	}
    public void setPre_task(HashSet<String> pre_task){
		
		this.pre_task=pre_task;
	}
    public void setPost_task(HashSet<String> post_task){
		
		this.post_task=post_task;
	}
	
	

}

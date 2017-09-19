package org.gcube.portlets.user.td.gwtservice.shared.task;

import java.io.Serializable;
import java.util.ArrayList;



/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ValidationsTasksMetadata implements Serializable {
	
	
	private static final long serialVersionUID = 4767980931682849226L;
	String id="ValidationsTasksMetadata";
	String title="Validations";
	ArrayList<TaskS> tasks;

	public ValidationsTasksMetadata(){
		
	}
	
	public ValidationsTasksMetadata(ArrayList<TaskS> tasks){
		this.tasks=tasks;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<TaskS> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<TaskS> tasks) {
		this.tasks = tasks;
	}

	@Override
	public String toString() {
		return "ValidationsTasksMetadata [id=" + id + ", title=" + title
				+ ", tasks=" + tasks + "]";
	}
	

	
	
}

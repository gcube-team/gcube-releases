package org.gcube.portlets.user.td.gwtservice.shared.task;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class TaskResubmitSession implements Serializable {

	private static final long serialVersionUID = -4503878699159491057L;
	
	protected TRId trId;
	protected String taskId;

	public TaskResubmitSession(){
		
	}
	
	public TaskResubmitSession(TRId trId, String taskId){
		this.trId=trId;
		this.taskId=taskId;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Override
	public String toString() {
		return "ResubmitTaskSession [trId=" + trId + ", taskId=" + taskId + "]";
	}

	
	
	
}

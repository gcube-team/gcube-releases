package org.gcube.portlets.user.td.gwtservice.shared.monitor;

import java.io.Serializable;

/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class OperationMonitorSession implements Serializable {

	
	private static final long serialVersionUID = 7998971732364151219L;
	
	private String taskId;
	private boolean inBackground;
	private boolean abort;
	private boolean hidden;

	public OperationMonitorSession(){
		
	}
	
	public OperationMonitorSession(String taskId){
		this.taskId=taskId;
		inBackground=false;
		abort=false;
	}
	
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean isInBackground() {
		return inBackground;
	}

	public void setInBackground(boolean inBackground) {
		this.inBackground = inBackground;
	}

	public boolean isAbort() {
		return abort;
	}

	public void setAbort(boolean abort) {
		this.abort = abort;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public String toString() {
		return "OperationMonitorSession [taskId=" + taskId + ", inBackground="
				+ inBackground + ", abort=" + abort + ", hidden=" + hidden
				+ "]";
	}

	
	
	
	
	
	
}

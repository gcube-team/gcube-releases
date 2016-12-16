/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.monitor;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.task.TaskS;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.UIOperationsId;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OperationMonitor implements Serializable {

	private static final long serialVersionUID = 5378053063599667767L;

	private String taskId;
	private UIOperationsId operationId;
	private TaskS task;
	private boolean inBackground;
	private boolean abort;
	private boolean hidden;
	private TRId trId;
	private String tabularResourceName;

	
	public OperationMonitor() {

	}

	public OperationMonitor(String taskId, UIOperationsId operationId) {
		this.operationId = operationId;
		this.taskId = taskId;
	}

	public UIOperationsId getOperationId() {
		return operationId;
	}

	public void setOperationId(UIOperationsId operationId) {
		this.operationId = operationId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public TaskS getTask() {
		return task;
	}

	public void setTask(TaskS task) {
		this.task = task;
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

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}
	
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public String getTabularResourceName() {
		return tabularResourceName;
	}

	public void setTabularResourceName(String tabularResourceName) {
		this.tabularResourceName = tabularResourceName;
	}

	@Override
	public String toString() {
		return "OperationMonitor [taskId=" + taskId + ", operationId="
				+ operationId + ", task=" + task + ", inBackground="
				+ inBackground + ", abort=" + abort + ", hidden=" + hidden
				+ ", trId=" + trId + ", tabularResourceName="
				+ tabularResourceName + "]";
	}

		
	
}

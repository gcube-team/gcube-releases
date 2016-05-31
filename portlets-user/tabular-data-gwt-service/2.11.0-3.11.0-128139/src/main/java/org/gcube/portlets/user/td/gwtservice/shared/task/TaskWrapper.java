package org.gcube.portlets.user.td.gwtservice.shared.task;

import java.io.Serializable;

import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.UIOperationsId;

/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TaskWrapper implements Serializable {


	private static final long serialVersionUID = -3291738536272488206L;
	
	private Task task;
	private UIOperationsId operationId;
	private TRId trId;
	
	public TaskWrapper(){
		
	}
	
	public TaskWrapper(Task task, UIOperationsId operationId, TRId trId) {
		super();
		this.task = task;
		this.operationId = operationId;
		this.trId = trId;
	}


	public Task getTask() {
		return task;
	}


	public void setTask(Task task) {
		this.task = task;
	}


	public UIOperationsId getOperationId() {
		return operationId;
	}


	public void setOperationId(UIOperationsId operationId) {
		this.operationId = operationId;
	}


	public TRId getTrId() {
		return trId;
	}


	public void setTrId(TRId trId) {
		this.trId = trId;
	}


	@Override
	public String toString() {
		return "TaskWrapper [task=" + task + ", operationId=" + operationId
				+ ", trId=" + trId + "]";
	}
	
	
	
	
	
	
}

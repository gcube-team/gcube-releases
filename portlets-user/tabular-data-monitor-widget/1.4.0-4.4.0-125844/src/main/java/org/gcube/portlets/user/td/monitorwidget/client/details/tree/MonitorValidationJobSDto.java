package org.gcube.portlets.user.td.monitorwidget.client.details.tree;

import org.gcube.portlets.user.td.gwtservice.shared.task.InvocationS;
import org.gcube.portlets.user.td.gwtservice.shared.task.WorkerState;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MonitorValidationJobSDto extends MonitorBaseDto {

	private static final long serialVersionUID = -4353641080571614057L;
	
	private WorkerState workerState;
	private float progress;
	private String description;
	private Throwable errorMessage;
	private String humanReadableStatus;
	private InvocationS invocation;
	
	
	

	public MonitorValidationJobSDto(){
		
	}
	
	/**
	 * 
	 * @param id
	 * @param workerState
	 * @param progress
	 * @param description
	 * @param errorMessage
	 * @param humanReadableStatus
	 * @param invocation
	 */
	public MonitorValidationJobSDto(String id, WorkerState workerState, float progress,
			String description, Throwable errorMessage,
			String humanReadableStatus, InvocationS invocation) {
		super(id);
		this.workerState = workerState;
		this.progress = progress;
		this.description = description;
		this.errorMessage = errorMessage;
		this.humanReadableStatus = humanReadableStatus;
		this.invocation = invocation;
	}
	

	public WorkerState getWorkerState() {
		return workerState;
	}

	public void setWorkerState(WorkerState workerState) {
		this.workerState = workerState;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}



	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Throwable getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(Throwable errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getHumanReadableStatus() {
		return humanReadableStatus;
	}

	public void setHumanReadableStatus(String humanReadableStatus) {
		this.humanReadableStatus = humanReadableStatus;
	}

	public InvocationS getInvocation() {
		return invocation;
	}

	public void setInvocation(InvocationS invocation) {
		this.invocation = invocation;
	}

	@Override
	public String toString() {
		return description;
	}

	
	

}

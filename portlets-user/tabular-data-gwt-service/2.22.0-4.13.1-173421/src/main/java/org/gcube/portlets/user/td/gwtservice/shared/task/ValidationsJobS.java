package org.gcube.portlets.user.td.gwtservice.shared.task;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ValidationsJobS implements Serializable {

	private static final long serialVersionUID = 5763629588700935290L;

	protected String id;// For grid only
	protected WorkerState workerState;
	protected float progress;
	protected String description;
	protected Throwable errorMessage;
	protected String humanReadableStatus;

	public ValidationsJobS() {

	}

	public ValidationsJobS(String id, WorkerState workerState, float progress,
			String description, Throwable errorMessage,
			String humanReadableStatus) {
		this.id = id;
		this.workerState = workerState;
		this.progress = progress;
		this.description = description;
		this.errorMessage = errorMessage;
		this.humanReadableStatus = humanReadableStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "ValidationsJobS [id=" + id + ", workerState=" + workerState
				+ ", progress=" + progress + ", description=" + description
				+ ", errorMessage=" + errorMessage + ", humanReadableStatus="
				+ humanReadableStatus + "]";
	}

}

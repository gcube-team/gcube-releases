package org.gcube.portlets.user.td.gwtservice.shared.task;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.table.Validations;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class JobS implements Serializable {

	private static final long serialVersionUID = 4502877601374000292L;

	private String id; // For grid only
	private float progress;
	private String humaReadableStatus;
	private JobSClassifier jobClassifier;
	private String description;
	private InvocationS invocation;
	private ArrayList<Validations> validations;
	private Throwable errorMessage;
	private WorkerState workerState;
	private ArrayList<ValidationsJobS> validationsJobS;

	public JobS() {

	}

	/**
	 * For Validation Tasks
	 * 
	 * @param id
	 *            Id
	 * @param progress
	 *            Progress
	 * @param humaReadableStatus
	 *            Human redable status
	 * @param jobClassifier
	 *            Job classifier
	 * @param description
	 *            Description
	 * @param validations
	 *            List of validations
	 * @param invocation
	 *            Invocation
	 */
	public JobS(String id, float progress, String humaReadableStatus, JobSClassifier jobClassifier, String description,
			ArrayList<Validations> validations, InvocationS invocation) {
		this.id = id;
		this.progress = progress;
		this.humaReadableStatus = humaReadableStatus;
		this.jobClassifier = jobClassifier;
		this.description = description;
		this.validations = validations;
		this.invocation = invocation;
		validationsJobS = null;
		errorMessage = null;
		validationsJobS = null;
	}

	/**
	 * For Operation Monitor
	 * 
	 * 
	 * @param id
	 *            Id
	 * @param progress
	 *            Progress
	 * @param humaReadableStatus
	 *            Human readable status
	 * @param jobClassifier
	 *            Job classifier
	 * @param description
	 *            Description
	 * @param workerState
	 *            Worker state
	 * @param errorMessage
	 *            Error message
	 * @param validationsJobs
	 *            Validations jobs
	 */
	public JobS(String id, float progress, String humaReadableStatus, JobSClassifier jobClassifier, String description,
			WorkerState workerState, Throwable errorMessage, ArrayList<ValidationsJobS> validationsJobs) {
		this.id = id;
		this.progress = progress;
		this.humaReadableStatus = humaReadableStatus;
		this.jobClassifier = jobClassifier;
		this.description = description;
		this.validationsJobS = validationsJobs;
		this.errorMessage = errorMessage;
		this.workerState = workerState;
		this.validations = null;
		this.invocation = null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public String getHumaReadableStatus() {
		return humaReadableStatus;
	}

	public void setHumaReadableStatus(String humaReadableStatus) {
		this.humaReadableStatus = humaReadableStatus;
	}

	public JobSClassifier getJobClassifier() {
		return jobClassifier;
	}

	public void setJobClassifier(JobSClassifier jobClassifier) {
		this.jobClassifier = jobClassifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<Validations> getValidations() {
		return validations;
	}

	public void setValidations(ArrayList<Validations> validations) {
		this.validations = validations;
	}

	public InvocationS getInvocation() {
		return invocation;
	}

	public void setInvocation(InvocationS invocation) {
		this.invocation = invocation;
	}

	public Throwable getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(Throwable errorMessage) {
		this.errorMessage = errorMessage;
	}

	public WorkerState getWorkerState() {
		return workerState;
	}

	public void setWorkerState(WorkerState workerState) {
		this.workerState = workerState;
	}

	public ArrayList<ValidationsJobS> getValidationsJobS() {
		return validationsJobS;
	}

	public void setValidationsJobS(ArrayList<ValidationsJobS> validationsJobS) {
		this.validationsJobS = validationsJobS;
	}

	@Override
	public String toString() {
		return "JobS [id=" + id + ", progress=" + progress + ", humaReadableStatus=" + humaReadableStatus
				+ ", jobClassifier=" + jobClassifier + ", description=" + description + ", invocation=" + invocation
				+ ", validations=" + validations + ", errorMessage=" + errorMessage + ", workerState=" + workerState
				+ ", validationsJobS=" + validationsJobS + "]";
	}

}

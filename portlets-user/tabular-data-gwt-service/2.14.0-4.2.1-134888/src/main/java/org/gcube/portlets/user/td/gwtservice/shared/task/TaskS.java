package org.gcube.portlets.user.td.gwtservice.shared.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class TaskS implements Serializable {

	private static final long serialVersionUID = 8228657333318157153L;

	private String id;
	private float progress;
	private State state;
	private Throwable errorCause;
	private String submitter;
	private Date startTime;
	private Date endTime;
	private ArrayList<JobS> jobs;
	private ArrayList<TRId> collateralTRIds;
	private String tabularResourceId;

	public TaskS() {

	}

	/**
	 * 
	 * @param id
	 * @param progress
	 * @param state
	 * @param errorCause
	 * @param submitter
	 * @param startTime
	 * @param endTime
	 * @param jobs
	 * @param collateralTRIds
	 * @param tabularResourceId TODO
	 *            
	 */
	public TaskS(String id, float progress, State state, Throwable errorCause,
			String submitter, Date startTime, Date endTime,
			ArrayList<JobS> jobs, ArrayList<TRId> collateralTRIds, String tabularResourceId) {
		this.id = id;
		this.progress = progress;
		this.state = state;
		this.errorCause = errorCause;
		this.submitter = submitter;
		this.startTime = startTime;
		this.endTime = endTime;
		this.jobs = jobs;
		this.collateralTRIds = collateralTRIds;
		this.tabularResourceId=tabularResourceId;
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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Throwable getErrorCause() {
		return errorCause;
	}

	public void setErrorCause(Throwable errorCause) {
		this.errorCause = errorCause;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public ArrayList<JobS> getJobs() {
		return jobs;
	}

	public void setJobs(ArrayList<JobS> jobs) {
		this.jobs = jobs;
	}

	public ArrayList<TRId> getCollateralTRIds() {
		return collateralTRIds;
	}

	public void setCollateralTRIds(ArrayList<TRId> collateralTRIds) {
		this.collateralTRIds = collateralTRIds;
	}
	
	public String getTabularResourceId() {
		return tabularResourceId;
	}

	public void setTabularResourceId(String tabularResourceId) {
		this.tabularResourceId = tabularResourceId;
	}

	@Override
	public String toString() {
		return "TaskS [id=" + id + ", progress=" + progress + ", state="
				+ state + ", errorCause=" + errorCause + ", submitter="
				+ submitter + ", startTime=" + startTime + ", endTime="
				+ endTime + ", jobs=" + jobs + ", collateralTRIds="
				+ collateralTRIds + ", tabularResourceId=" + tabularResourceId
				+ "]";
	}

	
}

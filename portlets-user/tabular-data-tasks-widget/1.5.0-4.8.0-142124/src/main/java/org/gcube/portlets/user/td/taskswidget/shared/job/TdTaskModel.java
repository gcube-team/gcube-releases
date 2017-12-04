package org.gcube.portlets.user.td.taskswidget.shared.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.td.taskswidget.shared.TdTableModel;
import org.gcube.portlets.user.td.taskswidget.shared.TdTabularResourceModel;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 27, 2014
 *
 */
public class TdTaskModel implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 2927579306998451009L;
	
	protected String identifier;
	protected String name;
	protected TdTaskStatusType status;
	protected Date startTime;
	protected Date submitTime;
	protected Date endTime;
	protected String elapsedTime;
	
	//Collateral Tabular Resource
	protected List<TdTabularResourceModel> listCollateralTRModel = null;
	
	//Tabula Resource is principal result
	protected TdTableModel tdTableModel;
	
	protected List<TdJobModel> listJobs = null;

	
	protected String description;
	protected float percentage = 0;
	protected boolean isCompleted = false;

	private long tabularResourceId;

	private String submitter;
	

	public TdTaskModel(){
	}


	/**
	 * 
	 * @param taskId
	 * @param name
	 */
	private TdTaskModel(String taskId, String name) {
		setId(taskId);
		setName(name);
	}



	/**
	 * 
	 * @param taskId
	 * @param name
	 * @param description
	 * @param state
	 * @param submitTime
	 * @param startTime
	 * @param endTime
	 * @param percentage
	 * @param isCompleted
	 */
	public TdTaskModel(String taskId, String name, String description, TdTaskStatusType state, Date submitTime, Date startTime, Date endTime, float percentage, boolean isCompleted) {
		this(taskId,name);
		setStartTime(startTime);
		setSubmitTime(submitTime);
		setStatus(state);
		setEndTime(endTime);
		setDescription(description);
		setPercentage(percentage);
		setCompleted(isCompleted);
	}
	
	public void setId(String jobId){
		this.identifier = jobId;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public String getTaskId(){
		return identifier;
	}
	
	public String getName(){
		return name;
	}
	
	
	public Date getSubmitTime(){
		return submitTime;
	}
	
	public void setSubmitTime(Date startTime){
		this.submitTime = startTime;
	}
	
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}
	
	public Date getEndTime(){
		return endTime;
	}
	
	public void setPercentage(float percentage){
		this.percentage = percentage;
	}
	
	public float getPercentage(){
		return percentage;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setName(String jobName) {
		this.name = jobName;
	}

	public void setTaskIdentifier(String taskId) {
		this.identifier = taskId;
	}

	public void setDownloadState(TdTaskStatusType downloadState) {
		this.status = downloadState;
	}

	public List<TdJobModel> getListJobs() {
		return listJobs;
	}


	public void setListJobs(ArrayList<TdJobModel> listJobs) {
		this.listJobs = listJobs;
	}

	public boolean isCompleted() {
		return isCompleted;
	}


	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public TdTableModel getTdTableModel() {
		return tdTableModel;
	}


	public void setTdTableModel(TdTableModel tdTableModel) {
		this.tdTableModel = tdTableModel;
	}


	public TdTaskStatusType getStatus() {
		return status;
	}


	public void setStatus(TdTaskStatusType status) {
		this.status = status;
	}


	public List<TdTabularResourceModel> getListCollateralTRModel() {
		return listCollateralTRModel;
	}


	public void setListCollateralTRModel(ArrayList<TdTabularResourceModel> listCollateralTRModel) {
		this.listCollateralTRModel = listCollateralTRModel;
	}


//	/**
//	 * @param jobName
//	 */
//	public void updateName(String jobName) {
//		this.name+=jobName+"; ";
//		
//	}


	/**
	 * @param value
	 */
	public void setTabularResourceId(long value) {
		this.tabularResourceId = value;
		
	}


	public long getTabularResourceId() {
		return tabularResourceId;
	}


	/**
	 * @param submitter
	 */
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
		
	}


	public String getSubmitter() {
		return submitter;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTaskModel [identifier=");
		builder.append(identifier);
		builder.append(", name=");
		builder.append(name);
		builder.append(", status=");
		builder.append(status);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", submitTime=");
		builder.append(submitTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", elapsedTime=");
		builder.append(elapsedTime);
		builder.append(", listCollateralTRModel=");
		builder.append(listCollateralTRModel);
		builder.append(", tdTableModel=");
		builder.append(tdTableModel);
		builder.append(", listJobs=");
		builder.append(listJobs);
		builder.append(", description=");
		builder.append(description);
		builder.append(", percentage=");
		builder.append(percentage);
		builder.append(", isCompleted=");
		builder.append(isCompleted);
		builder.append(", tabularResourceId=");
		builder.append(tabularResourceId);
		builder.append(", submitter=");
		builder.append(submitter);
		builder.append("]");
		return builder.toString();
	}
}

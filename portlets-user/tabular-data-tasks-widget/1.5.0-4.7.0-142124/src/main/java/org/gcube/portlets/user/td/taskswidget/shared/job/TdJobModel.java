package org.gcube.portlets.user.td.taskswidget.shared.job;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class TdJobModel extends BaseModelData implements Serializable{


	/**
	 * 
	 */
	
	public static enum ColumnConfigTdJobModel {
		Classifier("Classifier","Classifier"), 
//		Status("Status", "Status"), 
		Type("Type", "Type"),
		Progress("Progress", "Progress"),
		StatusIcon("StatusIcon", "Status"),

		Start_Time("Start_Time", "Start Time"), 
		End_Time("End_Time", "End Time"),
		Time("Time", "Time"),
		HumanReadableStatus("Status","Status"),
		OperationInfo("OperationInfo","Operation Info"), ValidationJobs("ValidationJobs", "Validation Jobs");
		
		String id;
		String label;
		ColumnConfigTdJobModel(String id, String label){
			this.id = id;
			this.label = label;
		}
		
		public String getId() {
			return id;
		}
		public String getLabel() {
			return label;
		}
	}
	
	private static final long serialVersionUID = 1L;
	

	protected String jobIdentifier;
	protected String jobName;
	protected Date startTime;
	protected Date submitTime;
	protected Date endTime;
	protected String elapsedTime;

	protected String description;
	protected float progressPercentage = 0;
	
	
	protected TdJobStatusType status;
	protected TdJobClassifierType classifierType;
	protected TdOperationModel opdModel;
	protected String errorMessage;


	private String humanReadableStatus;


	private List<TdValidationDescription> lstVD;


	private List<TdJobModel> listValidationJobModel;

	public TdJobModel(){
	}


	/**
	 * 
	 * @param jobId
	 * @param jobName
	 */
	public TdJobModel(String jobId, String jobName) {
		setJobIdentifier(jobId);
		setJobName(jobName);
	}

	/**
	 * 
	 * @param jobIdentifier
	 * @param jobName
	 * @param description
	 * @param state
	 * @param submitTime
	 * @param endTime
	 * @param percentage
	 */
	public TdJobModel(String jobIdentifier, String jobName, TdJobClassifierType classifierType, TdJobStatusType status, Date startTime, Date endTime, float percentage, String erroMessage) {
		this(jobIdentifier,jobName);
		setEndTime(endTime);
		setStartTime(startTime);
		setProgressPercentage(percentage);
		setClassifierType(classifierType);
		setStatus(status);
		setErrorMessage(erroMessage);
	}
	


	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public String getJobIdentifier(){
		return jobIdentifier;
	}
	
	public String getJobName(){
		return jobName;
	}
	
	
	public Date getSubmitTime(){
		return submitTime;
	}
	
	public void setSubmitTime(Date startTime){
		this.submitTime = startTime;
	}
	
	public void setEndTime(Date endTime){
		this.endTime = endTime;
		
		this.set(ColumnConfigTdJobModel.End_Time.getId(), endTime);
	}
	
	public Date getEndTime(){
		return endTime;
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
		
		this.set(ColumnConfigTdJobModel.Start_Time.getId(), startTime);
	}

	protected void setJobName(String jobName) {
		this.jobName = jobName;
	}

	
	public void setJobIdentifier(String jobIdentifier) {
		this.jobIdentifier = jobIdentifier;
	}


	public TdJobClassifierType getClassifierType() {
		return classifierType;
	}


	public void setClassifierType(TdJobClassifierType classifierType) {
		this.classifierType = classifierType;
		
		this.set(ColumnConfigTdJobModel.Classifier.getId(), classifierType);
	}


	public TdJobStatusType getStatus() {
		return status;
	}


	public void setStatus(TdJobStatusType status) {
		this.status = status;
//		this.set(ColumnConfigTdJobModel.Status.getId(), status);
	}


	public TdOperationModel getOpdModel() {
		return opdModel;
	}


	public void setOpdModel(TdOperationModel opdModel) {
		this.opdModel = opdModel;
		this.set(ColumnConfigTdJobModel.Type.getId(), opdModel.getName());
		this.set(ColumnConfigTdJobModel.OperationInfo.getId(), opdModel);
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public float getProgressPercentage() {
		return progressPercentage;
	}


	public void setProgressPercentage(float progressPercentage) {
		this.progressPercentage = progressPercentage;
		
		this.set(ColumnConfigTdJobModel.Progress.getId(), progressPercentage);
	}
	
	/**
	 * @param humaReadableStatus
	 */
	public void setHumanReadableStatus(String humaReadableStatus) {
		this.humanReadableStatus = humaReadableStatus;
		this.set(ColumnConfigTdJobModel.HumanReadableStatus.getId(), humanReadableStatus);
	}

	public String getHumanReadableStatus() {
		return humanReadableStatus;
	}


	/**
	 * @param List oValidations
	 */
	public void setValidations(List<TdValidationDescription> lstVD) {
		this.lstVD = lstVD;
	}


	public List<TdValidationDescription> getLstValidations() {
		return lstVD;
	}


	/**
	 * @param listValidationJobModel
	 */
	public void setListValidationJobs(List<TdJobModel> listValidationJobModel) {
		this.set(ColumnConfigTdJobModel.ValidationJobs.getId(), listValidationJobModel);
		this.listValidationJobModel = listValidationJobModel;
		
	}


	public List<TdJobModel> getListValidationJobModel() {
		return listValidationJobModel;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdJobModel [jobIdentifier=");
		builder.append(jobIdentifier);
		builder.append(", jobName=");
		builder.append(jobName);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", submitTime=");
		builder.append(submitTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", elapsedTime=");
		builder.append(elapsedTime);
		builder.append(", description=");
		builder.append(description);
		builder.append(", progressPercentage=");
		builder.append(progressPercentage);
		builder.append(", status=");
		builder.append(status);
		builder.append(", classifierType=");
		builder.append(classifierType);
		builder.append(", opdModel=");
		builder.append(opdModel);
		builder.append(", errorMessage=");
		builder.append(errorMessage);
		builder.append(", humanReadableStatus=");
		builder.append(humanReadableStatus);
		builder.append(", lstVD=");
		builder.append(lstVD);
		builder.append(", listValidationJobModel=");
		builder.append(listValidationJobModel);
		builder.append("]");
		return builder.toString();
	}
}

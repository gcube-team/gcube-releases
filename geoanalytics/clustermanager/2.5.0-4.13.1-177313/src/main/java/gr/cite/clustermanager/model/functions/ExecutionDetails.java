package gr.cite.clustermanager.model.functions;

import java.io.Serializable;
import java.util.UUID;

public class ExecutionDetails implements Serializable {

	private static final long serialVersionUID = 4849309531298719439L;

	private String id;
	private String submissionOrigin;
	private String tenantName;
	private Long startTimestamp;
	private Long stopTimestamp;
	private ExecutionStatus status;
	private Integer progress;
	private String layerID;
	private String layerName;
	private String userID;
	private String pluginID;
	private String projectID;
	
	
	public ExecutionDetails(){
		this(UUID.randomUUID().toString(), null, null, System.currentTimeMillis(), null, null, null, null);
	}
	
	public ExecutionDetails(String id, String submissionOrigin, String tenantName, Long startTimestamp, String userID, String pluginID, String projectID, String layerName) {
		this.id = id;
		this.submissionOrigin = submissionOrigin;
		this.startTimestamp = startTimestamp;
		this.userID = userID;
		this.pluginID = pluginID;
		this.projectID = projectID;
		this.progress = 0;
		this.status = ExecutionStatus.INPROGRESS;
		this.stopTimestamp = null;
		this.layerID = null;
		this.layerName = layerName;
	}
	
	
	
	public String getLayerID() {
		return layerID;
	}

	public void setLayerID(String layerID) {
		this.layerID = layerID;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getStartTimestamp() {
		return startTimestamp;
	}
	public void setStartTimestamp(Long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}
	public Long getStopTimestamp() {
		return stopTimestamp;
	}
	public void setStopTimestamp(Long stopTimestamp) {
		this.stopTimestamp = stopTimestamp;
	}
	public ExecutionStatus getStatus() {
		return status;
	}
	public void setStatus(ExecutionStatus status) {
		this.status = status;
	}
	public Integer getProgress() {
		return progress;
	}
	public void setProgress(Integer progress) {
		this.progress = progress;
	}
	public String getSubmissionOrigin() {
		return submissionOrigin;
	}
	public void setSubmissionOrigin(String submissionOrigin) {
		this.submissionOrigin = submissionOrigin;
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPluginID() {
		return pluginID;
	}

	public void setPluginID(String pluginID) {
		this.pluginID = pluginID;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	
	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	
	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	@Override
	public String toString() {
		return "[ "
				+ "id : " + id
				+ " progress: " + progress 
				+ " status: " + status 
				+ " startTimestamp: " + startTimestamp 
				+ " stopTimestamp: " + stopTimestamp
				+ " submissionOrigin: " + submissionOrigin 
				+ " ]"; 
	}

	
}

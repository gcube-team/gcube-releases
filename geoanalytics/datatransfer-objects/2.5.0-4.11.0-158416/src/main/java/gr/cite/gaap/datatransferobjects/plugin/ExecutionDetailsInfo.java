package gr.cite.gaap.datatransferobjects.plugin;

import java.util.Calendar;

/**
 * @author vfloros
 *
 */
public class ExecutionDetailsInfo {

	private String executionDetailsID;
	private String submissionOrigin;
	private Long startTimestamp;
	private Long stopTimestamp;
	private String status;
	private Integer progress;
	private String layerName;
	private String layerId;
	private String userName;
	private String pluginName;
	private String projectName;
	private String tenantName;
	
	public ExecutionDetailsInfo() {}

	public ExecutionDetailsInfo(String executionDetailsID, String submissionOrigin, Long startTimestamp,
			Long stopTimestamp, String status, Integer progress, String layerName, String layerId,
			String userName, String pluginName, String projectName, String tenantName) {
		super();
		
		this.executionDetailsID = executionDetailsID;
		this.submissionOrigin = submissionOrigin;
		this.startTimestamp = startTimestamp;//convertMiliSecondsToDateString(startTimestamp);
		this.stopTimestamp = stopTimestamp;//convertMiliSecondsToDateString(stopTimestamp);
		this.status = status;
		this.progress = progress;
		this.layerName = layerName;
		this.userName = userName;
		this.pluginName = pluginName;
		this.projectName = projectName;
		this.tenantName = tenantName;
		this.layerId = layerId;
	}

	public String getExecutionDetailsID() {
		return executionDetailsID;
	}

	public void setExecutionDetailsID(String executionDetailsID) {
		this.executionDetailsID = executionDetailsID;
	}

	public String getSubmissionOrigin() {
		return submissionOrigin;
	}

	public void setSubmissionOrigin(String submissionOrigin) {
		this.submissionOrigin = submissionOrigin;
	}

	public Long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Long startTimestamp) {
		this.startTimestamp = startTimestamp;//convertMiliSecondsToDateString(startTimestamp);
	}

	public Long getStopTimestamp() {
		return stopTimestamp;
	}

	public void setStopTimestamp(Long stopTimestamp) {
		this.stopTimestamp = stopTimestamp;//convertMiliSecondsToDateString(stopTimestamp);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	
	public String getLayerId() {
		return layerId;
	}

	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}

	public String convertMiliSecondsToDateString(Long timeStamp) {
		if(timeStamp == null || timeStamp == 0L) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeStamp);

		int mYear = calendar.get(Calendar.YEAR);
		int mMonth = calendar.get(Calendar.MONTH);
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);
		int mHour = calendar.get(Calendar.HOUR_OF_DAY);
		int mMinutes = calendar.get(Calendar.MINUTE);

		return mMonth + "/" + mDay + "/" + mYear + " " + mHour + ":" + mMinutes;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((executionDetailsID == null) ? 0 : executionDetailsID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecutionDetailsInfo other = (ExecutionDetailsInfo) obj;
		if (executionDetailsID == null) {
			if (other.executionDetailsID != null)
				return false;
		} else if (!executionDetailsID.equals(other.executionDetailsID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExecutionDetailsInfo [executionDetailsID=" + executionDetailsID + ", submissionOrigin="
				+ submissionOrigin + ", startTimestamp=" + startTimestamp + ", stopTimestamp=" + stopTimestamp
				+ ", status=" + status + ", progress=" + progress + ", layerName=" + layerName + ", userName="
				+ userName + ", pluginName=" + pluginName + ", projectName=" + projectName + "]";
	}
}

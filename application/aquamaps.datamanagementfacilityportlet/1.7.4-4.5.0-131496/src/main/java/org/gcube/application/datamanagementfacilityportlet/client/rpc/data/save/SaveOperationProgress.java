package org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SaveOperationProgress implements IsSerializable{

	
	
	private SaveOperationState state=SaveOperationState.RETRIEVING_FILES;
	private long toSaveCount=Long.MAX_VALUE;
	private long savedCount=0l;
	private String failureReason;
	private String failureDetails;
	public SaveOperationProgress() {
		// TODO Auto-generated constructor stub
	}
	
	public SaveOperationProgress(SaveOperationState state, long toSaveCount,
			long savedCount, String failureReason, String failureDetails) {
		super();
		this.state = state;
		this.toSaveCount = toSaveCount;
		this.savedCount = savedCount;
		this.failureReason = failureReason;
		this.failureDetails = failureDetails;
	}

	public SaveOperationState getState() {
		return state;
	}
	public void setState(SaveOperationState state) {
		this.state = state;
	}
	public long getToSaveCount() {
		return toSaveCount;
	}
	public void setToSaveCount(long toSaveCount) {
		this.toSaveCount = toSaveCount;
	}
	public long getSavedCount() {
		return savedCount;
	}
	public void setSavedCount(long savedCount) {
		this.savedCount = savedCount;
	}
	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	public String getFailureDetails() {
		return failureDetails;
	}
	public void setFailureDetails(String failureDetails) {
		this.failureDetails = failureDetails;
	}

		
	@Override
	public String toString() {
		return "SaveOperation [state=" + state + ", toSaveCount=" + toSaveCount
				+ ", savedCount=" + savedCount + ", failureReason="
				+ failureReason + ", failureDetails=" + failureDetails + "]";
	}
	
	
}

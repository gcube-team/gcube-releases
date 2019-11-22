package org.gcube.data.publishing.gCatFeeder.service.model.reports;

import java.time.Instant;

public class GenericInfos{
	
	private Instant startTime;
	private Instant endTime;
	private Boolean success=false;
	private String genericMessage;
	
	
	public Instant getStartTime() {
		return startTime;
	}
	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}
	public Instant getEndTime() {
		return endTime;
	}
	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getGenericMessage() {
		return genericMessage;
	}
	public void setGenericMessage(String genericMessage) {
		this.genericMessage = genericMessage;
	}
	
	
	
}
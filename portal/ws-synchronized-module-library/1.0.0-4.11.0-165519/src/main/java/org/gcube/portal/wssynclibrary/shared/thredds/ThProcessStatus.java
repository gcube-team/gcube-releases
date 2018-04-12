package org.gcube.portal.wssynclibrary.shared.thredds;

import java.io.Serializable;


public class ThProcessStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -432977518148737956L;
	private Long queuedTransfers;
	private Long servedTransfers;
	private Long errorCount;
	private Status status;
	
	private float percentCompleted = 0;
	
	private String logBuilder;
	
	private String currentMessage="Waiting to start..";
	
	public ThProcessStatus() {
	}

	public ThProcessStatus(Long queuedTransfers, Long servedTransfers, Long errorCount, Status status,
			String log, String currentMessage, float percentCompleted) {
		super();
		this.queuedTransfers = queuedTransfers;
		this.servedTransfers = servedTransfers;
		this.errorCount = errorCount;
		this.status = status;
		this.logBuilder = log;
		this.currentMessage = currentMessage;
		this.percentCompleted = percentCompleted;
	}

	public Long getQueuedTransfers() {
		return queuedTransfers;
	}

	public void setQueuedTransfers(Long queuedTransfers) {
		this.queuedTransfers = queuedTransfers;
	}

	public Long getServedTransfers() {
		return servedTransfers;
	}

	public void setServedTransfers(Long servedTransfers) {
		this.servedTransfers = servedTransfers;
	}

	public Long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Long errorCount) {
		this.errorCount = errorCount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getLogBuilder() {
		return logBuilder;
	}

	public void setLogBuilder(String logBuilder) {
		this.logBuilder = logBuilder;
	}

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}
	
	public float getPercentCompleted() {
		return percentCompleted;
	}
	
	public void setPercentCompleted(float percentCompleted) {
		this.percentCompleted = percentCompleted;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThProcessStatus [queuedTransfers=");
		builder.append(queuedTransfers);
		builder.append(", servedTransfers=");
		builder.append(servedTransfers);
		builder.append(", errorCount=");
		builder.append(errorCount);
		builder.append(", status=");
		builder.append(status);
		builder.append(", percentCompleted=");
		builder.append(percentCompleted);
		builder.append(", logBuilder=");
		builder.append(logBuilder);
		builder.append(", currentMessage=");
		builder.append(currentMessage);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
	
package org.gcube.data.access.queueManager.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("LogItem")
public class LogItem implements QueueItem{

	@XStreamAsAttribute
	private String executionId;
	private RemoteExecutionStatus status;
	private String logMessage;
	
	
	public LogItem(String executionId, RemoteExecutionStatus status,
			String logMessage) {
		super();
		this.executionId = executionId;
		this.status = status;
		this.logMessage = logMessage;
	}


	/**
	 * @return the executionId
	 */
	public String getExecutionId() {
		return executionId;
	}


	/**
	 * @return the status
	 */
	public RemoteExecutionStatus getStatus() {
		return status;
	}


	/**
	 * @return the logMessage
	 */
	public String getLogMessage() {
		return logMessage;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LogItem [executionId=");
		builder.append(executionId);
		builder.append(", status=");
		builder.append(status);
		builder.append(", logMessage=");
		builder.append(logMessage);
		builder.append("]");
		return builder.toString();
	}
	
	public String getId() {
		return getExecutionId();
	}
	
	
}

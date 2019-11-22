package org.gcube.portlets.admin.accountingmanager.server.util;

import java.io.Serializable;

import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class TaskWrapper implements Serializable {

	private static final long serialVersionUID = -4010108343968344171L;
	private String operationId;
	private TaskStatus taskStatus;
	private String errorMessage;
	private SeriesResponse seriesResponse;

	public TaskWrapper(String operationId, TaskStatus taskStatus,
			SeriesResponse seriesResponse) {
		super();
		this.operationId = operationId;
		this.taskStatus = taskStatus;
		this.errorMessage = null;
		this.seriesResponse = seriesResponse;
	}

	public TaskWrapper(String operationId, TaskStatus taskStatus,
			String errorMessage) {
		super();
		this.operationId = operationId;
		this.taskStatus = taskStatus;
		this.errorMessage = errorMessage;
		this.seriesResponse = null;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public SeriesResponse getSeriesResponse() {
		return seriesResponse;
	}

	public void setSeriesResponse(SeriesResponse seriesResponse) {
		this.seriesResponse = seriesResponse;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "TaskWrapper [operationId=" + operationId + ", taskStatus="
				+ taskStatus + ", errorMessage=" + errorMessage
				+ ", seriesResponse=" + seriesResponse + "]";
	}

}

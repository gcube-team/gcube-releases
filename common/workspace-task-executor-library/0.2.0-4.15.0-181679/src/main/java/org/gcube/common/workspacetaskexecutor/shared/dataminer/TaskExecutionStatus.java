package org.gcube.common.workspacetaskexecutor.shared.dataminer;

import java.io.Serializable;

import org.gcube.common.workspacetaskexecutor.shared.BaseTaskExecutionStatus;
import org.gcube.common.workspacetaskexecutor.shared.TaskStatus;


/**
 * The Class TaskExecutionStatus.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 16, 2018
 */
public class TaskExecutionStatus implements BaseTaskExecutionStatus, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4801536252236521384L;
	private Long errorCount;
	private TaskStatus status;
	private Float percentCompleted = new Float(0);
	//private String log;
	private String message="Waiting to start..";
	private TaskComputation taskComputation;
	private TaskConfiguration taskConfiguration;


	/**
	 * Instantiates a new task execution status.
	 */
	public TaskExecutionStatus() {
	}



	/**
	 * Instantiates a new task execution status.
	 *
	 * @param taskConfiguration the task configuration
	 * @param taskComputation the task computation
	 */
	public TaskExecutionStatus(TaskConfiguration taskConfiguration, TaskComputation taskComputation) {
		this.taskConfiguration = taskConfiguration;
		this.taskComputation = taskComputation;
	}


	/**
	 * Instantiates a new task execution status.
	 *
	 * @param errorCount the error count
	 * @param status the status
	 * @param percentCompleted the percent completed
	 * @param currentMessage the current message
	 * @param taskConfiguration the task configuration
	 * @param taskComputation the task computation
	 */
	public TaskExecutionStatus(Long errorCount, TaskStatus status, Float percentCompleted, String currentMessage, TaskConfiguration taskConfiguration, TaskComputation taskComputation) {
		this.errorCount = errorCount;
		this.status = status;
		this.percentCompleted = percentCompleted;
		//this.log = log;
		this.message = currentMessage;
		this.taskComputation = taskComputation;
		this.taskConfiguration = taskConfiguration;
	}


	/**
	 * Gets the task computation.
	 *
	 * @return the task computation
	 */
	public TaskComputation getTaskComputation() {

		return taskComputation;
	}

	/**
	 * Sets the task computation.
	 *
	 * @param computationId the new task computation
	 */
	public void setTaskComputation(TaskComputation computationId) {

		this.taskComputation = computationId;
	}


	/**
	 * Gets the error count.
	 *
	 * @return the error count
	 */
	public Long getErrorCount() {
		return errorCount;
	}

	/**
	 * Sets the error count.
	 *
	 * @param errorCount the new error count
	 */
	public void setErrorCount(Long errorCount) {
		this.errorCount = errorCount;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
	}

//	/**
//	 * Gets the log builder.
//	 *
//	 * @return the log builder
//	 */
//	public String getLog() {
//		return log;
//	}
//
//
//	/**
//	 * Adds the log.
//	 *
//	 * @param logMessage the log message
//	 */
//	public void addLog(String logMessage) {
//		if(log==null)
//			this.log = logMessage;
//		else
//			log=logMessage+log;
//	}

	/**
	 * Gets the current message.
	 *
	 * @return the current message
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * Sets the message.
	 *
	 * @param currentMessage the new message
	 */
	public void setMessage(String currentMessage) {
		this.message = currentMessage;
	}

	/**
	 * Gets the percent completed.
	 *
	 * @return the percent completed
	 */
	public Float getPercentCompleted() {
		return percentCompleted;
	}

	/**
	 * Sets the percent completed.
	 *
	 * @param percentCompleted the new percent completed
	 */
	public void setPercentCompleted(float percentCompleted) {
		this.percentCompleted = percentCompleted;
	}



	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.shared.BaseTaskExecutionStatus#getTaskConfiguration()
	 */
	public TaskConfiguration getTaskConfiguration() {

		return taskConfiguration;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TaskExecutionStatus [errorCount=");
		builder.append(errorCount);
		builder.append(", status=");
		builder.append(status);
		builder.append(", percentCompleted=");
		builder.append(percentCompleted);
		builder.append(", currentMessage=");
		builder.append(message);
		builder.append(", taskComputation=");
		builder.append(taskComputation);
		builder.append(", taskConfiguration=");
		builder.append(taskConfiguration);
		builder.append("]");
		return builder.toString();
	}

}

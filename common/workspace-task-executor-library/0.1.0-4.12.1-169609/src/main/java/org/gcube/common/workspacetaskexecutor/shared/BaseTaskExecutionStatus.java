package org.gcube.common.workspacetaskexecutor.shared;



/**
 * The Interface BaseTaskExecutionStatus.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 2, 2018
 */
public interface BaseTaskExecutionStatus {


	/**
	 * Gets the error count.
	 *
	 * @return the error count
	 */
	public Long getErrorCount();

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public TaskStatus getStatus();


	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage();


	/**
	 * Gets the percent completed.
	 *
	 * @return the percent completed
	 */
	public Float getPercentCompleted();


	/**
	 * Gets the task configuration.
	 *
	 * @return the task configuration
	 */
	public BaseTaskConfiguration getTaskConfiguration();

}

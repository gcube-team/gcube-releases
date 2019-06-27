/**
 *
 */
package org.gcube.common.workspacetaskexecutor.shared;

import java.util.List;


/**
 * The Interface BaseTaskOutput.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 7, 2018
 */
public interface BaseTaskOutput {


	/**
	 * Gets the task execution status.
	 *
	 * @return the task execution status
	 */
	public BaseTaskExecutionStatus getTaskExecutionStatus();


	/**
	 * Gets the output message.
	 *
	 * @return the outputMessage
	 */
	public List<String> getOutputMessages();


}
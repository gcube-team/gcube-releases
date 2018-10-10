/**
 *
 */
package org.gcube.common.workspacetaskexecutor.shared;

import java.io.Serializable;
import java.util.List;



/**
 * The Class TaskOutput.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 7, 2018
 */
public class TaskOutput implements BaseTaskOutput, Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 4243040464402882775L;
	private BaseTaskExecutionStatus taskExecutionStatus;
	private List<String> outputMessages;

	/**
	 * Instantiates a new task parameter.
	 */
	public TaskOutput() {

	}


	/**
	 * Instantiates a new task output.
	 *
	 * @param taskExecutionstatus the task executionstatus
	 * @param outputMessages the output messages
	 */
	public TaskOutput(BaseTaskExecutionStatus taskExecutionstatus, List<String> outputMessages) {
		this.taskExecutionStatus = taskExecutionstatus;
		this.outputMessages = outputMessages;
	}



	/**
	 * @return the taskExecutionStatus
	 */
	public BaseTaskExecutionStatus getTaskExecutionStatus() {

		return taskExecutionStatus;
	}



	/**
	 * @return the outputMessages
	 */
	public List<String> getOutputMessages() {

		return outputMessages;
	}



	/**
	 * @param taskExecutionStatus the taskExecutionStatus to set
	 */
	public void setTaskExecutionStatus(BaseTaskExecutionStatus taskExecutionStatus) {

		this.taskExecutionStatus = taskExecutionStatus;
	}



	/**
	 * @param outputMessages the outputMessages to set
	 */
	public void setOutputMessages(List<String> outputMessages) {

		this.outputMessages = outputMessages;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TaskOutput [taskExecutionStatus=");
		builder.append(taskExecutionStatus);
		builder.append(", outputMessages=");
		builder.append(outputMessages);
		builder.append("]");
		return builder.toString();
	}




}
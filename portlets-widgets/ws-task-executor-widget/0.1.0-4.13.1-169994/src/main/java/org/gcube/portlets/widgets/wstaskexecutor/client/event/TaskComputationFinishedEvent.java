/*
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskExecutionStatus;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class TaskComputationFinishedEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 7, 2018
 */
public class TaskComputationFinishedEvent extends GwtEvent<TaskComputationFinishedEventHandler> {

	/** The type. */
	public static Type<TaskComputationFinishedEventHandler> TYPE = new Type<TaskComputationFinishedEventHandler>();
	private TaskExecutionStatus taskExecutionStatus;
	private WSItem wsItem;
	private Throwable error;


	/**
	 * Instantiates a new perform run task event.
	 *
	 * @param wsItem the ws item
	 * @param taskExecutionStatus the task execution status
	 */
	public TaskComputationFinishedEvent(WSItem wsItem, TaskExecutionStatus taskExecutionStatus, Throwable exception) {
		this.wsItem = wsItem;
		this.taskExecutionStatus = taskExecutionStatus;
		this.error = exception;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<TaskComputationFinishedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(TaskComputationFinishedEventHandler handler) {
		handler.onTaskFinished(this);
	}



	/**
	 * Gets the task execution status.
	 *
	 * @return the taskExecutionStatus
	 */
	public TaskExecutionStatus getTaskExecutionStatus() {

		return taskExecutionStatus;
	}


	/**
	 * Gets the ws item.
	 *
	 * @return the ws item
	 */
	public WSItem getWsItem() {
		return wsItem;
	}


	/**
	 * @return the error
	 */
	public Throwable getError() {

		return error;
	}

}

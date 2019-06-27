/*
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ShowCreateTaskConfigurationDialogEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 15, 2018
 */
public class ShowCreateTaskConfigurationDialogEvent extends GwtEvent<ShowCreateTaskConfigurationDialogEventHandler> {

	/** The type. */
	public static Type<ShowCreateTaskConfigurationDialogEventHandler> TYPE = new Type<ShowCreateTaskConfigurationDialogEventHandler>();
	private WSItem wsItem;
	private Operation operation;
	private TaskConfiguration taskConfiguration;

	/**
	 * The Enum Operation.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * May 15, 2018
	 */
	public static enum Operation{CREATE_NEW, EDIT_EXISTING}


	/**
	 * Instantiates a new show create task configuration dialog event.
	 *
	 * @param wsItem the ws item
	 * @param taskConfiguration the task configuration
	 * @param op the op
	 */
	public ShowCreateTaskConfigurationDialogEvent(WSItem wsItem, TaskConfiguration taskConfiguration, Operation op) {
		this.wsItem = wsItem;
		this.taskConfiguration = taskConfiguration;
		this.operation = op;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowCreateTaskConfigurationDialogEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowCreateTaskConfigurationDialogEventHandler handler) {
		handler.onShowCreateConfiguration(this);
	}

	 /* Gets the folder.
	 *
	 * @return the folder
	 */
	/**
 	 * Gets the folder.
 	 *
 	 * @return the folder
 	 */
 	public WSItem getWsItem() {
		return wsItem;
	}


	/**
	 * Gets the operation.
	 *
	 * @return the operation
	 */
	public Operation getOperation() {

		return operation;
	}


	/**
	 * Gets the task configuration.
	 *
	 * @return the taskConfiguration
	 */
	public TaskConfiguration getTaskConfiguration() {

		return taskConfiguration;
	}

}

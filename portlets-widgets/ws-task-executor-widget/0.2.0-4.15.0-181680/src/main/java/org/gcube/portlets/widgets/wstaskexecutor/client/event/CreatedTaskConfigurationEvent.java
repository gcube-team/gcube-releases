/*
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class CreatedTaskConfigurationEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 15, 2018
 */
public class CreatedTaskConfigurationEvent extends GwtEvent<CreatedTaskConfigurationEventHandler> {

	/** The type. */
	public static Type<CreatedTaskConfigurationEventHandler> TYPE = new Type<CreatedTaskConfigurationEventHandler>();
	private TaskConfiguration conf;
	private WSItem wsItem;
	private boolean isUpdate;


	/**
	 * Instantiates a new creates the task configuration event.
	 *
	 * @param wsItem the ws item
	 * @param conf the conf
	 * @param isUpdate the is update
	 */
	public CreatedTaskConfigurationEvent(WSItem wsItem, TaskConfiguration conf, boolean isUpdate) {
		this.wsItem = wsItem;
		this.conf = conf;
		this.isUpdate = isUpdate;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<CreatedTaskConfigurationEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(CreatedTaskConfigurationEventHandler handler) {
		handler.onCreatedConfiguration(this);
	}


	/**
	 * Checks if is update.
	 *
	 * @return the isUpdate
	 */
	public boolean isUpdate() {

		return isUpdate;
	}


	/**
	 * Gets the conf.
	 *
	 * @return the conf
	 */
	public TaskConfiguration getConf() {
		return conf;

	}

	/**
	 * Gets the folder.
	 *
	 * @return the folder
	 */
	public WSItem getWsItem() {
		return wsItem;
	}

}

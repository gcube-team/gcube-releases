/*
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class PerformRunTaskEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public class PerformRunTaskEvent extends GwtEvent<PerformRunTaskEventHandler> {

	/** The type. */
	public static Type<PerformRunTaskEventHandler> TYPE = new Type<PerformRunTaskEventHandler>();
	private TaskConfiguration conf;
	private WSItem wsItem;


	/**
	 * Instantiates a new perform run task event.
	 *
	 * @param wsItem the ws item
	 * @param conf the conf
	 */
	public PerformRunTaskEvent(WSItem wsItem, TaskConfiguration conf) {
		this.wsItem = wsItem;
		this.conf = conf;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<PerformRunTaskEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(PerformRunTaskEventHandler handler) {
		handler.onPerformRunTask(this);
	}



	/**
	 * Gets the configuration.
	 *
	 * @return the configuration
	 */
	public TaskConfiguration getConfiguration() {
		return conf;

	}


	/**
	 * Gets the ws item.
	 *
	 * @return the ws item
	 */
	public WSItem getWsItem() {
		return wsItem;
	}

}

/*
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client.event;

import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ShowListOfTaskConfigurationsEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 16, 2018
 */
public class ShowListOfTaskConfigurationsEvent extends GwtEvent<ShowListOfTaskConfigurationsEventHandler> {

	/** The type. */
	public static Type<ShowListOfTaskConfigurationsEventHandler> TYPE = new Type<ShowListOfTaskConfigurationsEventHandler>();
	private WSItem wsItem;


	/**
	 * Instantiates a new show list of task configurations event.
	 *
	 * @param wsItem the ws item
	 */
	public ShowListOfTaskConfigurationsEvent(WSItem wsItem) {
		this.wsItem = wsItem;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowListOfTaskConfigurationsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowListOfTaskConfigurationsEventHandler handler) {
		handler.onShowListOfTaskConfigurations(this);
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

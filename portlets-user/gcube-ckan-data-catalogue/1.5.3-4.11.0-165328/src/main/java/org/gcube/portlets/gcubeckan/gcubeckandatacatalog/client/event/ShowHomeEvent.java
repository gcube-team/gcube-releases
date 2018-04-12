package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Raised when the user wants to go to the catalogue home
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShowHomeEvent extends GwtEvent<ShowHomeEventHandler> {
	public static Type<ShowHomeEventHandler> TYPE = new Type<ShowHomeEventHandler>();


	/**
	 * Instantiates a new insert metadata event.
	 */
	public ShowHomeEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowHomeEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowHomeEventHandler handler) {
		handler.onShowHome(this);
	}

}

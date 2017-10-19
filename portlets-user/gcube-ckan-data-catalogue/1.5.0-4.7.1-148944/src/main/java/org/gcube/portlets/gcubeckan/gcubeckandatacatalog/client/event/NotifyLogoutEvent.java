package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class NotifyLogoutEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2016
 */
public class NotifyLogoutEvent extends GwtEvent<NotifyLogoutEventHandler> {
	public static Type<NotifyLogoutEventHandler> TYPE = new Type<NotifyLogoutEventHandler>();


	/**
	 * Instantiates a new insert metadata event.
	 */
	public NotifyLogoutEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<NotifyLogoutEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(NotifyLogoutEventHandler handler) {
		handler.onLogout(this);
	}

}

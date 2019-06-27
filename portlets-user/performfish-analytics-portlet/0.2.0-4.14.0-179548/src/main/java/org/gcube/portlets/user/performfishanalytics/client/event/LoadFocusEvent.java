package org.gcube.portlets.user.performfishanalytics.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class LoadFocusEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 8, 2019
 */
public class LoadFocusEvent extends GwtEvent<LoadFocusEventHandler> {
	public static Type<LoadFocusEventHandler> TYPE = new Type<LoadFocusEventHandler>();
	/**
	 * Instantiates a new load batches event.
	 */
	public LoadFocusEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadFocusEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadFocusEventHandler handler) {
		handler.onLoadFocusEvent(this);
	}
}

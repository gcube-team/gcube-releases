/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class AbortCurrentSearchEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 16, 2017
 */
public class StopCurrentSearchEvent extends GwtEvent<StopCurrentSearchEventHandler> {

	public static final GwtEvent.Type<StopCurrentSearchEventHandler> TYPE = new Type<StopCurrentSearchEventHandler>();

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<StopCurrentSearchEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(StopCurrentSearchEventHandler handler) {
		handler.onAbortCurrentSearch(this);
	}
}

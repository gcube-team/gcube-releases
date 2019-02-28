package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class NotifyLogoutEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2016
 */
public class IFrameInstanciedEvent extends GwtEvent<IFrameInstanciedEventHandler> {
	public static Type<IFrameInstanciedEventHandler> TYPE = new Type<IFrameInstanciedEventHandler>();


	/**
	 * Instantiates a new insert metadata event.
	 */
	public IFrameInstanciedEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<IFrameInstanciedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(IFrameInstanciedEventHandler handler) {
		handler.onNewInstance(this);
	}

}

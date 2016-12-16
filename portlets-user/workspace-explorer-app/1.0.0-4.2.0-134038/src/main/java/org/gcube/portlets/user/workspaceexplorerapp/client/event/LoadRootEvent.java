package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class LoadRootEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 */
public class LoadRootEvent extends GwtEvent<LoadRootEventHandler> {
	
	public static Type<LoadRootEventHandler> TYPE = new Type<LoadRootEventHandler>();

	public LoadRootEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadRootEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadRootEventHandler handler) {
		handler.onLoadRoot(this);
	}
}

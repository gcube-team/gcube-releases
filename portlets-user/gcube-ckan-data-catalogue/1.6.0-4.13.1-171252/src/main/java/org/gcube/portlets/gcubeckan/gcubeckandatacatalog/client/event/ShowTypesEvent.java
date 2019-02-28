package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Show types page
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShowTypesEvent extends GwtEvent<ShowTypesEventHandler>{

public static Type<ShowTypesEventHandler> TYPE = new Type<ShowTypesEventHandler>();
	
	private boolean ownOnly;
	
	/**
	 * Instantiates a new show user datasets event.
	 */
	public ShowTypesEvent(boolean ownOnly) {
		
		this.ownOnly = ownOnly;
		
	}
	
	public boolean isOwnOnly() {
		return ownOnly;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowTypesEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowTypesEventHandler handler) {
		handler.onShowTypes(this);
	}
	
}

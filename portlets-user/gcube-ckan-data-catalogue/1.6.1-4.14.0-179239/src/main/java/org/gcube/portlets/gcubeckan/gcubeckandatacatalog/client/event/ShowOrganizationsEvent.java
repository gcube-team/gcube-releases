package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Raised when the user wants to see his organizations.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShowOrganizationsEvent extends GwtEvent<ShowOrganizationsEventHandler>{

	public static Type<ShowOrganizationsEventHandler> TYPE = new Type<ShowOrganizationsEventHandler>();
	private boolean ownOnly;
	
	/**
	 * Instantiates a new show user organizations event.
	 */
	public ShowOrganizationsEvent(boolean ownOnly) {
		this.ownOnly = ownOnly;
	}

	public boolean isOwnOnly() {
		return ownOnly;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowOrganizationsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowOrganizationsEventHandler handler) {
		handler.onShowOrganizations(this);
	}
	
}

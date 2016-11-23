package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Raised when the user wants to see his organizations.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShowUserOrganizationsEvent extends GwtEvent<ShowUserOrganizationsEventHandler>{

	public static Type<ShowUserOrganizationsEventHandler> TYPE = new Type<ShowUserOrganizationsEventHandler>();
	
	/**
	 * Instantiates a new show user organizations event.
	 */
	public ShowUserOrganizationsEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowUserOrganizationsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowUserOrganizationsEventHandler handler) {
		handler.onShowOrganizations(this);
	}
	
}

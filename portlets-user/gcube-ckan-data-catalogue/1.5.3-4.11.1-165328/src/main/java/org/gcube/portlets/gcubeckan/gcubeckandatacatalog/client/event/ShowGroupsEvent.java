package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ShowUserGroupsEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2016
 */
public class ShowGroupsEvent extends GwtEvent<ShowGroupsEventHandler>{

	public static Type<ShowGroupsEventHandler> TYPE = new Type<ShowGroupsEventHandler>();
	
	private boolean ownOnly;

	/**
	 * Instantiates a new show user organizations event.
	 */
	public ShowGroupsEvent(boolean ownOnly) {
		this.ownOnly = ownOnly;
	}
	
	public boolean isOwnOnly() {
		return ownOnly;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowGroupsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowGroupsEventHandler handler) {
		handler.onShowGroups(this);
	}

}

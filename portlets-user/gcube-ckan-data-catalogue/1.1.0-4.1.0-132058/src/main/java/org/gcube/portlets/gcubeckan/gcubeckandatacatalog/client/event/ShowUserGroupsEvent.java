package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ShowUserGroupsEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2016
 */
public class ShowUserGroupsEvent extends GwtEvent<ShowUserGroupsEventHandler>{

	public static Type<ShowUserGroupsEventHandler> TYPE = new Type<ShowUserGroupsEventHandler>();

	/**
	 * Instantiates a new show user organizations event.
	 */
	public ShowUserGroupsEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowUserGroupsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowUserGroupsEventHandler handler) {
		handler.onShowGroups(this);
	}

}

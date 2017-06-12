package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class ManageReleasesEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ManageReleasesEvent extends GwtEvent<ManageReleasesEventHandler> {
	public static Type<ManageReleasesEventHandler> TYPE = new Type<ManageReleasesEventHandler>();

	/**
	 * Instantiates a new manage releases event.
	 */
	public ManageReleasesEvent() {

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ManageReleasesEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ManageReleasesEventHandler handler) {
		handler.onManageReleases(this);
	}

}

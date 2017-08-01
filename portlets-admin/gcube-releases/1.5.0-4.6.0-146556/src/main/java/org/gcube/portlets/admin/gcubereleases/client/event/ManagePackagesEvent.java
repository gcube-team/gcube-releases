package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class ManagePackagesEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ManagePackagesEvent extends GwtEvent<ManagePackagesEventHandler> {
	public static Type<ManagePackagesEventHandler> TYPE = new Type<ManagePackagesEventHandler>();

	/**
	 * Instantiates a new manage packages event.
	 */
	public ManagePackagesEvent() {

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ManagePackagesEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ManagePackagesEventHandler handler) {
		handler.onManagePackages(this);
	}

}

package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class NewInsertReleasesEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class NewInsertReleasesEvent extends GwtEvent<NewInsertReleaseEventHandler> {
	public static Type<NewInsertReleaseEventHandler> TYPE = new Type<NewInsertReleaseEventHandler>();

	/**
	 * Instantiates a new new insert releases event.
	 */
	public NewInsertReleasesEvent() {

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<NewInsertReleaseEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(NewInsertReleaseEventHandler handler) {
		handler.onNewInsertRelease(this);
	}

}

package org.gcube.portlets.admin.gcubereleases.client.event;

import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class DisplaySelectedReleaseEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class DisplaySelectedReleaseEvent extends GwtEvent<DisplaySelectedReleaseEventHandler> {
	public static Type<DisplaySelectedReleaseEventHandler> TYPE = new Type<DisplaySelectedReleaseEventHandler>();
	private Release release;


	/**
	 * Instantiates a new display selected release event.
	 *
	 * @param release the release
	 */
	public DisplaySelectedReleaseEvent(Release release) {
		this.release = release;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<DisplaySelectedReleaseEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(DisplaySelectedReleaseEventHandler handler) {
		handler.onSelectRelease(this);
	}

	/**
	 * Gets the release.
	 *
	 * @return the release
	 */
	public Release getRelease() {
		return release;
	}

	/**
	 * Sets the release.
	 *
	 * @param release the new release
	 */
	public void setRelease(Release release) {
		this.release = release;
	}
	
}

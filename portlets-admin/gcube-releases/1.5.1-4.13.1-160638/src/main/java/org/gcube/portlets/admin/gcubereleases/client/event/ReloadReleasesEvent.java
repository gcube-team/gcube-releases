package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class ReloadReleasesEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ReloadReleasesEvent extends GwtEvent<ReloadReleasesEventHandler> {
	public static Type<ReloadReleasesEventHandler> TYPE = new Type<ReloadReleasesEventHandler>();
	private boolean displayFirst;


	/**
	 * Instantiates a new reload releases event.
	 *
	 * @param displayFirst the display first
	 */
	public ReloadReleasesEvent(boolean displayFirst) {
		this.displayFirst = displayFirst;
		
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ReloadReleasesEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ReloadReleasesEventHandler handler) {
		handler.onReleadReleases(this);
	}

	/**
	 * Checks if is display first.
	 *
	 * @return true, if is display first
	 */
	public boolean isDisplayFirst() {
		return displayFirst;
	}

	/**
	 * Sets the display first.
	 *
	 * @param displayFirst the new display first
	 */
	public void setDisplayFirst(boolean displayFirst) {
		this.displayFirst = displayFirst;
	}

}

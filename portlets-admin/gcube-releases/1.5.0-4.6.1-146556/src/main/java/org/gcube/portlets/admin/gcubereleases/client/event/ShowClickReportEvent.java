package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class ReloadReleasesEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ShowClickReportEvent extends GwtEvent<ShowClickReportEventHandler> {
	public static Type<ShowClickReportEventHandler> TYPE = new Type<ShowClickReportEventHandler>();
	
	public ShowClickReportEvent() {

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowClickReportEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowClickReportEventHandler handler) {
		handler.onShowClickReport(this);
	}
}

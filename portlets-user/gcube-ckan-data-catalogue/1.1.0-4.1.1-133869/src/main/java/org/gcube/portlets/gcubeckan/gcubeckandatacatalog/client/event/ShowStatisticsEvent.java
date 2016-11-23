package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Show catalog statistics event
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShowStatisticsEvent extends GwtEvent<ShowStatisticsEventHandler> {

	public static Type<ShowStatisticsEventHandler> TYPE = new Type<ShowStatisticsEventHandler>();

	/**
	 * Instantiates a new show statistics event.
	 */
	public ShowStatisticsEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowStatisticsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowStatisticsEventHandler handler) {
		handler.onShowStatistics(this);
	}

}

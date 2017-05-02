package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event handler for show statistics event.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface ShowStatisticsEventHandler extends EventHandler {

	/**
	 * Show statistics
	 * @param showStatisticsEvent
	 */
	void onShowStatistics(ShowStatisticsEvent showStatisticsEvent);

}

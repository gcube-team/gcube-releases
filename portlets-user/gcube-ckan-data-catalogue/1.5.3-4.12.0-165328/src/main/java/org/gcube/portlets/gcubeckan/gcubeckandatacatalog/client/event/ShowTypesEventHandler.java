package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Show types interface event
 * @author Costantino Perciante at ISTI-CNR  (costantino.perciante@isti.cnr.it)
 */
public interface ShowTypesEventHandler extends EventHandler {

	/**
	 * Show statistics
	 * @param showStatisticsEvent
	 */
	void onShowTypes(ShowTypesEvent showTypes);
	
}

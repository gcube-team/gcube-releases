package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event handler for the ShowUserDatasetsEvent
 * @author Costantino Perciante at ISTI-CNR  (costantino.perciante@isti.cnr.it)
 */
public interface ShowDatasetsEventHandler extends EventHandler {

	/**
	 * 
	 * @param showUserDatasetsEvent
	 */
	void onShowDatasets(ShowDatasetsEvent showUserDatasetsEvent);

}

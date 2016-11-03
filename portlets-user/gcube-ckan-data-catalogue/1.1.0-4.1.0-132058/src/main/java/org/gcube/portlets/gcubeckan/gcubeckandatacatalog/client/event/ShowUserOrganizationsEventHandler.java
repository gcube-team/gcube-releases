package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event handler for the ShowUserOrganizationsEvent
 * @author Costantino Perciante at ISTI-CNR  (costantino.perciante@isti.cnr.it)
 */
public interface ShowUserOrganizationsEventHandler extends EventHandler {
	
	/**
	 * 
	 * @param showUserDatasetsEvent
	 */
	void onShowOrganizations(ShowUserOrganizationsEvent showUserDatasetsEvent);

}

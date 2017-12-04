package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The event handler interface for the show home event.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface ShowHomeEventHandler extends EventHandler {

	/**
	 * 
	 * @param showHomeEvent
	 */
	void onShowHome(ShowHomeEvent showHomeEvent);

}

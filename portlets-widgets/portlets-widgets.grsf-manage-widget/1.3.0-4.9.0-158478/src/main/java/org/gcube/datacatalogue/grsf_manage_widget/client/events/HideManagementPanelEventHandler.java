package org.gcube.datacatalogue.grsf_manage_widget.client.events;

import com.google.gwt.event.shared.EventHandler;


/**
 * Hide management panel event handler.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface HideManagementPanelEventHandler extends EventHandler {

	/**
	 * @param onEvent
	 */
	void onEvent(HideManagementPanelEvent hideEvent);

}
/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.occurences;

import com.google.gwt.event.shared.HandlerManager;


public class EventBusProvider {

	private static HandlerManager eventBus=null;
	
	public static HandlerManager getInstance() {
		if (eventBus==null)
			eventBus = new HandlerManager(null);
		return eventBus;
	}

}

package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface NavigationPanelStatusChangeEventHandler extends EventHandler {

	public void onSelectedResourceType(NavigationPanelStatusChangeEvent event);
	
}

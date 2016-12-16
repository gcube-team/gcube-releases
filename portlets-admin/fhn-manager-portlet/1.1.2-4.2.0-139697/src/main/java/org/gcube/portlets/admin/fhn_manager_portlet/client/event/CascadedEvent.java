package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import com.google.gwt.event.shared.GwtEvent;

public interface CascadedEvent {

	public GwtEvent getCascade();
	public void setCascade(GwtEvent theEvent);
	
}

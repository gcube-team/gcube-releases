package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

public interface CascadedEvent {

	public FutureEvent getCascade();
	public void setCascade(FutureEvent theEvent);
	
}

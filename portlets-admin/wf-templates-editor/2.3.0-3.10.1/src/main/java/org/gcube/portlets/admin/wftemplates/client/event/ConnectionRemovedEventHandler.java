package org.gcube.portlets.admin.wftemplates.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ConnectionRemovedEventHandler extends EventHandler {
	  void onRemovedConnection(ConnectionRemovedEvent connectionRemovedEvent);  
}

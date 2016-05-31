package org.gcube.portlets.user.workflowdocuments.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ForwardEventHandler  extends EventHandler {
	  void onHasForwarded(ForwardEvent event);
}

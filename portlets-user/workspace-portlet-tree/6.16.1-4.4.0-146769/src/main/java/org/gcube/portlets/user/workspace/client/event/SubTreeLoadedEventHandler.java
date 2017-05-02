package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SubTreeLoadedEventHandler extends EventHandler {
	void onSubTreeLoaded(SubTreeLoadedEvent event);
}
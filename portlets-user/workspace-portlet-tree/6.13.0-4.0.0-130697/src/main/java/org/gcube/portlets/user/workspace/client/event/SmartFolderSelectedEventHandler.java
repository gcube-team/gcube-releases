package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SmartFolderSelectedEventHandler extends EventHandler {
	void onSmartFolderSelected(SmartFolderSelectedEvent smartFolderSelectedEvent);
}
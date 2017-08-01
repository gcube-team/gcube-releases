package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ExpandFolderEventHandler extends EventHandler {
	void onExpandFolder(ExpandFolderEvent expandFolderEvent);
}
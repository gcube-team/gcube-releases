package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AddFolderEventHandler extends EventHandler {
	void onAddItem(AddFolderEvent event);
}
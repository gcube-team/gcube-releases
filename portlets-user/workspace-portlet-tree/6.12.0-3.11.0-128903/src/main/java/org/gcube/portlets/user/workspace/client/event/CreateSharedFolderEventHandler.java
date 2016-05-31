package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CreateSharedFolderEventHandler extends EventHandler {
	void onCreateSharedFolder(CreateSharedFolderEvent createSharedFolderEvent);
}
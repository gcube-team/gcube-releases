package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface RefreshItemEventHandler extends EventHandler {
	void onRefreshItem(RefreshFolderEvent refreshtemEvent);
}
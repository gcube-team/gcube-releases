package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DeleteSmartFolderEventHandler extends EventHandler {
	void onDeleteItem(DeleteSmartFolderEvent deleteSmartFolderEvent);
}
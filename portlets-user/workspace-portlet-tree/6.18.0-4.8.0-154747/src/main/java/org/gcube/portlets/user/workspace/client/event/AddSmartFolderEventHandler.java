package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AddSmartFolderEventHandler extends EventHandler {
	void onSaveSmartFolder(AddSmartFolderEvent saveSmartFolderEvent);
}
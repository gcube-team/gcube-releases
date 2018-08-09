package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface FileUploadEventHandler extends EventHandler {
	void onFileUploadEvent(FileUploadEvent fileUploadEvent);
}
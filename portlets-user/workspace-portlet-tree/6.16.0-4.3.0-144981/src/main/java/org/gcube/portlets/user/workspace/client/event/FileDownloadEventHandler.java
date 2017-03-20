package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface FileDownloadEventHandler extends EventHandler {
	void onFileDownloadEvent(FileDownloadEvent fileDownloadEvent);
}
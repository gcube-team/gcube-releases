package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface FileDownloadEventHandler extends EventHandler {
	void onFileDownloadEvent(FileDownloadEvent fileDownloadEvent);
}
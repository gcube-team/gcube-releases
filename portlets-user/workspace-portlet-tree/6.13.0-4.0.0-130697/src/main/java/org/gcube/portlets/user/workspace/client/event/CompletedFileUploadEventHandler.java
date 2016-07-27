package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CompletedFileUploadEventHandler extends EventHandler {
	void onCompletedFileUploadEvent(CompletedFileUploadEvent completedFileUploadEvent);
}
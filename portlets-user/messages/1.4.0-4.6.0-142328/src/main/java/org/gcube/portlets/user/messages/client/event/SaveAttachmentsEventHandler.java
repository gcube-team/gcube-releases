package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SaveAttachmentsEventHandler extends EventHandler {
	void onSaveAttachments(SaveAttachmentsEvent saveAttachmentsEvent);
}
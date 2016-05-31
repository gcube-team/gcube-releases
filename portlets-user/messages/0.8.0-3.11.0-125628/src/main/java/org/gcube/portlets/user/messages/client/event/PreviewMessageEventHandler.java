package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface PreviewMessageEventHandler extends EventHandler {
	void onPreviewMessage(PreviewMessageEvent previewMessageEvent);
}
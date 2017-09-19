package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SendMessageEventHandler extends EventHandler {
	void onSendMessage(SendMessageEvent sendMessageEvent);
}
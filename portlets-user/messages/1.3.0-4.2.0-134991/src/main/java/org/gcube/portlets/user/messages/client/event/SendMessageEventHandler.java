package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SendMessageEventHandler extends EventHandler {
	void onSendMessage(SendMessageEvent sendMessageEvent);
}
package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface LoadMessagesEventHandler extends EventHandler {
	void onLoadMessages(LoadMessagesEvent getMessagesEvent);
}
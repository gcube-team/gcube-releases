package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface GetAllNewMessagesEventHandler extends EventHandler {
	void onNewMessagesEvent(GetAllNewMessagesEvent newMessagesEvent);
}
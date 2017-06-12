package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DeleteMessageEventHandler extends EventHandler {
	void onDeleteMessage(DeleteMessageEvent deleteMessageEvent);
}
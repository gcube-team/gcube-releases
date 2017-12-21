package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DeleteMessageEventHandler extends EventHandler {
	void onDeleteMessage(DeleteMessageEvent deleteMessageEvent);
}
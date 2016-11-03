package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface OpenMessageEventHandler extends EventHandler {
	void onOpenMessage(OpenMessageEvent openMessageEvent);
}
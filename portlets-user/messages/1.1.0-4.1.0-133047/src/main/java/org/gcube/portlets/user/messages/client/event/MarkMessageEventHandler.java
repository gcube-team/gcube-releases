package org.gcube.portlets.user.messages.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface MarkMessageEventHandler extends EventHandler {
	void onMark(MarkMessageEvent markAsReadMessageEvent);
}
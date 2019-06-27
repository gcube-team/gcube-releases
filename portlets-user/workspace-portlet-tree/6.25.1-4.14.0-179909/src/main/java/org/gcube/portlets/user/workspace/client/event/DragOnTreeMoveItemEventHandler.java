package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DragOnTreeMoveItemEventHandler extends EventHandler {
	void onMoveItem(DragOnTreeMoveItemEvent event);
}
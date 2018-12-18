package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DeleteBulkEventHandler extends EventHandler {
	void onDeleteBulk(DeleteBulkEvent deleteBulkEvent);
}
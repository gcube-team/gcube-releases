package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SelectedItemEventHandler extends EventHandler {
	void onSelectedItem(SelectedItemEvent selectedItemEvent);
}
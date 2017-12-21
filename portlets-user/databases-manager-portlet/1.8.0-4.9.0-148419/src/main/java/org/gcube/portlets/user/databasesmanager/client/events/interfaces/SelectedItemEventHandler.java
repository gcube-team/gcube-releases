package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.SelectedItemEvent;
import com.google.gwt.event.shared.EventHandler;

// Handler for "SelectedItemEvent" event
public interface SelectedItemEventHandler extends EventHandler {
	public void onSelectedItem(SelectedItemEvent selectedItemEvent);
}

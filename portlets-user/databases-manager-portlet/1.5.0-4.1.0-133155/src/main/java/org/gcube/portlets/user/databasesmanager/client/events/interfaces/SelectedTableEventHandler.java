package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.SelectedTableEvent;
import com.google.gwt.event.shared.EventHandler;

public interface SelectedTableEventHandler extends EventHandler {
	public void onSelectedTable(SelectedTableEvent selectedTableEvent);
}

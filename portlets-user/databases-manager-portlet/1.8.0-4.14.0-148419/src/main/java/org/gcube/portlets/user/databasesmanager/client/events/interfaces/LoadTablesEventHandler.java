package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.LoadTablesEvent;
import com.google.gwt.event.shared.EventHandler;

public interface LoadTablesEventHandler extends EventHandler {
	public void onLoadTables(LoadTablesEvent loadTablesEvent);
}

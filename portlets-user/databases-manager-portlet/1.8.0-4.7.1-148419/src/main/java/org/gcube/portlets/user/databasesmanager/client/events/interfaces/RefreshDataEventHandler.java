package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.RefreshDataEvent;

import com.google.gwt.event.shared.EventHandler;

public interface RefreshDataEventHandler extends EventHandler {
	public void onRefreshData(RefreshDataEvent refreshDataEvent);
}

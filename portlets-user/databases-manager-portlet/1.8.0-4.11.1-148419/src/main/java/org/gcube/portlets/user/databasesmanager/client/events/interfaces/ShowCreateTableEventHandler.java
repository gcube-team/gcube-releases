package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.ShowCreateTableEvent;
import com.google.gwt.event.shared.EventHandler;

public interface ShowCreateTableEventHandler extends EventHandler {
	public void onShowCreateTable(ShowCreateTableEvent showCreateTableEvent);

}

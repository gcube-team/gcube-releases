package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.SubmitQueryEvent;
import com.google.gwt.event.shared.EventHandler;

//Handler for "SubmitQueryEvent" event
public interface SubmitQueryEventHandler extends EventHandler {
	public void onSubmitQuery(SubmitQueryEvent submitQueryEvent);
}

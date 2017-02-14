package org.gcube.portlets.user.databasesmanager.client.events.interfaces;

import org.gcube.portlets.user.databasesmanager.client.events.CancelQueryExecutionEvent;
import com.google.gwt.event.shared.EventHandler;

public interface CancelQueryExecutionEventHandler extends EventHandler{
	public void onCancelQueryExecution(CancelQueryExecutionEvent cancelQueryExecutionEvent);
}

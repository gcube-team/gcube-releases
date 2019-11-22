package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.events.interfaces.CancelQueryExecutionEventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CancelQueryExecutionEvent extends
		GwtEvent<CancelQueryExecutionEventHandler> {

	public static Type<CancelQueryExecutionEventHandler> TYPE = new Type<CancelQueryExecutionEventHandler>();

	@Override
	public Type<CancelQueryExecutionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CancelQueryExecutionEventHandler handler) {
		handler.onCancelQueryExecution(this);
	}
	
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.CANCEL_EXECUTION_QUERY;
	}

}

package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.events.interfaces.LoadTablesEventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class LoadTablesEvent extends GwtEvent<LoadTablesEventHandler> {

	public static Type<LoadTablesEventHandler> TYPE = new Type<LoadTablesEventHandler>();

	@Override
	protected void dispatch(LoadTablesEventHandler handler) {
		handler.onLoadTables(this);
	}

	@Override
	public Type<LoadTablesEventHandler> getAssociatedType() {
		return TYPE;
	}

	public EventsTypeEnum getKey() {
		return EventsTypeEnum.LOAD_TABLES_EVENT;
	}
}

package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.events.interfaces.ShowCreateTableEventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ShowCreateTableEvent extends GwtEvent<ShowCreateTableEventHandler> {

	public static Type<ShowCreateTableEventHandler> TYPE = new Type<ShowCreateTableEventHandler>();

	@Override
	protected void dispatch(ShowCreateTableEventHandler handler) {
		handler.onShowCreateTable(this);
	}

	@Override
	public Type<ShowCreateTableEventHandler> getAssociatedType() {
		return TYPE;
	}

	public EventsTypeEnum getKey() {
		return EventsTypeEnum.SHOW_CREATE_TABLE_EVENT;
	}
}

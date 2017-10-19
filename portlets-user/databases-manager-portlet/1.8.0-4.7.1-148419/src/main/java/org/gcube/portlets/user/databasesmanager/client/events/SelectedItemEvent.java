package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.events.interfaces.SelectedItemEventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class SelectedItemEvent extends GwtEvent<SelectedItemEventHandler> {

	public static Type<SelectedItemEventHandler> TYPE = new Type<SelectedItemEventHandler>();

	@Override
	public Type<SelectedItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectedItemEventHandler handler) {
		handler.onSelectedItem(this);
	}

	public EventsTypeEnum getKey() {
		return EventsTypeEnum.SELECTED_ITEM_EVENT;
	}
}

package org.gcube.portlets.user.databasesmanager.client.events;

import org.gcube.portlets.user.databasesmanager.client.events.interfaces.RefreshDataEventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class RefreshDataEvent extends GwtEvent<RefreshDataEventHandler> {

	public static Type<RefreshDataEventHandler> TYPE = new Type<RefreshDataEventHandler>();
	
	@Override
	public Type<RefreshDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RefreshDataEventHandler handler) {
		handler.onRefreshData(this);
	}
	
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.REFRESH_DATA;
	}

}

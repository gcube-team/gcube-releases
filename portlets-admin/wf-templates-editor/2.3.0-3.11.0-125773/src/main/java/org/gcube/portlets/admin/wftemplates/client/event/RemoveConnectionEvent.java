package org.gcube.portlets.admin.wftemplates.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.orange.links.client.connection.Connection;

public class RemoveConnectionEvent extends GwtEvent<RemoveConnectionEventHandler> {
	public static Type<RemoveConnectionEventHandler> TYPE = new Type<RemoveConnectionEventHandler>();
	private final Connection selected;
	
	public RemoveConnectionEvent(Connection selected) {
		this.selected = selected;
	}
	
	public Connection getSelected() {
		return selected;
	}

	@Override
	protected void dispatch(RemoveConnectionEventHandler handler) {
		handler.onConnectionRemoval(this);
	}

	@Override
	public Type<RemoveConnectionEventHandler> getAssociatedType() {
		return TYPE;
	}
}

package org.gcube.portlets.admin.wftemplates.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.orange.links.client.connection.Connection;

public class ConnectionRemovedEvent extends GwtEvent<ConnectionRemovedEventHandler> {
	public static Type<ConnectionRemovedEventHandler> TYPE = new Type<ConnectionRemovedEventHandler>();
	private final Connection selected;
	
	public ConnectionRemovedEvent(Connection selected) {
		this.selected = selected;
	}
	
	public Connection getSelected() {
		return selected;
	}

	@Override
	protected void dispatch(ConnectionRemovedEventHandler handler) {
		handler.onRemovedConnection(this);
	}

	@Override
	public Type<ConnectionRemovedEventHandler> getAssociatedType() {
		return TYPE;
	}
}

package org.gcube.datacatalogue.grsf_manage_widget.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class EnableConfirmButtonEvent extends GwtEvent<EnableConfirmButtonEventHandler> {
	public static Type<EnableConfirmButtonEventHandler> TYPE = new Type<EnableConfirmButtonEventHandler>();
	
	public EnableConfirmButtonEvent() {
	}

	@Override
	public Type<EnableConfirmButtonEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EnableConfirmButtonEventHandler handler) {
		handler.onEvent(this);
	}

}

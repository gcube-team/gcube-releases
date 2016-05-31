package org.gcube.portlets.admin.vredefinition.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NextButtonEvent extends GwtEvent<NextButtonEventHandler> {
	public static Type<NextButtonEventHandler> TYPE = new Type<NextButtonEventHandler>();

	@Override
	protected void dispatch(NextButtonEventHandler handler) {
		handler.onNextButton(this);
		
	}

	@Override
	public Type<NextButtonEventHandler> getAssociatedType() {
		return TYPE;
	}

}

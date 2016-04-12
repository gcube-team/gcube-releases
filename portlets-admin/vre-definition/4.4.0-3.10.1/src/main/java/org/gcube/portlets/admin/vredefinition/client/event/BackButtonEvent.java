package org.gcube.portlets.admin.vredefinition.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class BackButtonEvent extends GwtEvent<BackButtonEventHandler> {
	public static GwtEvent.Type<BackButtonEventHandler> TYPE = new Type<BackButtonEventHandler>();

	@Override
	protected void dispatch(BackButtonEventHandler handler) {
		// TODO Auto-generated method stub
		System.out.println("Dispatch event");
		handler.onBackButton(this);
	}

	@Override
	public Type<BackButtonEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

}

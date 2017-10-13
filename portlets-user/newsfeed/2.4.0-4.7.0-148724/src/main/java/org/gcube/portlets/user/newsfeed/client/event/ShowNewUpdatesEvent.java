package org.gcube.portlets.user.newsfeed.client.event;

import com.google.gwt.event.shared.GwtEvent;



public class ShowNewUpdatesEvent  extends GwtEvent<ShowNewUpdatesEventHandler> {
	public static Type<ShowNewUpdatesEventHandler> TYPE = new Type<ShowNewUpdatesEventHandler>();
	
	public ShowNewUpdatesEvent() {	}

	@Override
	public Type<ShowNewUpdatesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowNewUpdatesEventHandler handler) {
		handler.onShowNewUpdatesClick(this);
	}
}

package org.gcube.portlets.user.newsfeed.client.event;

import com.google.gwt.event.shared.GwtEvent;



public class ShowMoreUpdatesEvent  extends GwtEvent<ShowMoreUpdatesEventHandler> {
	public static Type<ShowMoreUpdatesEventHandler> TYPE = new Type<ShowMoreUpdatesEventHandler>();
	
	public ShowMoreUpdatesEvent() {	}

	@Override
	public Type<ShowMoreUpdatesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowMoreUpdatesEventHandler handler) {
		handler.onShowMoreUpdatesClick(this);
	}
}

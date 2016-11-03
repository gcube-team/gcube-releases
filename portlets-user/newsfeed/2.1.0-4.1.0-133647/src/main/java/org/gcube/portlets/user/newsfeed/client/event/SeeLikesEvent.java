package org.gcube.portlets.user.newsfeed.client.event;

import com.google.gwt.event.shared.GwtEvent;



public class SeeLikesEvent extends GwtEvent<SeeLikesEventHandler> {
	public static Type<SeeLikesEventHandler> TYPE = new Type<SeeLikesEventHandler>();
	private final String feedid;
	
	public SeeLikesEvent(String feedid) {
		this.feedid = feedid;
	}

	public String getFeedId() { 
		return feedid;
	}
	
	@Override
	public Type<SeeLikesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SeeLikesEventHandler handler) {
		handler.onSeeLikes(this);
	}
}

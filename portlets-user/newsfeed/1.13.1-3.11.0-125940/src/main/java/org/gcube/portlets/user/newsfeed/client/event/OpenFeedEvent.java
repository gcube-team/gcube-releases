package org.gcube.portlets.user.newsfeed.client.event;

import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;



public class OpenFeedEvent  extends GwtEvent<OpenFeedEventHandler> {
	public static Type<OpenFeedEventHandler> TYPE = new Type<OpenFeedEventHandler>();
	
	private TweetTemplate toShow;
	
	
	public TweetTemplate getToShow() {
		return toShow;
	}

	public OpenFeedEvent(TweetTemplate toShow) {
		this.toShow = toShow;
	}

	@Override
	public Type<OpenFeedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OpenFeedEventHandler handler) {
		handler.onOpenFeed(this);
	}
}

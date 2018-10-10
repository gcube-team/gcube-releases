package org.gcube.portlets.user.newsfeed.client.event;

import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;



public class OpenPostEvent  extends GwtEvent<OpenPostEventHandler> {
	public static Type<OpenPostEventHandler> TYPE = new Type<OpenPostEventHandler>();
	
	private TweetTemplate toShow;
	
	
	public TweetTemplate getToShow() {
		return toShow;
	}

	public OpenPostEvent(TweetTemplate toShow) {
		this.toShow = toShow;
	}

	@Override
	public Type<OpenPostEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OpenPostEventHandler handler) {
		handler.onOpenPost(this);
	}
}

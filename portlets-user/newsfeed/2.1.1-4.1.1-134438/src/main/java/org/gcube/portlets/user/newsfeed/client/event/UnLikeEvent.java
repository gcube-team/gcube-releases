package org.gcube.portlets.user.newsfeed.client.event;

import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;



public class UnLikeEvent  extends GwtEvent<UnLikeEventHandler> {
	public static Type<UnLikeEventHandler> TYPE = new Type<UnLikeEventHandler>();
	private TweetTemplate owner;
	private final String feedid;


	
	public UnLikeEvent(TweetTemplate owner, String feedid) {
		this.feedid = feedid;
		this.owner = owner;
	}

	public String getFeedId() { 
		return feedid;
	}
	public TweetTemplate getOwner() {
		return owner;
	}
	@Override
	public Type<UnLikeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UnLikeEventHandler handler) {
		handler.onUnLike(this);
	}
}

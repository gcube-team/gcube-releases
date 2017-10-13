package org.gcube.portlets.user.newsfeed.client.event;

import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;



public class AddLikeEvent  extends GwtEvent<AddLikeEventHandler> {
	public static Type<AddLikeEventHandler> TYPE = new Type<AddLikeEventHandler>();
	private TweetTemplate owner;
	private final String feedid;


	
	public AddLikeEvent(TweetTemplate owner, String feedid) {
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
	public Type<AddLikeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddLikeEventHandler handler) {
		handler.onAddLike(this);
	}
}

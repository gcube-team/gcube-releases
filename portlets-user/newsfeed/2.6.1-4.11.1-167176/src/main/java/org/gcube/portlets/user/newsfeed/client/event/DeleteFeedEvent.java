package org.gcube.portlets.user.newsfeed.client.event;

import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;



public class DeleteFeedEvent  extends GwtEvent<DeleteFeedEventHandler> {
	public static Type<DeleteFeedEventHandler> TYPE = new Type<DeleteFeedEventHandler>();
	
	private TweetTemplate toDelete;
	
	
	public TweetTemplate getToDelete() {
		return toDelete;
	}

	public DeleteFeedEvent(TweetTemplate toDelete) {
		this.toDelete = toDelete;
	}

	@Override
	public Type<DeleteFeedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteFeedEventHandler handler) {
		handler.onDeleteFeed(this);
	}
}

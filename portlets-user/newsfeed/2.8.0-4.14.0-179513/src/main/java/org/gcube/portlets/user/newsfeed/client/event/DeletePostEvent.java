package org.gcube.portlets.user.newsfeed.client.event;

import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;



public class DeletePostEvent  extends GwtEvent<DeletePostEventHandler> {
	public static Type<DeletePostEventHandler> TYPE = new Type<DeletePostEventHandler>();
	
	private TweetTemplate toDelete;
	
	
	public TweetTemplate getToDelete() {
		return toDelete;
	}

	public DeletePostEvent(TweetTemplate toDelete) {
		this.toDelete = toDelete;
	}

	@Override
	public Type<DeletePostEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeletePostEventHandler handler) {
		handler.onDeletePost(this);
	}
}

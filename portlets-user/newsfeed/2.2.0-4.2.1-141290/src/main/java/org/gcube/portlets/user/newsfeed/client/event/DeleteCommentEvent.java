package org.gcube.portlets.user.newsfeed.client.event;

import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;



public class DeleteCommentEvent  extends GwtEvent<DeleteCommentEventHandler> {
	public static Type<DeleteCommentEventHandler> TYPE = new Type<DeleteCommentEventHandler>();
	
	private TweetTemplate owner;
	private String commentid;
	
	public TweetTemplate getOwner() {
		return owner;
	}
	public String getCommentId() {
		return commentid;
	}
	public DeleteCommentEvent(TweetTemplate owner, String commentid) {
		this.owner = owner;
		this.commentid = commentid;
	}

	@Override
	public Type<DeleteCommentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteCommentEventHandler handler) {
		handler.onDeleteComment(this);
	}
}

package org.gcube.portlets.user.newsfeed.client.event;

import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;



public class SeeCommentsEvent  extends GwtEvent<SeeCommentsEventHandler> {
	public static Type<SeeCommentsEventHandler> TYPE = new Type<SeeCommentsEventHandler>();
	
	private TweetTemplate owner;
	
	private boolean commentForm2Add;
	
	public TweetTemplate getOwner() {
		return owner;
	}
	
	public boolean isCommentForm2Add() {
		return commentForm2Add;
	}
	public SeeCommentsEvent(TweetTemplate owner, boolean commentForm2Add) {
		this.owner = owner;
		this.commentForm2Add = commentForm2Add;
	}

	@Override
	public Type<SeeCommentsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SeeCommentsEventHandler handler) {
		handler.onSeeComments(this);
	}
}

package org.gcube.portlets.user.newsfeed.client.event;

import java.util.HashSet;

import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;



public class AddCommentEvent  extends GwtEvent<AddCommentEventHandler> {
	public static Type<AddCommentEventHandler> TYPE = new Type<AddCommentEventHandler>();
	
	private TweetTemplate owner;
	private String text;
	private HashSet<String> mentionedUsers;
	
	public AddCommentEvent(TweetTemplate owner, String text, HashSet<String> mentionedUsers) {
		this.owner = owner;
		this.text = text;
		this.mentionedUsers = mentionedUsers;
	}
	
	public TweetTemplate getOwner() {
		return owner;
	}
	
	public String getText() {
		return text;
	}
	
	public HashSet<String> getMentionedUsers() {
		return mentionedUsers;
	}

	@Override
	public Type<AddCommentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddCommentEventHandler handler) {
		handler.onAddComment(this);
	}
}

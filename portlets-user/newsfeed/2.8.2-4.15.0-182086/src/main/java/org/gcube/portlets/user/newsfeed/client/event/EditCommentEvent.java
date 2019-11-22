package org.gcube.portlets.user.newsfeed.client.event;

import java.util.HashSet;

import org.gcube.portal.databook.shared.Comment;
import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;
import org.gcube.portlets.user.newsfeed.shared.MentionedDTO;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.HTMLPanel;



public class EditCommentEvent  extends GwtEvent<EditCommentEventHandler> {
	public static Type<EditCommentEventHandler> TYPE = new Type<EditCommentEventHandler>();
	
	private TweetTemplate owner;
	private Comment edited;
	private String text;
	private HTMLPanel commentPanel;
	private HashSet<MentionedDTO> mentionedUsers;
	
	public TweetTemplate getOwner() {
		return owner;
	}
	public Comment getCommentInstance() {
		return edited;
	}
	
	public HTMLPanel getCommentPanel() {
		return commentPanel;
	}
	public EditCommentEvent(TweetTemplate owner, String text, Comment editedComment, HTMLPanel commentPanel, HashSet<MentionedDTO> mentionedUsers) {
		this.owner = owner;
		this.text = text;
		this.edited = editedComment;
		this.commentPanel = commentPanel;
		this.mentionedUsers = mentionedUsers;
	}

	public HashSet<MentionedDTO> getMentionedUsers() {
		return mentionedUsers;
	}
	public String getText() {
		return text;
	}
	
	@Override
	public Type<EditCommentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EditCommentEventHandler handler) {
		handler.onEditComment(this);
	}
}

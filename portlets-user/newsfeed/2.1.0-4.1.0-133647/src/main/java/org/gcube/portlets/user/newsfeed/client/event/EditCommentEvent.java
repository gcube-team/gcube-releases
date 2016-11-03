package org.gcube.portlets.user.newsfeed.client.event;

import org.gcube.portal.databook.shared.Comment;
import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.HTMLPanel;



public class EditCommentEvent  extends GwtEvent<EditCommentEventHandler> {
	public static Type<EditCommentEventHandler> TYPE = new Type<EditCommentEventHandler>();
	
	private TweetTemplate owner;
	private Comment edited;
	private HTMLPanel commentPanel;
	
	public TweetTemplate getOwner() {
		return owner;
	}
	public Comment getCommentInstance() {
		return edited;
	}
	
	public HTMLPanel getCommentPanel() {
		return commentPanel;
	}
	public EditCommentEvent(TweetTemplate owner, Comment editedComment, HTMLPanel commentPanel) {
		this.owner = owner;
		this.edited = editedComment;
		this.commentPanel = commentPanel;
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

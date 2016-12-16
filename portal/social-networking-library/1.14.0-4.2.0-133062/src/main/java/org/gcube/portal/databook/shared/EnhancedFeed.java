package org.gcube.portal.databook.shared;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 
 * @author massi
 * This class contains addtional user related information about a Feed
 * e.g. if this user has liked it
 */
@SuppressWarnings("serial")
public class EnhancedFeed implements Serializable{
	private Feed feed;
	private boolean liked;
	private boolean isUsers;
	private ArrayList<Comment> comments;
	private ArrayList<Attachment> attachments;
	
	
	public EnhancedFeed() {
		super();
	}
	public EnhancedFeed(Feed feed, boolean liked,  boolean isUsers) {
		super();
		this.feed = feed;
		this.liked = liked;
		this.isUsers = isUsers;
	}
	
	public EnhancedFeed(Feed feed, boolean liked, boolean isUsers, ArrayList<Comment> comments) {
		super();
		this.isUsers = isUsers;
		this.feed = feed;
		this.liked = liked;
		this.comments = comments;
	}
	
	
	public EnhancedFeed(Feed feed, boolean liked, boolean isUsers,
			ArrayList<Comment> comments, ArrayList<Attachment> attachments) {
		super();
		this.feed = feed;
		this.liked = liked;
		this.isUsers = isUsers;
		this.comments = comments;
		this.attachments = attachments;
	}
	public ArrayList<Comment> getComments() {
		return comments;
	}
	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}
	public Feed getFeed() {
		return feed;
	}
	public void setFeed(Feed feed) {
		this.feed = feed;
	}
	public boolean isLiked() {
		return liked;
	}
	public void setLiked(boolean liked) {
		this.liked = liked;
	}
	public boolean isUsers() {
		return isUsers;
	}
	public void setUsers(boolean isUsers) {
		this.isUsers = isUsers;
	}
	public ArrayList<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(ArrayList<Attachment> attachments) {
		this.attachments = attachments;
	}
	@Override
	public String toString() {
		return "EnhancedFeed [feed=" + feed + ", liked=" + liked + ", isUsers="
				+ isUsers + ", comments=" + comments + ", attachments="
				+ attachments + "]";
	}	
	
}

package org.gcube.portlet.user.userstatisticsportlet.shared;

import java.io.Serializable;

/**
 * Contains the retrieved user's statistics relative to number of feeds written, replies and likes got.
 * 
 * @author Costantino Perciante at ISTI-CNR
 */
public class PostsStatsBean implements Serializable{

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 2043823499293477290L;
	private long feedsNumber;
	private long likesReceived;
	private long commentsReceived;
	private long commentsMade;
	private long likesMade;

	public PostsStatsBean(){
		super();
	}

	/**
	 * @param feeds
	 * @param likesReceived
	 * @param commentsReceived
	 * @param commentsMade
	 * @param likesMade
	 */
	public PostsStatsBean(long feeds, long likesReceived,
			long commentsReceived, long commentsMade, long likesMade) {
		super();
		this.feedsNumber = feeds;
		this.likesReceived = likesReceived;
		this.commentsReceived = commentsReceived;
		this.commentsMade = commentsMade;
		this.likesMade = likesMade;
	}

	public long getFeedsNumber() {
		return feedsNumber;
	}

	public void setFeedsNumber(long feeds) {
		this.feedsNumber = feeds;
	}

	public long getLikesReceived() {
		return likesReceived;
	}

	public void setLikesReceived(long likesReceived) {
		this.likesReceived = likesReceived;
	}

	public long getCommentsReceived() {
		return commentsReceived;
	}

	public void setCommentsReceived(long commentsReceived) {
		this.commentsReceived = commentsReceived;
	}

	public long getCommentsMade() {
		return commentsMade;
	}

	public void setCommentsMade(long commentsMade) {
		this.commentsMade = commentsMade;
	}

	public long getLikesMade() {
		return likesMade;
	}

	public void setLikesMade(long likesMade) {
		this.likesMade = likesMade;
	}

	@Override
	public String toString() {
		return "PostsStatsBean [feeds=" + feedsNumber + ", likesReceived="
				+ likesReceived + ", commentsReceived=" + commentsReceived
				+ ", commentsMade=" + commentsMade + ", likesMade=" + likesMade
				+ "]";
	}

}

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
	private long feeds;
	private long likesReceived;
	private long commentsReceived;

	public PostsStatsBean(){
		super();
	}

	public PostsStatsBean(long feeds, long likesReceived, long commentsReceived) {
		super();
		this.feeds = feeds;
		this.likesReceived = likesReceived;
		this.commentsReceived = commentsReceived;
	}

	public long getFeedsNumber() {
		return feeds;
	}

	public void setFeedsNumber(long feeds) {
		this.feeds = feeds;
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

	@Override
	public String toString() {
		return "FeedsInformation [feeds=" + feeds + ", likesReceived="
				+ likesReceived + ", commentsReceived=" + commentsReceived
				+ "]";
	}

}

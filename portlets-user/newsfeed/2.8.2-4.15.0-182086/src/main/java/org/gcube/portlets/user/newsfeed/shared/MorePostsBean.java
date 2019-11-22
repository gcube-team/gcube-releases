package org.gcube.portlets.user.newsfeed.shared;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portal.databook.shared.EnhancedFeed;

@SuppressWarnings("serial")
public class MorePostsBean implements Serializable {
	private int lastReturnedPostTimelineIndex;
	private ArrayList<EnhancedFeed> posts;

	public MorePostsBean() {
		super();
	}
	
	public MorePostsBean(int lastReturnedFeedTimelineIndex,
			ArrayList<EnhancedFeed> feeds) {
		super();
		this.lastReturnedPostTimelineIndex = lastReturnedFeedTimelineIndex;
		this.posts = feeds;
	}

	public int getLastReturnedFeedTimelineIndex() {
		return lastReturnedPostTimelineIndex;
	}
	public void setLastReturnedFeedTimelineIndex(int lastReturnedFeedTimelineIndex) {
		this.lastReturnedPostTimelineIndex = lastReturnedFeedTimelineIndex;
	}
	public ArrayList<EnhancedFeed> getPosts() {
		return posts;
	}
	public void setPosts(ArrayList<EnhancedFeed> posts) {
		this.posts = posts;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MorePostsBean [lastReturnedPostTimelineIndex=");
		builder.append(lastReturnedPostTimelineIndex);
		builder.append(", posts=");
		builder.append(posts);
		builder.append("]");
		return builder.toString();
	}

	
	
	
}

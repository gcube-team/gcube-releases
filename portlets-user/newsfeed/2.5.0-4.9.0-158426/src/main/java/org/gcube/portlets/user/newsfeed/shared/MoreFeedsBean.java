package org.gcube.portlets.user.newsfeed.shared;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portal.databook.shared.EnhancedFeed;

@SuppressWarnings("serial")
public class MoreFeedsBean implements Serializable {
	private int lastReturnedFeedTimelineIndex;
	private ArrayList<EnhancedFeed> feeds;

	public MoreFeedsBean() {
		super();
	}
	
	public MoreFeedsBean(int lastReturnedFeedTimelineIndex,
			ArrayList<EnhancedFeed> feeds) {
		super();
		this.lastReturnedFeedTimelineIndex = lastReturnedFeedTimelineIndex;
		this.feeds = feeds;
	}

	public int getLastReturnedFeedTimelineIndex() {
		return lastReturnedFeedTimelineIndex;
	}
	public void setLastReturnedFeedTimelineIndex(int lastReturnedFeedTimelineIndex) {
		this.lastReturnedFeedTimelineIndex = lastReturnedFeedTimelineIndex;
	}
	public ArrayList<EnhancedFeed> getFeeds() {
		return feeds;
	}
	public void setFeeds(ArrayList<EnhancedFeed> feeds) {
		this.feeds = feeds;
	}

	@Override
	public String toString() {
		return "MoreFeedsBean [lastReturnedFeedTimelineIndex="
				+ lastReturnedFeedTimelineIndex + ", feeds=" + feeds + "]";
	}
	
	
}

package org.gcube.portal.databook.shared;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings("serial")
public class RangeFeeds implements Serializable {
	
	private int lastReturnedFeedTimelineIndex;
	private ArrayList<Feed> feeds;
	
	public RangeFeeds() {
		super();
	}
	
	public RangeFeeds(int lastReturnedFeedTimelineIndex, ArrayList<Feed> feeds) {
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
	public ArrayList<Feed> getFeeds() {
		return feeds;
	}
	public void setFeeds(ArrayList<Feed> feeds) {
		this.feeds = feeds;
	}
	
	
}

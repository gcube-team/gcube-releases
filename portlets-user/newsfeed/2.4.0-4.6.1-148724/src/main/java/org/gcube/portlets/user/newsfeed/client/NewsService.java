package org.gcube.portlets.user.newsfeed.client;

import java.util.ArrayList;
import java.util.HashSet;

import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.portal.databook.shared.Like;
import org.gcube.portal.databook.shared.ShowUserStatisticAction;
import org.gcube.portlets.user.newsfeed.shared.MoreFeedsBean;
import org.gcube.portlets.user.newsfeed.shared.OperationResult;
import org.gcube.portlets.user.newsfeed.shared.UserSettings;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("newsServlet")
public interface NewsService extends RemoteService {
	ArrayList<EnhancedFeed> getAllUpdateUserFeeds(int feedsNoPerCategory);
	
	ArrayList<EnhancedFeed> getOnlyConnectionsUserFeeds();
	
	ArrayList<EnhancedFeed> getOnlyMyUserFeeds();
	
	ArrayList<EnhancedFeed> getOnlyLikedFeeds();
	
	ArrayList<EnhancedFeed> getFeedsByHashtag(String hashtag);
	
	ArrayList<EnhancedFeed> getFeedsByQuery(String query, int from, int quantity);
	
	ArrayList<EnhancedFeed> getFeedsRelatedToUserStatistics(ShowUserStatisticAction action, int from, int quantity);
	
	MoreFeedsBean getMoreFeeds(int from, int quantity);
	
	boolean like(String feedid, String feedText, String feedOwnerId);
	
	boolean unlike(String feedid, String feedText, String feedOwnerId);
	
	boolean deleteComment(String commentid, String feedid);
	
	boolean deleteFeed(String feedid);
	
	OperationResult comment(String feedid, String text,
			HashSet<String> mentionedUsers, String feedOwnerId,
			boolean isAppFeed);
	
	OperationResult editComment(Comment toEdit);
	
	ArrayList<Like> getAllLikesByFeed(String feedid);
	
	ArrayList<Comment> getAllCommentsByFeed(String feedid);
	
	UserSettings getUserSettings();
	
	EnhancedFeed getSingleFeed(String feedKey);
	
	ArrayList<ItemBean> getOrganizationUsers();
}

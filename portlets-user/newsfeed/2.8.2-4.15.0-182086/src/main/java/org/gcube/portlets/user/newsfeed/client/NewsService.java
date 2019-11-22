package org.gcube.portlets.user.newsfeed.client;

import java.util.ArrayList;
import java.util.HashSet;

import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.portal.databook.shared.Like;
import org.gcube.portal.databook.shared.ShowUserStatisticAction;
import org.gcube.portlets.user.newsfeed.shared.MentionedDTO;
import org.gcube.portlets.user.newsfeed.shared.MorePostsBean;
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
	
	ArrayList<EnhancedFeed> getOnlyConnectionsUserPosts();
	
	ArrayList<EnhancedFeed> getOnlyMyUserPosts();
	
	ArrayList<EnhancedFeed> getOnlyLikedPosts();
	
	ArrayList<EnhancedFeed> getPostsByHashtag(String hashtag);
	
	ArrayList<EnhancedFeed> getPostsByQuery(String query, int from, int quantity);
	
	ArrayList<EnhancedFeed> getPostsRelatedToUserStatistics(ShowUserStatisticAction action, int from, int quantity);
	
	MorePostsBean getMorePosts(int from, int quantity);
	
	boolean like(String postid, String postText, String postOwnerId);
	
	boolean unlike(String postid, String postText, String postOwnerId);
	
	boolean deleteComment(String commentid, String feedid);
	
	boolean deletePost(String postid);
	
	OperationResult comment(String feedid, String text,
			HashSet<MentionedDTO> mentionedUsers, String feedOwnerId,
			boolean isAppFeed);
	
	OperationResult editComment(String text, Comment toEdit, HashSet<MentionedDTO> mentionedUsers);
	
	ArrayList<Like> getAllLikesByPost(String postid);
	
	ArrayList<Comment> getAllCommentsByPost(String feedid);
	
	UserSettings getUserSettings();
	
	EnhancedFeed getSinglePost(String postKey);
	
	ArrayList<ItemBean> getOrganizationUsers();
}

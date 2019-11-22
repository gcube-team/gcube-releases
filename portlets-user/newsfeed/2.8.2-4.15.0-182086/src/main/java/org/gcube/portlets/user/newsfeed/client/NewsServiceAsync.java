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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>NewsService</code>.
 */
public interface NewsServiceAsync {

	void getAllUpdateUserFeeds(int feedsNoPerCategory,
			AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void getOnlyConnectionsUserPosts(
			AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void like(String postid, String postText, String postOwnerId,
			AsyncCallback<Boolean> callback);

	void getAllLikesByPost(String postid,
			AsyncCallback<ArrayList<Like>> callback);

	void getOnlyMyUserPosts(AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void getUserSettings(AsyncCallback<UserSettings> callback);

	void comment(String feedid, String text, HashSet<MentionedDTO> mentionedUsers,
			String feedOwnerId, boolean isAppFeed,
			AsyncCallback<OperationResult> callback);

	void getAllCommentsByPost(String postid,
			AsyncCallback<ArrayList<Comment>> callback);

	void deleteComment(String commentid, String feedid,
			AsyncCallback<Boolean> callback);

	void deletePost(String feedid, AsyncCallback<Boolean> callback);

	void editComment(String text, Comment toEdit, HashSet<MentionedDTO> mentionedUsers, AsyncCallback<OperationResult> callback);

	void getOnlyLikedPosts(AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void getSinglePost(String postKey, AsyncCallback<EnhancedFeed> callback);

	void getMorePosts(int from, int quantity,
			AsyncCallback<MorePostsBean> callback);

	void unlike(String postid, String postText, String postOwnerId,
			AsyncCallback<Boolean> callback);

	void getOrganizationUsers(AsyncCallback<ArrayList<ItemBean>> callback);

	void getPostsByHashtag(String hashtag,
			AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void getPostsByQuery(String query, int from, int quantity,
			AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void getPostsRelatedToUserStatistics(ShowUserStatisticAction action, int from, int quantity,
			AsyncCallback<ArrayList<EnhancedFeed>> callback);
	
}

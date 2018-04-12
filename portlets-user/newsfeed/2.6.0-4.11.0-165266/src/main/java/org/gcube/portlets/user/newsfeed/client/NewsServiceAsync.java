package org.gcube.portlets.user.newsfeed.client;

import java.util.ArrayList;
import java.util.HashSet;

import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.portal.databook.shared.Like;
import org.gcube.portal.databook.shared.ShowUserStatisticAction;
import org.gcube.portlets.user.newsfeed.shared.MentionedDTO;
import org.gcube.portlets.user.newsfeed.shared.MoreFeedsBean;
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

	void getOnlyConnectionsUserFeeds(
			AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void like(String feedid, String feedText, String feedOwnerId,
			AsyncCallback<Boolean> callback);

	void getAllLikesByFeed(String feedid,
			AsyncCallback<ArrayList<Like>> callback);

	void getOnlyMyUserFeeds(AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void getUserSettings(AsyncCallback<UserSettings> callback);

	void comment(String feedid, String text, HashSet<MentionedDTO> mentionedUsers,
			String feedOwnerId, boolean isAppFeed,
			AsyncCallback<OperationResult> callback);

	void getAllCommentsByFeed(String feedid,
			AsyncCallback<ArrayList<Comment>> callback);

	void deleteComment(String commentid, String feedid,
			AsyncCallback<Boolean> callback);

	void deleteFeed(String feedid, AsyncCallback<Boolean> callback);

	void editComment(Comment toEdit, AsyncCallback<OperationResult> callback);

	void getOnlyLikedFeeds(AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void getSingleFeed(String feedKey, AsyncCallback<EnhancedFeed> callback);

	void getMoreFeeds(int from, int quantity,
			AsyncCallback<MoreFeedsBean> callback);

	void unlike(String feedid, String feedText, String feedOwnerId,
			AsyncCallback<Boolean> callback);

	void getOrganizationUsers(AsyncCallback<ArrayList<ItemBean>> callback);

	void getFeedsByHashtag(String hashtag,
			AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void getFeedsByQuery(String query, int from, int quantity,
			AsyncCallback<ArrayList<EnhancedFeed>> callback);

	void getFeedsRelatedToUserStatistics(ShowUserStatisticAction action, int from, int quantity,
			AsyncCallback<ArrayList<EnhancedFeed>> callback);
	
}

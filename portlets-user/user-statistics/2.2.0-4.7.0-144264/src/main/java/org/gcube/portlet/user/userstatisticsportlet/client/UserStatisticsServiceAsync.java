package org.gcube.portlet.user.userstatisticsportlet.client;

import org.gcube.portlet.user.userstatisticsportlet.shared.PostsStatsBean;
import org.gcube.portlet.user.userstatisticsportlet.shared.QuotaInfo;
import org.gcube.portlet.user.userstatisticsportlet.shared.UserInformation;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async version of the UserStatisticsService interface
 * @author Costantino Perciante at ISTI-CNR
 */
public interface UserStatisticsServiceAsync {

	void getUserSettings(String userid, AsyncCallback<UserInformation> callback);

	void getPostsStats(String userid, AsyncCallback<PostsStatsBean> callback);

	void getTotalSpaceInUse(String userid, AsyncCallback<String> callback);

	void getProfileStrength(String userid, AsyncCallback<Integer> callback);

	void setShowMyOwnStatisticsToOtherPeople(boolean show,
			AsyncCallback<Void> callback);

	void getQuotaStorage(String userid, AsyncCallback<QuotaInfo> callback);
}

package org.gcube.portlet.user.userstatisticsportlet.client;

import org.gcube.portlet.user.userstatisticsportlet.shared.PostsStatsBean;
import org.gcube.portlet.user.userstatisticsportlet.shared.UserInformation;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async version of the UserStatisticsService interface
 * 
 * @author Costantino Perciante at ISTI-CNR
 */
public interface UserStatisticsServiceAsync {

	void getUserSettings(AsyncCallback<UserInformation> callback);

	void getPostsStats(AsyncCallback<PostsStatsBean> callback);

	void getTotalSpaceInUse(AsyncCallback<String> callback);

	void getProfileStrength(AsyncCallback<Integer> callback);

}

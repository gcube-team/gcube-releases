package org.gcube.portlet.user.userstatisticsportlet.client;

import org.gcube.portlet.user.userstatisticsportlet.shared.PostsStatsBean;
import org.gcube.portlet.user.userstatisticsportlet.shared.UserInformation;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface for retrieving user's statistics
 * @author Costantino Perciante at ISTI-CNR
 */
@RemoteServiceRelativePath("statisticservice")
public interface UserStatisticsService extends RemoteService {
	
	/**
	 * get other user's information
	 * @return an object with user's information (actual vre, his name, url of his image)
	 */
	UserInformation getUserSettings();

	/**
	 *  get information relatives to feeds(posts), comment replies and likes received
	 *  @return an object with user's statistics (number of feeds(posts), comment replies, likes received)
	 */
	PostsStatsBean getPostsStats();
	
	/**
	 * get the total space in use on the workspace
	 */
	String getTotalSpaceInUse();
	
	/**
	 * get profile strenght
	 */
	int getProfileStrength();
}

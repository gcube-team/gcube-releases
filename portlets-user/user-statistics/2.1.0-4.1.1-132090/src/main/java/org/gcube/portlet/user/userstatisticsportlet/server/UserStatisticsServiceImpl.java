package org.gcube.portlet.user.userstatisticsportlet.server;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portlet.user.userstatisticsportlet.client.UserStatisticsService;
import org.gcube.portlet.user.userstatisticsportlet.server.cache.UserInfrastructureSpaceCache;
import org.gcube.portlet.user.userstatisticsportlet.shared.PostsStatsBean;
import org.gcube.portlet.user.userstatisticsportlet.shared.UserInformation;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserStatisticsServiceImpl extends RemoteServiceServlet implements UserStatisticsService {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserStatisticsServiceImpl.class);

	//dev user
	public static final String defaultUserId = "test.user";

	//dev vre
	public static final String vreID = "/gcube/devsec/devVRE";

	// Cassandra connection
	private DatabookStore store;

	// custom field' name to remember action to take for the portlet deployed in the user profile TODO (make it a resource?)
	private static final String CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY = "show_user_statistics_other_people";

	@Override
	public void init() {

		// get connection to Cassandra
		logger.debug("Getting connection to Cassandra..");
		store = new DBCassandraAstyanaxImpl();

		// add statistics option for profile pages and set to true
		ServerUtils.createUserCustomField(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY, true);

	}

	@Override
	public void destroy(){
		// shutting down connection to Cassandra
		logger.info("Closing connection to Cassandra");
		store.closeConnection();
	}

	@Override
	public String getTotalSpaceInUse(String userid) {

		String storageInUse = null;

		// get the session
		ASLSession session = ServerUtils.getASLSession(this.getThreadLocalRequest().getSession());

		// username in the session
		String userName = session.getUsername();

		// retrieve statistics of...
		String statisticsOfUsername = userName;

		if(userid != null && !userid.equals(userName))
			statisticsOfUsername = userid;

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(defaultUserId) == 0) {

			logger.debug("Found " + userName + " returning nothing");
			return null;

		}else{

			logger.debug("Getting " + statisticsOfUsername + " amount of workspace in use.");

			try{

				long init = System.currentTimeMillis();

				// retrieve the cache
				UserInfrastructureSpaceCache cacheWorkspace = UserInfrastructureSpaceCache.getCacheInstance();

				// check if information is present
				Long storageInUseLong = (Long) cacheWorkspace.get(statisticsOfUsername);

				// if not, ask the workspace
				if(storageInUseLong == null){

					logger.debug("Information not available in the cache, asking workspace");
					Workspace workspace = HomeLibrary.getUserWorkspace(statisticsOfUsername);
					storageInUseLong = workspace.getDiskUsage();

					logger.debug("Put information in the cache");
					cacheWorkspace.insert(statisticsOfUsername, storageInUseLong);

				}

				storageInUse = ServerUtils.formatFileSize(storageInUseLong);

				long end = System.currentTimeMillis();
				logger.debug("[USER-STATISTICS] time taken to retrieve user space is " + (end - init) + "ms");

			}catch(Exception e){

				logger.error("Unable to retrieve workspace information!");

			}
		}
		return storageInUse;

	}

	@Override
	public int getProfileStrength(String userid) {

		int profileStrenght = -1;

		// get the session
		ASLSession session = ServerUtils.getASLSession(this.getThreadLocalRequest().getSession());

		// username
		String userName = session.getUsername();

		// retrieve statistics of...
		String statisticsOfUsername = userName;

		if(userid != null && !userid.equals(userName))
			statisticsOfUsername = userid;

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(defaultUserId) == 0) {
			logger.debug("Found " + userName + " returning nothing");
			return profileStrenght;
		}else{

			// valuate profile strength
			if(ServerUtils.isWithinPortal()){

				try{

					long init = System.currentTimeMillis();

					// check if the avatar is present
					boolean avatarPresent = (new LiferayUserManager().getUserAvatarBytes(statisticsOfUsername) != null);

					User user = UserLocalServiceUtil.getUserByScreenName(SiteManagerUtil.getCompany().getCompanyId(), statisticsOfUsername);
					profileStrenght = ServerUtils.evaluateProfileStrenght(user, avatarPresent);

					long end = System.currentTimeMillis();
					logger.debug("[USER-STATISTICS] time taken to evaluate user profile strenght is " + (end - init) + "ms");

				}catch(Exception e){

					logger.error("Profile strenght evaluation failed!!" + e.toString(), e);

				}
			}
		}
		return profileStrenght;

	}

	@Override
	public UserInformation getUserSettings(String userid) {

		// get the session
		ASLSession session = ServerUtils.getASLSession(this.getThreadLocalRequest().getSession());

		//username of the asl session
		String userName = session.getUsername();

		// retrieve statistics of...
		String statisticsOfUsername = userName;

		// is he the owner of the profile?
		boolean isOwner = false;

		// can we show this profile to other people?
		boolean isProfileShowable = true;

		if(userid == null || (userid !=null && userid.equals(userName))){
			isOwner = true;
			isProfileShowable = checkUserPrivacyOption(userName);
		}

		if(userid != null && !userid.equals(userName)){
			// the stastics to show will be of the userid
			statisticsOfUsername = userid;
			isProfileShowable = checkUserPrivacyOption(statisticsOfUsername);

			logger.info("Is profile showable for user " + userid +  " " +  isProfileShowable);
		}

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(defaultUserId) == 0) {
			logger.debug("Found " + userName + " returning nothing");
			return null;
		}

		if(ServerUtils.isWithinPortal()){
			// If the user is in the root panel (or is visiting a profile page), we have to send him the overall number of posts made, comments/likes(received) and the space in use.
			// Otherwise we have to filter on the vre.
			boolean isInfrastructure = ServerUtils.isInfrastructureScope(userid, this.getThreadLocalRequest().getSession());
			logger.debug("User scope is " + (isInfrastructure ? " the whole infrastucture " : " a VRE"));

			// get path of the avatar
			UserManager um = new LiferayUserManager();

			String thumbnailURL = null;
			try {
				thumbnailURL = um.getUserByUsername(statisticsOfUsername).getUserAvatarURL();
			} catch (UserManagementSystemException e) {
				logger.error("Unable to retrieve avatar url for user " + statisticsOfUsername, e);
			} catch (UserRetrievalFault e) {
				logger.error("Unable to retrieve avatar url for user " + statisticsOfUsername, e);
			}

			logger.debug(statisticsOfUsername + " avatar has url " + thumbnailURL);

			// get the vre (if not in the infrastructure)
			String actualVre = null;

			if(!isInfrastructure){

				String[] temp = session.getScope().split("/");
				actualVre = temp[temp.length - 1];

			}

			// page landing
			String pageLanding = PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest());
			UserInformation bean = new  UserInformation(isInfrastructure, thumbnailURL, userName, actualVre, isOwner, isProfileShowable);
			bean.setCurrentPageLanding(pageLanding);
			return bean;
		}
		else 
			return new UserInformation(true, null, userName, vreID, true, true);
	}

	/**
	 * Check privacy option for user's own statistics
	 * @param username
	 * @return
	 */
	private boolean checkUserPrivacyOption(String username) {
		try{

			// set permission checker
			ServerUtils.setPermissionChecker();

			//needed to avoid cache use by liferay API
			CacheRegistryUtil.clear(); 

			User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), username);

			// the user has not decided yet
			if(!user.getExpandoBridge().hasAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY))
				return true;

			return (boolean)user.getExpandoBridge().getAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY);

		}catch(Exception e){

			logger.error("Unable to retrieve user's privacy option for his statistics");
			return true;
		}
	}

	@Override
	public PostsStatsBean getPostsStats(String userid){

		// get the session
		ASLSession session = ServerUtils.getASLSession(this.getThreadLocalRequest().getSession());

		//username
		String userName = session.getUsername();

		// retrieve statistics of...
		String statisticsOfUsername = userName;

		if(userid != null && !userid.equals(userName))
			statisticsOfUsername = userid;

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(defaultUserId) == 0) {
			logger.debug("Found " + userName + " returning nothing");
			return null;
		}

		long feedsMade = 0, likesGot = 0, commentsGot = 0, commentsMade = 0, likesMade = 0;

		// check if the user is or not in a VRE
		boolean isInfrastructure = ServerUtils.isInfrastructureScope(userid, this.getThreadLocalRequest().getSession());

		// date corresponding to one year ago
		Calendar oneYearAgo = Calendar.getInstance();
		oneYearAgo.set(Calendar.YEAR, oneYearAgo.get(Calendar.YEAR) - 1);

		logger.debug("Reference time is " + oneYearAgo.getTime());

		try {

			long init = System.currentTimeMillis();

			logger.debug("Getting " + statisticsOfUsername + " feeds in the last year.");

			// retrieve the most recent user's feeds
			List<Feed> userFeeds = store.getRecentFeedsByUserAndDate(statisticsOfUsername, oneYearAgo.getTime().getTime());

			logger.debug("Evaluating number of comments and likes of " + statisticsOfUsername + "'s feeds.");

			for (Feed feed : userFeeds) {

				try{

					// check if the user is in the root, if not check if the VRE of the feed is the current one
					if(!isInfrastructure && !feed.getVreid().equals(session.getScope()))
						continue;

					// increment feeds number
					feedsMade ++;

					//increment number of post replies and likes
					commentsGot += Integer.parseInt(feed.getCommentsNo());
					likesGot += Integer.parseInt(feed.getLikesNo());

				}catch(NumberFormatException e){
					logger.error(e.toString());
				}
			}

			// retrieve the most recent user's liked feeds
			List<Feed> recentLikedFeeds = store.getRecentLikedFeedsByUserAndDate(statisticsOfUsername, oneYearAgo.getTime().getTime());

			for (Feed feed : recentLikedFeeds) {

				// check if the user is in the root, if not check if the VRE of the feed is the current one
				if(!isInfrastructure && !feed.getVreid().equals(session.getScope()))
					continue;

				likesMade ++; // no further check is needed since the user can do like just one time per feed
			}

			// retrieve the most recent user's comments
			List<Comment> recentComments = store.getRecentCommentsByUserAndDate(statisticsOfUsername, oneYearAgo.getTime().getTime());
			Map<String, Feed> parentFeeds = new HashMap<String, Feed>();

			for (Comment comment : recentComments) {

				Feed parentFeed = null;

				if(!parentFeeds.containsKey(comment.getFeedid())){
					parentFeed = store.readFeed(comment.getFeedid());
					parentFeeds.put(comment.getFeedid(), parentFeed);
				}

				parentFeed = parentFeeds.get(comment.getFeedid());

				// check if the user is in the root, if not check if the VRE of the feed is the current one
				if(!isInfrastructure && !parentFeed.getVreid().equals(session.getScope()))
					continue;

				commentsMade ++;
			}

			long end = System.currentTimeMillis();

			logger.debug("[USER-STATISTICS] time taken to retrieve statistics is " + (end - init) + " ms");
			logger.debug("Total number of feeds made (after time filtering) of  " + statisticsOfUsername + " is " + feedsMade);
			logger.debug("Total number of likes got (after time filtering) for " + statisticsOfUsername + " is " + likesGot);
			logger.debug("Total number of comments got (after time filtering) for " + statisticsOfUsername + " is " + commentsGot);
			logger.debug("Total number of likes made (after time filtering) for " + statisticsOfUsername + " is " + likesMade);
			logger.debug("Total number of comments made (after time filtering) for " + statisticsOfUsername + " is " + commentsMade);

		}catch(Exception e){
			logger.error(e.toString());
			return null;
		}

		// return the object
		return new PostsStatsBean(feedsMade, likesGot, commentsGot, commentsMade, likesMade);
	}

	@Override
	public void setShowMyOwnStatisticsToOtherPeople(boolean show) {

		if(ServerUtils.isWithinPortal()){

			ASLSession session = ServerUtils.getASLSession(this.getThreadLocalRequest().getSession());

			String username = session.getUsername();

			//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
			//this check just return nothing if that happens
			if (username.compareTo(defaultUserId) == 0) {
				logger.debug("Found " + username + " returning nothing");
				return;
			}

			try{

				// set permission checker
				ServerUtils.setPermissionChecker();

				//needed to avoid cache use by liferay API
				CacheRegistryUtil.clear(); 

				logger.debug("User " + username + (show ? " want to show " : " doesn't want to show ") + " his statistics");

				User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), username);
				boolean hasAttribute = user.getExpandoBridge().hasAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY);

				if(hasAttribute){

					// set the new value
					logger.debug("Setting custom field value to "  + show + " for user " + username);

					// set the current value
					user.getExpandoBridge().setAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY, show);

				}

			}catch(Exception e){
				logger.error("Unable to check user's privacy for his statistics", e);
			}
		}

	}
}

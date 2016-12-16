package org.gcube.portlet.user.userstatisticsportlet.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserStatisticsServiceImpl extends RemoteServiceServlet implements UserStatisticsService {

	//	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserStatisticsServiceImpl.class);
	private static final Log logger = LogFactoryUtil.getLog(UserStatisticsServiceImpl.class);
	private DatabookStore store;
	private static final String CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY = "show_user_statistics_other_people";
	private UserManager userManager = new LiferayUserManager();
	private GroupManager groupManager = new LiferayGroupManager();

	@Override
	public void init() {
		logger.info("Getting connection to Cassandra..");
		store = new DBCassandraAstyanaxImpl();
		ServerUtils.createUserCustomField(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY, true);
	}

	@Override
	public void destroy(){
		logger.info("Closing connection to Cassandra");
		store.closeConnection();
	}

	@Override
	public String getTotalSpaceInUse(String userid) {
		String storageInUse = null;
		String userName = ServerUtils.getCurrentUser(this.getThreadLocalRequest()).getUsername();

		// get context & token and set
		ServerUtils.getCurrentContext(this.getThreadLocalRequest(), true);
		ServerUtils.getCurrentSecurityToken(this.getThreadLocalRequest(), true);

		if(userName == null){
			logger.warn("Unable to determine the current user, returing null");
		}

		String statisticsOfUsername = userName;

		if(userid != null && !userid.equals(userName))
			statisticsOfUsername = userid;

		logger.debug("Getting " + statisticsOfUsername + " amount of workspace in use.");
		try{
			UserInfrastructureSpaceCache cacheWorkspace = UserInfrastructureSpaceCache.getCacheInstance();
			Long storageInUseLong = (Long) cacheWorkspace.get(statisticsOfUsername);

			if(storageInUseLong == null){
				Workspace workspace = HomeLibrary.getUserWorkspace(statisticsOfUsername);
				storageInUseLong = workspace.getDiskUsage();
				cacheWorkspace.insert(statisticsOfUsername, storageInUseLong);
			}

			storageInUse = ServerUtils.formatFileSize(storageInUseLong);
		}catch(Exception e){
			logger.error("Unable to retrieve workspace information!", e);
		}

		return storageInUse;
	}

	@Override
	public int getProfileStrength(String userid) {

		int profileStrenght = -1;
		String userName = ServerUtils.getCurrentUser(this.getThreadLocalRequest()).getUsername();

		if(userName == null){
			logger.warn("Unable to determine the current user, returing null");
		}

		String statisticsOfUsername = userName;

		if(userid != null && !userid.equals(userName))
			statisticsOfUsername = userid;

		if(ServerUtils.isWithinPortal()){
			try{
				boolean avatarPresent = (userManager.getUserAvatarBytes(statisticsOfUsername) != null);
				User user = UserLocalServiceUtil.getUserByScreenName(SiteManagerUtil.getCompany().getCompanyId(), statisticsOfUsername);
				profileStrenght = ServerUtils.evaluateProfileStrenght(user, avatarPresent);
			}catch(Exception e){
				logger.error("Profile strenght evaluation failed!!" + e.toString(), e);
			}
		}

		return profileStrenght;
	}

	@Override
	public UserInformation getUserSettings(String userid) {

		String userName = ServerUtils.getCurrentUser(this.getThreadLocalRequest()).getUsername();

		if(userName == null){
			logger.warn("Unable to determine the current user, returing null");
			return null;
		}

		String statisticsOfUsername = userName;
		boolean isOwner = false;
		boolean isProfileShowable = true;

		if(userid == null || (userid !=null && userid.equals(userName))){
			isOwner = true;
			isProfileShowable = checkUserPrivacyOption(userName);
		}

		if(userid != null && !userid.equals(userName)){
			statisticsOfUsername = userid;
			isProfileShowable = checkUserPrivacyOption(statisticsOfUsername);
			logger.info("Is profile showable for user " + userid +  " " +  isProfileShowable);
		}

		if(ServerUtils.isWithinPortal()){
			boolean isInfrastructure = ServerUtils.isInfrastructureScope(userid, this.getThreadLocalRequest());
			logger.debug("User scope is " + (isInfrastructure ? " the whole infrastucture " : " a VRE"));

			String thumbnailURL = null;
			try {
				thumbnailURL = userManager.getUserByUsername(statisticsOfUsername).getUserAvatarURL();
			} catch (Exception e) {
				logger.error("Unable to retrieve avatar url for user " + statisticsOfUsername +". Likely he/she doesn't have an avatar");
			}

			String actualVre = null;

			if(!isInfrastructure){

				String[] temp = ServerUtils.getCurrentContext(this.getThreadLocalRequest(), true).split("/");
				actualVre = temp[temp.length - 1];
			}

			String pageLanding = PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest());
			UserInformation bean = new  UserInformation(isInfrastructure, thumbnailURL, userName, actualVre, isOwner, isProfileShowable);
			bean.setCurrentPageLanding(pageLanding);
			return bean;
		}
		else 
			return new UserInformation(true, null, userName, ServerUtils.getCurrentContext(this.getThreadLocalRequest(), false), true, true);
	}

	/**
	 * Check privacy option for user's own statistics
	 * @param username
	 * @return
	 */
	private boolean checkUserPrivacyOption(String username) {
		if(ServerUtils.isWithinPortal()){
			try{
				ServerUtils.setPermissionChecker();
				CacheRegistryUtil.clear(); 
				User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), username);
				if(!user.getExpandoBridge().hasAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY))
					return true;
				return (boolean)user.getExpandoBridge().getAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY);
			}catch(Exception e){
				logger.error("Unable to retrieve user's privacy option for his statistics");
				return true;
			}
		}
		return false;
	}

	@Override
	public PostsStatsBean getPostsStats(String userid){

		String userName = ServerUtils.getCurrentUser(this.getThreadLocalRequest()).getUsername();

		if(userName == null){
			logger.warn("Unable to determine the current user, returing null");
		}

		String scope = ServerUtils.getCurrentContext(this.getThreadLocalRequest(), true);
		PostsStatsBean toReturn = null;

		String statisticsOfUsername = userName;
		if(userid != null && !userid.equals(userName))
			statisticsOfUsername = userid;

		boolean isInfrastructure = ServerUtils.isInfrastructureScope(userid, this.getThreadLocalRequest());

		Calendar oneYearAgo = Calendar.getInstance();
		oneYearAgo.set(Calendar.YEAR, oneYearAgo.get(Calendar.YEAR) - 1);

		logger.debug("Reference time is " + oneYearAgo.getTime());
		try {
			long userId = userManager.getUserId(statisticsOfUsername);

			List<Feed> userFeeds = store.getRecentFeedsByUserAndDate(statisticsOfUsername, oneYearAgo.getTimeInMillis());
			List<Feed> recentLikedFeeds = store.getRecentLikedFeedsByUserAndDate(statisticsOfUsername, oneYearAgo.getTimeInMillis());
			List<Comment> recentComments = store.getRecentCommentsByUserAndDate(statisticsOfUsername, oneYearAgo.getTimeInMillis());

			// Evaluate the contexts to use
			List<String> contexts = new ArrayList<String>();
			if(isInfrastructure){
				Set<GCubeGroup> vresInPortal = groupManager.listGroupsByUserAndSite(userId, getThreadLocalRequest().getServerName());
				for (GCubeGroup gCubeGroup : vresInPortal) {
					contexts.add(groupManager.getInfrastructureScope(gCubeGroup.getGroupId()));
				}
			}else{
				contexts.add(scope);
			}

			logger.info("Context(s) that are going to be used " + contexts);

			long feedsMade = 0, likesGot = 0, commentsGot = 0, commentsMade = 0, likesMade = 0;

			for (Feed feed : userFeeds) {

				if(contexts.contains(feed.getVreid())){
					feedsMade ++;
					commentsGot += Integer.parseInt(feed.getCommentsNo());
					likesGot += Integer.parseInt(feed.getLikesNo());
				}
			}
			for (Feed feed : recentLikedFeeds) {

				if(contexts.contains(feed.getVreid()))
					likesMade ++; 
			}

			Map<String, Feed> parentFeeds = new HashMap<String, Feed>();

			for (Comment comment : recentComments) {
				Feed parentFeed = null;
				if(!parentFeeds.containsKey(comment.getFeedid())){
					parentFeed = store.readFeed(comment.getFeedid());
					parentFeeds.put(comment.getFeedid(), parentFeed);
				}else
					parentFeed = parentFeeds.get(comment.getFeedid());
				if((contexts.contains(parentFeed.getVreid())))
					commentsMade ++;
			}

			toReturn = new PostsStatsBean(feedsMade, likesGot, commentsGot, commentsMade, likesMade);

		}catch(Exception e){
			logger.error("Error while retrieving user's statistics", e);
		}

		return toReturn;
	}

	@Override
	public void setShowMyOwnStatisticsToOtherPeople(boolean show) {

		if(ServerUtils.isWithinPortal()){

			String username = ServerUtils.getCurrentUser(this.getThreadLocalRequest()).getUsername();

			if(username == null){
				logger.warn("Unable to determine the current user, returing null");
			}

			try{
				ServerUtils.setPermissionChecker();
				CacheRegistryUtil.clear(); 
				User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), username);
				boolean hasAttribute = user.getExpandoBridge().hasAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY);

				if(hasAttribute){
					logger.debug("Setting custom field value to "  + show + " for user " + username);
					user.getExpandoBridge().setAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY, show);
				}
			}catch(Exception e){
				logger.error("Unable to check user's privacy for his statistics", e);
			}
		}

	}
}

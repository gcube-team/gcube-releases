package org.gcube.portlet.user.userstatisticsportlet.server;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.custom.communitymanager.SiteManagerUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portlet.user.userstatisticsportlet.client.UserStatisticsService;
import org.gcube.portlet.user.userstatisticsportlet.server.cache.UserInfrastructureSpaceCache;
import org.gcube.portlet.user.userstatisticsportlet.shared.PostsStatsBean;
import org.gcube.portlet.user.userstatisticsportlet.shared.UserInformation;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.User;
import com.liferay.portal.model.Website;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.WebsiteLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserStatisticsServiceImpl extends RemoteServiceServlet implements UserStatisticsService {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(UserStatisticsServiceImpl.class);

	//dev user
	public static final String defaultUserId = "test.user";

	//dev vre
	private static final String vreID = "/gcube/devsec/devVRE";

	// Cassandra connection
	private DatabookStore store;

	// custom field' name to remember action to take for the portlet deployed in the user profile
	private static final String CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY = "show_user_statistics_other_people";

	@Override
	public void init() {
		// get connection to Cassandra
		_log.debug("Getting connection to Cassandra..");
		store = new DBCassandraAstyanaxImpl();

		// add statistics option for profile pages and set to true
		createUserCustomField(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY, true);

	}

	@Override
	public void destroy(){
		// shutting down connection to Cassandra
		_log.info("Closing connection to Cassandra");
		store.closeConnection();
	}

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {

		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {

			_log.warn("USER IS NULL setting " + defaultUserId + " and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(vreID);

		}		

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @param userid in case userid is not null, the user is visiting a profile page and 
	 * the statistics to return are the ones available in the whole infrastructure
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope(String userid) {
		boolean toReturn = false;
		try {
			GroupManager manager = new LiferayGroupManager();
			long groupId = manager.getGroupIdFromInfrastructureScope(getASLSession().getScope());
			toReturn = !manager.isVRE(groupId) || userid != null;
			return toReturn;
		}
		catch (Exception e) {
			_log.error("NullPointerException in isInfrastructureScope returning false");
			return false;
		}		
	}

	/**
	 * Online or in development mode?
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}

	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = defaultUserId;
		//		user = "costantino.perciante";
		return user;
	}


	@Override
	public String getTotalSpaceInUse(String userid) {

		String storageInUse = null;

		// get the session
		ASLSession session = getASLSession();

		// username in the session
		String userName = session.getUsername();

		// retrieve statistics of...
		String statisticsOfUsername = userName;

		if(userid != null && !userid.equals(userName))
			statisticsOfUsername = userid;

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(defaultUserId) == 0) {

			_log.debug("Found " + userName + " returning nothing");
			return null;

		}else{

			_log.debug("Getting " + statisticsOfUsername + " amount of workspace in use.");

			try{

				long init = System.currentTimeMillis();

				// retrieve the cache
				UserInfrastructureSpaceCache cacheWorkspace = UserInfrastructureSpaceCache.getCacheInstance();

				// check if information is present
				Long storageInUseLong = (Long) cacheWorkspace.get(statisticsOfUsername);

				// if not, ask the workspace
				if(storageInUseLong == null){

					_log.debug("Information not available in the cache, asking workspace");
					Workspace workspace = HomeLibrary.getUserWorkspace(statisticsOfUsername);
					storageInUseLong = workspace.getDiskUsage();

					_log.debug("Put information in the cache");
					cacheWorkspace.insert(statisticsOfUsername, storageInUseLong);

				}

				storageInUse = formatFileSize(storageInUseLong);

				long end = System.currentTimeMillis();
				_log.debug("[USER-STATISTICS] time taken to retrieve user space is " + (end - init) + "ms");

			}catch(Exception e){

				_log.error("Unable to retrieve workspace information!");

			}
		}
		return storageInUse;

	}

	@Override
	public int getProfileStrength(String userid) {

		int profileStrenght = -1;

		// get the session
		ASLSession session = getASLSession();

		// username
		String userName = session.getUsername();

		// retrieve statistics of...
		String statisticsOfUsername = userName;

		if(userid != null && !userid.equals(userName))
			statisticsOfUsername = userid;

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(defaultUserId) == 0) {
			_log.debug("Found " + userName + " returning nothing");
			return profileStrenght;
		}else{

			// valuate profile strength
			if(isWithinPortal()){

				try{

					long init = System.currentTimeMillis();
					
					// check if the avatar is present
					boolean avatarPresent = (new LiferayUserManager().getUserAvatarBytes(statisticsOfUsername) != null);

					User user = UserLocalServiceUtil.getUserByScreenName(SiteManagerUtil.getCompany().getCompanyId(), statisticsOfUsername);
					profileStrenght = evaluateProfileStrenght(user, avatarPresent);

					long end = System.currentTimeMillis();
					_log.debug("[USER-STATISTICS] time taken to evaluate user profile strenght is " + (end - init) + "ms");

				}catch(Exception e){

					_log.error("Profile strenght evaluation failed!!" + e.toString(), e);

				}
			}
		}
		return profileStrenght;

	}

	@Override
	public UserInformation getUserSettings(String userid) {

		// get the session
		ASLSession session = getASLSession();

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
			
			_log.info("Is profile showable for user " + userid +  " " +  isProfileShowable);
		}

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(defaultUserId) == 0) {
			_log.debug("Found " + userName + " returning nothing");
			return null;
		}

		if(isWithinPortal()){
			// If the user is in the root panel (or is visiting a profile page), we have to send him the overall number of posts made, comments/likes(received) and the space in use.
			// Otherwise we have to filter on the vre.
			boolean isInfrastructure = isInfrastructureScope(userid);
			_log.debug("User scope is " + (isInfrastructure ? " the whole infrastucture " : " a VRE"));

			// get path of the avatar
			UserManager um = new LiferayUserManager();

			String thumbnailURL = null;
			try {
				thumbnailURL = um.getUserByUsername(statisticsOfUsername).getUserAvatarURL();
			} catch (UserManagementSystemException e) {
				_log.error("Unable to retrieve avatar url for user " + statisticsOfUsername, e);
			} catch (UserRetrievalFault e) {
				_log.error("Unable to retrieve avatar url for user " + statisticsOfUsername, e);
			}

			_log.debug(statisticsOfUsername + " avatar has url " + thumbnailURL);

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
		else return new UserInformation(true, null, userName, vreID, true, true);
	}

	/**
	 * Check privacy option for user's own statistics
	 * @param username
	 * @return
	 */
	private boolean checkUserPrivacyOption(String username) {
		try{

			// set permission checker
			setPermissionChecker();
			
			//needed to avoid cache use by liferay API
			CacheRegistryUtil.clear(); 

			User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), username);

			// the user has not decided yet
			if(!user.getExpandoBridge().hasAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY))
				return true;

			return (boolean)user.getExpandoBridge().getAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY);

		}catch(Exception e){

			_log.error("Unable to retrieve user's privacy option for his statistics");
			return true;
		}
	}

	@Override
	public PostsStatsBean getPostsStats(String userid){

		// get the session
		ASLSession session = getASLSession();

		//username
		String userName = session.getUsername();

		// retrieve statistics of...
		String statisticsOfUsername = userName;

		if(userid != null && !userid.equals(userName))
			statisticsOfUsername = userid;

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(defaultUserId) == 0) {
			_log.debug("Found " + userName + " returning nothing");
			return null;
		}

		long totalFeeds = 0, totalLikes = 0, totalComments = 0;

		// check if the user is or not in a VRE
		boolean isInfrastructure = isInfrastructureScope(userid);

		// date corresponding to one year ago
		Calendar oneYearAgo = Calendar.getInstance();
		oneYearAgo.set(Calendar.YEAR, oneYearAgo.get(Calendar.YEAR) - 1);

		_log.debug("Reference time is " + oneYearAgo.getTime());

		try {

			long init = System.currentTimeMillis();
			_log.debug("Getting " + statisticsOfUsername + " feeds in the last year.");

			List<Feed> userFeeds = store.getRecentFeedsByUserAndDate(statisticsOfUsername, oneYearAgo.getTime().getTime());

			_log.debug("Evaluating number of comments and likes of " + statisticsOfUsername + "'s feeds.");

			for (Feed feed : userFeeds) {

				try{

					// check if the user is in the root, if not check if the VRE of the feed is the current one
					if(!isInfrastructure && !feed.getVreid().equals(session.getScope()))
						continue;

					// increment feeds number
					totalFeeds ++;

					//increment number of post replies and likes
					totalComments += Integer.parseInt(feed.getCommentsNo());
					totalLikes += Integer.parseInt(feed.getLikesNo());

				}catch(NumberFormatException e){
					_log.error(e.toString());
				}
			}

			long end = System.currentTimeMillis();

			_log.debug("[USER-STATISTICS] time taken to retrieve and filter user feeds, get likes and replies got is " + (end - init) + "ms");
			_log.debug("Total number of feeds (after time filtering) of  " + statisticsOfUsername + " is " + totalFeeds);
			_log.debug("Total number of likes (after time filtering) for " + statisticsOfUsername + " is " + totalLikes);
			_log.debug("Total number of comments (after time filtering) for " + statisticsOfUsername + " is " + totalComments);

		}catch(Exception e){
			_log.error(e.toString());
			return null;
		}

		// return the object
		return new PostsStatsBean(totalFeeds, totalLikes, totalComments);
	}

	@Override
	public void setShowMyOwnStatisticsToOtherPeople(boolean show) {

		if(isWithinPortal()){

			ASLSession session = getASLSession();

			String username = session.getUsername();

			//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
			//this check just return nothing if that happens
			if (username.compareTo(defaultUserId) == 0) {
				_log.debug("Found " + username + " returning nothing");
				return;
			}

			try{

				// set permission checker
				setPermissionChecker();
				
				//needed to avoid cache use by liferay API
				CacheRegistryUtil.clear(); 

				_log.debug("User " + username + (show ? " want to show " : " doesn't want to show ") + " his statistics");

				User user = UserLocalServiceUtil.getUserByScreenName(ManagementUtils.getCompany().getCompanyId(), username);
				boolean hasAttribute = user.getExpandoBridge().hasAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY);

				if(hasAttribute){

					// set the new value
					_log.debug("Setting custom field value to "  + show + " for user " + username);

					// set the current value
					user.getExpandoBridge().setAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY, show);

				}

			}catch(Exception e){
				_log.error("Unable to check user's privacy for his statistics", e);
			}
		}

	}

	/**
	 * On servlet instanciation, create the custom field and set it to startingValue
	 * @param customFieldNameUserStatisticsVisibility
	 * @param b
	 */
	private void createUserCustomField(
			String customFieldNameUserStatisticsVisibility, boolean startingValue) {

		// set permission checker
		setPermissionChecker();

		try{

			User defaultUser = UserLocalServiceUtil.getDefaultUser(ManagementUtils.getCompany().getCompanyId());

			// check if it exists
			boolean exists = defaultUser.getExpandoBridge().hasAttribute(customFieldNameUserStatisticsVisibility);

			if(exists){

				_log.debug("Custom field already exists... There is no need to create it");

			}else{

				_log.debug("Creating custom field " + customFieldNameUserStatisticsVisibility + 
						" with starting value " + startingValue);

				// create
				defaultUser.getExpandoBridge().addAttribute(CUSTOM_FIELD_NAME_USER_STATISTICS_VISIBILITY, ExpandoColumnConstants.BOOLEAN, (Serializable)(true)); 

			}

		}catch(Exception e){
			_log.error("Unable to create custom field " + customFieldNameUserStatisticsVisibility);
		}		
	}

	/**
	 * returns dynamically the formated size.
	 *
	 * @param size the size
	 * @return the string
	 */
	private static String formatFileSize(long size) {
		String formattedSize = null;

		double b = size;
		double k = size/1024.0;
		double m = ((size/1024.0)/1024.0);
		double g = (((size/1024.0)/1024.0)/1024.0);
		double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

		DecimalFormat dec = new DecimalFormat("0.00");

		if ( t >= 1.0 ) {
			formattedSize = dec.format(t).concat(" TB");
		} else if ( g >= 1.0 ) {
			formattedSize = dec.format(g).concat(" GB");
		} else if ( m >= 1.0 ) {
			formattedSize = dec.format(m).concat(" MB");
		} else if ( k >= 1.0 ) {
			formattedSize = dec.format(k).concat(" KB");
		} else {
			formattedSize = dec.format(b).concat(" Bytes");
		}
		return formattedSize;
	}

	/**
	 * Evaluates the profile strenght of the user
	 * @param user 
	 * @return a int in [0, 100]
	 */
	private static int evaluateProfileStrenght(User user, boolean imageIsPresent) {

		int score = evaluateContactScore(user);
		score += evaluateInformationScore(user, imageIsPresent);

		return score;
	}


	/**
	 * Evaluates a score according to the information of the user such as job, organization, comments
	 * @param user
	 * @return a score in [0, 65]
	 */
	private static int evaluateInformationScore(User user, boolean imageIsPresent) {
		int score = 0;

		if(user.getJobTitle() != null)
			score += !user.getJobTitle().isEmpty() ? 20 : 0;
		if(user.getOpenId() != null)
			score += !user.getOpenId().isEmpty() ? 20 : 0;
		String summary = getSummary(user);
		if(summary != null){
			int lenght = summary.replace(" ", "").length(); 
			float partialScore = ((float)lenght / 10.0f);
			score +=  partialScore  > 20f ? 20 : (int)partialScore;
		}

		if(imageIsPresent)
			score += 5;

		return score;
	}

	/**
	 * get the user's comment
	 * @param user
	 * @return
	 */
	private static String getSummary(User user) {
		if(user.getComments() != null){
			String toReturn = escapeHtml(user.getComments());
			// replace all the line breaks by <br/>
			toReturn = toReturn.replaceAll("(\r\n|\n)"," <br/> ");
			// then replace all the double spaces by the html version &nbsp;
			toReturn = toReturn.replaceAll("\\s\\s","&nbsp;&nbsp;");
			return toReturn;
		}else
			return null;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private static String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	/**
	 * Evaluates user's contact information
	 * @param user
	 * @return a value in [0, 35]
	 */
	private static int evaluateContactScore(User user){

		int score = 0;

		try{
			Contact contact = user.getContact();

			if(contact.getMySpaceSn() != null)
				score += !contact.getMySpaceSn().isEmpty() ? 5 : 0;
			if(contact.getTwitterSn() != null)
				score += !contact.getTwitterSn().isEmpty() ? 5 : 0;
			if(contact.getFacebookSn() != null)
				score += !contact.getFacebookSn().isEmpty() ? 5 : 0;
			if(contact.getSkypeSn() != null)
				score += !contact.getSkypeSn().isEmpty() ? 5 : 0;
			if(contact.getJabberSn() != null)
				score += !contact.getJabberSn().isEmpty() ? 5 : 0;
			if(contact.getAimSn() != null)
				score += !contact.getAimSn().isEmpty()  ? 5 : 0;

			List<Website> websites = WebsiteLocalServiceUtil.getWebsites(user.getCompanyId(), "com.liferay.portal.model.Contact", contact.getContactId());
			score += websites.size() > 0 ? 5 : 0; 
		}catch(Exception e ){

			_log.error("Contact profile score evaluation failed!!");
			score = 0;
		}

		return score;
	}

	/**
	 * Set the permission checker to set/get custom fields into liferay
	 */
	private void setPermissionChecker(){

		// set permission checker
		try{
			long adminId = LiferayUserManager.getAdmin().getUserId();
			PrincipalThreadLocal.setName(adminId);
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(adminId));
			PermissionThreadLocal.setPermissionChecker(permissionChecker); 
		}catch(Exception e){
			_log.error("Unable to set permission checker. Custom fields set/get operations are likely to fail...");
		}

	}
}

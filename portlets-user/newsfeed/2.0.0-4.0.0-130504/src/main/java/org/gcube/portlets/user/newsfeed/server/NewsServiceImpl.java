package org.gcube.portlets.user.newsfeed.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Attachment;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.Like;
import org.gcube.portal.databook.shared.RangeFeeds;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portal.databook.shared.ex.ColumnNameNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedIDNotFoundException;
import org.gcube.portal.databook.shared.ex.FeedTypeNotFoundException;
import org.gcube.portal.databook.shared.ex.LikeIDNotFoundException;
import org.gcube.portal.databook.shared.ex.PrivacyLevelTypeNotFoundException;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.gcube.portal.notifications.thread.CommentNotificationsThread;
import org.gcube.portal.notifications.thread.LikeNotificationsThread;
import org.gcube.portal.notifications.thread.MentionNotificationsThread;
import org.gcube.portlets.user.newsfeed.client.NewsService;
import org.gcube.portlets.user.newsfeed.shared.MoreFeedsBean;
import org.gcube.portlets.user.newsfeed.shared.NewsConstants;
import org.gcube.portlets.user.newsfeed.shared.OperationResult;
import org.gcube.portlets.user.newsfeed.shared.UserSettings;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;
import org.gcube.social_networking.socialutillibrary.Utils;
import org.gcube.socialnetworking.social_data_search_client.ElasticSearchClientImpl;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class NewsServiceImpl extends RemoteServiceServlet implements NewsService {

	private static final Logger _log = LoggerFactory.getLogger(NewsServiceImpl.class);

	private static final String VRE_LABEL = "VRE_LABEL";
	private static final String SHOW_TIMELINE_SOURCE = "SHOW_TIMELINE_SOURCE";
	private static final String REFRESH_TIME = "REFRESH_TIME";

	private static final String SESSION_ADMIN_ATTR = "SESSION_ADMIN_ATTR";
	private static final String USER_SETTINGS_ATTR = "USER_SETTINGS_ATTR";

	public static final String TEST_SCOPE = "/gcube/devsec/devVRE";

	private String APP_ID;

	/**
	 * Cassandra client
	 */
	private DatabookStore store;

	/**
	 * Elasticsearch client
	 */
	private ElasticSearchClientImpl el;

	private final static int MAX_FEEDS_NO = 45;

	public void init() {
		store = new DBCassandraAstyanaxImpl();

		try {
			el = new ElasticSearchClientImpl(null);
			_log.info("Elasticsearch connection created");
		} catch (Exception e) {
			el = null;
			_log.error("Unable to create elasticsearch client connection!!!", e);
		}

		APP_ID = this.getClass().getName();
	}

	public void destroy() {
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
			_log.warn("USER IS NULL setting testing user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(TEST_SCOPE);
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = NewsConstants.TEST_USER;
		//		user = "costantino.perciante";
		return user;
	}
	/**
	 * 
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
	 * this is the first method called by the web app
	 */
	@Override
	public UserSettings getUserSettings() {
		if (getUserSettingsFromSession() != null)
			return getUserSettingsFromSession();
		try {
			ASLSession session = getASLSession();
			String username = session.getUsername();
			String email = username+"@isti.cnr.it";
			String fullName = username+" FULL";
			String thumbnailURL = "images/Avatar_default.png";

			boolean isDevelopment = false;
			try {
				UserLocalServiceUtil.getService();
			} 
			catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {
				isDevelopment = true;
			}

			if (username.compareTo(NewsConstants.TEST_USER) != 0 && !isDevelopment) {
				UserManager um = new LiferayUserManager();
				GCubeUser user = um.getUserByUsername(username);

				thumbnailURL = user.getUserAvatarURL();
				fullName = user.getFullname();
				email = user.getEmail();
				final String profilePageURL = 
						GCubePortalConstants.PREFIX_GROUP_URL + 
						PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest())+
						GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;
				String accountURL = profilePageURL;

				try {
					accountURL = "";
				}catch (NullPointerException e) {
					e.printStackTrace();
				}
				UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, user.getEmail(), accountURL, true, isAdmin(), null);
				CustomConfiguration config = getUserConfiguration();
				UserSettings toReturn = new UserSettings(userInfo, config.getRefreshTime(), session.getScopeName(), config.getVreLabel(), isInfrastructureScope(), config.isShowTimelineSource());
				setUserSettingsInSession(toReturn);
				return toReturn;
			}
			else {
				_log.info("Returning test USER");
				CustomConfiguration config = getUserConfiguration();
				UserInfo user = new UserInfo(session.getUsername(), fullName, thumbnailURL, email, "fakeAccountUrl", true, false, null);
				return new UserSettings(user, config.getRefreshTime(), session.getScopeName(), config.getVreLabel(), isInfrastructureScope(), config.isShowTimelineSource());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new UserSettings();
	}

	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope() {
		boolean toReturn = false;
		try {
			ScopeBean scope = new ScopeBean(getASLSession().getScope());
			toReturn = scope.is(Type.INFRASTRUCTURE);
			return toReturn;
		}
		catch (NullPointerException e) {
			_log.error("NullPointerException in isInfrastructureScope returning false");
			return false;
		}		
	}

	@Override
	public ArrayList<EnhancedFeed> getAllUpdateUserFeeds(int feedsNoPerCategory) {
		String userName = getASLSession().getUsername();
		ArrayList<Feed> toMerge = new ArrayList<Feed>();
		HashMap<String, Feed> feedsMap = new HashMap<String, Feed>();
		try {
			if (!isWithinPortal()) {
				return getEclipseResult(userName, feedsNoPerCategory, false);
			}
			else {
				//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
				//this check just return nothing if that happens
				if (userName.compareTo("test.user") == 0) {
					_log.debug("Found " + userName + " returning nothing");
					return null;
				}

				_log.info("****** retrieving posts for user: " + userName);
				UserManager um = new LiferayUserManager();
				GCubeUser currUser = um.getUserByUsername(userName);
				/**
				 * this handles the case where the portlet is deployed outside of VREs (regular)
				 */
				if (isInfrastructureScope()) {
					_log.info("****** risInfrastructureScope() = true");
					//VRE Feeds
					GroupManager gm = new LiferayGroupManager();
					for (GCubeGroup group : gm.listGroupsByUser(currUser.getUserId())) {		
						if (gm.isVRE(group.getGroupId())) {
							String vreid = gm.getInfrastructureScope(group.getGroupId()); //get the scope 
							_log.info("Reading feeds for VRE: " + vreid);
							ArrayList<Feed> OrganizationFeeds =  (ArrayList<Feed>) store.getRecentFeedsByVRE(vreid, feedsNoPerCategory); 
							for (Feed feed : OrganizationFeeds) {
								feedsMap.put(feed.getKey(), feed);							
							}
						}
					}

					//Portal Feeds
					ArrayList<Feed> portalFeeds = (ArrayList<Feed>) store.getAllPortalPrivacyLevelFeeds();	
					for (Feed feed : portalFeeds) {
						feedsMap.put(feed.getKey(), feed);
					}
				}
				//else must be in a VRE scope
				else {
					String vreid = getASLSession().getScopeName();
					_log.trace("News Feed in VRE, Reading feeds for VRE: " + vreid);
					ArrayList<Feed> OrganizationFeeds =  (ArrayList<Feed>) store.getRecentFeedsByVRE(vreid, (NewsConstants.FEEDS_MAX_PER_CATEGORY)); 
					for (Feed feed : OrganizationFeeds) {
						feedsMap.put(feed.getKey(), feed);							
					}
				}

				for (String key: feedsMap.keySet()) {
					toMerge.add(feedsMap.get(key));
				}				
			}
			//sort the feeds in reverse chronological order
			Collections.sort(toMerge, Collections.reverseOrder());

			ArrayList<Feed> toReturn = new ArrayList<Feed>();
			//return only <MAX_FEEDS_NO> feeds
			if (toMerge.size() > MAX_FEEDS_NO) 
				for (int i = 0; i < MAX_FEEDS_NO; i++) 
					toReturn.add(toMerge.get(i));
			else {
				return enhanceFeeds(toMerge, 2);
			}
			return enhanceFeeds(toReturn, 2);
		} catch (PrivacyLevelTypeNotFoundException e) {
			_log.error("Privacy Level not Found " + e.getMessage());
			e.printStackTrace();
		} catch (FeedTypeNotFoundException e) {
			_log.error("Feed Type not Found " + e.getMessage());
			e.printStackTrace();
		} catch (ColumnNameNotFoundException e) {
			_log.error("Column name not Found " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * return the feeds having the hashtag passed as param the user has access on
	 * @param hashtag the hashtag to look for including '#'
	 */
	@Override
	public ArrayList<EnhancedFeed> getFeedsByHashtag(String hashtag)  {
		ASLSession session = getASLSession();
		String userName = session.getUsername();
		ArrayList<Feed> toMerge = new ArrayList<Feed>();
		HashMap<String, Feed> feedsMap = new HashMap<String, Feed>();
		try {
			//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
			//this check just return nothing if that happens
			if (userName.compareTo("test.user") == 0) {
				_log.debug("Found " + userName + " returning nothing");
				return null;
			}

			String lowerCaseHashtag = hashtag.toLowerCase();

			/**
			 * this handles the case where the portlet is deployed outside of VREs (regular)
			 */
			if (isInfrastructureScope()) {
				UserManager um = new LiferayUserManager();
				GCubeUser currUser = um.getUserByUsername(userName);
				//VRE Feeds
				GroupManager gm = new LiferayGroupManager();
				for (GCubeGroup group : gm.listGroupsByUser(currUser.getUserId())) {		
					if (gm.isVRE(group.getGroupId())) {
						String vreid = gm.getInfrastructureScope(group.getGroupId()); //get the scope 
						ArrayList<Feed> feeds =  (ArrayList<Feed>) store.getVREFeedsByHashtag(vreid, lowerCaseHashtag);
						for (Feed feed : feeds) {
							feedsMap.put(feed.getKey(), feed);							
						}
					}
				}				
			}
			//else must be in a VRE scope
			else {
				String vreid = session.getScopeName();
				ArrayList<Feed> feeds =  (ArrayList<Feed>) store.getVREFeedsByHashtag(vreid, lowerCaseHashtag);
				for (Feed feed : feeds) {
					feedsMap.put(feed.getKey(), feed);							
				}
			}

			for (String key: feedsMap.keySet()) {
				toMerge.add(feedsMap.get(key));
			}				

			//sort the feeds in reverse chronological order
			Collections.sort(toMerge, Collections.reverseOrder());

			ArrayList<Feed> toReturn = new ArrayList<Feed>();
			//return only <MAX_FEEDS_NO> feeds
			if (toMerge.size() > MAX_FEEDS_NO) 
				for (int i = 0; i < MAX_FEEDS_NO; i++) 
					toReturn.add(toMerge.get(i));
			else {
				return enhanceFeeds(toMerge, 2);
			}
			return enhanceFeeds(toReturn, 2);
		} catch (PrivacyLevelTypeNotFoundException e) {
			_log.error("Privacy Level not Found " + e.getMessage());
			e.printStackTrace();
		} catch (FeedTypeNotFoundException e) {
			_log.error("Feed Type not Found " + e.getMessage());
			e.printStackTrace();
		} catch (ColumnNameNotFoundException e) {
			_log.error("Column name not Found " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ArrayList<EnhancedFeed> getFeedsByQuery(String query, int from, int quantity) {

		// TODO : check this error better
		if(el == null){

			_log.debug("There is no connection to elasticsearch, sorry.");
			return null;

		}

		ASLSession session = getASLSession();
		String userName = session.getUsername();
		try {
			//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
			//this check just return nothing if that happens
			if (userName.compareTo("test.user") == 0) {
				_log.debug("Found " + userName + " returning nothing");
				return null;
			}

			// Retrieve user's vres in which we must search
			Set<String> vres = new HashSet<String>();

			if (isInfrastructureScope()) {
				UserManager um = new LiferayUserManager();
				GCubeUser currUser = um.getUserByUsername(userName);
				//VRE Feeds
				GroupManager gm = new LiferayGroupManager();
				for (GCubeGroup group : gm.listGroupsByUser(currUser.getUserId())) {		
					if (gm.isVRE(group.getGroupId())) {
						String vreid = gm.getInfrastructureScope(group.getGroupId()); //get the scope		
						vres.add(vreid);
					}
				}				
			}
			//else must be in a VRE scope
			else {
				vres.add(session.getScopeName());
			}			

			// query elastic search
			List<EnhancedFeed> enhancedFeeds = el.searchInEnhancedFeeds(query, vres, from, quantity);

			// retrieve the ids of liked feeds by the user
			List<String> likedFeeds = store.getAllLikedFeedIdsByUser(userName);

			// update fields "liked" and "isuser"
			for (EnhancedFeed enhancedFeed : enhancedFeeds) {

				if(isUsers(enhancedFeed.getFeed(), userName))
					enhancedFeed.setUsers(true);

				if(likedFeeds.contains(enhancedFeed.getFeed().getKey()))
					enhancedFeed.setLiked(true);

			}

			return (ArrayList<EnhancedFeed>) enhancedFeeds;

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * return only the user connection feeds
	 */
	@Override
	public ArrayList<EnhancedFeed> getOnlyConnectionsUserFeeds() {
		ArrayList<Feed> toMerge = new ArrayList<Feed>();
		HashMap<String, Feed> feedsMap = new HashMap<String, Feed>();
		String userName = getASLSession().getUsername();
		try {
			if (! isWithinPortal()) {
				return getEclipseResult(userName, NewsConstants.FEEDS_NO_PER_CATEGORY, true);
			}
			else {
				//UserFriends Feeds
				ArrayList<String> userFriendsIds = (ArrayList<String>)store.getFriends(userName);
				for (String userid : userFriendsIds) {
					for (Feed feed : store.getRecentFeedsByUser(userid, NewsConstants.FEEDS_NO_PER_CATEGORY)) {
						feedsMap.put(feed.getKey(), feed);
					}
				}
				for (String key: feedsMap.keySet()) {
					toMerge.add(feedsMap.get(key));
				}
				Collections.sort(toMerge, Collections.reverseOrder());
				ArrayList<Feed> toReturn = new ArrayList<Feed>();
				//return only <MAX_FEEDS_NO> feeds
				if (toMerge.size() > MAX_FEEDS_NO) 
					for (int i = 0; i < MAX_FEEDS_NO; i++) 
						toReturn.add(toMerge.get(i));
				else
					return enhanceFeeds(toMerge, 2);		
			}
		} catch (PrivacyLevelTypeNotFoundException e) {
			_log.error("Privacy Level not Found " + e.getMessage());
			e.printStackTrace();
		} catch (FeedTypeNotFoundException e) {
			_log.error("Feed Type not Found " + e.getMessage());
			e.printStackTrace();
		} catch (ColumnNameNotFoundException e) {
			_log.error("Column name not Found " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * return only one feed with all the comments
	 */
	@Override
	public EnhancedFeed getSingleFeed(String feedKey) {
		Feed feed = null;
		try {
			if (feedKey != null) {
				feed = store.readFeed(feedKey);
				if (feed != null) {
					ArrayList<Feed> toEnhance = new ArrayList<Feed>();
					toEnhance.add(feed);
					return enhanceFeeds(toEnhance, -1).get(0); //-1 all the comments
				}
			}
		} catch (Exception e) {
			_log.debug("Error while trying to fetch feed with key " + feedKey + " returning nothing");
			return new EnhancedFeed();
		}
		return new EnhancedFeed();
	}
	/**
	 * MoreFeedsBean contains the timeline index of the last returned valid feed (because if you delete a feed is stays on in the timeline and is marked deleted)
	 * and contains the Feeds 
	 * @param strat the range start (most recent feeds for this vre) has to be greater than 0
	 * @param quantity the number of most recent feeds for this vre starting from "start" param
	 */
	@Override
	public MoreFeedsBean getMoreFeeds(int start, int quantity) {
		ArrayList<Feed> toMerge = new ArrayList<Feed>();
		HashMap<String, Feed> feedsMap = new HashMap<String, Feed>();
		String vreid = getASLSession().getScope();
		_log.debug("\n\nAsking more feed for Timeline " + vreid + " from " + start + " get other " + quantity);
		ArrayList<Feed> organizationFeeds;
		RangeFeeds rangeFeeds = null;
		try {
			rangeFeeds = store.getRecentFeedsByVREAndRange(vreid, start, quantity);
			organizationFeeds = rangeFeeds.getFeeds();
			for (Feed feed : organizationFeeds) {
				feedsMap.put(feed.getKey(), feed);		
				//System.out.println("->\n"+feed.getDescription());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		} 

		for (String key: feedsMap.keySet()) {
			toMerge.add(feedsMap.get(key));
		}
		//sort the feeds in reverse chronological order
		Collections.sort(toMerge, Collections.reverseOrder());
		ArrayList<EnhancedFeed> toReturn = enhanceFeeds(toMerge, 2);
		return new MoreFeedsBean(rangeFeeds.getLastReturnedFeedTimelineIndex(), toReturn);
	}
	/**
	 * just for testing purposes
	 * 
	 * @param userName
	 * @return
	 * @throws PrivacyLevelTypeNotFoundException
	 * @throws FeedTypeNotFoundException
	 * @throws ColumnNameNotFoundException
	 * @throws FeedIDNotFoundException 
	 */
	private ArrayList<EnhancedFeed> getEclipseResult(String userName, int feedsNoPerCategory, boolean onlyConnections) throws PrivacyLevelTypeNotFoundException, FeedTypeNotFoundException, ColumnNameNotFoundException, FeedIDNotFoundException {
		ArrayList<Feed> toMerge = new ArrayList<Feed>();
		HashMap<String, Feed> feedsMap = new HashMap<String, Feed>();

		ArrayList<Feed> OrganizationFeeds =  (ArrayList<Feed>) store.getRecentFeedsByVRE(TEST_SCOPE, feedsNoPerCategory*3); 
		for (Feed feed : OrganizationFeeds) {
			feedsMap.put(feed.getKey(), feed);
			_log.trace("Reading desc: " + feed.getDescription());
		}

		//		if (! onlyConnections) {
		//			//User Own Feeds
		//			ArrayList<Feed> userFeeds = (ArrayList<Feed>) store.getRecentFeedsByUser(userName, 10);	
		//			for (Feed feed : userFeeds) 
		//				feedsMap.put(feed.getKey(), feed);			
		//			//			//Portal Feeds
		//			ArrayList<Feed> portalFeeds = (ArrayList<Feed>) store.getAllPortalPrivacyLevelFeeds();	
		//			for (Feed feed : portalFeeds) 
		//				feedsMap.put(feed.getKey(), feed);			
		//		}
		//		//UserFriends Feeds
		//		ArrayList<String> userFriendsIds = (ArrayList<String>)store.getFriends(userName);
		//		for (String userid : userFriendsIds) {
		//			for (Feed feed : store.getRecentFeedsByUser(userid, 10)) {
		//				feedsMap.put(feed.getKey(), feed);
		//			}
		//		}
		for (String key: feedsMap.keySet()) {
			toMerge.add(feedsMap.get(key));
		}
		for (Feed feed : toMerge) {
			feed.setThumbnailURL("http://127.0.0.1:8888/images/Avatar_default.png");
		}
		//sort the feeds in reverse chronological order
		Collections.sort(toMerge, Collections.reverseOrder());
		return enhanceFeeds(toMerge, 2);
	}

	@Override
	public ArrayList<EnhancedFeed> getOnlyMyUserFeeds() {
		String userName = getASLSession().getUsername();
		_log.trace("getOnly UserFeeds for " + userName);
		ArrayList<Feed> userFeeds = null;
		try {
			userFeeds = (ArrayList<Feed>) store.getRecentFeedsByUser(userName, 15);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Collections.sort(userFeeds, Collections.reverseOrder());
		return enhanceFeeds(userFeeds, 2);
	}

	@Override
	public ArrayList<EnhancedFeed> getOnlyLikedFeeds() {
		String userName = getASLSession().getUsername();
		_log.trace("getLiked Feeds for " + userName);
		ArrayList<Feed> userFeeds = null;
		try {
			userFeeds = (ArrayList<Feed>) store.getAllLikedFeedsByUser(userName, 25);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Collections.sort(userFeeds, Collections.reverseOrder());
		return enhanceFeeds(userFeeds, 2);
	}

	@Override
	public boolean like(String feedid, String feedText, String feedOwnerId) {
		boolean likeCommitResult = false;
		UserInfo user = getUserSettings().getUserInfo();
		String username = user.getUsername();
		if (username.compareTo(NewsConstants.TEST_USER) == 0) {
			return false;
		}

		Like toLike = new Like(UUID.randomUUID().toString(), user.getUsername(), 
				new Date(), feedid, user.getFullName(), user.getAvatarId());
		try {
			likeCommitResult = store.like(toLike);
		} catch (FeedIDNotFoundException e) {
			_log.error("Feed not Found for this like " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		//if the like was correctly delivered notify users involved
		if (likeCommitResult) {
			//if the user who liked this post is not the user who posted it notify the poster user (Feed owner) 
			if (! user.getUsername().equals(feedOwnerId)) {
				NotificationsManager nm = new ApplicationNotificationsManager(
						new SocialNetworkingSite(getThreadLocalRequest()), 
						getASLSession().getScopeName(), 
						new SocialNetworkingUser(username, user.getEmailaddress(), user.getFullName(), user.getAvatarId()),
						APP_ID);
				boolean nResult = nm.notifyLikedFeed(feedOwnerId, feedid, Utils.escapeHtml(feedText));
				_log.trace("Like Notification added? " + nResult);
			}
		}
		return likeCommitResult;
	}

	@Override
	public boolean unlike(String feedid, String feedText, String feedOwnerId) {		
		UserInfo user = getUserSettings().getUserInfo();		
		try {
			for (Like like : store.getAllLikesByFeed(feedid)) {
				if (like.getUserid().compareTo(user.getUsername()) == 0) {
					_log.trace("Trying unlike of " + feedText + " for " + user.getFullName());
					store.unlike(user.getUsername(), like.getKey(), feedid);
					return true;
				}
			}	
		} catch (FeedIDNotFoundException | PrivacyLevelTypeNotFoundException | FeedTypeNotFoundException | ColumnNameNotFoundException | LikeIDNotFoundException e) {
			_log.error("Either Feed or Like not Found " + e.getMessage());
			e.printStackTrace();
			return false;
		}		
		return false;
	}
	/**
	 * @param feedid the id of the commented feed
	 * @param commentText the comment text
	 * @param feedOwnerId the username of the user who created the post that was commented
	 */
	@Override
	public OperationResult comment(String feedid, String commentText, HashSet<String> mentionedUserFullNames, String feedOwnerId, boolean isAppFeed) {
		boolean commentCommitResult = false;
		_log.trace("Trying to add this comment " + commentText);
		UserInfo user = getUserSettings().getUserInfo();

		if (user.getUsername().compareTo(NewsConstants.TEST_USER) == 0) {
			return new OperationResult(false, "Session Expired", null);
		}

		String escapedCommentText = Utils.escapeHtmlAndTransformUrl(commentText);

		//copy the set into a list
		ArrayList<String> mentionedUserFullNamesList = new ArrayList<String>();
		mentionedUserFullNamesList.addAll(mentionedUserFullNames);

		ArrayList<ItemBean> mentionedUsers = null; 
		if (mentionedUserFullNames != null && ! mentionedUserFullNames.isEmpty()) {
			mentionedUsers = getSelectedUserIds(mentionedUserFullNamesList);
			escapedCommentText = Utils.convertMentionPeopleAnchorHTML(escapedCommentText, mentionedUsers, getThreadLocalRequest());
		}		

		Comment comment = new Comment(UUID.randomUUID().toString(), user.getUsername(), 
				new Date(), feedid, escapedCommentText, user.getFullName(), user.getAvatarId());
		try {
			if (store.addComment(comment)) 
				commentCommitResult = true;
		} catch (FeedIDNotFoundException e) {
			_log.error("Related post not found for this comment " + e.getMessage());
			e.printStackTrace();
			return new OperationResult(false, "Related post not found for this comment", comment);
		}
		//if the comment was correctly delivered && is not an app feed notify users involved
		if (commentCommitResult && isWithinPortal()) {
			//if the user who commented this post is not the user who posted it notify the poster user (Feed owner) 
			NotificationsManager nm = new ApplicationNotificationsManager(
					new SocialNetworkingSite(getThreadLocalRequest()), 
					getASLSession().getScopeName(), 
					new SocialNetworkingUser(user.getUsername(), user.getEmailaddress(), user.getFullName(), user.getAvatarId()),
					APP_ID);
			if (! user.getUsername().equals(feedOwnerId) && (!isAppFeed)) {				
				boolean result = nm.notifyOwnCommentReply(feedOwnerId, feedid, escapedCommentText, comment.getKey());
				_log.trace("Comment Notification to post owner added? " + result);
			} 

			//if there are users who liked this post they get notified, asynchronously with this thread
			ArrayList<Like> favorites = getAllLikesByFeed(feedid);
			Thread likesThread = new Thread(new LikeNotificationsThread(commentText, nm, favorites, feedOwnerId, comment.getKey()));
			likesThread.start();

			//notify the other users who commented this post (excluding the ones above)
			Thread commentsNotificationthread = new Thread(new CommentNotificationsThread(store, user.getUsername(), comment.getFeedid(), escapedCommentText, nm, feedOwnerId, comment.getKey(), favorites));
			commentsNotificationthread.start();	

			//send the notification to the mentioned users, if any
			if (mentionedUsers != null && mentionedUsers.size() > 0) {
				ArrayList<GenericItemBean> toPass = new ArrayList<GenericItemBean>();


				// among the mentionedUsers there could be groups of people
				Map<String, ItemBean> uniqueUsersToNotify = new HashMap<>();
				UserManager um = new LiferayUserManager();

				for (ItemBean bean : mentionedUsers) {

					if(bean.isItemGroup()){

						// retrieve the users of this group
						try {
							List<GCubeUser> teamUsers = um.listUsersByTeam(Long.parseLong(bean.getId()));

							for (GCubeUser userTeam : teamUsers) {
								if(!uniqueUsersToNotify.containsKey(userTeam.getUsername()))
									uniqueUsersToNotify.put(userTeam.getUsername(), new ItemBean(userTeam.getUserId()+"",
											userTeam.getUsername(), userTeam.getFullname(), userTeam.getUserAvatarURL()));
							}

						} catch (NumberFormatException
								| UserManagementSystemException
								| TeamRetrievalFault | UserRetrievalFault e) {
							_log.error("Unable to retrieve team information", e);
						}

					}else{
						// it is a user, just add to the hashmap
						if(!uniqueUsersToNotify.containsKey(bean.getName()))
							uniqueUsersToNotify.put(bean.getName(), bean);

					}
				}

				// iterate over the hashmap
				Iterator<Entry<String, ItemBean>> userMapIterator = uniqueUsersToNotify.entrySet().iterator();
				while (userMapIterator.hasNext()) {
					Map.Entry<String, ItemBean> userEntry = (Map.Entry<String, ItemBean>) userMapIterator
							.next();
					ItemBean userBean = userEntry.getValue();
					toPass.add(new GenericItemBean(userBean.getId(), userBean.getName(), userBean.getAlternativeName(), userBean.getThumbnailURL()));
				}

				Thread thread = new Thread(new MentionNotificationsThread(comment.getFeedid(), commentText, nm, null, toPass));
				thread.start();
			}
		}
		return new OperationResult(true, "OK", comment);
	}

	@Override
	public OperationResult editComment(Comment toEdit) {
		UserInfo user = getUserSettings().getUserInfo();
		if (user.getUsername().compareTo(NewsConstants.TEST_USER) == 0) {
			return new OperationResult(false, "Session Expired", null);
		}		

		String escapedCommentText = Utils.escapeHtmlAndTransformUrl(toEdit.getText());

		Comment edited = new Comment(toEdit.getKey(), toEdit.getUserid(), 
				toEdit.getTime(), toEdit.getFeedid(), escapedCommentText, user.getFullName(), user.getAvatarId(), true, new Date());
		try {
			store.editComment(edited);
		} catch (Exception e) {
			e.printStackTrace();
			return new OperationResult(false, "Exception on the server: " + e.getMessage(), null);
		} 
		return new OperationResult(true, "OK", edited);
	}	

	/**
	 * this method sorts the Feeds in Chronological Reversed order and adds additional informations such as comments and attachments
	 * @param toEnhance
	 * @param } catch (Exception e) { the max number of comments you want to get back, -1 to get all
	 *
	 * @return
	 */
	private ArrayList<EnhancedFeed> enhanceFeeds(ArrayList<Feed> toEnhance, int commentsNumberPerFeed) {
		ArrayList<EnhancedFeed> toReturn = new ArrayList<EnhancedFeed>();
		ASLSession session = getASLSession();
		String username = session.getUsername();
		
		
		//patch needed for maintaining mention link backward compatibility (they point to /group/data-e-infrastructure-gateway/profile)
		final String LINK_TO_REPLACE = "data-e-infrastructure-gateway";
		String siteLandinPagePath = PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest());
		String tokenTosubstitute = siteLandinPagePath.replace("/group/", "");

		ArrayList<String> likedFeeds = (ArrayList<String>) store.getAllLikedFeedIdsByUser(getASLSession().getUsername());
		boolean skip = false;
		for (Feed feed : toEnhance) {
			//patch needed for maintaining mention link backward compatibility (they point to /group/data-e-infrastructure-gateway/profile)
			String currPostText = feed.getDescription().replace(LINK_TO_REPLACE,tokenTosubstitute);
			feed.setDescription(currPostText);
			boolean isMultiFileUpload = feed.isMultiFileUpload();
			ArrayList<Attachment> attachments = new ArrayList<Attachment>();
			if (isMultiFileUpload) {
				try {
					attachments = (ArrayList<Attachment>) store.getAttachmentsByFeedId(feed.getKey());
				} catch (FeedIDNotFoundException e) {
					_log.error("It looks like sth wrong with this feedid having attachments, could not find feedId = " + feed.getKey() + "\n" + e.getMessage());
				}
			}			

			skip = false;
			if (! feed.isApplicationFeed()) {
				String thumb = getUserImagePortraitUrlLocal(feed.getEntityId());
				if (thumb == null) {
					_log.warn(feed.getEntityId() + " is not avaialble on this portal, skipping this feed: " + feed.getKey());
					skip = true;
				} else
					feed.setThumbnailURL(thumb);
			}
			//if likedFeeds contains this feed key it means the user already Liked it
			boolean liked = likedFeeds.contains(feed.getKey());
			int commentsNo = 0;
			try {
				commentsNo = Integer.parseInt(feed.getCommentsNo());
			}
			catch (NumberFormatException e) {
				commentsNo = 0;
				_log.error("NumberFormatException while reading comments number " + e.getMessage());
			}
			if (!skip) {
				if (commentsNo == 0) {
					EnhancedFeed toAdd = null;
					//create the enhanced feed
					if (feed.isApplicationFeed()) {
						toAdd = new EnhancedFeed(feed, liked, checkisAdminUser()); 
					} else
						toAdd = new EnhancedFeed(feed, liked, isUsers(feed, username)); 
					toAdd.setAttachments(attachments);
					toReturn.add(toAdd);
				} else {
					ArrayList<Comment> comments = getAllCommentsByFeed(feed.getKey());
					//sort in chronological order
					Collections.sort(comments);

					int currCommentsNumber = comments.size();
					//if comments are less than $commentsNumberPerFeed they are the more recent, -1 return all the comments
					if (currCommentsNumber < commentsNumberPerFeed || commentsNumberPerFeed == -1) {
						EnhancedFeed toAdd = new EnhancedFeed(feed, liked, isUsers(feed, username), comments, attachments); 
						toReturn.add(toAdd);
					} else {
						//need to get the last two
						ArrayList<Comment> comments2Attach = new ArrayList<Comment>();					
						for (int i = currCommentsNumber -commentsNumberPerFeed; i < currCommentsNumber; i++) {
							comments2Attach.add(comments.get(i));
						}
						EnhancedFeed toAdd = new EnhancedFeed(feed, liked, isUsers(feed, username), comments2Attach, attachments); 
						toReturn.add(toAdd);
					}
				}					
			}

		}
		_log.trace("ENHANCED FEEDS TOTAL= " + toReturn.size() + " for user: " + username);
		return toReturn;
	}

	/**
	 * this method is needed because user images portrait change id depending on the portal instance
	 * e.g. a post made from iMarine portal would not show the avatarIMage in D4Science.org
	 * @param screenname
	 * @return the url of the image portrait for this portal instance
	 */
	private String getUserImagePortraitUrlLocal(String screenName) {
		if (! isWithinPortal()) {
			return "";
		}
		String thumbnailURL = "";
		try {
			thumbnailURL = new LiferayUserManager().getUserByUsername(screenName).getUserAvatarURL();
		} catch (UserManagementSystemException | UserRetrievalFault e) {
			e.printStackTrace();
		}
		return thumbnailURL;
	}

	@Override
	public ArrayList<Like> getAllLikesByFeed(String feedid) {
		ArrayList<Like> toReturn = (ArrayList<Like>) store.getAllLikesByFeed(feedid);
		_log.trace("Asking likes for " + feedid);
		for (Like like : toReturn) {
			String thumb = getUserImagePortraitUrlLocal(like.getUserid());

			like.setThumbnailURL(thumb == null ? "" : thumb);
		}
		return toReturn;
	}

	@Override
	public ArrayList<Comment> getAllCommentsByFeed(String feedid) {
		_log.trace("Asking comments for " + feedid);
		ArrayList<Comment> toReturn =  (ArrayList<Comment>) store.getAllCommentByFeed(feedid);
		for (Comment comment : toReturn) {
			String thumb = getUserImagePortraitUrlLocal(comment.getUserid());
			comment.setThumbnailURL(thumb == null ? "" : thumb);
		}
		Collections.sort(toReturn);
		return toReturn;
	}
	@Override
	public boolean deleteComment(String commentid, String feedid) {
		_log.trace("Attempting to delete comment " + commentid);
		try {
			return store.deleteComment(commentid, feedid);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	@Override
	public boolean deleteFeed(String feedid) {
		_log.trace("Called delete feed " + feedid);
		try {
			Feed toDelete = store.readFeed(feedid);
			List<String> hashtags = Utils.getHashTags(toDelete.getDescription());
			if (hashtags != null && !hashtags.isEmpty()) {
				_log.trace("The feed has hashtags, attempting to delete them ... " +  hashtags.toString());
				boolean deletedHashtag = store.deleteHashTags(feedid, toDelete.getVreid(), hashtags);
				_log.trace("deletedHashtag? " +  deletedHashtag);
			}
			_log.trace("Attempting to delete feed " + feedid);
			return store.deleteFeed(feedid);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	@Override
	public ArrayList<ItemBean> getOrganizationUsers(String currentScope) {
		ArrayList<ItemBean> toReturn = Utils.getDisplayableItemBeans(currentScope, getASLSession().getUsername(), isWithinPortal());
		_log.trace("Returning " + toReturn.size() + " users for scope " + currentScope);
		return toReturn;
	}

	private UserSettings getUserSettingsFromSession() {
		try {
			return (UserSettings) getASLSession().getAttribute(USER_SETTINGS_ATTR);			
		} catch (ClassCastException e) { //handle the hot deploy
			return null;
		}		
	}

	private void setUserSettingsInSession(UserSettings user) {
		getASLSession().setAttribute(USER_SETTINGS_ATTR, user);
	}
	/**
	 * tell if a feed is belonging to the current user or not
	 * @param tocheck
	 * @param username
	 * @return true if this feed is of the current user
	 */
	private boolean isUsers(Feed tocheck, String username) {
		return (tocheck.getEntityId().equals(username));
	}

	/**
	 * 
	 * @return true if the user is a portal administrator or not
	 */
	private boolean checkisAdminUser() {
		if (getASLSession().getAttribute(SESSION_ADMIN_ATTR) == null) {
			boolean isAdmin = false;
			try {
				isAdmin = isAdmin();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			getASLSession().setAttribute(SESSION_ADMIN_ATTR, isAdmin);
			return isAdmin;
		}
		return (Boolean) getASLSession().getAttribute(SESSION_ADMIN_ATTR);
	}
	/**
	 * 
	 * @return the screennames of the addressee (user logins e.g. pino.pini)
	 */
	public ArrayList<ItemBean> getSelectedUserIds(ArrayList<String> fullNames) {
		if (fullNames == null) 
			return new ArrayList<ItemBean>();
		else {
			ArrayList<ItemBean> allUsers = Utils.getDisplayableItemBeans("/"+PortalContext.getConfiguration().getInfrastructureName(), getASLSession().getUsername(), isWithinPortal());
			ArrayList<ItemBean> toReturn = new ArrayList<ItemBean>();
			for (String fullName : fullNames) 
				for (ItemBean puser : allUsers) {					
					if (puser.getAlternativeName().compareTo(fullName) == 0) {
						toReturn.add(puser);
						break;
					}
				}
			return toReturn;
		}
	}
	/**
	 * tell if the user is a portal administrator or not
	 * @param username
	 * @return true if is admin
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private boolean isAdmin() throws PortalException, SystemException  {
		if (! isWithinPortal())
			return false;
		try {
			GCubeUser curUser = new LiferayUserManager().getUserByUsername(getASLSession().getUsername());
			return new LiferayRoleManager().isAdmin(curUser.getUserId());
		}
		catch (Exception e) {
			_log.error("Could not check if the user is an Administrator, returning false");
			return false;
		}
	}

	/**
	 * read from the property file in /conf the refreshing time and other configurations needed
	 * @return CustomConfiguration
	 */
	private CustomConfiguration getUserConfiguration() {
		CustomConfiguration toReturn = null;
		_log.info("Trying to read custom config fr News Feed (REFRESH_TIME, VRE Label and show timeline source)");
		Properties props = new Properties();
		int minutes = 0;
		String label = "";
		boolean showTimelineSource = true;

		String propertyfile = "";
		try {
			ServletContext servletContext = getServletContext();
			String contextPath = servletContext.getRealPath(File.separator);
			propertyfile = contextPath + "conf" +  File.separator + "settings.properties";			
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			try {
				minutes = Integer.parseInt(props.getProperty(REFRESH_TIME));
				minutes = minutes*60*1000;
			}
			//catch exception in case the property value isNot a Number
			catch (ClassCastException ex) {
				minutes = 300000; //5 minutes
				_log.error(REFRESH_TIME + " must be a number (in minutes) returning 5 minutes");
			}
			//the vre label
			label = props.getProperty(VRE_LABEL);
			try {
				showTimelineSource = Boolean.parseBoolean(props.getProperty(SHOW_TIMELINE_SOURCE));

			}
			//catch exception in case the property value isNot true or false
			catch (ClassCastException ex) {
				showTimelineSource = true;
				_log.error(showTimelineSource + " must be true or false, returning true");
			}

		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			minutes = 300000; //5 minutes
			_log.error("settings.properties file not found under " + propertyfile +", returning 5 minutes");
		}
		toReturn = new CustomConfiguration(minutes, label, showTimelineSource);
		_log.debug("Read Configuration from property file: " + toReturn);
		return toReturn;

	}
}

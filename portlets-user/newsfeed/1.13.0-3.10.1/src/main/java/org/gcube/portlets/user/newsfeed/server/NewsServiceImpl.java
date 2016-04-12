package org.gcube.portlets.user.newsfeed.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Attachment;
import org.gcube.portal.databook.shared.Comment;
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
import org.gcube.portlets.user.newsfeed.shared.EnhancedFeed;
import org.gcube.portlets.user.newsfeed.shared.MoreFeedsBean;
import org.gcube.portlets.user.newsfeed.shared.NewsConstants;
import org.gcube.portlets.user.newsfeed.shared.OperationResult;
import org.gcube.portlets.user.newsfeed.shared.UserSettings;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserModel;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class NewsServiceImpl extends RemoteServiceServlet implements NewsService {

	private static final Logger _log = LoggerFactory.getLogger(NewsServiceImpl.class);
	/**
	 * 
	 */
	private static final String ADMIN_ROLE = "Administrator";
	private static final String VRE_LABEL = "VRE_LABEL";
	private static final String SHOW_TIMELINE_SOURCE = "SHOW_TIMELINE_SOURCE";
	private static final String REFRESH_TIME = "REFRESH_TIME";

	private static final String SESSION_ADMIN_ATTR = "SESSION_ADMIN_ATTR";
	private static final String USER_SETTINGS_ATTR = "USER_SETTINGS_ATTR";

	public static final String TEST_SCOPE = "/gcube/devsec/devVRE";

	private String APP_ID;


	/**
	 * 
	 */
	private DatabookStore store;

	private final static int MAX_FEEDS_NO = 45;

	public void init() {
		store = new DBCassandraAstyanaxImpl();
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
				UserModel user = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
				thumbnailURL = "/image/user_male_portrait?img_id="+user.getPortraitId();
				fullName = user.getFirstName() + " " + user.getLastName();
				email = user.getEmailAddress();
				String accountURL = "";
				try {
					ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
					accountURL = themeDisplay.getURLMyAccount().toString();
				}catch (NullPointerException e) {
					e.printStackTrace();
				}
				UserInfo userInfo = new UserInfo(username, fullName, thumbnailURL, user.getEmailAddress(), accountURL, true, isAdmin(), null);
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

				_log.info("****** retrieving feeds for user: " + userName);
				User currUser = OrganizationsUtil.validateUser(userName);
				/**
				 * this handles the case where the portlet is deployed outside of VREs (regular)
				 */
				if (isInfrastructureScope()) {
					//VRE Feeds
					GroupManager gm = new LiferayGroupManager();
					for (Organization org : currUser.getOrganizations()) {						
						if (gm.isVRE(org.getOrganizationId()+"")) {
							String vreid = gm.getScope(""+org.getOrganizationId()); //get the scope 
							_log.trace("Reading feeds for VRE: " + vreid);
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
				User currUser = OrganizationsUtil.validateUser(userName);
				//VRE Feeds
				for (Organization org : currUser.getOrganizations()) {
					GroupManager gm = new LiferayGroupManager();
					if (gm.isVRE(org.getOrganizationId()+"")) {
						String vreid = gm.getScope(""+org.getOrganizationId()); //get the scope 		
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

		if (user.getUsername().compareTo(NewsConstants.TEST_USER) == 0) {
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
				NotificationsManager nm = new ApplicationNotificationsManager(getASLSession(), APP_ID);
				boolean nResult = nm.notifyLikedFeed(feedOwnerId, feedid, escapeHtml(feedText));
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
			escapedCommentText = Utils.convertMentionPeopleAnchorHTML(escapedCommentText, mentionedUsers);
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
			NotificationsManager nm = new ApplicationNotificationsManager(getASLSession(), APP_ID);
			if (! user.getUsername().equals(feedOwnerId) && (!isAppFeed)) {				
				boolean result = nm.notifyOwnCommentReply(feedOwnerId, feedid, escapedCommentText);
				_log.trace("Comment Notification to post owner added? " + result);
			} 

			//if there are users who liked this post they get notified, asynchronously with this thread
			ArrayList<Like> favorites = getAllLikesByFeed(feedid);
			Thread likesThread = new Thread(new LikeNotificationsThread(commentText, nm, favorites, feedOwnerId));
			likesThread.start();

			//notify the other users who commented this post (excluding the ones above)
			Thread commentsNotificationthread = new Thread(new CommentNotificationsThread(store, user.getUsername(), comment.getFeedid(), escapedCommentText, nm, feedOwnerId, favorites));
			commentsNotificationthread.start();	

			//send the notification to the mentioned users, if any
			if (mentionedUsers != null && mentionedUsers.size() > 0) {
				ArrayList<GenericItemBean> toPass = new ArrayList<GenericItemBean>();
				for (ItemBean u : mentionedUsers) {
					toPass.add(new GenericItemBean(u.getId(), u.getName(), u.getAlternativeName(), u.getThumbnailURL()));
				}
				Thread thread = new Thread(new MentionNotificationsThread(comment.getFeedid(), commentText, nm, toPass));
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
	 * this method sorts the Feeds in Chronological Reversed order and adds additional user informations 
	 * @param toEnhance
	 * @param } catch (Exception e) { the max number of comments you want to get back, -1 to get all
	 *
	 * @return
	 */
	private ArrayList<EnhancedFeed> enhanceFeeds(ArrayList<Feed> toEnhance, int commentsNumberPerFeed) {
		ArrayList<EnhancedFeed> toReturn = new ArrayList<EnhancedFeed>();
		ASLSession session = getASLSession();
		String username = session.getUsername();

		ArrayList<String> likedFeeds = (ArrayList<String>) store.getAllLikedFeedIdsByUser(getASLSession().getUsername());
		boolean skip = false;
		for (Feed feed : toEnhance) {

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
		StringBuilder thumbnailURL = new StringBuilder("/image/user_male_portrait?img_id=");
		User user = null;
		try {
			user = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), screenName);
		}
		catch (com.liferay.portal.NoSuchUserException ex) {
			return null;
		}
		catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return thumbnailURL.append(user.getPortraitId()).toString();
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
		ArrayList<ItemBean> toReturn = Utils.getOrganizationUsers(currentScope, getASLSession().getUsername(), isWithinPortal());
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
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
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
			ArrayList<ItemBean> allUsers = Utils.getOrganizationUsers("/"+OrganizationsUtil.getRootOrganizationName(), getASLSession().getUsername(), isWithinPortal());
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
			User currUser = OrganizationsUtil.validateUser(getASLSession().getUsername());
			List<Organization> organizations = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
			Organization rootOrganization = null;
			for (Organization organization : organizations) {
				if (organization.getName().equals(OrganizationsUtil.getRootOrganizationName() ) ) {
					rootOrganization = organization;
					break;
				}
			}		

			_log.trace("root: " + rootOrganization.getName() );
			return (hasRole(ADMIN_ROLE, rootOrganization.getName(), currUser));
		}
		catch (NullPointerException e) {
			_log.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder");
			return false;
		}
	}
	/**
	 * 
	 * @param rolename
	 * @param organizationName
	 * @param user
	 * @return
	 * @throws SystemException 
	 */
	private boolean hasRole(String rolename, String organizationName, User user) throws SystemException {
		for (Role role : user.getRoles()) 
			if (role.getName().compareTo(rolename) == 0 ) 
				return true;
		return false;
	}
	/**
	 * utilty method that convert a URL in a text into a clickable link into the browser
	 * 
	 * @param text
	 * @return the text with the clickable url in it
	 */
	public String transformUrls(String textToCheck) {
		StringBuilder sb = new StringBuilder();
		// separate input by spaces ( URLs have no spaces )
		String [] parts = textToCheck.split("\\s");
		// Attempt to convert each item into an URL.
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].startsWith("http")) {
				try {
					URL url = new URL(parts[i]);
					// If possible then replace with anchor...
					sb.append("<a class=\"link\" style=\"font-size:11px;\" href=\"").append(url).append("\" target=\"_blank\">").append(url).append("</a> ");    
				} catch (MalformedURLException e) {
					// If there was an URL then it's not valid
					_log.error("MalformedURLException returning... ");
					return textToCheck;
				}
			} else {
				sb.append(parts[i]);
				sb.append(" ");
			}
		}
		return sb.toString();

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

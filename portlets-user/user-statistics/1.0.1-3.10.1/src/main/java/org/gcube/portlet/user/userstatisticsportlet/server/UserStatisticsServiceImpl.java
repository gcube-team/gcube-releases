package org.gcube.portlet.user.userstatisticsportlet.server;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portlet.user.userstatisticsportlet.client.UserStatisticsService;
import org.gcube.portlet.user.userstatisticsportlet.shared.PostsStatsBean;
import org.gcube.portlet.user.userstatisticsportlet.shared.UserInformation;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.User;
import com.liferay.portal.model.Website;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.WebsiteLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserStatisticsServiceImpl extends RemoteServiceServlet implements UserStatisticsService {

	// Logger
	private static final org.slf4j.Logger _log = LoggerFactory.getLogger(UserStatisticsServiceImpl.class);

	//dev user
	public static final String userid = "test.user";

	//dev vre
	private static final String vreID = "/gcube/devsec/devVRE";

	// Cassandra connection
	private DatabookStore store;

	@Override
	public void init() {
		// get connection to Cassandra
		_log.debug("Getting connection to Cassandra..");
		store = new DBCassandraAstyanaxImpl();

	}

	@Override
	public void destroy(){
		// shutting down connection to Cassandra
		_log.debug("Closing connection to Cassandra");
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

			_log.warn("USER IS NULL setting " + userid + " and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(vreID);

		}		

		return SessionManager.getInstance().getASLSession(sessionID, user);
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
		String user = userid;
//		user = "massimiliano.assante";
		return user;
	}


	@Override
	public String getTotalSpaceInUse() {

		String storageInUse = null;

		// get the session
		ASLSession session = getASLSession();

		//username
		String userName = session.getUsername();

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(userid) == 0) {

			_log.debug("Found " + userName + " returning nothing");
			return null;
			
		}else{

			_log.debug("Getting " + userName + " amount of workspace in use.");

			try{
				
				long init = System.currentTimeMillis();
				
				Workspace workspace = HomeLibrary.getUserWorkspace(userName);
				long storage = workspace.getDiskUsage();
				storageInUse = formatFileSize(storage);
				
				long end = System.currentTimeMillis();
				_log.debug("[USER-STATISTICS] time taken to retrieve user space is " + (end - init) + "ms");

			}catch(Exception e){

				_log.error("Unable to retrieve workspace information!");

			}
		}
		return storageInUse;

	}

	@Override
	public int getProfileStrength() {

		int profileStrenght = -1;

		// get the session
		ASLSession session = getASLSession();

		//username
		String userName = session.getUsername();

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(userid) == 0) {
			_log.debug("Found " + userName + " returning nothing");
			return profileStrenght;
		}else{

			// valuate profile strength
			if(isWithinPortal()){

				try{

					// check if the avatar is present
					boolean avatarPresent = session.getUserAvatarId() != null;

					long init = System.currentTimeMillis();
					
					User user = OrganizationsUtil.validateUser(userName);
					profileStrenght = evaluateProfileStrenght(user, avatarPresent);
					
					long end = System.currentTimeMillis();
					_log.debug("[USER-STATISTICS] time taken to evaluate user profile strenght is " + (end - init) + "ms");

				}catch(Exception e){

					_log.error("Profile strenght evaluation failed!!");

				}
			}
		}
		return profileStrenght;

	}

	@Override
	public UserInformation getUserSettings() {

		// get the session
		ASLSession session = getASLSession();

		//username
		String userName = session.getUsername();

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(userid) == 0) {
			_log.debug("Found " + userName + " returning nothing");
			return null;
		}

		if(isWithinPortal()){
			// If the user is in the root panel, we have to send him the overall number of posts made, comments/likes(received) and the space in use.
			// Otherwise we have to filter on the vre.
			boolean isInfrastructure = isInfrastructureScope();
			_log.debug("User scope is " + (isInfrastructure ? " the whole infrastucture " : " a VRE"));

			// get path of the avatar
			String thumbnailURL = session.getUserAvatarId();
			_log.debug(userName + " avatar has url " + thumbnailURL);

			// get the vre (if not in the infrastructure)
			String actualVre = null;

			if(!isInfrastructure){

				String[] temp = session.getScope().split("/");
				actualVre = temp[temp.length - 1];

			}

			// url account to change the avatar
			String accountURL = null;
			ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
			accountURL = themeDisplay.getURLMyAccount().toString();
			_log.debug("Account url for " + userName + " is "  + accountURL);

			return new UserInformation(isInfrastructure, thumbnailURL, userName, actualVre, accountURL);
		}
		else return new UserInformation(false, null, userName, vreID, null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public PostsStatsBean getPostsStats(){
		
		// get the session
		ASLSession session = getASLSession();

		//username
		String userName = session.getUsername();

		//in case the portal is restarted and you have the social home open it will get test.user (no callback to set session info)
		//this check just return nothing if that happens
		if (userName.compareTo(userid) == 0) {
			_log.debug("Found " + userName + " returning nothing");
			return null;
		}

		long totalFeeds = 0, totalLikes = 0, totalComments = 0;

		// check if the user is or not in a VRE
		boolean isInfrastructure = isInfrastructureScope();

		// date corresponding to one year ago
		Date oneYearAgo = new Date();
		oneYearAgo.setYear(oneYearAgo.getYear() - 1);

		_log.debug("Reference time is " + oneYearAgo.toString());

		try {

			long init = System.currentTimeMillis();
			_log.debug("Getting " + userName + " feeds in the last year.");

			List<Feed> userFeeds = store.getRecentFeedsByUserAndDate(userName, oneYearAgo.getTime());

			_log.debug("Evaluating number of comments and likes of " + userName + "'s feeds.");

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
			_log.debug("Total number of feeds (after time filtering) of  " + userName + " is " + totalFeeds);
			_log.debug("Total number of likes (after time filtering) for " + userName + " is " + totalLikes);
			_log.debug("Total number of comments (after time filtering) for " + userName + " is " + totalComments);

		}catch(Exception e){
			_log.error(e.toString());
			return null;
		}

		// return the object
		return new PostsStatsBean(totalFeeds, totalLikes, totalComments);
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

		score += user.getJobTitle().compareTo("") != 0 ? 20 : 0;
		score += user.getOpenId().compareTo("") != 0 ? 20 : 0;
		String summary = getSummary(user);
		int lenght = summary.replace(" ", "").length(); 
		float partialScore = ((float)lenght / 10.0f);
		score +=  partialScore  > 20f ? 20 : (int)partialScore;

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
		String toReturn = escapeHtml(user.getComments());
		// replace all the line breaks by <br/>
		toReturn = toReturn.replaceAll("(\r\n|\n)"," <br/> ");
		// then replace all the double spaces by the html version &nbsp;
		toReturn = toReturn.replaceAll("\\s\\s","&nbsp;&nbsp;");
		return toReturn;
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

			score += contact.getMySpaceSn().compareTo("") != 0 ? 5 : 0;
			score += contact.getTwitterSn().compareTo("") != 0 ? 5 : 0;
			score += contact.getFacebookSn().compareTo("") != 0 ? 5 : 0;
			score += contact.getSkypeSn().compareTo("") != 0 ? 5 : 0;
			score += contact.getJabberSn().compareTo("") != 0 ? 5 : 0;
			score += contact.getAimSn().compareTo("") != 0 ? 5 : 0;

			List<Website> websites = WebsiteLocalServiceUtil.getWebsites(user.getCompanyId(), "com.liferay.portal.model.Contact", contact.getContactId());
			score += websites.size() > 0 ? 5 : 0; 
		}catch(Exception e ){

			_log.error("Contact profile score evaluation failed!!");
			score = 0;
		}

		return score;
	}
}

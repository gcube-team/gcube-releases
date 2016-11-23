package org.gcube.portlet.user.userstatisticsportlet.server;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;
import org.slf4j.LoggerFactory;

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
 * This class contains server side utils methods
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ServerUtils {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ServerUtils.class);

	/**
	 * the current ASLSession
	 * @return the session
	 */
	public static ASLSession getASLSession(HttpSession session) {

		String sessionID = session.getId();
		String user = (String) session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {

			logger.warn("USER IS NULL setting " + UserStatisticsServiceImpl.defaultUserId + " and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(UserStatisticsServiceImpl.vreID);

		}		

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public static String getDevelopmentUser() {
		String user = UserStatisticsServiceImpl.defaultUserId;
		//				user = "costantino.perciante";
		return user;
	}

	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @param userid in case userid is not null, the user is visiting a profile page and 
	 * the statistics to return are the ones available in the whole infrastructure
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	public static boolean isInfrastructureScope(String userid, HttpSession session) {
		boolean toReturn = false;
		try {
			GroupManager manager = new LiferayGroupManager();
			long groupId = manager.getGroupIdFromInfrastructureScope(getASLSession(session).getScope());
			toReturn = !manager.isVRE(groupId) || userid != null;
			return toReturn;
		}
		catch (Exception e) {
			logger.error("NullPointerException in isInfrastructureScope returning false");
			return false;
		}		
	}

	/**
	 * Online or in development mode?
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			logger.trace("Development Mode ON");
			return false;
		}			
	}

	/**
	 * returns dynamically the formated size.
	 *
	 * @param size the size
	 * @return the string
	 */
	public static String formatFileSize(long size) {
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
	public static int evaluateProfileStrenght(User user, boolean imageIsPresent) {

		int score = evaluateContactScore(user);
		score += evaluateInformationScore(user, imageIsPresent);

		return score;
	}


	/**
	 * Evaluates a score according to the information of the user such as job, organization, comments
	 * @param user
	 * @return a score in [0, 65]
	 */
	public static int evaluateInformationScore(User user, boolean imageIsPresent) {
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
	public static String getSummary(User user) {
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
	public  static String escapeHtml(String html) {
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
	public static  int evaluateContactScore(User user){

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

			logger.error("Contact profile score evaluation failed!!");
			score = 0;
		}

		return score;
	}

	/**
	 * Set the permission checker to set/get custom fields into liferay
	 */
	public static void setPermissionChecker(){

		// set permission checker
		try{
			long adminId = LiferayUserManager.getAdmin().getUserId();
			PrincipalThreadLocal.setName(adminId);
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(adminId));
			PermissionThreadLocal.setPermissionChecker(permissionChecker); 
		}catch(Exception e){
			logger.error("Unable to set permission checker. Custom fields set/get operations are likely to fail...");
		}

	}

	/**
	 * On servlet instanciation, create the custom field and set it to startingValue
	 * @param customFieldNameUserStatisticsVisibility
	 * @param b
	 */
	public static void createUserCustomField(
			String customFieldNameUserStatisticsVisibility, boolean startingValue) {

		// set permission checker
		ServerUtils.setPermissionChecker();

		try{

			User defaultUser = UserLocalServiceUtil.getDefaultUser(ManagementUtils.getCompany().getCompanyId());

			// check if it exists
			boolean exists = defaultUser.getExpandoBridge().hasAttribute(customFieldNameUserStatisticsVisibility);

			if(exists){

				logger.debug("Custom field already exists... There is no need to create it");

			}else{

				logger.debug("Creating custom field " + customFieldNameUserStatisticsVisibility + 
						" with starting value " + startingValue);

				// create
				defaultUser.getExpandoBridge().addAttribute(customFieldNameUserStatisticsVisibility, ExpandoColumnConstants.BOOLEAN, (Serializable)(true)); 

			}

		}catch(Exception e){
			logger.error("Unable to create custom field " + customFieldNameUserStatisticsVisibility);
		}		
	}
}

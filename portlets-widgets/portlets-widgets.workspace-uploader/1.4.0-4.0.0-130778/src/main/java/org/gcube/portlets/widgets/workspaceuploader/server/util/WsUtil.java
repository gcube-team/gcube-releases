/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server.util;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;

import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 18, 2014
 */
public class WsUtil {

	public static final String USERNAME_ATTRIBUTE = ScopeHelper.USERNAME_ATTRIBUTE;
	public static final String NOTIFICATION_MANAGER_UPLOADER = "WS_UPLOADER_NOTIFICATION_MANAGER";
//	public static final String NOTIFICATION_PRODUCER = "WORKSPACE_UPLOADER_NOTIFICATION_PRODUCER";
	//	public static final String NOTIFICATION_PORTLET_CLASS_ID = WorkspaceUploadServletStream.class.getName(); //IN DEV
	public static final String NOTIFICATION_PORTLET_CLASS_ID = "org.gcube.portlets.user.workspace.server.GWTWorkspaceServiceImpl";
	//IN DEV
	public static final String TEST_SCOPE = "/gcube/devsec";
	public static String TEST_USER = "francesco.mangiacrapa";
	public static String TEST_USER_FULL_NAME = "Test User";


	protected static Logger logger = Logger.getLogger(WsUtil.class);

	//	public static boolean withoutPortal = false;

	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}
		catch (Exception ex) {
			logger.trace("Development Mode ON");
			return false;
		}
	}

	/**
	 * Gets the asl session.
	 *
	 * @param httpSession the http session
	 * @return the asl session
	 */
	public static ASLSession getAslSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);
		ASLSession session;

		if (user == null) {

			/*USE ANOTHER ACCOUNT (OTHERWHISE BY TEST_USER) FOR RUNNING
			 * COMMENT THIS IN DEVELOP ENVIROMENT (UNCOMMENT IN PRODUCTION)*/
			user=TEST_USER;

			if (!isWithinPortal()) { //DEV MODE
				user = "francesco.mangiacrapa";
				TEST_USER_FULL_NAME = "Francesco Mangiacrapa";
			}

			logger.warn("WORKSPACE PORTLET STARTING IN TEST MODE - NO USER FOUND - PORTLETS STARTING WITH FOLLOWING SETTINGS:");
			logger.warn("session id: "+sessionID);
			logger.warn("TEST_USER: "+user);
			logger.warn("TEST_SCOPE: "+TEST_SCOPE);
			logger.warn("USERNAME_ATTRIBUTE: "+USERNAME_ATTRIBUTE);
			session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(TEST_SCOPE);

			//MANDATORY FOR SOCIAL LIBRARY
			session.setUserAvatarId(user + "Avatar");
			session.setUserFullName(TEST_USER_FULL_NAME);
			session.setUserEmailAddress(user + "@mail.test");

			//SET HTTP SESSION ATTRIBUTE
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
			return session;

		}

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * Checks if is session expired.
	 *
	 * @param httpSession the http session
	 * @return true if current username into ASL session is WsUtil.TEST_USER, false otherwise
	 * @throws Exception the exception
	 */
	public static boolean isSessionExpired(HttpSession httpSession) throws Exception {
		logger.trace("workspace session validating...");
		//READING USERNAME FROM ASL SESSION
		String userUsername = getAslSession(httpSession).getUsername();
		boolean isTestUser = userUsername.compareTo(WsUtil.TEST_USER)==0;

		//TODO UNCOMMENT THIS FOR RELEASE
		logger.trace("Is "+WsUtil.TEST_USER+" test user? "+isTestUser);

		if(isTestUser){
			logger.error("workspace session is expired! username is: "+WsUtil.TEST_USER);
			return true; //is TEST_USER, session is expired
		}

		logger.trace("workspace session is valid! current username is: "+userUsername);

		return false;

	}

	/**
	 * Gets the workspace.
	 *
	 * @param httpSession the http session
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace(final HttpSession httpSession) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException{

		logger.trace("Get Workspace");
		final ASLSession session = getAslSession(httpSession);
		logger.trace("ASLSession scope: "+session.getScope() + " username: "+session.getUsername());

		ScopeProvider.instance.set(session.getScope());
		logger.trace("Scope provider instancied");

		return HomeLibrary.getUserWorkspace(session.getUsername());
	}

//
//	/**
//	 * Gets the notification producer.
//	 *
//	 * @param session the session
//	 * @param request the request
//	 * @return the notification producer
//	 */
//	public static NotificationsWorkspaceUploaderProducer getNotificationProducer(ASLSession session, HttpServletRequest request)
//	{
//
//		NotificationsWorkspaceUploaderProducer notifProducer = (NotificationsWorkspaceUploaderProducer) session.getAttribute(NOTIFICATION_PRODUCER);
//
//		if (notifProducer == null) {
//			logger.trace("Create new Notification Producer for user: "+session.getUsername());
//			notifProducer = new NotificationsWorkspaceUploaderProducer(session, request);
//			session.setAttribute(NOTIFICATION_PRODUCER, notifProducer);
//		}
//
//		return notifProducer;
//	}

	/**
	 * Put workspace uploader in session.
	 *
	 * @param httpSession the http session
	 * @param uploader the uploader
	 * @throws Exception the exception
	 */
	public static void putWorkspaceUploaderInSession(final HttpSession httpSession, WorkspaceUploaderItem uploader) throws Exception
	{
		logger.trace("Put workspace uploader in session: "+uploader.getIdentifier() + ", STATUS: "+uploader.getUploadStatus());
		final ASLSession session = getAslSession(httpSession);

		if(uploader.getIdentifier()==null || uploader.getIdentifier().isEmpty())
			throw new Exception("Invalid uploader");

		session.setAttribute(uploader.getIdentifier(), uploader);
		logger.debug("Added uploader: "+uploader.getIdentifier() +" in session");
	}

	/**
	 * Erase workspace uploader in session.
	 *
	 * @param httpSession the http session
	 * @param uploader the uploader
	 * @throws Exception the exception
	 */
	public static void forceEraseWorkspaceUploaderInSession(final HttpSession httpSession, WorkspaceUploaderItem uploader) throws Exception
	{
		logger.trace("Force Erase WorkspaceUploader workspace uploader in session: "+uploader.getIdentifier());
		final ASLSession session = getAslSession(httpSession);

		if(uploader==null || uploader.getIdentifier()==null || uploader.getIdentifier().isEmpty())
			throw new Exception("Invalid uploader");

		session.removeAttribute(uploader.getIdentifier());
		logger.info("Erased uploader: "+uploader.getIdentifier());

	}


	/**
	 * Erase workspace uploader in session.
	 *
	 * @param httpSession the http session
	 * @param uploader the uploader
	 * @throws Exception the exception
	 */
	public static void eraseWorkspaceUploaderInSession(final HttpSession httpSession, WorkspaceUploaderItem uploader) throws Exception
	{
		logger.trace("Erase WorkspaceUploader workspace uploader in session: "+uploader.getIdentifier() + ", erasable? "+uploader.isErasable());
		final ASLSession session = getAslSession(httpSession);

		if(uploader==null || uploader.getIdentifier()==null || uploader.getIdentifier().isEmpty())
			throw new Exception("Invalid uploader");

		if(uploader.isErasable()){
			//			session.setAttribute(uploader.getIdentifier(), null);
			session.removeAttribute(uploader.getIdentifier());
			logger.info("Erased uploader: "+uploader.getIdentifier());
		}
	}


	/**
	 * Gets the workspace uploader in session.
	 *
	 * @param httpSession the http session
	 * @param uploaderIdentifier the uploader identifier
	 * @return the workspace uploader in session
	 * @throws Exception the exception
	 */
	public static WorkspaceUploaderItem getWorkspaceUploaderInSession(final HttpSession httpSession, String uploaderIdentifier) throws Exception
	{
		final ASLSession session = getAslSession(httpSession);

		if(uploaderIdentifier==null || uploaderIdentifier.isEmpty())
			throw new Exception("Invalid uploader");

		return (WorkspaceUploaderItem) session.getAttribute(uploaderIdentifier);

	}


	/**
	 * Sets the erasable workspace uploader in session.
	 *
	 * @param httpSession the http session
	 * @param uploaderIdentifier the uploader identifier
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public static boolean setErasableWorkspaceUploaderInSession(final HttpSession httpSession, String uploaderIdentifier) throws Exception
	{
		WorkspaceUploaderItem uploader = getWorkspaceUploaderInSession(httpSession, uploaderIdentifier);

		if(uploader!=null){
			/*TODO REMOVE SESSION KEY FROM SESSION MUST BE MANAGED IN ANOTHER WAY IN ORDER TO AVOID THATH IT'S REMOVED FROM SESSION BEFORE THAT
			 * POLLING RETRIEVES STATUS, USE A FLAG REMOVED?*/
			logger.debug("Set erasable uploader: "+uploader.getIdentifier() +" in session");
			//			uploader = null;
			uploader.setErasable(true);
			putWorkspaceUploaderInSession(httpSession, uploader);
			return true;
		}
		logger.debug("Uploader not found, skipping erase from session");
		return false;
	}


	/**
	 * Gets the user id.
	 *
	 * @param httpSession the http session
	 * @return the user id
	 */
	public static String getUserId(HttpSession httpSession) {

		ASLSession session = getAslSession(httpSession);

		return session.getUsername();
	}

}

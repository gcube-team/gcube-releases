/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.server.util;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.gcubereleases.server.database.DaoGcubeBuilderReportDBManager;
import org.gcube.portlets.admin.gcubereleases.shared.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ScopeUtil {

	public static final String USERNAME_ATTRIBUTE = ScopeHelper.USERNAME_ATTRIBUTE;
	//public static final String TEST_SCOPE = "/gcube/devsec/devVRE";
	public static final String TEST_SCOPE = "/d4science.research-infrastructures.eu";
	//public static final String TEST_SCOPE = "/gcube/devsec/devVRE";

	private static final String DAO_RELEASE_MANAGER = "Dao_Release_Mng";

	// COMMENT THIS FOR RELEASE
	// public static final String TEST_USER = "francesco.mangiacrapa";
	// public static final String TEST_USER_FULL_NAME = "Francesco Mangiacrapa";

	// UNCOMMENT THIS FOR RELEASE
	public static String TEST_USER = "test.user";
	public static String TEST_USER_FULL_NAME = "Test User";

	protected static Logger logger = LoggerFactory.getLogger(ScopeUtil.class);

	/**
	 * Checks if is within portal.
	 *
	 * @return true, if is within portal
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} catch (Exception ex) {
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
	public static ASLSession getAslSession(HttpSession httpSession) {
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);
		ASLSession session;

//		logger.info("user is: "+user);

		if (user == null) {

			/*
			 * USE ANOTHER ACCOUNT (OTHERWHISE BY TEST_USER) FOR RUNNING COMMENT
			 * THIS IN DEVELOP ENVIROMENT (UNCOMMENT IN PRODUCTION)
			 */
			user = TEST_USER;

			if (!isWithinPortal()) {
				user = "francesco.mangiacrapa";
				TEST_USER_FULL_NAME = "Francesco Mangiacrapa";
			}

			logger.warn("WORKSPACE PORTLET STARTING IN TEST MODE - NO USER FOUND - PORTLETS STARTING WITH FOLLOWING SETTINGS:");
			logger.warn("session id: " + sessionID);
			logger.warn("TEST_USER: " + user);
			logger.warn("TEST_SCOPE: " + TEST_SCOPE);
			logger.warn("USERNAME_ATTRIBUTE: " + USERNAME_ATTRIBUTE);
			session = SessionManager.getInstance().getASLSession(sessionID,user);
			session.setScope(TEST_SCOPE);

			// MANDATORY FOR SOCIAL LIBRARY
			session.setUserAvatarId(user + "Avatar");
			session.setUserFullName(TEST_USER_FULL_NAME);
			session.setUserEmailAddress(user + "@mail.test");

			// SET HTTP SESSION ATTRIBUTE
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
			return session;
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * Checks if is session expired.
	 *
	 * @param httpSession the http session
	 * @return true, if is session expired
	 * @throws Exception the exception
	 */
	public static boolean isSessionExpired(HttpSession httpSession)
			throws Exception {
		logger.trace("workspace session validating...");
		// READING USERNAME FROM ASL SESSION
		String userUsername = getAslSession(httpSession).getUsername();
		boolean isTestUser = userUsername.compareTo(ScopeUtil.TEST_USER) == 0;

		// TODO UNCOMMENT THIS FOR RELEASE
		logger.trace("Is " + ScopeUtil.TEST_USER + " test user? " + isTestUser);

		if (isTestUser) {
			logger.error("workspace session is expired! username is: "
					+ ScopeUtil.TEST_USER);
			return true; // is TEST_USER, session is expired
		}

		logger.trace("workspace session is valid! current username is: "
				+ userUsername);

		return false;
	}

	/**
	 * Gets the db manger for release.
	 *
	 * @param session the session
	 * @param factory the factory
	 * @return the db manger for release
	 */
	public static DaoGcubeBuilderReportDBManager<Release> getDbMangerForRelease(ASLSession session, EntityManagerFactory factory)
	{

		DaoGcubeBuilderReportDBManager<Release> daoManager = (DaoGcubeBuilderReportDBManager<Release>) session.getAttribute(DAO_RELEASE_MANAGER);

		if (daoManager == null) {
			try{
				logger.info("Create new DaoGcubeBuilderReportDBManager for user: "+session.getUsername());
				daoManager = new DaoGcubeBuilderReportDBManager<Release>(factory);
				daoManager.instanceReleaseEntity();
				session.setAttribute(DAO_RELEASE_MANAGER, daoManager);
				logger.info("Set DaoGcubeBuilderReportDBManager for user: "+session.getUsername() +" with attribute: "+DAO_RELEASE_MANAGER);
			}catch (Exception e) {
				logger.error("An error occurred instancing DaoGcubeBuilderReportDBManager for user: "+session.getUsername(),e);
			}
		}

		return daoManager;
	}

	/*
	public static DaoGcubeBuilderReportDBManager<Release> closeDbMangerForRelease(ASLSession session)
	{

		DaoGcubeBuilderReportDBManager<Release> daoManager = (DaoGcubeBuilderReportDBManager<Release>) session.getAttribute(DAO_RELEASE_MANAGER);
		logger.info("Tentative closing DaoGcubeBuilderReportDBManager for user: "+session.getUsername());

		if (daoManager != null) {
			try{
				logger.info("DaoGcubeBuilderReportDBManager not null for user: "+session.getUsername());
				EntityManagerFactory factory = daoManager.getFactory();
				if(factory.isOpen()){
					factory.close();
					logger.info("DaoGcubeBuilderReportDBManager - Factory closed to for user: "+session.getUsername());
				}

				session.setAttribute(DAO_RELEASE_MANAGER, null);
			}catch (Exception e) {
				logger.error("Silent close, error occurred on closing DaoGcubeBuilderReportDBManager Factory for user: "+session.getUsername());
			}
		}else
			logger.trace("DaoGcubeBuilderReportDBManager null for user: "+session.getUsername());

		return daoManager;
	}
	*/
}

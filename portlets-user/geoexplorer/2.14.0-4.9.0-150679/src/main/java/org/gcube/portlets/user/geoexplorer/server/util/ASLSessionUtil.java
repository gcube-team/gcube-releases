/**
 *
 */
package org.gcube.portlets.user.geoexplorer.server.util;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.shared.SessionExpiredException;

import com.liferay.portal.service.UserLocalServiceUtil;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 25, 2016
 */
public class ASLSessionUtil {

	/**
	 *
	 */
	public static final String TEST_USER = "test.user";
	public static final String USERNAME_ATTRIBUTE = "username";
	public static Logger logger = Logger.getLogger(ASLSessionUtil.class);

	/**
	 * Checks if is test mode;
	 *
	 * @return  true if in development false if you're running into the portal
	 */
	public static boolean isTestMode() {
		try {
			UserLocalServiceUtil.getService();
			return false;
		}
		catch (Exception ex) {
			logger.trace("Development Mode ON");
			return true;
		}
	}


	/**
	 * Checks if is session expired.
	 *
	 * @param httpSession the http session
	 * @return true, if is session expired
	 */
	public static boolean isSessionExpired(HttpSession httpSession){
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);
		return user == null;
	}


	/**
	 * Gets the ASL session.
	 *
	 * @param httpSession the http session
	 * @return the ASL session
	 */
	public static ASLSession getASLSession(HttpSession httpSession) throws SessionExpiredException {
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);

		if (user == null) {
			if(isTestMode()){
				logger.info("GEOEXPLORER STARTING IN TEST MODE - NO USER FOUND");
				//for test only
				user = TEST_USER;
				httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
				ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
				session.setScope(Constants.defaultScope);
				return session;
			}else{
				logger.error("ASL Session is expired, the user is null!!!");
				throw new SessionExpiredException("User into ASL session is null");
			}
		}else{
			logger.trace("user found in session "+user);
		}

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
}

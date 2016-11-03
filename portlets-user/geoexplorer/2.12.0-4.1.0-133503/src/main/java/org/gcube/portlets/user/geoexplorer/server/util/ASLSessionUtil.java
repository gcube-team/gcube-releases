/**
 *
 */
package org.gcube.portlets.user.geoexplorer.server.util;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portlets.user.geoexplorer.client.Constants;


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
	 * Gets the ASL session.
	 *
	 * @param httpSession the http session
	 * @return the ASL session
	 */
	public static ASLSession getASLSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);

		if (user == null) {

			logger.info("GEOEXPLORER STARTING IN TEST MODE - NO USER FOUND");

			//for test only
			user = TEST_USER;
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(Constants.defaultScope);
			//session.setScope("/gcube/devsec/devVRE");

			return session;
		} else
			logger.trace("user found in session "+user);

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
}

/**
 * 
 */
package org.gcube.portlets.widgets.githubconnector.server;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.widgets.githubconnector.shared.Constants;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ExpiredSessionServiceException;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ServiceException;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */

public class SessionUtil {

	private static final Logger logger = Logger.getLogger(SessionUtil.class);

	public static ASLSession getASLSession(HttpSession httpSession)
			throws ServiceException {
		String username = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession aslSession;
		if (username == null) {
			if (Constants.DEBUG_MODE) {
				logger.info("no user found in session, use test user");
				username = Constants.DEFAULT_USER;
				String scope = Constants.DEFAULT_SCOPE;

				httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE,
						username);
				aslSession = SessionManager.getInstance().getASLSession(
						httpSession.getId(), username);
				aslSession.setScope(scope);
			} else {
				logger.info("no user found in session!");
				throw new ExpiredSessionServiceException("Session Expired!");

			}
		} else {
			aslSession = SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);

		}

		logger.info("SessionUtil: aslSession " + aslSession.getUsername() + " "
				+ aslSession.getScope());

		return aslSession;
	}

	public static String getToken(ASLSession aslSession)
			throws ServiceException {
		String token = null;
		if (Constants.DEBUG_MODE) {
			token = Constants.DEFAULT_TOKEN;
		} else {
			token = aslSession.getSecurityToken();
		}
		logger.info("received token: " + token);
		return token;

	}

}

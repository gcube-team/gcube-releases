/**
 * 
 */
package org.gcube.portlets.admin.authportletmanager.server;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.authportletmanager.shared.ConstantsSharing;
import org.gcube.portlets.admin.authportletmanager.shared.exceptions.ExpiredSessionServiceException;
import org.gcube.portlets.admin.authportletmanager.shared.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class SessionUtil {

	private static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	public static ASLSession getASLSession(HttpSession httpSession)
			throws ServiceException {
		String username = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession aslSession;
		if (username == null) {
			if (ConstantsSharing.DEBUG_MODE) {
				logger.info("no user found in session, use test user");
				username = ConstantsSharing.DEFAULT_USER;
				String scope = ConstantsSharing.DEFAULT_SCOPE;
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
}

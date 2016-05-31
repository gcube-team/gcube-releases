/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.server;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerSessionExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SessionUtil {

	private static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	public static ASLSession getAslSession(HttpSession httpSession)
			throws AccountingManagerSessionExpiredException {
		String username = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession session;
		if (username == null) {
			logger.warn("no user found in session, use test user");
			throw new AccountingManagerSessionExpiredException("Session Expired!");
			
			/*
			// Remove comment for Test
			username = Constants.DEFAULT_USER;
			String scope = Constants.DEFAULT_SCOPE;

			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, username);
			session = SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);
			session.setScope(scope);
			*/
		} else {
			session = SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);

		}

		logger.info("SessionUtil: aslSession " + session.getUsername() + " "
				+ session.getScope());

		return session;

	}

	
}

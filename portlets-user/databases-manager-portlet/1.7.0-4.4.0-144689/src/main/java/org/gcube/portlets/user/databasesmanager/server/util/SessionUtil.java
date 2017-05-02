package org.gcube.portlets.user.databasesmanager.server.util;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.databasesmanager.shared.Constants;
import org.gcube.portlets.user.databasesmanager.shared.SessionExpiredException;


public class SessionUtil {

	private static Logger logger = Logger.getLogger(SessionUtil.class);
	//public static final String TEST_USER = "test.user";
	//public static final String USER = "database.manager";
	

	public static ASLSession getASLSession(HttpSession httpSession)
			throws Exception {
		String username = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession aslSession;
		if (username == null) {
			if (Constants.DEBUG_MODE) {
				logger.info("no user found in session, use test user");

				// Remove comment for Test
				username = Constants.DEFAULT_USER;
				String scope = Constants.DEFAULT_SCOPE;

				httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE,
						username);
				aslSession = SessionManager.getInstance().getASLSession(
						httpSession.getId(), username);
				aslSession.setScope(scope);
			} else {
				logger.info("No user found in session");
				throw new SessionExpiredException();

			}
		} else {
			aslSession = SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);

		}

		logger.info("SessionUtil: aslSession " + aslSession.getUsername() + " "
				+ aslSession.getScope());

		return aslSession;
	}

	public static String getToken(ASLSession aslSession) throws Exception {
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

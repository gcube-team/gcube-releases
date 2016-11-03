package org.gcube.portlets.user.databasesmanager.server.util;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;


public class SessionUtil {

	protected static Logger logger = Logger.getLogger(SessionUtil.class);
	public static final String TEST_USER = "test.user";
	public static final String USER = "database.manager";


	public static ASLSession getAslSession(HttpSession httpSession) {
		String sessionID = httpSession.getId();
		String user = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {

			// user = "loredana.liccardo";
			user = USER;
			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			ASLSession session = SessionManager.getInstance().getASLSession(
					sessionID, user);
			session.setScope("/gcube/devsec/devVRE");

			return session;
			// return null;

		} else {
			logger.trace("user found in session " + user);
		}
		ASLSession session = SessionManager.getInstance().getASLSession(
				sessionID, user);

		return session;
	}
	
	public static boolean isSessionExpired(HttpSession httpSession) throws Exception {
		logger.info("session validating...");
		//reading username from asl session
		String userUsername = getAslSession(httpSession).getUsername();
		
		/*COMMENT THIS IN DEVELOP ENVIROMENT (UNCOMMENT IN PRODUCTION)*/
		
		if(userUsername.compareToIgnoreCase(USER)==0){
			logger.error("session is expired! username is: "+SessionUtil.USER);
			return true; //is USER, session is expired
		}
		
		if(userUsername.compareToIgnoreCase(TEST_USER)==0){
			logger.error("session is expired! username is: "+SessionUtil.TEST_USER);
			return true; //is TEST_USER, session is expired
		}

		logger.info("session is valid! current username is: "+userUsername);
		
		return false;
		
	}

	public static StatisticalManagerFactory getFactory(HttpSession httpSession) {
		ASLSession session = getAslSession(httpSession);
		return getFactory(session);
	}

	public static StatisticalManagerFactory getFactory(ASLSession session) {
		ScopeProvider.instance.set(session.getScope());

		return getFactory(session.getScope());
	}

	public static StatisticalManagerFactory getFactory(String scope) {
		ScopeProvider.instance.set(scope.toString());

		return StatisticalManagerDSL.createStateful()
				.withTimeout(5, TimeUnit.MINUTES).build(); // IS

	}

	public static StatisticalManagerDataSpace getDataSpaceService(
			HttpSession httpSession) {
		ASLSession session = getAslSession(httpSession);
		return getDataSpaceService(session);
	}

	public static StatisticalManagerDataSpace getDataSpaceService(
			ASLSession session) {
		ScopeProvider.instance.set(getScope(session));

		return StatisticalManagerDSL.dataSpace()
				.withTimeout(5, TimeUnit.MINUTES).build();
	}

	public static String getUsername(HttpSession httpSession) {
		ASLSession session = getAslSession(httpSession);
		return getUsername(session);
	}

	private static String getUsername(ASLSession session) {
		return session.getUsername();
	}

	public static String getScope(HttpSession httpSession) {
		ASLSession session = getAslSession(httpSession);
		return getScope(session);
	}

	private static String getScope(ASLSession session) {
		return session.getScope().toString();
	}

}

package org.gcube.portlets.user.trendylyzer_portlet.server.utils;

import java.util.concurrent.TimeUnit;

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
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.trendylyzer_portlet.client.Constants;

public class SessionUtil {
	protected static Logger logger = Logger.getLogger(SessionUtil.class);

	protected static ASLSession getSession(HttpSession httpSession) {
		String sessionID = httpSession.getId();
		String user = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			logger.error("TABULAR DATA WIDGET STARTING IN TEST MODE - NO USER FOUND");
//			MessageBox.alert("ERROR", "User not found, please refresh session.", null);
//			logger.error(" NO USER FOUND");
			user = Constants.DEFAULT_USER;
			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(
					sessionID, user);
			session.setScope(Constants.DEFAULT_SCOPE);
			return session;
		} else
			logger.trace("user found in session " + user);
		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	
	public static StatisticalManagerFactory getFactory(HttpSession httpSession)
	{
		ASLSession session = getSession(httpSession);
		return getFactory(session);
	}
	public static StatisticalManagerFactory getFactory(ASLSession session)
	{
		return getFactory(session.getScope());
	}
	
	public static StatisticalManagerFactory getFactory(String scope)
	{
		ScopeProvider.instance.set(scope.toString());
		return StatisticalManagerDSL.createStateful().withTimeout(5, TimeUnit.MINUTES).build();
		
	}
	public static StatisticalManagerDataSpace getDataSpaceService(HttpSession httpSession) {
		ASLSession session = getSession(httpSession);
		return getDataSpaceService(session);
	}
	public static StatisticalManagerDataSpace getDataSpaceService(ASLSession session) {
		ScopeProvider.instance.set(getScope(session));
		
			return StatisticalManagerDSL.dataSpace().build();
	}
	public static String getUsername(HttpSession httpSession) {
		ASLSession session = getSession(httpSession);
		return getUsername(session);
	}

	private static String getUsername(ASLSession session) {
		return session.getUsername();
	}
	
	public static String getScope(HttpSession httpSession) {
		ASLSession session = getSession(httpSession);
		return getScope(session);
	}

	private static String getScope(ASLSession session) {
		return session.getScope().toString();
	}
	public static Workspace getWorkspace(HttpSession httpSession) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException {
		ASLSession session = getSession(httpSession);
		return getWorkspace(session);
	}

	private static Workspace getWorkspace(ASLSession session) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException {
		return HomeLibrary.getUserWorkspace(session.getUsername());
	}


	
}

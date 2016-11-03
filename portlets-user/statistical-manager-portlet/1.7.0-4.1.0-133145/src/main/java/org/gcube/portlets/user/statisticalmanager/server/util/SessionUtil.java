/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.server.util;

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
import org.gcube.portlets.user.statisticalmanager.shared.Constants;
import org.gcube.portlets.user.tdw.server.datasource.DataSource;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * 
 */
public class SessionUtil {

	private static Logger logger = Logger.getLogger(SessionUtil.class);


	public static final String DATASOURCE_ATTRIBUTE_NAME = "TDW.DATASOURCE";

	
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
				throw new Exception("Session Expired!");

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
			throws Exception {
		String token = null;
		if (Constants.DEBUG_MODE) {
			token = Constants.DEFAULT_TOKEN;
		} else {
			token = aslSession.getSecurityToken();
		}
		logger.info("received token: " + token);
		return token;

	}


	public static DataSource getDataSource(HttpSession httpSession) throws Exception {
		ASLSession session = getASLSession(httpSession);
		return (DataSource) session.getAttribute(DATASOURCE_ATTRIBUTE_NAME);
	}

	public static StatisticalManagerFactory getFactory(HttpSession httpSession)
			throws Exception {
		if (httpSession == null) {
			throw new Exception("HttpSession is null");
		} else {
			ASLSession session = getASLSession(httpSession);
			return getFactory(session);
		}
	}

	public static StatisticalManagerFactory getFactory(ASLSession session) throws Exception {
		if (session == null) {
			throw new Exception("ASLSession is null");
		} else {
			ScopeProvider.instance.set(session.getScope());
			return getFactory(session.getScope());
		}

	}

	public static StatisticalManagerFactory getFactory(String scope) {
		// get service entry point
		ScopeProvider.instance.set(scope.toString());
		// if (Constants.TEST_MODE)
		// {
		// return StatisticalManagerDSL.createStateful().at(host).build();
		// ScopeProvider.instance.set(scope.toString());
		// }// TEST
		// else
		return StatisticalManagerDSL.createStateful()
				.withTimeout(5, TimeUnit.MINUTES).build(); // IS

		// Try to insert in session
	}

	public static StatisticalManagerDataSpace getDataSpaceService(
			HttpSession httpSession) throws Exception {
		ASLSession session = getASLSession(httpSession);
		return getDataSpaceService(session);
	}

	public static StatisticalManagerDataSpace getDataSpaceService(
			ASLSession session) {
		ScopeProvider.instance.set(getScope(session));
		// if (Constants.TEST_MODE)
		// return StatisticalManagerDSL.dataSpace().at(host).build();
		// else
		return StatisticalManagerDSL.dataSpace()
				.withTimeout(5, TimeUnit.MINUTES).build();
	}

	public static Workspace getWorkspace(HttpSession httpSession)
			throws Exception {
		ASLSession session = getASLSession(httpSession);
		return getWorkspace(session);
	}

	private static Workspace getWorkspace(ASLSession session)
			throws WorkspaceFolderNotFoundException, InternalErrorException,
			HomeNotFoundException {

		return HomeLibrary.getUserWorkspace(session.getUsername());
	}

	public static String getUsername(HttpSession httpSession) throws Exception {
		ASLSession session = getASLSession(httpSession);
		return getUsername(session);
	}

	private static String getUsername(ASLSession session) {
		return session.getUsername();
	}

	public static String getScope(HttpSession httpSession) throws Exception {
		ASLSession session = getASLSession(httpSession);
		return getScope(session);
	}

	private static String getScope(ASLSession session) {
		return session.getScope().toString();
	}

}

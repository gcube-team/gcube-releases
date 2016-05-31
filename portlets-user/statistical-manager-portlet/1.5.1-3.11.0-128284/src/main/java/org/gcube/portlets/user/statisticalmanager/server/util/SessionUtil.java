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
import org.gcube.portlets.user.tdw.server.datasource.DataSource;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * 
 */
public class SessionUtil {

	protected static Logger logger = Logger.getLogger(SessionUtil.class);
	// private final static String hostUrl =
	// "http://pc-angela.isti.cnr.it:9090";
	// private static URI host = URI.create(hostUrl);
	// private final static String SCOPE = "/gcube";

	public static final String DATASOURCE_ATTRIBUTE_NAME = "TDW.DATASOURCE";

	// private static final String SM_SERVICE_ATTRIBUTE_NAME =
	// "statisticalManagerService";

	public static ASLSession getSession(HttpSession httpSession) {
		String sessionID = httpSession.getId();
		String user = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {
		
			// user = "angela.italiano";
			// httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			// ScopeProvider.instance.set("/gcube/devsec");
			// ASLSession session = SessionManager.getInstance().getASLSession(
			// sessionID, user);
			// session.setScope("/gcube/devsec");
			// logger.trace("user found is null");
			// logger.trace("******Return  null session***");
			// return session;
			return null;

		} else
			logger.trace("user found in session " + user);
		ASLSession session = SessionManager.getInstance().getASLSession(
				sessionID, user);

		return session;
	}

	public static DataSource getDataSource(HttpSession httpSession) {
		ASLSession session = getSession(httpSession);
		return (DataSource) session.getAttribute(DATASOURCE_ATTRIBUTE_NAME);
	}

	public static StatisticalManagerFactory getFactory(HttpSession httpSession)
			throws Exception {
		if (httpSession == null) {
			throw new Exception("HttpSession is null");
		} else {
			ASLSession session = getSession(httpSession);
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
			HttpSession httpSession) {
		ASLSession session = getSession(httpSession);
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
			throws WorkspaceFolderNotFoundException, InternalErrorException,
			HomeNotFoundException {
		ASLSession session = getSession(httpSession);
		return getWorkspace(session);
	}

	private static Workspace getWorkspace(ASLSession session)
			throws WorkspaceFolderNotFoundException, InternalErrorException,
			HomeNotFoundException {

		return HomeLibrary.getUserWorkspace(session.getUsername());
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

}

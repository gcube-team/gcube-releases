/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server.util;

import java.net.URI;

import javax.servlet.http.HttpSession;

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
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Constants;



/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SessionUtil {

//	protected static Logger logger = Logger.getLogger("Logger");
	private final static String hostUrl = "http://statistical-manager.d.d4science.research-infrastructures.eu:8888";
	private static URI host = URI.create(hostUrl);
	private final static String SCOPE = "/gcube/devsec";
	
	public static final String DATASOURCE_ATTRIBUTE_NAME = "TDW.DATASOURCE";
	private static final String SM_SERVICE_ATTRIBUTE_NAME = "statisticalManagerService";
	
	
	protected static ASLSession getSession(HttpSession httpSession) {
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {

		user = Constants.DEFAULT_USER;
			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			ScopeProvider.instance.set(Constants.DEFAULT_SCOPE);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(Constants.DEFAULT_SCOPE);
			System.out.println("berfore return test session");
			System.out.println("******user found in session is null");
			return session;
//			return null;
		
		} else System.out.println("*****user found in session "+user);
		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	
	public static DataSource getDataSource(HttpSession httpSession) {
		ASLSession session = getSession(httpSession);
		return (DataSource) session.getAttribute(DATASOURCE_ATTRIBUTE_NAME);
	}
	
	public static StatisticalManagerFactory getFactory(HttpSession httpSession) {
		ASLSession session = getSession(httpSession);
		return getFactory(session);
	}
	public static StatisticalManagerFactory getFactory(ASLSession session) {
		return getFactory(session.getScope());
	}
	
	public static StatisticalManagerFactory getFactory(String scope) {
		ScopeProvider.instance.set(scope);
		// get service entry point
		if (Constants.TEST_MODE)
			return StatisticalManagerDSL.createStateful().at(host).build(); // TEST
		else
			return StatisticalManagerDSL.createStateful().build(); // IS
		
		// Try to insert in session
	}


	public static StatisticalManagerDataSpace getDataSpaceService(HttpSession httpSession) {
		ASLSession session = getSession(httpSession);
		return getDataSpaceService(session);
	}


	public static StatisticalManagerDataSpace getDataSpaceService(ASLSession session) {
		ScopeProvider.instance.set(getScope(session));
		if (Constants.TEST_MODE)
			return StatisticalManagerDSL.dataSpace().at(host).build();
		else
			return StatisticalManagerDSL.dataSpace().build();
	}
	
	public static Workspace getWorkspace(HttpSession httpSession) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException {
		ASLSession session = getSession(httpSession);
		return getWorkspace(session);
	}

	private static Workspace getWorkspace(ASLSession session) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException {
		
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


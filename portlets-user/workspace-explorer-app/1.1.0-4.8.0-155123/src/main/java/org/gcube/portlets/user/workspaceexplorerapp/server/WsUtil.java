/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.server;

import javax.servlet.http.HttpSession;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 13, 2016
 */
public class WsUtil {

	public static final Logger logger = LoggerFactory.getLogger(WsUtil.class);
	public static final String ENVIRONMENT_VARIABLE_SCOPE_NAME = "EnvironmentVariableScope";
	public static final String SESSION_SCOPE = "session_scope";

	/**
	 * Gets the workspace.
	 *
	 * @param httpSession the http session
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 * @throws UserNotFoundException the user not found exception
	 */
	public static Workspace getWorkspace(HttpSession httpSession) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException, UserNotFoundException	{
//		ASLSession session = getASLSession(httpSession);

		String scope = getScope(httpSession);
		//GET CONTEXT
		logger.info("Setting scope: "+scope);
		ScopeProvider.instance.set(scope);
		return HomeLibrary.getHomeManagerFactory().getHomeManager().getGuestLogin().getWorkspace();
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public static String getScope(HttpSession httpSession){
		String scope =  (String) httpSession.getAttribute(SESSION_SCOPE);
		logger.info("Variable "+SESSION_SCOPE + " read from httpsession is: "+scope);

		if(scope==null){
			logger.info("Variable "+SESSION_SCOPE + " is null reading from web.xml context");
			String variableScopeName = httpSession.getServletContext().getInitParameter(ENVIRONMENT_VARIABLE_SCOPE_NAME);
			logger.info("Found param-value '"+variableScopeName+"' from web context, reading its value from ENVIRONMENT");
			scope = System.getenv(variableScopeName);
			logger.info("Value of "+variableScopeName+" from ENVIRONMENT is: "+scope);
		}

		httpSession.setAttribute(ENVIRONMENT_VARIABLE_SCOPE_NAME, scope);
		return scope;
	}
}

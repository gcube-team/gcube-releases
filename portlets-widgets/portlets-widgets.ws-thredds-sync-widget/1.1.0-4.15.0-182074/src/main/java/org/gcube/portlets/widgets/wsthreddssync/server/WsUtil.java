/**
 *
 */
package org.gcube.portlets.widgets.wsthreddssync.server;

import javax.servlet.http.HttpServletRequest;

//import org.gcube.common.homelibrary.home.HomeLibrary;
//import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
//import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
//import org.gcube.common.homelibrary.home.workspace.Workspace;
//import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
//import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 25, 2016
 */
public class WsUtil {

	private static Logger logger = LoggerFactory.getLogger(WsUtil.class);


	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}
		catch (Exception ex) {
			logger.trace("Development Mode ON");
			return false;
		}
	}


	/**
	 * Checks if is session expired.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return true, if is session expired
	 * @throws Exception the exception
	 */
	public static boolean isSessionExpired(HttpServletRequest httpServletRequest) throws Exception {
		logger.trace("workspace session validating...");
		return PortalContext.getConfiguration().getCurrentUser(httpServletRequest)==null;
	}

	/**
	 * Gets the workspace from storage hub.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the workspace from storage hub
	 * @throws Exception the exception
	 */
	public Workspace getWorkspaceFromStorageHub(HttpServletRequest httpServletRequest) throws Exception {
		logger.trace("Get Workspace");
//		String scope = PortalContext.getConfiguration().getCurrentScope(httpServletRequest);

		GCubeUser user = null;
		
		try {
			String scope = PortalContext.getConfiguration().getCurrentScope(httpServletRequest);
			user = PortalContext.getConfiguration().getCurrentUser(httpServletRequest);
			if (user == null || user.getUsername().isEmpty())
				throw new Exception("Session expired");
			
			ScopeProvider.instance.set(scope);
			logger.trace("Scope provider instancied at: "+scope);
			
			logger.debug("Getting " + StorageHubWrapper.class.getSimpleName() + " for user: " + user.getUsername()
					+ " by using the scope: " + scope);

			String token =  PortalContext.getConfiguration().getCurrentUserToken(scope, user.getUsername());
			StorageHubWrapper shWrapper = new StorageHubWrapper(scope, token, false, false, true);
			return shWrapper.getWorkspace();
		} catch (Exception e) {
			logger.error("Error on getting the Workspace via SHUB wrapper", e);
			throw new Exception("Error on gettig the Workspace for userId: " + user);
		}
	}

}

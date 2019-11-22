/**
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.server.util;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;



/**
 * The Class PortalContextUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public class PortalContextUtil {


	/**
	 * Gets the user logged.
	 *
	 * @param request the request
	 * @return the user logged
	 */
	public static GCubeUser getUserLogged(HttpServletRequest request){
		return PortalContext.getConfiguration().getCurrentUser(request);
	}

	/**
	 * Gets the token for.
	 *
	 * @param scope the scope
	 * @param username the username
	 * @return the token for
	 */
	public static String getTokenFor(String scope, String username){
		return PortalContext.getConfiguration().getCurrentUserToken(scope, username);
	}


	/**
	 * Gets the current scope.
	 *
	 * @param request the request
	 * @return the current scope
	 */
	public static String getCurrentScope(HttpServletRequest request){
		return PortalContext.getConfiguration().getCurrentScope(request);
	}
}

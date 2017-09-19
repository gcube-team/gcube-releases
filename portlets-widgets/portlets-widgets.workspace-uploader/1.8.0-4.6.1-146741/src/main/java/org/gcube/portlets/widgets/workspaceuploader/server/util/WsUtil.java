/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.service.UserLocalServiceUtil;



/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 21, 2016
 */
public class WsUtil {

	public static final String NOTIFICATION_MANAGER_UPLOADER = "WS_UPLOADER_NOTIFICATION_MANAGER";
	public static final String NOTIFICATION_PORTLET_CLASS_ID = "org.gcube.portlets.user.workspace.server.GWTWorkspaceServiceImpl";
	protected static Logger logger = Logger.getLogger(WsUtil.class);

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
	public static boolean isSessionExpired(HttpServletRequest request) throws Exception {
		logger.debug("workspace session validating...");
		return PortalContext.getConfiguration().getCurrentUser(request)==null;
	}


	/**
	 * Gets the workspace.
	 *
	 * @param request the request
	 * @param scopeGroupId the scope group id
	 * @param currUserId the curr user id
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace(final HttpServletRequest request, String scopeGroupId, GCubeUser user) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException, Exception{

		if(user==null || user.getUsername().isEmpty())
			throw new Exception("Session expired");

		logger.trace("Get Workspace scope group id: "+scopeGroupId);
		PortalContext pContext = PortalContext.getConfiguration();
		String scope;
		if(isWithinPortal())
			scope = pContext.getCurrentScope(scopeGroupId);
		else
			scope = pContext.getCurrentScope(request);

		ScopeProvider.instance.set(scope);
		String username = user.getUsername();
		logger.info("Client context scope: "+ scope + " username: "+username);
		return HomeLibrary.getUserWorkspace(username);
	}

	/**
	 * Put workspace uploader in session.
	 *
	 * @param httpSession the http session
	 * @param uploader the uploader
	 * @throws Exception the exception
	 */
	public static void putWorkspaceUploaderInSession(final HttpSession httpSession, WorkspaceUploaderItem uploader) throws Exception {
		logger.trace("Put workspace uploader in session: "+uploader.getIdentifier() + ", STATUS: "+uploader.getUploadStatus());


		if(uploader.getIdentifier()==null || uploader.getIdentifier().isEmpty())
			throw new Exception("Invalid uploader");

		httpSession.setAttribute(uploader.getIdentifier(), uploader);
		logger.debug("Added uploader: "+uploader.getIdentifier() +" in session");
	}

	/**
	 * Erase workspace uploader in session.
	 *
	 * @param httpSession the http session
	 * @param uploader the uploader
	 * @throws Exception the exception
	 */
	public static void forceEraseWorkspaceUploaderInSession(final HttpSession httpSession, WorkspaceUploaderItem uploader) throws Exception	{
		logger.trace("Force Erase WorkspaceUploader workspace uploader in session: "+uploader.getIdentifier());

		if(uploader==null || uploader.getIdentifier()==null || uploader.getIdentifier().isEmpty())
			throw new Exception("Invalid uploader");

		httpSession.removeAttribute(uploader.getIdentifier());
		logger.info("Erased uploader: "+uploader.getIdentifier());

	}


	/**
	 * Erase workspace uploader in session.
	 *
	 * @param httpSession the http session
	 * @param uploader the uploader
	 * @throws Exception the exception
	 */
	public static void eraseWorkspaceUploaderInSession(final HttpSession httpSession, WorkspaceUploaderItem uploader) throws Exception
	{
		logger.trace("Erase WorkspaceUploader workspace uploader in session: "+uploader.getIdentifier() + ", erasable? "+uploader.isErasable());

		if(uploader==null || uploader.getIdentifier()==null || uploader.getIdentifier().isEmpty())
			throw new Exception("Invalid uploader");

		if(uploader.isErasable()){
			httpSession.removeAttribute(uploader.getIdentifier());
			logger.info("Erased uploader: "+uploader.getIdentifier());
		}
	}


	/**
	 * Gets the workspace uploader in session.
	 *
	 * @param request the request
	 * @param uploaderIdentifier the uploader identifier
	 * @return the workspace uploader in session
	 * @throws Exception the exception
	 */
	public static WorkspaceUploaderItem getWorkspaceUploaderInSession(final HttpServletRequest request, String uploaderIdentifier) throws Exception	{
		HttpSession httpSession = request.getSession();

		if(uploaderIdentifier==null || uploaderIdentifier.isEmpty())
			throw new Exception("Invalid uploader");

		return (WorkspaceUploaderItem) httpSession.getAttribute(uploaderIdentifier);

	}


	/**
	 * Sets the erasable workspace uploader in session.
	 *
	 * @param request the request
	 * @param uploaderIdentifier the uploader identifier
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public static boolean setErasableWorkspaceUploaderInSession(final HttpServletRequest request, String uploaderIdentifier) throws Exception {

		HttpSession httpSession = request.getSession();
		WorkspaceUploaderItem uploader = getWorkspaceUploaderInSession(request, uploaderIdentifier);

		if(uploader!=null){
			/*TODO REMOVE SESSION KEY FROM SESSION MUST BE MANAGED IN ANOTHER WAY IN ORDER TO AVOID THATH IT'S REMOVED FROM SESSION BEFORE THAT
			 * POLLING RETRIEVES STATUS, USE A FLAG REMOVED?*/
			logger.debug("Set erasable uploader: "+uploader.getIdentifier() +" in session");
			//			uploader = null;
			uploader.setErasable(true);
			putWorkspaceUploaderInSession(httpSession, uploader);
			return true;
		}
		logger.debug("Uploader not found, skipping erase from session");
		return false;
	}


	/**
	 * Gets the user id.
	 *
	 * @param request the request
	 * @return the user id
	 */
	public static String getUserId(HttpServletRequest request) {
		PortalContext pContext = PortalContext.getConfiguration();
		String username =  pContext.getCurrentUser(request).getUsername();
		logger.debug("workspace upload UserId: "+username);
		return username;
	}

}

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

import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 18, 2014
 */
public class WsUtil {

	public static final String NOTIFICATION_MANAGER_UPLOADER = "WS_UPLOADER_NOTIFICATION_MANAGER";
//	public static final String NOTIFICATION_PRODUCER = "WORKSPACE_UPLOADER_NOTIFICATION_PRODUCER";
	//	public static final String NOTIFICATION_PORTLET_CLASS_ID = WorkspaceUploadServletStream.class.getName(); //IN DEV
	public static final String NOTIFICATION_PORTLET_CLASS_ID = "org.gcube.portlets.user.workspace.server.GWTWorkspaceServiceImpl";



	protected static Logger logger = Logger.getLogger(WsUtil.class);

	//	public static boolean withoutPortal = false;

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
	 * Gets the workspace.
	 *
	 * @param httpSession the http session
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace(final HttpServletRequest request, String scopeGroupId) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException{

		logger.trace("Get Workspace");
		PortalContext pContext = PortalContext.getConfiguration();
		String username = pContext.getCurrentUser(request).getUsername();
		String scope = pContext.getCurrentScope(scopeGroupId);
		logger.trace("Client context scope: "+ scope + " username: "+username);

		ScopeProvider.instance.set(scope);
		return HomeLibrary.getUserWorkspace(username);
	}

	/**
	 * Gets the workspace.
	 *
	 * @param httpSession the http session
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace(String username, String scopeGroupId) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException{

		logger.trace("Get Workspace for user = " + username);
		PortalContext pContext = PortalContext.getConfiguration();
			String scope = pContext.getCurrentScope(scopeGroupId);
		logger.trace("Client context scope: "+ scope + " username: "+username);

		ScopeProvider.instance.set(scope);
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
	 * @param httpSession the http session
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
	 * @param httpSession the http session
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
	 * @param httpSession the http session
	 * @return the user id
	 */
	public static String getUserId(HttpServletRequest request) {
		PortalContext pContext = PortalContext.getConfiguration();
		String username =  pContext.getCurrentUser(request).getUsername();
		logger.debug("workspace upload UserId: "+username);
		return username;
	}

}

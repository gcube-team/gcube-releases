/**
 *
 */
package org.gcube.portlets.user.trainingcourse.server.hl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.trainingcourse.server.ServerUtil;
import org.gcube.portlets.user.trainingcourse.shared.WorkspaceItemInfo;
import org.gcube.portlets.user.trainingcourse.shared.WorkspaceItemInfo.Type;
import org.gcube.portlets.user.trainingcourse.shared.bean.PortalContextInfo;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 25, 2016
 */
public class WsUtil {


	protected static Logger logger = LoggerFactory.getLogger(WsUtil.class);


	/**
	 * Gets the portal context.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the portal context
	 */
	public static PortalContextInfo getPortalContext(HttpServletRequest httpServletRequest){
		PortalContext pContext = PortalContext.getConfiguration();
		//USER
		GCubeUser user = pContext.getCurrentUser(httpServletRequest);
		String username = user.getUsername();
		String fullName = user.getFullname();
		String email = user.getEmail();
		String avatarID = user.getUserAvatarId();
		String avatarURL = user.getUserAvatarURL();
		//SESSION
		String currentScope = pContext.getCurrentScope(httpServletRequest);
		String userToken = pContext.getCurrentUserToken(httpServletRequest);
		long currGroupId = pContext.getCurrentGroupId(httpServletRequest);

		return new PortalContextInfo(username, fullName, email, avatarID, avatarURL, currentScope, userToken, currGroupId);
	}


	/**
	 * Gets the portal context.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param overrideScope the override scope
	 * @return the portal context
	 */
	public static PortalContextInfo getPortalContext(HttpServletRequest httpServletRequest, String overrideScope){
		PortalContextInfo info = getPortalContext(httpServletRequest);
		info.setCurrentScope(overrideScope);
		return info;
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
	 * Gets the workspace.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace(HttpServletRequest httpServletRequest) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		logger.trace("Get Workspace");
		PortalContextInfo info = getPortalContext(httpServletRequest);
		logger.trace("PortalContextInfo: "+info);

		ScopeProvider.instance.set(info.getCurrentScope());
		logger.info("Scope provider instancied: "+info.getCurrentScope());

		Workspace workspace = HomeLibrary.getUserWorkspace(info.getUsername());
		return workspace;
	}


	/**
	 * Gets the workspace.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param contextID the context id
	 * @param user the user
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace(HttpServletRequest httpServletRequest, String contextID, GCubeUser user) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		logger.info("Get workspace using contextID: "+contextID +", current user: "+user.getUsername());
		String currentScope;

		if(ServerUtil.isWithinPortal())
			currentScope = PortalContext.getConfiguration().getCurrentScope(contextID);
		else{
			currentScope = PortalContext.getConfiguration().getCurrentScope(httpServletRequest);
			logger.warn("STARTING IN TEST MODE!!!! USING SCOPE: "+currentScope);
		}

		logger.info("For ContextID: "+contextID +", read scope from Portal Context: "+currentScope);
		PortalContextInfo info = getPortalContext(httpServletRequest, currentScope);
		logger.trace("PortalContextInfo: "+info);

		ScopeProvider.instance.set(info.getCurrentScope());
		logger.trace("Scope provider instancied");

		String username = null;
		try {
			if(user.getUsername().compareTo(info.getUsername())!=0){
				logger.debug("Gcube user read from Portal Context "+user.getUsername()+" is different by GCubeUser passed, using the second one: "+info.getUsername());
				username = user.getUsername();
			}

		} catch (Exception e) {
			logger.error("Error comparing username read from input parameter and Portal context");
		}

		if(username!=null)
			info.setUsername(username);

		Workspace workspace = HomeLibrary.getUserWorkspace(info.getUsername());
		return workspace;

	}

	/**
	 * Gets the user id.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the user id
	 */
	public static String getUserId(HttpServletRequest httpServletRequest) {

		PortalContextInfo info = getPortalContext(httpServletRequest);
		return info.getUsername();
	}

	/**
	 * Checks if is vre.
	 *
	 * @param scope the scope
	 * @return true, if is vre
	 */
	public static boolean isVRE(String scope){

		int slashCount = StringUtils.countMatches(scope, "/");

		if(slashCount < 3){
			logger.trace("currentScope is not VRE");
			return false;
		}

		logger.trace("currentScope is VRE");
		return true;

	}
	
	
	/**
	 * To workspace item info.
	 *
	 * @param item the item
	 * @return the workspace item info
	 * @throws InternalErrorException the internal error exception
	 */
	public static WorkspaceItemInfo toWorkspaceItemInfo(WorkspaceItem item) throws InternalErrorException{

		String mimeType = "";
		WorkspaceItemInfo.Type itemType = null;
		String publicLink = null;
		
		switch (item.getType()) {

		case FOLDER:
			itemType = Type.FOLDER;
			break;

		case FOLDER_ITEM:
			itemType = Type.FILE;
			FolderItem folderItem = (FolderItem) item;
			publicLink = folderItem.getPublicLink(false);
			try {
				mimeType = folderItem.getMimeType();
			} catch (InternalErrorException e) {
				//silent
			}
			break;
		default:
			break;
		}
		
		List<String> sharedWith = null;
		if (item.isFolder() && item.isShared()) {
			if(item instanceof WorkspaceSharedFolder) {
				WorkspaceSharedFolder folder = (WorkspaceSharedFolder) item;
				sharedWith = folder.getMembers();
			}
		}
		
		String parentId = null;
		try{
			parentId = item.getParent()!=null?item.getParent().getId():null;
		}catch (Exception e) {
			// TODO: handle exception
		}
		return new WorkspaceItemInfo(item.getId(), item.getName(), item.getDescription(), mimeType , itemType, sharedWith, publicLink, parentId, item.isFolder());
	}

}

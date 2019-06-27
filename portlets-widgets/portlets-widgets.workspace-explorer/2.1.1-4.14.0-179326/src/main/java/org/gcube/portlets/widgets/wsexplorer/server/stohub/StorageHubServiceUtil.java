package org.gcube.portlets.widgets.wsexplorer.server.stohub;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.VreFolder;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


/**
 * The Class StorageHubServiceUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Apr 9, 2019
 */
public class StorageHubServiceUtil {
	private static Log _log = LogFactoryUtil.getLog(StorageHubServiceUtil.class);
	public static final String ACCOUNTING_HL_NODE_NAME = "hl:accounting";

	/**
	 * Gets the root.
	 *
	 * @param request the request
	 * @return the root
	 */
	public static Item getRoot(HttpServletRequest request) {
		PortalContext pContext = PortalContext.getConfiguration();
		String userName = pContext.getCurrentUser(request).getUsername();
		String scope = pContext.getCurrentScope(request);
		String authorizationToken = pContext.getCurrentUserToken(scope, userName);
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(authorizationToken);
		WorkspaceManagerClient client = AbstractPlugin.workspace().build();
		Item itemRoot = client.getWorkspace(ACCOUNTING_HL_NODE_NAME);
		return itemRoot;
	}
	
// /**
//	 * Gets the notification manager to storage hub.
//	 *
//	 * @param httpServletRequest the http servlet request
//	 * @return the notification manager to storage hub
//	 */
//	public static NotificationsManager getNotificationManagerToStorageHub(HttpServletRequest httpServletRequest)
//	{
//		PortalContextInfo info = getPortalContext(httpServletRequest);
//		HttpSession session = httpServletRequest.getSession();
//		NotificationsManager notifMng = (NotificationsManager) session.getAttribute(NOTIFICATION_MANAGER_TO_STORAGEHUB);
//
//		if (notifMng == null) {
//			try{
//				logger.trace("Create new NotificationsManager for user: "+info.getUsername());
//				logger.trace("New ApplicationNotificationsManager with portlet class name: "+NOTIFICATION_PORTLET_CLASS_ID);
//				SocialNetworkingSite site = new SocialNetworkingSite(httpServletRequest);
//				SocialNetworkingUser curser = new SocialNetworkingUser(info.getUsername(), info.getUserEmail(), info.getUserFullName(), info.getUserAvatarID());
//				notifMng = new ApplicationNotificationsManager(site, info.getCurrentScope(), curser, NOTIFICATION_PORTLET_CLASS_ID);
//				session.setAttribute(NOTIFICATION_MANAGER_TO_STORAGEHUB, notifMng);
//			}catch (Exception e) {
//				logger.error("An error occurred instancing ApplicationNotificationsManager for user: "+info.getUsername(),e);
//			}
//		}
//
//		return notifMng;
//	}

	/**
 * Gets the item.
 *
 * @param request the request
 * @param itemId the item id
 * @return the item
 * @throws Exception the exception
 */
public static Item getItem(HttpServletRequest request, String itemId) throws Exception {
		PortalContext pContext = PortalContext.getConfiguration();
		String userName = pContext.getCurrentUser(request).getUsername();
		String scope = pContext.getCurrentScope(request);
		String authorizationToken = pContext.getCurrentUserToken(scope, userName);
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(authorizationToken);
		ItemManagerClient client = AbstractPlugin.item().build();
		Item toReturn = client.get(itemId, ACCOUNTING_HL_NODE_NAME);
		return toReturn;
	}

	/**
	 * Gets the parents.
	 *
	 * @param request the request
	 * @param itemId the item id
	 * @return the parents
	 * @throws Exception the exception
	 */
	public static List<? extends Item> getParents(HttpServletRequest request, String itemId) throws Exception {
		PortalContext pContext = PortalContext.getConfiguration();
		String userName = pContext.getCurrentUser(request).getUsername();
		String scope = pContext.getCurrentScope(request);
		String authorizationToken = pContext.getCurrentUserToken(scope, userName);
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(authorizationToken);
		ItemManagerClient client = AbstractPlugin.item().build();
		List<? extends Item> toReturn = client.getAnchestors(itemId, ACCOUNTING_HL_NODE_NAME);
		return toReturn;
	}

	/**
	 * Creates the folder.
	 *
	 * @param request the request
	 * @param parentId the parent id
	 * @param name the name
	 * @param description the description
	 * @return the folder item
	 * @throws Exception the exception
	 */
	public static FolderItem createFolder(HttpServletRequest request, String parentId, String name, String description) throws Exception {
		PortalContext pContext = PortalContext.getConfiguration();
		String userName = pContext.getCurrentUser(request).getUsername();
		String scope = pContext.getCurrentScope(request);
		String authorizationToken = pContext.getCurrentUserToken(scope, userName);
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(authorizationToken);
		ItemManagerClient client = AbstractPlugin.item().build();
		String createdFolderId = client.createFolder(parentId, name, description);
		return (FolderItem) client.get(createdFolderId, ACCOUNTING_HL_NODE_NAME);
	}

	/**
	 * Gets the VRE folders id.
	 *
	 * @param request the request
	 * @return the VRE Folders Id
	 */
	public static String getVREFoldersId(HttpServletRequest request) {
		PortalContext pContext = PortalContext.getConfiguration();
		String userName = pContext.getCurrentUser(request).getUsername();
		String scope = pContext.getCurrentScope(request);
		String authorizationToken = pContext.getCurrentUserToken(scope, userName);
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(authorizationToken);
		String toReturn = "";
		try {
			WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
			try {
				List<? extends Item> list = wsclient.getVreFolders("hl:accounting");
				toReturn =list.iterator().next().getParentId();
			} catch (Exception e) {
				_log.info("This user has no VRE Folders", e);
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}


	/**
	 * Gets the user ACL for folder id.
	 *
	 * @param request the request
	 * @param folderId the folder id
	 * @return the user ACL for folder id
	 * @throws Exception the exception
	 */
	public static String getUserACLForFolderId(HttpServletRequest request, String folderId) throws Exception {
		PortalContext pContext = PortalContext.getConfiguration();
		String userName = pContext.getCurrentUser(request).getUsername();
		String scope = pContext.getCurrentScope(request);
		String authorizationToken = pContext.getCurrentUserToken(scope, userName);
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(authorizationToken);

		Item theItem = getItem(request, folderId);

		//IS IT REALLY A FOLDER?
		if (theItem instanceof FolderItem){
			//THE FOLDER IS NOT SHARED
			if (!theItem.isShared()) {
				return "OWNER";
			} else {
			//HERE THE FOLDER IS SHARED
				ItemManagerClient client = AbstractPlugin.item().build();
	    		FolderItem folderItem = (FolderItem) theItem;
	    		SharedFolder sharedfolder;
	    		if (theItem instanceof SharedFolder || theItem instanceof VreFolder) {
	    			///THE FolderItem IS THE ROOT SHARED FOLDER
	    			sharedfolder = (SharedFolder) folderItem;

	    		}else{
	    			//THE FolderItem IS SON OF ROOT SHARED FOLDER SO REQUESTING THE ROOT SHARED FOLDER
	    			sharedfolder = (SharedFolder) client.getRootSharedFolder(folderItem.getId());
	    		}

				List<ACL> acls = client.getACL(sharedfolder.getId());
				boolean found = false; //this is needed because in case o VRE Folder the permission is assigned to the group and not to the user.
				for (ACL acl : acls) {
					if (acl.getPricipal().compareTo(userName) == 0) {
						found = true;
						return acl.getAccessTypes().get(0).toString();
					}
				}

				if (!found && sharedfolder.isVreFolder()) {
					for (ACL acl : acls) {
						if (acl.getPricipal().startsWith(pContext.getInfrastructureName()))
							return acl.getAccessTypes().get(0).toString();
					}
				}

			}
		}
		return "UNDEFINED";
	}

	/**
	 * Gets the item children count.
	 *
	 * @param request the request
	 * @param itemId the item id
	 * @return the item children count
	 * @throws Exception the exception
	 */
	public static int getItemChildrenCount(HttpServletRequest request, String itemId) throws Exception {
		PortalContext pContext = PortalContext.getConfiguration();
		String userName = pContext.getCurrentUser(request).getUsername();
		String scope = pContext.getCurrentScope(request);
		String authorizationToken = pContext.getCurrentUserToken(scope, userName);
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(authorizationToken);
		ItemManagerClient client = AbstractPlugin.item().build();
		return client.childrenCount(itemId);
	}
}

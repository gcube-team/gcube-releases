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


public class StorageHubServiceUtil {
	private static Log _log = LogFactoryUtil.getLog(StorageHubServiceUtil.class);
	public static final String ACCOUNTING_HL_NODE_NAME = "hl:accounting";

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

	public static Item getItem(HttpServletRequest request, String itemId) {
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

	public static List<? extends Item> getParents(HttpServletRequest request, String itemId) {
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

	public static FolderItem createFolder(HttpServletRequest request, String parentId, String name, String description) {
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
	 *
	 * @param request
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


	public static String getUserACLForFolderId(HttpServletRequest request, String folderId) {
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
	 *
	 */
	public static int getItemChildrenCount(HttpServletRequest request, String itemId) {
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

package org.gcube.portlets.user.wswidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.portlets.user.wswidget.shared.AuthorizedUser;
import org.gcube.portlets.user.wswidget.shared.WSItem;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class StorageHubServiceUtil {
	private static Log _log = LogFactoryUtil.getLog(StorageHubServiceUtil.class);
	/**
	 * 
	 * @param authUser
	 * @param itemId
	 * @param itemName
	 * @param from
	 * @param offset
	 * @return
	 */
	public static List<WSItem> getItemChildren(AuthorizedUser authUser, String itemId, String itemName, int from, int offset)  {
		WSItem toReturn = new WSItem(itemId, itemName, true);
		_log.debug("getItemChildren folder/item = " + itemId);
		ArrayList<WSItem> children = new ArrayList<>();
		SecurityTokenProvider.instance.set(authUser.getSecurityToken());
		ItemManagerClient client = AbstractPlugin.item().build();
		List<? extends Item> theChildren = null;
		try {
			if (offset >= 0) {
				int limit = offset;
				theChildren = client.getChildren(itemId, from, limit, false, "hl:accounting");
			}
			else { //all the items
				theChildren = client.getChildren(itemId, false, "hl:accounting");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		if (theChildren == null || theChildren.isEmpty()) {
			toReturn.setChildren(children);
			_log.debug("*** Returning empty ");
			return new ArrayList<>();
		}
		for (Item workspaceItem : theChildren) {
			WSItem toAdd = ItemBuilder.getItem(toReturn, workspaceItem, workspaceItem.getPath(), authUser.getUser().getUsername());
			children.add(toAdd);
		}
		toReturn.setChildren(children);
		Collections.sort(toReturn.getChildren(), new ItemComparator());
		_log.debug("*** Returning children size: "+toReturn.getChildren().size());
		return children;
	}
	/**
	 * 
	 */
	public static int getItemChildrenCount(ResourceRequest request, String itemId) {
		String userName = Utils.getCurrentUser(request).getUsername();
		String scope = Utils.getCurrentContext(request);
		String authorizationToken = Utils.getCurrentUserToken(scope, userName);
		SecurityTokenProvider.instance.set(authorizationToken);
		ItemManagerClient client = AbstractPlugin.item().build();
		int toReturn = -1;
		try {
			 client.childrenCount(itemId, false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * 
	 * @param authUser
	 * @param from
	 * @param offset
	 * @return
	 */
	public static List<WSItem> getRootChildren(AuthorizedUser authUser, int from, int offset)  {
		try {
			SecurityTokenProvider.instance.set(authUser.getSecurityToken());
			WorkspaceManagerClient client = AbstractPlugin.workspace().build();
			Item itemRoot = client.getWorkspace("hl:accounting");
			WSItem root = new WSItem(itemRoot.getId(), Utils.HOME_LABEL, true);
			root.setIsRoot(true);
			return getItemChildren(authUser, itemRoot.getId(), Utils.HOME_LABEL, from, offset);
		} catch (Exception e) {
			_log.error("Error during root retrieving", e);
		}
		return null;
	}
	/**
	 * 
	 * @param authUser
	 * @param limit
	 * @return
	 */
	public static List<WSItem> getRecentItems(AuthorizedUser authUser) {
		WSItem toReturn = new WSItem("recents", "Recent Documents", true);
		_log.debug("getRecentItems ");
		ArrayList<WSItem> children = new ArrayList<>();
		SecurityTokenProvider.instance.set(authUser.getSecurityToken());
		WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
		List<? extends Item> theChildren = wsclient.getRecentModifiedFilePerVre();


		if (theChildren == null || theChildren.isEmpty()) {
			toReturn.setChildren(children);
			_log.debug("*** Returning empty ");
			return new ArrayList<>();
		}
		for (Item workspaceItem : theChildren) {
			WSItem toAdd = ItemBuilder.getItem(toReturn, workspaceItem, workspaceItem.getPath(), authUser.getUser().getUsername());
			children.add(toAdd);
		}
		toReturn.setChildren(children);
		_log.debug("*** Returning recents items size: "+toReturn.getChildren().size());
		return children;
	}
	/**
	 * 
	 * @param request
	 * @return the id of the VRE Folder associated to the given context
	 */
	public static String getWorkspaceFolderURL(HttpServletRequest request) {
		String userName = Utils.getCurrentUser(request).getUsername();
		String scope = Utils.getCurrentContext(request);
		String authorizationToken = Utils.getCurrentUserToken(scope, userName);
		SecurityTokenProvider.instance.set(authorizationToken);
		String siteLandingPagePath = PortalContext.getConfiguration().getSiteLandingPagePath(request);
		String toReturn = siteLandingPagePath;

		try {
			WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
			String itemId = wsclient.getVreFolder("hl:accounting").getId();
			toReturn = new String(new StringBuffer(siteLandingPagePath)
					.append(GCubePortalConstants.USER_WORKSPACE_FRIENDLY_URL)
					.append("?itemid=")
					.append(itemId));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * 
	 * @param request
	 * @return the VRE Folders Id
	 */
	public static String getVREFoldersId(HttpServletRequest request) {
		String userName = Utils.getCurrentUser(request).getUsername();
		String scope = Utils.getCurrentContext(request);
		String authorizationToken = Utils.getCurrentUserToken(scope, userName);
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





}

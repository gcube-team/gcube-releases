package org.gcube.portlets.user.takecourse;

import java.util.List;

import javax.portlet.RenderRequest;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.trainingmodule.TrainingModuleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.util.PortalUtil;

public class Utils {
	private static Log _log = LogFactoryUtil.getLog(Utils.class);
	public static UserManager UMAN = new LiferayUserManager();
	public static String getIconImage(String type) {
		switch (type) {
		case "DOC":
			return "docx.png";
		case "XLS":
			return "xls.png";
		case "PPT":
			return "pptx.png";
		case "PDF":
			return "pdf.png";
		case "IMAGE":
			return "jpeg.png";
		case "MOVIE":
			return "avi.png";
		case "HTML":
			return "html.png";
		case "RAR":
			return "rar.png";
		case "ZIP":
			return "zip.png";
		default:
			return "default.png";
		}
	}

	public static GCubeUser getCurrentUser(RenderRequest request) {
		long userId;
		try {
			userId = PortalUtil.getUser(request).getUserId();
			return getCurrentUser(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}

	public static GCubeUser getCurrentUserByUsername(String username) {
		try {
			return UMAN.getUserByUsername(username);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}


	public static GCubeUser getCurrentUser(long userId) {
		try {
			return UMAN.getUserById(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}


	public static String getCurrentContext(RenderRequest request) {
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			return getCurrentContext(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentContext(long groupId) {
		try {
			PortalContext pContext = PortalContext.getConfiguration(); 
			return pContext.getCurrentScope(""+groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static String setReadAndgetPercentageReadFolder(String folderId, String workspaceItemId, String username, String context) {
		String authorizationToken = PortalContext.getConfiguration().getCurrentUserToken(context, username);
		SecurityTokenProvider.instance.set(authorizationToken);
		ScopeProvider.instance.set(context);
		Workspace ws = null;
		try {		
			ws = HomeLibrary.getUserWorkspace(username);
			List<WorkspaceItem> items = null;
			WorkspaceItem itemToSetRead = ws.getItem(workspaceItemId);
			itemToSetRead.markAsRead(true);
			_log.info("item " + itemToSetRead.getName() + " marked as read");  

//			WorkspaceFolder sharedFolder = (WorkspaceFolder) ws.getItem(folderId);
//			String folderName = sharedFolder.getName();
//			_log.info("getPercentageReadFolder="+folderName + " for " + username);
//			items = sharedFolder.getChildren();
//			int readItems = 0;
//			for (WorkspaceItem item : items) {
//				List<AccountingEntry> entries = item.getAccounting();
//
//				for (AccountingEntry entry : entries) {
//					if (entry.getEntryType() == AccountingEntryType.READ && entry.getUser().compareTo(username)==0) {
//						readItems++;
//						break;
//					}
//				}				
//
//			}
//			JSONObject fileObject = JSONFactoryUtil.createJSONObject();
//			fileObject.put("readFiles", readItems);
//			fileObject.put("totalFiles", items.size());
//			return fileObject.toString();
			return "read";
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return "0";
	}

	/**
	 * Scope to HL group.
	 *
	 * @param scope the scope
	 * @return the string
	 */
	public static String scopeToHLGroup(String scope) {

		if(scope==null)
			return null;

		String contextAsGroup = scope.substring(1,scope.length()).replaceAll("/", TrainingModuleManager.HL_GROUP_SEPARATOR);
		_log.debug("Scope "+scope+ " to group: "+contextAsGroup);
		return contextAsGroup;

	}



	/**
	 * Group HL to scope.
	 *
	 * @param group the group
	 * @return the string
	 */
	public static String groupHLToScope(String group) {

		if(group==null)
			return null;

		String groupAsScope = group.replaceAll(TrainingModuleManager.HL_GROUP_SEPARATOR, "/");
		groupAsScope="/"+groupAsScope;
		_log.debug("Group "+group+ " to scope: "+groupAsScope);
		return groupAsScope;

	}


}

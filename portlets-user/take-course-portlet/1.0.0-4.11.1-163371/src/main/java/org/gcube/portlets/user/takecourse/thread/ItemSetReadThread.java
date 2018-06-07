package org.gcube.portlets.user.takecourse.thread;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.takecourse.Utils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ItemSetReadThread implements Runnable {
	private static Log _log = LogFactoryUtil.getLog(ItemSetReadThread.class);
	private String workspaceItemId;
	private long userId;
	private long groupId;
	
	public ItemSetReadThread(String workspaceItemId, long userId, long groupId) {
		super();
		this.workspaceItemId = workspaceItemId;
		this.userId = userId;
		this.groupId = groupId;
	}

	public void run() {
		String context =  Utils.getCurrentContext(groupId); 
		String username =  Utils.getCurrentUser(userId).getUsername();
		String authorizationToken = PortalContext.getConfiguration().getCurrentUserToken(context, username);
		SecurityTokenProvider.instance.set(authorizationToken);
		ScopeProvider.instance.set(context);
		Workspace ws = null;
		try {
			ws = HomeLibrary.getUserWorkspace(username);
			WorkspaceItem item = ws.getItem(workspaceItemId);
			item.markAsRead(true);
			_log.info(username + ": item read marked as true name: " + item.getName() +" id: " + workspaceItemId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

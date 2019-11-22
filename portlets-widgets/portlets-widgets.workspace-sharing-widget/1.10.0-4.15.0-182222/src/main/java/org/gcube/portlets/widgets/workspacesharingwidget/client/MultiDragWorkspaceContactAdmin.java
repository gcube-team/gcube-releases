/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.admin.DialogMultiDragContactAdmin;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.google.gwt.core.client.GWT;

/**
 * @author Francesco Mangiacrapa Jul 30, 2014
 *
 *         A simple multi drag dialog to manage users administrators of a
 *         workspace item
 */
public class MultiDragWorkspaceContactAdmin {

	private DialogMultiDragContactAdmin dialogMultiDragContactAdmin;
		
	/**
	 * Load administrators or shared users to workspace item id
	 *
	 * @param workspaceItemId
	 *            Item id
	 * 
	 * @param workspaceItemId
	 */
	public MultiDragWorkspaceContactAdmin(String workspaceItemId) {
		GWT.log("MultiDragWorkspaceContactAdmin(): "+workspaceItemId);
		try {
		dialogMultiDragContactAdmin = new DialogMultiDragContactAdmin(workspaceItemId);
		} catch (Throwable e){
			GWT.log("Error in MultiDragWorkspaceContactAdmin(): "+e.getLocalizedMessage(),e);
		}
	}

	/**
	 * 
	 * @return the multi drag DialogMultiDragContact
	 */
	public DialogMultiDragContactAdmin getDialogMultiDragContact() {
		return dialogMultiDragContactAdmin;
	}

	public void show() {
		dialogMultiDragContactAdmin.show();
	}

	public List<InfoContactModel> getTargetContacts() {
		return dialogMultiDragContactAdmin.getMultiDrag().getTargetListContactWithMyLogin();
	}

}

package org.gcube.portlets.widgets.workspacesharingwidget.client.rpc;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.ACL_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.AllowAccess;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ExtendedWorkspaceACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ReportAssignmentACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.UserVRE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorkspaceSharingServiceAsync {

	public static WorkspaceSharingServiceAsync INSTANCE = (WorkspaceSharingServiceAsync) GWT
			.create(WorkspaceSharingService.class);

	void getAllContacts(AsyncCallback<List<InfoContactModel>> callback);
		
	void getUsersByKeyword(String keyword, AsyncCallback<List<InfoContactModel>> callback);
	
	void getUserVREList(AsyncCallback<List<UserVRE>> callback);
	
	void getAllContactsByVRE(UserVRE gGroup, AsyncCallback<List<InfoContactModel>> callback);

	void getListUserSharedByFolderSharedId(String folderSharedId, AsyncCallback<List<InfoContactModel>> callback);

	void getOwnerByItemId(String itemId, AsyncCallback<InfoContactModel> callback);

	void getACLs(AsyncCallback<List<WorkspaceACL>> callback);

	void getMyLogin(AsyncCallback<String> callback);

	void getFileModelByWorkpaceItemId(String itemId, AsyncCallback<FileModel> callback);

	void isSessionExpired(AsyncCallback<Boolean> callback);

	void shareFolder(String id, String name, String description, String parentId, List<InfoContactModel> listContacts,
			boolean isNewFolder, WorkspaceACL acl, AsyncCallback<Boolean> callback);

	void getInfoContactModelsFromCredential(List<CredentialModel> listAlreadySharedContact,
			AsyncCallback<List<InfoContactModel>> callback);

	void getAdministratorsByFolderId(String sharedFolderId, AsyncCallback<List<InfoContactModel>> callback);

	void getACLsForSharedFolderId(String itemID, AsyncCallback<WorkspaceACL> asyncCallback);

	void getACLsDescriptionForSharedFolderId(String folderId, AsyncCallback<String> callback);

	void unSharedFolderByFolderSharedId(String folderId, AsyncCallback<Boolean> asyncCallback);

	void getUserACLForFolderId(String folderId, AsyncCallback<List<ExtendedWorkspaceACL>> callback);

	void getACLsDescriptionForWorkspaceItemById(String workspaceItemId, AsyncCallback<String> callback);

	void accessToFolderLink(String itemId, AsyncCallback<AllowAccess> callback);

	void setACLs(String folderId, List<String> listLogins, String aclType, AsyncCallback<Void> callback);

	void updateACLForVREbyGroupName(String folderId, ACL_TYPE aclType, AsyncCallback<Void> callback);

	void validateACLToUser(String folderId, List<String> listLogins, String aclType,
			AsyncCallback<ReportAssignmentACL> callback);

	void addAdministratorsByFolderId(String itemId, List<String> listContactLogins, AsyncCallback<Boolean> callback);

	
}

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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("workspacesharing")
public interface WorkspaceSharingService extends RemoteService {

	AllowAccess accessToFolderLink(String itemId) throws Exception;

	List<InfoContactModel> getAllContacts() throws Exception;
	
	List<InfoContactModel> getUsersByKeyword(String keyword) throws Exception;

	List<UserVRE> getUserVREList() throws Exception;

	List<InfoContactModel> getAllContactsByVRE(UserVRE gGroup) throws Exception;

	List<InfoContactModel> getListUserSharedByFolderSharedId(String folderSharedId) throws Exception;

	InfoContactModel getOwnerByItemId(String itemId) throws Exception;

	List<WorkspaceACL> getACLs() throws Exception;

	String getMyLogin() throws Exception;

	FileModel getFileModelByWorkpaceItemId(String itemId) throws Exception;

	boolean isSessionExpired() throws Exception;

	boolean shareFolder(String id, String name, String description, String parentId,
			List<InfoContactModel> listContacts, boolean isNewFolder, WorkspaceACL acl) throws Exception;

	List<InfoContactModel> getInfoContactModelsFromCredential(List<CredentialModel> listAlreadySharedContact)
			throws Exception;

	List<InfoContactModel> getAdministratorsByFolderId(String sharedFolderId) throws Exception;

	WorkspaceACL getACLsForSharedFolderId(String itemID) throws Exception;

	String getACLsDescriptionForSharedFolderId(String folderId) throws Exception;

	boolean unSharedFolderByFolderSharedId(String folderId) throws Exception;

	List<ExtendedWorkspaceACL> getUserACLForFolderId(String folderId) throws Exception;

	String getACLsDescriptionForWorkspaceItemById(String workspaceItemId) throws Exception;

	void setACLs(String folderId, List<String> listLogins, String aclType) throws Exception;

	void updateACLForVREbyGroupName(String folderId, ACL_TYPE aclType) throws Exception;

	ReportAssignmentACL validateACLToUser(String folderId, List<String> listLogins, String aclType) throws Exception;

	boolean addAdministratorsByFolderId(String itemId, List<String> listContactLogins) throws Exception;

	
}

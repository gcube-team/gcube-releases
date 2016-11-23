package org.gcube.portlets.widgets.workspacesharingwidget.client.rpc;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("workspacesharing")
public interface WorkspaceSharingService extends RemoteService {

	List<InfoContactModel> getAllContacts(boolean readGroupsFromHL,
			boolean readGroupsFromPortal) throws Exception;

	/**
	 * @param folderSharedId
	 * @return
	 * @throws Exception
	 */
	List<InfoContactModel> getListUserSharedByFolderSharedId(
			String folderSharedId) throws Exception;

	/**
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	InfoContactModel getOwnerByItemId(String itemId) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	List<WorkspaceACL> getACLs() throws Exception;

	/**
	 * @return
	 */
	String getMyLogin();

	/**
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	FileModel getFileModelByWorkpaceItemId(String itemId) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	boolean isSessionExpired() throws Exception;

	/**
	 * @param folder
	 * @param listContacts
	 * @param isNewFolder
	 * @param acl
	 * @return
	 * @throws Exception
	 */
	boolean shareFolder(FileModel folder, List<InfoContactModel> listContacts,
			boolean isNewFolder, WorkspaceACL acl) throws Exception;

	/**
	 * @param listAlreadySharedContact
	 * @return
	 * @throws Exception
	 */
	List<InfoContactModel> getInfoContactModelsFromCredential(List<CredentialModel> listAlreadySharedContact) throws Exception;

	List<InfoContactModel> getAdministratorsByFolderId(String sharedFolderId) throws Exception;

	WorkspaceACL getACLsForSharedFolderId(String itemID) throws Exception;

	/**
	 * @param folderId
	 * @return
	 * @throws Exception
	 */
	String getACLsDescriptionForSharedFolderId(String folderId)
			throws Exception;

}

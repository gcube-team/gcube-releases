package org.gcube.portlets.widgets.workspacesharingwidget.client.rpc;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorkspaceSharingServiceAsync
{

    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static WorkspaceSharingServiceAsync instance;

        public static final WorkspaceSharingServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (WorkspaceSharingServiceAsync) GWT.create( WorkspaceSharingService.class );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }


    void getAllContacts(boolean readGroupsFromHL, boolean readGroupsFromPortal, AsyncCallback<List<InfoContactModel>> callback);


	void getListUserSharedByFolderSharedId(String folderSharedId,
			AsyncCallback<List<InfoContactModel>> callback);


	void getOwnerByItemId(String itemId,
			AsyncCallback<InfoContactModel> callback);


	void getACLs(AsyncCallback<List<WorkspaceACL>> callback);


	void getMyLogin(AsyncCallback<String> callback);


	void getFileModelByWorkpaceItemId(String itemId,
			AsyncCallback<FileModel> callback);


	void isSessionExpired(AsyncCallback<Boolean> callback);


	void shareFolder(FileModel folder, List<InfoContactModel> listContacts,
			boolean isNewFolder, WorkspaceACL acl,
			AsyncCallback<Boolean> callback);


	void getInfoContactModelsFromCredential(
			List<CredentialModel> listAlreadySharedContact,
			AsyncCallback<List<InfoContactModel>> callback);


	/**
	 * @param sharedFolderId
	 */
	void getAdministratorsByFolderId(String sharedFolderId, AsyncCallback<List<InfoContactModel>> callback);


	/**
	 * @param itemID
	 * @param asyncCallback
	 */
	void getACLsForSharedFolderId(String itemID,
			AsyncCallback<WorkspaceACL> asyncCallback);


	void getACLsDescriptionForSharedFolderId(String folderId,
			AsyncCallback<String> callback);


}

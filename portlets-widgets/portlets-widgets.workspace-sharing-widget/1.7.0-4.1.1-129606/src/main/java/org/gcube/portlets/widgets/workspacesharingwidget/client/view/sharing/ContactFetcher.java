package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContactFetcher {
	
	public void getListContact(AsyncCallback<List<InfoContactModel>> callback, boolean reloadList, boolean readGroupsFromHL, boolean readGroupsFromPortal);
	public void getListSharedUserByFolderId(String sharedFolderId, AsyncCallback<List<InfoContactModel>> callback);
	public List<InfoContactModel> getExclusiveContactsFromAllContact(List<InfoContactModel> listSharedUser);
	public void getOwner(String sharedFolderId, AsyncCallback<InfoContactModel> callback);

	/**
	 * @param listAlreadySharedContact
	 * @param callback
	 */
	public void getInfoContactModelsFromCredential(
			List<CredentialModel> listAlreadySharedContact,
			AsyncCallback<List<InfoContactModel>> callback);
	
	public void getAdministratorsByFolderId(String sharedFolderId, AsyncCallback<List<InfoContactModel>> callback);

}

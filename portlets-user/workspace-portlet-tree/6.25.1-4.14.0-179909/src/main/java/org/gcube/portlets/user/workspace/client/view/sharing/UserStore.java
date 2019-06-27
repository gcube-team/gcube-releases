package org.gcube.portlets.user.workspace.client.view.sharing;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingServiceAsync;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class UserStore.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 */
public class UserStore implements ContactFetcher{

	public static List<InfoContactModel> listAllContact = null;

//    public boolean syncronizeCleanSharedUser = false;

	/**
	 * Instantiates a new user store.
	 */
	public UserStore() {
		loadAllUsersFromServer(null);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.view.sharing.ContactFetcher#getListContact(com.google.gwt.user.client.rpc.AsyncCallback, boolean)
	 */
	@Override
	public void getListContact(AsyncCallback<List<InfoContactModel>> callback, boolean reloadList){

		if(reloadList || listAllContact==null || listAllContact.size()==0)
			loadAllUsersFromServer(callback);
		else
			callback.onSuccess(listAllContact);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.view.sharing.ContactFetcher#getListSharedUserByFolderId(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getListSharedUserByFolderId(String sharedFolderId, AsyncCallback<List<InfoContactModel>> callback){
		loadSharedUserBySharedFolderId(sharedFolderId, callback);
	}

	/**
	 * Load all users from server.
	 *
	 * @param callback the callback
	 */
	private void loadAllUsersFromServer(final AsyncCallback<List<InfoContactModel>> callback){

		listAllContact = new ArrayList<InfoContactModel>();

		WorkspaceSharingServiceAsync.INSTANCE.getAllContacts(new AsyncCallback<List<InfoContactModel>>() {

			@Override
			public void onSuccess(List<InfoContactModel> result) {
				GWT.log("loaded "+result.size() + " contacts from server");
				listAllContact = result;
				if(callback!=null)
					callback.onSuccess(listAllContact);
			}

			@Override
			public void onFailure(Throwable caught) {
				//TODO TEMPORARY SOLUTION
				//new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR +" retrieving user "  + ConstantsExplorer.TRY_AGAIN, null);
				if(callback!=null)
					callback.onFailure(caught);
			}
		});
	}

	/**
	 * Load shared user by shared folder id.
	 *
	 * @param sharedFolderId the shared folder id
	 * @param callback the callback
	 */
	private void loadSharedUserBySharedFolderId(final String sharedFolderId, final AsyncCallback<List<InfoContactModel>> callback){

//		comboSharedUsers.mask("Loading users");

		WorkspaceSharingServiceAsync.INSTANCE.getListUserSharedByFolderSharedId(sharedFolderId, new AsyncCallback<List<InfoContactModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR +" retrieving user. "  + ConstantsExplorer.TRY_AGAIN, null);
				if(callback!=null)
					callback.onFailure(caught);
			}

			@Override
			public void onSuccess(List<InfoContactModel> result) {
				GWT.log("loaded "+result.size() + " contacts from server for "+sharedFolderId);
				if(callback!=null)
					callback.onSuccess(result);
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.view.sharing.ContactFetcher#getExclusiveContactsFromAllContact(java.util.List)
	 */
	@Override
	public List<InfoContactModel> getExclusiveContactsFromAllContact(List<InfoContactModel> listSharedUser){

		List<InfoContactModel> listExclusiveContact = new ArrayList<InfoContactModel>(listAllContact);
		for (InfoContactModel contact : listSharedUser) {
			if(listAllContact.contains(contact)){
				GWT.log("Removing not eclusive contact "+contact);
				listExclusiveContact.remove(contact);
			}
		}
		return listExclusiveContact;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.view.sharing.ContactFetcher#getOwner(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getOwner(final String sharedFolderId,final AsyncCallback<InfoContactModel> callback) {

		WorkspaceSharingServiceAsync.INSTANCE.getOwnerByItemId(sharedFolderId, new AsyncCallback<InfoContactModel>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("an error occured in get Owner by Id "+sharedFolderId + " "+caught.getMessage());

			}

			@Override
			public void onSuccess(InfoContactModel result) {
				if(callback!=null)
					callback.onSuccess(result);
			}
		});
	}
}
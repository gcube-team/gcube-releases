package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing;
import org.gcube.portlets.widgets.workspacesharingwidget.client.WorkspaceSharingController;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa
 *
 */
public class UserStore implements ContactFetcher {

	private static List<InfoContactModel> listAllContact = new ArrayList<InfoContactModel>();;

	// public boolean syncronizeCleanSharedUser = false;

	public UserStore() {

	}

	private void loadSharedUserBySharedFolderId(final String sharedFolderId,
			final AsyncCallback<List<InfoContactModel>> callback) {

		// comboSharedUsers.mask("Loading users");

		WorkspaceSharingController.rpcWorkspaceSharingService.getListUserSharedByFolderSharedId(sharedFolderId,
				new AsyncCallback<List<InfoContactModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageBox.alert("Error",
								ConstantsSharing.SERVER_ERROR + " retrieving user. " + ConstantsSharing.TRY_AGAIN,
								null);
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(List<InfoContactModel> result) {
						GWT.log("Get List user shared loaded " + result.size() + " contacts from server for "
								+ sharedFolderId);
						callback.onSuccess(result);
					}
				});
	}

	@Override
	public void getListSharedUserByFolderId(String sharedFolderId, AsyncCallback<List<InfoContactModel>> callback) {
		loadSharedUserBySharedFolderId(sharedFolderId, callback);
	}

	@Override
	public void getExclusiveContactsFromAllContact(final List<InfoContactModel> listSharedUser,
			final AsyncCallback<List<InfoContactModel>> callback) {
		if (listAllContact == null || listAllContact.isEmpty()) {
			WorkspaceSharingController.rpcWorkspaceSharingService
					.getAllContacts(new AsyncCallback<List<InfoContactModel>>() {

						@Override
						public void onSuccess(List<InfoContactModel> result) {
							GWT.log("Get all contacts loaded " + result.size() + " contacts from server");
							listAllContact = result;
							List<InfoContactModel> listExclusiveContact = new ArrayList<InfoContactModel>(
									listAllContact);
							for (InfoContactModel contact : listSharedUser) {
								if (listExclusiveContact.contains(contact)) {
									listExclusiveContact.remove(contact);
								}
							}
							callback.onSuccess(listExclusiveContact);

						}

						@Override
						public void onFailure(Throwable caught) {
							MessageBox.alert("Error",
									ConstantsSharing.SERVER_ERROR + " retrieving user " + ConstantsSharing.TRY_AGAIN,
									null);
							callback.onFailure(new Throwable("Error retrieving users!"));
						}
					});
		} else {
			List<InfoContactModel> listExclusiveContact = new ArrayList<InfoContactModel>(listAllContact);
			for (InfoContactModel contact : listSharedUser) {
				if (listAllContact.contains(contact)) {
					listExclusiveContact.remove(contact);
				}
			}
			callback.onSuccess(listExclusiveContact);
		}

	}

	@Override
	public void getOwner(final String sharedFolderId, final AsyncCallback<InfoContactModel> callback) {

		WorkspaceSharingController.rpcWorkspaceSharingService.getOwnerByItemId(sharedFolderId,
				new AsyncCallback<InfoContactModel>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("an error occured in get Owner by Id " + sharedFolderId + " " + caught.getMessage());
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(InfoContactModel result) {
						callback.onSuccess(result);

					}
				});
	}

	@Override
	public void getInfoContactModelsFromCredential(List<CredentialModel> listAlreadySharedContact,
			final AsyncCallback<List<InfoContactModel>> callback) {

		WorkspaceSharingController.rpcWorkspaceSharingService.getInfoContactModelsFromCredential(
				listAlreadySharedContact, new AsyncCallback<List<InfoContactModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("an error occured ingetInfoContactModelsFromCredential " + caught.getMessage());

					}

					@Override
					public void onSuccess(List<InfoContactModel> result) {
						callback.onSuccess(result);

					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.
	 * ContactFetcher#getListContact(com.google.gwt.user.client.rpc.
	 * AsyncCallback, boolean, boolean, boolean)
	 */
	@Override
	public void getListContact(final AsyncCallback<List<InfoContactModel>> callback, boolean reloadList) {

		if (listAllContact == null || listAllContact.isEmpty() || reloadList) {
			WorkspaceSharingController.rpcWorkspaceSharingService
					.getAllContacts(new AsyncCallback<List<InfoContactModel>>() {

						@Override
						public void onSuccess(List<InfoContactModel> result) {
							GWT.log("Get all contacts loaded " + result.size() + " contacts from server");
							listAllContact = result;
							callback.onSuccess(listAllContact);
						}

						@Override
						public void onFailure(Throwable caught) {
							MessageBox.alert("Error",
									ConstantsSharing.SERVER_ERROR + " retrieving user " + ConstantsSharing.TRY_AGAIN,
									null);
							callback.onFailure(new Throwable("Error retrieving users!"));
						}
					});
		} else {
			callback.onSuccess(listAllContact);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.
	 * ContactFetcher#getAdministratorsByFolderId(java.lang.String,
	 * com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	public void getAdministratorsByFolderId(String sharedFolderId,
			final AsyncCallback<List<InfoContactModel>> callback) {

		if (sharedFolderId == null || sharedFolderId.isEmpty())
			return;
		WorkspaceSharingController.rpcWorkspaceSharingService.getAdministratorsByFolderId(sharedFolderId,
				new AsyncCallback<List<InfoContactModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);

					}

					@Override
					public void onSuccess(List<InfoContactModel> result) {
						callback.onSuccess(result);

					}

				});

	}
}
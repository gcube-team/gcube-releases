/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingServiceAsync;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.UserStore;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.user.DialogMultiDragContact;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa Jul 30, 2014
 *
 *         A simple multi drag dialog to manage users administrators
 *         of a workspace item
 */
public class MultiDragWorkspaceContactUser {
 
	private UserStore userStore = new UserStore();
	private DialogMultiDragContact dialogMultiDragContactSimple;
	private List<InfoContactModel> targets = new ArrayList<InfoContactModel>();
	private boolean hiddenMySelf;
	private String workspaceItemId;
	private String myLogin;
	private InfoContactModel myContact;

	/**
	 * Load administrators or shared users to workspace item id
	 * 
	 * 
	 * @param workspaceItemId
	 *            Item id
	 * @param hiddenMySelf
	 *            if true the login read from ASL is hidden (so it's not
	 *            removable to target users), the login returned anyway among
	 *            the target users
	 */
	public MultiDragWorkspaceContactUser(String workspaceItemId,
			final boolean hiddenMySelf) {
		
		this.workspaceItemId = workspaceItemId;
		dialogMultiDragContactSimple=new DialogMultiDragContact(
				MultiDragConstants.HEADING_DIALOG, MultiDragConstants.ALL_CONTACTS_LEFT_LIST,
				MultiDragConstants.SHARE_WITH_RIGHT_LIST, false, false, hiddenMySelf);
		
		if (hiddenMySelf)
			loadMyLogin(true);
		else
			loadSharedContacts();
	}

	
	private void loadMyLogin(final boolean loadContacts) {

		WorkspaceSharingServiceAsync.INSTANCE.getMyLogin(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error on loading my login is empty");
				myLogin = "";
				loadSharedContacts();
			}

			@Override
			public void onSuccess(String result) {
				GWT.log("My login is: " + result);
				myLogin = result;

				if (loadContacts)
					loadSharedContacts();
			}
		});
	}

	/**
	 * Load the target contacts
	 */
	private void loadSharedContacts() {

	

			// LOADING LIST OF ALREADY SHARED USER
			userStore.getListSharedUserByFolderId(workspaceItemId, new AsyncCallback<List<InfoContactModel>>() {

				@Override
				public void onSuccess(List<InfoContactModel> result) {
					GWT.log("Returned " + result.size() + " contact/s");
					fillMultiDrag(result);
					loadAllContacts();
				}

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Error on loading shared contacts");
					MessageBox.alert("Error on shared contacts", caught.getMessage(), null);

				}
			});

	}

	
	
	private void fillMultiDrag(List<InfoContactModel> result) {

		GWT.log("Filling multi-drag..");
		GWT.log("Hidden my self: " + hiddenMySelf);

		for (InfoContactModel infoContactModel : result) {
			if (infoContactModel.getLogin() != null) {
				if (hiddenMySelf && (infoContactModel.getLogin().compareTo(myLogin) == 0)) {
					myContact = infoContactModel;
					GWT.log("Skipping myLogin as: " + myContact);
				} else {
					dialogMultiDragContactSimple.getMultiDrag().addTargetContact(infoContactModel);
					targets.add(infoContactModel);
				}
			}
		}
	}

	/**
	 * Load all contacts
	 */
	private void loadAllContacts() {
		userStore.getListContact(addSourceContacts, false);
	}

	private AsyncCallback<List<InfoContactModel>> addSourceContacts = new AsyncCallback<List<InfoContactModel>>() {

		@Override
		public void onFailure(Throwable caught) {
			GWT.log("Error on loading contacts");
			MessageBox.alert("Error", caught.getMessage(), null);
		}

		@Override
		public void onSuccess(List<InfoContactModel> result) {

			List<InfoContactModel> contactTargets = new ArrayList<InfoContactModel>(targets.size() + 1);
			contactTargets.addAll(targets);
			if (result != null && result.size() > 0) {
				if (hiddenMySelf)
					contactTargets.add(myContact);
				
				
				AsyncCallback<List<InfoContactModel>> callback=new AsyncCallback<List<InfoContactModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error retrieving contacts: "+caught,caught);
						
					}

					@Override
					public void onSuccess(List<InfoContactModel> result) {
						GWT.log("Contact List retrieved");
						dialogMultiDragContactSimple.getMultiDrag().addSourceContacts(result);
						
					}
				};
			
				userStore.getExclusiveContactsFromAllContact(contactTargets,callback);
			}
		}
	};

	/**
	 * 
	 * @return the multi drag DialogMultiDragContact
	 */
	public DialogMultiDragContact getDialogMultiDragContact() {
		return dialogMultiDragContactSimple;
	}

	public void show() {
		dialogMultiDragContactSimple.show();
	}

	
	public List<InfoContactModel> getTargetContacts() {
		return dialogMultiDragContactSimple.getMultiDrag().getTargetListContact();
	}

	public List<InfoContactModel> getTargetContactsWithMyLogin() {
		List<InfoContactModel> contacts = getTargetContacts();

		if (myContact == null) {
			GWT.log("TargetContactsWithMyLogin my Contact is null, skipping!");
		} else
			contacts.add(myContact);

		return contacts;
	}
}

/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.permissions.MessageBoxAlert;
import org.gcube.portlets.widgets.workspacesharingwidget.client.permissions.PanelTogglePermission;
import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingServiceAsync;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.UserStore;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.user.DialogMultiDragContact;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.user.MultiDragContact;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * A dialog to drag and edit permissions of a workspace folder
 * 
 * @author Francesco Mangiacrapa  Jan 27, 2015
 *
 */
public class MultiDragContactsEditPermissions {

	private PanelTogglePermission permission;
	private HorizontalPanel hpPermission = new HorizontalPanel();
	private Html htmlCurrentPermissions = new Html();

	private UserStore userStore = new UserStore();
	private ConstantsSharing.LOAD_CONTACTS_AS loadContactAs;
	private DialogMultiDragContact dialogMultiDragContact; 
	private List<InfoContactModel> sources = new ArrayList<InfoContactModel>();
	private boolean hiddenMySelf;
	private String workspaceItemId;
	private String myLogin;
	private InfoContactModel myContact;

	
	public MultiDragContactsEditPermissions(ConstantsSharing.LOAD_CONTACTS_AS load, final String workspaceItemId,
			boolean hiddenMySelf) {
		dialogMultiDragContact= new DialogMultiDragContact(
				MultiDragConstants.HEADING_DIALOG, MultiDragConstants.ALL_CONTACTS_LEFT_LIST,
				MultiDragConstants.SHARE_WITH_RIGHT_LIST, false, false,hiddenMySelf);
		initMultiDrag(load, workspaceItemId, hiddenMySelf);

		// dialog = super.getDialogMultiDragContact();
		dialogMultiDragContact.setScrollMode(Scroll.AUTOY);

		// htmlCurrentPermissions.setReadOnly(true);
		LayoutContainer lcCurrentPermissions = new LayoutContainer();
		htmlCurrentPermissions.setWidth(MultiDragContact.WIDTH_CP);
		htmlCurrentPermissions.setStyleAttribute("padding", MultiDragContact.PADDING + "px");
		lcCurrentPermissions.setScrollMode(Scroll.AUTOY);
		lcCurrentPermissions.setHeight(50);
		lcCurrentPermissions.add(htmlCurrentPermissions);
		lcCurrentPermissions.add(lcCurrentPermissions);

		dialogMultiDragContact.getLcTop().add(lcCurrentPermissions);
		// dialog.setHeight(dialog.getHeight()+100);

		WorkspaceSharingController.rpcWorkspaceSharingService.getACLsDescriptionForSharedFolderId(workspaceItemId,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("error getACLsDescriptionForSharedFolderId: " + caught.getMessage());

					}

					@Override
					public void onSuccess(String result) {
						String msg = "#<b>Current Permissions:</b> <br/>";
						msg += result;
						htmlCurrentPermissions.setHtml(msg);

					}
				});

		enableSubmit(false);
		// hpPermission.setStyleAttribute("margin-top", "20px");
		hpPermission.setStyleAttribute("margin-left", "165px");
		// Html html = new Html("With Permission : ");
		// html.setStyleAttribute("margin-left", "10px");
		// hpPermission.add(html);

		// TODO GET ACL FOR USER
		WorkspaceSharingController.rpcWorkspaceSharingService.getACLs(new AsyncCallback<List<WorkspaceACL>>() {

			@Override
			public void onSuccess(List<WorkspaceACL> result) {
				permission = new PanelTogglePermission(result);
				hpPermission.add(permission);
				hpPermission.layout();
				// selectAclForFolder(workspaceItemId);
				enableSubmit(true);

				// dialog.unmask();
			}

			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Alert", "Sorry, an error occurred on recovering ACLs", null);
				// dialog.unmask();
			}
		});

		dialogMultiDragContact.getLcBottom().add(hpPermission);

		dialogMultiDragContact.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				dialogMultiDragContact.setHeight(dialogMultiDragContact.getHeight() + 100);
			}
		});
	}

	public void enableSubmit(boolean bool) {
		dialogMultiDragContact.getButtonById(Dialog.OK).setEnabled(bool);
	}

	public DialogMultiDragContact getDialog() {
		return dialogMultiDragContact;
	}

	/**
	 * The HL ID of the selected ACL
	 * 
	 * @return ACL ID
	 */
	public String getSelectedAclID() {
		if (permission.getSelectedACL() != null)
			return permission.getSelectedACL().getId();

		return null;

	}

	/**
	 * The selected ACL
	 * 
	 * @return Workspace ACL
	 */
	public WorkspaceACL getSelectedAcl() {
		if (permission.getSelectedACL() != null)
			return permission.getSelectedACL();

		return null;

	}

	/**
	 * Load administrators or shared users to workspace item id
	 * 
	 * @param load
	 *            if LOAD_CONTACTS_AS.SHARED_USER loads source users from Shared
	 *            Users if LOAD_CONTACTS_AS.ADMINISTRATOR loads source users
	 *            from Administrators
	 * 
	 * @param workspaceItemId
	 * @param hiddenMySelf
	 *            if true the login read from ASL is hidden (so it's not
	 *            removable to target users), the login returned anyway among
	 *            the target users
	 */
	private void initMultiDrag(ConstantsSharing.LOAD_CONTACTS_AS load, String workspaceItemId,
			final boolean hiddenMySelf) {
		this.loadContactAs = load;
		this.hiddenMySelf = hiddenMySelf;
		this.workspaceItemId = workspaceItemId;

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

		switch (loadContactAs) {

		case ADMINISTRATOR:

			// LOADING LIST OF ALREADY SHARED USER
			userStore.getAdministratorsByFolderId(workspaceItemId, new AsyncCallback<List<InfoContactModel>>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Error on loading admnistrators");
					MessageBox.alert("Error on loading admnistrators", caught.getMessage(), null);
				}

				@Override
				public void onSuccess(List<InfoContactModel> result) {
					GWT.log("Returned " + result.size() + " admin/s");
					fillMultiDrag(result);

				}
			});

			break;

		case SHARED_USER:

			// LOADING LIST OF ALREADY SHARED USER
			userStore.getListSharedUserByFolderId(workspaceItemId, new AsyncCallback<List<InfoContactModel>>() {

				@Override
				public void onSuccess(List<InfoContactModel> result) {
					GWT.log("Returned " + result.size() + " contact/s");
					fillMultiDrag(result);

				}

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Error on loading shared contacts");
					MessageBox.alert("Error on shared contacts", caught.getMessage(), null);

				}
			});

			break;
		default:

			break;
		}
	}

	public void addTargetContact(List<InfoContactModel> listContacts) {
		if (listContacts != null) {
			for (InfoContactModel infoContactModel : listContacts) {
				dialogMultiDragContact.getMultiDrag().addTargetContact(infoContactModel);
			}
		}

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
					dialogMultiDragContact.getMultiDrag().addSourceContact(infoContactModel);
					sources.add(infoContactModel);
				}
			}
		}
	}

	public void show() {
		dialogMultiDragContact.show();
	}

	
	public List<InfoContactModel> getTargetContacts() {
		return dialogMultiDragContact.getMultiDrag().getTargetListContact();
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

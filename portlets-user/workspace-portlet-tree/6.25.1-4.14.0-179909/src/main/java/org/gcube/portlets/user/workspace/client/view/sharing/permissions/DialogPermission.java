package org.gcube.portlets.user.workspace.client.view.sharing.permissions;

import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.event.UpdatedVREPermissionEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.view.sharing.PanelTogglePermission;
import org.gcube.portlets.user.workspace.client.view.windows.InfoDisplay;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingServiceAsync;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * 
 */
public class DialogPermission extends Dialog {

	private int widthDialog = 400;
	private FileModel parentFolder = null;

	private PanelTogglePermission permission;
	private HorizontalPanel hpPermission = new HorizontalPanel();
	private FileModel folder;

	private DialogPermission INSTANCE = this;

	public FileModel getParentFolder() {
		return parentFolder;
	}

	public void initLayout(String folderParentName) {
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(90);
		layout.setDefaultWidth(380);
		setLayout(layout);
		setModal(true);
		setScrollMode(Scroll.AUTOY);
		setBodyStyle("padding: 9px; background: none");
		setWidth(widthDialog);
		setHeight(140);
		setResizable(true);
		setButtonAlign(HorizontalAlignment.CENTER);
		setWidth(widthDialog);
		setButtons(Dialog.OKCANCEL);
		enableFormDialog(false);
	}

	/**
	 * Use to set permission to VRE Folder
	 * 
	 * @param folder
	 *            folder
	 */
	public DialogPermission(FileModel folder) {

		this.folder = folder;

		Label labelProperty = new Label("Permissions");

		hpPermission.add(labelProperty);
		hpPermission.setVerticalAlign(VerticalAlignment.MIDDLE);
		hpPermission.mask("Checking authorization");

		AsyncCallback<List<InfoContactModel>> callback = new AsyncCallback<List<InfoContactModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Alert", "Sorry, an error occurred on recovering ACLs", null);
				hpPermission.unmask();
			}

			@Override
			public void onSuccess(List<InfoContactModel> listManagers) {
				permissionControl(listManagers, true);
				hpPermission.unmask();
			}
		};

		getUsersManagers(folder.getIdentifier(), callback);

		// TODO GET ACL FOR USER
		WorkspaceSharingServiceAsync.INSTANCE.getACLs(new AsyncCallback<List<WorkspaceACL>>() {

			@Override
			public void onSuccess(List<WorkspaceACL> result) {
				permission = new PanelTogglePermission(result);
				hpPermission.add(permission);
				hpPermission.layout();
				layout();
			}

			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Alert", "Sorry, an error occurred on recovering ACLs", null);
			}
		});

		initLayout(folder.getName());
		this.setIcon(Resources.getIconVREFolder());
		setHeading("Change permissions to folder: " + folder.getName());

		setWidth(widthDialog);
		setButtons(Dialog.OKCANCEL);

		add(hpPermission);
		addListners();
		getButtonById(Dialog.OK).setEnabled(false);
		enableFormDialog(false);
	}

	public void addListners() {

		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});

		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				INSTANCE.mask("Changing permissions");
				if (folder != null && getSelectedACL().getId() != null) {

					WorkspaceSharingServiceAsync.INSTANCE.updateACLForVREbyGroupName(folder.getIdentifier(),
							getSelectedACL().getId(), new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									INSTANCE.hide();
									new MessageBoxAlert("Error", caught.getMessage(), null);
								}

								@Override
								public void onSuccess(Void result) {
									INSTANCE.unmask();
									INSTANCE.hide();
									new InfoDisplay("Permission updated",
											"The permissions have been changed correctly");

									AppControllerExplorer.getEventBus()
											.fireEvent(new UpdatedVREPermissionEvent(folder.getIdentifier()));
								}
							});
				}
			}
		});

	}

	private void permissionControl(List<InfoContactModel> listManagers, boolean showAlert) {

		boolean permissionsOk = false;
		for (InfoContactModel infoContactModel : listManagers) {

			GWT.log("DialogPermission control compare between : " + infoContactModel.getLogin() + " and my login: "
					+ AppControllerExplorer.myLogin);
			if (AppControllerExplorer.myLogin.compareToIgnoreCase(infoContactModel.getLogin()) == 0) {
				permissionsOk = true;
				break;
			}

		}

		if (permissionsOk) {
			enableFormDialog(true);
		} else {
			enableFormDialog(false);
			if (showAlert)
				new MessageBoxAlert("Permission denied",
						"You have no permissions to change sharing. You are not manager of \"" + folder.getName()
								+ "\"",
						null);
		}

	}

	private void enableFormDialog(boolean bool) {
		getButtonById(Dialog.OK).setEnabled(bool);

		if (permission != null)
			permission.setEnabled(bool);
	}

	public void getUsersManagers(final String sharedFolderId, final AsyncCallback<List<InfoContactModel>> callback) {

		WorkspaceSharingServiceAsync.INSTANCE.getAdministratorsByFolderId(sharedFolderId,
				new AsyncCallback<List<InfoContactModel>>() {

					@Override
					public void onFailure(Throwable arg0) {
						GWT.log("an error occured in getting user managers by Id " + sharedFolderId + " "
								+ arg0.getMessage());
						new MessageBoxAlert("Alert",
								"Sorry, an error occurred on getting users managers, try again later", null);

					}

					@Override
					public void onSuccess(List<InfoContactModel> listManagers) {
						callback.onSuccess(listManagers);

					}
				});
	}

	public WorkspaceACL getSelectedACL() {
		if (permission != null)
			return permission.getSelectedACL();

		return null;
	}
}

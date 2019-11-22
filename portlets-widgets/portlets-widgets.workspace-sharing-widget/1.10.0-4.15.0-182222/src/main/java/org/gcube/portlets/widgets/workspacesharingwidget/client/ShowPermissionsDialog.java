package org.gcube.portlets.widgets.workspacesharingwidget.client;

import org.gcube.portlets.widgets.workspacesharingwidget.client.resources.Resources;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.user.MultiDragContact;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ShowPermissionsDialog extends Dialog {

	private static final int HEIGHT_DIALOG = 300;
	private static final int WIDTH_DIALOG = 600;
	private String headTitle;
	private String itemId;
	private Html htmlCurrentPermissions;

	public ShowPermissionsDialog(String headTitle, String itemId) {
		this.headTitle = headTitle;
		this.itemId = itemId;
		init();
	}

	private void init() {
		setId(ShowPermissionsDialog.class.getName() + Random.nextInt());
		setSize(WIDTH_DIALOG, HEIGHT_DIALOG);
		setResizable(false);
		setMaximizable(false);
		setIcon(Resources.getIconUsers());
		setModal(true);
		setHeading(headTitle);

		setButtonAlign(HorizontalAlignment.CENTER);
		setButtons(Dialog.OKCANCEL);

		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});

		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});

		createCurrentPermissionPanel();

	}

	void createCurrentPermissionPanel() {
		htmlCurrentPermissions = new Html();

		LayoutContainer lcCurrentPermissions = new LayoutContainer();
		htmlCurrentPermissions.setWidth(MultiDragContact.WIDTH_CP);
		htmlCurrentPermissions.setStyleAttribute("padding", MultiDragContact.PADDING + "px");
		lcCurrentPermissions.setScrollMode(Scroll.AUTOY);
		lcCurrentPermissions.setHeight(50);
		lcCurrentPermissions.add(htmlCurrentPermissions);

		WorkspaceSharingController.rpcWorkspaceSharingService.getACLsDescriptionForSharedFolderId(itemId,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("error getACLsDescriptionForSharedFolderId: " + caught.getMessage(), caught);
					}

					@Override
					public void onSuccess(String result) {
						String msg = "#<b>Current Permissions:</b> <br/>";
						msg += result;
						htmlCurrentPermissions.setHtml(msg);
					}
				});
		add(lcCurrentPermissions);

	}

	public String getHeadTitle() {
		return headTitle;
	}

}
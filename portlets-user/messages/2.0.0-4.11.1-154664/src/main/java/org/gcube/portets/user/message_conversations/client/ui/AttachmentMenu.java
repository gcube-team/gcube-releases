package org.gcube.portets.user.message_conversations.client.ui;

import org.gcube.portets.user.message_conversations.client.MessageServiceAsync;
import org.gcube.portets.user.message_conversations.client.Utils;
import org.gcube.portets.user.message_conversations.shared.FileModel;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialDropDown;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialToast;

public class AttachmentMenu extends Composite  {

	private static AttachmentMenuUiBinder uiBinder = GWT.create(AttachmentMenuUiBinder.class);

	interface AttachmentMenuUiBinder extends UiBinder<Widget, AttachmentMenu> {
	}

	private enum DownloadStatus { FAILED, IN_PROGRESS, COMPLETE };

	@UiField MaterialDropDown menu;
	@UiField MaterialLink downloadButton;
	@UiField MaterialLink saveWSButton;

	private FileModel item;
	private MaterialButton parentButton;
	private MessageServiceAsync convService;
	private DownloadStatus generatingDownloadURL = DownloadStatus.IN_PROGRESS;

	public AttachmentMenu(MessageServiceAsync convService, MaterialButton parent, FileModel item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.item = item;
		this.parentButton = parent;
		this.convService = convService;
		convService.getAttachmentDownloadURL(item.getIdentifier(), new AsyncCallback<String>() {			
			@Override
			public void onSuccess(String result) {
				if (result != null) {
					downloadButton.setHref(result);	
					generatingDownloadURL = DownloadStatus.COMPLETE;
				}
				else {
					generatingDownloadURL = DownloadStatus.FAILED;
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("onFailure");
				generatingDownloadURL = DownloadStatus.FAILED;
			}
		});
	}

	public void setSeparator(boolean separator) {
		menu.setSeparator(separator);
	}

	public void setActivator(String activator) {
		menu.setActivator(activator);
	}

	@UiHandler("downloadButton")
	void onFileDownload(ClickEvent e) {
		switch (generatingDownloadURL) {
		case IN_PROGRESS:
			e.stopPropagation();
			MaterialToast.fireToast("Generating link is in progress, please retry in few seconds", "rounded");
			break;
		case COMPLETE:
			MaterialToast.fireToast("Download in progress ... ", "rounded");
			break;
		case FAILED:
			e.stopPropagation();
			downloadButton.setHref("#");
			MaterialToast.fireToast("Warning: could not generate a download link, some error occurred in the server", "rounded");

			break;
		}
	}

	@UiHandler("saveWSButton")
	void onSave2WS(ClickEvent e) {
		final WorkspaceExplorerSaveDialog wpTreepopup = new WorkspaceExplorerSaveDialog(item.getName(), true);
		wpTreepopup.setId(Utils.ID_MODALBOOTSTRAP);
		wpTreepopup.getElement().getStyle().setLeft(50, Unit.PCT);
		WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){

			@Override
			public void onSaving(Item parent, String fileName) {
				GWT.log("onSaving parent: "+parent +", fileName" +fileName);
				wpTreepopup.hide();
				MaterialLoader.showProgress(true, parentButton);
				convService.saveAttachmentToWorkspaceFolder(item.getIdentifier(), parent.getId(), new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						MaterialLoader.showProgress(false, parentButton);
						MaterialToast.fireToast("Warning: could not save this file to the selected Workspace folder, please retry", "rounded");	
					}
					@Override
					public void onSuccess(Boolean result) {
						MaterialLoader.showProgress(false, parentButton);
						MaterialToast.fireToast("File " +fileName+" Saved succesfully");							
					}
				});
			}

			@Override
			public void onAborted() {
				GWT.log("onAborted");
			}

			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("onFailed");
			}
		};

		wpTreepopup.addWorkspaceExplorerSaveNotificationListener(listener);
		wpTreepopup.show();
	}
}

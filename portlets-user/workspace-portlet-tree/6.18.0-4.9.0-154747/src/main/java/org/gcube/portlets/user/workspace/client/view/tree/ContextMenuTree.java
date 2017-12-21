package org.gcube.portlets.user.workspace.client.view.tree;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WS_UPLOAD_TYPE;
import org.gcube.portlets.user.workspace.client.constant.WorkspaceOperation;
import org.gcube.portlets.user.workspace.client.event.AccountingHistoryEvent;
import org.gcube.portlets.user.workspace.client.event.AccountingReadersEvent;
import org.gcube.portlets.user.workspace.client.event.AddAdministratorEvent;
import org.gcube.portlets.user.workspace.client.event.AddFolderEvent;
import org.gcube.portlets.user.workspace.client.event.CopytemEvent;
import org.gcube.portlets.user.workspace.client.event.CreateSharedFolderEvent;
import org.gcube.portlets.user.workspace.client.event.CreateUrlEvent;
import org.gcube.portlets.user.workspace.client.event.DeleteItemEvent;
import org.gcube.portlets.user.workspace.client.event.EditUserPermissionEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent.DownloadType;
import org.gcube.portlets.user.workspace.client.event.FileUploadEvent;
import org.gcube.portlets.user.workspace.client.event.GetFolderLinkEvent;
import org.gcube.portlets.user.workspace.client.event.GetInfoEvent;
import org.gcube.portlets.user.workspace.client.event.GetPublicLinkEvent;
import org.gcube.portlets.user.workspace.client.event.GetShareLinkEvent;
import org.gcube.portlets.user.workspace.client.event.ImagePreviewEvent;
import org.gcube.portlets.user.workspace.client.event.OpenUrlEvent;
import org.gcube.portlets.user.workspace.client.event.PasteItemEvent;
import org.gcube.portlets.user.workspace.client.event.PublishOnDataCatalogueEvent;
import org.gcube.portlets.user.workspace.client.event.PublishOnThreddsCatalogueEvent;
import org.gcube.portlets.user.workspace.client.event.RefreshFolderEvent;
import org.gcube.portlets.user.workspace.client.event.RenameItemEvent;
import org.gcube.portlets.user.workspace.client.event.SendMessageEvent;
import org.gcube.portlets.user.workspace.client.event.UnShareFolderEvent;
import org.gcube.portlets.user.workspace.client.event.VRESettingPermissionEvent;
import org.gcube.portlets.user.workspace.client.event.VersioningHistoryShowEvent;
import org.gcube.portlets.user.workspace.client.event.WebDavUrlEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.util.FileModelUtils;
import org.gcube.portlets.user.workspace.client.view.tree.CutCopyAndPaste.OperationType;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class ContextMenuTree.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 17, 2015
 */
public class ContextMenuTree {

	private Menu contextMenu = new Menu();
	private HandlerManager eventBus = AppControllerExplorer.getEventBus();
	private List<FileModel> listSelectedItems = null;

	private MenuItem mnRead = new MenuItem();

	private boolean hideSharing = false;

	/**
	 * Instantiates a new context menu tree.
	 */
	public ContextMenuTree() {
		this.contextMenu.setWidth(140);
		this.listSelectedItems = new ArrayList<FileModel>();
		createContextMenu();

	}


	/**
	 * Creates the context menu.
	 */
	private void createContextMenu() {
		//SPECIFIC OPERATION

		//Preview Image
		MenuItem previewImage = new MenuItem();
		previewImage.setId(WorkspaceOperation.PREVIEW.getId());
		previewImage.setText(ConstantsExplorer.MESSAGE_PREVIEW);
		previewImage.setIcon(Resources.getIconPreview());

		previewImage.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				FileModel selected = listSelectedItems.get(0);
				if(selected!=null){
					eventBus.fireEvent(new ImagePreviewEvent(selected, ce.getClientX(), ce.getClientY()));
				}
			}
		});

		contextMenu.add(previewImage);

		//Open Url
		MenuItem openUrl = new MenuItem();
		openUrl.setId(WorkspaceOperation.LINK.getId());
		openUrl.setText(ConstantsExplorer.MESSAGE_OPEN_URL);
		openUrl.setIcon(Resources.getIconOpenUrl());

		openUrl.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				FileModel selected = listSelectedItems.get(0);

				if(selected!=null){
					eventBus.fireEvent(new OpenUrlEvent(selected));
				}


			}
		});

		contextMenu.add(openUrl);

		//Open Report Template
		//TODO REMOVE
		//		MenuItem openReportTemplate = new MenuItem();
		//		openReportTemplate.setId(WorkspaceOperation.OPEN_REPORT_TEMPLATE.getId());
		//		openReportTemplate.setText(ConstantsExplorer.MESSAGE_OPEN_REPORT_TEMPLATE);
		//		openReportTemplate.setIcon(Resources.getIconShow());
		//
		//		openReportTemplate.addSelectionListener(new SelectionListener<MenuEvent>() {
		//			public void componentSelected(MenuEvent ce) {
		//
		//				FileModel selected = listSelectedItems.get(0);
		//
		//				if(selected!=null){
		//					eventBus.fireEvent(new OpenReportsEvent(selected));
		//				}
		//			}
		//		});

		//contextMenu.add(openReportTemplate);

		//Open Report Template
		//TODO REMOVE
		//		MenuItem openReport = new MenuItem();
		//		openReport.setId(WorkspaceOperation.OPEN_REPORT.getId());
		//		openReport.setText(ConstantsExplorer.MESSAGE_OPEN_REPORT);
		//		openReport.setIcon(Resources.getIconShow());
		//
		//		openReport.addSelectionListener(new SelectionListener<MenuEvent>() {
		//			public void componentSelected(MenuEvent ce) {
		//
		//				FileModel selected = listSelectedItems.get(0);
		//
		//				if (selected != null){
		//					eventBus.fireEvent(new OpenReportsEvent(selected));
		//				}
		//
		//			}
		//		});

		//contextMenu.add(openReport);
		contextMenu.add(new SeparatorMenuItem());
		//END SPECIFIC OPERATION

		MenuItem mnGetInfo = new MenuItem();
		mnGetInfo.setId(WorkspaceOperation.GET_INFO.getId());
		mnGetInfo.setText(ConstantsExplorer.MESSAGE_GET_INFO);
		mnGetInfo.setIcon(Resources.getIconInfo());

		mnGetInfo.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					eventBus.fireEvent(new GetInfoEvent(sel));
				}
			}
		});

		contextMenu.add(mnGetInfo);


		MenuItem mnFileVersioning = new MenuItem();
		mnFileVersioning.setId(WorkspaceOperation.VERSIONING.getId());
		mnFileVersioning.setText(ConstantsExplorer.FILE_VERSIONS);
		mnFileVersioning.setIcon(Resources.getIconVersioning());

		mnFileVersioning.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					eventBus.fireEvent(new VersioningHistoryShowEvent(WorkspaceVersioningOperation.SHOW, sel));
				}
			}
		});

		contextMenu.add(mnFileVersioning);


		//SHARE LINK
		MenuItem mnGetLink = new MenuItem();
		mnGetLink.setId(WorkspaceOperation.SHARE_LINK.getId());
		mnGetLink.setText(ConstantsExplorer.MESSAGE_SHARE_LINK);
		mnGetLink.setIcon(Resources.getIconShareLink());

		mnGetLink.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					eventBus.fireEvent(new GetShareLinkEvent(sel));
				}
			}
		});

		contextMenu.add(mnGetLink);

		//PUBLIC LINK
		MenuItem mnPublicLink = new MenuItem();
		mnPublicLink.setId(WorkspaceOperation.PUBLIC_LINK.getId());
		mnPublicLink.setText(ConstantsExplorer.MESSAGE_PUBLIC_LINK);
		mnPublicLink.setIcon(Resources.getIconPublicLink());

		mnPublicLink.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					eventBus.fireEvent(new GetPublicLinkEvent(sel));
				}
			}
		});

		contextMenu.add(mnPublicLink);


		//FOLDER LINK
		MenuItem mnFolderLink = new MenuItem();
		mnFolderLink.setId(WorkspaceOperation.FOLDER_LINK.getId());
		mnFolderLink.setText(ConstantsExplorer.MESSAGE_FOLDER_LINK);
		mnFolderLink.setIcon(Resources.getIconFolderPublic());

		mnFolderLink.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					if(sel.isDirectory())
						eventBus.fireEvent(new GetFolderLinkEvent(sel, true));
				}
			}
		});

		contextMenu.add(mnFolderLink);


		//FOLDER LINK REMOVE
		MenuItem mnFolderLinkRemove = new MenuItem();
		mnFolderLinkRemove.setId(WorkspaceOperation.FOLDER_LINK_REMOVE.getId());
		mnFolderLinkRemove.setText(ConstantsExplorer.MESSAGE_FOLDER_LINK_REMOVE);
		mnFolderLinkRemove.setIcon(Resources.getIconFolderPublicRemove());

		mnFolderLinkRemove.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					if(sel.isDirectory())
						eventBus.fireEvent(new GetFolderLinkEvent(sel, false));
				}
			}
		});

		contextMenu.add(mnFolderLinkRemove);

		contextMenu.add(new SeparatorMenuItem());

		MenuItem mnHistory = new MenuItem();
		mnHistory.setIcon(Resources.getIconHistory());
		mnHistory.setId(WorkspaceOperation.HISTORY.getId());
		mnHistory.setText(ConstantsExplorer.HISTORY);

		mnHistory.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {

				final FileModel sourceFileModel = listSelectedItems.get(0);

				if (sourceFileModel != null) {

					eventBus.fireEvent(new AccountingHistoryEvent(sourceFileModel));
				}
			}
		});
		contextMenu.add(mnHistory);

		//ACCOUNTING READ
		mnRead = new MenuItem();
		mnRead.setIcon(Resources.getIconRead());
		mnRead.setId(WorkspaceOperation.ACCREAD.getId());
		mnRead.setText(ConstantsExplorer.ACCREAD);

		mnRead.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {

				final FileModel sourceFileModel = listSelectedItems.get(0);

				if (sourceFileModel != null) {

					eventBus.fireEvent(new AccountingReadersEvent(sourceFileModel));
				}
			}
		});

		//COMMENTED AT 29/08/2013
		//		contextMenu.add(mnRead);

		contextMenu.add(new SeparatorMenuItem());

		MenuItem insertFolder = new MenuItem();
		insertFolder.setId(WorkspaceOperation.INSERT_FOLDER.getId());
		insertFolder.setText(ConstantsExplorer.MESSAGE_ADD_FOLDER);
		insertFolder.setIcon(Resources.getIconAddFolder());

		insertFolder.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				final FileModel sourceFileModel = listSelectedItems.get(0);

				if (sourceFileModel != null) {

					eventBus.fireEvent(new AddFolderEvent(sourceFileModel, sourceFileModel.getParentFileModel()));
				}
			}
		});

		contextMenu.add(insertFolder);


		MenuItem createShareFolder = new MenuItem();
		createShareFolder.setId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId());
		createShareFolder.setText(ConstantsExplorer.MESSAGE_ADD_SHARED_FOLDER);
		createShareFolder.setIcon(Resources.getIconSharedFolder());

		createShareFolder.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				FileModel sourceFileModel = listSelectedItems.get(0);

				if (sourceFileModel != null) {
					eventBus.fireEvent(new CreateSharedFolderEvent(sourceFileModel, sourceFileModel.getParentFileModel(), true));
				}
			}
		});
		contextMenu.add(createShareFolder);

		// publish on data catalogue
		MenuItem publishOnDataCatalogue = new MenuItem();
		publishOnDataCatalogue.setId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId());
		publishOnDataCatalogue.setText(ConstantsExplorer.MESSAGE_DATA_CATALOGUE_PUBLISH);
		publishOnDataCatalogue.setIcon(Resources.getIconDataCataloguePublish());   // TODO change icon

		publishOnDataCatalogue.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				FileModel sourceFileModel = listSelectedItems.get(0);
				if (sourceFileModel != null) {
					eventBus.fireEvent(new PublishOnDataCatalogueEvent(sourceFileModel.getIdentifier()));
				}
			}
		});
		publishOnDataCatalogue.setVisible(false);
		contextMenu.add(publishOnDataCatalogue);

		// publish on data catalogue
		MenuItem publishOnThredds = new MenuItem();
		publishOnThredds.setId(WorkspaceOperation.PUBLISH_ON_THREDDS.getId());
		publishOnThredds.setText(ConstantsExplorer.MESSAGE_THREDDS_PUBLISH);
		publishOnThredds.setIcon(Resources.getIconThreddsPublish());   // TODO change icon

		publishOnThredds.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				FileModel sourceFileModel = listSelectedItems.get(0);
				if (sourceFileModel != null) {
					eventBus.fireEvent(new PublishOnThreddsCatalogueEvent(sourceFileModel));
				}
			}
		});
		publishOnThredds.setVisible(false);
		contextMenu.add(publishOnThredds);


		MenuItem editPermissions = new MenuItem();
		editPermissions.setId(WorkspaceOperation.EDIT_PERMISSIONS.getId());
		editPermissions.setText(ConstantsExplorer.EDIT_PERMISSIONS);
		editPermissions.setIcon(Resources.getIconPermissions());

		editPermissions.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				FileModel sourceFileModel = listSelectedItems.get(0);

				if (sourceFileModel != null) {
					eventBus.fireEvent(new EditUserPermissionEvent(sourceFileModel));
				}
			}
		});

		contextMenu.add(editPermissions);

		//Add Url
		MenuItem addUrl = new MenuItem();
		addUrl.setId(WorkspaceOperation.ADD_URL.getId());
		addUrl.setText(ConstantsExplorer.MESSAGE_ADD_URL);
		addUrl.setIcon(Resources.getIconAddUrl());

		addUrl.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				FileModel selected = listSelectedItems.get(0);

				FileModel parent = getDirectoryOrParent(selected);

				if(parent!=null){
					eventBus.fireEvent(new CreateUrlEvent(null, parent));
				}

			}
		});

		contextMenu.add(addUrl);
		contextMenu.add(new SeparatorMenuItem());

		MenuItem copy = new MenuItem();
		copy.setId(WorkspaceOperation.COPY.getId());
		copy.setText(ConstantsExplorer.COPYITEM);
		copy.setIcon(Resources.getIconCopy());

		copy.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				List<String> ids = FileModelUtils.convertFileModelsToIds(listSelectedItems);
				if(ids.size()>0){
					CutCopyAndPaste.copy(ids, OperationType.COPY);
					eventBus.fireEvent(new CopytemEvent(ids));
				}
			}
		});

		contextMenu.add(copy);

		MenuItem paste = new MenuItem();
		paste.setId(WorkspaceOperation.PASTE.getId());
		paste.setText(ConstantsExplorer.PASTEITEM);
		paste.setIcon(Resources.getIconPaste());

		paste.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel target : listSelectedItems) {
					FileModel parentTarget = getDirectoryOrParent(target);
					if(parentTarget!=null){

						eventBus.fireEvent(new PasteItemEvent(CutCopyAndPaste.getCopiedIdsFilesModel(), parentTarget.getIdentifier(), CutCopyAndPaste.getOperationType()));
						CutCopyAndPaste.setCopiedIdsFileModels(null);
						CutCopyAndPaste.setOperationType(null);

					}
				}

			}
		});

		contextMenu.add(paste);

		MenuItem share = new MenuItem();
		share.setId(WorkspaceOperation.SHARE.getId());
		share.setText("Share");
		share.setIcon(Resources.getIconShareFolder());

		share.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				FileModel sourceFileModel = listSelectedItems.get(0);

				if (sourceFileModel != null) {
					eventBus.fireEvent(new CreateSharedFolderEvent(sourceFileModel, sourceFileModel.getParentFileModel(),false));
				}

			}
		});

		contextMenu.add(share);

		MenuItem unShare = new MenuItem();
		unShare.setId(WorkspaceOperation.UNSHARE.getId());
		unShare.setText("UnShare");
		unShare.setIcon(Resources.getIconUnShareUser());

		unShare.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				for (FileModel target : listSelectedItems) {
					if(target.getIdentifier()!=null){
						eventBus.fireEvent(	new UnShareFolderEvent(target));

					}
				}

			}
		});

		contextMenu.add(unShare);

		MenuItem rename = new MenuItem();
		rename.setId(WorkspaceOperation.RENAME.getId());
		rename.setText("Rename Item");
		rename.setIcon(Resources.getIconRenameItem());

		rename.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel target : listSelectedItems) {
					if(target.getIdentifier()!=null){
						eventBus.fireEvent(new RenameItemEvent(target));
					}
				}

			}
		});

		contextMenu.add(rename);

		MenuItem remove = new MenuItem();
		remove.setId(WorkspaceOperation.REMOVE.getId());
		remove.setText(ConstantsExplorer.MESSAGE_DELETE_ITEM);
		remove.setIcon(Resources.getIconDeleteItem());

		remove.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					eventBus.fireEvent(new DeleteItemEvent(sel));
				}

			}
		});

		contextMenu.add(remove);

		contextMenu.add(new SeparatorMenuItem());
		MenuItem show = new MenuItem();
		show.setId(WorkspaceOperation.SHOW.getId());
		show.setText(ConstantsExplorer.MESSAGE_SHOW);
		show.setIcon(Resources.getIconShow());

		show.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {


				for (final FileModel sel : listSelectedItems) {
					eventBus.fireEvent(new FileDownloadEvent(sel.getIdentifier(), sel.getName(), DownloadType.SHOW, sel.isDirectory() || sel.isVreFolder(), null));
				}
			}
		});


		contextMenu.add(show);

		MenuItem viewWebDav = new MenuItem();
		viewWebDav.setId(WorkspaceOperation.WEBDAV_URL.getId());
		viewWebDav.setText(ConstantsExplorer.MESSAGE_WEBDAV_URL);
		viewWebDav.setIcon(Resources.getIconUrlWebDav());

		viewWebDav.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {


				for (final FileModel sel : listSelectedItems)
					eventBus.fireEvent(new WebDavUrlEvent(sel.getIdentifier()));


			}
		});


		contextMenu.add(viewWebDav);


		MenuItem sendTo = new MenuItem();
		sendTo.setId(WorkspaceOperation.SENDTO.getId());
		sendTo.setText(ConstantsExplorer.MESSAGE_SEND_TO);
		sendTo.setIcon(Resources.getIconSendTo());

		sendTo.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				if(listSelectedItems!=null && listSelectedItems.size()>0)
					eventBus.fireEvent(new SendMessageEvent(listSelectedItems));

			}
		});


		contextMenu.add(sendTo);

		MenuItem upload = new MenuItem();
		upload.setId(WorkspaceOperation.UPLOAD_FILE.getId());
		upload.setText(ConstantsExplorer.MESSAGE_UPLOAD_FILE);
		upload.setIcon(Resources.getIconFileUpload());

		upload.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (final FileModel sel : listSelectedItems) {

					FileModel parent = getDirectoryOrParent(sel);
					eventBus.fireEvent(new FileUploadEvent(parent, WS_UPLOAD_TYPE.File));

				}

			}
		});

		contextMenu.add(upload);

		MenuItem uploadArchive = new MenuItem();
		uploadArchive.setId(WorkspaceOperation.UPLOAD_ARCHIVE.getId());
		uploadArchive.setText(ConstantsExplorer.MESSAGE_UPLOAD_ARCHIVE);
		uploadArchive.setToolTip("Upload a zip archive into workspace");
		uploadArchive.setIcon(Resources.getIconArchiveUpload());

		uploadArchive.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {

					FileModel parent = getDirectoryOrParent(sel);

					eventBus.fireEvent(new FileUploadEvent(parent, WS_UPLOAD_TYPE.Archive));
				}

			}
		});

		contextMenu.add(uploadArchive);


		MenuItem downloadArchive = new MenuItem();
		downloadArchive.setId(WorkspaceOperation.DOWNLOAD.getId());
		downloadArchive.setText(ConstantsExplorer.MESSAGE_DOWNLOAD_ITEM);
		downloadArchive.setIcon(Resources.getIconDownload());

		downloadArchive.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					eventBus.fireEvent(new FileDownloadEvent(sel.getIdentifier(), sel.getName(), DownloadType.DOWNLOAD, sel.isDirectory() || sel.isVreFolder(), null));
				}
			}
		});

		contextMenu.add(downloadArchive);
		contextMenu.add(new SeparatorMenuItem());

		MenuItem changePermission = new MenuItem();
		changePermission.setId(WorkspaceOperation.VRE_CHANGE_PERIMISSIONS.getId());
		changePermission.setText("Change Permission");
		changePermission.setIcon(Resources.getIconWriteAll());

		changePermission.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				for (FileModel target : listSelectedItems) {
					if(target.getIdentifier()!=null){
						eventBus.fireEvent(new VRESettingPermissionEvent(target));

					}
				}

			}
		});

		contextMenu.add(changePermission);
		contextMenu.add(new SeparatorMenuItem());

		MenuItem addAdministrator = new MenuItem();
		//		addAdministrator.setId(WorkspaceOperation.ADD_ADMINISTRATOR.getId());
		addAdministrator.setText("Manage Administrator/s");
		addAdministrator.setIcon(Resources.getIconManageAdministrator());

		addAdministrator.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					eventBus.fireEvent(new AddAdministratorEvent(sel));
				}
			}
		});

		//		contextMenu.add(addAdministrator);

		MenuItem refreshItem = new MenuItem();
		refreshItem.setId(WorkspaceOperation.REFRESH_FOLDER.getId());
		refreshItem.setText(ConstantsExplorer.MESSAGE_REFRESH_FOLDER);
		refreshItem.setIcon(Resources.getIconRefresh());

		refreshItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					eventBus.fireEvent(new RefreshFolderEvent(sel, true, false, false));
				}
			}
		});

		contextMenu.add(refreshItem);
		contextMenu.add(new SeparatorMenuItem());

	}

	/**
	 * Clear list selected items.
	 */
	public void clearListSelectedItems() {
		listSelectedItems.clear();
	}


	/**
	 * Sets the hide sharing.
	 */
	public void setHideSharing() {
		hideSharing = true;
	}

	/**
	 * Called from context menu on grid.
	 *
	 * @param targetFileModel the target file model
	 * @param posX the pos x
	 * @param posY the pos y
	 */
	public void openContextMenuOnItem(final FileModel targetFileModel, final int posX, final int posY) {
		clearListSelectedItems();
		listSelectedItems.add(0, targetFileModel);

		printSelected();

		if(targetFileModel!=null){

			if(targetFileModel.getParentFileModel()==null){

				Info.display("Wait", "loading available operations..");

				AppControllerExplorer.rpcWorkspaceService.getParentByItemId(targetFileModel.getIdentifier(), new AsyncCallback<FileModel>() {

					@Override
					public void onFailure(Throwable caught) {
						Info.display("Error", "sorry an error occurrend on loading available operations");

					}

					@Override
					public void onSuccess(FileModel result) {
						if(result!=null){
							targetFileModel.setParentFileModel(result);
							viewContextMenu(targetFileModel, posX, posY);
						}
						else
							Info.display("Error", "sorry an error occurrend on loading available operations");

					}

				});

			}
			else
				viewContextMenu(targetFileModel, posX, posY);

		}
	}


	/**
	 * View context menu.
	 *
	 * @param targetFileModel the target file model
	 * @param posX the pos x
	 * @param posY the pos y
	 */
	private void viewContextMenu(FileModel targetFileModel, int posX, int posY){

		contextMenuSwitch(targetFileModel);
		contextMenu.getItemByItemId(WorkspaceOperation.INSERT_FOLDER.getId()).setVisible(false); //set invisible create folder
		contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(false); //set invisible create shared folder
		contextMenu.getItemByItemId(WorkspaceOperation.WEBDAV_URL.getId()).setVisible(false); //set invisible webdav url
		contextMenu.getItemByItemId(WorkspaceOperation.UPLOAD_FILE.getId()).setVisible(false); //set invisible upload file
		contextMenu.getItemByItemId(WorkspaceOperation.UPLOAD_ARCHIVE.getId()).setVisible(false); //set invisible upload archive
		contextMenu.getItemByItemId(WorkspaceOperation.ADD_URL.getId()).setVisible(false); //set invisible add url
		contextMenu.getItemByItemId(WorkspaceOperation.REFRESH_FOLDER.getId()).setVisible(false); //set invisible refresh
		contextMenu.showAt(posX, posY);
	}


	/**
	 * Prints the selected.
	 */
	private void printSelected(){

		for (FileModel sel: listSelectedItems) {
			System.out.println("selected " +sel.getName() );
		}
	}


	/**
	 * switch visible operation on context menu according to selected item.
	 *
	 * @param selectedItem the selected item
	 */
	public void contextMenuSwitch(FileModel selectedItem) {

		contextMenu.getItemByItemId(WorkspaceOperation.INSERT_FOLDER.getId()).setVisible(true); //insert folder
		contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(true); //insert shared folder
		contextMenu.getItemByItemId(WorkspaceOperation.UPLOAD_FILE.getId()).setVisible(true);
		contextMenu.getItemByItemId(WorkspaceOperation.UPLOAD_ARCHIVE.getId()).setVisible(true);
		contextMenu.getItemByItemId(WorkspaceOperation.ADD_URL.getId()).setVisible(true);
		contextMenu.getItemByItemId(WorkspaceOperation.SENDTO.getId()).setVisible(true); //send to
		contextMenu.getItemByItemId(WorkspaceOperation.DOWNLOAD.getId()).setVisible(true); //DOWNLOAD
		contextMenu.getItemByItemId(WorkspaceOperation.COPY.getId()).setEnabled(true);

		contextMenu.getItemByItemId(WorkspaceOperation.PREVIEW.getId()).setVisible(false); //preview image
		contextMenu.getItemByItemId(WorkspaceOperation.LINK.getId()).setVisible(false); //open link
		contextMenu.getItemByItemId(WorkspaceOperation.SHOW.getId()).setVisible(false); //show
		//contextMenu.getItemByItemId(WorkspaceOperation.OPEN_REPORT.getId()).setVisible(false); //open report
		//contextMenu.getItemByItemId(WorkspaceOperation.OPEN_REPORT_TEMPLATE.getId()).setVisible(false); //open report template
		contextMenu.getItemByItemId(WorkspaceOperation.PASTE.getId()).setEnabled(false); //paste
		contextMenu.getItemByItemId(WorkspaceOperation.REFRESH_FOLDER.getId()).setVisible(false); //refresh
		contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(false); //public link
		contextMenu.getItemByItemId(WorkspaceOperation.FOLDER_LINK.getId()).setVisible(false); //folder link
		contextMenu.getItemByItemId(WorkspaceOperation.FOLDER_LINK_REMOVE.getId()).setVisible(false); //folder link remove
		//		contextMenu.getItemByItemId(WorkspaceOperation.ADD_ADMINISTRATOR.getId()).setVisible(false); //public link
		contextMenu.getItemByItemId(WorkspaceOperation.EDIT_PERMISSIONS.getId()).setVisible(false);
		contextMenu.getItemByItemId(WorkspaceOperation.SHARE.getId()).setVisible(false); //SHARE
		contextMenu.getItemByItemId(WorkspaceOperation.UNSHARE.getId()).setVisible(false); //UNSHARE
		contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(false); //publish on data catalogue
		contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_THREDDS.getId()).setVisible(false); //publish on thredds

		contextMenu.getItemByItemId(WorkspaceOperation.SHARE_LINK.getId()).setVisible(false); //SHARE

		contextMenu.getItemByItemId(WorkspaceOperation.VRE_CHANGE_PERIMISSIONS.getId()).setVisible(false); //VRE CHANGE PERMISSIONS

		contextMenu.getItemByItemId(WorkspaceOperation.VERSIONING.getId()).setVisible(false);

		if(selectedItem.isShared()){ //SHARE LINK ON SHARED ITEM
			contextMenu.getItemByItemId(WorkspaceOperation.SHARE_LINK.getId()).setVisible(true);
		}

		//IS VRE FOLDER or SPECIAL FOLDER?
		if(selectedItem.isVreFolder() || selectedItem.isSpecialFolder()){

			contextMenu.getItemByItemId(WorkspaceOperation.SHARE.getId()).setVisible(false); //SHARE
			contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(false); //insert shared folder
			contextMenu.getItemByItemId(WorkspaceOperation.UNSHARE.getId()).setVisible(false); //UNSHARE
			contextMenu.getItemByItemId(WorkspaceOperation.RENAME.getId()).setVisible(false); //RENAME
			contextMenu.getItemByItemId(WorkspaceOperation.REMOVE.getId()).setVisible(false); //REMOVE
			contextMenu.getItemByItemId(WorkspaceOperation.REFRESH_FOLDER.getId()).setVisible(true); //REFRESH_FOLDER
			contextMenu.getItemByItemId(WorkspaceOperation.COPY.getId()).setEnabled(false);

			//ADDED 14/03/2014
			if(selectedItem.isVreFolder()){
				contextMenu.getItemByItemId(WorkspaceOperation.VRE_CHANGE_PERIMISSIONS.getId()).setVisible(true); //REFRESH_FOLDER

				if(CutCopyAndPaste.getCopiedIdsFilesModel()!=null)
					contextMenu.getItemByItemId(WorkspaceOperation.PASTE.getId()).setEnabled(true); //enable paste button
			}

			//			if(selectedItem.isVreFolder() && CutCopyAndPaste.getCopiedIdsFilesModel()!=null)
			//				contextMenu.getItemByItemId(WorkspaceOperation.PASTE.getId()).setEnabled(true); //enable paste button
			else if(selectedItem.isSpecialFolder()){
				//contextMenu.getItemByItemId(WorkspaceOperation.COPY.getId()).setEnabled(false);
				contextMenu.getItemByItemId(WorkspaceOperation.PASTE.getId()).setEnabled(false);
				contextMenu.getItemByItemId(WorkspaceOperation.UPLOAD_FILE.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.UPLOAD_ARCHIVE.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.ADD_URL.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.SENDTO.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.INSERT_FOLDER.getId()).setVisible(false); //insert folder
				contextMenu.getItemByItemId(WorkspaceOperation.DOWNLOAD.getId()).setVisible(false);
			}
			return;
		}

		//CASE DIRECTORY
		if(selectedItem.isDirectory()){
			contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(true);
			contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_THREDDS.getId()).setVisible(true);
			contextMenu.getItemByItemId(WorkspaceOperation.SHARE.getId()).setVisible(true); //SHARE
			contextMenu.getItemByItemId(WorkspaceOperation.SHOW.getId()).setVisible(false); //show
			contextMenu.getItemByItemId(WorkspaceOperation.REFRESH_FOLDER.getId()).setVisible(true); //refresh
			contextMenu.getItemByItemId(WorkspaceOperation.SENDTO.getId()).setVisible(false); //send to
			contextMenu.getItemByItemId(WorkspaceOperation.FOLDER_LINK.getId()).setVisible(true); //folder link
			if(selectedItem.isShared()){//IS SHARED
				//contextMenu.getItemByItemId(WorkspaceOperation.ADD_ADMINISTRATOR.getId()).setVisible(true); //add administrator
				if(selectedItem.isShareable()){ //IS SHARABLE
					contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(false); //insert shared folder
					contextMenu.getItemByItemId(WorkspaceOperation.UNSHARE.getId()).setVisible(true);
				}else { //IS SUBFOLDER
					contextMenu.getItemByItemId(WorkspaceOperation.EDIT_PERMISSIONS.getId()).setVisible(true);
				}

			}else if(selectedItem.isRoot()){ //IS ROOT
				contextMenu.getItemByItemId(WorkspaceOperation.SHARE.getId()).setVisible(false); //SHARE
				contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(true); //insert shared folder
				contextMenu.getItemByItemId(WorkspaceOperation.UNSHARE.getId()).setVisible(false); //UNSHARE
			}

			if(selectedItem.isPublic())
				contextMenu.getItemByItemId(WorkspaceOperation.FOLDER_LINK_REMOVE.getId()).setVisible(true); //folder link

			GWT.log("HideSharing = " + hideSharing);
			//not supported in tree Reports
			if (hideSharing) {
				contextMenu.getItemByItemId(WorkspaceOperation.SHARE.getId()).setVisible(false); //SHARE
				contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(false); //insert shared folder
				contextMenu.getItemByItemId(WorkspaceOperation.UNSHARE.getId()).setVisible(false); //UNSHARE
			}
		}
		else{
			contextMenu.getItemByItemId(WorkspaceOperation.SHOW.getId()).setVisible(true);

			switch(selectedItem.getGXTFolderItemType()){

			case EXTERNAL_IMAGE:
				contextMenu.getItemByItemId(WorkspaceOperation.PREVIEW.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(true); //public link
				contextMenu.getItemByItemId(WorkspaceOperation.VERSIONING.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(true);
				break;
			case EXTERNAL_FILE:
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(true); //public link
				contextMenu.getItemByItemId(WorkspaceOperation.VERSIONING.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(true);
				break;
			case EXTERNAL_PDF_FILE:
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(true); //public link
				contextMenu.getItemByItemId(WorkspaceOperation.VERSIONING.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(true);
				break;
			case EXTERNAL_URL:
				contextMenu.getItemByItemId(WorkspaceOperation.LINK.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(true);
				break;
			case REPORT_TEMPLATE:
				//contextMenu.getItemByItemId(WorkspaceOperation.OPEN_REPORT_TEMPLATE.getId()).setVisible(true);
				break;
			case REPORT:
				//contextMenu.getItemByItemId(WorkspaceOperation.OPEN_REPORT.getId()).setVisible(true);
				break;
			case QUERY:
				break;
			case TIME_SERIES:
				break;
				//				case AQUAMAPS_ITEM:
				//					break;
			case PDF_DOCUMENT:
				break;
			case IMAGE_DOCUMENT:
				contextMenu.getItemByItemId(WorkspaceOperation.PREVIEW.getId()).setVisible(true);
				break;
			case DOCUMENT:
				break;
			case URL_DOCUMENT:
				contextMenu.getItemByItemId(WorkspaceOperation.LINK.getId()).setVisible(true);
				break;
			case METADATA:
				break;
			case WORKFLOW_REPORT:
				break;
			case WORKFLOW_TEMPLATE:
				break;
			case EXTERNAL_RESOURCE_LINK:
				break;
			default:

			}

		}


		if(CutCopyAndPaste.getCopiedIdsFilesModel()!=null)
			contextMenu.getItemByItemId(WorkspaceOperation.PASTE.getId()).setEnabled(true); //enable paste button

	}

	/**
	 * The method return input file model if is directory otherwise parent of file model.
	 *
	 * @param fileModel the file model
	 * @return the directory or parent
	 */
	private FileModel getDirectoryOrParent(FileModel fileModel){

		if(fileModel!=null){
			if(fileModel.isDirectory())
				return fileModel;
			else
				return fileModel.getParentFileModel();
		}

		return null;
	}


	/**
	 * Gets the context menu.
	 *
	 * @return the context menu
	 */
	public Menu getContextMenu(){
		return this.contextMenu;
	}

	/**
	 * Sets the page position.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setPagePosition(int x, int y){
		this.contextMenu.setPagePosition(x, y);
	}


	/**
	 * Gets the list selected items.
	 *
	 * @return the list selected items
	 */
	public List<FileModel> getListSelectedItems() {
		return listSelectedItems;
	}


	/**
	 * Sets the list selected items.
	 *
	 * @param listSelectedItems the new list selected items
	 */
	public void setListSelectedItems(List<FileModel> listSelectedItems) {
		this.listSelectedItems.clear();
		this.listSelectedItems = listSelectedItems;
	}

}

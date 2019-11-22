package org.gcube.portlets.user.workspace.client.view.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portal.stohubicons.shared.resources.StorageHubIconResources;
import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WS_UPLOAD_TYPE;
import org.gcube.portlets.user.workspace.client.MyWindow;
import org.gcube.portlets.user.workspace.client.constant.WorkspaceOperation;
import org.gcube.portlets.user.workspace.client.event.AccountingHistoryEvent;
import org.gcube.portlets.user.workspace.client.event.AccountingReadersEvent;
import org.gcube.portlets.user.workspace.client.event.AddAdministratorEvent;
import org.gcube.portlets.user.workspace.client.event.AddFolderEvent;
import org.gcube.portlets.user.workspace.client.event.CopyItemsEvent;
import org.gcube.portlets.user.workspace.client.event.CreateSharedFolderEvent;
import org.gcube.portlets.user.workspace.client.event.CreateUrlEvent;
import org.gcube.portlets.user.workspace.client.event.DeleteItemEvent;
import org.gcube.portlets.user.workspace.client.event.EditUserPermissionEvent;
import org.gcube.portlets.user.workspace.client.event.ExecuteDataMinerTaskEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent.DownloadType;
import org.gcube.portlets.user.workspace.client.event.FileUploadEvent;
import org.gcube.portlets.user.workspace.client.event.GetInfoEvent;
import org.gcube.portlets.user.workspace.client.event.GetShareableLink;
import org.gcube.portlets.user.workspace.client.event.ImagePreviewEvent;
import org.gcube.portlets.user.workspace.client.event.MoveItemsEvent;
import org.gcube.portlets.user.workspace.client.event.OpenUrlEvent;
import org.gcube.portlets.user.workspace.client.event.PublishOnDataCatalogueEvent;
import org.gcube.portlets.user.workspace.client.event.RefreshFolderEvent;
import org.gcube.portlets.user.workspace.client.event.RenameItemEvent;
import org.gcube.portlets.user.workspace.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.workspace.client.event.SyncWithThreddsCatalogueEvent;
import org.gcube.portlets.user.workspace.client.event.UnShareFolderEvent;
import org.gcube.portlets.user.workspace.client.event.VRESettingPermissionEvent;
import org.gcube.portlets.user.workspace.client.event.VersioningHistoryShowEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.util.FileModelUtils;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class ContextMenuTree.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 */
public class ContextMenuTree {

	private Menu contextMenu = new Menu();
	private HandlerManager eventBus = AppControllerExplorer.getEventBus();
	private List<FileModel> listSelectedItems = null;

	private MenuItem mnRead = new MenuItem();

	private boolean hideSharing = false;
	//tells if you're on PARTHENOS Gateway or not
	private boolean showCLARINSwitchBoardOption = false;
	/**
	 * Instantiates a new context menu tree.
	 */
	public ContextMenuTree() {
		this.contextMenu.setWidth(200);
		this.listSelectedItems = new ArrayList<FileModel>();
		createContextMenu();
	}


	/**
	 * Creates the context menu.
	 */
	private void createContextMenu() {
		//PARTHENOS GATEWAY CASE Show CLARIN SwitchBoard
		if (ConstantsExplorer.PARTHENOS_GATEWAY_HOST_NAME.compareToIgnoreCase(Window.Location.getHostName()) == 0) {
			showCLARINSwitchBoardOption = true;
		}


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
		//contextMenu.add(new SeparatorMenuItem());
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


//		//SHARE LINK
//		MenuItem mnGetLink = new MenuItem();
//		mnGetLink.setId(WorkspaceOperation.SHARE_LINK.getId());
//		mnGetLink.setText(ConstantsExplorer.MESSAGE_SHARE_LINK);
//		mnGetLink.setIcon(Resources.getIconShareLink());
//
//		mnGetLink.addSelectionListener(new SelectionListener<MenuEvent>() {
//			public void componentSelected(MenuEvent ce) {
//
//				for (FileModel sel : listSelectedItems) {
//					eventBus.fireEvent(new GetShareLinkEvent(sel));
//				}
//			}
//		});
//
//		contextMenu.add(mnGetLink);

		//PUBLIC LINK
		MenuItem mnPublicLink = new MenuItem();
		mnPublicLink.setId(WorkspaceOperation.PUBLIC_LINK.getId());
		mnPublicLink.setText(ConstantsExplorer.MESSAGE_SHAREABLE_LINK);
		mnPublicLink.setIcon(Resources.getIconShareLink());

		mnPublicLink.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				for (FileModel sel : listSelectedItems) {
					//see Task #17552
//					if(sel.isDirectory()) {
//						eventBus.fireEvent(new GetFolderLinkEvent(sel, true));
//					}else {
//						eventBus.fireEvent(new GetShareableLink(sel, null));
//					}
//					
					AppControllerExplorer.getEventBus().fireEvent(new GetShareableLink(sel, null));
				}
			}
		});

		contextMenu.add(mnPublicLink);


//		//FOLDER LINK
//		MenuItem mnFolderLink = new MenuItem();
//		mnFolderLink.setId(WorkspaceOperation.FOLDER_LINK.getId());
//		mnFolderLink.setText(ConstantsExplorer.MESSAGE_FOLDER_LINK);
//		mnFolderLink.setIcon(Resources.getIconFolderPublic());
//
//		mnFolderLink.addSelectionListener(new SelectionListener<MenuEvent>() {
//			public void componentSelected(MenuEvent ce) {
//
//				for (FileModel sel : listSelectedItems) {
//					if(sel.isDirectory())
//						eventBus.fireEvent(new GetFolderLinkEvent(sel, true));
//				}
//			}
//		});
//		
//		//see Task #17552
//		contextMenu.add(mnFolderLink);


		//FOLDER LINK REMOVE
//		MenuItem mnFolderLinkRemove = new MenuItem();
//		mnFolderLinkRemove.setId(WorkspaceOperation.FOLDER_LINK_REMOVE.getId());
//		mnFolderLinkRemove.setText(ConstantsExplorer.MESSAGE_FOLDER_LINK_REMOVE);
//		mnFolderLinkRemove.setIcon(Resources.getIconFolderPublicRemove());
//
//		mnFolderLinkRemove.addSelectionListener(new SelectionListener<MenuEvent>() {
//			public void componentSelected(MenuEvent ce) {
//
//				for (FileModel sel : listSelectedItems) {
//					if(sel.isDirectory())
//						eventBus.fireEvent(new GetFolderLinkEvent(sel, false));
//				}
//			}
//		});

//		contextMenu.add(mnFolderLinkRemove);

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

		contextMenu.add(new SeparatorMenuItem());

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

		//contextMenu.add(new SeparatorMenuItem());

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

		//Add Url
		MenuItem addUrl = new MenuItem();
		addUrl.setId(WorkspaceOperation.ADD_URL.getId());
		addUrl.setText(ConstantsExplorer.MESSAGE_ADD_URL);
		addUrl.setIcon(Resources.getIconAddUrl());

		addUrl.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				FileModel selected = listSelectedItems.get(0);
				FileModel parent = null;

				if(selected.isDirectory()){
					parent = selected; //Creating New URL in the selected folder
				}else{
					parent = selected.getParentFileModel(); //Creating New URL as brother of selected file
				}

				if(parent!=null){
					eventBus.fireEvent(new CreateUrlEvent(null, parent));
				}

			}
		});

		contextMenu.add(addUrl);
		contextMenu.add(new SeparatorMenuItem());


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
					eventBus.fireEvent(new SyncWithThreddsCatalogueEvent(sourceFileModel));
				}
			}
		});
		publishOnThredds.setVisible(false);
		contextMenu.add(publishOnThredds);

		// executre DM task
		MenuItem executeDMTask = new MenuItem();
		executeDMTask.setId(WorkspaceOperation.EXECUTE_DM_TASK.getId());
		executeDMTask.setText(ConstantsExplorer.MESSAGE_EXECUTE_DM_TASK);
		executeDMTask.setIcon(Resources.getIconShell());

		executeDMTask.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				FileModel sourceFileModel = listSelectedItems.get(0);
				if (sourceFileModel != null) {
					eventBus.fireEvent(new ExecuteDataMinerTaskEvent(sourceFileModel));
				}
			}
		});
		executeDMTask.setVisible(false);
		contextMenu.add(executeDMTask);

		//send to Switchboard
		MenuItem sendToSwitchboard = new MenuItem();
		sendToSwitchboard.setId(WorkspaceOperation.SEND_TO_SWITCHBOARD.getId());
		sendToSwitchboard.setText(ConstantsExplorer.MESSAGE_CLARIN_SWITCHBOARD);
		sendToSwitchboard.setIcon(Resources.getIconSendToSwitchboard());

		sendToSwitchboard.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				FileModel sourceFileModel = listSelectedItems.get(0);
				if (sourceFileModel != null) {
					final MyWindow window = MyWindow.open(null, "_blank", null);
					AppControllerExplorer.rpcWorkspaceService.getLinkForSendToSwitchBoard(sourceFileModel.getIdentifier(), new AsyncCallback<String>() {
						@Override
						public void onSuccess(String link) {
							window.setUrl(link);
						}
						@Override
						public void onFailure(Throwable caught) {
							if(caught instanceof SessionExpiredException){
								GWT.log("Session expired");
								AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
								return;
							}
							new MessageBoxAlert("Error", caught.getMessage(), null);
							 window.close();
						}
					});
				}
			}
		});
		sendToSwitchboard.setVisible(false);
		contextMenu.add(sendToSwitchboard);


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

		contextMenu.add(new SeparatorMenuItem());

		MenuItem moveto = new MenuItem();
		moveto.setId(WorkspaceOperation.MOVE.getId());
		moveto.setText(ConstantsExplorer.MOVE);
		moveto.setIcon(AbstractImagePrototype.create(StorageHubIconResources.INSTANCE.move16()));
//		Widget element = new Widget();
//		element.getElement().setInnerHTML(theIcon.getHtml());
//		Image theImage = Image.wrap(element.getElement());

		moveto.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				List<String> ids = FileModelUtils.convertFileModelsToIds(listSelectedItems);
				if(ids.size()>0){
					FileModel theSourceParent = listSelectedItems.get(0).getParentFileModel();
					eventBus.fireEvent(new MoveItemsEvent(ids, null, theSourceParent));
				}
			}
		});

		contextMenu.add(moveto);

		MenuItem copy = new MenuItem();
		copy.setId(WorkspaceOperation.COPY.getId());
		copy.setText(ConstantsExplorer.COPY);
		copy.setIcon(AbstractImagePrototype.create(StorageHubIconResources.INSTANCE.PASTE()));

		copy.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				List<String> ids = FileModelUtils.convertFileModelsToIds(listSelectedItems);
				if(ids.size()>0){
					FileModel theSourceParent = listSelectedItems.get(0).getParentFileModel();
					eventBus.fireEvent(new CopyItemsEvent(ids, theSourceParent.getIdentifier()));
				}

			}
		});

		contextMenu.add(copy);

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

//		MenuItem viewWebDav = new MenuItem();
//		viewWebDav.setId(WorkspaceOperation.WEBDAV_URL.getId());
//		viewWebDav.setText(ConstantsExplorer.MESSAGE_WEBDAV_URL);
//		viewWebDav.setIcon(Resources.getIconUrlWebDav());
//
//		viewWebDav.addSelectionListener(new SelectionListener<MenuEvent>() {
//			public void componentSelected(MenuEvent ce) {
//
//
//				for (final FileModel sel : listSelectedItems)
//					eventBus.fireEvent(new WebDavUrlEvent(sel.getIdentifier()));
//
//
//			}
//		});


		//contextMenu.add(viewWebDav);

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
		//contextMenu.add(new SeparatorMenuItem());

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
		//contextMenu.add(new SeparatorMenuItem());

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
		//contextMenu.add(new SeparatorMenuItem());

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
	 * @param selectedItems the selected items
	 * @param posX the pos x
	 * @param posY the pos y
	 */
	public void openContextMenuOnItem(final List<FileModel> selectedItems, final int posX, final int posY) {
		clearListSelectedItems();

		if(selectedItems!=null){

			int i=0;
			for (FileModel fileModel : selectedItems) {
				listSelectedItems.add(i, fileModel);
				i++;
			}

			final FileModel targetFileModel = listSelectedItems.get(0); //selecting the first item
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
							viewContextMenu(Arrays.asList(targetFileModel), posX, posY);
						}
						else
							Info.display("Error", "sorry an error occurrend on loading available operations");

					}

				});

			}
			else
				viewContextMenu(listSelectedItems, posX, posY);

		}
	}


	/**
	 * View context menu.
	 * Called by right-click
	 * @param listFileModel the list file model
	 * @param posX the pos x
	 * @param posY the pos y
	 */
	protected void viewContextMenu(List<FileModel> listFileModel, int posX, int posY){

		//SET NOT VISIBLE ALL OPERATIONS
		for (WorkspaceOperation value : WorkspaceOperation.values()) {
			try{
				contextMenu.getItemByItemId(value.getId()).setVisible(false); //setting all to visible false
			}catch(Exception e){

			}
		}

		//SINGLE SELECTION
		if(listFileModel.size()==1){
			contextMenuSwitch(listFileModel.get(0));

			//IT IS A SINGLE SELECTION TO FILE.. ACTIVATING COPY
			if(!listFileModel.get(0).isDirectory())
				contextMenu.getItemByItemId(WorkspaceOperation.COPY.getId()).setVisible(true);
		}else{
		//IT IS MULTIPLE-SELECTION
			boolean foundDir = false;
			for (FileModel fileModel : listFileModel) {
				if(fileModel.isDirectory()){
					foundDir = true;
					break;
				}
			}

			//IF NO DIRECTORY FOUND AS SELECTION...  ACTIVATING COPY
			if(!foundDir)
				contextMenu.getItemByItemId(WorkspaceOperation.COPY.getId()).setVisible(true);

			contextMenu.getItemByItemId(WorkspaceOperation.MOVE.getId()).setVisible(true);
		}

		//SETTINGS GET_INFO TO VISIBLE ALWAYS
		contextMenu.getItemByItemId(WorkspaceOperation.GET_INFO.getId()).setVisible(true);

		if(posX!=-1 && posY!=-1)
			contextMenu.showAt(posX, posY);
		else
			contextMenu.show();
	}

	/**
	 * switch visible operation on context menu according to selected item.
	 *
	 * @param selectedItem the selected item
	 */
	private void contextMenuSwitch(FileModel selectedItem) {

		//VALID OPERATIONS FOR FILE AND FOLDER
		contextMenu.getItemByItemId(WorkspaceOperation.DOWNLOAD.getId()).setVisible(true);
		contextMenu.getItemByItemId(WorkspaceOperation.MOVE.getId()).setVisible(true);
		contextMenu.getItemByItemId(WorkspaceOperation.RENAME.getId()).setVisible(true);
		contextMenu.getItemByItemId(WorkspaceOperation.REMOVE.getId()).setVisible(true);
		contextMenu.getItemByItemId(WorkspaceOperation.HISTORY.getId()).setVisible(true);
		contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(false);


		 //SELECTED ITEM IS A FOLDER
		if(selectedItem.isDirectory()){

			showWriteOperations(true);
			showExecuteOperations(true);

			contextMenu.getItemByItemId(WorkspaceOperation.SHARE.getId()).setVisible(true);
			//contextMenu.getItemByItemId(WorkspaceOperation.SHOW.getId()).setVisible(false);
			contextMenu.getItemByItemId(WorkspaceOperation.REFRESH_FOLDER.getId()).setVisible(true);
			//contextMenu.getItemByItemId(WorkspaceOperation.FOLDER_LINK.getId()).setVisible(true); //create folder link, set folder as public
			contextMenu.getItemByItemId(WorkspaceOperation.EXECUTE_DM_TASK.getId()).setVisible(true);
			contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(true);

			if(selectedItem.isShared()){//IS SHARED
				contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(false);
				//contextMenu.getItemByItemId(WorkspaceOperation.SHARE_LINK.getId()).setVisible(true);
				if(selectedItem.isShareable()){ //IS SHARABLE
					contextMenu.getItemByItemId(WorkspaceOperation.UNSHARE.getId()).setVisible(true);
				}else { //IS SUBFOLDER
					contextMenu.getItemByItemId(WorkspaceOperation.EDIT_PERMISSIONS.getId()).setVisible(true);
				}

			}
			
			if(selectedItem.isRoot()){ //IS ROOT
				contextMenu.getItemByItemId(WorkspaceOperation.SHARE.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.UNSHARE.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.RENAME.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.REMOVE.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.MOVE.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.DOWNLOAD.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(false);
				//contextMenu.getItemByItemId(WorkspaceOperation.FOLDER_LINK.getId()).setVisible(false);
				showExecuteOperations(false);
			}

			if(selectedItem.isPublic()) {
				//contextMenu.getItemByItemId(WorkspaceOperation.FOLDER_LINK_REMOVE.getId()).setVisible(true);
			}

			GWT.log("HideSharing = " + hideSharing);
			//not supported in tree Reports
			if (hideSharing) {
				contextMenu.getItemByItemId(WorkspaceOperation.SHARE.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.UNSHARE.getId()).setVisible(false);
			}

	    //SELECTED ITEM IS A FILE
		}else{
			contextMenu.getItemByItemId(WorkspaceOperation.SHOW.getId()).setVisible(true);
			contextMenu.getItemByItemId(WorkspaceOperation.EXECUTE_DM_TASK.getId()).setVisible(true);

			/*
			 * we show the send to CLARIN SwitchBoard if and only if the file is a txt file and showCLARINSwitchBoardOption is true
			 */
			contextMenu.getItemByItemId(WorkspaceOperation.SEND_TO_SWITCHBOARD.getId()).setVisible(selectedItem.getName().endsWith(".txt") && showCLARINSwitchBoardOption);

			switch(selectedItem.getGXTFolderItemType()){

			case EXTERNAL_IMAGE:
				contextMenu.getItemByItemId(WorkspaceOperation.PREVIEW.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(true); //public link
				contextMenu.getItemByItemId(WorkspaceOperation.VERSIONING.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.COPY.getId()).setVisible(true);
				break;
			case EXTERNAL_FILE:
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(true); //public link
				contextMenu.getItemByItemId(WorkspaceOperation.VERSIONING.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.COPY.getId()).setVisible(true);
				break;
			case EXTERNAL_PDF_FILE:
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(true); //public link
				contextMenu.getItemByItemId(WorkspaceOperation.VERSIONING.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.COPY.getId()).setVisible(true);
				break;
			case EXTERNAL_URL:
				contextMenu.getItemByItemId(WorkspaceOperation.LINK.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(true);
				contextMenu.getItemByItemId(WorkspaceOperation.COPY.getId()).setVisible(true);
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

		//IS VRE FOLDER or SPECIAL FOLDER?
		if(selectedItem.isVreFolder() || selectedItem.isSpecialFolder()){

			contextMenu.getItemByItemId(WorkspaceOperation.SHARE.getId()).setVisible(false);
			contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(false);
			contextMenu.getItemByItemId(WorkspaceOperation.UNSHARE.getId()).setVisible(false);
			contextMenu.getItemByItemId(WorkspaceOperation.RENAME.getId()).setVisible(false);
			contextMenu.getItemByItemId(WorkspaceOperation.REMOVE.getId()).setVisible(false);
			contextMenu.getItemByItemId(WorkspaceOperation.MOVE.getId()).setVisible(false);
			contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(false);
			showExecuteOperations(false);

			//ADDED 14/03/2014
			if(selectedItem.isVreFolder()){
				contextMenu.getItemByItemId(WorkspaceOperation.VRE_CHANGE_PERIMISSIONS.getId()).setVisible(true); //VRE_CHANGE_PERIMISSIONS
			}
			else if(selectedItem.isSpecialFolder()){ //e.g. the root folder "VRE Folders"
				showExecuteOperations(false);
				showWriteOperations(false);
				contextMenu.getItemByItemId(WorkspaceOperation.DOWNLOAD.getId()).setVisible(false);
				contextMenu.getItemByItemId(WorkspaceOperation.PUBLIC_LINK.getId()).setVisible(false);
			}
		}
	}

	/**
	 * Show write operations.
	 *
	 * @param visible the visible
	 */
	private void showWriteOperations(boolean visible){
		//CREATE OPERATIONS
		contextMenu.getItemByItemId(WorkspaceOperation.INSERT_FOLDER.getId()).setVisible(visible);
		contextMenu.getItemByItemId(WorkspaceOperation.INSERT_SHARED_FOLDER.getId()).setVisible(visible);
		contextMenu.getItemByItemId(WorkspaceOperation.UPLOAD_FILE.getId()).setVisible(visible);
		contextMenu.getItemByItemId(WorkspaceOperation.UPLOAD_ARCHIVE.getId()).setVisible(visible);
		contextMenu.getItemByItemId(WorkspaceOperation.ADD_URL.getId()).setVisible(visible);

	}

	/**
	 * Show execute operations.
	 *
	 * @param visible the visible
	 */
	private void showExecuteOperations(boolean visible){
		//CREATE OPERATIONS
		contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_DATA_CATALOGUE.getId()).setVisible(visible);
		contextMenu.getItemByItemId(WorkspaceOperation.PUBLISH_ON_THREDDS.getId()).setVisible(visible);
		contextMenu.getItemByItemId(WorkspaceOperation.EXECUTE_DM_TASK.getId()).setVisible(visible);

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

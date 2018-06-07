package org.gcube.portlets.user.workspace.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer.ViewSwitchType;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WS_UPLOAD_TYPE;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WsPortletInitOperation;
import org.gcube.portlets.user.workspace.client.ConstantsPortlet.ViewSwitchTypeInResult;
import org.gcube.portlets.user.workspace.client.event.AccountingHistoryEvent;
import org.gcube.portlets.user.workspace.client.event.AccountingHistoryEventHandler;
import org.gcube.portlets.user.workspace.client.event.AccountingReadersEvent;
import org.gcube.portlets.user.workspace.client.event.AccountingReadersEventHandler;
import org.gcube.portlets.user.workspace.client.event.ActiveGroupingView;
import org.gcube.portlets.user.workspace.client.event.ActiveGroupingViewHandler;
import org.gcube.portlets.user.workspace.client.event.AddAdministratorEvent;
import org.gcube.portlets.user.workspace.client.event.AddAdministratorEventHandler;
import org.gcube.portlets.user.workspace.client.event.AddFolderEvent;
import org.gcube.portlets.user.workspace.client.event.AddFolderEventHandler;
import org.gcube.portlets.user.workspace.client.event.AddSmartFolderEvent;
import org.gcube.portlets.user.workspace.client.event.CheckItemLockedBySyncEvent;
import org.gcube.portlets.user.workspace.client.event.CompletedFileUploadEvent;
import org.gcube.portlets.user.workspace.client.event.DeleteItemEvent;
import org.gcube.portlets.user.workspace.client.event.DeleteItemEventHandler;
import org.gcube.portlets.user.workspace.client.event.DeleteMessageEvent;
import org.gcube.portlets.user.workspace.client.event.DeleteMessageEventHandler;
import org.gcube.portlets.user.workspace.client.event.DoubleClickElementSelectedEvent;
import org.gcube.portlets.user.workspace.client.event.DoubleClickElementSelectedEventHandler;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent.DownloadType;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEventHandler;
import org.gcube.portlets.user.workspace.client.event.FileUploadEvent;
import org.gcube.portlets.user.workspace.client.event.FileUploadEventHandler;
import org.gcube.portlets.user.workspace.client.event.FileVersioningEvent;
import org.gcube.portlets.user.workspace.client.event.FileVersioningEventHandler;
import org.gcube.portlets.user.workspace.client.event.FilterScopeEvent;
import org.gcube.portlets.user.workspace.client.event.GetFolderLinkEvent;
import org.gcube.portlets.user.workspace.client.event.GetInfoEvent;
import org.gcube.portlets.user.workspace.client.event.GetInfoEventHandler;
import org.gcube.portlets.user.workspace.client.event.GetPublicLinkEvent;
import org.gcube.portlets.user.workspace.client.event.GetPublicLinkEventHandler;
import org.gcube.portlets.user.workspace.client.event.GetShareLinkEvent;
import org.gcube.portlets.user.workspace.client.event.GetSharedLinkEventHandler;
import org.gcube.portlets.user.workspace.client.event.GridElementSelectedEvent;
import org.gcube.portlets.user.workspace.client.event.GridElementSelectedEventHandler;
import org.gcube.portlets.user.workspace.client.event.GridElementUnSelectedEvent;
import org.gcube.portlets.user.workspace.client.event.GridElementUnSelectedEventHandler;
import org.gcube.portlets.user.workspace.client.event.GridRefreshEvent;
import org.gcube.portlets.user.workspace.client.event.GridRefreshEventHandler;
import org.gcube.portlets.user.workspace.client.event.ImagePreviewEvent;
import org.gcube.portlets.user.workspace.client.event.ImagePreviewEventHandler;
import org.gcube.portlets.user.workspace.client.event.LoadAllScopeEvent;
import org.gcube.portlets.user.workspace.client.event.LoadAllScopeEventHandler;
import org.gcube.portlets.user.workspace.client.event.LoadBreadcrumbEvent;
import org.gcube.portlets.user.workspace.client.event.LoadBreadcrumbEventHandler;
import org.gcube.portlets.user.workspace.client.event.OpenContextMenuTreeEvent;
import org.gcube.portlets.user.workspace.client.event.OpenContextMenuTreeEventHandler;
import org.gcube.portlets.user.workspace.client.event.OpenReportsEvent;
import org.gcube.portlets.user.workspace.client.event.OpenReportsEventHandler;
import org.gcube.portlets.user.workspace.client.event.OpenUrlEvent;
import org.gcube.portlets.user.workspace.client.event.PasteItemEvent;
import org.gcube.portlets.user.workspace.client.event.PasteItemEventHandler;
import org.gcube.portlets.user.workspace.client.event.PathElementSelectedEvent;
import org.gcube.portlets.user.workspace.client.event.PathElementSelectedEventHandler;
import org.gcube.portlets.user.workspace.client.event.RefreshFolderEvent;
import org.gcube.portlets.user.workspace.client.event.RenameItemEvent;
import org.gcube.portlets.user.workspace.client.event.RenameItemEventHandler;
import org.gcube.portlets.user.workspace.client.event.SaveSmartFolderEvent;
import org.gcube.portlets.user.workspace.client.event.SaveSmartFolderEventHandler;
import org.gcube.portlets.user.workspace.client.event.ScopeChangeEvent;
import org.gcube.portlets.user.workspace.client.event.ScopeChangeEventHandler;
import org.gcube.portlets.user.workspace.client.event.SearchItemByIdEvent;
import org.gcube.portlets.user.workspace.client.event.SearchItemByIdEventHandler;
import org.gcube.portlets.user.workspace.client.event.SearchTextEvent;
import org.gcube.portlets.user.workspace.client.event.SearchTextEventHandler;
import org.gcube.portlets.user.workspace.client.event.SendMessageEvent;
import org.gcube.portlets.user.workspace.client.event.SendMessageEventHandler;
import org.gcube.portlets.user.workspace.client.event.ShowUrlEvent;
import org.gcube.portlets.user.workspace.client.event.ShowUrlEventHandler;
import org.gcube.portlets.user.workspace.client.event.StoreGridChangedEvent;
import org.gcube.portlets.user.workspace.client.event.StoreGridChangedEventHandler;
import org.gcube.portlets.user.workspace.client.event.TrashEvent;
import org.gcube.portlets.user.workspace.client.event.TrashEventHandler;
import org.gcube.portlets.user.workspace.client.event.UpdateWorkspaceSizeEvent;
import org.gcube.portlets.user.workspace.client.event.UpdateWorkspaceSizeEventHandler;
import org.gcube.portlets.user.workspace.client.event.VREChangePermissionsEvent;
import org.gcube.portlets.user.workspace.client.event.VREChangePermissionsEventHandler;
import org.gcube.portlets.user.workspace.client.event.VRESettingPermissionEvent;
import org.gcube.portlets.user.workspace.client.event.WebDavUrlEvent;
import org.gcube.portlets.user.workspace.client.event.WebDavUrlEventHandler;
import org.gcube.portlets.user.workspace.client.event.WsGetFolderLinkEvent;
import org.gcube.portlets.user.workspace.client.event.WsGetFolderLinkEventHandler;
import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategorySmartFolder;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.model.ScopeModel;
import org.gcube.portlets.user.workspace.client.view.WorkspaceFeaturesView;
import org.gcube.portlets.user.workspace.client.view.WorkspaceQuotesView;
import org.gcube.portlets.user.workspace.client.view.WorskpacePortlet;
import org.gcube.portlets.user.workspace.client.view.panels.GxtBorderLayoutPanel;
import org.gcube.portlets.user.workspace.client.view.panels.GxtItemsPanel;
import org.gcube.portlets.user.workspace.client.view.toolbars.GxtBreadcrumbPathPanel;
import org.gcube.portlets.user.workspace.client.view.trash.WindowTrash;
import org.gcube.portlets.user.workspace.client.view.versioning.WindowVersioning;
import org.gcube.portlets.user.workspace.client.view.windows.DialogConfirm;
import org.gcube.portlets.user.workspace.client.view.windows.DialogGetInfo;
import org.gcube.portlets.user.workspace.client.view.windows.InfoDisplay;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
import org.gcube.portlets.user.workspace.shared.ExtendedWorkspaceACL;
import org.gcube.portlets.user.workspace.shared.GarbageItem;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.portlets.user.workspace.shared.TrashContent;
import org.gcube.portlets.user.workspace.shared.TrashOperationContent;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;
import org.gcube.portlets.user.workspace.shared.WorkspaceUserQuote;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload;
import org.gcube.portlets.widgets.wsmail.client.forms.MailForm;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;



/**
 * The Class AppController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 13, 2016
 */
public class AppController implements SubscriberInterface {
	private final static HandlerManager eventBus = new HandlerManager(null);
	private WorskpacePortlet wsPortlet = null;
	private AppControllerExplorer appContrExplorer = null;
	private String rootIdentifier = null;
	private HasWidgets rootPanel;
	private String selectedSmartFolderId;
	private GXTCategorySmartFolder selectedSmartFolderCategory;
	private WorkspaceFeaturesView workspaceFeatures = new WorkspaceFeaturesView();
	private WorkspaceQuotesView wsQuotesView = new WorkspaceQuotesView();
	public static final Logger logger = Logger.getLogger("WsAppController");

	/**
	 * Instantiates a new app controller.
	 *
	 * @param appControllerExplorer the app controller explorer
	 */
	public AppController(AppControllerExplorer appControllerExplorer) {
		this.appContrExplorer = appControllerExplorer;
		this.appContrExplorer.subscribe(this, new EventsTypeEnum[] {
				EventsTypeEnum.RENAME_ITEM_EVENT,
				EventsTypeEnum.SELECTED_ITEM_EVENT,
				//				EventsTypeEnum.EXPAND_FOLDER_EVENT,
				EventsTypeEnum.ADDED_FOLDER_EVENT,
				EventsTypeEnum.DELETE_ITEM_EVENT,
				EventsTypeEnum.SUBTREE_LOAD_EVENT,
				EventsTypeEnum.SMART_FOLDER_EVENT,
				EventsTypeEnum.COMPLETED_FILE_UPLOAD_EVENT,
				EventsTypeEnum.ADDED_FILE_EVENT,
				EventsTypeEnum.MOVED_ITEM_EVENT,
				EventsTypeEnum.LOAD_MESSAGES_EVENT,
				EventsTypeEnum.SWITCH_VIEW_EVENT,
				EventsTypeEnum.DELETED_MESSAGE,
				EventsTypeEnum.MARK_MESSAGE_AS_READ,
				EventsTypeEnum.REFRESH_FOLDER,
				EventsTypeEnum.SELECTED_MESSAGE,
				EventsTypeEnum.CREATE_NEW_MESSAGE,
				EventsTypeEnum.REPLY_FORWARD_MESSAGE,
				EventsTypeEnum.FILE_DOWNLAD_EVENT,
				EventsTypeEnum.SESSION_EXPIRED,
				EventsTypeEnum.PASTED_EVENT,
				EventsTypeEnum.COPY_EVENT,
				EventsTypeEnum.TRASH_EVENT,
				EventsTypeEnum.UPDATE_WORKSPACE_SIZE,
				EventsTypeEnum.UPDATED_VRE_PERMISSION,
				EventsTypeEnum.FILE_VERSIONING_HISTORY_EVENT
		});
		bind();
	}


	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public static HandlerManager getEventBus() {
		return eventBus;
	}

	/**
	 * Bind.
	 */
	private void bind() {
		//double click on URLs
		eventBus.addHandler(ShowUrlEvent.TYPE, new ShowUrlEventHandler() {
			@Override
			public void onClickUrl(ShowUrlEvent openUrlEvent) {
				if(openUrlEvent.getSourceFileModel()!=null)

					AppControllerExplorer.getInstance().doClickUrl(new OpenUrlEvent(openUrlEvent.getSourceFileModel()));
			}
		});

		eventBus.addHandler(WsGetFolderLinkEvent.TYPE, new WsGetFolderLinkEventHandler() {

			@Override
			public void onGetFolderLink(WsGetFolderLinkEvent getFolderLinkEvent) {

				AppControllerExplorer.getEventBus().fireEvent(new GetFolderLinkEvent(getFolderLinkEvent.getSourceFile(), true));
			}
		});

		eventBus.addHandler(FileVersioningEvent.TYPE, new FileVersioningEventHandler() {

			@Override
			public void onFileVersioning(FileVersioningEvent fileVersioningEvent) {
				ConstantsExplorer.log("quiqui");
				performVersioningOperation(fileVersioningEvent);
			}
		});


		eventBus.addHandler(UpdateWorkspaceSizeEvent.TYPE, new UpdateWorkspaceSizeEventHandler() {

			@Override
			public void onUpdateWorkspaceSize(UpdateWorkspaceSizeEvent updateWorkspaceSizeEvent) {
				updateWorksapaceSize(true);
			}
		});

		eventBus.addHandler(StoreGridChangedEvent.TYPE, new StoreGridChangedEventHandler() {

			@Override
			public void onStoreChanged(StoreGridChangedEvent storeGridChangedEvent) {

				wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemDetails().updateItemsNumber(storeGridChangedEvent.getSize());
			}
		});

		eventBus.addHandler(AddAdministratorEvent.TYPE, new AddAdministratorEventHandler() {

			@Override
			public void onAddAdministrator(AddAdministratorEvent addAdministratorEvent) {
				FileModel parent = wsPortlet.getToolBarPath().getLastParent();

				if(parent!=null)
					AppControllerExplorer.getEventBus().fireEvent(new AddAdministratorEvent(parent));

			}
		});

		eventBus.addHandler(TrashEvent.TYPE, new TrashEventHandler() {

			@Override
			public void onTrashEvent(TrashEvent trashEvent) {

				performTrashOperation(trashEvent.getTrashOperation(), trashEvent.getTargetFileModels());
			}
		});

		//********EVENTS TO NOTIFY SUBSCRIBERS
		eventBus.addHandler(ActiveGroupingView.TYPE, new ActiveGroupingViewHandler() {

			@Override
			public void onActiveGroupingView(ActiveGroupingView activeGroupingView) {

				if(activeGroupingView.isActiveGrouping()){
					setCookieGridViewSetting("true");
					wsPortlet.getGridGroupContainer().enableGrouping();
				}
				else{
					setCookieGridViewSetting("false");
					wsPortlet.getGridGroupContainer().disableGrouping();
				}
			}
		});

		//PASTE EVENT
		eventBus.addHandler(PasteItemEvent.TYPE, new PasteItemEventHandler() {


			@Override
			public void onCutCopyAndPaste(PasteItemEvent pasteItemEvent) {

				String folderDestinationId = null;
				if(pasteItemEvent.getFolderDestinationId()!=null){ //IF FOLDER DESTINATION EXISTS
					folderDestinationId = pasteItemEvent.getFolderDestinationId();
				}else{
					FileModel file = wsPortlet.getToolBarPath().getLastParent();

					if(file.getIdentifier()!=null){ //GET LAST PARENT FROM BREADRUMB
						folderDestinationId = file.getIdentifier();
					}

				}

				wsPortlet.getGridGroupContainer().mask(ConstantsExplorer.VALIDATINGOPERATION,ConstantsExplorer.LOADINGSTYLE);

				if(folderDestinationId!=null){
					//					Info.display("Info", "pasting...");
					AppControllerExplorer.getEventBus().fireEvent(new PasteItemEvent(pasteItemEvent.getIds(), folderDestinationId, pasteItemEvent.getOperationType()));
				}
			}
		});

		//********EVENTS TO NOTIFY SUBSCRIBERS
		eventBus.addHandler(OpenReportsEvent.TYPE, new OpenReportsEventHandler() {

			@Override
			public void onClickOpenReports(OpenReportsEvent openReportsEvent) {

				AppControllerExplorer.getEventBus().fireEvent(new OpenReportsEvent(openReportsEvent.getSourceFileModel()));

			}
		});

		eventBus.addHandler(GetPublicLinkEvent.TYPE, new GetPublicLinkEventHandler() {

			@Override
			public void onGetPublicLink(GetPublicLinkEvent getPublicLinkEvent) {
				// TODO Auto-generated method stub
				if(getPublicLinkEvent.getSourceFile()!=null){
					AppControllerExplorer.getEventBus().fireEvent(new GetPublicLinkEvent(getPublicLinkEvent.getSourceFile()));
				}
			}
		});



		eventBus.addHandler(AccountingHistoryEvent.TYPE, new AccountingHistoryEventHandler() {

			@Override
			public void onAccountingHistoryShow(AccountingHistoryEvent accountingHistoryEvent) {

				FileGridModel fileItem = getGridSelectedItem();

				if(fileItem!=null){
					AppControllerExplorer.getEventBus().fireEvent(new AccountingHistoryEvent(fileItem));
				}
			}
		});

		eventBus.addHandler(AccountingReadersEvent.TYPE, new AccountingReadersEventHandler() {

			@Override
			public void onAccountingReadersShow(AccountingReadersEvent accountingReadersEvent) {


				FileGridModel fileItem = getGridSelectedItem();

				if(fileItem!=null){
					AppControllerExplorer.getEventBus().fireEvent(new AccountingReadersEvent(fileItem));
				}
			}
		});

		eventBus.addHandler(GetInfoEvent.TYPE, new GetInfoEventHandler() {

			@Override
			public void onGetInfo(GetInfoEvent getInfoEvent) {

				FileModel file = getInfoEvent.getSourceFile();
				if(file==null)
					file = getGridSelectedItem();

				if(file!=null)
					new DialogGetInfo(file);
			}
		});

		eventBus.addHandler(GetShareLinkEvent.TYPE, new GetSharedLinkEventHandler() {

			@Override
			public void onGetLink(GetShareLinkEvent getLinkEvent) {

				FileModel getLinkFile = getLinkEvent.getSourceFile()!=null?getLinkEvent.getSourceFile():wsPortlet.getToolBarPath().getLastParent();

				AppControllerExplorer.getEventBus().fireEvent(new GetShareLinkEvent(getLinkFile));
			}
		});

		eventBus.addHandler(GridRefreshEvent.TYPE, new GridRefreshEventHandler() {

			@Override
			public void onGridRefresh(GridRefreshEvent gridRefreshEvent) {

				if(wsPortlet.getToolBarPath().getLastParent()!=null){

					//ID DISPLAYED SMART FOLDER CONTENTS?
					if(selectedSmartFolderId!=null || selectedSmartFolderCategory!=null){

						smartFolderSelected(selectedSmartFolderId, selectedSmartFolderCategory);
						return;
					}

					FileModel parent =  wsPortlet.getToolBarPath().getLastParent();
					//CREATE FOLDER PARENT FOR RPC
					//					FolderModel folder = new FolderModel(parent.getIdentifier(), parent.getName(), parent.getParentFileModel(), true, parent.isShared());
					updateStoreByRpc(parent);
				}
			}
		});

		eventBus.addHandler(WebDavUrlEvent.TYPE, new WebDavUrlEventHandler() {

			@Override
			public void onClickWebDavUrl(WebDavUrlEvent webDavUrlEvent) {
				AppControllerExplorer.getEventBus().fireEvent(new WebDavUrlEvent(webDavUrlEvent.getItemIdentifier()));

			}

		});

		eventBus.addHandler(OpenContextMenuTreeEvent.TYPE, new OpenContextMenuTreeEventHandler() {

			@Override
			public void onOpenContextMenuTree(OpenContextMenuTreeEvent openContextMenuTreeEvent) {
				AppControllerExplorer.getEventBus().fireEvent(openContextMenuTreeEvent);


			}
		});

		eventBus.addHandler(ImagePreviewEvent.TYPE, new ImagePreviewEventHandler() {

			@Override
			public void onClickPreview(ImagePreviewEvent imgPrevEvnt) {
				AppControllerExplorer.getEventBus().fireEvent(new ImagePreviewEvent(imgPrevEvnt.getSourceFileModel(), imgPrevEvnt.getClientX(), imgPrevEvnt.getClientY()));

			}

		});

		eventBus.addHandler(FileDownloadEvent.TYPE, new FileDownloadEventHandler() {

			@Override
			public void onFileDownloadEvent(FileDownloadEvent fileDownloadEvent) {

				if(fileDownloadEvent!=null && fileDownloadEvent.getItemIdentifier()!=null){
					AppControllerExplorer.getEventBus().fireEvent(new FileDownloadEvent(fileDownloadEvent.getItemIdentifier(), fileDownloadEvent.getItemName(), fileDownloadEvent.getDownloadType(), fileDownloadEvent.isFolder(), null));
				}

			}
		});

		eventBus.addHandler(DeleteMessageEvent.TYPE, new DeleteMessageEventHandler() {

			@Override
			public void onDeleteMessage(DeleteMessageEvent deleteMessageEvent) {
				AppControllerExplorer.getEventBus().fireEvent(new DeleteMessageEvent(deleteMessageEvent.getMessageTarget()));
			}

		});

		eventBus.addHandler(GridElementSelectedEvent.TYPE, new GridElementSelectedEventHandler() {

			@Override
			public void onGridElementSelected(GridElementSelectedEvent event) {

				if(!event.isMultiSelection()){ //IS NOT MULTISELECTION
					doElementGridSelected(true, event.getSourceFile());

				}else{ //IS MULTISELECTION

					wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().activeButtonForMultiSelection(true);
					wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemDetails().enableInfoHistoryButtons(event.getSourceFile(), false);
				}
			}

		});

		eventBus.addHandler(LoadAllScopeEvent.TYPE, new LoadAllScopeEventHandler() {

			@Override
			public void onLoadScopes(LoadAllScopeEvent loadAllScopeEvent) {
				doLoadAllScope();

			}

			private void doLoadAllScope() {

				appContrExplorer.getRpcWorkspaceService().getAllScope(new AsyncCallback<List<ScopeModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting all scope." +ConstantsExplorer.TRY_AGAIN, null);
					}

					@Override
					public void onSuccess(List<ScopeModel> result) {

						if(result!=null && result.size()>0){
							wsPortlet.getSearchAndFilterContainer().setListScope(result);
							wsPortlet.getSearchAndFilterContainer().selectScopeByIndex(0); //select first scope
						}
						else
							new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting all scope. No scope available. " +ConstantsExplorer.TRY_AGAIN, null);

					}
				});
			}
		});

		eventBus.addHandler(ScopeChangeEvent.TYPE, new ScopeChangeEventHandler() {

			@Override
			public void onLoadScope(ScopeChangeEvent scopeChangeEvent) {
				doLoadScope(scopeChangeEvent.getScopeId());

			}

			private void doLoadScope(String scopeId) {
				AppControllerExplorer.getEventBus().fireEvent(new FilterScopeEvent(scopeId));

			}
		});

		eventBus.addHandler(SaveSmartFolderEvent.TYPE, new SaveSmartFolderEventHandler() {

			@Override
			public void onSaveSmartFolder(SaveSmartFolderEvent saveSmartFolderEvent) {
				doSaveSmartFolder(saveSmartFolderEvent);

			}

			private void doSaveSmartFolder(SaveSmartFolderEvent event) {

				AppControllerExplorer.getEventBus().fireEvent(new AddSmartFolderEvent(event.getSearchText(), event.getWorkpaceFolderId()));

			}
		});

		eventBus.addHandler(DoubleClickElementSelectedEvent.TYPE, new DoubleClickElementSelectedEventHandler() {

			@Override
			public void onDoubleClickElementGrid(DoubleClickElementSelectedEvent doubleClickElementSelectedEvent) {

				doElementDoubleClick(doubleClickElementSelectedEvent.getSourceFile());

			}

			private void doElementDoubleClick(final FileGridModel fileModel) {
				if(fileModel.getIdentifier()!=null){

					if(wsPortlet.getSearchAndFilterContainer().isSearchActive()){
						AppController.getEventBus().fireEvent(new SearchTextEvent(null, null));
						wsPortlet.getSearchAndFilterContainer().setSearchActive(false);
						resetSmartFolderSelected();
					}

					GWT.log("FILE MODEL DOUBLE CLICK: "+fileModel);
					FolderModel folder = new FolderModel(fileModel.getIdentifier(), fileModel.getName(), fileModel.getParentFileModel(), true, fileModel.isShared(),fileModel.isVreFolder(), fileModel.isPublic());
					updateStoreByRpc(folder);
					wsPortlet.getGridGroupContainer().setCurrentFolderView(folder);
					loadBreadcrumbByFileModel(fileModel, true);
				}
				else
					GWT.log("an error occurred in double click on grid, item select is null");

			}
		});

		eventBus.addHandler(SearchTextEvent.TYPE, new SearchTextEventHandler() {

			@Override
			public void onSearchText(SearchTextEvent searchTextEvent) {

				if(searchTextEvent.getTextSearch()==null){
					appContrExplorer.searching(false);
					wsPortlet.getGridGroupContainer().setBorderAsOnSearch(false);
					wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().activeButtonsOnSearch(false); //ADDED 09-08-13
				}
				else{
					wsPortlet.getSearchAndFilterContainer().setSearchActive(true); //ADDED 06-04-12
					//					setSearchActive(true);
					appContrExplorer.searching(true);
					wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().activeButtonsOnSearch(true); //ADDED 09-08-13
					doSearchText(searchTextEvent.getTextSearch(), searchTextEvent.getFolderId());
				}
			}

			private void doSearchText(String textSearch, String folderId) {

				if(wsPortlet.getGxtCardLayoutResultPanel().getActivePanel() instanceof GxtItemsPanel){ //If active panel is panel with file items

					wsPortlet.getSearchAndFilterContainer().setVisibleButtonSave(true); //ADDED 24/04/2012

					wsPortlet.getGridGroupContainer().setBorderAsOnSearch(true);

					wsPortlet.getGridGroupContainer().mask(ConstantsExplorer.LOADING,ConstantsExplorer.LOADINGSTYLE);

					appContrExplorer.getRpcWorkspaceService().getItemsBySearchName(textSearch, folderId, new AsyncCallback<List<FileGridModel>>() {

						@Override
						public void onFailure(Throwable caught) {
							//							System.out.println("Failure search RPC");
							new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " searching item. " +ConstantsExplorer.TRY_AGAIN, null);
							wsPortlet.getGridGroupContainer().unmask();

						}

						@Override
						public void onSuccess(List<FileGridModel> result) {
							wsPortlet.getGridGroupContainer().updateStore(result);
							wsPortlet.getGridGroupContainer().unmask();
						}
					});
				}
			}
		});


		eventBus.addHandler(SearchItemByIdEvent.TYPE, new SearchItemByIdEventHandler() {

			@Override
			public void onSearchItemById(SearchItemByIdEvent searchItemByIdEvent) {

				if(searchItemByIdEvent.getItemId()!=null && !searchItemByIdEvent.getItemId().isEmpty()){
					wsPortlet.getSearchAndFilterContainer().setSearchActive(true); //ADDED 06-04-12
					appContrExplorer.searching(true);
					doSearchItemById(searchItemByIdEvent.getItemId(), searchItemByIdEvent.getOperationParameter());
				}else{
					appContrExplorer.searching(false);
					wsPortlet.getGridGroupContainer().setBorderAsOnSearch(false);
				}
			}

			private void doSearchItemById(final String itemId, WsPortletInitOperation wsPortletInitOperation) {

				wsPortlet.getGridGroupContainer().setBorderAsOnSearch(true);
				wsPortlet.getGridGroupContainer().mask(ConstantsExplorer.LOADING,ConstantsExplorer.LOADINGSTYLE);

				switch (wsPortletInitOperation) {

				case gotofolder:

					appContrExplorer.getRpcWorkspaceService().getFolderChildrenForFileGridById(itemId, new AsyncCallback<List<FileGridModel>>() {

						@Override
						public void onFailure(Throwable caught) {

							if(caught instanceof SessionExpiredException){
								GWT.log("Session expired");
								viewSessionExpiredPanel();
								return;
							}

							new MessageBoxAlert("Error", caught.getMessage(), null);
							wsPortlet.getGridGroupContainer().unmask();

						}

						@Override
						public void onSuccess(List<FileGridModel> result) {

							wsPortlet.getGridGroupContainer().updateStore(result);
							wsPortlet.getGridGroupContainer().unmask();

							if(result!=null && result.size()>0){
								String childId = result.get(0).getIdentifier(); //GET FIRST CHILD ID TO LOAD PARENTS BREADCRUMB
								GWT.log("reloading breadcrumb for first child...");
								loadParentBreadcrumbByItemId(childId, false);
							}else{ //FOLDER IS EMPTY AND EXISTS, FOLDER IS LAST PARENT INTO BREADCRUMB
								GWT.log("reloading breadcrumb by item including as last parent");
								loadParentBreadcrumbByItemId(itemId, true); //RELOAD BREDCRUMB
							}

							boolean selected =wsPortlet.getGridGroupContainer().selectItemByFileModelId(itemId);

							if(selected)
								new InfoDisplay("Info", "The searched element was selected");
						}
					});


					break;

				case sharelink:

					appContrExplorer.getRpcWorkspaceService().getItemForFileGrid(itemId, new AsyncCallback<FileGridModel>() {

						@Override
						public void onFailure(Throwable caught) {
							new MessageBoxAlert("Error", caught.getMessage(), null);
							wsPortlet.getGridGroupContainer().unmask();

						}

						@Override
						public void onSuccess(FileGridModel result) {

							List<FileGridModel> listResult = new ArrayList<FileGridModel>();
							listResult.add(result);
							wsPortlet.getGridGroupContainer().updateStore(listResult);
							wsPortlet.getGridGroupContainer().unmask();

							if(result!=null)
								loadBreadcrumbByFileModel(result, false); //ADDED 13-06-2013
						}
					});


					break;
				}



			}
		});

		eventBus.addHandler(PathElementSelectedEvent.TYPE, new PathElementSelectedEventHandler() {

			@Override
			public void onPathElementSelected(PathElementSelectedEvent event) {
				wsPortlet.getSearchAndFilterContainer().searchCancel(); //cancel search

				if(wsPortlet.getGxtCardLayoutResultPanel().getActivePanel() instanceof GxtItemsPanel){ //If active panel is panel with file items
					AppController.getEventBus().fireEvent(new SearchTextEvent(null, null));
				}
				doPathElementSelected(event);
			}

			private void doPathElementSelected(PathElementSelectedEvent event) {
				//				appContrExplorer.selectItemInTree(event.getSourceFile().getIdentifier());
				appContrExplorer.expandFolder(event.getSourceFile().getIdentifier());
			}

		});

		eventBus.addHandler(GridElementUnSelectedEvent.TYPE, new GridElementUnSelectedEventHandler() {

			@Override
			public void onGridElementUnSelected(GridElementUnSelectedEvent event) {
				doElementGridSelected(false, null);
			}
		});


		eventBus.addHandler(AddFolderEvent.TYPE, new AddFolderEventHandler() {

			@Override
			public void onAddItem(AddFolderEvent event) {

				FileModel folderToInsert = event.getFileSourceModel();
				GWT.log("Folder parent to create new folder not found, Loading from breadcrumb");
				if(folderToInsert==null){
					folderToInsert = wsPortlet.getToolBarPath().getLastParent();
					GWT.log("Folder parent in breadcrumb: "+folderToInsert.getName());
				}

				if(folderToInsert!=null)
					AppControllerExplorer.getEventBus().fireEvent(new AddFolderEvent(folderToInsert,event.getParentFileModel()));
			}

		});

		eventBus.addHandler(SendMessageEvent.TYPE, new SendMessageEventHandler() {

			@Override
			public void onSendMessage(SendMessageEvent sendMessageEvent) {
				doSendMessage(sendMessageEvent.getListFileModelSelected());

			}

			private void doSendMessage(List<FileModel> listFileModelSelected) {
				AppControllerExplorer.getEventBus().fireEvent(new SendMessageEvent(null));
			}
		});


		eventBus.addHandler(FileUploadEvent.TYPE, new FileUploadEventHandler() {

			@Override
			public void onFileUploadEvent(FileUploadEvent fileUploadEvent) {

				FileModel parent = null;

				try{
					if(!wsPortlet.getSearchAndFilterContainer().isSearchActive()){ //IF IS NOT SEARCH ACTIVE
						//FILE UPLOAD CASES..
						if(fileUploadEvent.getTargetFolderModel()!=null){
							parent = fileUploadEvent.getTargetFolderModel();
							GWT.log("Search is not active, get parent item for uploading from parent of file model: " +fileUploadEvent.getTargetFolderModel().getName());
						}else if(wsPortlet.getToolBarPath().getLastParent()!=null){
							parent = wsPortlet.getToolBarPath().getLastParent();
							GWT.log("Search is not active get parent item for uploading from breadcrumb: " +parent.getName());
						}else if(wsPortlet.getGridGroupContainer().getCurrentFolderView()!=null){
							parent = wsPortlet.getGridGroupContainer().getCurrentFolderView();
							GWT.log("Search is not active get parent item for uploading from CurrentFolderView: " +parent.getName());
						}
						//					}else if(wsPortlet.getGridGroupContainer().getCurrentFolderView()!=null){
						//						parent = wsPortlet.getGridGroupContainer().getCurrentFolderView();
						//						GWT.log("get parent item for uploading from CurrentFolderView: " +parent.getName());
						//					}else if(wsPortlet.getToolBarPath().getLastParent()!=null){
						//						parent = wsPortlet.getToolBarPath().getLastParent();
						//						GWT.log("get parent item for uploading from breadcrumb: " +parent.getName());
						//					}
					}
					else{ //IF IS SEARCH ACTIVE

						//is BREADCRUMB fully?
						if(wsPortlet.getToolBarPath().getLastParent()!=null){
							parent = wsPortlet.getToolBarPath().getLastParent();
							GWT.log("Search is active get parent item for uploading from breadcrumb: " +parent.getName());
						}else
							parent = null; //also SET null AS PARENT

					}

					AppControllerExplorer.getEventBus().fireEvent(new FileUploadEvent(parent,fileUploadEvent.getUploadType()));

				}catch (Exception e) {
					GWT.log("Error onFileUploadEvent", e);
				}

			}
		});

		eventBus.addHandler(DeleteItemEvent.TYPE, new DeleteItemEventHandler() {

			@Override
			public void onDeleteItem(DeleteItemEvent event) {
				doDeleteItem(event);
			}

			private void doDeleteItem(final DeleteItemEvent event){


				if(!event.isMultiSelection()){ //IS NOT MULTI

					AppControllerExplorer.getEventBus().fireEvent(new DeleteItemEvent(event.getFileTarget()));

				}else{ //IS MULTI


					if(event.getListTarget()!=null){

						FileModel parent = null;

						if(!wsPortlet.getSearchAndFilterContainer().isSearchActive()){ // IS NOT A SEARCH
							if(wsPortlet.getToolBarPath().getLastParent()!=null){
								parent = wsPortlet.getToolBarPath().getLastParent();
								GWT.log("Search is not active get parent item for uploading from breadcrumb: " +parent.getName());
							}else if(wsPortlet.getGridGroupContainer().getCurrentFolderView()!=null){
								parent = wsPortlet.getGridGroupContainer().getCurrentFolderView();
								GWT.log("Search is not active get parent item for uploading from CurrentFolderView: " +parent.getName());
							}
						}

						final FileModel target = parent;

						final int size = event.getListTarget().size();

						List<? extends FileModel> targets = event.getListTarget();

						final List<FileModel> clearTargets = new ArrayList<FileModel>(targets);

						//IGNORING SHARED FOLDER
						List<FileModel> ingnoreFile = new ArrayList<FileModel>(size);
						for (FileModel file : targets) {
							GWT.log("Checking file "+file);
							if(file.getType()!=null && file.getType().equals(GXTFolderItemTypeEnum.FOLDER_SHARED.toString()) || file.isSpecialFolder()){
								ingnoreFile.add(file);
								clearTargets.remove(file);
							}
						}

						final int diff = size-ingnoreFile.size();
						if(diff==0){
							MessageBox.alert("Alert", "Shared folders cannot be deleted through multiple selection", null);
							return;
						}

						String msg = "Are you sure you want to delete "+diff+" items?<br/>";

						if(ingnoreFile.size()>0){
							msg+="<br/>The shared "+(ingnoreFile.size()>1?"folders":"folder");
							msg+= " following will not be removed:";
							for (FileModel fileGridModel : ingnoreFile) {
								msg+="<br/>"+fileGridModel.getName();
							}
						}

						final DialogConfirm confirm = new DialogConfirm(null, ConstantsExplorer.MESSAGE_DELETE,msg);
						confirm.setModal(true);
						confirm.center();

						//CREATING LIST IDS
						final List<String> ids = new ArrayList<String>();
						for (FileModel file : clearTargets) {
							ids.add(file.getIdentifier());
						}

						confirm.getYesButton().addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent clickEvent) {
								confirm.loader("Deleting "+diff+ " items, please wait");
//								wsPortlet.getGridGroupContainer().mask("Deleting",ConstantsExplorer.LOADINGSTYLE);


								appContrExplorer.getRpcWorkspaceService().deleteListItemsForIds(ids, new AsyncCallback<List<GarbageItem>>() {

									@Override
									public void onFailure(Throwable arg0) {
										confirm.hide();
										new MessageBoxAlert("Error", arg0.getMessage(),null);
										wsPortlet.getGridGroupContainer().unmask();
										appContrExplorer.refreshRoot(true);
									}

									@Override
									public void onSuccess(List<GarbageItem> errors) {
										confirm.hide();

										List<String> idErros = new ArrayList<String>(errors.size());

										if(errors.size()>0){
											String msg = "Error during delete of following: ";
											for (GarbageItem gbi : errors) {
												msg+=gbi.getOldItemName()+"; ";
												idErros.add(gbi.getOldItemId());
											}

											MessageBox.alert("Error", msg, null);
											int diff = ids.size()-errors.size();

											if(diff>0){
												List<String> deletable = new ArrayList<String>(diff);
												//Deleting item from grid
												for (String id : ids) {
													if(!idErros.contains(id)) //is not an error so is deletable
														deletable.add(id);
												}
												deleteItems(deletable);
											}
										}else
											deleteItems(ids); //no error, delete all ids

										GWT.log("target: "+target);

										if(wsPortlet.getSearchAndFilterContainer().isSearchActive()){
											appContrExplorer.refreshRoot(false);
										}else
											AppControllerExplorer.getEventBus().fireEvent(new RefreshFolderEvent(target, true, false, true));
									}
								});

							}

						});
					}

				}

			}

		});

		eventBus.addHandler(RenameItemEvent.TYPE, new RenameItemEventHandler() {

			@Override
			public void onRenameItem(RenameItemEvent event) {
				doRenameItem(event);
			}

			public void doRenameItem(final RenameItemEvent event) {

				AppControllerExplorer.getEventBus().fireEvent(new RenameItemEvent(event.getFileTarget()));
			}
		});

		eventBus.addHandler(LoadBreadcrumbEvent.TYPE, new LoadBreadcrumbEventHandler() {

			@Override
			public void loadBreadcrumb(LoadBreadcrumbEvent loadBreadcrumbEvent) {

				if(loadBreadcrumbEvent.getFileModel()!=null)
					loadBreadcrumbByFileModel(loadBreadcrumbEvent.getFileModel(), true);
			}
		});

		eventBus.addHandler(VREChangePermissionsEvent.TYPE, new VREChangePermissionsEventHandler() {

			@Override
			public void onChangePermissionsOpen(VREChangePermissionsEvent vreChangePermissionsEvent) {
				if(vreChangePermissionsEvent.getFileModel()!=null)
					AppControllerExplorer.getEventBus().fireEvent(new VRESettingPermissionEvent(vreChangePermissionsEvent.getFileModel()));

			}
		});

	}
	/*
	private void accountingSetItemAsRead(boolean read) {
		wsPortlet.getToolBarItemDetails().setRead(read);
	}*/

	/**
	 * Do element grid selected.
	 *
	 * @param isSelected the is selected
	 * @param target the target
	 */
	private void doElementGridSelected(boolean isSelected, FileModel target) {

		wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().activeButtonsOnSelectForOperation(target, isSelected);
		wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemDetails().enableInfoHistoryButtons(target, isSelected);
		disableButtonsOnBreadcrumbSpecialFolder();

		if(isSelected){
			FileModel lastParent = this.wsPortlet.getToolBarPath().getLastParent();

			if(lastParent!=null && target.getParentFileModel()!=null){
				boolean parentChanged = lastParent.getIdentifier().compareTo(target.getParentFileModel().getIdentifier())==0?false:true;

				//RELOAD breadcrumb only if last parent id is changed
				if(parentChanged)
					loadBreadcrumbByFileModel(target,false);
			}
			else
				loadBreadcrumbByFileModel(target,false);
		}

	}

	/**
	 * Disable buttons on breadcrumb special folder.
	 */
	private void disableButtonsOnBreadcrumbSpecialFolder(){
		GxtBreadcrumbPathPanel breadCrumb = this.wsPortlet.getToolBarPath();
		FileModel parent = breadCrumb.getLastParent();
		if(parent!=null && parent.isSpecialFolder())
			wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().disableButtonSpecialFolderSelected();
	}

	/**
	 * Gets the grid selected item.
	 *
	 * @return the grid selected item
	 */
	private FileGridModel getGridSelectedItem(){
		return wsPortlet.getGridGroupContainer().getSelectedItem();
	}

	/**
	 * Reset smart folder selected.
	 */
	private void resetSmartFolderSelected(){
		selectedSmartFolderId = null;
		selectedSmartFolderCategory = null;
	}


	/**
	 * Load breadcrumb by file model.
	 *
	 * @param item the item
	 * @param isLastParent - if is true, load the item passed in input as last item of the list resulted
	 */
	private void loadBreadcrumbByFileModel(final FileModel item, final boolean isLastParent){

		GWT.log("Reload Breadcrumb: [FileModel name: "+item.getName()+ ", isLastParent: "+isLastParent+"]");

		AppControllerExplorer.rpcWorkspaceService.getListParentsByItemIdentifier(item.getIdentifier(), false, new AsyncCallback<List<FileModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("failure get list parents by item identifier "+caught);

			}

			@Override
			public void onSuccess(List<FileModel> result) {

				if(isLastParent && item.isDirectory() && !item.isRoot()){
					result.add(item);
				}
				updateBreadcrumb(result); //Set file path in tab panel on current item selected
			}
		});

	}

	/**
	 * Load parent breadcrumb by item id.
	 *
	 * @param childItemId the child item id
	 * @param includeItemAsParent the include item as parent
	 */
	protected void loadParentBreadcrumbByItemId(final String childItemId, boolean includeItemAsParent){
		GWT.log("Reload Parent Breadcrumb: [Item id: "+childItemId+"]");
		AppControllerExplorer.rpcWorkspaceService.getListParentsByItemIdentifier(childItemId, includeItemAsParent, new AsyncCallback<List<FileModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("failure get list parents by item identifier "+caught);
			}

			@Override
			public void onSuccess(List<FileModel> result) {
				updateBreadcrumb(result); //Set file path in tab panel on current item selected
			}
		});
	}

	/**
	 * Update breadcrumb.
	 *
	 * @param parents the parents
	 */
	private void updateBreadcrumb(List<FileModel> parents){
		GWT.log("Updating Breadcrumb : "+parents);
		//this.wsPortlet.getBasicTabContainer().setLabelPath(path); //Set path in breadcrumb

		GxtBreadcrumbPathPanel breadCrumb = this.wsPortlet.getToolBarPath();
		breadCrumb.setPath(parents); //Set path in breadcrumb
		FileModel parent = breadCrumb.getLastParent();

		GWT.log("Updated Breadcrumb for : "+parent);

		if(parent!=null){
			wsPortlet.getBorderLayoutContainer().updateDnDParentId(parent.getIdentifier());

			if(parent.isDirectory() && parent.isShared()){ //IS SHARED FOLDER, ENABLING OPERATION FOR ACTIVE BREADCRUMB
				setACLInfo(parent.getIdentifier());
				wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().enableButtonForActiveBreadcrumb(parent);
			}else if(parent.isSpecialFolder()){ //IS SPECIAL FOLDER? DISABLING BUTTONS
				GWT.log("Update Breadcrumb is special folder.. disabling special folder buttons");
				wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().disableButtonSpecialFolderSelected();
				setACLInfo(null);
			}else{  //ENABLING OPERATION FOR ACTIVE BREADCRUMB
				wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().enableButtonForActiveBreadcrumb(parent);
				setACLInfo(null); //IS NOT A SHARE FOLDER  DOSN'T DISPLAY ACL INFO
			}
		}else{
			wsPortlet.getBorderLayoutContainer().updateDnDParentId(null);
			setACLInfo(null);
		}

	}


	/**
	 * Sets the ACL info.
	 *
	 * @param parentId the new ACL info
	 */
	private void setACLInfo(final String parentId){

		if(parentId==null){
			wsPortlet.getToolBarItemDetails().updateACLInfo(null);
			return;
		}

		GWT.log("Updating ACL info for folder id: "+parentId);
		AppControllerExplorer.rpcWorkspaceService.getUserACLForFolderId(parentId, new AsyncCallback<List<ExtendedWorkspaceACL>>() {

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(List<ExtendedWorkspaceACL> res) {
				FileModel parent = wsPortlet.getToolBarPath().getLastParent();
				ConstantsExplorer.log("Validating correct ACL id: "+parentId +" and "+parent.getIdentifier());
				if(parentId.compareTo(parent.getIdentifier())==0){
					if(res!=null && res.size()>0){
						wsPortlet.getToolBarItemDetails().updateACLInfo(res.get(0));
						wsPortlet.getToolBarItemDetails().updateAddAdministatorInfo(AppControllerExplorer.myLogin, res.get(0));
					}
				}

//				if(res!=null && res.size()>0){
//					wsPortlet.getToolBarItemDetails().updateACLInfo(res.get(0));
//					wsPortlet.getToolBarItemDetails().updateAddAdministatorInfo(AppControllerExplorer.myLogin, res.get(0));
//				}
			}
		});
	}

	/**
	 * init method.
	 *
	 * @param rootPanel the root panel
	 */
	public void go(final HasWidgets rootPanel) {

		this.rootPanel = rootPanel;

		final String searchParameter = Window.Location.getParameter(ConstantsPortlet.GET_SEARCH_PARAMETER);
		final String itemIdParameter = Window.Location.getParameter(ConstantsPortlet.GET_ITEMID_PARAMETER);
		final String operationParameter = Window.Location.getParameter(ConstantsPortlet.GET_OPERATION_PARAMETER);
		final String validateSession = Window.Location.getParameter(ConstantsPortlet.GET_VALIDATE_SESSION);

		boolean sessionValidating = true;

		if(validateSession!=null && validateSession.compareToIgnoreCase("false")==0)
			sessionValidating = false;

		GWT.log("GET PARAMETER "+ConstantsPortlet.GET_SEARCH_PARAMETER+": "+searchParameter);
		GWT.log("GET PARAMETER "+ConstantsPortlet.GET_ITEMID_PARAMETER+": "+itemIdParameter);
		GWT.log("GET PARAMETER "+ConstantsPortlet.GET_OPERATION_PARAMETER+": "+operationParameter);
		GWT.log("GET PARAMETER "+ConstantsPortlet.GET_VALIDATE_SESSION+": "+validateSession);

		final boolean instanceWithGrouping = readCookieWorkspaceGridViewSetting();

		System.out.println("Cookie "+ConstantsPortlet.GCUBE_COOKIE_WORKSPACE_GRID_VIEW_SETTING + " return "+instanceWithGrouping);
		System.out.println("Session Validating is "+sessionValidating);

		boolean isSearch = false;
		boolean isSearchForItemId = false;
		//		Log.trace("Start Workspace Portlet");
		if (this.appContrExplorer == null){

			this.wsPortlet = new WorskpacePortlet(instanceWithGrouping);
		}
		else{
			if(searchParameter!=null && !searchParameter.isEmpty())
				isSearch = true;

			if(itemIdParameter!=null && !itemIdParameter.isEmpty()){
				isSearch = true;
				isSearchForItemId = true;
			}

			this.wsPortlet = new WorskpacePortlet(this.appContrExplorer.getPanel(true, false, !isSearch), instanceWithGrouping);

			final MultipleDNDUpload dnd = this.wsPortlet.getDND();

			WorskpaceUploadNotificationListener listener = new WorskpaceUploadNotificationListener() {

				@Override
				public void onUploadCompleted(String parentId, String itemId) {
					GWT.log("Upload completed: [parentID: "+parentId+", itemId: "+itemId+", uploadType: "+dnd.getUploadType()+"]");
					AppControllerExplorer.getEventBus().fireEvent(new CompletedFileUploadEvent(parentId, itemId, WS_UPLOAD_TYPE.File, false));
				}

				@Override
				public void onUploadAborted(String parentId, String itemId) {
					GWT.log("Upload Aborted: [parentID: "+parentId+", itemId: "+itemId+"]");
				}

				@Override
				public void onError(String parentId, String itemId, Throwable throwable) {
					GWT.log("Upload Error: [parentID: "+parentId+", itemId: "+itemId+"]");
				}

				@Override
				public void onOverwriteCompleted(String parentId, String itemId) {
					GWT.log("Upload Override Completed: [parentID: "+parentId+", itemId: "+itemId+"]");
					AppControllerExplorer.getEventBus().fireEvent(new CompletedFileUploadEvent(parentId, itemId, WS_UPLOAD_TYPE.File, true));
				}
			};

			dnd.addWorkspaceUploadNotificationListener(listener);

			eventBus.fireEvent(new LoadAllScopeEvent()); //LOAD ALL SCOPE EVENT
		}

		final boolean searchingForItemId = isSearchForItemId;

		//VALIDATING SESSION
		if(appContrExplorer!=null && sessionValidating){
			appContrExplorer.getRpcWorkspaceService().isSessionExpired(new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("ERROR ON VALIDATING SESSION - DEFAULT INIT");
					initPortlet(rootPanel, instanceWithGrouping, searchingForItemId, searchParameter, itemIdParameter, operationParameter);
				}

				@Override
				public void onSuccess(Boolean result) {

					GWT.log("SESSION IS EXPIRED: "+result);

					//SESSION IS NOT EXPIRED
					if(!result)
						initPortlet(rootPanel, instanceWithGrouping, searchingForItemId, searchParameter, itemIdParameter, operationParameter);
					else{
						//SESSION IS EXPIRED
						viewSessionExpiredPanel();
					}

				}
			});
		}
		else
			initPortlet(rootPanel, instanceWithGrouping, searchingForItemId, searchParameter, itemIdParameter, operationParameter);

		//CheckSession if you do not need to something when the session expire
		//CheckSession.getInstance().startPolling();
	}

	/**
	 * Inits the portlet.
	 *
	 * @param rootPanel the root panel
	 * @param instanceWithGrouping the instance with grouping
	 * @param isSearchForItemId the is search for item id
	 * @param searchParameter the search parameter
	 * @param itemIdParameter the item id parameter
	 * @param operationParameter the operation parameter
	 */
	private void initPortlet(final HasWidgets rootPanel, final boolean instanceWithGrouping, boolean isSearchForItemId, final String searchParameter, final String itemIdParameter, final String operationParameter){

		boolean displayFeatures = readCookieWorkspaceAvailableFeatures();

		GWT.log("Display features? "+displayFeatures);

		if(displayFeatures)
			rootPanel.add(workspaceFeatures);

		rootPanel.add(wsPortlet.getBorderLayoutContainer());

		//SET TOGGLE BUTTON GRID VIEW
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().toggleGridViewButton(instanceWithGrouping);
			}
		});

		//IF IS SEARCH and IS NOT SEARCH FOR ITEM ID - fire event search text
		if(searchParameter!=null && !searchParameter.isEmpty() && !isSearchForItemId){
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					wsPortlet.getSearchAndFilterContainer().searchText(searchParameter);
					wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().activeButtonsOnSearch(false); //ADDED 09-08-13
				}
			});
		} else if(itemIdParameter!=null && !itemIdParameter.isEmpty()){ //SEARCH FOR ITEM ID

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					AppController.getEventBus().fireEvent(new SearchItemByIdEvent(itemIdParameter, operationParameter));
					wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().activeButtonsOnSearch(false); //ADDED 09-08-13
				}
			});

		}

		//LOADING TRASH CONTENT
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				performTrashOperation(WorkspaceTrashOperation.REFRESH, null);
			}
		});

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				updateWorksapaceSize(false);
			}
		});
		rootPanel.add(wsQuotesView);
	}

	/**
	 * Sets the workspace user quotes.
	 *
	 * @param size the size
	 * @param totalItems the total items
	 */
	private void setWorkspaceUserQuotes(String size, long totalItems){
		String msg;

		if(totalItems<=0){
			msg = "No items";
		}else if(totalItems==1){
			msg = totalItems + " item";
		}else {
			msg = totalItems + " items";
		}

		msg+= ", "+size;
		wsQuotesView.updateQuotes(msg);
	}


	/**
	 * Read cookie workspace grid view setting.
	 *
	 * @return true if exists a cookie with msg as true value (or not exists the cookie), false otherwise
	 */
	private boolean readCookieWorkspaceGridViewSetting() {

		//get the cookie with name GCBUEWorkspaceGridViewSetting
		String msg = Cookies.getCookie(ConstantsPortlet.GCUBE_COOKIE_WORKSPACE_GRID_VIEW_SETTING);
		//if null, there was no cookie
		if(msg == null){
			setCookieGridViewSetting("true");
			return true;
		}

		if(msg.compareTo("true")==0)
			return true;

		return false;

	}


	/**
	 * Read cookie workspace available features.
	 *
	 * @return true if exists a cookie with msg as true value (or not exists the cookie), false otherwise
	 */
	private boolean readCookieWorkspaceAvailableFeatures() {

		//get the cookie with name GCBUEWorkspaceGridViewSetting
		String msg = Cookies.getCookie(ConstantsPortlet.GCUBE_COOKIE_WORKSPACE_AVAILABLE_FEATURES);
		//if null, there was no cookie
		if(msg == null){
			setCookie(ConstantsPortlet.GCUBE_COOKIE_WORKSPACE_AVAILABLE_FEATURES, "true", ConstantsPortlet.COOKIE_EXPIRE_DAYS);
			return true;
		}

		if(msg.compareTo("true")==0)
			return true;

		return false;
	}

	/**
	 * Sets the cookie.
	 *
	 * @param name the name
	 * @param value the value
	 * @param days the days
	 */
	public static void setCookie(String name, String value, int days) {

		if (value == null) {
			Cookies.removeCookie(name);
			return;
		}

		// Now
		Date expiringDate = new Date();
		// Now + days
		expiringDate.setTime(expiringDate.getTime() + ConstantsPortlet.MILLISECS_PER_DAY * days);
		Cookies.setCookie(name, value, expiringDate);
	}

	/**
	 * Sets the cookie grid view setting.
	 *
	 * @param value the new cookie grid view setting
	 */
	protected static void setCookieGridViewSetting(String value) {

		setCookie(ConstantsPortlet.GCUBE_COOKIE_WORKSPACE_GRID_VIEW_SETTING, value, ConstantsPortlet.COOKIE_EXPIRE_DAYS);
	}

	/**
	 * Gets the main panel.
	 *
	 * @return the main panel
	 */
	public GxtBorderLayoutPanel getMainPanel(){
		return wsPortlet.getBorderLayoutContainer();
	}

	/**
	 * Sets the size async tree panel.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void setSizeAsyncTreePanel(int width, int height) {
		wsPortlet.getExplorerPanel().getAsycTreePanel().setSizeTreePanel(width-17, height-55);
	}

	/**
	 * Update store by rpc.
	 *
	 * @param folder the folder
	 */
	private void updateStoreByRpc(final FileModel folder){

		resetSmartFolderSelected();

		if(folder==null)
			return;

		//CREATE FOLDER PARENT FOR RPC
		final FileModel parent = new FolderModel(folder.getIdentifier(), folder.getName(), folder.getParentFileModel(), true, folder.isShared(), folder.isVreFolder(), folder.isPublic());
		wsPortlet.getGridGroupContainer().mask(ConstantsExplorer.LOADING,ConstantsExplorer.LOADINGSTYLE);

		appContrExplorer.getRpcWorkspaceService().getFolderChildrenForFileGrid(parent, new AsyncCallback<List<FileGridModel>>() {

			@Override
			public void onFailure(Throwable caught) {

				if(caught instanceof SessionExpiredException){
					GWT.log("Session expired");
					viewSessionExpiredPanel();
					return;
				}

				new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting items for grid. " +ConstantsExplorer.TRY_AGAIN, null);
				wsPortlet.getGridGroupContainer().unmask();

			}

			@Override
			public void onSuccess(List<FileGridModel> result) {
				wsPortlet.getGridGroupContainer().setCurrentFolderView(parent); //SET folder as current view
				wsPortlet.getGridGroupContainer().unmask();
				wsPortlet.getGridGroupContainer().updateStore(result);

				AppControllerExplorer.getEventBus().fireEvent(new CheckItemLockedBySyncEvent(folder));
			}
		});
	}

	/**
	 * Gets the selected folder.
	 *
	 * @return the selected folder
	 */
	public FileModel getSelectedFolder(){
		return this.appContrExplorer.getSelectedFolderInTree();
	}


	/**
	 * Following methods implements SubscriberInterface.
	 *
	 * @param itemIdentifier the item identifier
	 * @param parent the parent
	 */

	@Override
	public void addedFolder(String itemIdentifier, FileModel parent) {
		updateStoreByRpc(parent);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#addedFile(java.lang.String, java.lang.String, org.gcube.portlets.user.workspace.client.ConstantsExplorer.WS_UPLOAD_TYPE, boolean)
	 */
	@Override
	public void addedFile(String itemIdentifier, String parentId, WS_UPLOAD_TYPE uploadType, boolean isOverwrite) {
//		updateStoreByRpc(parent);
		if(parentId==null){
			GWT.log("addedFile skipping, parentId is null");
			return;
		}

		//UPDATE GRID ONLY IF TREE AND GRID DISPLAYING THE SAME PARENT
		if(parentIsBreadcrumbLastParent(parentId)){

			if(uploadType.equals(WS_UPLOAD_TYPE.File)){
				GWT.log("Calling addFileToStoreById "+parentId);
				//IF IS OVERWRITE DELETE BEFORE THE ITEM FORM STORE
				if(itemIdentifier!=null)
					addFileToStoreById(itemIdentifier, parentId, isOverwrite);
				else{
					//IS AN ADD URL?

				}
			}else{ //is ARCHIVE
				updateStoreByRpc(wsPortlet.getToolBarPath().getLastParent());
			}
		}
	}


	/**
	 * Parent is breadcrumb last parent.
	 *
	 * @param parentId the parent id
	 * @return true, if successful
	 */
	private boolean parentIsBreadcrumbLastParent(String parentId){
		FileModel breadParent = wsPortlet.getToolBarPath().getLastParent();
		GWT.log("Comparing parentId: "+parentId +" and bread parent: "+breadParent.getIdentifier());
		return breadParent!=null && parentId!=null && breadParent.getIdentifier().compareTo(parentId)==0;
	}


	/**
	 * Adds the file to store by id.
	 *
	 * @param itemIdentifier the item identifier
	 * @param parentId the parent id
	 * @param deleteCurrentItem the delete current item from store
	 */
	private void addFileToStoreById(final String itemIdentifier, String parentId, final boolean deleteCurrentItem){

		appContrExplorer.getRpcWorkspaceService().getItemForFileGrid(itemIdentifier, new AsyncCallback<FileGridModel>() {

			@Override
			public void onFailure(Throwable caught) {
//				wsPortlet.getGridGroupContainer().unmask();
			}

			@Override
			public void onSuccess(FileGridModel result) {

				if(deleteCurrentItem)
					wsPortlet.getGridGroupContainer().deleteItem(itemIdentifier);

				GWT.log("Add to store: "+result);
				wsPortlet.getGridGroupContainer().addToStore(result);
//				wsPortlet.getGridGroupContainer().unmask();
			}
		});

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#selectedItem(org.gcube.portlets.user.workspace.client.model.FileModel, java.util.List)
	 */
	@Override
	public void selectedItem(FileModel item, List<FileModel> parents) {
		GWT.log("selectedItem.. ");
		FileModel currentFolder = wsPortlet.getGridGroupContainer().getCurrentFolderView();

		if(currentFolder!=null){

			if(item.isDirectory()) //ADDED 11-06-2013
				parents.add(item);

			updateBreadcrumb(parents); //set path

			//CASE IS A SEARCH
			if(wsPortlet.getSearchAndFilterContainer().isSearchActive()){ //ADDED 12-04-12
				wsPortlet.getSearchAndFilterContainer().searchCancel(); //TODO added in 05/04/2012
				AppController.getEventBus().fireEvent(new SearchTextEvent(null, null));
				wsPortlet.getSearchAndFilterContainer().setSearchActive(false);
				resetSmartFolderSelected();
				updatGridViewForSelectedItem(item);
				return;
			}

			//CASE IS NOT A SEARCH
			if (item.isDirectory())
				updateStoreByRpc(item); //View children of folder
			else{
				String currentIdentifier = currentFolder.getIdentifier(); //Actual id parent

				if(!currentIdentifier.equals(item.getParentFileModel().getIdentifier())) {//Update store only if folder parent is differently
					updateStoreByRpc(item.getParentFileModel()); //View contents of parent folder
				}
			}
		}
		else{
			updatGridViewForSelectedItem(item); //No operation is running.. view only items of same level tree of selected item
			loadBreadcrumbByFileModel(item, true);
		}
	}

	/**
	 * Updat grid view for selected item.
	 *
	 * @param item the item
	 */
	private void updatGridViewForSelectedItem(FileModel item){

		if(item==null)
			return;

		if(item.isDirectory())
			updateStoreByRpc(item);
		else
			updateStoreByRpc(item.getParentFileModel());
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#expandFolderItem(org.gcube.portlets.user.workspace.client.model.FolderModel)
	 */
	@Override
	public void expandFolderItem(FolderModel folder) {
		updateStoreByRpc(folder);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#renameItem(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean renameItem(String itemIdentifier, String newName, String extension) {

		FileModel lastParent = wsPortlet.getToolBarPath().getLastParent(); //RELOAD BREADCRUMB
		loadBreadcrumbByFileModel(lastParent, true);

		return wsPortlet.getGridGroupContainer().renameItem(itemIdentifier, newName, extension);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#deleteItems(java.util.List)
	 */
	@Override
	public boolean deleteItems(List<String> ids)  {

		if(ids==null || ids.size()==0)
			return false;

		boolean deleted = false;
		for (String itemIdentifier : ids) {
			deleted = wsPortlet.getGridGroupContainer().deleteItem(itemIdentifier);
		}
		performTrashOperation(WorkspaceTrashOperation.REFRESH, null);
		return deleted;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#rootLoaded(org.gcube.portlets.user.workspace.client.model.FileModel)
	 */
	@Override
	public void rootLoaded(FileModel root) {
		this.rootIdentifier = root.getIdentifier();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#setParentItemSelected(java.util.ArrayList)
	 */
	@Override
	public void setParentItemSelected(ArrayList<FileModel> listParents){
		GWT.log("setParentItemSelected.. ");
//		updateBreadcrumb(listParents);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#smartFolderSelected(java.lang.String, java.lang.String)
	 */
	@Override
	public void smartFolderSelected(final String folderId, final GXTCategorySmartFolder category) {

		selectedSmartFolderId = folderId;
		selectedSmartFolderCategory = category;

		GWT.log("Smart folder selected, folderId: " + selectedSmartFolderId);
		GWT.log("Smart folder selected, category: " + selectedSmartFolderCategory);

		wsPortlet.getGridGroupContainer().mask(ConstantsExplorer.LOADING,ConstantsExplorer.LOADINGSTYLE);

		wsPortlet.getGridGroupContainer().setBorderAsOnSearch(true);

		if (folderId != null && !folderId.isEmpty()) {

			appContrExplorer.getRpcWorkspaceService().getSmartFolderResultsById(folderId,new AsyncCallback<List<FileGridModel>>() {

				@Override
				public void onFailure(Throwable caught) {
					new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting smart folders. " +ConstantsExplorer.TRY_AGAIN, null);
					wsPortlet.getGridGroupContainer().unmask();
				}

				@Override
				public void onSuccess(List<FileGridModel> result) {
					wsPortlet.getSearchAndFilterContainer().setSearchActive(true);
					//					setSearchActive(true);
					wsPortlet.getGridGroupContainer().unmask();
					wsPortlet.getGridGroupContainer().updateStore(result);
				}

			});
		} else {

			if (category != null) {

				appContrExplorer.getRpcWorkspaceService().getSmartFolderResultsByCategory(category, new AsyncCallback<List<FileGridModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting smart folder results by category. " +ConstantsExplorer.TRY_AGAIN, null);
					}

					@Override
					public void onSuccess(List<FileGridModel> result) {
						wsPortlet.getGridGroupContainer().unmask();
						wsPortlet.getGridGroupContainer().updateStore(result);
					}
				});

			}

		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#movedItems(java.lang.String, org.gcube.portlets.user.workspace.client.model.FileModel)
	 */
	@Override
	public void movedItems(String sourceParentIdentifier, FileModel targetParent) {
		updateStoreByRpc(targetParent);
		loadBreadcrumbByFileModel(targetParent, true); //ADDED 13-06-2013

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#switchView(org.gcube.portlets.user.workspace.client.ConstantsExplorer.ViewSwitchType)
	 */
	@Override
	public void switchView(ViewSwitchType type){

		if(type.equals(ViewSwitchType.Tree) || type.equals(ViewSwitchType.SmartFolder)){
			wsPortlet.getGxtCardLayoutResultPanel().setActivePanel(ViewSwitchTypeInResult.Group);
			wsPortlet.getSearchAndFilterContainer().setEmptyText(ConstantsPortlet.SEARCHBYNAME);
		}
		else{
			wsPortlet.getGxtCardLayoutResultPanel().setActivePanel(ViewSwitchTypeInResult.Messages);
			wsPortlet.getSearchAndFilterContainer().setEmptyText(ConstantsPortlet.SEARCHINMESSAGE);
		}

		//***ADDED 24/04/2012
		wsPortlet.getSearchAndFilterContainer().searchCancel();
		eventBus.fireEvent(new SearchTextEvent(null, null));
		//		appContrExplorer.searching(false);
		//**************

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#refreshFolder(org.gcube.portlets.user.workspace.client.model.FileModel, boolean, boolean)
	 */
	@Override
	public void refreshFolder(FileModel folderTarget, boolean forceRefreshContent, boolean forceRefreshBreadcrumb){

		resetSmartFolderSelected();

		GWT.log("refreshFolder method..");
		if(folderTarget!=null){
			GWT.log("folder target is: "+ folderTarget.getName() + ", forceRefresh is :" +forceRefreshContent);
			if(forceRefreshContent){
				//FORCED REFRESH FOLDER
				FileGridModel folder = wsPortlet.getGridGroupContainer().getFileGridModelByIdentifier(folderTarget.getIdentifier());
				GWT.log("force refresh, folder :" +folder);
				updateStoreByRpc(folderTarget);

				if(forceRefreshBreadcrumb){
					GWT.log("forcing reload breadcrumb for: "+folderTarget);
					loadBreadcrumbByFileModel(folderTarget, true);
				}

				return;
			}

			FileModel filePath = wsPortlet.getToolBarPath().getLastParent();
			if(filePath!=null){

				//REFRESH FOLDER ONLY IF IS THE LAST ITEM OF BREADCRUMB
				if(folderTarget.getIdentifier().compareToIgnoreCase(filePath.getIdentifier())==0){
					//FileGridModel folder = wsPortlet.getGridGroupContainer().getFileGridModelByIdentifier(folderTarget.getIdentifier());
					GWT.log("refresh folder is equal to the LAST ITEM OF BREADCRUMB: " +folderTarget);
					updateStoreByRpc(folderTarget);
					return;
				}
			}

			GWT.log("folderTarget is not egual to last parent of the breadrcrumb, refresh folder skipped");

			if(forceRefreshBreadcrumb){
				GWT.log("forcing reload breadcrumb for: "+folderTarget);
				loadBreadcrumbByFileModel(folderTarget, true);
			}

		}
		else
			GWT.log("folderTarget is null, refresh skypped");
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#createNewMessage(java.util.HashMap)
	 */
	@Override
	public void createNewMessage(final HashMap<String, String> hashAttachs) {
		GWT.runAsync(MailForm.class, new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				if (hashAttachs.size() == 0) { //no attachments
					new MailForm();
				} else {
					new MailForm(hashAttachs);
				}
			}
			public void onFailure(Throwable reason) {
				Window.alert("Could not load this component: " + reason.getMessage());
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#fileDownloaded(java.lang.String)
	 */
	@Override
	public void fileDownloaded(String itemIdentifier) {

		FileGridModel fileItem = wsPortlet.getGridGroupContainer().getFileGridModelByIdentifier(itemIdentifier);

		if(fileItem!=null && itemIdentifier!= null && fileItem.getIdentifier().compareTo(itemIdentifier)==0){

			//SET ICON AS READ IF ITEM IS SELECETED IN THE GRID
//			FileGridModel fileSelected = getGridSelectedItem();
//			if(fileSelected!=null && fileSelected.getIdentifier().compareTo(itemIdentifier)==0)
//				accountingSetItemAsRead(true);
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#sessionExpired()
	 */
	@Override
	public void viewSessionExpiredPanel() {

		FileModel folder = wsPortlet.getGridGroupContainer().getCurrentFolderView();
		String folderId = "";

		if(folder!=null){
			folderId = folder.getIdentifier();
		}else if(wsPortlet.getToolBarPath().getLastParent()!=null){
			folderId = wsPortlet.getToolBarPath().getLastParent().getIdentifier();
		}

		logger.log(Level.INFO, "Showing session expired panel, folderId is: "+folderId);

		if(folderId!=null && !folderId.isEmpty()){
			HashMap<String, String> params = new HashMap<String, String>(1);
			params.put(ConstantsExplorer.GET_ITEMID_PARAMETER, folderId);
			logger.log(Level.INFO, "show logout with parameter is: "+folderId);
			CheckSession.showLogoutDialog(params);
		}else{
			logger.log(Level.INFO, "show logout without parameters");
			CheckSession.showLogoutDialog();
		}

		if(!CheckSession.getInstance().isShowSessionExpiredDialog() && this.rootPanel!=null){
			rootPanel.clear();
			rootPanel.add(showProblems());
			getMainPanel().setHeight(350);
		}
	}


	/**
	 * Show problems.
	 *
	 * @return the layout container
	 */
	private LayoutContainer showProblems() {
		LayoutContainer errorPanel = new LayoutContainer();
		errorPanel.setLayout(new FitLayout());

		errorPanel.add(new HTML("<div class=\"nofeed-message\">" +
				"Ops! There were problems while retrieving your workspace!" +
				"<br> Your session expired, please try to <a href=\"/c/portal/logout\">login again</a> "));

		return errorPanel;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#pasteEventIsCompleted()
	 */
	@Override
	public void pasteEventIsCompleted(boolean isTreeRefreshable, String parentId) {

		if(isTreeRefreshable){
			wsPortlet.getGridGroupContainer().unmask();
		}else{ //FORCE GRID REFRESH
			FileModel lastBreadCrumb = wsPortlet.getToolBarPath().getLastParent();
			GWT.log("PasteEventIsCompleted tree is not refreshable");

			if(lastBreadCrumb!=null){
				GWT.log("Comparing breadcrumb id: "+lastBreadCrumb.getIdentifier() + " and parent id: "+parentId);
				if(lastBreadCrumb.getIdentifier().compareToIgnoreCase(parentId)==0){
					eventBus.fireEvent(new GridRefreshEvent());
				}
			}
		}
		//		Info.display("Info", "paste submitting...");
		wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().activePasteButton(false);

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#copyEventIsCompleted()
	 */
	@Override
	public void copyEventIsCompleted() {
		wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemFunctionalities().activePasteButton(true);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#updatedVREPermissions(java.lang.String)
	 */
	@Override
	public void updatedVREPermissions(String vreFolderId) {

		GxtBreadcrumbPathPanel breadCrumb = this.wsPortlet.getToolBarPath();
		FileModel parent = breadCrumb.getLastParent();

		if(parent!=null)
			GWT.log("UpdatedVREPermissions comparing "+vreFolderId +" and "+parent.getIdentifier());
		//IF VRE FOLDER (UPDATED) IS CURRENT BREADCRUMB DISPLAING -> UPDATE
		if(vreFolderId!=null && parent!=null && vreFolderId.compareToIgnoreCase(parent.getIdentifier())==0){
			setACLInfo(vreFolderId);
		}
	}


	/**
	 * Perform versioning operation.
	 *
	 * @param fileVersioningEvent the file versioning event
	 */
	private void performVersioningOperation(final FileVersioningEvent fileVersioningEvent){

		FileModel currentVersion = fileVersioningEvent.getCurrentVersion();
		List<FileVersionModel> olderVersions = fileVersioningEvent.getOlderVersion();

		GWT.log("current version: "+currentVersion);
		GWT.log("olderVersions: "+olderVersions);
		//INVOKING FROM TOOLBAR??
		if(currentVersion==null) {
			currentVersion = getGridSelectedItem();
		}
		GWT.log("current version: "+currentVersion);

		switch (fileVersioningEvent.getVersioningOperation()) {
			case SHOW:{

				final WindowVersioning wv = new WindowVersioning(currentVersion);
				AppControllerExplorer.rpcWorkspaceService.getVersionHistory(currentVersion.getIdentifier(), new AsyncCallback<List<FileVersionModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", caught.getMessage(), null);
					}

					@Override
					public void onSuccess(List<FileVersionModel> result) {
						wv.updateVersioningContainer(result);
						wv.updateItemsNumber(result.size());
					}
				});
				wv.show();

				break;
			}
			case DOWNLOAD:{
				for (FileVersionModel fileVersionModel : olderVersions) {
					AppControllerExplorer.getEventBus().fireEvent(new FileDownloadEvent(currentVersion.getIdentifier(), currentVersion.getName(), DownloadType.DOWNLOAD, false, fileVersionModel.getIdentifier()));
				}

				break;
			}

			case DELETE_PERMANENTLY: {

				fileVersioningEvent.getWinVersioning().mask("performing operation...");
				List<String> olderVsIds = new ArrayList<String>(olderVersions.size());
				for (FileVersionModel fileVersionModel : olderVersions) {
					olderVsIds.add(fileVersionModel.getIdentifier());
				}

				AppControllerExplorer.rpcWorkspaceService.performOperationOnVersionedFile(currentVersion.getIdentifier(), olderVsIds, WorkspaceVersioningOperation.DELETE_PERMANENTLY, new AsyncCallback<List<FileVersionModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						fileVersioningEvent.getWinVersioning().unmask();
						//Window.alert("Error: "+caught.getMessage());
						new MessageBoxAlert("Error", caught.getMessage(), null);

					}

					@Override
					public void onSuccess(List<FileVersionModel> result) {
						fileVersioningEvent.getWinVersioning().unmask();
						fileVersioningEvent.getWinVersioning().updateVersioningContainer(result);
						fileVersioningEvent.getWinVersioning().updateItemsNumber(result.size());

					}
				});
				break;
			}

			case REFRESH: {
				fileVersioningEvent.getWinVersioning().mask("performing operation...");
				AppControllerExplorer.rpcWorkspaceService.getVersionHistory(currentVersion.getIdentifier(), new AsyncCallback<List<FileVersionModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						fileVersioningEvent.getWinVersioning().unmask();
					}

					@Override
					public void onSuccess(List<FileVersionModel> result) {
						fileVersioningEvent.getWinVersioning().unmask();
						fileVersioningEvent.getWinVersioning().updateVersioningContainer(result);
						fileVersioningEvent.getWinVersioning().updateItemsNumber(result.size());
					}
				});
				break;
			}

			case DELETE_ALL_OLDER_VERSIONS: {

				fileVersioningEvent.getWinVersioning().mask("performing operation...");
				List<String> olderVsIds = new ArrayList<String>(olderVersions.size());
				for (FileVersionModel fileVersionModel : olderVersions) {
					olderVsIds.add(fileVersionModel.getIdentifier());
				}

				AppControllerExplorer.rpcWorkspaceService.performOperationOnVersionedFile(currentVersion.getIdentifier(), olderVsIds, WorkspaceVersioningOperation.DELETE_ALL_OLDER_VERSIONS, new AsyncCallback<List<FileVersionModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						fileVersioningEvent.getWinVersioning().unmask();
						new MessageBoxAlert("Error", caught.getMessage(), null);

					}

					@Override
					public void onSuccess(List<FileVersionModel> result) {
						fileVersioningEvent.getWinVersioning().unmask();
						fileVersioningEvent.getWinVersioning().updateVersioningContainer(result);
						fileVersioningEvent.getWinVersioning().updateItemsNumber(result.size());

					}
				});
				break;
			}
			case RESTORE:{

				fileVersioningEvent.getWinVersioning().mask("performing operation...");
				List<String> olderVsIds = new ArrayList<String>(olderVersions.size());
				for (FileVersionModel fileVersionModel : olderVersions) {
					olderVsIds.add(fileVersionModel.getIdentifier());
				}

				AppControllerExplorer.rpcWorkspaceService.performOperationOnVersionedFile(currentVersion.getIdentifier(), olderVsIds, WorkspaceVersioningOperation.RESTORE, new AsyncCallback<List<FileVersionModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						fileVersioningEvent.getWinVersioning().unmask();
						new MessageBoxAlert("Error", caught.getMessage(), null);

					}

					@Override
					public void onSuccess(List<FileVersionModel> result) {
						fileVersioningEvent.getWinVersioning().unmask();
						fileVersioningEvent.getWinVersioning().updateVersioningContainer(result);
						fileVersioningEvent.getWinVersioning().updateItemsNumber(result.size());

					}
				});
				break;
			}
		}


	}


	/**
	 * Perform trash operation.
	 *
	 * @param operation the operation
	 * @param trashItemIds the trash item ids
	 */
	private void performTrashOperation(final WorkspaceTrashOperation operation, List<FileModel> trashItemIds){

		GWT.log("Executing trash operation: "+operation);

		if(operation==null)
			return;

		switch (operation) {

		case SHOW:
			WindowTrash.getInstance().show();
			break;

		case DELETE_PERMANENTLY:
		case RESTORE:{

			if(trashItemIds==null || trashItemIds.isEmpty())
				return;


			WindowTrash.getInstance().maskContainer("Updating Trash");
			List<String> trashIds = new ArrayList<String>(trashItemIds.size());
			for (FileModel fileModel : trashItemIds) {
				trashIds.add(fileModel.getIdentifier());
			}

			AppControllerExplorer.rpcWorkspaceService.executeOperationOnTrash(trashIds, operation, new AsyncCallback<TrashOperationContent>() {

				@Override
				public void onFailure(Throwable arg0) {
					WindowTrash.getInstance().unmaskContainer();

				}

				@Override
				public void onSuccess(TrashOperationContent operationResult) {

					WindowTrash.getInstance().executeOperationOnTrashContainer(operationResult.getListTrashIds(), operationResult.getOperation());

					wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemDetails().updateTrashIcon(WindowTrash.getInstance().getTrashedFiles().size()>0);

					//SHOW POSSIBLY ERRORS
					WindowTrash.getInstance().showTrashErrors(operation, operationResult.getListErrors());

					if(operation.equals(WorkspaceTrashOperation.RESTORE)){
						appContrExplorer.refreshRoot(true);
					}

					WindowTrash.getInstance().unmaskContainer();

				}
			});

			break;
		}


		default:{

			WindowTrash.getInstance().maskContainer("Updating Trash");
			AppControllerExplorer.rpcWorkspaceService.updateTrashContent(operation, new AsyncCallback<TrashContent>() {

				@Override
				public void onFailure(Throwable arg0) {
					WindowTrash.getInstance().unmaskContainer();

				}

				@Override
				public void onSuccess(TrashContent operationResult) {

					WindowTrash.getInstance().updateTrashContainer(operationResult.getTrashContent());

					wsPortlet.getGxtCardLayoutResultPanel().getToolBarItemDetails().updateTrashIcon(operationResult.getTrashContent().size()>0);

					//SHOW POSSIBLY ERRORS
					WindowTrash.getInstance().showTrashErrors(operation, operationResult.getListErrors());

					if(operation.equals(WorkspaceTrashOperation.RESTORE_ALL)){
						appContrExplorer.refreshRoot(true);
					}

					WindowTrash.getInstance().unmaskContainer();
				}
			});

			break;
		}
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#trashEvent(org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation, java.util.List)
	 */
	@Override
	public void trashEvent(WorkspaceTrashOperation trashOperation, List<FileModel> targetFileModels) {
		performTrashOperation(trashOperation, targetFileModels);

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#changedFileModelId(java.lang.String, java.lang.String)
	 */
	@Override
	public void changedFileModelId(String oldId, String newId) {

	}

	/**
	 * Sets the visible user quote.
	 *
	 * @param bool the new visible user quote
	 */
	private void setVisibleUserQuote(boolean bool){
		wsQuotesView.setQuoteVisible(bool);
	}

	/**
	 * Sets the visible ws available features.
	 *
	 * @param bool the new visible ws available features
	 */
	private void setVisibleWsAvailableFeatures(boolean bool){
		workspaceFeatures.setVisible(bool);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#updateWorksapaceSize()
	 */
	@Override
	public void updateWorksapaceSize(boolean delayCall) {

		Timer t = new Timer() {

			@Override
			public void run() {

				AppControllerExplorer.rpcWorkspaceService.getUserWorkspaceQuote(new AsyncCallback<WorkspaceUserQuote>() {

					@Override
					public void onFailure(Throwable arg0) {
						GWT.log("Failed get worskpace quote as", arg0);
						setVisibleUserQuote(false);
					}

					@Override
					public void onSuccess(WorkspaceUserQuote wsquote) {

						if(wsquote==null)
							setVisibleUserQuote(false);
						else{
							GWT.log("Updating worskpace quote as: "+wsquote);
							setWorkspaceUserQuotes(wsquote.getDiskSpaceFormatted(), wsquote.getTotalItems());
						}
					}
				});

			}
		};

		if(delayCall)
			t.schedule(3000); //UPDATING RUN AFTER THREE SECOND
		else
			t.run();
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface#versioningHistory(org.gcube.portlets.user.workspace.client.model.FileModel)
	 */
	@Override
	public void versioningHistory(FileModel file) {

		AppController.getEventBus().fireEvent(new FileVersioningEvent(WorkspaceVersioningOperation.SHOW, file, null, null));

	}
}

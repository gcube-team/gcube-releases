package org.gcube.portlets.user.workspace.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WS_UPLOAD_TYPE;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WsPortletInitOperation;
import org.gcube.portlets.user.workspace.client.event.AccountingHistoryEvent;
import org.gcube.portlets.user.workspace.client.event.AccountingHistoryEventHandler;
import org.gcube.portlets.user.workspace.client.event.AccountingReadersEvent;
import org.gcube.portlets.user.workspace.client.event.AccountingReadersEventHandler;
import org.gcube.portlets.user.workspace.client.event.AddAdministratorEvent;
import org.gcube.portlets.user.workspace.client.event.AddAdministratorEventHandler;
import org.gcube.portlets.user.workspace.client.event.AddFolderEvent;
import org.gcube.portlets.user.workspace.client.event.AddFolderEventHandler;
import org.gcube.portlets.user.workspace.client.event.AddSmartFolderEvent;
import org.gcube.portlets.user.workspace.client.event.AddSmartFolderEventHandler;
import org.gcube.portlets.user.workspace.client.event.BulkCreatorEvent;
import org.gcube.portlets.user.workspace.client.event.BulkCreatorEventHandler;
import org.gcube.portlets.user.workspace.client.event.CompletedFileUploadEvent;
import org.gcube.portlets.user.workspace.client.event.CompletedFileUploadEventHandler;
import org.gcube.portlets.user.workspace.client.event.CopytemEvent;
import org.gcube.portlets.user.workspace.client.event.CopytemEventHandler;
import org.gcube.portlets.user.workspace.client.event.CreateSharedFolderEvent;
import org.gcube.portlets.user.workspace.client.event.CreateSharedFolderEventHandler;
import org.gcube.portlets.user.workspace.client.event.CreateUrlEvent;
import org.gcube.portlets.user.workspace.client.event.CreateUrlEventHandler;
import org.gcube.portlets.user.workspace.client.event.DeleteItemEvent;
import org.gcube.portlets.user.workspace.client.event.DeleteItemEventHandler;
import org.gcube.portlets.user.workspace.client.event.DeleteSmartFolderEvent;
import org.gcube.portlets.user.workspace.client.event.DeleteSmartFolderEventHandler;
import org.gcube.portlets.user.workspace.client.event.EditUserPermissionEvent;
import org.gcube.portlets.user.workspace.client.event.EditUserPermissionEventHandler;
import org.gcube.portlets.user.workspace.client.event.ExpandFolderEvent;
import org.gcube.portlets.user.workspace.client.event.ExpandFolderEventHandler;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent.DownloadType;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEventHandler;
import org.gcube.portlets.user.workspace.client.event.FileUploadEvent;
import org.gcube.portlets.user.workspace.client.event.FileUploadEventHandler;
import org.gcube.portlets.user.workspace.client.event.FilterScopeEvent;
import org.gcube.portlets.user.workspace.client.event.FilterScopeEventHandler;
import org.gcube.portlets.user.workspace.client.event.GetInfoEvent;
import org.gcube.portlets.user.workspace.client.event.GetInfoEventHandler;
import org.gcube.portlets.user.workspace.client.event.GetPublicLinkEvent;
import org.gcube.portlets.user.workspace.client.event.GetPublicLinkEventHandler;
import org.gcube.portlets.user.workspace.client.event.GetShareLinkEvent;
import org.gcube.portlets.user.workspace.client.event.GetSharedLinkEventHandler;
import org.gcube.portlets.user.workspace.client.event.ImagePreviewEvent;
import org.gcube.portlets.user.workspace.client.event.ImagePreviewEventHandler;
import org.gcube.portlets.user.workspace.client.event.MoveItemEvent;
import org.gcube.portlets.user.workspace.client.event.MoveItemEventHandler;
import org.gcube.portlets.user.workspace.client.event.OpenContextMenuTreeEvent;
import org.gcube.portlets.user.workspace.client.event.OpenContextMenuTreeEventHandler;
import org.gcube.portlets.user.workspace.client.event.OpenReportsEvent;
import org.gcube.portlets.user.workspace.client.event.OpenReportsEventHandler;
import org.gcube.portlets.user.workspace.client.event.OpenUrlEvent;
import org.gcube.portlets.user.workspace.client.event.OpenUrlEventHandler;
import org.gcube.portlets.user.workspace.client.event.PasteItemEvent;
import org.gcube.portlets.user.workspace.client.event.PasteItemEventHandler;
import org.gcube.portlets.user.workspace.client.event.RefreshFolderEvent;
import org.gcube.portlets.user.workspace.client.event.RefreshItemEventHandler;
import org.gcube.portlets.user.workspace.client.event.RenameItemEvent;
import org.gcube.portlets.user.workspace.client.event.RenameItemEventHandler;
import org.gcube.portlets.user.workspace.client.event.RenderForm;
import org.gcube.portlets.user.workspace.client.event.RenderFormEventHandler;
import org.gcube.portlets.user.workspace.client.event.SelectedItemEvent;
import org.gcube.portlets.user.workspace.client.event.SelectedItemEventHandler;
import org.gcube.portlets.user.workspace.client.event.SendMessageEvent;
import org.gcube.portlets.user.workspace.client.event.SendMessageEventHandler;
import org.gcube.portlets.user.workspace.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.workspace.client.event.SessionExpiredEventHandler;
import org.gcube.portlets.user.workspace.client.event.SmartFolderSelectedEvent;
import org.gcube.portlets.user.workspace.client.event.SmartFolderSelectedEventHandler;
import org.gcube.portlets.user.workspace.client.event.SubTreeLoadedEvent;
import org.gcube.portlets.user.workspace.client.event.SubTreeLoadedEventHandler;
import org.gcube.portlets.user.workspace.client.event.SwitchViewEvent;
import org.gcube.portlets.user.workspace.client.event.SwitchViewEventHandler;
import org.gcube.portlets.user.workspace.client.event.TrashEvent;
import org.gcube.portlets.user.workspace.client.event.TrashEventHandler;
import org.gcube.portlets.user.workspace.client.event.UnShareFolderEvent;
import org.gcube.portlets.user.workspace.client.event.UnShareFolderEventHandler;
import org.gcube.portlets.user.workspace.client.event.UpdateWorkspaceSizeEvent;
import org.gcube.portlets.user.workspace.client.event.UpdateWorkspaceSizeEventHandler;
import org.gcube.portlets.user.workspace.client.event.UpdatedVREPermissionEvent;
import org.gcube.portlets.user.workspace.client.event.UpdatedVREPermissionEventHandler;
import org.gcube.portlets.user.workspace.client.event.VRESettingPermissionEvent;
import org.gcube.portlets.user.workspace.client.event.VRESettingPermissionEventHandler;
import org.gcube.portlets.user.workspace.client.event.WebDavUrlEvent;
import org.gcube.portlets.user.workspace.client.event.WebDavUrlEventHandler;
import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.interfaces.SubscriberInterface;
import org.gcube.portlets.user.workspace.client.interfaces.TreeAppControllerInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.model.SmartFolderModel;
import org.gcube.portlets.user.workspace.client.model.SubTree;
import org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService;
import org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceServiceAsync;
import org.gcube.portlets.user.workspace.client.util.RequestBuilderWorkspaceValidateItem;
import org.gcube.portlets.user.workspace.client.util.WindowOpenParameter;
import org.gcube.portlets.user.workspace.client.view.ExplorerPanel;
import org.gcube.portlets.user.workspace.client.view.sharing.DialogShareFolder;
import org.gcube.portlets.user.workspace.client.view.sharing.permissions.DialogPermission;
import org.gcube.portlets.user.workspace.client.view.tree.AsyncTreePanel;
import org.gcube.portlets.user.workspace.client.view.windows.BulkCreatorWindow;
import org.gcube.portlets.user.workspace.client.view.windows.DialogAddFolderAndSmart;
import org.gcube.portlets.user.workspace.client.view.windows.DialogAddFolderAndSmart.AddType;
import org.gcube.portlets.user.workspace.client.view.windows.DialogAddUrl;
import org.gcube.portlets.user.workspace.client.view.windows.DialogGetInfo;
import org.gcube.portlets.user.workspace.client.view.windows.DialogPublicLink;
import org.gcube.portlets.user.workspace.client.view.windows.DialogShareLink;
import org.gcube.portlets.user.workspace.client.view.windows.DialogText;
import org.gcube.portlets.user.workspace.client.view.windows.DialogWebDavUrl;
import org.gcube.portlets.user.workspace.client.view.windows.InfoDisplayMessage;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxConfirm;
import org.gcube.portlets.user.workspace.client.view.windows.NewBrowserWindow;
import org.gcube.portlets.user.workspace.client.view.windows.WindowImagePreview;
import org.gcube.portlets.user.workspace.client.view.windows.accounting.WindowAccountingInfo;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalImage;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalUrl;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube.GWTImageDocument;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube.GWTUrlDocument;
import org.gcube.portlets.user.workspace.shared.ReportAssignmentACL;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.portlets.user.workspace.shared.UserBean;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;
import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing.LOAD_CONTACTS_AS;
import org.gcube.portlets.widgets.workspacesharingwidget.client.MultiDragConstants;
import org.gcube.portlets.widgets.workspacesharingwidget.client.MultiDragContactsEditPermissions;
import org.gcube.portlets.widgets.workspacesharingwidget.client.SimpleMultiDragWorkspaceContact;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest.DialogMultiDragContact;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.DialogUpload.UPLOAD_TYPE;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

/**
 * The Class AppControllerExplorer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class AppControllerExplorer implements EventHandler, TreeAppControllerInterface{

	public static final GWTWorkspaceServiceAsync rpcWorkspaceService = (GWTWorkspaceServiceAsync) GWT.create(GWTWorkspaceService.class);
	private ExplorerPanel explorerPanel;
	
	private final static HandlerManager eventBus = new HandlerManager(null);
	private HashMap<EventsTypeEnum, ArrayList<SubscriberInterface>> subscribers = null;

	
	private boolean selectRootItem;
//	private FileUploader fileUploader;
	public static String myLogin;
	public static String myLoginFirstName;
	
	private static AppControllerExplorer singleton;

	/**
	 * Instantiates a new app controller explorer.
	 */
	public AppControllerExplorer() {
		Registry.register(ConstantsExplorer.RPC_WORKSPACE_SERVICE, rpcWorkspaceService);
		subscribers = new HashMap<EventsTypeEnum, ArrayList<SubscriberInterface>>(); 
		bind();
		singleton = this;
	}
	

	/**
	 * Gets the single instance of AppControllerExplorer.
	 *
	 * @return single instance of AppControllerExplorer
	 */
	public static AppControllerExplorer getInstance() {
		return singleton;
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
	 * Gets the rpc workspace service.
	 *
	 * @return the rpc workspace service
	 */
	public GWTWorkspaceServiceAsync getRpcWorkspaceService() {

		return rpcWorkspaceService;
	}

	/**
	 * Bind.
	 */
	private void bind() {

		eventBus.addHandler(UpdateWorkspaceSizeEvent.TYPE, new UpdateWorkspaceSizeEventHandler() {
			
			@Override
			public void onUpdateWorkspaceSize(UpdateWorkspaceSizeEvent updateWorkspaceSizeEvent) {
				doUpdateWorkspaceSize(updateWorkspaceSizeEvent);
			}
		});

		eventBus.addHandler(RenderForm.TYPE, new RenderFormEventHandler() {
			@Override
			public void onRenderForm(RenderForm event) {
				
			}
		});  


		eventBus.addHandler(SendMessageEvent.TYPE, new SendMessageEventHandler() {

			@Override
			public void onSendMessage(SendMessageEvent sendMessageEvent) {
				notifySubscriber(sendMessageEvent);
			}
		});
		
		eventBus.addHandler(UnShareFolderEvent.TYPE,new UnShareFolderEventHandler() {

			@Override
			public void onUnShareFolder(UnShareFolderEvent unShareFolderEvent) {

				final FileModel source = unShareFolderEvent.getTargetFileModel();

				String folderId = null;

				if(source!=null)
					folderId = source.getIdentifier();

				if(folderId!=null){
					String msg = "Unsharing the folder, the files will be removed from your workspace. Continue?";
					MessageBoxConfirm mbc = new MessageBoxConfirm("Confirm Unshare?", msg);

					final String folderIdentification = folderId;

					mbc.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

						public void handleEvent(MessageBoxEvent be) {

							//IF NOT CANCELLED
							String clickedButton = be.getButtonClicked().getItemId();
							if(clickedButton.equals(Dialog.YES)){

								rpcWorkspaceService.unSharedFolderByFolderSharedId(folderIdentification, new AsyncCallback<Boolean>() {

									@Override
									public void onFailure(Throwable caught) {

										if(caught instanceof SessionExpiredException){
											GWT.log("Session expired");
											eventBus.fireEvent(new SessionExpiredEvent());
											return;
										}

										new MessageBoxAlert("Error", caught.getMessage(), null);
										explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();
									}

									@Override
									public void onSuccess(Boolean result) {
										if(result) //REFRESH PARENT FOLDER
											eventBus.fireEvent(new RefreshFolderEvent(source.getParentFileModel(), true, false, false));

									}
								});
							}
						}
					});	 
				}

			}
		});

		eventBus.addHandler(TrashEvent.TYPE, new TrashEventHandler() {

			@Override
			public void onTrashEvent(TrashEvent trashEvent) {

				notifySubscriber(trashEvent);
			}
		});
		
		eventBus.addHandler(AddAdministratorEvent.TYPE, new AddAdministratorEventHandler() {
			
			@Override
			public void onAddAdministrator(AddAdministratorEvent addAdministratorEvent) {
				
				final FileModel file = addAdministratorEvent.getSelectedFolder();
				
				if(file==null || file.getIdentifier()==null)
					return;
				
				rpcWorkspaceService.getOwnerByItemId(file.getIdentifier(), new AsyncCallback<InfoContactModel>() {

					@Override
					public void onFailure(Throwable caught) {
						
						if(caught instanceof SessionExpiredException){
							GWT.log("Session expired");
							eventBus.fireEvent(new SessionExpiredEvent());
							return;
						}

						new MessageBoxAlert("Error", "Sorry, an error occurred on recovering the contacts, try again later", null);
						explorerPanel.unmask();
					}

					@Override
					public void onSuccess(InfoContactModel infoContactModel) {

						//IF IS OWNER
						if(AppControllerExplorer.myLogin.compareToIgnoreCase(infoContactModel.getLogin())==0)
							showAddAdministratorsDialog(file);
						else
							new MessageBoxAlert("Permission denied", "You have no permissions to manage administrators. You are not manager of \""+file.getName()+"\"", null);
						
					}
				});
			}
		});
		
		
		eventBus.addHandler(EditUserPermissionEvent.TYPE, new EditUserPermissionEventHandler() {
			
			@Override
			public void onEditUserPermission(EditUserPermissionEvent editUserPermissionEvent) {
				
				final FileModel file = editUserPermissionEvent.getSourceFolder();
				
				if(file==null || file.getIdentifier()==null)
					return;
				
				rpcWorkspaceService.getOwnerByItemId(file.getIdentifier(), new AsyncCallback<InfoContactModel>() {

					@Override
					public void onFailure(Throwable caught) {
						
						if(caught instanceof SessionExpiredException){
							GWT.log("Session expired");
							eventBus.fireEvent(new SessionExpiredEvent());
							return;
						}

						new MessageBoxAlert("Error", "Sorry, an error occurred on recovering the contacts, try again later", null);
						explorerPanel.unmask();
					}

					@Override
					public void onSuccess(InfoContactModel infoContactModel) {

						GWT.log("Comparing owner login "+infoContactModel +" with " +AppControllerExplorer.myLogin);
						//IF IS OWNER
						if(AppControllerExplorer.myLogin.compareToIgnoreCase(infoContactModel.getLogin())==0)
							ediPermissions(file);
						else
							new MessageBoxAlert("Permission denied", "You have no authority to manage user permissions. You are not manager of \""+file.getName()+"\"", null);
						
					}

				});
				
			}
		});
		
		eventBus.addHandler(GetInfoEvent.TYPE, new GetInfoEventHandler() {

			@Override
			public void onGetInfo(GetInfoEvent getInfoEvent) {
				new DialogGetInfo(getInfoEvent.getSourceFile());
			}
		});


		eventBus.addHandler(CreateSharedFolderEvent.TYPE, new CreateSharedFolderEventHandler() {

			@Override
			public void onCreateSharedFolder(CreateSharedFolderEvent createSharedFolderEvent) {

				final FileModel sourceFileModel = createSharedFolderEvent.getFileSourceModel();

				//IF ITEM IS SHAREABLE
				if(sourceFileModel.isShareable()){

					//DEBUG
					System.out.println("create shared folderEvent - sourceFileModel id "+sourceFileModel.getIdentifier());

					final FileModel parentFileModel= createSharedFolderEvent.getParentFileModel(); 
					final boolean isNewFolder = createSharedFolderEvent.isNewFolder();
					DialogShareFolder dialogSharedFolder = null;
					String parentDirectoryName = null;
					FileModel parent = null;

					//COMMENTED 26/02/2014
//					if(parentFileModel==null){ //PARENT IS ROOT
//						parent = explorerPanel.getAsycTreePanel().getRootItem();
//						sourceFileModel.setParentFileModel(parent);
//					}

					if(sourceFileModel.isDirectory()){
						//					explorerPanel.getAsycTreePanel().setExpandTreeLevel(sourceFileModel.getIdentifier(), true);
						parentDirectoryName = sourceFileModel.getName();
						parent = sourceFileModel;
					}
					else{
						parentDirectoryName = parentFileModel.getName();

					}

					if(isNewFolder)
						dialogSharedFolder = new DialogShareFolder(parentDirectoryName,eventBus);
					else
						dialogSharedFolder = new DialogShareFolder(parentDirectoryName, sourceFileModel, eventBus);


					final DialogShareFolder finalDialog = dialogSharedFolder;
					final FileModel parentModel = parent;

					finalDialog.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {

							if(finalDialog.isValidForm(true)){

								FileModel fileModel = null;

								if(isNewFolder){
									//create a lowest object to send to server
									fileModel = new FileModel("tempId", finalDialog.getName(), parentModel, true, false); //create new shared folder

								}
								else{
									fileModel = finalDialog.getParentFolder(); //add user for share	

									//create a lowest object to send to server
									fileModel = new FileModel(fileModel.getIdentifier(), fileModel.getName(), fileModel.getParentFileModel(), fileModel.isDirectory(), fileModel.isShared());
								}

								fileModel.setDescription(finalDialog.getDescription());

								//DEBUG
								/*
								System.out.println("FileModel id "+fileModel.getIdentifier() + " name: "+fileModel.getName() + " parent " + fileModel.getParentFileModel());
								for(InfoContactModel contact:finalDialog.getSharedListUsers() ){
									System.out.println("Share with Contact "+contact) ;
									
								}*/

								System.out.println("ACL is "+finalDialog.getSelectedACL());
								
								
								Info.display("Info", "An operation of sharing was submitted");
								explorerPanel.mask("Setting permissions", ConstantsExplorer.LOADINGSTYLE);
								
								rpcWorkspaceService.shareFolder(fileModel, finalDialog.getSharedListUsers(), isNewFolder, finalDialog.getSelectedACL(), new AsyncCallback<Boolean>() {

									@Override
									public void onFailure(Throwable caught) {

										if(caught instanceof SessionExpiredException){
											GWT.log("Session expired");
											eventBus.fireEvent(new SessionExpiredEvent());
											return;
										}

										new MessageBoxAlert("Error", caught.getMessage(), null);
										explorerPanel.unmask();
										explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();
									}

									@Override
									public void onSuccess(Boolean result) {
										if(result){
											FileModel parentToRefresh=null;

											if(isNewFolder)
												parentToRefresh = parentModel;
											else
												parentToRefresh = parentFileModel;

											GWT.log("share completed throwing refresh folder : "+parentToRefresh.getName() +" get id: "+parentToRefresh.getIdentifier());
											
//											eventBus.fireEvent(new RefreshFolderEvent(parentToRefresh, true, false, false));
											
											//TODO UPDATED ID
											RefreshFolderEvent refEvent = new RefreshFolderEvent(parentToRefresh, true, true, false);
											refEvent.setForceReloadBreadCrumb(true);
											eventBus.fireEvent(refEvent);
										}
										explorerPanel.unmask();
									}
								});
							}

						}
					});	

				}
				else{//ITEM IS NOT SHAREABLE
					new InfoDisplayMessage("Info", "The selected item is not shareable because an ancestor item is already shared");

				}

			}
		});

		//********EVENTS TO NOTIFY SUBSCRIBERS
		eventBus.addHandler(SubTreeLoadedEvent.TYPE, new SubTreeLoadedEventHandler() {

			@Override
			public void onSubTreeLoaded(SubTreeLoadedEvent event) {
				doSubTreeLoaded(event);

			}
			private void doSubTreeLoaded(SubTreeLoadedEvent event) {
				notifySubscriber(event);

			}
		});

		//********EVENTS TO NOTIFY SUBSCRIBERS
		eventBus.addHandler(SessionExpiredEvent.TYPE, new SessionExpiredEventHandler() {

			@Override
			public void onSessionExpired(SessionExpiredEvent sessionExpiredEvent) {
				notifySubscriber(sessionExpiredEvent);
			}
		});

		eventBus.addHandler(WebDavUrlEvent.TYPE, new WebDavUrlEventHandler() {

			@Override
			public void onClickWebDavUrl(WebDavUrlEvent webDavUrlEvent) {

				String itemIdentifier = webDavUrlEvent.getItemIdentifier();
				if(itemIdentifier==null)
					itemIdentifier = explorerPanel.getAsycTreePanel().getRootItem().getIdentifier();

				rpcWorkspaceService.getUrlWebDav(itemIdentifier, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR+" viewing WebDAV url"+ ConstantsExplorer.TRY_AGAIN, null);
					}

					@Override
					public void onSuccess(String url) {
						//						new MessageBoxInfo(ConstantsExplorer.URL_WEBDAV, url);
						DialogWebDavUrl diag = new DialogWebDavUrl(ConstantsExplorer.URL_WEBDAV +": "+ ConstantsExplorer.TITLEACCESSWEBDAV, "", url);
						diag.selectTxt();

					}

				});

			}

		});

		eventBus.addHandler(GetShareLinkEvent.TYPE, new GetSharedLinkEventHandler() {

			@Override
			public void onGetLink(GetShareLinkEvent getLinkEvent) {

				if(getLinkEvent.getSourceFile()!=null){

					String currentUrl = portalURL();
					int lastChar = currentUrl.lastIndexOf("?");
					currentUrl = lastChar>-1?currentUrl.substring(0, lastChar):currentUrl; //IF EXISTS - REMOVE STRING AFTER ? (? INLCUSE)

					//int last = currentUrl.lastIndexOf("/");
					//String shareLinkUrl = currentUrl.substring(0,last+1) + "?" +ConstantsExplorer.GET_ITEMID_PARAMETER+"="+getLinkEvent.getSourceFile().getIdentifier();
					String shareLinkUrl = currentUrl+ "?" +ConstantsExplorer.GET_ITEMID_PARAMETER+"="+getLinkEvent.getSourceFile().getIdentifier();
					shareLinkUrl+="&"+ConstantsExplorer.GET_OPERATION_PARAMETER+"="+WsPortletInitOperation.gotofolder;

					DialogShareLink dialog = new DialogShareLink("Copy to clipboard Share Link: Ctrl+C", shareLinkUrl);
					dialog.show();
				}
			}
		});


		eventBus.addHandler(GetPublicLinkEvent.TYPE, new GetPublicLinkEventHandler() {

			@Override
			public void onGetPublicLink(GetPublicLinkEvent getPublicLinkEvent) {
				// TODO Auto-generated method stub
				if(getPublicLinkEvent.getSourceFile()!=null){
					DialogPublicLink dialog = new DialogPublicLink("Copy to clipboard Public Link: Ctrl+C", getPublicLinkEvent.getSourceFile().getIdentifier());
					dialog.show();
				}
			}
		});


		eventBus.addHandler(RefreshFolderEvent.TYPE, new RefreshItemEventHandler() {

			@Override
			public void onRefreshItem(RefreshFolderEvent refreshItemEvent) {
				
				GWT.log("RefreshFolderEvent: "+refreshItemEvent);

				if(refreshItemEvent.getFolderTarget()!=null){

					if(!refreshItemEvent.isIfExists()){ //Called Tree side
						explorerPanel.getAsycTreePanel().reloadTreeLevelAndExpandFolder(refreshItemEvent.getFolderTarget().getIdentifier(), refreshItemEvent.isExpandFolder());
						notifySubscriber(refreshItemEvent);
					}
					else{ //Validating folder existence - called portlet side
						FileModel target = explorerPanel.getAsycTreePanel().getFileModelByIdentifier(refreshItemEvent.getFolderTarget().getIdentifier());
						if(target!=null)
							explorerPanel.getAsycTreePanel().reloadTreeLevelAndExpandFolder(refreshItemEvent.getFolderTarget().getIdentifier(), refreshItemEvent.isExpandFolder());
					}
				}
				else
					GWT.log("warn: escape refresh because item is null");
			}
		});

		//Called from context menu on grid
		eventBus.addHandler(OpenContextMenuTreeEvent.TYPE, new OpenContextMenuTreeEventHandler() {

			@Override
			public void onOpenContextMenuTree(OpenContextMenuTreeEvent openContextMenuTreeEvent) {

				explorerPanel.getAsycTreePanel().getContextMenuTree().openContextMenuOnItem(openContextMenuTreeEvent.getTargetFileModel(), openContextMenuTreeEvent.getClientX(), openContextMenuTreeEvent.getClientY());
			}
		});


		eventBus.addHandler(BulkCreatorEvent.TYPE, new BulkCreatorEventHandler() {

			@Override
			public void onBulkCreator(BulkCreatorEvent bulkCreatorEvent) {

				boolean isLoading = BulkCreatorWindow.getInstance().addProgressBar(bulkCreatorEvent.getListBulks());

				explorerPanel.setLoadingBulk(isLoading);
			}
		});

		eventBus.addHandler(CopytemEvent.TYPE, new CopytemEventHandler() {

			@Override
			public void onCopyItem(CopytemEvent copytemEvent) {
				notifySubscriber(copytemEvent);
			}
		});

		eventBus.addHandler(PasteItemEvent.TYPE, new PasteItemEventHandler() {

			@Override
			public void onCutCopyAndPaste(PasteItemEvent pasteItemEvent) {

				GWT.log("PasteItemEvent is fired on : "+pasteItemEvent.getIds().size()+ "items, DestinationId: "+pasteItemEvent.getFolderDestinationId());
				doCutCopyAndPaste(pasteItemEvent);
			}

			private void doCutCopyAndPaste(final PasteItemEvent pasteItemEvent) {

				if(pasteItemEvent.getIds()==null)
					return;
				
				Info.display("Info", "Paste working...");

				switch(pasteItemEvent.getOperationType()){
				
				case CUT:
					
					//TODO remove this comments
//					if(pasteItemEvent.getFolderSourceId()==null || pasteItemEvent.getFolderSourceId().isEmpty())
//						return;
					
					rpcWorkspaceService.moveItems(pasteItemEvent.getIds(), pasteItemEvent.getFolderDestinationId(), new AsyncCallback<Boolean>() {

						public void onFailure(Throwable caught) {

							if(caught instanceof SessionExpiredException){
								GWT.log("Session expired");
								eventBus.fireEvent(new SessionExpiredEvent());
								return;
							}

							new MessageBoxAlert("Error", caught.getMessage()+"." , null);
//							System.out.println(caught.getMessage());
							explorerPanel.getAsycTreePanel().reloadTreeLevelAndExpandFolder(pasteItemEvent.getFolderDestinationId(), false);
							explorerPanel.getAsycTreePanel().reloadTreeLevelAndExpandFolder(pasteItemEvent.getFolderSourceId(), true);
						}

						@Override
						public void onSuccess(Boolean result) {
							
							FileModel fileInTree = explorerPanel.getAsycTreePanel().getFileModelByIdentifier(pasteItemEvent.getFolderDestinationId());
							
							if(result){
								eventBus.fireEvent(new RefreshFolderEvent(fileInTree, false, false, false));	
								//explorerPanel.getAsycTreePanel().setExpandTreeLevel(event.getTargetParentFileModel(), true); //Expand level drop
							}
							
							if(fileInTree!=null)
								pasteItemEvent.setTreeRefreshable(true);
							else
								pasteItemEvent.setTreeRefreshable(false);
							
							notifySubscriber(pasteItemEvent);
						}
					});
					
					break;
					
				case COPY: 
					
					rpcWorkspaceService.copyItems(pasteItemEvent.getIds(), pasteItemEvent.getFolderDestinationId(), new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {

							if(caught instanceof SessionExpiredException){
								GWT.log("Session expired");
								eventBus.fireEvent(new SessionExpiredEvent());
								return;
							}

							new MessageBoxAlert("Error", caught.getMessage(), null);
							
							eventBus.fireEvent(new RefreshFolderEvent(explorerPanel.getAsycTreePanel().getFileModelByIdentifier(pasteItemEvent.getFolderDestinationId()), false, true, false));
							
							notifySubscriber(pasteItemEvent);
						}

						@Override
						public void onSuccess(Boolean result) {

							FileModel fileInTree = explorerPanel.getAsycTreePanel().getFileModelByIdentifier(pasteItemEvent.getFolderDestinationId());
							if(result)
								eventBus.fireEvent(new RefreshFolderEvent(fileInTree, false, false, false));

							if(fileInTree!=null)
								pasteItemEvent.setTreeRefreshable(true);
							else
								pasteItemEvent.setTreeRefreshable(false);
							
							notifySubscriber(pasteItemEvent);
						}

					});
					
					break;
				
				default:
					
				}
			}
		});

		eventBus.addHandler(SwitchViewEvent.TYPE, new SwitchViewEventHandler() {

			@Override
			public void onSwitchView(SwitchViewEvent switchViewEvent) {
				notifySubscriber(switchViewEvent);
			}
		});

		eventBus.addHandler(FilterScopeEvent.TYPE, new FilterScopeEventHandler() {

			@Override
			public void onClickScopeFilter(FilterScopeEvent filterScopeEvent) {

				explorerPanel.getAsycTreePanel().setSearch(false); //SET IS SEARCH FALSE

				doChangeScope(filterScopeEvent.getScopeId());


			}

			private void doChangeScope(String scopeId) {

				explorerPanel.getAsycTreePanel().loadRootItem(scopeId,selectRootItem); // RELOAD ROOT BY SCOPE
				explorerPanel.getSmartFolderPanel().reloadPanelSmartFolder(); //RELOAD SMART FOLDER

			}
		});

		eventBus.addHandler(AccountingHistoryEvent.TYPE, new AccountingHistoryEventHandler() {

			@Override
			public void onAccountingHistoryShow(AccountingHistoryEvent accountingHistoryEvent) {

				FileModel fileItem = accountingHistoryEvent.getTargetFileModel();

				if(fileItem!=null){

					String title = ConstantsExplorer.ACCOUNTING_HISTORY_OF+fileItem.getName();

					final WindowAccountingInfo winInfo = new WindowAccountingInfo(fileItem,title);
					winInfo.show();
					winInfo.maskAccountingInfo(true);

					rpcWorkspaceService.getAccountingHistory(fileItem.getIdentifier(), new AsyncCallback<List<GxtAccountingField>>() {

						@Override
						public void onFailure(Throwable caught) {
							winInfo.maskAccountingInfo(false);
							new MessageBoxAlert("Error", caught.getMessage(), null);

						}

						@Override
						public void onSuccess(List<GxtAccountingField> result) {

							winInfo.updateInfoContainer(result);
							winInfo.maskAccountingInfo(false);
						}
					});
				}
			}
		});

		eventBus.addHandler(AccountingReadersEvent.TYPE, new AccountingReadersEventHandler() {

			@Override
			public void onAccountingReadersShow(AccountingReadersEvent accountingReadersEvent) {

				FileModel fileItem = accountingReadersEvent.getTargetFileModel();

				if(fileItem!=null){

					String title = ConstantsExplorer.ACCOUNTING_READERS_OF+fileItem.getName();

					final WindowAccountingInfo winInfo = new WindowAccountingInfo(fileItem,title);
					winInfo.show();
					winInfo.maskAccountingInfo(true);

					rpcWorkspaceService.getAccountingReaders(fileItem.getIdentifier(), new AsyncCallback<List<GxtAccountingField>>() {

						@Override
						public void onFailure(Throwable caught) {
							winInfo.maskAccountingInfo(false);
							new MessageBoxAlert("Error", caught.getMessage(), null);

						}

						@Override
						public void onSuccess(List<GxtAccountingField> result) {

							winInfo.updateInfoContainer(result);
							winInfo.maskAccountingInfo(false);
						}
					});

				}
			}
		});


		eventBus.addHandler(FileDownloadEvent.TYPE, new FileDownloadEventHandler() {

			@Override
			public void onFileDownloadEvent(FileDownloadEvent fileDownloadEvent) {

				if(fileDownloadEvent.getItemIdentifier()!=null){
					//					if(fileDownloadEvent.getDownloadType().equals(DownloadType.SHOW)){
					//						if(fileDownloadEvent.getItemName()!= null)
					//							com.google.gwt.user.client.Window.open(ConstantsExplorer.DOWNLOAD_WORKSPACE_SERVICE+"?id="+fileDownloadEvent.getItemIdentifier()+"&viewContent=true", fileDownloadEvent.getItemName(), "");
					//					}
					//					else
					//						com.google.gwt.user.client.Window.open(ConstantsExplorer.DOWNLOAD_WORKSPACE_SERVICE+"?id="+fileDownloadEvent.getItemIdentifier(), "_self", "");
					//					

					if(fileDownloadEvent.getDownloadType().equals(DownloadType.SHOW)){
						if(fileDownloadEvent.getItemName()!= null){

							try {
								new RequestBuilderWorkspaceValidateItem(RequestBuilder.GET, ConstantsExplorer.DOWNLOAD_WORKSPACE_SERVICE, "id="+fileDownloadEvent.getItemIdentifier()+"&viewContent=true", "_blank", downloadHandlerCallback);

							} catch (Exception e) {
								explorerPanel.getAsycTreePanel().unmask();
								new MessageBoxAlert("Error", e.getMessage(), null);
								explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();
							}

						}
						//							com.google.gwt.user.client.Window.open(ConstantsExplorer.DOWNLOAD_WORKSPACE_SERVICE+"?id="+fileDownloadEvent.getItemIdentifier()+"&viewContent=true", fileDownloadEvent.getItemName(), "");
					} else{

						try {
							new RequestBuilderWorkspaceValidateItem(RequestBuilder.GET,ConstantsExplorer.DOWNLOAD_WORKSPACE_SERVICE, "id="+fileDownloadEvent.getItemIdentifier(), "_self", downloadHandlerCallback);
						} catch (Exception e) {
							explorerPanel.getAsycTreePanel().unmask();
							new MessageBoxAlert("Error", e.getMessage(), null);
							explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();
						}
					}
					notifySubscriber(fileDownloadEvent);
				}

			}
		});



		eventBus.addHandler(FileUploadEvent.TYPE, new FileUploadEventHandler() {

			@Override
			public void onFileUploadEvent(FileUploadEvent fileUploadEvent) {
				doFileUploadEvent(fileUploadEvent);
			}

			private void doFileUploadEvent(final FileUploadEvent fileUploadEvent) {
				GWT.log("FileUploadEvent...");
				FileModel folder = fileUploadEvent.getTargetFolderModel();

				if(folder == null)
					folder = explorerPanel.getAsycTreePanel().getRootItem();

				String caption = "Upload ";
				UPLOAD_TYPE upType = UPLOAD_TYPE.File;
				if(fileUploadEvent.getUploadType().compareTo(WS_UPLOAD_TYPE.Archive)==0){
					caption+= " a zip Archive";
					upType = UPLOAD_TYPE.Archive;
				}else if(fileUploadEvent.getUploadType().compareTo(WS_UPLOAD_TYPE.File)==0){
					caption+= "File/s";
					upType = UPLOAD_TYPE.File;
				}
					
				caption+= " in: "+folder.getName();
				
				MultipleDilaogUpload uploadStream = new MultipleDilaogUpload(caption, folder.getIdentifier(), upType);
				WorskpaceUploadNotificationListener listener = new WorskpaceUploadNotificationListener() {
					
					@Override
					public void onUploadCompleted(String parentId, String itemId) {
						GWT.log("Upload completed: [parentID: "+parentId+", itemId: "+itemId+", uploadType: "+fileUploadEvent.getUploadType()+"]");
						eventBus.fireEvent(new CompletedFileUploadEvent(parentId, itemId, fileUploadEvent.getUploadType(), false));
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
						eventBus.fireEvent(new CompletedFileUploadEvent(parentId, itemId, fileUploadEvent.getUploadType(), true));
					}
				};
				
				uploadStream.addWorkspaceUploadNotificationListener(listener);
				uploadStream.center();
			}
		});

		eventBus.addHandler(CompletedFileUploadEvent.TYPE, new CompletedFileUploadEventHandler() {

			@Override
			public void onCompletedFileUploadEvent(CompletedFileUploadEvent completedFileUploadEvent) {
				doCompletedFileUploadEvent(completedFileUploadEvent);	
			}

			private void doCompletedFileUploadEvent(CompletedFileUploadEvent completedFileUploadEvent) {

				boolean isLevelExpanded = treeLevelIsExpanded(completedFileUploadEvent.getParentId());
				//REFRESH TREE ONLY IF FOLDER PARENT EXISTS IN TREE
				FileModel parent = explorerPanel.getAsycTreePanel().getFileModelByIdentifier(completedFileUploadEvent.getParentId());
				GWT.log("doCompletedFileUploadEvent..."+parent);
				if(parent!=null && completedFileUploadEvent.getItemIdentifier()!=null){
					explorerPanel.getAsycTreePanel().reloadTreeLevelAndExpandFolder(completedFileUploadEvent.getParentId(), isLevelExpanded);
//					explorerPanel.getAsycTreePanel().addItemIdAndExpandFolder(parent, completedFileUploadEvent.getItemIdentifier(), isLevelExpanded);
				}
				doUpdateWorkspaceSize(new UpdateWorkspaceSizeEvent());
				notifySubscriber(completedFileUploadEvent);
			}
		});


		eventBus.addHandler(DeleteSmartFolderEvent.TYPE, new DeleteSmartFolderEventHandler() {

			@Override
			public void onDeleteItem(DeleteSmartFolderEvent deleteSmartFolderEvent) {
				doDeleteSmartFolder(deleteSmartFolderEvent);

			}

			private void doDeleteSmartFolder(DeleteSmartFolderEvent deleteSmartFolderEvent) {
				final String smartIdentifier = deleteSmartFolderEvent.getSmartIdentifier();
				final String smartName = deleteSmartFolderEvent.getSmartName();

				MessageBoxConfirm mbc = new MessageBoxConfirm(ConstantsExplorer.MESSAGE_DELETE, ConstantsExplorer.MESSAGE_CONFIRM_DELETE_SMART_FOLDER  + " "+ smartName +"?");
				mbc.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

					public void handleEvent(MessageBoxEvent be) {

						//							eventBus.fireEvent(new DeleteItemEvent(sel));

						//IF NOT CANCELLED
						String clickedButton = be.getButtonClicked().getItemId();
						if(clickedButton.equals(Dialog.YES)){

							rpcWorkspaceService.removeSmartFolder(smartIdentifier, smartName, new AsyncCallback<Boolean>() {

								@Override
								public void onSuccess(Boolean result) {
									if(result)
										explorerPanel.getSmartFolderPanel().removeSmartFolder(smartIdentifier);		
								}

								@Override
								public void onFailure(Throwable caught) {
									//									explorerPanel.getAsycTreePanel().unmask();
									new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " deleting smart folder.", null);
									explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();

								}
							});

						}
					}
				});	 
			}
		});


		eventBus.addHandler(AddSmartFolderEvent.TYPE, new AddSmartFolderEventHandler() {


			@Override
			public void onSaveSmartFolder(AddSmartFolderEvent saveSmartFolderEvent) {
				doSaveSmartFolder(saveSmartFolderEvent);


			}

			private void doSaveSmartFolder(final AddSmartFolderEvent saveSmartFolderEvent) {

				final String query = saveSmartFolderEvent.getSearchText();
				final String parentId = saveSmartFolderEvent.getParentId();
				
				final DialogAddFolderAndSmart dialogAddSmartFolder = new DialogAddFolderAndSmart("", AddType.SMARTFOLDER);

				dialogAddSmartFolder.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {

						if(dialogAddSmartFolder.isValidForm())

							rpcWorkspaceService.createSmartFolder(dialogAddSmartFolder.getName(), dialogAddSmartFolder.getDescription(), query, parentId, new AsyncCallback<SmartFolderModel>() {

								@Override
								public void onFailure(Throwable caught) {
									explorerPanel.getAsycTreePanel().unmask();
									new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " saving smart folder.", null);
									explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();

								}

								@Override
								public void onSuccess(SmartFolderModel smart) {
									explorerPanel.getShortuctsPanel().loadSmartFolder(smart);
								}
							});
					}


				});
			}	
		});


		eventBus.addHandler(SmartFolderSelectedEvent.TYPE, new SmartFolderSelectedEventHandler() {

			@Override
			public void onSmartFolderSelected(SmartFolderSelectedEvent smartFolderSelectedEvent) {
				searching(true);
//				System.out.println("Click smart folder : " + smartFolderSelectedEvent.getSmartFolderName());
				doSmartFolderSelected(smartFolderSelectedEvent);
			}

			private void doSmartFolderSelected(SmartFolderSelectedEvent smartFolderSelectedEvent) {

				notifySubscriber(smartFolderSelectedEvent);
			}
		});


		eventBus.addHandler(RenameItemEvent.TYPE, new RenameItemEventHandler() {
			@Override
			public void onRenameItem(RenameItemEvent event) {
				doRenameItem(event);
			}

			private void doRenameItem(final RenameItemEvent event) {

				final FileModel target = event.getFileTarget();
				final DialogText dgt = new DialogText(ConstantsExplorer.MESSAGE_RENAME, ConstantsExplorer.MESSAGE_ITEM_NAME, event.getFileTarget().getName()); 

				dgt.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {

						final String newName = dgt.getTxtValue();

						if(dgt.isValidForm()){
							dgt.mask("Renaming...");
							rpcWorkspaceService.renameItem(target.getIdentifier(), newName, target.getName(), new AsyncCallback<Boolean>(){

								@Override
								public void onFailure(Throwable caught) {
									dgt.unmask();
									dgt.hide();
									if(caught instanceof SessionExpiredException){
										GWT.log("Session expired");
										eventBus.fireEvent(new SessionExpiredEvent());
										return;
									}

									new MessageBoxAlert("Error", caught.getMessage(), null);
									explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();

								}

								@Override
								public void onSuccess(Boolean result) {
									dgt.unmask();
									dgt.hide();
									explorerPanel.getAsycTreePanel().renameItem(target.getIdentifier(), newName, null);
									event.setNewName(newName);
									notifySubscriber(event);
								}
							});
						}
					}
				});
			}
		});

		eventBus.addHandler(DeleteItemEvent.TYPE, new DeleteItemEventHandler() {

			@Override
			public void onDeleteItem(DeleteItemEvent event) {
				doDeleteItem(event);
			}

			private void doDeleteItem(final DeleteItemEvent event){

				String title = "";
				String msg = "";

				if(event.getFileTarget().isShared()){
					title = ConstantsExplorer.MESSAGE_DELETE;
					msg = "This item is shared. Deleting this item will affect other users. Continue?";
				}

				else{
					title = ConstantsExplorer.MESSAGE_DELETE;
					msg = ConstantsExplorer.MESSAGE_CONFIRM_DELETE_ITEM  + " "+ event.getFileTarget().getName() +"?";
				}

				MessageBoxConfirm mbc = new MessageBoxConfirm(title, msg);
				mbc.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

					public void handleEvent(MessageBoxEvent be) {
						
						//IF NOT CANCELLED
						String clickedButton = be.getButtonClicked().getItemId();
						if(clickedButton.equals(Dialog.YES)){
							
							explorerPanel.getAsycTreePanel().mask("Deleting", ConstantsExplorer.LOADINGSTYLE);
							rpcWorkspaceService.removeItem(event.getFileTarget().getIdentifier(), new AsyncCallback<Boolean>(){

								@Override
								public void onFailure(Throwable caught) {
									explorerPanel.getAsycTreePanel().unmask();
									new MessageBoxAlert("Error", caught.getMessage(), null);
									explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();

								}

								@Override
								public void onSuccess(Boolean result) {
									explorerPanel.getAsycTreePanel().unmask();
									//Timer is used as work around to chrome and safari browser
									Timer t = new Timer() {
										public void run() {

											FileModel fileModel = event.getFileTarget();
											if(explorerPanel.getAsycTreePanel().deleteItem(fileModel.getIdentifier())){
												explorerPanel.getAsycTreePanel().selectItem(fileModel.getParentFileModel().getIdentifier()); //Select parent of item deleted
											}

											notifySubscriber(event);
//											doUpdateWorkspaceSize(new UpdateWorkspaceSizeEvent());
//											eventBus.fireEvent(new UpdateWorkspaceSizeEvent());
										}
									};

									// Schedule the timer to run after 250 ms.
									t.schedule(250);

								}
							});

						}
					}
				});	 
			}

		});


		eventBus.addHandler(SelectedItemEvent.TYPE, new SelectedItemEventHandler() {

			@Override
			public void onSelectedItem(SelectedItemEvent selectedItemEvent) {
				doSelectedItem(selectedItemEvent);
			}

			private void doSelectedItem(SelectedItemEvent event) {

				notifySubscriber(event);	
			}

		});


		eventBus.addHandler(ExpandFolderEvent.TYPE, new ExpandFolderEventHandler() {

			@Override
			public void onExpandFolder(ExpandFolderEvent expandFolderEvent) {
				doExpandFolder(expandFolderEvent);

			}

			private void doExpandFolder(ExpandFolderEvent expandFolderEvent) {

				notifySubscriber(expandFolderEvent);
			}

		});

		eventBus.addHandler(AddFolderEvent.TYPE, new AddFolderEventHandler() {

			@Override
			public void onAddItem(AddFolderEvent event) {
				doAddItem(event);	
			}

			private void doAddItem(final AddFolderEvent event) {

				final FileModel sourceFileModel = event.getFileSourceModel();
				final FileModel parentFileModel= event.getParentFileModel(); 

				String directory = null;

				if(sourceFileModel.isDirectory()){
					explorerPanel.getAsycTreePanel().setExpandTreeLevel(sourceFileModel.getIdentifier(), true);
					directory = sourceFileModel.getName();
				}
				else
					directory = parentFileModel.getName();

				final DialogAddFolderAndSmart dialogAddFolder = new DialogAddFolderAndSmart(directory, AddType.FOLDER);

				dialogAddFolder.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {

						if(dialogAddFolder.isValidForm()){

							if(sourceFileModel.isDirectory()){
								//									

								//TODO REMOVE
								//								System.out.println("description folder: "+dialogAddFolder.getDescription());

								rpcWorkspaceService.createFolder(dialogAddFolder.getName(), dialogAddFolder.getDescription(), sourceFileModel, new AsyncCallback<FolderModel>(){

									@Override
									public void onFailure(Throwable caught) {
										explorerPanel.getAsycTreePanel().unmask();
										new MessageBoxAlert("Error", caught.getMessage(), null);
									}

									@Override
									public void onSuccess(FolderModel child) {

										explorerPanel.getAsycTreePanel().addItem(sourceFileModel.getIdentifier(), child, false); 
										event.setNewFolder(child);
										notifySubscriber(event);
									}

								});

							}else{

								rpcWorkspaceService.createFolder(dialogAddFolder.getName(), dialogAddFolder.getDescription(), parentFileModel, new AsyncCallback<FolderModel>(){

									@Override
									public void onFailure(Throwable caught) {
										explorerPanel.getAsycTreePanel().unmask();
										new MessageBoxAlert("Error", caught.getMessage(), null);

									}

									@Override
									public void onSuccess(FolderModel child) {

										explorerPanel.getAsycTreePanel().addItem(parentFileModel.getIdentifier(), child, false); 
										event.setNewFolder(child);
										notifySubscriber(event);
									}

								});

							}
						}

					}
				});			

			}
		});


		eventBus.addHandler(MoveItemEvent.TYPE, new MoveItemEventHandler() {

			@Override
			public void onMoveItem(final MoveItemEvent event) {

				explorerPanel.getAsycTreePanel().mask(ConstantsExplorer.MOVING,ConstantsExplorer.LOADINGSTYLE);
				rpcWorkspaceService.moveItem(event.getFileSourceModel().getIdentifier(), event.getTargetParentFileModel().getIdentifier(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						explorerPanel.getAsycTreePanel().unmask();
						if(caught instanceof SessionExpiredException){
							GWT.log("Session expired");
							eventBus.fireEvent(new SessionExpiredEvent());
							return;
						}

						new MessageBoxAlert("Error", caught.getMessage()+"." , null);
//						System.out.println(caught.getMessage());
						explorerPanel.getAsycTreePanel().reloadTreeLevelAndExpandFolder(event.getTargetParentFileModel().getIdentifier(), false);
						explorerPanel.getAsycTreePanel().reloadTreeLevelAndExpandFolder(event.getFileSourceModel().getParentFileModel().getIdentifier(), true);
					}

					@Override
					public void onSuccess(Boolean result) {
						explorerPanel.getAsycTreePanel().unmask();
						if(result){
							explorerPanel.getAsycTreePanel().reloadTreeLevelAndExpandFolder(event.getTargetParentFileModel().getIdentifier(), true);
							notifySubscriber(event);
						}
					}
				});

			}
		});

		eventBus.addHandler(ImagePreviewEvent.TYPE, new ImagePreviewEventHandler() {

			@Override
			public void onClickPreview(ImagePreviewEvent imagePreviewEvent) {

				if(imagePreviewEvent.getClientX()  > 0 && imagePreviewEvent.getClientY() > 0)
					doClickPreview(imagePreviewEvent, imagePreviewEvent.getClientX(), imagePreviewEvent.getClientY() );
				else
					doClickPreview(imagePreviewEvent, 50, 50);


				//ADDED 24/07/2013
				if(imagePreviewEvent.getSourceFileModel()!=null){ //FILE CAN NOT LOADED IN TREE

					notifySubscriber(new FileDownloadEvent(imagePreviewEvent.getSourceFileModel().getIdentifier(), imagePreviewEvent.getSourceFileModel().getName(), FileDownloadEvent.DownloadType.SHOW));
				}

			}

			private void doClickPreview(ImagePreviewEvent imagePreviewEvent, final int positionX, final int positionY) {

				final FileModel fileModel = imagePreviewEvent.getSourceFileModel();
				boolean fullDetails = false;

				rpcWorkspaceService.getImageById(fileModel.getIdentifier(), fileModel.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.IMAGE_DOCUMENT), fullDetails, new AsyncCallback<GWTWorkspaceItem>() {

					@Override
					public void onFailure(Throwable caught) {
						explorerPanel.getAsycTreePanel().unmask();
						new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " imaging preview.", null);
						explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();

					}

					@Override
					public void onSuccess(GWTWorkspaceItem item) {

						if(fileModel.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.IMAGE_DOCUMENT))
							new WindowImagePreview(fileModel.getName(), (GWTImageDocument) item, positionX, positionY);
						else
							new WindowImagePreview(fileModel.getName(), (GWTExternalImage) item, positionX, positionY);

					}
				});
			}
		});


		eventBus.addHandler(OpenUrlEvent.TYPE, new OpenUrlEventHandler() {

			@Override
			public void onClickUrl(OpenUrlEvent openUrlEvent) {
				doClickUrl(openUrlEvent);

			}			
		});


		eventBus.addHandler(CreateUrlEvent.TYPE, new CreateUrlEventHandler() {

			@Override
			public void onClickCreateUrl(CreateUrlEvent createUrlEvent) {
				doClickCreateUrl(createUrlEvent);

			}

			private void doClickCreateUrl(final CreateUrlEvent createUrlEvent) {

				final FileModel parent = createUrlEvent.getParentFileModel();

				final DialogAddUrl dgu = new DialogAddUrl(parent.getName());

				dgu.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {

						if(dgu.isValidForm()){
							explorerPanel.getAsycTreePanel().mask(ConstantsExplorer.VALIDATINGOPERATION,ConstantsExplorer.LOADINGSTYLE);


							rpcWorkspaceService.createExternalUrl(parent, dgu.getName(), dgu.getDescription(), dgu.getUrl(), new AsyncCallback<FileModel>() {

								@Override
								public void onFailure(Throwable caught) {
									explorerPanel.getAsycTreePanel().unmask();
									new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR +" creating url.", null);
									explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();

								}

								@Override
								public void onSuccess(FileModel child) {

									explorerPanel.getAsycTreePanel().addItem(parent.getIdentifier(), child, false); 
									explorerPanel.getAsycTreePanel().unmask();
									notifySubscriber(createUrlEvent);
								}
							});
						}
					}
				});
			}
		});
		
		eventBus.addHandler(VRESettingPermissionEvent.TYPE, new VRESettingPermissionEventHandler() {
					
			@Override
			public void onPermissionSetting(VRESettingPermissionEvent settingPermissionEvent) {
				
				if(settingPermissionEvent.getSourceFile()!=null){
					DialogPermission dialog = new DialogPermission(settingPermissionEvent.getSourceFile());
					dialog.show();
				}else
					Info.display("Attention", "Select a VRE Folder to change permissions!");
			}
		});
		
		eventBus.addHandler(UpdatedVREPermissionEvent.TYPE, new UpdatedVREPermissionEventHandler() {
			
			@Override
			public void onUpdateVREPermissions(UpdatedVREPermissionEvent updatedVREPermissionEvent) {
				notifySubscriber(updatedVREPermissionEvent);
				
			}
		});



		eventBus.addHandler(OpenReportsEvent.TYPE, new OpenReportsEventHandler() {

			@Override
			public void onClickOpenReports(OpenReportsEvent openReportsEvent) {

				if(openReportsEvent.getSourceFileModel().getGXTFolderItemType().equals(GXTFolderItemTypeEnum.REPORT_TEMPLATE))
					doClickOpenReportTemplate(openReportsEvent);
				else
					doClickOpenReport(openReportsEvent);

			}


			public void doClickOpenReport(OpenReportsEvent openReportsEvent) {
				
				final NewBrowserWindow newBrowserWindow = NewBrowserWindow.open("", "_self", "");
				
				rpcWorkspaceService.getURLFromApplicationProfile(openReportsEvent.getSourceFileModel().getIdentifier(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting application profile - attribute idreport", null);

					}

					@Override
					public void onSuccess(String url) {

						if(url==null || url.isEmpty()){
							String currentUrl = portalURL();
							int last = currentUrl.lastIndexOf("/");
							String reportUrl = currentUrl.substring(0,last+1) + ConstantsExplorer.REPORTGENERATION;
//							new WindowOpenUrl(reportUrl, "_self", "");
							newBrowserWindow.setUrl(reportUrl);
						}
						else{
							String reportUrl = url;
//							new WindowOpenUrl(reportUrl, "_self", "");
							newBrowserWindow.setUrl(reportUrl);
						}
					}
				});

			}

			private void doClickOpenReportTemplate(OpenReportsEvent openReportTemplateEvent) {

				final NewBrowserWindow newBrowserWindow = NewBrowserWindow.open("", "_self", "");
				
				rpcWorkspaceService.getURLFromApplicationProfile(openReportTemplateEvent.getSourceFileModel().getIdentifier(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting application profile - attribute idreport", null);

					}

					@Override
					public void onSuccess(String url) {

						if(url==null || url.isEmpty()){
							String currentUrl = portalURL();
							int last = currentUrl.lastIndexOf("/");
							String templateUrl = currentUrl.substring(0,last+1) + ConstantsExplorer.TEMPLATECREATION;
							//							Log.trace("Url: "+templateUrl);
							GWT.log("currentUrl " +currentUrl);
							GWT.log("reportUrl " +templateUrl);
							newBrowserWindow.setUrl(templateUrl);
//							new WindowOpenUrl(templateUrl, "_self", "");
						}else{

							String templateUrl = url;
							newBrowserWindow.setUrl(templateUrl);
//							new WindowOpenUrl(templateUrl, "_self", "");
						}

					}
				});
			}
		});

	}


	/**
	 * Do update workspace size.
	 *
	 * @param updateWorkspaceSizeEvent the update workspace size event
	 */
	protected void doUpdateWorkspaceSize(UpdateWorkspaceSizeEvent updateWorkspaceSizeEvent) {
		notifySubscriber(updateWorkspaceSizeEvent);
	}

	/**
	 * Portal url.
	 *
	 * @return the string
	 */
	public static native String portalURL()/*-{
	return $wnd.location.href;
	}-*/;

	//********END EVENTS TO NOTIFY SUBSCRIBERS


	//Method Notify Subscriber
	/**
	 * Notify subscriber.
	 *
	 * @param event the event
	 */
	public void notifySubscriber(GuiEventInterface event)
	{
		if (subscribers.containsKey(event.getKey()))
			for (SubscriberInterface sub : subscribers.get(event.getKey())){

				if(event instanceof RenameItemEvent){

					RenameItemEvent renameEvent = (RenameItemEvent) event;

					sub.renameItem(renameEvent.getFileTarget().getIdentifier(), renameEvent.getNewName(), renameEvent.getExtension());

				}else if(event instanceof DeleteItemEvent){

					DeleteItemEvent deleteEvent = (DeleteItemEvent) event;
					List<String> ids = new ArrayList<String>(1);
					ids.add(deleteEvent.getFileTarget().getIdentifier());
					sub.deleteItems(ids);

				}else if(event instanceof SelectedItemEvent){

					SelectedItemEvent selectedEvent = (SelectedItemEvent) event;

					List<FileModel> listFileModel = new ArrayList<FileModel>();

					FileModel item = explorerPanel.getAsycTreePanel().getFileModelByIdentifier(selectedEvent.getFileTarget().getIdentifier());

					listFileModel = getListParents(listFileModel,item); //used for update path bar

					sub.selectedItem(selectedEvent.getFileTarget(), listFileModel);

				}else if(event instanceof ExpandFolderEvent){

					ExpandFolderEvent expandEvent = (ExpandFolderEvent) event;

					sub.expandFolderItem(expandEvent.getFolderTarget());

				}else if(event instanceof AddFolderEvent){

					AddFolderEvent addItemEvent = (AddFolderEvent) event;

					if(addItemEvent.getFileSourceModel().isDirectory())

						sub.addedFolder(addItemEvent.getNewFolder().getIdentifier(), addItemEvent.getFileSourceModel());
					else
						sub.addedFolder(addItemEvent.getNewFolder().getIdentifier(), addItemEvent.getParentFileModel());	

				}else if(event instanceof SubTreeLoadedEvent){

					GWT.log("SubTreeLoadedEvent...");
					SubTreeLoadedEvent subTreeEvent = (SubTreeLoadedEvent) event;

					sub.setParentItemSelected(subTreeEvent.getPathParentsList());

					//            		if(openTreeMenuEvent && isShowTreeMenu==false)
						//            			eventBus.fireEvent(new O)

				}else if(event instanceof SmartFolderSelectedEvent){

					SmartFolderSelectedEvent smartEvent = (SmartFolderSelectedEvent) event;

					sub.smartFolderSelected(smartEvent.getIdSmartFolder(), smartEvent.getCategory());

				}else if(event instanceof FileUploadEvent){

					//            		FileUploadEvent fileUpEvent = (FileUploadEvent) event;       		
					//            		sub.addedFile(fileUpEvent.getParentFileModel(), "");

				} else if(event instanceof CompletedFileUploadEvent){

					CompletedFileUploadEvent fileUpEvent = (CompletedFileUploadEvent) event;

					sub.addedFile(fileUpEvent.getItemIdentifier(), fileUpEvent.getParentId(), fileUpEvent.getUploadType(), fileUpEvent.isOverwrite());

				}else if(event instanceof CreateUrlEvent){

					CreateUrlEvent createUrlEvent = (CreateUrlEvent) event;

					sub.addedFile(createUrlEvent.getItemIdentifier(), createUrlEvent.getParentFileModel().getIdentifier(), WS_UPLOAD_TYPE.File, false);

				}else if(event instanceof MoveItemEvent){

					MoveItemEvent moveItemEvent = (MoveItemEvent) event;

					sub.movedItems(moveItemEvent.getFileSourceModel().getIdentifier(), moveItemEvent.getTargetParentFileModel());

				}else if(event instanceof SwitchViewEvent){

					SwitchViewEvent switchView = (SwitchViewEvent) event;

					sub.switchView(switchView.getType());

				}else if(event instanceof RefreshFolderEvent){

					RefreshFolderEvent refresh = (RefreshFolderEvent) event;

					sub.refreshFolder(refresh.getFolderTarget(), refresh.isForceRefresh(), refresh.isForceReloadBreadCrumb());

				}else if(event instanceof SendMessageEvent){

					SendMessageEvent messageEvent = (SendMessageEvent) event;

					HashMap<String, String> hashFiles = new HashMap<String, String>();

					if(messageEvent.getListFileModelSelected()!=null){
						for (FileModel fileModel : messageEvent.getListFileModelSelected()) {
							hashFiles.put(fileModel.getIdentifier(), fileModel.getName());
						}
					}

					sub.createNewMessage(hashFiles); 	
				}else if(event instanceof FileDownloadEvent){

					FileDownloadEvent messageEvent = (FileDownloadEvent) event;

					sub.fileDownloaded(messageEvent.getItemIdentifier());

				}else if(event instanceof SessionExpiredEvent){

					sub.viewSessionExpiredPanel();

				}else if(event instanceof PasteItemEvent){

					PasteItemEvent pasteEvent = (PasteItemEvent) event; 
					sub.pasteEventIsCompleted(pasteEvent.isTreeRefreshable(), pasteEvent.getFolderDestinationId());

				}else if(event instanceof CopytemEvent){

					sub.copyEventIsCompleted();

				}else if(event instanceof TrashEvent){
					TrashEvent trashEvent = (TrashEvent) event;
					sub.trashEvent(trashEvent.getTrashOperation(), trashEvent.getTargetFileModels());
				
				}else if(event instanceof UpdatedVREPermissionEvent){
					UpdatedVREPermissionEvent vreEvent = (UpdatedVREPermissionEvent) event;
					sub.updatedVREPermissions(vreEvent.getVreFolderId());
				
				}else if(event instanceof UpdateWorkspaceSizeEvent){
					sub.updateWorksapaceSize(true);
				}
			}

	}

	/**
	 * Tree level is expanded.
	 *
	 * @param folderId the folder id
	 * @return true, if successful
	 */
	public boolean treeLevelIsExpanded(String folderId){
		return explorerPanel.getAsycTreePanel().isExpanded(folderId);
	}

	/**
	 * Gets the list parents.
	 *
	 * @param listParentModel the list parent model
	 * @param item the item
	 * @return the list parents
	 */
	private List<FileModel> getListParents(List<FileModel> listParentModel, FileModel item){

		getParents(listParentModel, item);

		Collections.reverse(listParentModel);

		return listParentModel;

	}

	/**
	 * Gets the parents.
	 *
	 * @param listParents the list parents
	 * @param item the item
	 * @return the parents
	 */
	private void getParents(List<FileModel> listParents, FileModel item){

		if(item==null || item.getParentFileModel()==null){
			return;
		}

		if(item.getParentFileModel().isRoot()){
			listParents.add(item.getParentFileModel());
			return;
		}
			
		listParents.add(item.getParentFileModel());
		getParents(listParents, item.getParentFileModel());
	}

	/**
	 * Subscribe.
	 *
	 * @param subscriber the subscriber
	 * @param keys the keys
	 */
	public void subscribe(SubscriberInterface subscriber, EventsTypeEnum[] keys)
	{
		for (EventsTypeEnum m : keys)
			subscribe(subscriber, m);
	}

	/**
	 * Subscribe.
	 *
	 * @param subscriber the subscriber
	 * @param key the key
	 */
	public void subscribe(SubscriberInterface subscriber, EventsTypeEnum key)
	{
		if (subscribers.containsKey(key))
			subscribers.get(key).add(subscriber);
		else
		{
			ArrayList<SubscriberInterface> subs = new ArrayList<SubscriberInterface>();
			subs.add(subscriber);
			subscribers.put(key, subs);
		}
	}

	/**
	 * Unsubscribe.
	 *
	 * @param subscriber the subscriber
	 * @param key the key
	 */
	public void unsubscribe(SubscriberInterface subscriber, EventsTypeEnum key)
	{
		if (subscribers.containsKey(key))
			subscribers.get(key).remove(subscriber);
	}

	/**
	 * Go.
	 *
	 * @param rootPanel the root panel
	 * @param onlyTree the only tree
	 * @param instancingSmartFolder the instancing smart folder
	 * @param instancingMessages the instancing messages
	 * @param selectRootItem the select root item
	 */
	public void go(final HasWidgets rootPanel, boolean onlyTree, boolean instancingSmartFolder, boolean instancingMessages, boolean selectRootItem) {

		if(onlyTree){
			this.selectRootItem = selectRootItem;
			this.explorerPanel = new ExplorerPanel(true,this.selectRootItem);
			this.selectRootItem = false; //set false select root item; only first time is used
			this.explorerPanel.setSize(400, 600);
			this.explorerPanel.getAsycTreePanel().setSizeTreePanel(350, 550);

//			rootPanel.add(new BasicDNDExample()); //it's example of drag&drop 
		}else
			this.explorerPanel = new ExplorerPanel(instancingSmartFolder,instancingMessages);


		rootPanel.add(explorerPanel);
	}

	/**
	 * Use method getPanel.
	 *
	 * @return ExplorerPanel
	 * @deprecated 
	 */
	public ExplorerPanel getTreePanel(){

		this.explorerPanel = getPanel();
		return this.explorerPanel;
	}

	/**
	 * Instance only Async Tree with specific width - height - select by default the root item .
	 *
	 * @param width the width
	 * @param height the height
	 * @return AsyncTreePanel
	 */

	public AsyncTreePanel getTree(int width, int height){

		this.explorerPanel = getPanel();
		this.explorerPanel.getAsycTreePanel().setSizeTreePanel(width, height);
		this.explorerPanel.getAsycTreePanel().setHeaderTreeVisible(false);		

		return explorerPanel.getAsycTreePanel();
	}

	/**
	 * Hide sharing facilities.
	 */
	public void hideSharingFacilities() {
		explorerPanel.getAsycTreePanel().getContextMenuTree().setHideSharing();
	}	

	/**
	 * Refresh root.
	 *
	 * @param selectRootItem the select root item
	 */
	public void refreshRoot(boolean selectRootItem){
		if(explorerPanel.getAsycTreePanel()!=null)
			explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot(selectRootItem);
	}


	/**
	 * Instance only Async Tree. Select by default root items of tree. 
	 * @return ExplorerPanel
	 */
	public ExplorerPanel getPanel(){
		this.explorerPanel = new ExplorerPanel(true, true);
		loadMyLogin();
		return this.explorerPanel;
	}


	/**
	 * Instance by default asyncronus Tree while Smart Folders and Messages was instanced as input value.
	 *
	 * @param instancingSmartFolder the instancing smart folder
	 * @param instancingMessages the instancing messages
	 * @param selectRootItem the select root item
	 * @return ExplorerPanel
	 */
	public ExplorerPanel getPanel(boolean instancingSmartFolder, boolean instancingMessages, boolean selectRootItem){

		this.explorerPanel = new ExplorerPanel(instancingSmartFolder, instancingMessages, selectRootItem);
		this.selectRootItem=selectRootItem;
		loadMyLogin();
		return this.explorerPanel;
	}
	
	/**
	 * Edi permissions.
	 *
	 * @param file the file
	 */
	private void ediPermissions(final FileModel file) {
		
		GWT.log("Edit Permissions on "+file);
		MultiDragConstants.HEADING_DIALOG = "Edit User/s permissions to: "+file.getName();
		MultiDragConstants.ALL_CONTACTS_LEFT_LIST = "Shared User/s";
		MultiDragConstants.SHARE_WITH_RIGHT_LIST = "Set permissions for User/s";
		
		final MultiDragContactsEditPermissions multiDragContact = new MultiDragContactsEditPermissions(LOAD_CONTACTS_AS.SHARED_USER, file.getIdentifier(), true);
		
		final DialogMultiDragContact multidrag = multiDragContact.getDialog();
		
		multidrag.setTxtHelp("Sets the permission for the user(s) dragged in the right list");
		
		multidrag.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {
			 
			@Override
			public void handleEvent(BaseEvent be) {
				final List<org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel> targets = multiDragContact.getTargetContacts();
				
				if(targets.size()==0){
					MessageBoxConfirm info = new MessageBoxConfirm("Any User/s?", "You have not selected any Users, confirm exit?");
					
					info.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

						public void handleEvent(MessageBoxEvent be) {
							//IF NOT CANCELLED
							String clickedButton = be.getButtonClicked().getItemId();
							if(clickedButton.equals(Dialog.YES)){
								multidrag.hide();
							}
						}
					});
				}

//				GWT.log(targets.toString());
				
				if(targets.size()>=1 && multiDragContact.getSelectedAcl()!=null){
					
					final List<String> logins = new ArrayList<String>(targets.size());
					
					for (org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel infoContactModel : targets) {
						GWT.log(infoContactModel.toString());
						logins.add(infoContactModel.getLogin());
					}

					rpcWorkspaceService.validateACLToUser(file.getIdentifier(), logins, multiDragContact.getSelectedAclID(), new AsyncCallback<ReportAssignmentACL>() {

						@Override
						public void onFailure(Throwable caught) {
							new MessageBoxAlert("Error", caught.getMessage(), null);
							
						}

						@Override
						public void onSuccess(ReportAssignmentACL result) {
							
							String msg = "";
							
							if(result.getErrors().size()>0){
								for (String error : result.getErrors()){
									msg+="<li>"+error +";</li><br/>";
								
								}
								
								new MessageBoxAlert("Warning!!", msg, null);
								return;
							}
							
							String names = "";
							for (String name : result.getValidLogins()) {
//								String name = infoContactModel.getName()!=null? infoContactModel.getName():infoContactModel.getLogin();
								names+="<li><i>"+name +";</i></li>";
							}

							msg+= "Setting permission '"+multiDragContact.getSelectedAcl().getLabel() +"' for: <ul>"+names+"</ul> confirm?";

							MessageBoxConfirm confirm = new MessageBoxConfirm("Setting new permissions to "+file.getName() +"?", msg);
							confirm.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

								public void handleEvent(MessageBoxEvent be) {

									//IF NOT CANCELLED
									String clickedButton = be.getButtonClicked().getItemId();
									if(clickedButton.equals(Dialog.YES)){
//										doAddAdministratorToFolderId(file, logins);
										setACLToFolderId(file.getIdentifier(), logins, multiDragContact.getSelectedAclID());
										multidrag.hide();
									}else if(clickedButton.equals(Dialog.CANCEL)){
										multidrag.hide();
									}
									
								}
							});
							
						}
					});
					
				}

			}
		});
		
		multidrag.show();
	}
	
	/**
	 * Show add administrators dialog.
	 *
	 * @param file the file
	 */
	private void showAddAdministratorsDialog(final FileModel file) {

		MultiDragConstants.HEADING_DIALOG = "Edit Administrator/s to: "+file.getName();
		MultiDragConstants.ALL_CONTACTS_LEFT_LIST = "All Contacts";
		MultiDragConstants.SHARE_WITH_RIGHT_LIST = "New Administrator/s";
		
		final SimpleMultiDragWorkspaceContact multiDragContact = new SimpleMultiDragWorkspaceContact(LOAD_CONTACTS_AS.ADMINISTRATOR, file.getIdentifier(), true, false, true);
		final Dialog multidrag = multiDragContact.getDialogMultiDragContact();
		
		multidrag.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {
			 
			@Override
			public void handleEvent(BaseEvent be) {
				final List<org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel> targets = multiDragContact.getTargetContactsWithMyLogin();
				
				if(targets.size()==1){
					MessageBoxConfirm info = new MessageBoxConfirm("Any Administrator/s?", "You have not selected any Administrator, confirm only you as Administrator and exit?");
					
					info.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

						public void handleEvent(MessageBoxEvent be) {
							//IF NOT CANCELLED
							String clickedButton = be.getButtonClicked().getItemId();
							if(clickedButton.equals(Dialog.YES)){
								List<String> logins = new ArrayList<String>(1);
								logins.add(targets.get(0).getLogin());
								doAddAdministratorToFolderId(file, logins);
								multidrag.hide();
							}
						}
					});
				}
				
				if(targets.size()>1){
					
					final List<String> logins = new ArrayList<String>(targets.size());
					String names = "<ul>";
					for (org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel infoContactModel : targets) {
						logins.add(infoContactModel.getLogin());
						names+="<li><i>"+infoContactModel.getName() +";</i></li>";
					}
					
					String tail = "</ul>as new ";
					tail += logins.size()>1?"administrators":"administrator";
					tail+= ", confirm?";
					MessageBoxConfirm confirm = new MessageBoxConfirm("Setting new Administrator/s?", "You have selected: <br/>"+names +tail);
					
					confirm.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

						public void handleEvent(MessageBoxEvent be) {

							//IF NOT CANCELLED
							String clickedButton = be.getButtonClicked().getItemId();
							if(clickedButton.equals(Dialog.YES)){
								doAddAdministratorToFolderId(file, logins);
								multidrag.hide();
							}
							if(clickedButton.equals(Dialog.CANCEL)){
								multidrag.hide();
							}
							
						}
					});

				}

			}
		});
		
		multidrag.show();
	}
	
	
	/**
	 * Do add administrator to folder id.
	 *
	 * @param file the file
	 * @param logins the logins
	 */
	private void doAddAdministratorToFolderId(final FileModel file, final List<String> logins) {
		
		rpcWorkspaceService.addAdministratorsByFolderId(file.getIdentifier(), logins, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable arg0) {
				new MessageBoxAlert("Error", arg0.getMessage(), null);
			}

			@Override
			public void onSuccess(Boolean arg0) {
				String msg =  "Updating administrator/s completed successfully";
				MessageBox.info("Operation completed", msg, null);
				
			}
		});
	}
	
	/**
	 * Sets the acl to folder id.
	 *
	 * @param folderId the folder id
	 * @param logins the logins
	 * @param aclTypeID the acl type id
	 */
	private void setACLToFolderId(final String folderId, final List<String> logins, String aclTypeID) {
		
		rpcWorkspaceService.setACLs(folderId, logins, aclTypeID, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Error", caught.getMessage(), null);
				
			}

			@Override
			public void onSuccess(Void result) {
				String msg =  "Updating permissions completed successfully";
				MessageBox.info("Operation completed", msg, null);
			}
		});
	}
	
	
	/**
	 * Load my login.
	 */
	private void loadMyLogin(){
		
		rpcWorkspaceService.getMyLogin(new AsyncCallback<UserBean>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error on loading my login is empty");
				myLogin = "";
				
			}

			@Override
			public void onSuccess(UserBean user) {
				GWT.log("My login is: "+user.getUsername());
				myLogin = user.getUsername();	
				myLoginFirstName = user.getFirstName();
			}
		});
	}


	/**
	 * Load item from workspace.
	 *
	 * @param itemIdentifier the item identifier
	 */
	private void loadItemFromWorkspace(final String itemIdentifier){


		explorerPanel.getAsycTreePanel().mask(ConstantsExplorer.LOADING,ConstantsExplorer.LOADINGSTYLE);

		rpcWorkspaceService.getChildrenSubTreeToRootByIdentifier(itemIdentifier, new AsyncCallback<ArrayList<SubTree>>() {

			@Override
			public void onFailure(Throwable caught) {
				explorerPanel.getAsycTreePanel().unmask();
				new MessageBoxAlert("Error", "Sorry - getChildrenSubTreeToRootByIdentifier", null);
				explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();
			}

			@Override
			public void onSuccess(ArrayList<SubTree> result) {

				boolean foundAncestor = false;
				// int index = 0;
				int i = 0;
				try{
					while (!foundAncestor) {

						if(i<result.size()){
							SubTree subTree = result.get(i); // get SubTree
							String folderModelId = subTree.getParentId(); // get folder id
							FolderModel folderInTree = (FolderModel) explorerPanel.getAsycTreePanel().getFileModelByIdentifier(folderModelId);

							if (folderInTree != null && explorerPanel.getAsycTreePanel().getChildrenNumber(folderInTree.getIdentifier()) == 0)
								foundAncestor = true;
							i++;
						}
						else
							break;
					}

					explorerPanel.getAsycTreePanel().setSubTreeLoaded(true);

					if(foundAncestor){

						for (int j = i - 1; j < result.size(); j++) {

							SubTree subTree = result.get(j); // get SubTree
							String folderModelId = subTree.getParentId(); // get folder id
							FolderModel folderInTree = (FolderModel) explorerPanel.getAsycTreePanel().getFileModelByIdentifier(folderModelId); // get folder in tree

							if (folderInTree.getParentFileModel() != null)
								subTree.getParent().setParentFileModel(folderInTree.getParentFileModel()); // set parent

							explorerPanel.getAsycTreePanel().addChildrenToFolder(subTree.getParent().getIdentifier(),subTree.getChildren()); // Add level
							explorerPanel.getAsycTreePanel().setExpandTreeLevel(subTree.getParent().getIdentifier(), true); // Expand level
						}

						ArrayList<FileModel> pathParentsList = (ArrayList<FileModel>) getListParentsByIdentifierFromTree(itemIdentifier);
						
						selectItemInTree(itemIdentifier);
						explorerPanel.getAsycTreePanel().setSubTreeLoaded(false);
						explorerPanel.getAsycTreePanel().unmask();
						eventBus.fireEvent(new SubTreeLoadedEvent(pathParentsList));

					}

				}catch (Exception e) {
					new MessageBoxAlert("Error", "Sorry, e "+e.getMessage(), null);
				}
			}

		});
	}


	/**
	 * Deselect current selection.
	 */
	private void deselectCurrentSelection(){
		FileModel fileModelSelected = explorerPanel.getAsycTreePanel().getSelectedFileModelItem();
		if(fileModelSelected!=null)
			explorerPanel.getAsycTreePanel().deselectItem(fileModelSelected);
	}

	//********METHODS TO NOTIFY TREE
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.TreeAppControllerInterface#getListParentsByIdentifierFromTree(java.lang.String)
	 */
	@Override
	public List<FileModel> getListParentsByIdentifierFromTree(String itemIdentifier) {

		List<FileModel> listParentModel = new ArrayList<FileModel>();

		FileModel item = this.explorerPanel.getAsycTreePanel().getFileModelByIdentifier(itemIdentifier);

		if(item==null){	
			return null;
		}
		else{
			return getListParents(listParentModel,item);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.TreeAppControllerInterface#renameItem(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean renameItem(String itemIdentifier, String newName, String extension) {
		return this.explorerPanel.getAsycTreePanel().renameItem(itemIdentifier, newName, extension);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.TreeAppControllerInterface#deleteItem(java.lang.String)
	 */
	@Override
	public boolean deleteItem(String itemIdentifier) {
		return this.explorerPanel.getAsycTreePanel().deleteItem(itemIdentifier);
	}

	/**
	 * method not used.
	 *
	 * @param itemIdentifier the item identifier
	 * @param name the name
	 * @param parentIdentifier the parent identifier
	 * @return true, if successful
	 */
	@Override
	public boolean addFolder(String itemIdentifier, String name, String parentIdentifier) {
		return false; //not used
	}

	/**
	 * method not used.
	 *
	 * @param itemIdentifier the item identifier
	 * @param name the name
	 * @param parentIdentifier the parent identifier
	 * @return true, if successful
	 */
	@Override
	public boolean addFile(String itemIdentifier, String name, String parentIdentifier) {
		return false; 
	}

	/**
	 * method not used.
	 *
	 * @param itemIdentifier the item identifier
	 * @return true, if successful
	 */
	@Override
	public boolean reloadFolderChildren(String itemIdentifier) {
		return false;
	}

	/**
	 * method not used.
	 *
	 * @param type the new visualization type
	 */
	@Override
	public void setVisualizationType(VisualizationType type) {
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.TreeAppControllerInterface#selectRootItem()
	 */
	@Override
	public void selectRootItem(){
		this.explorerPanel.getAsycTreePanel().selectRootItem();
	}

	/**
	 * Gets the selected folder in tree.
	 *
	 * @return FileModel parent selected or root item if no directory is selected
	 */
	@Override
	public FileModel getSelectedFolderInTree(){

		FileModel fileModel = this.explorerPanel.getAsycTreePanel().getSelectedFileModelItem();

		if(fileModel.isDirectory())
			return fileModel;
		else
			return fileModel.getParentFileModel();	
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.TreeAppControllerInterface#findItemAndSelectItemInTree(java.lang.String)
	 */
	@Override
	public void findItemAndSelectItemInTree(String itemIdentifier) {

		FileModel fileModel = this.explorerPanel.getAsycTreePanel().getFileModelByIdentifier(itemIdentifier);

		if(fileModel==null) { //Loading item by RPC

			loadItemFromWorkspace(itemIdentifier);
		}

		else{

			selectItemInTree(itemIdentifier);
		}
	}

	/**
	 * Select item in tree.
	 *
	 * @param itemIdentifier the item identifier
	 */
	private void selectItemInTree(String itemIdentifier){

		FileModel fileModel = this.explorerPanel.getAsycTreePanel().getFileModelByIdentifier(itemIdentifier);

		if(fileModel!=null){

			if(explorerPanel.getAsycTreePanel().isSearch())
				deselectCurrentSelection();

			if(fileModel.isDirectory()){
				if(fileModel.getParentFileModel()!=null)
					this.explorerPanel.getAsycTreePanel().setExpandTreeLevel(fileModel.getParentFileModel().getIdentifier(), true); //expand parent folder

				this.explorerPanel.getAsycTreePanel().selectItem(itemIdentifier);

			}else
				this.explorerPanel.getAsycTreePanel().selectItem(itemIdentifier); //select item
		}
	}

	public AsyncCallback<WindowOpenParameter> downloadHandlerCallback = new AsyncCallback<WindowOpenParameter>() {

		@Override
		public void onFailure(Throwable caught) {
			explorerPanel.getAsycTreePanel().unmask();
			new MessageBoxAlert("Error", caught.getMessage(), null);
			explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();
		}

		@Override
		public void onSuccess(WindowOpenParameter windowOpenParam) {
			String params = "?"+windowOpenParam.getParameters();

			if(params.length()>1)
				params+="&";

			params+=ConstantsExplorer.REDIRECTONERROR+"="+windowOpenParam.isRedirectOnError();

			windowOpenParam.getBrowserWindow().setUrl(ConstantsExplorer.DOWNLOAD_WORKSPACE_SERVICE+params);


			//			com.google.gwt.user.client.Window.open(ConstantsExplorer.DOWNLOAD_WORKSPACE_SERVICE+params, windowOpenParam.getOption(), "");	
		}
	};

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.TreeAppControllerInterface#expandFolder(java.lang.String)
	 */
	@Override
	/**
	 * Called when path item is clicked
	 */
	public void expandFolder(final String itemIdentifier){
		searching(false);

		FileModel fileModel = this.explorerPanel.getAsycTreePanel().getFileModelByIdentifier(itemIdentifier);

		if(fileModel==null) { //Loading by RPC
			loadItemFromWorkspace(itemIdentifier);
		}
		else{
			deselectCurrentSelection();
			this.explorerPanel.getAsycTreePanel().selectItem(itemIdentifier); //select item
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.TreeAppControllerInterface#searching(boolean)
	 */
	@Override
	public void searching(boolean isSearch){
		//		deselecteCurrentSelection();
		explorerPanel.getAsycTreePanel().setSearch(isSearch);
	}

	/**
	 * Do click url.
	 *
	 * @param openUrlEvent the open url event
	 */
	public void doClickUrl(OpenUrlEvent openUrlEvent) {

		final FileModel fileModel = openUrlEvent.getSourceFileModel();

		if(fileModel==null)
			return;
		
		GWT.log("do click url");
		final NewBrowserWindow newBrowserWindow = NewBrowserWindow.open("", "_blank", "");
		
		rpcWorkspaceService.getUrlById(fileModel.getIdentifier(), fileModel.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.URL_DOCUMENT), false, new AsyncCallback<GWTWorkspaceItem>() {

			@Override
			public void onFailure(Throwable caught) {
				explorerPanel.getAsycTreePanel().unmask();
				new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " clicking url.", null);
				explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();

			}

			@Override
			public void onSuccess(GWTWorkspaceItem result) {


				if(fileModel.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.URL_DOCUMENT)){
					newBrowserWindow.setUrl(((GWTUrlDocument) result).getUrl());
//					new WindowOpenUrl(((GWTUrlDocument) result).getUrl(), "_blank", "");
					GWT.log("URL_DOCUMENT Open " + ((GWTUrlDocument) result).getUrl());
				}
				else{
//					new WindowOpenUrl(((GWTExternalUrl) result).getUrl(), "_blank", "");
					newBrowserWindow.setUrl(((GWTExternalUrl) result).getUrl());
					GWT.log("ExternalUrl Open " + ((GWTExternalUrl) result).getUrl());
				}

			}

		});

		//				com.google.gwt.user.client.Window.open(ConstantsExplorer.DOWNLOAD_WORKSPACE_SERVICE+"?id="+fileModel.getIdentifier()+"&viewContent=true", fileModel.getName(), "");

	}
}

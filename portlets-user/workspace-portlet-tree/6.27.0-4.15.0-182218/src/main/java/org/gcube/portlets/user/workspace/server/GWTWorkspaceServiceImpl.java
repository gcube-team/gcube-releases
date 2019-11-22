package org.gcube.portlets.user.workspace.server;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.model.exceptions.UserNotAuthorizedException;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.shared.tohl.TrashedItem;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFileVersion;
import org.gcube.common.storagehubwrapper.shared.tohl.items.ImageFileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.PropertyMap;
import org.gcube.common.storagehubwrapper.shared.tohl.items.URLFileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.URLItem;
import org.gcube.portal.wssynclibrary.shared.ItemNotSynched;
import org.gcube.portal.wssynclibrary.shared.WorkspaceFolderLocked;
import org.gcube.portal.wssynclibrary.shared.thredds.Sync_Status;
import org.gcube.portal.wssynclibrary.thredds.WorkspaceThreddsSynchronize;
import org.gcube.portlets.user.urlshortener.UrlShortener;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.model.GcubeVRE;
import org.gcube.portlets.user.workspace.client.model.SubTree;
import org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalUrl;
import org.gcube.portlets.user.workspace.server.notifications.tostoragehub.NotificationStorageHubUtil;
import org.gcube.portlets.user.workspace.server.notifications.tostoragehub.NotificationsProducerToStorageHub;
import org.gcube.portlets.user.workspace.server.reader.ApplicationProfile;
import org.gcube.portlets.user.workspace.server.reader.ApplicationProfileReader;
import org.gcube.portlets.user.workspace.server.tostoragehub.FormatterUtil;
import org.gcube.portlets.user.workspace.server.tostoragehub.ObjectStorageHubToWorkpaceMapper;
import org.gcube.portlets.user.workspace.server.tostoragehub.StorageHubToWorkpaceConverter;
import org.gcube.portlets.user.workspace.server.util.PortalContextInfo;
import org.gcube.portlets.user.workspace.server.util.StringUtil;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.user.workspace.server.util.resource.PropertySpecialFolderReader;
import org.gcube.portlets.user.workspace.server.util.scope.ScopeUtilFilter;
import org.gcube.portlets.user.workspace.shared.GarbageItem;
import org.gcube.portlets.user.workspace.shared.PublicLink;
import org.gcube.portlets.user.workspace.shared.SHUBOperationNotAllowedException;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.portlets.user.workspace.shared.TrashContent;
import org.gcube.portlets.user.workspace.shared.TrashOperationContent;
import org.gcube.portlets.user.workspace.shared.UserBean;
import org.gcube.portlets.user.workspace.shared.WorkspaceOperationResult;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;
import org.gcube.portlets.user.workspace.shared.WorkspaceUserQuote;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingEntryType;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;
import org.gcube.portlets.widgets.workspacesharingwidget.server.notifications.NotificationsProducer;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * The Class GWTWorkspaceServiceImpl.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Aug 1, 2019
 */
public class GWTWorkspaceServiceImpl extends RemoteServiceServlet implements GWTWorkspaceService {

	protected static final String IDENTIFIER_IS_NULL = "Identifier is null";
	protected static final String RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST = " retrieving item. Either the item doesn't exist anymore or you do not have the permission to access it";
	private static final long serialVersionUID = 2828885661214875589L;
	public static final String LAST_OPEN_FOLDER_ATTRIBUTE = "WORKSPACE.LAST_OPEN_FOLDER";
	public static final String SELECTION_STATE_ATTRIBUTE = "WORKSPACE.SELECTION_STATE";
	protected Logger workspaceLogger = LoggerFactory.getLogger(GWTWorkspaceServiceImpl.class);

	/**
	 * Gets the notification producer.
	 *
	 * @return the notification producer
	 */
	protected NotificationsProducer getNotificationProducer() {

		return WsUtil.getNotificationProducer(this.getThreadLocalRequest());
	}

	/**
	 * Gets the scope util filter.
	 *
	 * @return the scope util filter
	 */
	protected ScopeUtilFilter getScopeUtilFilter() {

		return WsUtil.getScopeUtilFilter(this.getThreadLocalRequest());
	}

	/**
	 * Checks if is test mode.
	 *
	 * @return true, if is test mode
	 */
	protected boolean isTestMode() {
		return !WsUtil.isWithinPortal();
	}

	/**
	 * Gets the url shortener.
	 *
	 * @return the url shortener
	 */
	protected UrlShortener getUrlShortener() {
		return WsUtil.getUrlShortener(this.getThreadLocalRequest());
	}
	
	/**
	 * Gets the property special folder reader.
	 *
	 * @return the property special folder reader
	 */
	protected PropertySpecialFolderReader getPropertySpecialFolderReader() {
		String absolutePathProperty = getSpecialFolderPath();
		return WsUtil.getPropertySpecialFolderReader(this.getThreadLocalRequest(), absolutePathProperty);
	}

	/**
	 * Gets the special folder path.
	 *
	 * @return the Category if there is correspondance, null otherwise
	 */
	private String getSpecialFolderPath() {
		ServletContext servletContext = getServletContext();
		String contextPath = servletContext.getRealPath(File.separator);
		return contextPath + File.separator + "conf" + File.separator
				+ ConstantsExplorer.SPECIALFOLDERNAMEPROPERTIESFILE;
	}

	
	/**
	 * Gets the name for special folder.
	 *
	 * @return the name for special folder
	 */
	private String getNameForSpecialFolder() {

		PropertySpecialFolderReader sfReader = getPropertySpecialFolderReader();

		if (sfReader == null) {
			workspaceLogger.warn("Reader is null, skypping set to special folder name");
			return "";
		}

		workspaceLogger
				.info("Read special folder name: '" + sfReader.getSpecialFolderName() + "', from property file..");
		return sfReader.getSpecialFolderName();
	}

	/**
	 * Gets the servlet context path.
	 *
	 * @param protocol
	 *            the protocol
	 * @return the servlet context path
	 */
	@Override
	public String getServletContextPath(String protocol) {
		HttpServletRequest req = getThreadLocalRequest();

		String scheme = protocol;

		String serverName = req.getServerName(); // hostname.com
		int serverPort = req.getServerPort(); // 80
		String contextPath = req.getServletContext().getContextPath(); // /mywebapp

		// Reconstruct original requesting URL
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("//").append(serverName);

		if (serverPort != 80 && serverPort != 443) {
			url.append(":").append(serverPort);
		}

		workspaceLogger.debug("server: " + url);
		workspaceLogger.debug("omitted contextPath: " + contextPath);

		url.append(contextPath);
		workspaceLogger.debug("getServletContextPath=" + url.toString());
		return url.toString();
	}

	
	/**
	 * Checks if is item under sync.
	 *
	 * @param itemId
	 *            the item id
	 * @return true, if is item under sync
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public Boolean isItemUnderSync(String itemId) throws Exception {

		try {
			GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
			try {
				Sync_Status status = WorkspaceThreddsSynchronize.getInstance().getSynchedStatusFromItemProperty(itemId,
						user.getUsername());
			} catch (Exception e) {
				return false;
			}
			// HERE THE ITEM IS SYNCHED SO CHECK IF IT IS LOCKED
			checkItemLocked(itemId);
			return false;

		} catch (WorkspaceFolderLocked e1) {
			return true;
		} catch (Exception e) {
			throw new Exception("Error on checking item " + itemId + " is under sync");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getMyLogin()
	 */
	/**
	 * Gets the my login.
	 *
	 * @param currentPortletUrl
	 *            the current portlet url
	 * @return the my login
	 */
	@Override
	public UserBean getMyLogin(String currentPortletUrl) {
		
		PortalContextInfo info = WsUtil.getPortalContext(this.getThreadLocalRequest());
		String username = info.getUsername();
		String email = info.getUserEmail();
		String firstName = "Testing";
		String lastName = "User";

		if (isWithinPortal() && username != null) {
			try {
				LiferayUserManager l = new LiferayUserManager();
				GCubeUser user = l.getUserByUsername(username);
				firstName = user.getFirstName();
				lastName = user.getLastName();
				email = user.getEmail();
			} catch (UserManagementSystemException e) {
				workspaceLogger.error("UserManagementSystemException for username: " + username);
			} catch (UserRetrievalFault e) {
				workspaceLogger.error("UserRetrievalFault for username: " + username);

			} catch (Exception e) {
				workspaceLogger.error("Error during getMyLogin for username: " + username, e);
			}

		}

		UserBean us = new UserBean(username, firstName, lastName, email);
		workspaceLogger.info("Returning myLogin: " + us);
		return us;
	}


	/**
	 * Gets the CLARIN switch board endpoint.
	 *
	 * @return the CLARIN switch board endpoint
	 */
	private String getCLARINSwitchBoardEndpoint() {
		// save the context for this resource
		String currContext = ScopeProvider.instance.get();
		// set the context for this resource
		ScopeProvider.instance.set("/" + PortalContext.getConfiguration().getInfrastructureName());

		// construct the xquery
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition(
				"$resource/Profile/Name/text() eq '" + ConstantsExplorer.CLARIN_SWITCHBOARD_ENDPOINT_NAME + "'");
		query.addCondition("$resource/Profile/Category/text() eq '"
				+ ConstantsExplorer.CLARIN_SWITCHBOARD_ENDPOINT_CATEGORY + "'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> conf = client.submit(query);
		if (conf == null || conf.isEmpty())
			return null;
		ServiceEndpoint res = conf.get(0);
		// reset the context
		ScopeProvider.instance.set(currContext);
		return res.profile().runtime().hostedOn();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getMyLogin()
	 */
	/**
	 * Gets the my first name.
	 *
	 * @return the my first name
	 */
	@Override
	public String getMyFirstName() {
		if (!isWithinPortal())
			return "";

		PortalContextInfo info = WsUtil.getPortalContext(this.getThreadLocalRequest());
		String username = info.getUsername();
		String firstName = "";
		if (isWithinPortal() && username != null) {
			try {
				LiferayUserManager l = new LiferayUserManager();
				GCubeUser user = l.getUserByUsername(username);
				workspaceLogger.info("My login first name is: " + user.getFirstName());
				firstName = user.getFirstName();
			} catch (UserManagementSystemException e) {
				workspaceLogger.error("UserManagementSystemException for username: " + username);
			} catch (UserRetrievalFault e) {
				workspaceLogger.error("UserRetrievalFault for username: " + username);
			}

		}
		return firstName;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * isSessionExpired()
	 */
	/**
	 * Checks if is session expired.
	 *
	 * @return true, if is session expired
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public boolean isSessionExpired() throws Exception {
		return WsUtil.isSessionExpired(this.getThreadLocalRequest());
	}

	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} catch (Exception ex) {
			workspaceLogger.trace("Development Mode ON");
			return false;
		}
	}


	/**
	 * Gets the workspace from storage hub.
	 *
	 * @return the workspace from storage hub
	 * @throws Exception
	 *             the exception
	 */
	protected org.gcube.common.storagehubwrapper.server.tohl.Workspace getWorkspaceFromStorageHub() throws Exception {
		GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
		StorageHubWrapper storageHubWrapper = WsUtil.getStorageHubWrapper(this.getThreadLocalRequest(), null, user);
		return storageHubWrapper.getWorkspace();
	}

	/**
	 * Gets the storage hub to workpace converter.
	 *
	 * @return the storage hub to workpace converter
	 * @throws Exception
	 *             the exception
	 */
	protected StorageHubToWorkpaceConverter getStorageHubToWorkpaceConverter() throws Exception {
		GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
		StorageHubToWorkpaceConverter converter = WsUtil.getStorageHubToWorkpaceConverter(this.getThreadLocalRequest(),
				null, user);
		// SETTING ROOT ID JUST ONCE
		if (converter.getWorkspaceRootId() == null) {
			org.gcube.common.storagehubwrapper.server.tohl.Workspace ws = getWorkspaceFromStorageHub();
			converter.setWorkspaceRootId(ws.getRoot().getId());
		}

		return converter;
	}

	/**
	 * Gets the notification producer to storage hub.
	 *
	 * @return the notification producer to storage hub
	 */
	protected NotificationsProducerToStorageHub getNotificationProducerToStorageHub() {

		return WsUtil.getNotificationProducerToStorageHub(this.getThreadLocalRequest());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getRootForTree()
	 */
	/**
	 * Gets the root for tree.
	 *
	 * @return the root for tree
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public FolderModel getRootForTree() throws Exception {

		workspaceLogger.trace("getRoot");

		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder root = workspace.getRoot();

			if (root == null) {
				workspaceLogger.error("The root is null");
				throw new Exception("The root is null");
			}

			workspaceLogger.trace("Root converted, returning...");
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			return converter.toRootFolder(root);
		} catch (Exception e) {
			workspaceLogger.error("Error in server during root retrieving", e);
			// workspaceLogger.trace("Error in server During root retrieving " +
			// e);

			// GWT can't serialize all exceptions
			throw new Exception("Error during workspace loading, please contact the support. Exception:" + e);
		}
	}

	/**
	 * Delete item.
	 *
	 * @param itemId
	 *            the item id
	 * @return the boolean
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public Boolean deleteItem(String itemId) throws Exception {

		org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem theItem = null;
		try {
			
			workspaceLogger.error("called deleteItem: "+itemId);
			if (itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			checkItemLocked(itemId);

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspaceSH = getWorkspaceFromStorageHub();
			theItem = workspaceSH.getItem(itemId);
			String itemName = theItem.getName();
			String sourceFolderSharedId = null;

			if (theItem.isShared()) {
				org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem rootSharedFolder = workspaceSH
						.getRootSharedFolder(itemId);
				sourceFolderSharedId = rootSharedFolder.getId();
			}

			// HERE REMOVING THE ITEM
			workspaceLogger.info("Calling storageHub to delete item with id: " + itemId);
			workspaceSH.deleteItem(itemId);
			
			NotificationsProducerToStorageHub np = getNotificationProducerToStorageHub();
			if (sourceFolderSharedId != null)
				NotificationStorageHubUtil.checkSendNotifyRemovedItemFromShare(this.getThreadLocalRequest(), theItem.isShared(),
						itemName, itemId, sourceFolderSharedId, workspaceSH, np);

			return Boolean.TRUE;

		} catch (WorkspaceFolderLocked e1) {
			throw new Exception(e1.getMessage());

//		} catch (InsufficientPrivilegesException e) {
//			workspaceLogger.error("Error in server Item remove", e);
//			String error = "Insufficient Privileges to remove the item";
//			throw new Exception(error);
//
//		} catch (ItemNotFoundException e) {
//			String error = "An error occurred on deleting item. " + ConstantsExplorer.ERROR_ITEM_DOES_NOT_EXIST;
//			workspaceLogger.error(error, e);
//			throw new Exception(error);
//			
//			//TO STORAGEHUB EXCEPTION	
//		} catch (UserNotAuthorizedException e) {
//			String error = "Insufficient Privileges to delete the item";
//			workspaceLogger.error(error, e);
//			throw new Exception(error);
		} catch (Exception e) {
			workspaceLogger.error("Removing item error:", e);
			StringBuilder error = new StringBuilder();
			if(theItem!=null && theItem.isShared()) {
				if(theItem.isFolder()) {
					error.append("Deleting shared folders is not supported. Please unshare it if your intent is to no longer share its content with your coworkers.");
				}else {
					error.append("Ops! This operation is not allowed, we're working hard to make this possible soon.");
				}
				throw new SHUBOperationNotAllowedException(error.toString());
			}else {
				error.append("Ops an error occurred deleting the item! Either you have not the permission to delete it or a server error occurred. Please, refresh and try again");
				throw new Exception(error.toString());
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getFolderChildren(org.gcube.portlets.user.workspace.client.model.
	 * FolderModel)
	 */
	/**
	 * Gets the folder children.
	 *
	 * @param folder
	 *            the folder
	 * @return the folder children
	 * @throws Exception
	 *             the exception
	 * @throws SessionExpiredException
	 *             the session expired exception
	 */
	@Override
	public List<FileModel> getFolderChildren(FolderModel folder) throws Exception, SessionExpiredException {
		
		org.gcube.common.storagehubwrapper.server.tohl.Workspace shWorkspace;

		if (isSessionExpired())
			throw new SessionExpiredException();

		try {

			if (folder == null)
				throw new Exception("Folder is null");

			workspaceLogger.info("Get FolderChildren called for folder: "+folder.getIdentifier());
			shWorkspace = getWorkspaceFromStorageHub();

			// REMEMBER wsItem.isRoot() is always false;

			// REQUIRING ONLY THE FOLDERS
			List<? extends org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem> children = shWorkspace
					.getFilteredChildren(folder.getIdentifier(),
							org.gcube.common.storagehub.model.items.FolderItem.class);

			workspaceLogger.info("The children are: "+children.size());
			
			for (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem workspaceItem : children) {
				workspaceLogger
						.trace("The ITEM: " + workspaceItem.getName() + ", is shared: " + workspaceItem.isShared()
								+ ", is folder: " + workspaceItem.isFolder() + " the id: " + workspaceItem.getId());
			}

			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			List<FileModel> listFileModels = new ArrayList<FileModel>(children.size());

			// boolean isParentShared =
			// workspace.isItemShared(folder.getIdentifier()); //removed for
			// optimization
			boolean isParentShared = folder.isShared();
			for (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem workspaceItem : children) {
				// TO BE SURE IT IS A FOLDER
				// if(workspaceItem.isFolder()){
				workspaceLogger
						.debug("Converting tree folder: " + workspaceItem.getId() + " name " + workspaceItem.getName());
				listFileModels.add(converter.toTreeFileModel(workspaceItem, folder, isParentShared));
				// }
			}

			boolean isRoot = WsUtil.isRootFolder(folder, converter);
			
			workspaceLogger.info("Is the workspace folder "+folder.getName() + "with id: "+folder.getIdentifier()+" the root? "+isRoot);

			// ADDING VRE FOLDER?
			if (isRoot) {
				folder.setIsRoot(true);
				// ADD VRE FOLDER
				try {

					String vreFolderId = shWorkspace.getVREFoldersId();
					org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem baseVREFolder = shWorkspace
							.getItem(vreFolderId);
					FileModel specialFolderModel = converter.toTreeFileModel(baseVREFolder, folder, false);

					specialFolderModel.setSpecialFolder(true);
					String newName = getNameForSpecialFolder();

					if (!newName.isEmpty()) {
						workspaceLogger.info("Special folder name updated as: " + newName);
						specialFolderModel.setName(newName);
					} else
						workspaceLogger.info("Special folder name is empty, skipping");

					listFileModels.add(specialFolderModel);

				} catch (Exception e) {
					workspaceLogger.warn(
							"An error occurred on retrieving special folders for folder id: " + folder.getIdentifier(),
							e);
				}
			}

			workspaceLogger.info("Returning " + listFileModels.size() + " tree item/s");
			/*
			 * int i = 0; for (FileModel fileModel : listFileModels) {
			 * System.out.println(i++ +")"+fileModel); }
			 */

			// if(!WsUtil.isWithinPortal()){
			// workspaceLogger.trace("Sleeping 4 sec...");
			// Thread.sleep(4000);
			// }
			return listFileModels;

		} catch (Exception e) {
			workspaceLogger.error("Error in server During item retrieving", e);
			// workspaceLogger.trace("Error in server During item retrieving " +
			// e);
			String error = ConstantsExplorer.SERVER_ERROR + RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST;
			// GWT can't serialize all exceptions
			throw new Exception(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getFolderChildrenForFileGrid(org.gcube.portlets.user.workspace.client.
	 * model.FileModel)
	 */
	/**
	 * Gets the folder children for file grid.
	 *
	 * @param folder
	 *            the folder
	 * @return the folder children for file grid
	 * @throws Exception
	 *             the exception
	 * @throws SessionExpiredException
	 *             the session expired exceptionworkspaceLogger.info("The children are: "+children.size());
	 */
	@Override
	public List<FileGridModel> getFolderChildrenForFileGrid(FileModel folder)
			throws Exception, SessionExpiredException {
	
		
		if (isSessionExpired())
			throw new SessionExpiredException();

		try {

			if (folder == null)
				throw new Exception("Folder is null");
			
			workspaceLogger.info("Get FolderChildrenForFileGrid called for folder: "+folder.getIdentifier());
			org.gcube.common.storagehubwrapper.server.tohl.Workspace shWorkspace = getWorkspaceFromStorageHub();
			List<? extends org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem> children = shWorkspace
					.getChildren(folder.getIdentifier());

			workspaceLogger.info("The children are: "+children.size());
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			List<FileGridModel> listFileGridModels = new ArrayList<FileGridModel>(children.size());
			// boolean isParentShared = folder.isShared();
			for (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem workspaceItem : children) {
				workspaceLogger
						.debug("Converting grid item: " + workspaceItem.getId() + " name " + workspaceItem.getName());
				listFileGridModels.add(converter.toGridFileModel(workspaceItem, folder));
			}

			boolean isRoot = WsUtil.isRootFolder(folder, converter);
			workspaceLogger.debug("****** IS ROOT? " + isRoot);
			// ADDING VRE FOLDER?
			if (isRoot) {
				folder.setIsRoot(true);
				// ADD VRE FOLDER
				try {

					String vreFolderId = shWorkspace.getVREFoldersId();
					org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem baseVREFolder = shWorkspace.getItem(vreFolderId);
					FileGridModel specialFolderModel = converter.toGridFileModel(baseVREFolder, folder);

					specialFolderModel.setSpecialFolder(true);
					String newName = getNameForSpecialFolder();

					if (!newName.isEmpty()) {
						workspaceLogger.info("Special folder name updated as: " + newName);
						specialFolderModel.setName(newName);
					} else
						workspaceLogger.info("Special folder name is empty, skipping");

					listFileGridModels.add(specialFolderModel);

				} catch (Exception e) {
					workspaceLogger.warn(
							"An error occurred on retrieving special folders for folder id: " + folder.getIdentifier(),
							e);
				}
			}

			return listFileGridModels;

		} catch (Exception e) {
			workspaceLogger.error("Error in server During items retrieving", e);
			String error = ConstantsExplorer.SERVER_ERROR + RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST;
			// GWT can't serialize all exceptions
			throw new Exception(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getFolderChildrenForFileGridById(java.lang.String)
	 */
	/**
	 * Gets the folder children for file grid by id.
	 *
	 * @param folderId
	 *            the folder id
	 * @return the folder children for file grid by id
	 * @throws Exception
	 *             the exception
	 * @throws SessionExpiredException
	 *             the session expired exception
	 */
	@Override
	public List<FileGridModel> getFolderChildrenForFileGridById(String folderId)
			throws Exception, SessionExpiredException {

		if (isSessionExpired())
			throw new SessionExpiredException();

		try {

			if (folderId == null || folderId.isEmpty())
				throw new Exception("Folder id is null or empty");

			org.gcube.common.storagehubwrapper.server.tohl.Workspace shWorkspace = getWorkspaceFromStorageHub();
			workspaceLogger.trace("get children for Grid by id: " + folderId);
			List<FileGridModel> listFileGridModels = new ArrayList<FileGridModel>();

			// BUILDING THE PARENT
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem = shWorkspace.getItem(folderId);
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder parent;

			if (wsItem.isFolder()) {
				workspaceLogger.trace("item id: " + folderId + " is of type: " + wsItem.getType());
				parent = (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) wsItem;

			} else {
				workspaceLogger.trace(
						"item id: " + folderId + " is not a folder but of type: " + wsItem.getType() + ", get parent");
				parent = (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) shWorkspace
						.getItem(wsItem.getParentId());
			}

			if (parent == null)
				return listFileGridModels;

			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			FileGridModel wsParent = converter.toGridFileModel(parent, null);

			// PARENT BUILT IS SHARED?
			if (parent.isShared()) {
				wsParent.setShared(true);
				wsParent.setShareable(false);
			}

			Long startTime = System.currentTimeMillis();

			// GET CHILDREN
			List<? extends org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem> children = shWorkspace
					.getChildren(wsParent.getIdentifier());
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			workspaceLogger.debug("grid getChildren() returning " + children.size() + " elements in " + time);
			// boolean isParentShared = folder.isShared();
			for (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem workspaceItem : children) {
				workspaceLogger
						.debug("Converting grid item: " + workspaceItem.getId() + " name " + workspaceItem.getName());
				listFileGridModels.add(converter.toGridFileModel(workspaceItem, wsParent));
			}
			workspaceLogger.info("All converted grid item/s is/are: " + listFileGridModels.size());
			return listFileGridModels;

		} catch (Exception e) {
			workspaceLogger.error("Error in server During items retrieving", e);
			String error = ConstantsExplorer.SERVER_ERROR + RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST;
			throw new Exception(error);
		}
	}

	/**
	 * Gets the trash content.
	 *
	 * @return the trash content
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<FileTrashedModel> getTrashContent() throws Exception {
		workspaceLogger.trace("Get TrashContent: ");

		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem baseTrashFolder = workspace.getTrash();
			List<? extends org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem> trashChildren = workspace
					.getChildren(baseTrashFolder.getId());
			List<FileTrashedModel> trashContent = new ArrayList<FileTrashedModel>(trashChildren.size());
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			for (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem workspaceItem : trashChildren) {
				if (workspaceItem.isTrashed()) {
					trashContent.add(converter.toFileTrashedModel((TrashedItem) workspaceItem));
				} else
					workspaceLogger.warn("The item: " + workspaceItem.getId() + " is not trashed");
			}

			return trashContent;

		} catch (Exception e) {
			workspaceLogger.error("Error in server TrashConten", e);
			String error = ConstantsExplorer.SERVER_ERROR + " get Trash content. " + e.getMessage();
			throw new Exception(error);
		}
	}

	/**
	 * Update trash content.
	 *
	 * @param operation
	 *            the operation
	 * @return the trash content
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public TrashContent updateTrashContent(WorkspaceTrashOperation operation) throws Exception {

		workspaceLogger.info("Updating TrashContent with operation: " + operation);

		try {
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			TrashContent result = new TrashContent();

			switch (operation) {

			case EMPTY_TRASH:
				workspace.emptyTrash();
				// case RESTORE_ALL:
				// //listErrors = trash.restoreAll();
				// //workspace.re
				// break;
			case REFRESH:
			default:
				result.setTrashContent(getTrashContent()); // THIS WORKING WITH
															// STORAGE-HUB
				return result;
			}

		} catch (Exception e) {
			workspaceLogger.error("Error in server TrashContent", e);
			String error = ConstantsExplorer.SERVER_ERROR + " update Trash content. " + e.getMessage();
			throw new Exception(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * executeOperationOnTrash(java.util.List,
	 * org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation)
	 */
	/**
	 * Execute operation on trash.
	 *
	 * @param listTrashItemIds
	 *            the list trash item ids
	 * @param operation
	 *            the operation
	 * @return the trash operation content
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public TrashOperationContent executeOperationOnTrash(List<String> listTrashItemIds,
			WorkspaceTrashOperation operation) throws Exception {

		workspaceLogger.info("Get TrashContent, operation: " + operation);

		if (listTrashItemIds == null || listTrashItemIds.size() == 0)
			throw new Exception("List of Trash item ids is null or empty");

		List<FileTrashedModel> listContentError = new ArrayList<FileTrashedModel>();
		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();

			// WorkspaceTrashFolder trash = workspace.getTrash();
			TrashOperationContent result = new TrashOperationContent();
			result.setOperation(operation);

			List<String> listUpdatedTrashIds = new ArrayList<String>();

			switch (operation) {

			case DELETE_PERMANENTLY: {

				boolean deleted = false;
				for (String trashItemId : listTrashItemIds) {
					try {
						org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem trashedItem = workspace
								.getItem(trashItemId);
						if (trashedItem != null && trashedItem.isTrashed()) {
							workspace.deleteItem(trashedItem.getId());
							listUpdatedTrashIds.add(trashItemId);
							deleted = true;
						}

					} catch (Exception e) {
						workspaceLogger.warn("Error on DELETE_PERMANENTLY the item : " + trashItemId, e);
						FileTrashedModel fakeFile = new FileTrashedModel();
						fakeFile.setIdentifier(trashItemId);
						listContentError.add(fakeFile);
					}
				}

				String label = listTrashItemIds.size() > 1 ? "items" : "item";
				if (!deleted)
					throw new Exception(
							"Sorry, an error occurred on deleting permanently the trash " + label + ", try again");

				break;
			}

			case RESTORE: {

				boolean restored = false;
				for (String trashItemId : listTrashItemIds) {
					try {
						org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem trashedItem = workspace
								.getItem(trashItemId);
						if (trashedItem != null && trashedItem.isTrashed()) {
							workspace.restoreThrashItem(trashedItem.getId());
							listUpdatedTrashIds.add(trashItemId);
							restored = true;
						}

					} catch (Exception e) {
						workspaceLogger.warn("Error on RESTORE the item : " + trashItemId, e);
						FileTrashedModel fakeFile = new FileTrashedModel();
						fakeFile.setIdentifier(trashItemId);
						listContentError.add(fakeFile);
					}
				}
				String label = listTrashItemIds.size() > 1 ? "items" : "item";

				if (!restored)
					throw new Exception("Sorry, an error occurred on restoring the trash " + label + ", try again");

				break;
			}

			default:
				break;
			}

			if (!listContentError.isEmpty()) {
				result.setListErrors(listContentError);
			}

			result.setListTrashIds(listUpdatedTrashIds);

			return result;

		} catch (Exception e) {
			workspaceLogger.error("Error in server executeOperationOnTrash", e);
			String error = ConstantsExplorer.SERVER_ERROR + " updating the trash content. " + e.getMessage();
			throw new Exception(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * deleteListItemsForIds(java.util.List)
	 */
	/**
	 * Delete list items for ids.
	 *
	 * @param ids
	 *            the ids
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<GarbageItem> deleteListItemsForIds(List<String> ids) throws Exception {

		try {

			if (ids == null)
				throw new Exception("List identifiers is null");

			workspaceLogger.debug("called deleteListItemsForIds with: " + ids.size() +" id/s");
			
			// Workspace workspace = getWorkspace();
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			String[] items = new String[ids.size()];
			items = ids.toArray(items);

			Map<String, GarbageItem> garbage = new HashMap<String, GarbageItem>(items.length);

			// SAVE DATE FOR NOTIFICATIONS
			for (String itemId : ids) {
				// NOTIFICATION
				org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem = workspace.getItem(itemId);
				checkItemLocked(itemId);
				// SAVING ATTRIBUTE FOR NOTIFICATION
				boolean sourceItemIsShared = wsItem.isShared();
				String itemName = wsItem.getName();
				String sourceFolderSharedId = null;
				try {
					if (sourceItemIsShared) {
						org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem rootSharedFolder = workspace
								.getRootSharedFolder(itemId);
						sourceFolderSharedId = rootSharedFolder.getId();
					}
					// REMOVE ITEM
					garbage.put(itemId, new GarbageItem(sourceItemIsShared, itemName, itemId, sourceFolderSharedId));

				} catch (Exception e) {
					workspaceLogger.warn("Impossible to send notifiaction for item with id: " + itemId);
				}
			}

			// ITEM ID - ERROR
			Map<String, String> backendError = workspace.removeItems(items);
			// GARBAGE ITEM ERROR
			List<GarbageItem> frontEndError = new ArrayList<GarbageItem>(backendError.size());

			// REMOVING IDS WHICH HAVE GENERATED AN ERROR
			for (String idError : backendError.keySet()) {
				GarbageItem gbi = garbage.get(idError);
				if (gbi != null) {
					frontEndError.add(gbi);
					garbage.remove(idError);
				}
			}

			NotificationsProducerToStorageHub np = getNotificationProducerToStorageHub();
			for (String idItem : garbage.keySet()) {
				GarbageItem item = garbage.get(idItem);
				workspaceLogger.trace("Check notification for " + item);
				// IF SOURCE SHARED FOLDER IS NOT NULL
				if (item.getSourceFolderSharedId() != null)
					NotificationStorageHubUtil.checkSendNotifyRemovedItemFromShare(this.getThreadLocalRequest(),
							item.isSourceItemIsShared(), item.getOldItemName(), item.getOldItemName(),
							item.getSourceFolderSharedId(), workspace, np);
			}

			return frontEndError;

		} catch (WorkspaceFolderLocked e1) {
			throw new Exception(e1.getMessage());

//		} catch (InsufficientPrivilegesException e) {
//			workspaceLogger.error("Error in server Item remove", e);
//			String error = "An error occurred on deleting item. " + e.getMessage();
//			throw new Exception(error);
//
//		} catch (ItemNotFoundException e) {
//			String error = "An error occurred on deleting item. " + ConstantsExplorer.ERROR_ITEM_DOES_NOT_EXIST;
//			workspaceLogger.error(error, e);
//			throw new Exception(error);
		} catch (Exception e) {
			workspaceLogger.error("Error in server Item remove", e);
			String error = ConstantsExplorer.SERVER_ERROR + " deleting item. " + e.getMessage();
			throw new Exception(error);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * createFolder(java.lang.String, java.lang.String,
	 * org.gcube.portlets.user.workspace.client.model.FileModel)
	 */
	/**
	 * Creates the folder.
	 *
	 * @param nameFolder
	 *            the name folder
	 * @param description
	 *            the description
	 * @param parent
	 *            the parent
	 * @return the folder model
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public FolderModel createFolder(String nameFolder, String description, FileModel parent) throws Exception {

		if (isSessionExpired())
			throw new SessionExpiredException();

		try {

			boolean isParentNull = parent == null;
			workspaceLogger.debug("Create folder: " + nameFolder + " parent is null? "+isParentNull);
			
			if (nameFolder == null)
				throw new Exception("Folder name is null");

			checkItemLocked(parent.getIdentifier());

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			// Creating the folder
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder wsFolder = workspace.createFolder(nameFolder,
					description, parent.getIdentifier());
			// Getting the parent folder
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder parentFolderDestionation = (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) workspace
					.getItem(parent.getIdentifier());
			NotificationsProducerToStorageHub np = getNotificationProducerToStorageHub();
			NotificationStorageHubUtil.checkNotifyAddItemToShare(wsFolder, null, parentFolderDestionation, workspace,
					np);
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			return (FolderModel) converter.toTreeFileModel(wsFolder, parent, parentFolderDestionation.isShared());

		} catch (WorkspaceFolderLocked e1) {
			throw new Exception(e1.getMessage());

//		} catch (InsufficientPrivilegesException e) {
//			String error = "Insufficient Privileges to create the folder";
//			workspaceLogger.error(error, e);
//			throw new Exception(error);
//		} catch (ItemAlreadyExistException e) {
//			String error = "An error occurred on creating folder, " + e.getMessage();
//			workspaceLogger.error(error, e);
//			throw new Exception(error);
		} catch (Exception e) {
			String error = "Error on creating folder. Either the folder already exist or you do not have the permission to create it";
			workspaceLogger.error(error, e);
			throw new Exception(error);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * moveItems(java.util.List, java.lang.String)
	 */
	/**
	 * Move items.
	 *
	 * @param ids
	 *            the ids
	 * @param destinationId
	 *            the destination id
	 * @return the boolean
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public WorkspaceOperationResult moveItems(List<String> ids, String destinationId) throws Exception {
		workspaceLogger.trace("moveItems " + ids.size() + ", destination: " + destinationId);

		if (isSessionExpired())
			throw new SessionExpiredException();

		// boolean error = false;
		WorkspaceOperationResult results = new WorkspaceOperationResult();
		results.setOperationName("Move Items");
		try {

			checkItemLocked(destinationId);

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem folderDestinationItem = workspace
					.getItem(destinationId); // retrieve folder destination
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder folderDestination = null;

			if (folderDestinationItem != null && folderDestinationItem.isFolder()) {
				folderDestination = (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) folderDestinationItem;
			} else
				throw new Exception("Wrong destination. Either It is not a folder or not exist");

			for (String itemId : ids) {

				org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem sourceItem = null;
				try {

					if (itemId == null)
						throw new Exception(IDENTIFIER_IS_NULL);

					workspaceLogger.trace("Moving item id: " + itemId + " in the destination: " + destinationId);
					sourceItem = workspace.getItem(itemId); // GET SOURCE ITEM
															// BEFORE OF MOVE

					checkItemLocked(itemId);

					String sourceRootSharedFolderId = null;
					boolean sourceItemIsShared = sourceItem.isShared();

					// JUST ONCE TO REDUCE THE NUMBER OF CALLS TO STORAGEHUB
					if (sourceItemIsShared && sourceRootSharedFolderId == null) {
						org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem sourceShared = workspace
								.getRootSharedFolder(itemId);
						sourceRootSharedFolderId = sourceShared.getId(); // GET
																			// SHARED
																			// ID
																			// BEFORE
																			// OF
																			// MOVE
					}

					workspaceLogger
							.debug("Invoking move on source item id: " + itemId + " with name: " + sourceItem.getName()
									+ " shared: " + sourceItemIsShared + " destination: " + destinationId);
					org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem movedItem = workspace.moveItem(itemId,
							destinationId); // move item
					workspaceLogger.debug("Moved item: " + movedItem);

					try {
						// NOTIFY?
						NotificationsProducerToStorageHub np = getNotificationProducerToStorageHub();
						NotificationStorageHubUtil.checkNotifyAddItemToShare(movedItem, sourceRootSharedFolderId,
								folderDestination, workspace, np);
						NotificationStorageHubUtil.checkNotifyMoveItemFromShare(sourceItemIsShared, sourceItem,
								sourceRootSharedFolderId, folderDestination, workspace, np);

					} catch (Exception e) {
						workspaceLogger.error("An error occurred in checkNotify ", e);
					}

				} catch (Exception e) {

					String error = results.getError();
					if (error == null)
						error = "Error on moving:";

					error += " ";
					error += sourceItem != null ? sourceItem.getName() : "item is null";
					error += ",";
					results.setError(error);
				}
			}

			// removing last ','
			if (results.getError() != null)
				results.setError(results.getError().substring(0, results.getError().length() - 1));

			workspaceLogger.info("Moved error: " + results.getError());

			if (results.getError() != null)
				results.setError(results.getError()
						+ ". Operation not allowed. Moving to wrong path, either it is a shared folder or you have not the permission to move the item");

			return results;

		} catch (WorkspaceFolderLocked e1) {
			throw new Exception(e1.getMessage());

//		} catch (InsufficientPrivilegesException e) {
//			workspaceLogger.error("Error in server Item move", e);
//			String error1 = "An error occurred on moving item. " + e.getMessage();
//			throw new Exception(error1);

		} catch (Exception e) {
			workspaceLogger.error("Item move error.", e);
			String error2 = ConstantsExplorer.SERVER_ERROR + " moving item/s. " + e.getMessage();
			throw new Exception(error2);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * renameItem(java.lang.String, java.lang.String, java.lang.String)
	 */
	/**
	 * Rename item.
	 *
	 * @param itemId
	 *            the item id
	 * @param newName
	 *            the new name
	 * @param previousName
	 *            the previous name
	 * @return the boolean
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public Boolean renameItem(String itemId, String newName, String previousName) throws Exception {

		if (isSessionExpired())
			throw new SessionExpiredException();

		boolean sourceItemIsShared = false;
		try {

			if (itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			checkItemLocked(itemId);

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			workspaceLogger.debug("Renaming item with id: " + itemId + " from old name " + previousName
					+ ", to new name: " + newName);

			//Needed to check if the item is shared
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem  = workspace.getItem(itemId);
			sourceItemIsShared = wsItem.isShared();
			
			wsItem = workspace.renameItem(itemId, newName);
			workspaceLogger.debug("Item renamed is: " + wsItem);

			// SEND NOTIFY?
			if (sourceItemIsShared) {

				try {
					List<InfoContactModel> listSharedContact = new ArrayList<InfoContactModel>();
					NotificationsProducerToStorageHub np = getNotificationProducerToStorageHub();
					org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem sourceShared = workspace
							.getRootSharedFolder(wsItem.getId());
					// NotificationsManager nManager =
					// WsUtil.getNotificationManager(this.getThreadLocalRequest());
					listSharedContact = NotificationStorageHubUtil.getListUserSharedByFolderSharedId(sourceShared,
							workspace);
					// IS A SHARED FOLDER
					if (NotificationStorageHubUtil.isASharedFolder(wsItem)) {
						np.notifyFolderRenamed(listSharedContact, wsItem, previousName, newName, sourceShared.getId());
						// NotificationStorageHubUtil.checkNotifyAddItemToShare(movedItem,
						// sourceRootSharedFolderId, folderDestination,
						// workspace, np);
						// NotificationStorageHubUtil.checkNotifyMoveItemFromShare(sourceItemIsShared,
						// sourceItem, sourceRootSharedFolderId,
						// folderDestination, workspace, np);
					} else {
						// IS AN SHARED ITEM
						if (sourceShared instanceof org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder)
							np.notifyItemRenamed(listSharedContact, previousName, wsItem,
									(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder) sourceShared,
									workspace);
						// //TWO CASES: EITHER ROOT FOLDER AS
						// WorkspaceSharedFolder OR DOESN'T.
						// WorkspaceItem sharedFolder =
						// workspace.getItem(wsItem.getIdSharedFolder());
						// if(sharedFolder instanceof WorkspaceSharedFolder)
						// notification.notifyItemRenamed(listSharedContact,
						// previousName, wsItem, (WorkspaceSharedFolder)
						// sharedFolder);
						// else
						// workspaceLogger.trace("Notifies for rename item
						// itemId: "+itemId+" doesn't sent because:
						// "+sharedFolder+" is not an instance of
						// WorkspaceSharedFolder");
					}
				} catch (Exception e) {
					workspaceLogger.error("An error occurred in checkNotify ", e);
					return true;
				}
			}

			return true;

		} catch (WorkspaceFolderLocked e1) {
			throw new Exception(e1.getMessage());

//		} catch (ItemAlreadyExistException e) {
//			String error = "An error occurred on renaming item, " + e.getMessage();
//			workspaceLogger.error(error, e);
//			throw new Exception(error);
//		} catch (ItemNotFoundException e2) {
//			String error = "An error occurred on renaming item. " + ConstantsExplorer.ERROR_ITEM_DOES_NOT_EXIST;
//			workspaceLogger.error(error, e2);
//			throw new Exception(error);
		//TO STORAGEHUB EXCEPTION	
		} catch (UserNotAuthorizedException e) {
			String error = "Insufficient Privileges to rename the item";
			workspaceLogger.error(error, e);
			throw new Exception(error);
		} catch (Exception e) {
			workspaceLogger.error("Renaming item error:", e);
			StringBuilder error = new StringBuilder();
			if(sourceItemIsShared) {
				error.append("Ops! This operation is not allowed, we're working hard to make this possible soon.");
				throw new SHUBOperationNotAllowedException(error.toString());
			}else {
				error.append("Ops an error occurred renaming the item! Either you have not the permission to rename it or a server error occurred. Please, refresh and try again");
				throw new Exception(error.toString());
			}
			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * copyItems(java.util.List, java.lang.String)
	 */
	/**
	 * Copy items.
	 *
	 * @param idsItem
	 *            the ids item
	 * @param destinationFolderId
	 *            the destination folder id
	 * @return the workspace operation result
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public WorkspaceOperationResult copyItems(List<String> idsItem, String destinationFolderId) throws Exception {

		workspaceLogger.debug("Copying ids: " + idsItem + " in the destionation folder: " + destinationFolderId);

		if (isSessionExpired())
			throw new SessionExpiredException();

		// boolean error = false;
		WorkspaceOperationResult results = new WorkspaceOperationResult();
		results.setOperationName("Copy Items");

		try {

			checkItemLocked(destinationFolderId);
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem sourceItem = null;

			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem folderDestinationItem = workspace
					.getItem(destinationFolderId);
			NotificationsProducerToStorageHub np = getNotificationProducerToStorageHub();
			String sourceRootSharedId = null;
			for (String itemId : idsItem) {

				try {
					sourceItem = workspace.getItem(itemId); // GET SOURCE ITEM
															// BEFORE COPY

					// JUST ONCE. THE ROOT SHARED IS THE SAME FOR ALL ITEMS
					if (sourceItem.isShared() && sourceRootSharedId != null) {
						org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem sourceShared = workspace
								.getRootSharedFolder(sourceItem.getId());
						sourceRootSharedId = sourceShared.getId();
					}
					workspaceLogger.debug("Copying item with id: " + sourceItem.getId());
					org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem copiedFile = workspace
							.copyFile(sourceItem.getId(), destinationFolderId); // copy
																				// item
					workspaceLogger.debug("Copied item is: " + copiedFile);
					// final WorkspaceItem workspaceItem, final String
					// sourceRootSharedFolderId, final WorkspaceFolder
					// parentFolderItem,
					// org.gcube.common.storagehubwrapper.server.tohl.Workspace
					// workspace, NotificationsProducerToStorageHub np) {
					NotificationStorageHubUtil.checkNotifyAddItemToShare(copiedFile, sourceRootSharedId,
							(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) folderDestinationItem,
							workspace, np);

				} catch (Exception e) {

					String error = results.getError();
					if (error == null)
						error = "Error on copying:";

					error += " ";
					error += sourceItem != null ? sourceItem.getName() : "item is null";
					error += ",";
					results.setError(error);
				}
			}

			// removing last ','
			if (results.getError() != null)
				results.setError(results.getError().substring(0, results.getError().length() - 1));

			workspaceLogger.info("Copied error: " + results.getError());

			if (results.getError() != null)
				results.setError(results.getError() + ". Operation not allowed");

			return results;

		} catch (WorkspaceFolderLocked e1) {
			throw new Exception(e1.getMessage());

//		} catch (InsufficientPrivilegesException e) {
//			String error = "An error occurred on copying item, " + e.getMessage() + ". " + ConstantsExplorer.TRY_AGAIN;
//			throw new Exception(error);
//
//		} catch (ItemAlreadyExistException e) {
//			String error = "An error occurred on copying item, " + e.getMessage();
//			throw new Exception(error);
		} catch (Exception e) {
			workspaceLogger.error("Error in server copyItem  by id", e);
			String error = ConstantsExplorer.SERVER_ERROR + " copying item " + ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getPublicLinkForFileItemId(java.lang.String, boolean)
	 */
	/**
	 * Gets the public link for file item id.
	 *
	 * @param itemId
	 *            the item id
	 * @param shortenUrl
	 *            the shorten url
	 * @return the public link for file item id
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public PublicLink getPublicLinkForFileItemId(String itemId, boolean shortenUrl) throws Exception {

		workspaceLogger.trace("get Public Link For ItemId: " + itemId);
		try {

			if (itemId == null)
				throw new Exception("Sorry, The Public Link for empty item is unavailable (itemId is null)");

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem = workspace.getItem(itemId);

			if (wsItem == null)
				throw new Exception("Sorry, The Public Link for empty item is unavailable");

			if (wsItem.getType().equals(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItemType.FILE_ITEM)) {

				URL publicLink = workspace.getPublicLinkForFile(itemId);

				if (publicLink == null || publicLink.toString() == null)
					throw new Exception("Sorry, public link on " + wsItem.getName() + " is not available");

				String shortURL = null;
				String httpURL = publicLink.toString();

				if (shortenUrl) {
					shortURL = getShortUrl(httpURL);
					shortURL = shortURL != null ? shortURL : "not available";
				}

				return new PublicLink(httpURL, shortURL);

			} else {
				workspaceLogger.warn("ItemId: " + itemId + " is not a file, sent exception Public Link unavailable");
				throw new Exception("Sorry, The Public Link for selected item is unavailable");
			}

		} catch (Exception e) {
			workspaceLogger.error("Error getPublicLinkForFileItemId for item: " + itemId, e);
			throw new Exception(e.getMessage());
		}

	}

	/**
	 * Gets the version history.
	 *
	 * @param fileIdentifier
	 *            the file identifier
	 * @return the version history
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<FileVersionModel> getVersionHistory(String fileIdentifier) throws Exception {

		workspaceLogger.info("Calling get Version History of: " + fileIdentifier);

		if (fileIdentifier == null)
			throw new Exception("File identifier is null");

		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem = workspace.getItem(fileIdentifier);

			if (wsItem.getType().equals(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItemType.FILE_ITEM)) {
				List<WorkspaceFileVersion> listOfVersions = workspace.getListVersionsForFile(fileIdentifier);
				StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
				workspaceLogger
						.info("Version list for " + fileIdentifier + " has " + listOfVersions.size() + " item/s");
				return converter.toVersionHistory(listOfVersions);
			} else
				throw new FileNotVersionedException("Selected file is not versioned");

		} catch (Exception e) {
			if (e instanceof FileNotVersionedException)
				throw new Exception(e.getMessage());

			String error = "An error occurred when getting version history of: " + fileIdentifier + ", try again";
			workspaceLogger.error(error);
			throw new Exception(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getPublicLinkForFileItemIdToVersion(java.lang.String, java.lang.String,
	 * boolean)
	 */
	@Override
	public PublicLink getPublicLinkForFileItemIdToVersion(String itemId, String version, boolean shortenUrl)
			throws Exception {

		workspaceLogger.trace("get Public Link For ItemId: " + itemId + " at the version: " + version);
		try {

			if (itemId == null)
				throw new Exception("Sorry, The Public Link for empty item is unavailable (itemId is null)");

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem = workspace.getItem(itemId);

			if (wsItem == null)
				throw new Exception("Sorry, The Public Link for empty item is unavailable");

			if (wsItem.getType().equals(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItemType.FILE_ITEM)) {

				URL publicLink = workspace.getPublicLinkForFile(itemId, version);

				if (publicLink == null || publicLink.toString() == null)
					throw new Exception("Sorry, public link on " + wsItem.getName() + " is not available");

				String shortURL = null;
				String httpURL = publicLink.toString();

				if (shortenUrl) {
					shortURL = getShortUrl(httpURL);
					shortURL = shortURL != null ? shortURL : "";
				}

				return new PublicLink(httpURL, shortURL);

			} else {
				workspaceLogger.warn("ItemId: " + itemId + " is not a file, sent exception Public Link unavailable");
				throw new Exception("Sorry, The Public Link for selected item is unavailable");
			}

		} catch (Exception e) {
			workspaceLogger.error(
					"Error getPublicLinkForFileItemIdToVersion for item: " + itemId + " at the version: " + version, e);
			throw new Exception(e.getMessage());
		}
	}
	

	/**
	 * Gets the list parents by item identifier.
	 *
	 * @param itemIdentifier
	 *            the item identifier
	 * @param includeItemAsParent
	 *            - if parameter is true and item passed in input is a folder,
	 *            the folder is included in path returned as last parent
	 * @return the list parents by item identifier
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<FileModel> getListParentsByItemIdentifier(String itemIdentifier, boolean includeItemAsParent)
			throws Exception {

		workspaceLogger.debug("called get List Parents by SHUB for id " + itemIdentifier
				+ ", include Item As (Last) Parent: " + includeItemAsParent);

		if (isSessionExpired())
			throw new SessionExpiredException();

		if (itemIdentifier == null)
			return new ArrayList<FileModel>(); // empty list

		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = getWorkspaceFromStorageHub();
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			List<? extends org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem> parents = workspace.getParentsById(itemIdentifier);
			workspaceLogger.info("The Parents returned by SHUB are: " + parents.size());
			if(workspaceLogger.isTraceEnabled()) {
				workspaceLogger.trace("They are: ");
				for (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem workspaceItem : parents) {
					workspaceLogger.trace(workspaceItem.getName());
				}
			}
			
			if(parents.size()==0) {
				//IT IS THE ROOT
				workspaceLogger.info("The item id "+itemIdentifier+" is the root, returning empty list");
				return new ArrayList<FileModel>(1);
			}
			
			String nameSpecialFolder = getNameForSpecialFolder();
			
			List<FileModel> arrayParents = new ArrayList<FileModel>(parents.size());
			//loop from last to first index of list of parents
			int lastIndex = parents.size()-1;
			for (int i=lastIndex; i>=0; i--) {
				org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsFolder = parents.get(i);
				workspaceLogger.trace("Adding the item "+ wsFolder.getName()+" at index "+i+" to list of parent");
				org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem parent = null;
				if(i<lastIndex) {
					parent = parents.get(i+1);
					workspaceLogger.trace("Passing the item "+ parent.getName()+" as parent of "+wsFolder.getName()+". Is it right?");
				}
				arrayParents.add(converter.buildGXTFolderModelItemHandleSpecialFolder(wsFolder, parent, nameSpecialFolder));
				workspaceLogger.trace("Added "+ wsFolder.getName()+" as parent to index "+(arrayParents.size()-1));
			}
			
			if(includeItemAsParent) {
				org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem theItem = workspace.getItem(itemIdentifier);
				if(theItem.isFolder()) {
					workspaceLogger.trace("The item passed is a folder, including it as last parent as requested");
					FolderModel passedFolder = converter.buildGXTFolderModelItemHandleSpecialFolder(theItem, parents.get(parents.size()-1), nameSpecialFolder);
					arrayParents.add(passedFolder);
				}
			}
			
			// SET PARENTS
			workspaceLogger.trace("setting parents..");
			for (int i=0; i < arrayParents.size()-1; i++) {
				FileModel parent = arrayParents.get(i);
				FileModel fileModel = arrayParents.get(i+1);
				fileModel.setParentFileModel(parent);
			}
			
			workspaceLogger.trace("list parents returning size: " + arrayParents.size());
			//IS THIS CHECK ALREADY NEEDED?
			if (arrayParents.get(0) == null) { // EXIT BY BREAK IN CASE OF SPECIAL FOLDER
				List<FileModel> breadcrumbs = new ArrayList<FileModel>(arrayParents.size() - 1);
				for (int i = 1; i < arrayParents.size(); i++) {
					breadcrumbs.add(arrayParents.get(i));
				}
				return breadcrumbs;
			} else
				return arrayParents;

		} catch (Exception e) {
			workspaceLogger.error("Error in get List Parents By Item Identifier ", e);
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Check item locked.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 * @throws WorkspaceFolderLocked the workspace folder locked
	 * @throws Exception the exception
	 */
	private boolean checkItemLocked(String itemId) throws WorkspaceFolderLocked, Exception {

		if (itemId == null || itemId.isEmpty())
			throw new Exception(IDENTIFIER_IS_NULL);
		
		org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace = null;
		org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem = null;
		try {

			workspace = getWorkspaceFromStorageHub();
			wsItem = workspace.getItem(itemId);

			// IF THE ITEM IS A FOLDER, CHECKING IT
			if (wsItem.isFolder())
				WorkspaceThreddsSynchronize.getInstance().checkItemSynched(wsItem.getId());
			else {
				// IF THE ITEM IS A FILE, CHECKING ITS PARENT
				String  parentId = wsItem.getParentId();
				if(parentId==null) {
					workspaceLogger.warn("I'm not able to check the lock because the parent id is null");
				}else {
					WorkspaceThreddsSynchronize.getInstance().checkItemSynched(parentId);
				}
			}
			// in this case the folder is synched but not locked
			return false;

		} catch (ItemNotSynched e1) {

			// in this case the folder is not synched;
			return false;

		} catch (WorkspaceFolderLocked e2) {
			// in this case the folder synching is on-going and the folder is
			// locked;

			String msg = "The folder";
			msg += wsItem != null ? ": " + wsItem.getName() : "";
			msg += " is locked by a sync. You can not change its content";
			workspaceLogger.warn(msg, e2);
			throw new WorkspaceFolderLocked(itemId, msg);

//		} catch (InternalErrorException | ItemNotFoundException | HomeNotFoundException
//				| WorkspaceFolderNotFoundException e) {
//			workspaceLogger.warn(e);
//			throw new Exception("Sorry an error occurred during checking is folder locked, Refresh and try again");

		} catch (Exception e) {
			workspaceLogger
					.warn("Was there an Exception HL side? Ignoring it.. returning false (that means item not locked)");
			return false;
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getItemForFileGrid(java.lang.String)
	 */
	@Override
	public FileGridModel getItemForFileGrid(String itemId) throws Exception {

		try {
			
			if (itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);
			
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(itemId);

			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			FileModel parentModel = null;
			if(wsItem.getParentId()!=null) {
				org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem parent = workspace.getItem(wsItem.getParentId());
				parentModel = converter.toTreeFileModel(parent, null, parent.isShared());
			}
			
			return converter.toGridFileModel(wsItem, parentModel);
			
		} catch (Exception e) {
			workspaceLogger.error("Error in server during item retrieving, getItemForFileGrid", e);
			String error = ConstantsExplorer.SERVER_ERROR + RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST;
			throw new Exception(error);
		}
	}

	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getItemForFileTree(java.lang.String)
	 */
	@Override
	public FileModel getItemForFileTree(String itemId) throws Exception {
		try {

			if (itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(itemId);
			
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			FileModel parentModel = null;
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem parent = null;
			if(wsItem.getParentId()!=null) {
				parent = workspace.getItem(wsItem.getParentId());
				parentModel = converter.toTreeFileModel(parent, null, parent.isShared());
			}
			
			return converter.toTreeFileModel(wsItem, parentModel, parent!=null?parent.isShared():false);

		} catch (Exception e) {
			workspaceLogger.error("Error in server during item retrieving, getItemForFileGrid", e);
			String error = ConstantsExplorer.SERVER_ERROR + RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST;
			throw new Exception(error);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getImageById(java.lang.String, boolean, boolean)
	 */
	@Override
	public GWTWorkspaceItem getImageById(String identifier, boolean isInteralImage, boolean fullDetails)
			throws Exception {

		if (identifier == null)
			throw new Exception(IDENTIFIER_IS_NULL);


		try {

			workspaceLogger.debug("get image by id: " + identifier);
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(identifier);
			
			if(wsItem instanceof ImageFileItem) {
				ImageFileItem imageFile = (ImageFileItem) wsItem;
				PortalContextInfo context = WsUtil.getPortalContext(this.getThreadLocalRequest());
				GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
				return ObjectStorageHubToWorkpaceMapper.buildGWTWorkspaceImage(imageFile, isInteralImage, fullDetails, context.getCurrGroupId() + "",
						user.getUserId() + "");
			}else {
				throw new Exception("The input id is not an image");
			}
		} catch (Exception e) {
			workspaceLogger.error("Error in server get image by id", e);
			throw new Exception(e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getUrlById(java.lang.String, boolean, boolean)
	 */
	/**
	 * Gets the url by id.
	 *
	 * @param identifier
	 *            the identifier
	 * @param isInternalUrl
	 *            the is internal url
	 * @param fullDetails
	 *            the full details
	 * @return the url by id
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public GWTWorkspaceItem getUrlById(String identifier, boolean isInternalUrl, boolean fullDetails) throws Exception {

		try {

			if (identifier == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			workspaceLogger.debug("get URL by id: " + identifier);
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(identifier);
																// workspace
			
			if(wsItem instanceof org.gcube.common.storagehubwrapper.shared.tohl.impl.URLFileItem) {
				URLFileItem fileItem = (URLFileItem) wsItem;
				return ObjectStorageHubToWorkpaceMapper.buildGWTWorspaceUrl(workspace, fileItem, isInternalUrl, fullDetails);
			}else if (wsItem instanceof URLItem){
				URLItem urlFile = (URLItem) wsItem;
				return new GWTExternalUrl(urlFile.getValue().toString());
			}else {
				throw new Exception("The input id is not a FILE or a URL");
			}

		} catch (Exception e) {
			workspaceLogger.error("Error in server get image by id ", e);
			// workspaceLogger.trace("Error in server get image by id " + e);
			// GWT can't serialize all exceptions
			throw new Exception(e.getMessage());
		}
	}

	
	/**
	 * Sets the value in session.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void setValueInSession(String name, String value) throws Exception {

		try {
			this.getThreadLocalRequest().getSession().setAttribute(name, value);
			workspaceLogger.trace("set value in session with name: " + name + ", value: " + value);
		} catch (Exception e) {
			workspaceLogger.error("setValueInSession", e);
			throw new Exception(e.getMessage());
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getItemCreationDateById(java.lang.String)
	 */
	@Override
	public Date getItemCreationDateById(String itemId) throws Exception {
		workspaceLogger.trace("get Item Creation Date By ItemId " + itemId);
		try {

			if (itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			workspaceLogger.debug("getItemCreationDateById by id: " + itemId);
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(itemId);
			
			Calendar cl = wsItem.getCreationTime();

			if (cl != null)
				return cl.getTime();

			return null;

		} catch (Exception e) {
			workspaceLogger.error("get Item Creation Date By ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * loadSizeByItemId(java.lang.String)
	 */
	/**
	 * Load size by item id.
	 *
	 * @param itemId
	 *            the item id
	 * @return the long
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public Long loadSizeByItemId(String itemId) throws Exception {
		
		if (itemId == null)
			throw new Exception(IDENTIFIER_IS_NULL);

		workspaceLogger.info("get Size By ItemId " + itemId);
		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(itemId);
			Long size = new Long(-1);

			if (wsItem instanceof org.gcube.common.storagehubwrapper.shared.tohl.impl.FileItem) { // ITEM
				org.gcube.common.storagehubwrapper.shared.tohl.impl.FileItem fileItem = (org.gcube.common.storagehubwrapper.shared.tohl.impl.FileItem) wsItem;
				size = new Long(fileItem.getSize());
			} else if (wsItem instanceof org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFolder) { // FOLDER
				org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFolder theFolder = (org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFolder) wsItem;
				//TODO ASK TO LUCIO;
				//size = theFolder.getSize();
			} else if (wsItem instanceof org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceSharedFolder) { // SHARED FOLDER												
				org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceSharedFolder theFolder = (org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceSharedFolder) wsItem;
				//TODO ASK TO LUCIO;
				//size = theFolder.getSize();
			}
			workspaceLogger.info("returning size: " + size);
			return size;

		} catch (Exception e) {
			workspaceLogger.error("get Size By ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#loadLastModificationDateById(java.lang.String)
	 */
	@Override
	public Date loadLastModificationDateById(String itemId) throws Exception {
		
		if (itemId == null)
			throw new Exception(IDENTIFIER_IS_NULL);

		workspaceLogger.trace("get last modification date ItemId " + itemId);
		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(itemId);
			Date lastModification = null;

			if (wsItem.getLastModificationTime() != null) {
				lastModification = wsItem.getLastModificationTime().getTime();
			}

			return lastModification;

		} catch (Exception e) {
			workspaceLogger.error("get last modification date ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getParentByItemId(java.lang.String)
	 */
	//TODO NEED TO TEST IT
	@Override
	public FileModel getParentByItemId(String identifier) throws Exception {
		
		if (identifier == null)
			throw new Exception(IDENTIFIER_IS_NULL);

		workspaceLogger.trace("get Parent By Item Identifier " + identifier);
		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(identifier);
			workspaceLogger.trace("workspace has returned item name: " + wsItem.getName());
		
			FileModel parentModel = null;
			if(wsItem!=null && wsItem.getParentId()!=null) {
				org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem parent = workspace.getItem(wsItem.getParentId());
				StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
				parentModel = converter.toTreeFileModel(parent, null, parent.isShared());
				
			}

			workspaceLogger.trace("parent not found - retuning");
			return parentModel;

		} catch (Exception e) {
			workspaceLogger.error("Error in get Parent By Item Identifier", e);
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getShortUrl(java.lang.String)
	 */
	/**
	 * Gets the short url.
	 *
	 * @param longUrl
	 *            the long url
	 * @return the short url
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public String getShortUrl(String longUrl) throws Exception {

		workspaceLogger.trace("get short url for " + longUrl);
		UrlShortener shortener = getUrlShortener();
		try {

			if (shortener != null && shortener.isAvailable())
				return shortener.shorten(longUrl);

			return longUrl;

		} catch (Exception e) {
			workspaceLogger.error("Error get short url for ", e);
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getLinkForSendToSwitchBoard(java.lang.String)
	 */
	/**
	 * Gets the link for send to switch board.
	 *
	 * @param itemId
	 *            the item id
	 * @return the link for send to switch board
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public String getLinkForSendToSwitchBoard(String itemId) throws Exception {
		String fallbackValue = ConstantsExplorer.CLARIN_SWITCHBOARD_ENDPOINT_FALLBACK;
		String sbEndpoint = "";
		try {
			sbEndpoint = getCLARINSwitchBoardEndpoint();
		} catch (Exception e) {
			workspaceLogger.error(
					"Could not find CLARINSwitchBoardEndpoint on IS, returning fallback value: " + fallbackValue);
			sbEndpoint = fallbackValue;
		}
		String URI = getPublicLinkForFileItemId(itemId, false).getCompleteURL();
		workspaceLogger.debug("Got LinkForSendToSwitchBoard: " + URI + " encoding ...");
		String encodedURI = URLEncoder.encode(getPublicLinkForFileItemId(itemId, false).getCompleteURL(), "UTF-8");
		workspaceLogger.debug("LinkForSendToSwitchBoard: " + encodedURI + " encoded ...");
		return new StringBuilder(sbEndpoint).append(encodedURI).toString();
	}
	
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getItemDescriptionById(java.lang.String)
	 */
	@Override
	public String getItemDescriptionById(String identifier) throws Exception {
		
		workspaceLogger.info("Getting ItemDescriptionById: " + identifier);
		if (identifier == null || identifier.isEmpty()) {
			workspaceLogger.warn("Getting ItemDescriptionById identifier is null or empty, returning null");
			return null;
		}

		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(identifier);
			workspaceLogger.trace("workspace has returned item name: " + wsItem.getName());
			return wsItem.getDescription();

		} catch (Exception e) {
			workspaceLogger.error("Error in server ItemDescriptionById: ", e);
			String error = ConstantsExplorer.SERVER_ERROR + " getting description for item id: " + identifier;
			throw new Exception(error);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getUserWorkspaceSize()
	 */
	@Override
	public String getUserWorkspaceSize() throws Exception {
		try {
			workspaceLogger.info("Getting workspace size..");
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			long size = workspace.getDiskUsage();
			// workspaceLogger.info("Root size is: "+size +" formatting..");
			String formatSize = FormatterUtil.formatFileSize(size);
			workspaceLogger.info("returning workspace size: " + formatSize);
			return formatSize;
		} catch (Exception e) {
			workspaceLogger.error("Error on UserWorkspaceSize", e);
			String error = ConstantsExplorer.SERVER_ERROR + " getting disk usage";
			throw new Exception(error);
		}

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getUserWorkspaceTotalItems()
	 */
	/**
	 * Gets the user workspace total items.
	 *
	 * @return the user workspace total items
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public long getUserWorkspaceTotalItems() throws Exception {
		try {
			workspaceLogger.info("Getting total items..");
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			long size = workspace.getTotalItems();
			workspaceLogger.info("returning total items value: " + size);
			return size;
		} catch (Exception e) {
			workspaceLogger.error("Error on UserWorkspaceSize", e);
			String error = ConstantsExplorer.SERVER_ERROR + " getting total items";
			throw new Exception(error);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getUserWorkspaceQuote()
	 */
	/**
	 * Gets the user workspace quote.
	 *
	 * @return the user workspace quote
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public WorkspaceUserQuote getUserWorkspaceQuote() throws Exception {
		try {
			workspaceLogger.info("Getting UserWorkspaceQuote..");
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			long size = workspace.getDiskUsage();
			workspaceLogger.info("Root size is: " + size + " formatting..");
			String formatSize = FormatterUtil.formatFileSize(size);
			long total = getUserWorkspaceTotalItems();

			WorkspaceUserQuote quote = new WorkspaceUserQuote();
			quote.setDiskSpace(size);
			quote.setDiskSpaceFormatted(formatSize);
			quote.setTotalItems(total);
			workspaceLogger.info("returning user quote: " + quote);
			return quote;
		} catch (Exception e) {
			workspaceLogger.error("Error on UserWorkspaceQuote", e);
			return null;
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * loadGcubeItemProperties(java.lang.String)
	 */
	/**
	 * Load gcube item properties.
	 *
	 * @param itemId
	 *            the item id
	 * @return the map
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public Map<String, String> loadGcubeItemProperties(String itemId) throws Exception {
		
		workspaceLogger.info("Getting GcubeItemProperties for itemId: " + itemId);
		if (itemId == null || itemId.isEmpty()) {
			workspaceLogger.warn("Getting GcubeItemProperties identifier is null or empty, returning null");
			return null;
		}

		try {
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			PropertyMap properties = workspace.getGcubeItemProperties(itemId);
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			Map<String, String> mapProperties = converter.toSimpleMap(properties);
			if (mapProperties != null)
				workspaceLogger.info("Returning " + mapProperties.size() + " properties");
			else
				workspaceLogger.info("Returning null properties");

			return mapProperties;
		} catch (Exception e) {
			workspaceLogger.error("Error in server GcubeItemProperties: ", e);
			String error = ConstantsExplorer.SERVER_ERROR + " getting gcube item properties for item id: " + itemId;
			throw new Exception(error);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getItemsBySearchName(java.lang.String)
	 */
	/**
	 * Gets the items by search name.
	 *
	 * @param text
	 *            the text
	 * @param folderId
	 *            the folder id
	 * @return the items by search name
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<FileGridModel> getItemsBySearchName(String text, String folderId) throws Exception {
		

		try {
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			if (folderId == null || folderId.isEmpty()) {
				workspaceLogger.debug("searching folderId is null, searching from the root folder");
				folderId = workspace.getRoot().getId();
			}

			workspaceLogger.info("searching by name: " + text + " in the folder id: " + folderId);
			List<org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem> foundItems = workspace.find(text, folderId);
			
			if(foundItems==null) {
				workspaceLogger.info("Searching by SHUB returned null, instancing empty list");
				foundItems = new ArrayList<org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem>();
			}
			
			workspaceLogger.info("Searching by SHUB returned" + foundItems.size() + " item/s, converting it/them");

			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			List<FileGridModel> listFileGridModels = new ArrayList<FileGridModel>();
			
			for (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem workspaceItem : foundItems) {
				workspaceLogger.debug("Converting grid item: " + workspaceItem.getId() + " name " + workspaceItem.getName());
				listFileGridModels.add(converter.toGridFileModel(workspaceItem, null));
			}
			
			workspaceLogger.info("Search objects converted is/are, "+listFileGridModels.size()+"returning");

			return listFileGridModels;
		} catch (Exception e) {
			workspaceLogger.error("Error occured on searching: ", e);
			throw new Exception("Error occured on searching, please retry or contact the support");
		}
	}
	
	/**
	 * Gets the children sub tree to root by identifier.
	 *
	 * @param itemIdentifier
	 *            the item identifier
	 * @return the children sub tree to root by identifier
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public ArrayList<SubTree> getChildrenSubTreeToRootByIdentifier(String itemIdentifier) throws Exception {
		
		throw new Exception("The method getChildrenSubTreeToRootByIdentifier is not implement to interact with SHUB");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * itemExistsInWorkpaceFolder(java.lang.String, java.lang.String)
	 */
	/**
	 * Item exists in workpace folder.
	 *
	 * @param parentId
	 *            the parent id
	 * @param itemName
	 *            the item name
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public String itemExistsInWorkpaceFolder(String parentId, String itemName) throws Exception {
		
		workspaceLogger.debug("called itemExistsInWorkpace for name "+itemName+" in the parent Id: " + parentId);

		if (parentId == null)
			throw new Exception("I'm not able to perform searching.. the folder id is null");

		
		try {
			
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			workspaceLogger.info("searching by name: " + itemName + " in the folder id: " + parentId);
			List<org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem> foundItems = workspace.find(itemName, parentId);

			if (foundItems == null || foundItems.isEmpty())
				return null;
			
			return foundItems.get(0).getId();

//		} catch (InternalErrorException e) {
//			return null;
//		} catch (ItemNotFoundException e) {
//			return null;
		} catch (Exception e) {
			String error = "Error on searching the item "+itemName+" in the passed folder id";
			workspaceLogger.error(error, e);
			throw new Exception(e.getMessage());
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getAccountingHistory(java.lang.String)
	 */
	/**
	 * Gets the accounting history.
	 *
	 * @param identifier
	 *            the identifier
	 * @return the accounting history
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<GxtAccountingField> getAccountingHistory(String identifier) throws Exception {
		
		if (identifier == null)
			throw new Exception("I'm not able to get accounting history... the folder id is null");
		
		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(identifier,true,false,false);
			List<AccountEntry> accoutings = wsItem.getAccounting();
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			List<GxtAccountingField> listAccounting = converter.buildGXTAccountingItem(accoutings, GxtAccountingEntryType.ALL);
			workspaceLogger.debug("get accouting returning size " + listAccounting.size());
			return listAccounting;

		} catch (Exception e) {
			workspaceLogger.error("Error reading the accounting history for item id: "+identifier, e);
			String error = ConstantsExplorer.SERVER_ERROR + " getting accounting History. " + ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getListOfVREsForLoggedUser()
	 */
	/**
	 * Gets the list of vr es for logged user.
	 *
	 * @return the list of vr es for logged user
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<GcubeVRE> getListOfVREsForLoggedUser() throws Exception {
		workspaceLogger.debug("getListOfVREsForLoggedUser...: ");
		// PortalContextInfo context =
		// WsUtil.getPortalContext(this.getThreadLocalRequest());
		GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
		long userId = user.getUserId();

		// Instanciate the manager
		GroupManager groupManager = new LiferayGroupManager();
		List<GcubeVRE> listOfVres = new ArrayList<GcubeVRE>();

		if (isTestMode()) {
			listOfVres.add(new GcubeVRE("devVRE", "/gcube/devsec/devVRE"));
			listOfVres.add(new GcubeVRE("NextNext", "/gcube/devNext/NextNext"));
			return listOfVres;
		}

		try {

			List<GCubeGroup> listOfGroups = groupManager.listGroupsByUser(userId);
			for (GCubeGroup gCubeGroup : listOfGroups) {
				if (groupManager.isVRE(gCubeGroup.getGroupId())) {
					GcubeVRE gcubeVRE = new GcubeVRE(gCubeGroup.getGroupName(),
							groupManager.getInfrastructureScope(gCubeGroup.getGroupId()));
					listOfVres.add(gcubeVRE);
				}
			}

		} catch (UserRetrievalFault | UserManagementSystemException | GroupRetrievalFault e) {
			workspaceLogger.error("Error occurred server-side getting VRE folders: ", e);
			throw new Exception("Sorry, an error occurred server-side getting VRE folders, try again later");
		}

		workspaceLogger.info("Returning list of VREs: " + listOfVres);
		return listOfVres;
	}
	
	/**
	 * Gets the HTML gcube item properties.
	 *
	 * @param itemId
	 *            the item id
	 * @return The Gcube Item Properties in HTML format if itemId is a GcubeItem
	 *         and contains properties, null otherwise
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public String getHTMLGcubeItemProperties(String itemId) throws Exception {
		workspaceLogger.info("getting HTMLGcubeItemProperties for itemId: " + itemId);
		if (itemId == null || itemId.isEmpty()) {
			workspaceLogger.warn("Error on getting GcubeItemProperties the  identifier is null or empty, returning null");
			return null;
		}
		
		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(itemId);
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			return converter.getGcubeItemPropertiesForGcubeItemAsHTML(wsItem);

		} catch (Exception e) {
			workspaceLogger.error("Error in server FormattedGcubeItemProperties: ", e);
			String error = ConstantsExplorer.SERVER_ERROR + " getting gcube item properties for item id: " + itemId;
			throw new Exception(error);
		}
	}
	
	/**
	 * Gets the accounting readers.
	 *
	 * @param identifier
	 *            the identifier
	 * @return the accounting readers
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<GxtAccountingField> getAccountingReaders(String identifier) throws Exception {
		
		if (identifier == null || identifier.isEmpty()) {
			workspaceLogger.warn("Error on getting accounting history to readers, identifier is null, returning null");
			return null;
		}

		workspaceLogger.debug("reading accounting history only to "+ GxtAccountingEntryType.READ+" for id: " + identifier);
		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(identifier,true,false,false);
			List<AccountEntry> accoutings = wsItem.getAccounting();
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			List<GxtAccountingField> listOfReadFromAccounting = converter.buildGXTAccountingItem(accoutings, GxtAccountingEntryType.READ);
			workspaceLogger.info("list of "+GxtAccountingEntryType.READ+" into accounting are:" + listOfReadFromAccounting.size());
			return listOfReadFromAccounting;
			
		} catch (Exception e) {
			workspaceLogger.error("Error get accounting readers ", e);
			String error = ConstantsExplorer.SERVER_ERROR + " getting account. " + ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);
		}
	}
	
	/**
	 * Sets the gcube item properties.
	 *
	 * @param itemId
	 *            the item id
	 * @param properties
	 *            the properties
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void setGcubeItemProperties(String itemId, Map<String, String> properties) throws Exception {
		workspaceLogger.info("Set GcubeItemProperties for itemId: " + itemId);
		if (itemId == null || itemId.isEmpty()) {
			workspaceLogger.warn("Set GcubeItemProperties, identifier is null or empty, returning");
			throw new Exception("The item id is null or empty");
		}

		try {

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(itemId);
			
			if (wsItem instanceof org.gcube.common.storagehubwrapper.shared.tohl.impl.GcubeItem) {
				workspaceLogger.info("Adding " + properties.size() + " properties to GCubeItem: " + itemId);
				Map<String, Object> mapObjs = properties==null?null:new HashMap<String, Object>(properties.size());
				mapObjs.putAll(properties);
				workspace.updateMetadata(itemId, mapObjs);
			} else
				throw new NoGcubeItemTypeException("The item is not a Gcube Item");

		} catch (NoGcubeItemTypeException e) {
			workspaceLogger.error("Error on settingss properties to GcubeItem: ", e);
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			workspaceLogger.error("Error on settings properties to GcubeItem: ", e);
			String error = ConstantsExplorer.SERVER_ERROR + " setting gcube item properties for item id: " + itemId;
			throw new Exception(error);
		}
	}
	
	/**
	 * Creates the external url.
	 *
	 * @param parentId
	 *            the parent id
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param url
	 *            the url
	 * @return the file model
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public FileModel createExternalUrl(String parentId, String name, String description, String url) throws Exception {
		workspaceLogger.info("Called createExternalUrl [parent: "+parentId+", url: "+url+"]");
		try {
			
			if (parentId == null) {
				workspaceLogger.error("Error on creating url. Parent ID is null");
				throw new Exception("Parent ID is null");
			}
			
			checkItemLocked(parentId);

			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsFolderParent =  workspace.getItem(parentId);
			
			StorageHubToWorkpaceConverter converter = getStorageHubToWorkpaceConverter();
			FileModel parentModel = converter.toTreeFileModel(wsFolderParent, null, wsFolderParent.isShared());
			
			workspaceLogger.debug("creating the URL...");
			URLItem ext = workspace.createURL(name, description, url, parentId);
			workspaceLogger.info("created URL file: " + ext.getName());
			FileModel urlFile = converter.toTreeFileModel(ext, parentModel, wsFolderParent.isShared());
			return urlFile;

		} catch (WorkspaceFolderLocked e1) {
			throw new Exception(e1.getMessage());

		} catch (Exception e) {
			workspaceLogger.error("Error on creating the URL in the folder id: "+parentId, e);
			throw new Exception(e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * markFolderAsPublicForFolderItemId(java.lang.String, boolean)
	 */
	/**
	 * Mark folder as public for folder item id.
	 *
	 * @param folderId
	 *            the item id
	 * @param setPublic
	 *            the set public
	 * @return the public link
	 * @throws SessionExpiredException
	 *             the session expired exception
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public PublicLink markFolderAsPublicForFolderItemId(String folderId, boolean setPublic)
			throws SessionExpiredException, Exception {
		workspaceLogger.info("called markFolderAsPublicForFolderItemId on folder id: " + folderId +" setPublic: "+setPublic);
		
		if (isSessionExpired())
			throw new SessionExpiredException();

		try {
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			String folderLink = null;
			try {
				
				//Changing access to.. if needed
				boolean newStatus = workspace.setFolderAsPublic(folderId, setPublic);
				workspaceLogger.info("The folder "+folderId+" has the public status at " +newStatus+ " on SHUB");
				//If the folder was published
				if (newStatus) {
					
					workspaceLogger.info("SHUB returned the folder link id: " + folderId);
					ApplicationProfileReader apReader = new ApplicationProfileReader("Workspace-Explorer-App", "org.gcube.portlets.user.workspaceexplorerapp.server.WorkspaceExplorerAppServiceImpl");
					ApplicationProfile ap = apReader.readProfileFromInfrastrucure();

					String encriptedFId = StringEncrypter.getEncrypter().encrypt(folderId);
					workspaceLogger.info("Encrypted folder Id: " + encriptedFId);
					String encodedFId = StringUtil.base64EncodeStringURLSafe(encriptedFId);
					workspaceLogger.info("Encoded in Base 64: " + encodedFId);
					workspaceLogger.info("Application profile returning url: " + ap.getUrl());
					folderLink = ap.getUrl() + "?folderId=" + encodedFId;
					String shortURL = null;
					try {
						shortURL = getShortUrl(folderLink);
						shortURL = shortURL != null ? shortURL : "not available";
					} catch (Exception e) {
						workspaceLogger.warn("Short url error, skipping");
						shortURL = "not available";
					}
					PublicLink pubL = new PublicLink(folderLink, shortURL);
					workspaceLogger.info("Returning the: "+pubL);
					return pubL;
				}
				workspaceLogger.info("The folder has the public status at "+setPublic+" so returning Public Link as null");
				return null;
					
			}catch (Exception e) {
				workspaceLogger.error("Error on changing access to folder as public: "+setPublic, e);
				String error = ConstantsExplorer.SERVER_ERROR + " changing access to folder id: " + folderId;
				throw new Exception(error);
			}
		} catch (Exception e) {
			workspaceLogger.error("Error on getting the folder id: "+folderId, e);
			throw new Exception(e.getMessage());
		}

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * getImagesForFolder(java.lang.String, java.lang.String)
	 */
	/**
	 * Gets the images for folder.
	 *
	 * @param folderId
	 *            the folder id
	 * @param currentImageId
	 *            the current image id
	 * @return the images for folder
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<GWTWorkspaceItem> getImagesForFolder(String folderId, String currentImageId) throws Exception {

		if (folderId == null)
			return null;

		try {
			workspaceLogger.debug("get images for folder id: " + folderId);
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(folderId);

			List<GWTWorkspaceItem> images = new ArrayList<GWTWorkspaceItem>();

			if (wsItem.isFolder()) {
				org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder folder = (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) wsItem;
				List<? extends org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem> children = workspace.getChildren(folder.getId());
				
//				GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
				PortalContextInfo context = WsUtil.getPortalContext(this.getThreadLocalRequest());
				GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());

				for (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem workspaceItem : children) {
					boolean foundCurrentImage = false;
					//IS AN IMAGE?
					if (workspaceItem instanceof ImageFileItem) {
						try {
							ImageFileItem imageFileItem = (ImageFileItem) workspaceItem;
							GWTWorkspaceItem image = null;
							
							image = ObjectStorageHubToWorkpaceMapper.buildGWTWorkspaceImage(imageFileItem, true, false,
									context.getCurrGroupId() + "", user.getUserId() + "");
							image.setId(workspaceItem.getId());
							
							
							if (image != null) {
								if (!foundCurrentImage && image.getId().compareTo(currentImageId) == 0) {
									workspaceLogger.debug(image.getName() + " is the current displaying image so adding to list as first element");
									images.add(0, image);
									foundCurrentImage = true;
								} else {
									workspaceLogger.debug("Adding thumbnail name to list: " + image.getName());
									images.add(image);
								}
							}
						}catch (Exception e) {
							workspaceLogger.warn("Error on managing the file item: "+workspaceItem.getId() +" as an "+ImageFileItem.class.getName());
						}
					}
				}
			}
			
			if(workspaceLogger.isTraceEnabled()) {
				workspaceLogger.trace("Returning list of images for folder: "+folderId);
				for (GWTWorkspaceItem gwtWorkspaceItem : images) {
					workspaceLogger.trace(gwtWorkspaceItem.toString());
				}
			}

			workspaceLogger.info("Returning " + images.size() + " images for folder id: " + folderId);
			return images;
		} catch (Exception e) {
			workspaceLogger.error("Error in server get images by folder id: " + folderId, e);
			throw new Exception(e.getMessage());
		}
	}


	/**
	 * Perform operation on versioned file.
	 *
	 * @param fileId
	 *            the file id
	 * @param olderVersionIDs
	 *            the older version i ds
	 * @param operation
	 *            the operation
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<FileVersionModel> performOperationOnVersionedFile(String fileId, List<String> olderVersionIDs,
			WorkspaceVersioningOperation operation) throws Exception {
		
		workspaceLogger.info("Called perform operation on Versioned File Id: " + fileId + ", Ids Version: " + olderVersionIDs + " perform operation: " + operation);
		if (fileId == null || olderVersionIDs == null || olderVersionIDs.size() == 0)
			throw new Exception("Versioned File id or List of versioned file  is null");

		try {
			
			org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace =  getWorkspaceFromStorageHub();
			org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsItem =  workspace.getItem(fileId);
			if (wsItem instanceof org.gcube.common.storagehubwrapper.shared.tohl.items.FileItem) {
				//org.gcube.common.storagehubwrapper.shared.tohl.items.FileItem fileItem = (org.gcube.common.storagehubwrapper.shared.tohl.items.FileItem) wsItem;
				switch (operation) {

					case DOWNLOAD: {
						// IMPLEMENTED CLIENT-SIDE
						break;
					}
					case DELETE_ALL_OLDER_VERSIONS: {
						throw new Exception(operation+" must be implemented on SHUB-side");
						// MUST BE OPTIMIZED HL-SIDE
	//					for (String olderVersionId : olderVersionIDs) {
	////						fileItem.removeVersion(olderVersionId);
	//						throw new Exception(operation+" must be implemented on SHUB-side");
	//						workspaceLogger.info("Version " + olderVersionId + " of file id: " + fileId + " removed");
	//					}
	//					return getVersionHistory(fileId);
					}
					case REFRESH: {
						return getVersionHistory(fileId);
					}
					case DELETE_PERMANENTLY: {
						throw new Exception(operation+" must be implemented on SHUB-side");
	//					for (String olderVersionId : olderVersionIDs) {
	//						fileItem.removeVersion(olderVersionId);
	//						workspaceLogger.info("Version " + olderVersionId + " of file id: " + fileId + " removed");
	//					}
	//					return getVersionHistory(fileId);
					}
					default: {
						break;
					}
				}

				return getVersionHistory(fileId);

			} else
				throw new FileNotVersionedException("Selected file is not versioned");

		} catch (Exception e) {
			
			workspaceLogger.error("Error during performing operation on versioned file with id: " + fileId, e);
			String error = ConstantsExplorer.SERVER_ERROR + " updating versioning of file id: " + fileId;
			throw new Exception(error);
		}
	}

}

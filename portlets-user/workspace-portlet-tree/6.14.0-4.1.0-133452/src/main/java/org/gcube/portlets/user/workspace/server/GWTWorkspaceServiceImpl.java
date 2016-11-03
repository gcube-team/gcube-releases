package org.gcube.portlets.user.workspace.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.ApplicationProfileScopePerUrlReader;
import org.gcube.datacatalogue.ckanutillibrary.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.utils.SessionCatalogueAttributes;
import org.gcube.datacatalogue.ckanutillibrary.utils.UtilMethods;
import org.gcube.portlets.user.urlshortener.UrlShortener;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategorySmartFolder;
import org.gcube.portlets.user.workspace.client.model.FileDetailsModel;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.model.ScopeModel;
import org.gcube.portlets.user.workspace.client.model.SmartFolderModel;
import org.gcube.portlets.user.workspace.client.model.SubTree;
import org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube.WorkspaceHandledException;
import org.gcube.portlets.user.workspace.server.notifications.NotificationsProducer;
import org.gcube.portlets.user.workspace.server.notifications.NotificationsUtil;
import org.gcube.portlets.user.workspace.server.reader.ApplicationProfile;
import org.gcube.portlets.user.workspace.server.reader.ApplicationProfileReader;
import org.gcube.portlets.user.workspace.server.resolver.UriResolverReaderParameterForResolverIndex;
import org.gcube.portlets.user.workspace.server.util.AclTypeComparator;
import org.gcube.portlets.user.workspace.server.util.DifferenceBetweenInfoContactModel;
import org.gcube.portlets.user.workspace.server.util.StringUtil;
import org.gcube.portlets.user.workspace.server.util.UserUtil;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.user.workspace.server.util.resource.PropertySpecialFolderReader;
import org.gcube.portlets.user.workspace.server.util.scope.ScopeUtilFilter;
import org.gcube.portlets.user.workspace.shared.ExtendedWorkspaceACL;
import org.gcube.portlets.user.workspace.shared.GarbageItem;
import org.gcube.portlets.user.workspace.shared.PublicLink;
import org.gcube.portlets.user.workspace.shared.ReportAssignmentACL;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.portlets.user.workspace.shared.TrashContent;
import org.gcube.portlets.user.workspace.shared.TrashOperationContent;
import org.gcube.portlets.user.workspace.shared.UserBean;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;
import org.gcube.portlets.user.workspace.shared.WorkspaceUserQuote;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingEntryType;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;
import org.gcube.portlets.user.workspaceapplicationhandler.ApplicationReaderFromGenericResource;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * The Class GWTWorkspaceServiceImpl.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class GWTWorkspaceServiceImpl extends RemoteServiceServlet implements GWTWorkspaceService{

	protected static final String IDENTIFIER_IS_NULL = "Identifier is null";
	protected static final String RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST = " retrieving item. Either the item doesn't exist anymore or you do not have the permission to access it";
	private static final long serialVersionUID = 2828885661214875589L;
	public static final String LAST_OPEN_FOLDER_ATTRIBUTE = "WORKSPACE.LAST_OPEN_FOLDER";
	public static final String SELECTION_STATE_ATTRIBUTE = "WORKSPACE.SELECTION_STATE";
	protected Logger workspaceLogger = Logger.getLogger(GWTWorkspaceServiceImpl.class);

	// for the data catalogue
	private static final String CKAN_ROLE = "ckanRole"; // a true value means the user has admin role, false means member
	private static final String CKAN_ORGANIZATIONS_PUBLISH_KEY = "ckanOrganizationsPublish"; // here he can publish

	/**
	 * Gets the GWT workspace builder.
	 *
	 * @return the GWT workspace builder
	 */
	protected GWTWorkspaceBuilder getGWTWorkspaceBuilder()
	{
		return WsUtil.getGWTWorkspaceBuilder(this.getThreadLocalRequest().getSession());
	}

	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	protected Workspace getWorkspace() throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		return WsUtil.getWorkspace(this.getThreadLocalRequest().getSession());
	}

	/**
	 * Gets the notification producer.
	 *
	 * @return the notification producer
	 */
	protected NotificationsProducer getNotificationProducer(){

		return WsUtil.getNotificationProducer(WsUtil.getAslSession(this.getThreadLocalRequest().getSession()), this.getThreadLocalRequest());
	}

	/**
	 * Gets the scope util filter.
	 *
	 * @return the scope util filter
	 */
	protected ScopeUtilFilter getScopeUtilFilter(){

		return WsUtil.getScopeUtilFilter(this.getThreadLocalRequest().getSession());
	}

	/**
	 * Checks if is test mode.
	 *
	 * @return true, if is test mode
	 */
	protected boolean isTestMode(){
		return !WsUtil.isWithinPortal();
	}

	/**
	 * Gets the url shortener.
	 *
	 * @return the url shortener
	 */
	protected UrlShortener getUrlShortener() {
		return WsUtil.getUrlShortener(this.getThreadLocalRequest().getSession());
	}

	/**
	 * Gets the uri resolver.
	 *
	 * @return the uri resolver
	 */
	protected UriResolverReaderParameterForResolverIndex getUriResolver() {
		return WsUtil.getUriResolver(this.getThreadLocalRequest().getSession());
	}

	/**
	 * Gets the property special folder reader.
	 *
	 * @return the property special folder reader
	 */
	protected PropertySpecialFolderReader getPropertySpecialFolderReader() {
		String absolutePathProperty = getSpecialFolderPath();
		return WsUtil.getPropertySpecialFolderReader(this.getThreadLocalRequest().getSession(),absolutePathProperty);
	}

	/**
	 * Gets the special folder path.
	 *
	 * @return the Category if there is correspondance, null otherwise
	 */
	private String getSpecialFolderPath() {
		ServletContext servletContext = getServletContext();
		String contextPath = servletContext.getRealPath(File.separator);
		return contextPath + File.separator +"conf" +  File.separator + ConstantsExplorer.SPECIALFOLDERNAMEPROPERTIESFILE;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getRootForTree()
	 */
	@Override
	public FolderModel getRootForTree() throws Exception {

		workspaceLogger.trace("getRoot");

		try {

			workspaceLogger.trace("getting workspace");
			Workspace workspace = getWorkspace();
			WorkspaceFolder root = workspace.getRoot();

			if (root == null) {
				workspaceLogger.error("The root is null");
				throw new Exception("The root is null");
			}

			workspaceLogger.trace("Root loaded, gxt conversion");
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			FolderModel gwtroot =builder.buildWorkspaceFileModelRoot(root);

			workspaceLogger.trace("Root converted, returnig...");
			return gwtroot;
		} catch (Exception e) {
			workspaceLogger.error("Error in server during root retrieving", e);
			//			workspaceLogger.trace("Error in server During root retrieving " + e);

			//GWT can't serialize all exceptions
			throw new Exception("Error during workspace loading, please contact the support. Exception:" +e);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getRootForTree(java.lang.String)
	 */
	@Override
	public FolderModel getRootForTree(String scopeId) throws Exception {

		workspaceLogger.info("getRoot for scope " + scopeId);
		workspaceLogger.trace("getting workspace");

		try {

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("on server getRoot for scope: " + scopeId);

			//			GCUBEScope gcubeScope = null;
			//
			//			if(scopeId.compareTo(ScopeUtilFilter.IDALLSCOPE)!=0){
			//				gcubeScope = GCUBEScope.getScope(scopeId);
			//			}

			WorkspaceFolder root =  workspace.getRoot();

			if (root == null) {
				workspaceLogger.error("The root is null");
				throw new Exception("The root is null");
			}

			workspaceLogger.trace("Root loaded, gxt conversion");
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			workspaceLogger.trace("Root converted, returnig...");
			return builder.buildWorkspaceFileModelRoot(root);

		} catch (Exception e) {
			workspaceLogger.error("Error in server during root retrieving", e);
			//GWT can't serialize all exceptions
			throw new Exception("Error during workspace loading, please contact the support. Exception:" +e);
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getItemsBySearchName(java.lang.String)
	 */
	@Override
	public List<FileGridModel> getItemsBySearchName(String text, String folderId) throws Exception {

		try {
			Workspace workspace = getWorkspace();
			if(folderId==null || folderId.isEmpty()){
				workspaceLogger.trace("searching folderId is null, settings root Id");
				folderId = workspace.getRoot().getId();
			}

			workspaceLogger.info("searching by name: "+text +" in "+folderId);
			List<SearchItem> listSearchItems = workspace.searchByName(text, folderId);
			workspaceLogger.info("HL search returning "+listSearchItems.size()+" items");

			workspaceLogger.info("Converting "+listSearchItems.size()+" items");
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			List<FileGridModel> listFileGridModels = new ArrayList<FileGridModel>();

			listFileGridModels = builder.buildGXTListFileGridModelItemForSearch(listSearchItems);
			workspaceLogger.info("Search objects converted, returning");

			return listFileGridModels;
		} catch (Exception e) {
			workspaceLogger.error("Error in server During search retrieving", e);
			//			workspaceLogger.trace("Error in server During search retrieving " + e);

			//GWT can't serialize all exceptions
			throw new Exception("Error during searching, please contact the support.");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getFolderChildren(org.gcube.portlets.user.workspace.client.model.FolderModel)
	 */
	@Override
	public List<FileModel> getFolderChildren(FolderModel folder) throws Exception, SessionExpiredException{

		Workspace workspace;

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			if(folder == null)
				throw new Exception("Folder is null");

			workspaceLogger.trace("get folder children for: "+folder.getIdentifier() +" name: "+folder.getName());
			workspace = getWorkspace();
			List<FileModel> listFileModels = new ArrayList<FileModel>();
			WorkspaceItem wsItem = workspace.getItem(folder.getIdentifier());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			listFileModels = builder.buildGXTListFileModelItem(wsItem, folder);

			//ADDING SPECIAL FOLDER?
			if(wsItem.isRoot()){
				//ADD SPECIAL FOLDER
				try{
					workspaceLogger.info("Folder is root, loading special folders..");
					WorkspaceFolder specialFolder =   workspace.getMySpecialFolders();
					FileModel specialFolderModel = builder.buildGXTFileModelItem(specialFolder, folder);
					specialFolderModel.setSpecialFolder(true);
					String newName = getNameForSpecialFolder();

					if(!newName.isEmpty()){
						workspaceLogger.info("Special folder name updated as: "+newName);
						specialFolderModel.setName(newName);
					}else
						workspaceLogger.info("Special folder name is empty, skipping");

					listFileModels.add(specialFolderModel);
				}catch (Exception e) {
					workspaceLogger.warn("An error occurred on retrieving special folders for folder id: "+folder.getIdentifier(), e);
				}
			}

			return listFileModels;

		} catch (Exception e) {
			workspaceLogger.error("Error in server During item retrieving", e);
			//			workspaceLogger.trace("Error in server During item retrieving " + e);
			String error = ConstantsExplorer.SERVER_ERROR + RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST;
			//GWT can't serialize all exceptions
			throw new Exception(error);
		}
	}

	/**
	 * Gets the name for special folder.
	 *
	 * @return the name for special folder
	 */
	private String getNameForSpecialFolder(){

		PropertySpecialFolderReader sfReader = getPropertySpecialFolderReader();

		if(sfReader==null){
			workspaceLogger.warn("Reader is null, skypping set to special folder name");
			return "";
		}

		workspaceLogger.info("Read special folder name: '"+sfReader.getSpecialFolderName()+"', from property file..");
		return sfReader.getSpecialFolderName();
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getFolderChildrenForFileGrid(org.gcube.portlets.user.workspace.client.model.FileModel)
	 */
	@Override
	public List<FileGridModel> getFolderChildrenForFileGrid(FileModel folder) throws Exception, SessionExpiredException {

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			if(folder == null)
				throw new Exception("Folder is null");

			workspaceLogger.trace("get children for Grid for folder: "+folder.getIdentifier());
			Workspace workspace = getWorkspace();
			List<FileGridModel> listFileGridModels = new ArrayList<FileGridModel>();
			WorkspaceFolder wsFolder = (WorkspaceFolder) workspace.getItem(folder.getIdentifier());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			List<WorkspaceItem> listItems = wsFolder.getChildren();
			listFileGridModels = builder.buildGXTListFileGridModelItem(listItems, folder);

			//ADDING SPECIAL FOLDER?
			if(wsFolder.isRoot()){
				//ADD SPECIAL FOLDER
				try{
					workspaceLogger.info("Folder is root, loading special folders..");
					WorkspaceFolder specialFolder =   workspace.getMySpecialFolders();
					FileGridModel specialFolderModel = builder.buildGXTFileGridModelItem(specialFolder, folder);
					//					specialFolderModel.setShortcutCategory(GXTCategoryItemInterface.SMF_VRE_FOLDERS);
					specialFolderModel.setSpecialFolder(true);
					String newName = getNameForSpecialFolder();

					if(!newName.isEmpty()){
						workspaceLogger.info("Special folder name updated as: "+newName);
						specialFolderModel.setName(newName);
					}else
						workspaceLogger.info("Special folder name is empty, skipping");

					listFileGridModels.add(specialFolderModel);
				}catch (Exception e) {
					workspaceLogger.warn("An error occurred on retrieving special folders for folder id: "+folder.getIdentifier(), e);
				}
			}

			return listFileGridModels;

		} catch (Exception e) {
			workspaceLogger.error("Error in server During items retrieving", e);
			String error = ConstantsExplorer.SERVER_ERROR + RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST;
			//GWT can't serialize all exceptions
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getFolderChildrenForFileGridById(java.lang.String)
	 */
	@Override
	public List<FileGridModel> getFolderChildrenForFileGridById(String folderId) throws Exception, SessionExpiredException {


		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			if(folderId == null)
				throw new Exception("Folder id is null");

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("get children for Grid by id: "+folderId);
			List<FileGridModel> listFileGridModels = new ArrayList<FileGridModel>();

			if(folderId==null || folderId.isEmpty()){
				workspaceLogger.trace("id is null or empty, return");
				return listFileGridModels;
			}

			workspaceLogger.trace("get children for Grid by id: "+folderId);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();

			//BUILD PARENT
			WorkspaceItem wsItem = workspace.getItem(folderId);
			WorkspaceFolder parent;

			if(wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER) || wsItem.getType().equals(WorkspaceItemType.FOLDER)){
				workspaceLogger.trace("item id: "+folderId +" is of type: "+wsItem.getType());
				parent = (WorkspaceFolder) wsItem;

			}else{
				workspaceLogger.trace("item id: "+folderId +" is not a folder but of type: "+wsItem.getType()+", get parent");
				parent = wsItem.getParent();
			}

			if(parent==null)
				return listFileGridModels;

			FileGridModel wsParent = builder.buildGXTFileGridModelItem(parent, null);

			//PARENT BUILDED IS SHARED?
			if(parent.isShared()){
				wsParent.setShared(true);
				wsParent.setShareable(false);
			}

			Long startTime =  System.currentTimeMillis();

			//GET CHILDREN
			List<WorkspaceItem> listItems = parent.getChildren();
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			workspaceLogger.debug("grid getChildren() returning "+listItems.size()+" elements in " + time);
			listFileGridModels = builder.buildGXTListFileGridModelItem(listItems, wsParent);
			return listFileGridModels;

		} catch (Exception e) {
			workspaceLogger.error("Error in server During items retrieving", e);
			String error = ConstantsExplorer.SERVER_ERROR + RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST;
			throw new Exception(error);
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getItemForFileGrid(java.lang.String)
	 */
	@Override
	public FileGridModel getItemForFileGrid(String itemId) throws Exception {

		try {

			if(itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("get child for Grid by id: "+itemId);
			WorkspaceItem wsItem =  workspace.getItem(itemId);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			Long startTime =  System.currentTimeMillis();
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			workspaceLogger.debug("get child for Grid by id returning element in " + time);
			//BUILD PARENT
			WorkspaceFolder folder = wsItem.getParent(); //get parent
			FileGridModel wsFolder = builder.buildGXTFileGridModelItem(folder, null);
			//BUILD ITEM
			return builder.buildGXTFileGridModelItem(wsItem, wsFolder);

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

			if(itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem =  workspace.getItem(itemId);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			//BUILD PARENT
			WorkspaceFolder folderParent = wsItem.getParent(); //get parent
			FileModel wsFolderParent = builder.buildGXTFileModelItem(folderParent, null);
			//BUILD ITEM
			return builder.buildGXTFileModelItem(wsItem, wsFolderParent);

		} catch (Exception e) {
			workspaceLogger.error("Error in server during item retrieving, getItemForFileGrid", e);
			String error = ConstantsExplorer.SERVER_ERROR + RETRIEVING_ITEM_EITHER_ITEM_DOESN_T_EXIST;
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#moveItem(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean moveItem(String itemId, String destinationId) throws Exception {
		workspaceLogger.trace("moveItem itemId: "+itemId+" destination: "+destinationId);

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {
			Workspace workspace = getWorkspace();

			if(itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			workspaceLogger.trace("moveItem item: "+itemId+" destination: "+destinationId);
			WorkspaceItem sourceItem = workspace.getItem(itemId);  //GET SOURCE ITEM BEFORE OF MOVE

			if(sourceItem==null)
				return Boolean.FALSE;

			String sourceSharedId = null;
			boolean sourceItemIsShared = sourceItem.isShared();

			if(sourceItemIsShared)
				sourceSharedId = sourceItem.getIdSharedFolder(); //GET SHARED ID BEFORE OF MOVE

			workspaceLogger.trace("moveItem item: "+itemId+" sourceItem name "+sourceItem.getName() + " shared: "+sourceItemIsShared+ " destination: "+destinationId);
			WorkspaceItem destinationItem = workspace.moveItem(itemId, destinationId); //move item
			WorkspaceItem folderDestinationItem = workspace.getItem(destinationId); //retrieve folder destination
			workspaceLogger.trace("sourceItem.isShared() "+sourceItemIsShared);
			workspaceLogger.trace("folderDestinationItem item: "+destinationId+" folderDestinationItem name "+folderDestinationItem.getName() + " folderDestinationItem shared: "+folderDestinationItem.isShared());

			if(folderDestinationItem!=null){

				try{
					checkNotifyAddItemToShare(destinationItem, sourceSharedId, folderDestinationItem);
					checkNotifyMoveItemFromShare(sourceItemIsShared, sourceItem, sourceSharedId, folderDestinationItem);

				}catch (Exception e) {
					workspaceLogger.error("An error occurred in checkNotify ", e);
				}
			}

			return Boolean.TRUE;

		}catch (InsufficientPrivilegesException e) {
			workspaceLogger.error("Error in server Item move", e);
			String error = "An error occurred on moving item. "+e.getMessage();
			throw new Exception(error);

		} catch (Exception e) {
			workspaceLogger.error("Error in server Item move", e);
			String error = ConstantsExplorer.SERVER_ERROR + " moving item. "+e.getMessage();
			throw new Exception(error);
		}

	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#moveItems(java.util.List, java.lang.String)
	 */
	@Override
	public Boolean moveItems(List<String> ids, String destinationId) throws Exception {
		workspaceLogger.trace("moveItems "+ids.size()+ ", destination: "+destinationId);

		if(isSessionExpired())
			throw new SessionExpiredException();

		boolean error = false;

		try {
			Workspace workspace = getWorkspace();

			for (String itemId : ids) {

				if(itemId == null)
					throw new Exception(IDENTIFIER_IS_NULL);

				workspaceLogger.trace("moveItem item: "+itemId+" destination: "+destinationId);

				WorkspaceItem sourceItem = workspace.getItem(itemId);  //GET SOURCE ITEM BEFORE OF MOVE

				if(sourceItem==null){
					error = true;
					break;
				}

				String sourceSharedId = null;
				boolean sourceItemIsShared = sourceItem.isShared();

				if(sourceItemIsShared)
					sourceSharedId = sourceItem.getIdSharedFolder(); //GET SHARED ID BEFORE OF MOVE

				workspaceLogger.trace("moveItem item: "+itemId+" sourceItem name "+sourceItem.getName() + " shared: "+sourceItemIsShared+ " destination: "+destinationId);
				WorkspaceItem destinationItem = workspace.moveItem(itemId, destinationId); //move item
				WorkspaceItem folderDestinationItem = workspace.getItem(destinationId); //retrieve folder destination
				workspaceLogger.trace("sourceItem.isShared() "+sourceItemIsShared );
				workspaceLogger.trace("folderDestinationItem item: "+destinationId+" folderDestinationItem name "+folderDestinationItem.getName() + " folderDestinationItem shared: "+folderDestinationItem.isShared());

				if(folderDestinationItem!=null){

					try{
						checkNotifyAddItemToShare(destinationItem, sourceSharedId, folderDestinationItem);

						checkNotifyMoveItemFromShare(sourceItemIsShared, sourceItem, sourceSharedId, folderDestinationItem);

					}catch (Exception e) {
						workspaceLogger.error("An error occurred in checkNotify ", e);
					}
				}
			}

			if(error)
				return Boolean.FALSE;

			return Boolean.TRUE;

		}catch (InsufficientPrivilegesException e) {
			workspaceLogger.error("Error in server Item move", e);
			String error1 = "An error occurred on moving item. "+e.getMessage();
			throw new Exception(error1);

		} catch (Exception e) {
			workspaceLogger.error("Error in server Item move", e);
			String error2 = ConstantsExplorer.SERVER_ERROR + " moving item. "+e.getMessage();
			throw new Exception(error2);
		}

	}

	/**
	 * Check notify add item to share.
	 *
	 * @param destinationItem the destination item
	 * @param sourceSharedId the source shared id
	 * @param folderDestinationItem the folder destination item
	 */
	private void checkNotifyAddItemToShare(final WorkspaceItem destinationItem, final String sourceSharedId, final WorkspaceItem folderDestinationItem) {

		workspaceLogger.trace("checkNotifyAddItemToShare");

		if(folderDestinationItem!=null){

			try{
				//if folder destination is shared folder
				if(folderDestinationItem.isShared()){ 	//Notify Added Item To Sharing?

					workspaceLogger.trace("checkNotifyAddItemToShare destination item: "+destinationItem.getName()+" sourceSharedId: "+sourceSharedId + " folder destination: "+folderDestinationItem.getName());

					//share condition is true if source shared folder is null or not equal to destination shared folder
					boolean shareChangeCondition = sourceSharedId==null || sourceSharedId.compareTo(folderDestinationItem.getIdSharedFolder())!=0;

					//System.out.println("shareChangeCondition add item: "+  shareChangeCondition);

					workspaceLogger.trace("shareChangeCondition add item: "+shareChangeCondition);

					//if shareChangeCondition is true.. notifies added item to sharing
					if(shareChangeCondition){

						List<InfoContactModel> listContacts = getListUserSharedByFolderSharedId(folderDestinationItem.getIdSharedFolder());

						//DEBUG
						printContacts(listContacts);
						Workspace workspace = getWorkspace();
						WorkspaceItem destinationSharedFolder = workspace.getItem(folderDestinationItem.getIdSharedFolder());
						NotificationsProducer np = getNotificationProducer();

						if(destinationSharedFolder instanceof WorkspaceSharedFolder){
							np.notifyAddedItemToSharing(listContacts, destinationItem, (WorkspaceSharedFolder) destinationSharedFolder);
							workspaceLogger.trace("The notifies sent correctly");
						}
						else
							workspaceLogger.warn("Notifies added item: "+destinationItem+ "to share doesn't sent because "+destinationSharedFolder+" is not istance of WorkspaceSharedFolder");
						//							np.notifyAddedItemToSharing(listContacts, (WorkspaceFolder) folderDestinationItem);
					}
				}
				else
					workspaceLogger.trace("folder destination is not shared");

			}catch (Exception e) {
				workspaceLogger.error("An error occurred in  verifyNotifyAddItemToShare ",e);
			}
		}else
			workspaceLogger.warn("The notifies is failure in verifyNotifyAddItemToShare because folder destination item is null");
	}


	/**
	 * Check notify move item from share.
	 *
	 * @param sourceItemIsShared the source item is shared
	 * @param sourceItem the source item
	 * @param sourceSharedId the source shared id
	 * @param folderDestinationItem the folder destination item
	 */
	private void checkNotifyMoveItemFromShare(final boolean sourceItemIsShared, final WorkspaceItem sourceItem, final String sourceSharedId, final WorkspaceItem folderDestinationItem) {

		workspaceLogger.trace("checkNotifyMoveItemFromShare:");

		try{

			if(folderDestinationItem!=null){

				String idSharedFolder = folderDestinationItem.getIdSharedFolder()!=null?folderDestinationItem.getIdSharedFolder():"";

				//share condition is true if source shared folder is not equal to destination shared folder
				boolean shareChangeCondition = sourceSharedId==null?false:sourceSharedId.compareTo(idSharedFolder)!=0;

				workspaceLogger.trace("checkNotifyMoveItemFromShare source item: "+sourceItem.getName()+" sourceSharedId: "+sourceSharedId + " folder destination: "+folderDestinationItem.getName() +" sourceItemIsShared: "+sourceItemIsShared);

				//				System.out.println("shareChangeCondition remove item: "+  shareChangeCondition);

				workspaceLogger.trace("shareChangeCondition remove item: "+  shareChangeCondition);

				//Notify Removed Item To Sharing?
				//if source Item is shared and folder destination is not shared or shareChangeCondition is true.. notifies removed item to sharing
				if(sourceItemIsShared && (!folderDestinationItem.isShared() || shareChangeCondition)){

					//get contacts
					List<InfoContactModel> listContacts = getListUserSharedByFolderSharedId(sourceSharedId);

					//DEBUG
					printContacts(listContacts);
					Workspace workspace = getWorkspace();
					WorkspaceItem sourceSharedFolder = workspace.getItem(sourceSharedId);
					NotificationsProducer np = getNotificationProducer();

					if(sourceSharedFolder instanceof WorkspaceSharedFolder){
						np.notifyMovedItemToSharing(listContacts, sourceItem, (WorkspaceSharedFolder) sourceSharedFolder);
						workspaceLogger.trace("The notifies was sent correctly");
					}else
						workspaceLogger.warn("Notifies moved item: "+sourceItem+ "from share doesn't sent because "+sourceSharedFolder+" is not istance of WorkspaceSharedFolder");
				}

			}else
				workspaceLogger.warn("The notifies is failure in checkNotifyMoveItemFromShare because folder destination item is null");

		}catch (Exception e) {
			workspaceLogger.error("An error occurred in checkNotifyMoveItemFromShare ",e);
		}

	}

	//DEBUG
	/**
	 * Prints the contacts.
	 *
	 * @param listContacts the list contacts
	 */
	private void printContacts(List<InfoContactModel> listContacts){

		workspaceLogger.trace("Contacts:");
		for (InfoContactModel infoContactModel : listContacts) {
			workspaceLogger.trace("User: "+infoContactModel);
		}
	}

	//DEBUG
	/**
	 * Prints the list.
	 *
	 * @param list the list
	 */
	private void printList(List<String> list){

		for (String string : list) {
			workspaceLogger.trace(string);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#removeItem(java.lang.String)
	 */
	@Override
	public Boolean removeItem(String itemId) throws Exception {

		try {

			if(itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("removeItem item for id: "+itemId);
			//NOTIFICATION
			WorkspaceItem wsItem = workspace.getItem(itemId);
			//SAVING ATTRIBUTE FOR NOTIFICATION
			boolean sourceItemIsShared = wsItem.isShared();
			String itemName = wsItem.getName();
			String sourceFolderSharedId = null;

			if(sourceItemIsShared)
				sourceFolderSharedId = wsItem.getIdSharedFolder();

			//REMOVE ITEM
			workspace.removeItem(itemId);
			//IF SOURCE SHARED FOLDER IS NOT NULL
			if(sourceFolderSharedId!=null)
				NotificationsUtil.checkSendNotifyRemoveItemToShare(this.getThreadLocalRequest(), this.getThreadLocalRequest().getSession(), sourceItemIsShared, itemName, itemId, sourceFolderSharedId);

			return Boolean.TRUE;

		} catch (InsufficientPrivilegesException e) {
			workspaceLogger.error("Error in server Item remove", e);
			String error = "Insufficient Privileges to remove the item";
			throw new Exception(error);

		}catch (ItemNotFoundException e) {
			String error = "An error occurred on deleting item. "+ConstantsExplorer.ERROR_ITEM_DOES_NOT_EXIST;
			workspaceLogger.error(error, e);
			throw new Exception(error);
		} catch (Exception e) {
			workspaceLogger.error("Error in server Item remove", e);
			String error = ConstantsExplorer.SERVER_ERROR +" deleting item. "+e.getMessage();
			throw new Exception(error);
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#renameItem(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean renameItem(String itemId, String newName, String previousName) throws Exception {

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			if(itemId == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("rename item itemId: "+itemId+" old name "+ previousName +", new name: "+newName);
			workspace.renameItem(itemId, newName);
			//NOTIFIER
			WorkspaceItem wsItem = workspace.getItem(itemId);

			if(wsItem.isShared()){

				try{
					List<InfoContactModel> listSharedContact = new ArrayList<InfoContactModel>();
					NotificationsProducer notification = getNotificationProducer();
					listSharedContact = getListUserSharedByFolderSharedId(wsItem.getIdSharedFolder());
					if(NotificationsUtil.isASharedFolder(wsItem)){
						notification.notifyFolderRenamed(listSharedContact, wsItem, previousName, newName, wsItem.getIdSharedFolder());
					}else{

						//TWO CASES: EITHER ROOT FOLDER AS WorkspaceSharedFolder OR DOESN'T.
						WorkspaceItem sharedFolder = workspace.getItem(wsItem.getIdSharedFolder());
						if(sharedFolder instanceof WorkspaceSharedFolder)
							notification.notifyItemRenamed(listSharedContact, previousName, wsItem, (WorkspaceSharedFolder) sharedFolder);
						else
							workspaceLogger.trace("Notifies for rename item itemId: "+itemId+" doesn't sent because: "+sharedFolder+" is not an instance of WorkspaceSharedFolder");
					}
				}catch (Exception e) {
					workspaceLogger.error("An error occurred in checkNotify ", e);
					return true;
				}
			}

			return true;

		} catch (InsufficientPrivilegesException e) {
			String error = "Insufficient Privileges to rename the item";
			workspaceLogger.error(error, e);
			throw new Exception(error);
		} catch (ItemAlreadyExistException e) {
			String error = "An error occurred on renaming item, "  +e.getMessage();
			workspaceLogger.error(error, e);
			throw new Exception(error);
		}catch (ItemNotFoundException e2) {
			String error = "An error occurred on renaming item. "+ConstantsExplorer.ERROR_ITEM_DOES_NOT_EXIST;
			workspaceLogger.error(error, e2);
			throw new Exception(error);
		} catch (Exception e) {
			String error = ConstantsExplorer.SERVER_ERROR + " renaming item. "+ConstantsExplorer.TRY_AGAIN;
			workspaceLogger.error(error, e);
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#createFolder(java.lang.String, java.lang.String, org.gcube.portlets.user.workspace.client.model.FileModel)
	 */
	@Override
	public FolderModel createFolder(String nameFolder, String description, FileModel parent) throws Exception {

		workspaceLogger.trace("create folder: "+nameFolder +" parent is null"+parent==null);

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			if(nameFolder == null)
				throw new Exception("Folder name is null");

			Workspace workspace = getWorkspace();
			WorkspaceFolder wsFolder = workspace.createFolder(nameFolder, description, parent.getIdentifier());
			WorkspaceItem folderDestinationItem = workspace.getItem(parent.getIdentifier());
			checkNotifyAddItemToShare(wsFolder, null, folderDestinationItem);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.buildGXTFolderModelItem(wsFolder, parent);

		} catch(InsufficientPrivilegesException e){
			String error = "Insufficient Privileges to create the folder";
			workspaceLogger.error(error, e);
			throw new Exception(error);
		} catch (ItemAlreadyExistException e) {
			String error = "An error occurred on creating folder, "  +e.getMessage();
			workspaceLogger.error(error, e);
			throw new Exception(error);
		} catch (Exception e) {
			String error = ConstantsExplorer.SERVER_ERROR + " creating folder. "+ConstantsExplorer.TRY_AGAIN;
			workspaceLogger.error(error, e);
			throw new Exception(error);
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getDetailsFile(org.gcube.portlets.user.workspace.client.model.FileModel)
	 */
	@Override
	public FileDetailsModel getDetailsFile(FileModel folder) throws Exception {

		try {

			if(folder == null)
				throw new Exception("Folder is null");

			workspaceLogger.trace("load file details: " + folder.getName());
			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(folder.getIdentifier());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();

			return builder.buildGWTWorkspaceFileDetails(wsItem, folder);

		} catch (Exception e) {
			workspaceLogger.error("Error in load server file details", e);
			throw new Exception(e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getChildrenSubTreeToRootByIdentifier(java.lang.String)
	 */
	@Override
	public ArrayList<SubTree> getChildrenSubTreeToRootByIdentifier(String itemIdentifier) throws Exception {

		ArrayList<SubTree>  listSubTree = new ArrayList<SubTree>();

		try {

			if(itemIdentifier == null)
				throw new Exception(IDENTIFIER_IS_NULL);

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("search itemId: "+itemIdentifier);
			WorkspaceItem item = workspace.getItem(itemIdentifier); //get item from workspace
			getListsChildrenByParent(listSubTree, item);
			Collections.reverse(listSubTree); //reverse order of array

			//SET THE ROOT
			int lenght = listSubTree.size();
			if(lenght>0){
				FolderModel firstLevel = listSubTree.get(0).getParent(); //get root
				workspaceLogger.trace("set: "+firstLevel.getName() +" as root");
				listSubTree.get(0).getParent().setIsRoot(true);
				//IF IT CASE - REWRITE SPECIAL FOLDER NAME
				if(lenght>1){
					FolderModel wsFolder = listSubTree.get(1).getParent(); //get first child
					String nameSpecialFolder = getNameForSpecialFolder();
					if(wsFolder.getName().compareTo(ConstantsExplorer.MY_SPECIAL_FOLDERS)==0 && firstLevel.isRoot()){
						//MANAGEMENT SPECIAL FOLDER
						workspaceLogger.debug("getChildrenSubTreeToRootByIdentifier MANAGEMENT SPECIAL FOLDER NAME REWRITING AS: "+nameSpecialFolder);
						if(nameSpecialFolder!=null && !nameSpecialFolder.isEmpty())
							listSubTree.get(1).getParent().setName(nameSpecialFolder);
					}
				}
			}

			workspaceLogger.trace("getChildrenSubTreeToRootByIdentifier returning list SubTree: "+listSubTree);
		} catch (Exception e) {
			workspaceLogger.error("Error in server find Item", e);
			throw new Exception(e.getMessage());
		}

		return listSubTree;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getSmartFolderResultsByCategory(java.lang.String)
	 */
	@Override
	public List<FileGridModel> getSmartFolderResultsByCategory(GXTCategorySmartFolder category) throws Exception {

		try {

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("get smart folder by category: "+category);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			List<SearchItem> listWorkspaceItems = new ArrayList<SearchItem>();

			//Category IMAGES
			if(category.equals(GXTCategorySmartFolder.SMF_IMAGES)){
				listWorkspaceItems = workspace.getFolderItems(FolderItemType.IMAGE_DOCUMENT, FolderItemType.EXTERNAL_IMAGE);
				//Category BIODIVERSITY
			}else if(category.equals(GXTCategorySmartFolder.SMF_BIODIVERSITY)){
				//				listWorkspaceItems = workspace.getFolderItems(FolderItemType.AQUAMAPS_ITEM);
				//Category DOCUMENTS
			}else if(category.equals(GXTCategorySmartFolder.SMF_DOCUMENTS)){

				listWorkspaceItems = workspace.getFolderItems(
						FolderItemType.EXTERNAL_FILE,
						FolderItemType.EXTERNAL_PDF_FILE,
						FolderItemType.QUERY,
						FolderItemType.PDF_DOCUMENT,
						FolderItemType.METADATA,
						FolderItemType.DOCUMENT
						);

				//Category LINKS
			}else if(category.equals(GXTCategorySmartFolder.SMF_LINKS)){
				//				listWorkspaceItems = workspace.getFolderItems(FolderItemType.EXTERNAL_URL, FolderItemType.URL_DOCUMENT, FolderItemType.EXTERNAL_RESOURCE_LINK);
				//Category REPORTS
			}else if(category.equals(GXTCategorySmartFolder.SMF_REPORTS)){
				listWorkspaceItems = workspace.getFolderItems(FolderItemType.REPORT_TEMPLATE, FolderItemType.REPORT);
				//Category TIME SERIES
			}else if(category.equals(GXTCategorySmartFolder.SMF_TIMESERIES)){
				listWorkspaceItems = workspace.getFolderItems(FolderItemType.TIME_SERIES);
			}
			else if(category.equals(GXTCategorySmartFolder.SMF_PUBLIC_FOLDERS)){
				List<WorkspaceItem> listFolder = workspace.getPublicFolders();
				if(listFolder==null || listFolder.isEmpty())
					return new ArrayList<FileGridModel>();
				return builder.buildGXTListFileGridModelItem(listFolder, null);
			}else
				new Exception("Smart folder category unknown");

			return builder.filterListFileGridModelItemByCategory(listWorkspaceItems, category);

		} catch (Exception e) {
			workspaceLogger.error("Error in server get smart folder by category", e);
			throw new Exception(e.getMessage());
		}

	}


	/**
	 * Gets the lists children by parents.
	 *
	 * @param listSubTree the list sub tree
	 * @param parent the parent
	 * @return the lists children by parents
	 * @throws Exception the exception
	 */
	private void getListsChildrenByParent(ArrayList<SubTree> listSubTree, WorkspaceItem parent) throws Exception{

		if(parent==null)
			return;

		if(!parent.isFolder()){
			workspaceLogger.warn("getListsChildrenByParent returning: "+parent.getName() +" is not a folder");
			return;
		}

		workspaceLogger.trace("getListsChildrenByParent: "+parent.getName());
		GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
		FolderModel parentModel = (FolderModel) builder.buildGXTFileModelItem(parent, null); //get folder
		List<FileModel> childrenList = getFolderChildren(parentModel); //get children
		SubTree subTree = new SubTree(parentModel, childrenList);
		listSubTree.add(subTree);
		getListsChildrenByParent(listSubTree, parent.getParent());
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#createSmartFolder(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SmartFolderModel createSmartFolder(String name, String description, String query, String parentId) throws Exception {

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("create smart folder by name: "+name);
			workspaceLogger.trace("description " + description);
			workspaceLogger.trace("query " + query);
			workspaceLogger.trace("parentId " + parentId);

			if(parentId==null || parentId.isEmpty()){
				workspaceLogger.trace("parent id is null using root id");
				parentId = workspace.getRoot().getId();
			}

			WorkspaceSmartFolder wsSmartFolder  = workspace.createSmartFolder(name, description, query, parentId); //create Smart Folder from workspace
			workspaceLogger.trace("create : " +wsSmartFolder.getName()  + " id "+ wsSmartFolder.getId());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.buildGXTSmartFolderModel(wsSmartFolder,query);

		} catch (Exception e) {
			workspaceLogger.error("Error in server create smart folder by name: ", e);
			//			workspaceLogger.trace("Error in server create smart folder by id " + e);
			//GWT can't serialize all exceptions
			throw new Exception(e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#removeSmartFolder(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean removeSmartFolder(String itemId, String name) throws Exception {

		if(itemId==null)
			return null;

		try {

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("remove smart folder by id: "+itemId);
			workspace.removeItem(itemId); //remove Smart Folder from workspace
			return true;

		} catch (Exception e) {
			workspaceLogger.error("Error in remove smart folder by id: ", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getAllSmartFolders()
	 */
	@Override
	public List<SmartFolderModel> getAllSmartFolders() throws Exception{

		try {

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("get all smart folder");
			List<WorkspaceSmartFolder> listWorkspaceFolder = new ArrayList<WorkspaceSmartFolder>();
			listWorkspaceFolder = workspace.getAllSmartFolders(); //create Smart Folder from workspace
			workspaceLogger.trace("list smart folders size" + listWorkspaceFolder.size());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.buildGXTListSmartFolderModel(listWorkspaceFolder);

		} catch (Exception e) {
			workspaceLogger.error("Error in server get all smart folder: ", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getSmartFolderResultsById(java.lang.String)
	 */
	@Override
	public List<FileGridModel> getSmartFolderResultsById(String folderId) throws Exception {

		if(folderId == null)
			return null;

		try {

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("get smart folder by id: "+folderId);
			WorkspaceSmartFolder wsSmartFolder  = workspace.getSmartFolder(folderId); //get Smart Folder from workspace
			workspaceLogger.trace("wsFolder " + wsSmartFolder.getName());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();

			List<? extends SearchItem> searchItems = wsSmartFolder.getSearchItems();

			if(searchItems!=null){
				workspaceLogger.trace("smart folders size "+searchItems.size());
				return builder.buildGXTListFileGridModelItemForSearch((List<SearchItem>) searchItems);
			}else
				return new ArrayList<FileGridModel>();

		} catch (Exception e) {
			workspaceLogger.error("Error in get server smart folder by id", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getImageById(java.lang.String, boolean, boolean)
	 */
	@Override
	public GWTWorkspaceItem getImageById(String identifier, boolean isInteralImage, boolean fullDetails) throws Exception {

		if(identifier==null)
			return null;

		try {

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("get image by id: "+identifier);
			WorkspaceItem item = workspace.getItem(identifier); //get item from workspace
			workspaceLogger.trace("item name " + item.getName());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.buildGWTWorkspaceImage(item, isInteralImage, fullDetails);

		} catch (Exception e) {
			workspaceLogger.error("Error in server get image by id", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getTimeSeriesById(java.lang.String)
	 */
	@Override
	@Deprecated
	/**
	 * this method return always null
	 */
	public GWTWorkspaceItem getTimeSeriesById(String identifier) throws Exception {

		if(identifier==null)
			return null;

		try {

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("get timeseries by id: "+identifier);
			WorkspaceItem item = workspace.getItem(identifier); //get item from workspace
			workspaceLogger.trace("item name " + item.getName());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return null;
		} catch (Exception e) {
			workspaceLogger.error("Error in server get timeseries by id", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getUrlById(java.lang.String, boolean, boolean)
	 */
	@Override
	public GWTWorkspaceItem getUrlById(String identifier, boolean isInternalUrl, boolean fullDetails) throws Exception {

		try {

			if(identifier==null)
				throw new Exception(IDENTIFIER_IS_NULL);

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("get url by id: "+identifier);
			WorkspaceItem item = workspace.getItem(identifier); //get item from workspace
			workspaceLogger.trace("item name " + item.getName());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.buildGWTWorspaceUrl(item, isInternalUrl, fullDetails);

		} catch (Exception e) {
			workspaceLogger.error("Error in server get image by id ", e);
			//			workspaceLogger.trace("Error in server get image by id  " + e);
			//GWT can't serialize all exceptions
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#createExternalUrl(org.gcube.portlets.user.workspace.client.model.FileModel, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public FileModel createExternalUrl(FileModel parentFileModel, String name, String description, String url) throws Exception {

		try {
			Workspace workspace = getWorkspace();

			if(parentFileModel==null)
				throw new Exception("Parent item is null");

			workspaceLogger.trace("create url in parent id: "+parentFileModel.getIdentifier());

			//DEBUG
			//			workspaceLogger.trace("Name " + name);
			//			workspaceLogger.trace("description " + description);
			//			workspaceLogger.trace("url " + url);
			//			workspaceLogger.trace("parentFileModel " + parentFileModel.getIdentifier() + " " + parentFileModel.getName());
			//			if(description == null)
			//				description = "";

			ExternalUrl ext = workspace.createExternalUrl(name, description, url, parentFileModel.getIdentifier());
			WorkspaceItem parent = workspace.getItem(parentFileModel.getIdentifier()); //get item from workspace
			workspaceLogger.trace("parent name " + parent.getName());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.buildGXTFileModelItem(ext, parentFileModel);

		} catch (Exception e) {
			workspaceLogger.error("Error in server create url in parent id ", e);
			//			workspaceLogger.trace("Error in server create url in parent id " + e);
			//GWT can't serialize all exceptions
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getURLFromApplicationProfile(java.lang.String)
	 */
	@Override
	public String getURLFromApplicationProfile(String oid) throws Exception {

		String urlPortlet = "";

		ApplicationReaderFromGenericResource app = new ApplicationReaderFromGenericResource();

		try{

			if(oid==null)
				throw new Exception(IDENTIFIER_IS_NULL);

			ASLSession session = WsUtil.getAslSession(this.getThreadLocalRequest().getSession());

			if(WsUtil.isVRE(session)){

				ScopeProvider.instance.set(session.getScope());
				// GET WORKSPACE
				Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());
				// GET ITEM FROM WORKSPACE
				WorkspaceItem item = workspace.getItem(oid);

				// ITEM IS A WorkspaceItemType.FOLDER_ITEM?
				if (item.getType().equals(WorkspaceItemType.FOLDER_ITEM)) {
					FolderItem folderItem = (FolderItem) item;

					if(folderItem.getFolderItemType().equals(FolderItemType.REPORT)){
						setValueInSession("idreport", oid);

					}else if(folderItem.getFolderItemType().equals(FolderItemType.REPORT_TEMPLATE)){
						setValueInSession("idtemplate", oid);
					}
					return "";
				}
			}
			else
				urlPortlet = app.getURLFromApplicationProfile(oid, WsUtil.getAslSession(this.getThreadLocalRequest().getSession()),this.getThreadLocalRequest().getSession());

		} catch (Exception e) {
			workspaceLogger.error("getURLFromApplicationProfile", e);
			throw new Exception("Sorry, an error occurred in retrieve application profile, try again");
		}

		return urlPortlet;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#setValueInSession(java.lang.String, java.lang.String)
	 */
	@Override
	public void setValueInSession(String name, String value) throws Exception {

		try{

			ASLSession session = WsUtil.getAslSession(this.getThreadLocalRequest().getSession());
			session.setAttribute(name, value);
			workspaceLogger.trace("set value in session with name: "+name+", value: "+value);
		} catch (Exception e) {
			workspaceLogger.error("setValueInSession", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getAllScope()
	 */
	@Override
	public List<ScopeModel> getAllScope() throws Exception {

		try {

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("get all scope");
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			ScopeUtilFilter scopeUtilFilter = getScopeUtilFilter();

			Home home = workspace.getHome();
			if(home!=null){
				List<String> listIdFilterdScope = scopeUtilFilter.convertListScopeToPortlet(home.listScopes());
				return builder.buildGXTListScopeModel(listIdFilterdScope, scopeUtilFilter.getHashScopesFiltered());
			}else{
				workspaceLogger.error("workspace.getHome() is null");
				throw new Exception("Sorry, an error occurred on getting all scope. Please try later");
			}

		} catch (Exception e) {
			workspaceLogger.error("Error in server get all scope ", e);
			e.printStackTrace();
			//			workspaceLogger.trace("Error in server get all scope " + e.getMessage());
			//GWT can't serialize all exceptions
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getAllContacts()
	 */
	@Override
	public List<InfoContactModel> getAllContacts() throws Exception {

		try {

			workspaceLogger.debug("Get all contacts from server...");
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			getWorkspace(); //DO NOT REMOVE, IT IS NEEDED TO SET TOKEN IN ECLIPSE
			org.gcube.common.homelibrary.home.workspace.usermanager.UserManager hlUserManager = HomeLibrary.getHomeManagerFactory().getUserManager();

			if(isTestMode()){

				workspaceLogger.warn("WORKSPACE PORTLET IS IN TEST MODE - RETURN TEST USERS");
				List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();
				HashMap<String, InfoContactModel> hashFakeUsers = GWTWorkspaceBuilder.getHashTestUsers();
				for (String login : hashFakeUsers.keySet()) {

					InfoContactModel contact = hashFakeUsers.get(login);
					listContactsModel.add(contact);

				}
				//				//TEST USERS
				//				listContactsModel.add(new InfoContactModel("antonio.gioia", "antonio.gioia", "Antonio Gioia",false));
				//				listContactsModel.add(new InfoContactModel("fabio.sinibaldi", "fabio.sinibaldi", "Fabio Sinibaldi",false));
				//				listContactsModel.add(new InfoContactModel("pasquale.pagano", "pasquale.pagano", "Pasquale Pagano",false));
				//				listContactsModel.add(new InfoContactModel(WsUtil.TEST_USER, WsUtil.TEST_USER, WsUtil.TEST_USER_FULL_NAME,false));
				//				listContactsModel.add(new InfoContactModel("francesco.mangiacrapa", "francesco.mangiacrapa", "Francesco Mangiacrapa",false));
				//				listContactsModel.add(new InfoContactModel("massimiliano.assante", "massimiliano.assante", "Massimiliano Assante",false));

				workspaceLogger.trace("Home Library User Manager getting list Gcube Group");
				listContactsModel.addAll(builder.buildGXTListContactsModelFromGcubeGroup(hlUserManager.getGroups()));

				return listContactsModel;

			}

			UserManager userManag = new LiferayUserManager();
			GroupManager gm = new LiferayGroupManager();
			long groupId = gm.getRootVO().getGroupId();

			workspaceLogger.trace("Liferay User Manager getting list users by group: "+groupId);
			List<InfoContactModel> listContactsModel = builder.buildGXTListContactsModelFromUserModel(userManag.listUsersByGroup(groupId));

			workspaceLogger.trace("Home Library User Manager getting list Gcube Group");
			listContactsModel.addAll(builder.buildGXTListContactsModelFromGcubeGroup(hlUserManager.getGroups()));

			workspaceLogger.debug("Returning list of contacts");

			return listContactsModel;

		} catch (Exception e) {
			workspaceLogger.error("Error in server get all contacts ", e);
			//			return new ArrayList<InfoContactModel>();
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#sendToById(java.util.List, java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean sendToById(List<String> listContactsId, List<String> listAttachmentsId, String subject, String body)  throws Exception {

		try {

			Workspace workspace = getWorkspace();
			//			workspaceLogger.trace("######### SEND TO: ");
			//			workspaceLogger.trace("subject " + subject);
			//			workspaceLogger.trace("body " + body);

			//DEBUG
			for(String contactId : listContactsId)
				workspaceLogger.trace("contactId " + contactId);
			//DEBUG
			for(String id : listAttachmentsId)
				workspaceLogger.trace("attachId " + id);

			workspace.getWorkspaceMessageManager().sendMessageToPortalLogins(subject, body, listAttachmentsId, listContactsId);
			return true;

		} catch (Exception e) {
			workspaceLogger.error("Error in server sendTo ", e);
			//GWT can't serialize all exceptions
			throw new Exception(e.getMessage());
		}

	}

	/**
	 * Gets the list workspace item by id.
	 *
	 * @param workspace the workspace
	 * @param listItemsId the list items id
	 * @return the list workspace item by id
	 * @throws ItemNotFoundException the item not found exception
	 * @throws InternalErrorException the internal error exception
	 */
	private List<WorkspaceItem> getListWorkspaceItemById(Workspace workspace, List<String> listItemsId) throws ItemNotFoundException, InternalErrorException{

		List<WorkspaceItem> listWorkspaceItem = new ArrayList<WorkspaceItem>();

		for(String itemId: listItemsId){

			WorkspaceItem item = workspace.getItem(itemId);
			workspaceLogger.trace("Attach name: " +item.getName());
			workspaceLogger.trace("Attach id: " +item.getId());
			listWorkspaceItem.add(workspace.getItem(itemId)); //get item from workspace
		}

		return listWorkspaceItem;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#copyItem(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean copyItem(String itemId, String destinationFolderId) throws Exception {

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem sourceItem = workspace.getItem(itemId); //GET SOURCE ITEM BEFORE COPY
			String sourceSharedId = sourceItem.getIdSharedFolder();
			workspaceLogger.trace("copyItem by id: " + itemId);
			WorkspaceItem destinationItem = workspace.copy(itemId, destinationFolderId);  //copy item
			WorkspaceItem folderDestinationItem = workspace.getItem(destinationFolderId);
			checkNotifyAddItemToShare(destinationItem, sourceSharedId, folderDestinationItem);

			if(destinationItem!=null)
				return true;

			return false;
		}catch (InsufficientPrivilegesException e) {
			String error = "An error occurred on copying item, "  +e.getMessage() + ". "+ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);

		}catch (ItemAlreadyExistException e) {
			String error = "An error occurred on copying item, "  +e.getMessage();
			throw new Exception(error);
		} catch (Exception e) {
			workspaceLogger.error("Error in server copyItem  by id", e);
			String error  = ConstantsExplorer.SERVER_ERROR +" copying item "  + ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#copyItems(java.util.List, java.lang.String)
	 */
	@Override
	public boolean copyItems(List<String> idsItem, String destinationFolderId) throws Exception {

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			Workspace workspace = getWorkspace();
			boolean error = false;

			for (String itemId : idsItem) {

				WorkspaceItem sourceItem = workspace.getItem(itemId); //GET SOURCE ITEM BEFORE COPY
				String sourceSharedId = sourceItem.getIdSharedFolder();
				workspaceLogger.trace("copyItem by id: " + itemId);
				WorkspaceItem destinationItem = workspace.copy(itemId, destinationFolderId);  //copy item
				WorkspaceItem folderDestinationItem = workspace.getItem(destinationFolderId);
				checkNotifyAddItemToShare(destinationItem, sourceSharedId, folderDestinationItem);

				if(destinationItem==null){
					error = true;
					break;
				}
			}

			if(error)
				return false; //Copied is false

			return true; //copied is true

		}catch (InsufficientPrivilegesException e) {
			String error = "An error occurred on copying item, "  +e.getMessage() + ". "+ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);

		}catch (ItemAlreadyExistException e) {
			String error = "An error occurred on copying item, "  +e.getMessage();
			throw new Exception(error);
		} catch (Exception e) {
			workspaceLogger.error("Error in server copyItem  by id", e);
			String error  = ConstantsExplorer.SERVER_ERROR +" copying item "  + ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getUrlWebDav(java.lang.String)
	 */
	@Override
	public String getUrlWebDav(String itemId) throws Exception {

		try {
			Workspace workspace = getWorkspace();
			workspaceLogger.trace("getWebDavUrl" + itemId);
			return workspace.getUrlWebDav();

		} catch (Exception e) {
			workspaceLogger.error("Error in getNewFolderBulkCreator ", e);
			//			workspaceLogger.trace("Error in getNewFolderBulkCreator " + e.getMessage());
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#shareFolder(org.gcube.portlets.user.workspace.client.model.FileModel, java.util.List, boolean, org.gcube.portlets.user.workspace.shared.WorkspaceACL)
	 */
	@Override
	public boolean shareFolder(FileModel folder, List<InfoContactModel> listContacts, boolean isNewFolder, WorkspaceACL acl) throws Exception {

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			Workspace workspace = getWorkspace();

			workspaceLogger.info("shareFolder "+ folder.getIdentifier()
			+ " name: "+ folder.getName()
			+ " parent is: " + folder.getParentFileModel()
			+ " listContacts size: " + listContacts.size()
			+" ACL: "+acl);

			//DEBUG
			printContacts(listContacts);

			List<String> listLogin = UserUtil.getListLoginByInfoContactModel(listContacts);
			WorkspaceSharedFolder sharedFolder = null;
			List<InfoContactModel> listSharedContact = null;

			boolean sourceFolderIsShared = folder.isShared();

			if(sourceFolderIsShared){ //if source folder is already share... retrieve old list of sharing to notify
				listSharedContact = getListUserSharedByFolderSharedId(folder.getIdentifier());
			}

			if(listLogin.size()>0){

				if(!isNewFolder){

					sharedFolder = workspace.shareFolder(listLogin, folder.getIdentifier());
					sharedFolder.setDescription(folder.getDescription()); //SET NEW DESCRIPTION

					//USER REMOVED FROM SHARE
					DifferenceBetweenInfoContactModel diff2 = new DifferenceBetweenInfoContactModel(listSharedContact, listContacts);
					List<InfoContactModel> listRemovedUsersFromShare = diff2.getDifferentsContacts();
					workspaceLogger.info("List removed user from share has size: "+listRemovedUsersFromShare.size());
					for (InfoContactModel userRemoved : listRemovedUsersFromShare) {
						workspaceLogger.info("Unsharing user: "+userRemoved.getLogin());
						sharedFolder.unShare(userRemoved.getLogin());
					}
				}
				else{
					FileModel parent = folder.getParentFileModel();
					String parentId = "";
					if(parent!=null){
						parentId = parent.getIdentifier();
					}else{
						workspaceLogger.info("Parent is null, reading root ID from workspace");
						parentId = getWorkspace().getRoot().getId();
					}
					sharedFolder = workspace.createSharedFolder(folder.getName(), folder.getDescription(), listLogin, parentId);
				}
			}

			boolean created = sharedFolder==null?false:true;

			if(acl!=null)
				setACLs(sharedFolder.getId(), listLogin, acl.getId().toString());

			if(created){
				NotificationsProducer np = getNotificationProducer();
				if(!sourceFolderIsShared) //if source folder is not already shared
					np.notifyFolderSharing(listContacts, sharedFolder);
				else{
					//					printContacts(listContacts);
					np.notifyUpdatedUsersToSharing(listSharedContact, listContacts, sharedFolder);
				}
			}

			return created;

		} catch (InsufficientPrivilegesException e) {
			workspaceLogger.error("Error in shareFolder ", e);
			String error = "An error occurred on creating shared folder. "+ e.getMessage();
			throw new Exception(error);

		} catch (ItemAlreadyExistException e) {
			workspaceLogger.error("Error in shareFolder ", e);
			String error = "An error occurred on creating shared folder. "+ e.getMessage();
			throw new Exception(error);

		} catch (WrongDestinationException e) {
			workspaceLogger.error("Error in shareFolder ", e);
			String error = "An error occurred on creating shared folder. "+ e.getMessage();
			throw new Exception(error);

		} catch (Exception e) {
			workspaceLogger.error("Error in shareFolder ", e);
			e.printStackTrace();
			String error = ConstantsExplorer.SERVER_ERROR+" creating shared folder.";
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getListUserSharedByFolderSharedId(java.lang.String)
	 */
	@Override
	public List<InfoContactModel> getListUserSharedByFolderSharedId(String folderSharedId) throws Exception{

		workspaceLogger.debug("getListUserSharedByFolderSharedId "+ folderSharedId);

		try {
			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(folderSharedId);

			if(NotificationsUtil.isASharedFolder(wsItem)){

				WorkspaceSharedFolder wsFolder = (WorkspaceSharedFolder) wsItem;
				GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
				List<String> listPortalLogin = wsFolder.getUsers();
				workspaceLogger.debug("HL return "+ listPortalLogin.size() + " user/s");

				if(isTestMode())
					return builder.buildGxtInfoContactFromPortalLoginTestMode(listPortalLogin);

				return builder.buildGxtInfoContactsFromPortalLogins(listPortalLogin);
			}
			else
				workspaceLogger.trace("the item with id: "+folderSharedId+ " is not  "+WorkspaceItemType.SHARED_FOLDER);

			return new ArrayList<InfoContactModel>();

		} catch (Exception e) {
			workspaceLogger.error("Error in getListUserSharedByItemId ", e);
			throw new Exception(e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getListUserSharedBySharedItem(java.lang.String)
	 */

	/*@Override
	 public List<InfoContactModel> getListUserSharedBySharedItem(String sharedItemId) throws Exception{
		workspaceLogger.trace("Get ListUserSharedBySharedItem "+ sharedItemId);
		try {

			WorkspaceFolder wsFolder = getSharedWorkspaceFolderForId(sharedItemId);
			if(wsFolder!=null){
				if(isASharedFolder(wsFolder, true)){
					GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();

					WorkspaceSharedFolder wsSharedFolder = (WorkspaceSharedFolder) wsFolder;
					List<String> listPortalLogin = wsSharedFolder.getUsers();
					workspaceLogger.trace("getListUserSharedByFolderSharedId return "+ listPortalLogin.size() + " user/s");

					if(isTestMode())
						return builder.buildGxtInfoContactFromPortalLoginTestMode(listPortalLogin);

					return builder.buildGxtInfoContactsFromPortalLogins(listPortalLogin);
				}else{
					workspaceLogger.warn("wsFolder with id: "+sharedItemId +" is not a: "+WorkspaceItemType.SHARED_FOLDER +", returning null");
					return null;
				}
			}
			workspaceLogger.warn("wsFolder with id: "+sharedItemId +" is null, returning null");
			return null;

		} catch (Exception e) {
			workspaceLogger.error("Error in getListUserSharedByItemId ", e);
			throw new Exception(e.getMessage());
		}
	}*/

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#unSharedFolderByFolderSharedId(java.lang.String)
	 */
	@Override
	public boolean unSharedFolderByFolderSharedId(String folderSharedId) throws Exception{

		boolean unShared = false;

		if(isSessionExpired())
			throw new SessionExpiredException();

		workspaceLogger.trace("unSharedFolderByFolderSharedId "+ folderSharedId);

		try {

			if(isASharedFolder(folderSharedId, true)){
				Workspace workspace = getWorkspace();
				WorkspaceItem wsItem = workspace.getItem(folderSharedId);
				workspaceLogger.trace("workspace return an item with name "+wsItem.getName());
				WorkspaceSharedFolder wsFolder = (WorkspaceSharedFolder) wsItem;

				List<InfoContactModel> contacts = getListUserSharedByFolderSharedId(folderSharedId);
				String sharedFolderName = wsFolder.getName();

				//RETURN A NEW FOLDER
				wsFolder.unShare();
				workspaceLogger.trace("no error incoming on unsharing");
				unShared = true;

				NotificationsProducer np = getNotificationProducer();
				List<InfoContactModel> purgeMyContact = new ArrayList<InfoContactModel>(contacts.size()-1);
				String myLogin = getMyLogin(null).getUsername();
				workspaceLogger.trace("Preparing list of contacts to send un share notification");
				for (InfoContactModel infoContactModel : contacts) {
					if(infoContactModel.getLogin().compareToIgnoreCase(getMyLogin(null).getUsername())==0)
						workspaceLogger.trace("skipping my login "+myLogin);
					else
						purgeMyContact.add(infoContactModel);
				}
				workspaceLogger.trace("UNSHARE WITH: ");
				printContacts(purgeMyContact);

				np.notifyFolderUnSharing(purgeMyContact, folderSharedId, sharedFolderName);
			}
			else{
				String msg = "The item with id: "+folderSharedId+ "is not a base shared folder";
				workspaceLogger.warn("the item with id: "+folderSharedId+ "is not  "+WorkspaceItemType.SHARED_FOLDER +" or root shared folder");
				throw new WorkspaceHandledException(msg);
			}

		} catch (InternalErrorException e) {
			workspaceLogger.error("Error in unSharedFolderByFolderSharedId ", e);
			String error = "An error occerred on unsharing folder. "+ e.getMessage();
			throw new Exception(error);

		} catch (WorkspaceHandledException e) {
			String error = ConstantsExplorer.SERVER_ERROR+" unshare folder. "+e.getMessage();
			throw new Exception(error);

		} catch (Exception e) {
			workspaceLogger.error("Error in unSharedFolderByFolderSharedId ", e);
			String error = ConstantsExplorer.SERVER_ERROR+" unshare folder. Refresh folder and " +ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);
		}

		return unShared;
	}

	/**
	 * Gets the list parents by item identifier.
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent -  if parameter is true and item passed in input is a folder, the folder is included in path returned as last parent
	 * @return the list parents by item identifier
	 * @throws Exception the exception
	 */
	@Override
	public List<FileModel> getListParentsByItemIdentifier(String itemIdentifier, boolean includeItemAsParent) throws Exception {

		workspaceLogger.debug("get List Parents By Item Identifier "+ itemIdentifier +", include Item As (Last) Parent: "+includeItemAsParent);

		if(isSessionExpired())
			throw new SessionExpiredException();

		if(itemIdentifier==null)
			return new ArrayList<FileModel>(); //empty list

		try {

			Workspace workspace = getWorkspace();
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			List<WorkspaceItem> parents = workspace.getParentsById(itemIdentifier);
			workspaceLogger.debug("parents returned by HL, size: "+ parents.size());
			String nameSpecialFolder = getNameForSpecialFolder();

			switch (parents.size()) {
			case 0: // itemIdentifier is ROOT
				workspaceLogger.trace("itemIdentifier isRoot...");
				if (includeItemAsParent) { //ADDIND ROOT
					WorkspaceFolder wsFolder =(WorkspaceFolder) workspace.getItem(itemIdentifier);
					FolderModel root = builder.buildGXTFolderModelItemHandleSpecialFolder(wsFolder, null, nameSpecialFolder);
					List<FileModel> listParents = new ArrayList<FileModel>(1);
					listParents.add(root);
					//					workspaceLogger.trace("returning: "+listParents.toString());
					return listParents;
				}
				else{
					workspaceLogger.trace("returning empty list");
					return new ArrayList<FileModel>(); // empty list
				}

			case 1: //itemIdentifier is first level from root
				workspaceLogger.trace("itemIdentifier is first level...");
				List<FileModel> listParents = new ArrayList<FileModel>();
				WorkspaceFolder wsRootFolder = (WorkspaceFolder) parents.get(0); //isRoot
				FolderModel root = builder.buildGXTFolderModelItemHandleSpecialFolder(wsRootFolder, null, nameSpecialFolder);
				FolderModel parent = null;
				if(includeItemAsParent){
					WorkspaceFolder wsFolder1 =(WorkspaceFolder) workspace.getItem(itemIdentifier); //root
					parent = builder.buildGXTFolderModelItemHandleSpecialFolder(wsFolder1, null, nameSpecialFolder);
				}
				listParents.add(root);
				if(parent!=null)
					listParents.add(parent);
				//				workspaceLogger.trace("returning: "+listParents.toString());
				return listParents;

			default:
				break;
			}

			WorkspaceItem lastItem =  parents.get(parents.size()-1);
			FileModel[] arrayParents;

			//CONVERTING LAST ELEMENT IF NECESSARY
			workspaceLogger.trace("converting last element..");
			if(includeItemAsParent && lastItem.isFolder()){ //FIX BUG #298
				arrayParents = new FileModel[parents.size()];
				workspaceLogger.debug("including last item into path: "+lastItem.getName());
				arrayParents[parents.size()-1] = builder.buildGXTFolderModelItemHandleSpecialFolder((WorkspaceFolder) lastItem, null, nameSpecialFolder);
			}else
				arrayParents = new FileModel[parents.size()-1];

			//CONVERTING PATH
			workspaceLogger.trace("converting path from second-last..");
			for (int i =  parents.size()-2; i >= 0; i--) {
				WorkspaceFolder wsFolder = (WorkspaceFolder) parents.get(i);
				arrayParents[i] = builder.buildGXTFolderModelItemHandleSpecialFolder(wsFolder, null, nameSpecialFolder);
			}

			//SET PARENTS
			workspaceLogger.trace("setting parents..");
			for(int i=0; i<arrayParents.length-1; i++){
				FileModel parent = arrayParents[i];
				FileModel fileModel = arrayParents[i+1];
				fileModel.setParentFileModel(parent);
			}
			//			workspaceLogger.trace("list parents returning size: "+arrayParents.length);
			//			return new ArrayList<FileModel>(Arrays.asList(arrayParents));
			workspaceLogger.trace("list parents returning size: "+arrayParents.length);
			if(arrayParents[0]==null){ //EXIT BY BREAK IN CASE OF SPECIAL FOLDER
				List<FileModel> breadcrumbs = new ArrayList<FileModel>(arrayParents.length-1);
				for (int i=1; i<arrayParents.length; i++) {
					breadcrumbs.add(arrayParents[i]);
				}
				return breadcrumbs;
			}else
				return new ArrayList<FileModel>(Arrays.asList(arrayParents));

		} catch (Exception e) {
			workspaceLogger.error("Error in get List Parents By Item Identifier ", e);
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getOwnerByItemId(java.lang.String)
	 */
	@Override
	public InfoContactModel getOwnerByItemId(String itemId) throws Exception {

		workspaceLogger.trace("get Owner By ItemId "+ itemId);
		try {

			//TEST MODE
			if(!isWithinPortal()){
				workspaceLogger.info("getOwnerByItemId is in test mode returning owner francesco.mangiacrapa");
				return new InfoContactModel("francesco.mangiacrapa", "francesco.mangiacrapa", "Francesco Mangiacrapa", false);
			}

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemId);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.buildGXTInfoContactModel(wsItem.getOwner());

		} catch (Exception e) {
			workspaceLogger.error("Error in getOwnerByItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getUsersManagerToSharedFolder(java.lang.String)
	 */
	@Override
	public List<InfoContactModel> getUsersManagerToSharedFolder(String folderId) throws Exception{

		try{

			workspaceLogger.info("Get User Manager to shared folder id: "+folderId);
			Workspace workspace = getWorkspace();
			List<InfoContactModel> listManagers = new ArrayList<InfoContactModel>();
			workspaceLogger.info("Adding owner..");
			InfoContactModel owner = getOwnerByItemId(folderId); //GET OWNER
			workspaceLogger.info("Added owner: "+owner);
			listManagers.add(owner);
			WorkspaceItem wsItem = workspace.getItem(folderId);

			if(wsItem.isShared() && wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
				WorkspaceSharedFolder ite = (WorkspaceSharedFolder) wsItem;

				workspaceLogger.info("Retrieving administrators...");
				Map<ACLType, List<String>> aclOwner = ite.getACLOwner();
				if(aclOwner!=null){
					List<String> listLogins = new ArrayList<String>();
					workspaceLogger.info("Adding administrators...");
					for (ACLType type : aclOwner.keySet()) {
						switch (type) {
						case ADMINISTRATOR:
							listLogins.addAll(aclOwner.get(type)); //ADD ALL ADMINISTRATORS
							break;
						}
					}
					workspaceLogger.info("Added " +listLogins.size() +"administrators, converting into InfoContactModel");
					GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();

					List<InfoContactModel> adms = builder.buildGxtInfoContactsFromPortalLogins(listLogins);
					listManagers.addAll(adms);
					workspaceLogger.info("Returing" +listManagers.size() +"users managers");
					return listManagers;

				}
			}else
				throw new Exception("Source item is not shared or shared folder");

		}catch (Exception e) {
			String error = "Sorry an error occurred on get managers ";
			workspaceLogger.error(error, e);
			throw new Exception(e.getMessage());
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#itemExistsInWorkpaceFolder(java.lang.String, java.lang.String)
	 */
	@Override
	public String itemExistsInWorkpaceFolder(String parentId, String itemName) throws Exception {

		workspaceLogger.trace("get itemExistsInWorkpace by parentId: "+parentId);
		System.out.println("get itemExistsInWorkpace by parentId: "+parentId);

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(parentId); //GET PARENT

			if(wsItem.getType().equals(WorkspaceItemType.FOLDER) || wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){

				WorkspaceItem itemFound = workspace.find(itemName, parentId);

				if(itemFound==null)
					return null;

				return itemFound.getId();
			}
			else
				throw new Exception("Invalid Folder parent");

		} catch (InternalErrorException e) {
			return null;
		} catch (ItemNotFoundException e) {
			return null;
		} catch (Exception e) {
			String error = "an error occurred on search item in folder ";
			workspaceLogger.error(error, e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getItemCreationDateById(java.lang.String)
	 */
	@Override
	public Date getItemCreationDateById(String itemId) throws Exception {
		workspaceLogger.trace("get Item Creation Date By ItemId "+ itemId);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemId);
			Calendar cl = wsItem.getCreationTime();

			if(cl!=null)
				return  cl.getTime();

			return null;

		} catch (Exception e) {
			workspaceLogger.error("get Item Creation Date By ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#loadSizeByItemId(java.lang.String)
	 */
	@Override
	public Long loadSizeByItemId(String itemId) throws Exception {

		workspaceLogger.info("get Size By ItemId "+ itemId);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemId);
			Long size = new Long(-1);

			if(wsItem instanceof FolderItem){ //ITEM
				FolderItem folderItem = (FolderItem) wsItem;
				size = new Long(folderItem.getLength());
			} else if (wsItem instanceof WorkspaceFolder ){ //FOLDER
				WorkspaceFolder theFolder = (WorkspaceFolder) wsItem;
				size = theFolder.getSize();
			} else if (wsItem instanceof WorkspaceSharedFolder){ //SHARED FOLDER
				WorkspaceSharedFolder theFolder = (WorkspaceSharedFolder) wsItem;
				size = theFolder.getSize();
			}
			workspaceLogger.info("returning size: " +size);
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

		workspaceLogger.trace("get last modification date ItemId "+ itemId);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemId);
			wsItem.getLastModificationTime().getTime();
			Date lastModification =null;

			if(wsItem.getLastModificationTime()!=null){
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
	@Override
	public FileModel getParentByItemId(String identifier) throws Exception {

		workspaceLogger.trace("get Parent By Item Identifier "+ identifier);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(identifier);
			workspaceLogger.trace("workspace retrieve item name: "+wsItem.getName());
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();

			while(wsItem!=null && wsItem.getParent()!=null){

				WorkspaceFolder wsFolder = wsItem.getParent();
				workspaceLogger.trace("parent was found "+wsFolder.getName()+ " retuning");
				return builder.buildGXTFolderModelItem(wsFolder, null);
			}
			workspaceLogger.trace("parent not found - retuning");
			return null;

		} catch (Exception e) {
			workspaceLogger.error("Error in get Parent By Item Identifier", e);
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getAccountingReaders(java.lang.String)
	 */
	@Override
	public List<GxtAccountingField> getAccountingReaders(String identifier) throws Exception {

		workspaceLogger.trace("get accounting readers "+ identifier);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(identifier);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			List<GxtAccountingField> listReaders = builder.buildGXTAccountingItemFromReaders(wsItem.getReaders());
			workspaceLogger.trace("get accounting readers - returning size "+listReaders.size());
			return listReaders;

		} catch (Exception e) {
			workspaceLogger.error("Error get accounting readers ", e);
			String error = ConstantsExplorer.SERVER_ERROR+" getting account. "+ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getAccountingHistory(java.lang.String)
	 */
	@Override
	public List<GxtAccountingField> getAccountingHistory(String identifier) throws Exception {

		workspaceLogger.trace("get accounting history "+ identifier);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(identifier);
			List<AccountingEntry> accoutings = wsItem.getAccounting();
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			List<GxtAccountingField> listReaders = builder.buildGXTAccountingItem(accoutings, GxtAccountingEntryType.ALL);
			workspaceLogger.trace("get accounting readers - returning size "+listReaders.size());
			return listReaders;

		} catch (Exception e) {
			workspaceLogger.error("Error get accounting readers ", e);
			String error = ConstantsExplorer.SERVER_ERROR+" getting account. "+ConstantsExplorer.TRY_AGAIN;
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getShortUrl(java.lang.String)
	 */
	@Override
	public String getShortUrl(String longUrl) throws Exception {

		workspaceLogger.trace("get short url for "+ longUrl);
		UrlShortener shortener = getUrlShortener();
		try{

			if(shortener!=null && shortener.isAvailable())
				return shortener.shorten(longUrl);

			return longUrl;

		}catch (Exception e) {
			workspaceLogger.error("Error get short url for ", e);
			return null;
		}
	}

	/**
	 * Gets the public link for folder item id.
	 *
	 * @param itemId the item id
	 * @param shortenUrl the shorten url
	 * @return the public link for folder item id
	 * @throws Exception the exception
	 */
	@Override
	public PublicLink getPublicLinkForFolderItemId(String itemId, boolean shortenUrl) throws Exception{

		workspaceLogger.trace("get Public Link For ItemId: "+ itemId);
		try{

			if(itemId==null)
				throw new Exception("Sorry, The Public Link for empty item is unavailable (itemId is null)");

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemId);

			if(wsItem==null)
				throw new Exception("Sorry, The Public Link for empty item is unavailable");

			if(wsItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)){

				FolderItem folderItem = (FolderItem) wsItem;
				String uriRequest = folderItem.getPublicLink(false);

				if(uriRequest==null || uriRequest.isEmpty())
					throw new Exception("Sorry, public link on "+folderItem.getName() +" is not available");

				String shortURL = null;

				if(shortenUrl){
					shortURL = getShortUrl(uriRequest);
					shortURL = shortURL!=null?shortURL:"not available";
				}

				return new PublicLink(uriRequest, shortURL);

			}else{
				workspaceLogger.warn("ItemId: "+ itemId +" is not a folder item, sent exception Public Link unavailable");
				throw new Exception("Sorry, The Public Link for selected file is unavailable");
			}

		}catch (Exception e) {
			workspaceLogger.error("Error getPublicLinkForFolderItemId for item: "+itemId, e);
			throw new Exception(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#isSessionExpired()
	 */
	@Override
	public boolean isSessionExpired() throws Exception {
		return WsUtil.isSessionExpired(this.getThreadLocalRequest().getSession());
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#deleteListItemsForIds(java.util.List)
	 */
	@Override
	public List<GarbageItem> deleteListItemsForIds(List<String> ids) throws Exception {

		try {

			if(ids == null)
				throw new Exception("List identifiers is null");

			Workspace workspace = getWorkspace();
			workspaceLogger.trace("removeItem item for list size: "+ids.size());

			String[] items = new String[ids.size()];
			items = ids.toArray(items);

			Map<String, GarbageItem> garbage = new HashMap<String, GarbageItem>(items.length);

			//SAVE DATE FOR NOTIFICATIONS
			for (String itemId : ids) {
				//NOTIFICATION
				WorkspaceItem wsItem = workspace.getItem(itemId);
				//SAVING ATTRIBUTE FOR NOTIFICATION
				boolean sourceItemIsShared = wsItem.isShared();
				String itemName = wsItem.getName();
				String sourceFolderSharedId = null;
				if(sourceItemIsShared){
					sourceFolderSharedId = wsItem.getIdSharedFolder();
				}
				//REMOVE ITEM

				garbage.put(itemId, new GarbageItem(sourceItemIsShared, itemName, itemId, sourceFolderSharedId));

				//				workspace.removeItem(itemId);
				//				//IF SOURCE SHARED FOLDER IS NOT NULL
				//				if(sourceFolderSharedId!=null)
				//					NotificationsUtil.checkSendNotifyRemoveItemToShare(this.getThreadLocalRequest().getSession(), sourceItemIsShared, itemName, itemId, sourceFolderSharedId);
			}

			//ITEM ID - ERROR
			Map<String, String> backendError = workspace.removeItems(items);

			//GARBAGE ITEM ERROR
			List<GarbageItem> frontEndError = new ArrayList<GarbageItem>(backendError.size());

			//REMOVING IDS WHICH HAVE GENERATED AN ERROR
			for (String idError : backendError.keySet()) {
				GarbageItem gbi = garbage.get(idError);
				if(gbi!=null){
					frontEndError.add(gbi);
					garbage.remove(idError);
				}
			}

			for (String idItem : garbage.keySet()) {
				GarbageItem item = garbage.get(idItem);
				workspaceLogger.trace("Check notification for "+item);
				//IF SOURCE SHARED FOLDER IS NOT NULL
				if(item.getSourceFolderSharedId()!=null)
					NotificationsUtil.checkSendNotifyRemoveItemToShare(this.getThreadLocalRequest(), this.getThreadLocalRequest().getSession(), item.isSourceItemIsShared(), item.getOldItemName(), item.getOldItemName(), item.getSourceFolderSharedId());
			}

			return frontEndError;

		} catch (InsufficientPrivilegesException e) {
			workspaceLogger.error("Error in server Item remove", e);
			String error = "An error occurred on deleting item. "+e.getMessage();
			throw new Exception(error);

		}catch (ItemNotFoundException e) {
			String error = "An error occurred on deleting item. "+ConstantsExplorer.ERROR_ITEM_DOES_NOT_EXIST;
			workspaceLogger.error(error, e);
			throw new Exception(error);
		} catch (Exception e) {
			workspaceLogger.error("Error in server Item remove", e);
			String error = ConstantsExplorer.SERVER_ERROR +" deleting item. "+e.getMessage();
			throw new Exception(error);
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#setACLs(java.lang.String, java.util.List, java.lang.String)
	 */
	@Override
	public void setACLs(String folderId, List<String> listLogins, String aclType) throws Exception{
		try {

			if(folderId == null)
				throw new Exception("Folder id is null");

			if(listLogins==null || listLogins.size()==0)
				throw new Exception("List Logins is null or empty");

			workspaceLogger.trace("Setting ACL for folder id: "+folderId);
			workspaceLogger.trace("ACL type is: "+aclType);
			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(folderId);

			//IS A WORKSPACE FOLDER?
			if(wsItem!= null && wsItem.isFolder() && wsItem.isShared()){
				WorkspaceFolder ite;
				if(wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
					//IS ROOT SHARED FOLDER
					ite = (WorkspaceSharedFolder) wsItem;
					workspaceLogger.trace("Folder " +ite.getName()+" is a "+WorkspaceSharedFolder.class.getName());
					//					ite = (WorkspaceSharedFolder) workspace.getItemByPath(wsItem.getPath());
				}else{
					// IS SUB FOLDER OF THE SHARING
					ite = (WorkspaceFolder) wsItem;
					workspaceLogger.trace("Folder " +ite.getName()+" is a "+WorkspaceFolder.class.getName());
					//					ite = (WorkspaceSharedFolder) workspace.getItem(wsItem.getIdSharedFolder());
				}
				//				validateACLToUser(ite, listLogins, aclType);
				ite.setACL(listLogins, ACLType.valueOf(aclType));
			}else
				throw new Exception("Source item is not shared or shared folder");

			workspaceLogger.info("Setting ACL for "+wsItem.getName()+" completed, returning");
		} catch (Exception e) {
			workspaceLogger.error("Error in set ACLs", e);
			String error = ConstantsExplorer.SERVER_ERROR +" setting permissions. "+e.getMessage();
			throw new Exception(error);
		}
	}

	/**
	 * Validate acl to user.
	 *
	 * @param folderId the folder id
	 * @param listLogins the list logins
	 * @param aclType the acl type
	 * @return the report assignment acl
	 * @throws Exception the exception
	 */
	@Override
	public ReportAssignmentACL validateACLToUser(String folderId, List<String> listLogins, String aclType) throws Exception {

		if(folderId == null)
			throw new Exception("Folder id is null");

		if(listLogins==null || listLogins.size()==0)
			throw new Exception("List Logins is null or empty");

		if(isASharedFolder(folderId, false)){

			try{
				Workspace workspace = getWorkspace();
				WorkspaceFolder folder = (WorkspaceFolder) workspace.getItem(folderId);

				if(!folder.getType().equals(WorkspaceItemType.SHARED_FOLDER)){ //IS NOT ROOT SHARED FOLDER?
					WorkspaceFolder parent = folder.getParent();
					if(parent!=null)
						folder = parent;
					else
						workspaceLogger.warn("Parent folder of folder: "+folder.getName() + " is null, using current folder");
				}
				Map<ACLType, List<String>> mapACL = folder.getACLOwner();

				ACLType settingACL = ACLType.valueOf(aclType);

				workspaceLogger.info("Validating assignment: "+settingACL);
				workspaceLogger.info("To logins: "+listLogins);

				AclTypeComparator comparator = new AclTypeComparator();

				//TO REMOVE ADMINS
				List<String> admins = mapACL.get(ACLType.ADMINISTRATOR);
				for (String admin : admins) {
					listLogins.remove(admin);
					workspaceLogger.info("Reject username: "+admin +" as "+ACLType.ADMINISTRATOR);
				}

				//TO COMPLETE REPORT
				List<String> validLogins = new ArrayList<String>(listLogins);
				List<String> errors = new ArrayList<String>();
				ReportAssignmentACL reportValidation = new ReportAssignmentACL();

				for (String username : listLogins) {
					workspaceLogger.trace("\nChecking username: "+username);
					for (ACLType aclHL : mapACL.keySet()) {

						if(!aclHL.equals(ACLType.ADMINISTRATOR)){
							List<String> loginsHL = mapACL.get(aclHL);
							workspaceLogger.trace("to ACLType: "+aclHL +", logins found: "+loginsHL);

							if(loginsHL.contains(username)){
								int cmp = comparator.compare(settingACL, aclHL);
								workspaceLogger.trace("Compare result between "+aclHL + " and "+settingACL +": "+cmp);
								String fullname = isTestMode()?username: UserUtil.getUserFullName(username);
								if(cmp==-1){
									//CHANGE ACL IS NOT VALID
									workspaceLogger.trace("Reject ACL: "+settingACL+ " to "+username);
									validLogins.remove(username);
									errors.add("Unable to grant the privilege "+settingACL+" for "+fullname+", it's lower than (parent privilege) "+ aclHL);
									break;
								}else if(cmp==0){
									//SAME ACL
									workspaceLogger.trace("Skipping ACL: "+settingACL+ " to "+username);
									errors.add(settingACL+" privilege for "+fullname+ " already assigned");
									validLogins.remove(username);
									break;
								}else if(cmp==1){
									//CHANGE ACL IS VALID
									workspaceLogger.trace("Valid ACL: "+settingACL+ " to "+username);
								}
							}else{
								//CHANGE ACL IS VALID
								workspaceLogger.trace("[Login not found], Set ACL: "+settingACL+ " to "+username);
							}
						}
					}
				}
				/*
				System.out.println("\n");
				for (String username : validLogins) {
					workspaceLogger.trace("Set ACL: "+settingACL+ " to "+username);
				}

				System.out.println("\n");
				for (String error : errors) {
					workspaceLogger.trace(error);
				}*/

				reportValidation.setAclType(aclType);
				reportValidation.setErrors(errors);
				reportValidation.setValidLogins(validLogins);
				return reportValidation;

			} catch (InternalErrorException e) {
				throw new Exception("Sorry, an error occurred when validating ACL assignment, try again later");
			}
		}else
			throw new WorkspaceHandledException("the item with "+folderId +" is not a base shared folder!");
	}

	/**
	 * Gets the AC ls.
	 *
	 * @return the AC ls
	 * @throws Exception the exception
	 */
	@Override
	public List<WorkspaceACL> getACLs() throws Exception{
		try {

			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.getWorkspaceACLFromACLs(Arrays.asList(ACLType.values()));
		} catch (Exception e) {
			workspaceLogger.error("Error in server get ACLs", e);
			String error = ConstantsExplorer.SERVER_ERROR +" get ACL rules. "+e.getMessage();
			throw new Exception(error);
		}
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
		}
		catch (Exception ex) {
			workspaceLogger.trace("Development Mode ON");
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getMyLogin()
	 */
	@Override
	public UserBean getMyLogin(String currentPortletUrl){
		ASLSession asl = WsUtil.getAslSession(this.getThreadLocalRequest().getSession());
		String username = asl.getUsername();
		String email = username+"@isti.cnr.it";
		String firstName = "Testing";
		String lastName = "User";
		boolean publishRights = false;

		if (isWithinPortal() && username.compareTo(WsUtil.TEST_USER) != 0) {
			try {
				LiferayUserManager l = new LiferayUserManager();
				GCubeUser user = l.getUserByUsername(username);
				firstName = user.getFirstName();
				lastName = user.getLastName();
				email = user.getEmail();

				// check if he has catalogue role
				publishRights = enablePublishOnCatalogue(currentPortletUrl);
			}catch (UserManagementSystemException e) {
				workspaceLogger.error("UserManagementSystemException for username: "+username);
			}
			catch (UserRetrievalFault e) {
				workspaceLogger.error("UserRetrievalFault for username: "+username);

			}catch (Exception e) {
				workspaceLogger.error("Error during getMyLogin for username: "+username, e);
			}

		}

		UserBean us = new UserBean(username, firstName, lastName, email, publishRights);
		workspaceLogger.info("Returning myLogin: "+us);

		return us;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getMyLogin()
	 */
	@Override
	public String getMyFirstName(){
		ASLSession asl = WsUtil.getAslSession(this.getThreadLocalRequest().getSession());
		String username = asl.getUsername();
		String firstName = "";
		if (isWithinPortal() && username.compareTo(WsUtil.TEST_USER) != 0) {
			try {
				LiferayUserManager l = new LiferayUserManager();
				GCubeUser user = l.getUserByUsername(username);
				workspaceLogger.info("My login first name is: "+user.getFirstName());
				firstName = user.getFirstName();
			}catch (UserManagementSystemException e) {
				workspaceLogger.error("UserManagementSystemException for username: "+username);
			}
			catch (UserRetrievalFault e) {
				workspaceLogger.error("UserRetrievalFault for username: "+username);
			}

		}
		return firstName;
	}


	/**
	 * Update acl for vr eby group name.
	 *
	 * @param folderId the folder id
	 * @param aclType the acl type
	 * @throws Exception the exception
	 */
	@Override
	public void updateACLForVREbyGroupName(String folderId, String aclType) throws Exception{
		try {

			if(folderId == null)
				throw new Exception("Folder id is null");

			workspaceLogger.trace("Updating ACL to VRE FOLDER id: "+folderId);
			workspaceLogger.trace("ACL type is: "+aclType);

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(folderId);

			if(wsItem.isShared() && wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
				WorkspaceSharedFolder ite = (WorkspaceSharedFolder) wsItem;
				//PATCH TODO TEMPORARY SOLUTION
				if(ite.isVreFolder()){
					List<String> groupName = new ArrayList<String>();
					groupName.add(wsItem.getName());
					ite.setACL(groupName, ACLType.valueOf(aclType));
				}
			}else
				throw new Exception("Source item is not shared or shared folder");

			workspaceLogger.trace("Updating ACL completed, retuning");
		} catch (Exception e) {
			workspaceLogger.error("Error in set ACLs", e);
			String error = ConstantsExplorer.SERVER_ERROR +" updating permissions. "+e.getMessage();
			throw new Exception(error);
		}
	}


	/**
	 * Gets the user acl for folder id.
	 *
	 * @param folderId the folder id
	 * @return the user acl for folder id
	 * @throws Exception the exception
	 */
	@Override
	public List<ExtendedWorkspaceACL> getUserACLForFolderId(String folderId) throws Exception{
		try {
			workspaceLogger.info("Get user ACL to FOLDER id: "+folderId);
			WorkspaceFolder wsFolder = getSharedWorkspaceFolderForId(folderId);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			List<WorkspaceACL> listACL = builder.getWorkspaceACLFromACLs(Arrays.asList(wsFolder.getACLUser()));

			List<ExtendedWorkspaceACL> listEACL = new ArrayList<ExtendedWorkspaceACL>(listACL.size());
			for (WorkspaceACL workspaceACL : listACL) {

				boolean isBaseSharedFolder = isASharedFolder(wsFolder, true) ?true:false;
				ExtendedWorkspaceACL eac = new ExtendedWorkspaceACL(workspaceACL.getId(), workspaceACL.getLabel(), workspaceACL.getDefaultValue(), workspaceACL.getUserType(), workspaceACL.getDescription(), wsFolder.getOwner().getPortalLogin(), folderId, isBaseSharedFolder);
				workspaceLogger.trace("ACL "+workspaceACL+" converted in: "+eac);
				listEACL.add(eac);
			}

			return listEACL;
		} catch (Exception e) {
			workspaceLogger.error("Error in server get getACLForFolderId", e);
			String error = ConstantsExplorer.SERVER_ERROR +" get ACL rules for selected folder. "+e.getMessage();
			throw new Exception(error);
		}
	}

	/**
	 * Gets ACLs Description For WorkspaceItem ById.
	 *
	 * @param workspaceItemId the folder id
	 * @return a description of the ACLs
	 * @throws Exception the exception
	 */
	@Override
	public String getACLsDescriptionForWorkspaceItemById(String workspaceItemId) throws Exception{
		try {

			workspaceLogger.info("Get ACLsDescriptionForWorkspaceItemById: "+workspaceItemId);

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(workspaceItemId);

			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();

			Map<ACLType, List<String>> acls = wsItem.getACLOwner();

			if(acls==null)
				throw new Exception("ACLOwner is null!");

			return builder.getFormatHtmlACLFromACLs(acls);

		} catch (Exception e) {
			workspaceLogger.error("Error in getACLsDescriptionForWorkspaceItemById for workspaceItemId: "+workspaceItemId, e);
			String error = ConstantsExplorer.SERVER_ERROR +" getting ACL rules for requested item. "+e.getMessage();
			throw new Exception(error);
		}
	}

	/**
	 * Gets the shared workspace folder for id.
	 *
	 * @param folderId the folder id
	 * @return the shared workspace folder for id
	 * @throws Exception the exception
	 */
	private WorkspaceFolder getSharedWorkspaceFolderForId(String folderId) throws Exception{

		if(folderId == null)
			throw new Exception("Folder id is null");

		workspaceLogger.trace("Get SharedFolderForId: "+folderId);

		Workspace workspace = getWorkspace();
		WorkspaceItem wsItem = null;

		try{
			wsItem = workspace.getItem(folderId);
		}catch(Exception e){
			workspaceLogger.error("Get SharedFolderForId error on folder id: "+folderId, e);
			throw new Exception(ConstantsExplorer.SERVER_ERROR +" retrieving item with id: "+folderId+". Try again later!");
		}

		if(isASharedFolder(wsItem, false)){
			workspaceLogger.trace("Get SharedFolderForId: folder id "+folderId+" is shared");

			//TODO REMOVE wsItem.getIdSharedFolder()
			//			WorkspaceSharedFolder wsFolder = (WorkspaceSharedFolder) workspace.getItem(wsItem.getIdSharedFolder());

			WorkspaceFolder wsFolder = (WorkspaceFolder) wsItem;
			if(wsFolder!=null){
				workspaceLogger.info("Get SharedFolderForId return name: "+wsFolder.getName());
				return wsFolder;

			}else{
				workspaceLogger.warn("Source item is not a shared folder, throw exception");
				throw new Exception("Source item is not a shared folder");
			}
		}else{
			workspaceLogger.warn("Source item is null or not shared folder, throw exception");
			throw new Exception("Source item is null or not shared folder for id: "+folderId);
		}
	}

	/**
	 * Checks if is a shared folder.
	 *
	 * @param itemID the item id
	 * @param asRoot true check if itemID is root, not otherwise
	 * @return true, if is a shared folder
	 */

	public boolean isASharedFolder(String itemID, boolean asRoot){
		try {

			if(itemID==null)
				throw new Exception("ItemId is null");

			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(itemID);

			return isASharedFolder(item, asRoot);

		}catch(Exception e){
			workspaceLogger.error("Error in server isASharedFolder", e);
			return false;
		}
	}

	/**
	 * Checks if is a shared folder.
	 *
	 * @param item the item
	 * @param asRoot the as root
	 * @return true, if is a shared folder
	 * @see #isASharedFolder(String, boolean)
	 */
	public boolean isASharedFolder(WorkspaceItem item, boolean asRoot){
		try {

			if(item!=null && item.isFolder() && item.isShared()){ //IS A SHARED SUB-FOLDER
				if(asRoot)
					return item.getType().equals(WorkspaceItemType.SHARED_FOLDER); //IS ROOT?

				return true;
			}
			return false;
		}catch(Exception e){
			workspaceLogger.error("Error in server isASharedFolder", e);
			return false;
		}
	}



	/**
	 * Gets the trash content.
	 *
	 * @return the trash content
	 * @throws Exception the exception
	 */
	@Override
	public List<FileTrashedModel> getTrashContent() throws Exception{
		workspaceLogger.trace("Get TrashContent: ");

		Workspace workspace;
		try {

			workspace = getWorkspace();
			WorkspaceTrashFolder trash = workspace.getTrash();
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();

			return builder.buildGXTListTrashContent(trash);

		}catch (Exception e) {
			workspaceLogger.error("Error in server TrashConten", e);
			String error = ConstantsExplorer.SERVER_ERROR +" get Trash content. "+e.getMessage();
			throw new Exception(error);
		}
	}

	/**
	 * Update trash content.
	 *
	 * @param operation the operation
	 * @return the trash content
	 * @throws Exception the exception
	 */
	@Override
	public TrashContent updateTrashContent(WorkspaceTrashOperation operation) throws Exception{

		workspaceLogger.info("Updating TrashContent with operation: "+operation);

		Workspace workspace;
		List<String> listErrors = null;
		try {

			workspace = getWorkspace();
			WorkspaceTrashFolder trash = workspace.getTrash();
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			TrashContent result = new TrashContent();

			switch (operation) {

			case EMPTY_TRASH:
				listErrors = trash.emptyTrash();
				break;

			case RESTORE_ALL:
				listErrors = trash.restoreAll();
				break;

			case REFRESH:
			default:
				result.setTrashContent(builder.buildGXTListTrashContent(trash));
				return result;
			}

			trash = workspace.getTrash();
			result.setTrashContent(builder.buildGXTListTrashContent(trash));

			if(listErrors!=null){
				List<FileTrashedModel> listContentError = new ArrayList<FileTrashedModel>(listErrors.size());
				for (String trashedItemId : listErrors) {
					listContentError.add(builder.buildGXTTrashModelItemById(trashedItemId, trash));
				}

				result.setListErrors(listContentError);
			}

			return result;

		}catch (Exception e) {
			workspaceLogger.error("Error in server TrashContent", e);
			String error = ConstantsExplorer.SERVER_ERROR +" update Trash content. "+e.getMessage();
			throw new Exception(error);
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#executeOperationOnTrash(java.util.List, org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation)
	 */
	@Override
	public TrashOperationContent executeOperationOnTrash(List<String> listTrashItemIds, WorkspaceTrashOperation operation) throws Exception{

		workspaceLogger.info("Get TrashContent, operation: "+operation);

		if(listTrashItemIds==null || listTrashItemIds.size()==0)
			throw new Exception("List of Trash item ids is null or empty");

		Workspace workspace;
		List<FileTrashedModel> listContentError = new ArrayList<FileTrashedModel>();
		try {

			workspace = getWorkspace();
			WorkspaceTrashFolder trash = workspace.getTrash();
			TrashOperationContent result = new TrashOperationContent();
			result.setOperation(operation);

			List<String> listUpdatedTrashIds = new ArrayList<String>();

			switch (operation) {

			case DELETE_PERMANENTLY:{

				boolean deleted = false;
				for (String trashItemId : listTrashItemIds) {
					try{
						WorkspaceTrashItem trashItem = trash.getTrashItemById(trashItemId);
						if(trashItem!=null){
							trashItem.deletePermanently();
							listUpdatedTrashIds.add(trashItemId);
							deleted = true;
						}

					}catch (Exception e) {
						workspaceLogger.warn("Error on DELETE_PERMANENTLY the item : "+trashItemId, e);
						FileTrashedModel fakeFile = new FileTrashedModel();
						fakeFile.setIdentifier(trashItemId);
						listContentError.add(fakeFile);
					}
				}

				String label = listTrashItemIds.size()>1?"items":"item";
				if(!deleted)
					throw new Exception("Sorry, an error occurred on deleting permanently the trash "+label+", try again");

				break;
			}

			case RESTORE:{

				boolean restored = false;
				for (String trashItemId : listTrashItemIds) {
					try{
						WorkspaceTrashItem trashItem = trash.getTrashItemById(trashItemId);
						if(trashItem!=null){
							trashItem.restore();
							listUpdatedTrashIds.add(trashItemId);
							restored = true;
						}

					}catch (Exception e) {
						workspaceLogger.warn("Error on RESTORE the item : "+trashItemId, e);
						FileTrashedModel fakeFile = new FileTrashedModel();
						fakeFile.setIdentifier(trashItemId);
						listContentError.add(fakeFile);
					}
				}
				String label = listTrashItemIds.size()>1?"items":"item";

				if(!restored)
					throw new Exception("Sorry, an error occurred on restoring the trash "+label+", try again");

				break;
			}

			default:
				break;
			}

			//			trash = workspace.getTrash();
			//			result.setTrashContent(builder.buildGXTListTrashContent(trash));

			if(!listContentError.isEmpty()){
				result.setListErrors(listContentError);
			}

			result.setListTrashIds(listUpdatedTrashIds);

			return result;

		}catch (Exception e) {
			workspaceLogger.error("Error in server executeOperationOnTrash", e);
			String error = ConstantsExplorer.SERVER_ERROR +" update Trash content. "+e.getMessage();
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#addAdministratorsByFolderId(java.lang.String, java.util.List)
	 *
	 * true if administrators have been added, false otherwise
	 */
	@Override
	public boolean addAdministratorsByFolderId(String folderId, List<String> listContactLogins) throws Exception {
		if(folderId==null || listContactLogins==null || listContactLogins.size()==0)
			return false;
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(folderId);
			workspaceLogger.info("Adding administator/s to folder: "+folderId);

			if(item!=null && item.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
				WorkspaceSharedFolder sharedFolder = (WorkspaceSharedFolder) item;

				//retrieving old administrators list
				GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
				workspaceLogger.info("Retrieving old administator/s..");
				List<InfoContactModel> oldAdmins = builder.buildGxtInfoContactsFromPortalLogins(sharedFolder.getAdministrators());
				workspaceLogger.info("Retrieving old administator/s are/is: "+oldAdmins.size());

				workspaceLogger.info("Setting administators:");
				printList(listContactLogins);
				sharedFolder.setAdmins(listContactLogins);

				workspaceLogger.info("Converting new administator/s..");
				List<InfoContactModel> newAdmins = builder.buildGxtInfoContactsFromPortalLogins(listContactLogins);
				NotificationsProducer np = getNotificationProducer();

				workspaceLogger.info("Sending notifications downgrade/upgrade administator/s..");
				DifferenceBetweenInfoContactModel diff1 = new DifferenceBetweenInfoContactModel(oldAdmins, newAdmins);
				List<InfoContactModel> contactsDowngrade = diff1.getDifferentsContacts();

				for (InfoContactModel infoContactModel : contactsDowngrade) {
					np.notifyAdministratorDowngrade(infoContactModel, sharedFolder);
				}

				DifferenceBetweenInfoContactModel diff2 = new DifferenceBetweenInfoContactModel(newAdmins, oldAdmins);
				List<InfoContactModel> contactsUpgrade = diff2.getDifferentsContacts();

				for (InfoContactModel infoContactModel : contactsUpgrade) {
					np.notifyAdministratorUpgrade(infoContactModel, sharedFolder);
				}

				return true;

			}else
				throw new Exception("The item is null or not instanceof "+WorkspaceItemType.SHARED_FOLDER);

		} catch (Exception e) {
			workspaceLogger.error("Error in server addAdministratorsByFolderId: "+e.getMessage());
			workspaceLogger.error(e);
			String error = ConstantsExplorer.SERVER_ERROR +" adding administrators, try again later";
			throw new Exception(error);
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getAdministratorsByFolderId(java.lang.String)
	 */
	@Override
	public List<InfoContactModel> getAdministratorsByFolderId(String folderId) throws Exception {
		List<InfoContactModel> admins = new ArrayList<InfoContactModel>();

		if(folderId==null)
			return admins;
		try {
			workspaceLogger.info("Getting administator/s to folder: "+folderId);
			WorkspaceFolder wsFolder = getSharedWorkspaceFolderForId(folderId);
			if(isASharedFolder(wsFolder, true)){
				GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
				WorkspaceSharedFolder wsSharedFolder = (WorkspaceSharedFolder) wsFolder;
				return builder.buildGxtInfoContactsFromPortalLogins(wsSharedFolder.getAdministrators());
			}else
				throw new WorkspaceHandledException("the item with "+folderId +" is not a base shared folder!");

		} catch (WorkspaceHandledException e){
			workspaceLogger.error("Error in server getAdministratorsByFolderId: "+e.getMessage());
			String error = ConstantsExplorer.SERVER_ERROR +" getting Administrators: "+e.getMessage();
			throw new Exception(error);
		} catch (Exception e) {
			workspaceLogger.error("Error in server getAdministratorsByFolderId: "+e.getMessage());
			workspaceLogger.error(e);
			String error = ConstantsExplorer.SERVER_ERROR +" getting Administrators";
			throw new Exception(error);
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getItemDescriptionById(java.lang.String)
	 */
	@Override
	public String getItemDescriptionById(String identifier) throws Exception {

		workspaceLogger.info("Getting ItemDescriptionById: "+identifier);
		if(identifier==null || identifier.isEmpty()){
			workspaceLogger.warn("Getting ItemDescriptionById identifier is null or empty, returning null");
			return null;
		}

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(identifier);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.getItemDescriptionForTypeById(item);

		} catch (Exception e) {
			workspaceLogger.error("Error in server ItemDescriptionById: ", e);
			String error = ConstantsExplorer.SERVER_ERROR +" getting description for item id: "+identifier;
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getACLBySharedFolderId(java.lang.String)
	 */
	@Override
	public WorkspaceACL getACLBySharedFolderId(String identifier) throws Exception {
		workspaceLogger.info("Getting ACLBySharedFolderId: "+identifier);
		if(identifier==null || identifier.isEmpty()){
			workspaceLogger.warn("Getting ACLBySharedFolderId identifier is null or empty, returning null");
			return null;
		}
		try {

			WorkspaceFolder sharedFolder = getSharedWorkspaceFolderForId(identifier);

			//IS ROOT??
			if(isASharedFolder(sharedFolder, true)){
				GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
				//CASTING TO ROOT
				WorkspaceSharedFolder wsSharedFolder = (WorkspaceSharedFolder) sharedFolder;
				workspaceLogger.info("Read getPrivilege from HL: "+wsSharedFolder.getACLUser());
				List<WorkspaceACL> wsAcls = builder.getWorkspaceACLFromACLs(Arrays.asList(wsSharedFolder.getPrivilege()));

				if(wsAcls==null || wsAcls.isEmpty()){
					workspaceLogger.info("Converted ACLBySharedFolderId is null or empty, returning null");
					return null;
				}

				workspaceLogger.info("Returning first acl with id: "+wsAcls.get(0).getId());
				return wsAcls.get(0);
			}
			else{
				workspaceLogger.warn("WorkspaceFolder "+sharedFolder+" is not type of "+WorkspaceItemType.SHARED_FOLDER+ ", returning null");
				return null;
			}

		} catch (Exception e) {
			workspaceLogger.error("Error in server ACLBySharedFolderId: "+e.getMessage());
			workspaceLogger.error(e);
			String error = ConstantsExplorer.SERVER_ERROR +" getting ACL of WorkspaceSharedFolder";
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getUserWorkspaceQuote()
	 */
	@Override
	public WorkspaceUserQuote getUserWorkspaceQuote() throws Exception {
		try{
			workspaceLogger.info("Getting UserWorkspaceQuote..");
			Workspace workspace = getWorkspace();
			long size = workspace.getDiskUsage();
			workspaceLogger.info("Root size is: "+size +" formatting..");
			String formatSize = GWTWorkspaceBuilder.formatFileSize(size);
			long total = getUserWorkspaceTotalItems();

			WorkspaceUserQuote quote = new WorkspaceUserQuote();
			quote.setDiskSpace(size);
			quote.setDiskSpaceFormatted(formatSize);
			quote.setTotalItems(total);
			workspaceLogger.info("returning user quote: "+quote);
			return quote;
		}catch(Exception e){
			workspaceLogger.error("Error on UserWorkspaceQuote",e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getUserWorkspaceSize()
	 */
	@Override
	public String getUserWorkspaceSize() throws Exception {
		try{
			workspaceLogger.info("Getting workspace size..");
			Workspace workspace = getWorkspace();
			long size = workspace.getDiskUsage();
			//			workspaceLogger.info("Root size is: "+size +" formatting..");
			String formatSize = GWTWorkspaceBuilder.formatFileSize(size);
			workspaceLogger.info("returning workspace size: "+formatSize);
			return formatSize;
		}catch(Exception e){
			workspaceLogger.error("Error on UserWorkspaceSize",e);
			String error = ConstantsExplorer.SERVER_ERROR +" getting disk usage";
			throw new Exception(error);
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#getUserWorkspaceTotalItems()
	 */
	@Override
	public long getUserWorkspaceTotalItems() throws Exception {
		try{
			workspaceLogger.info("Getting total items..");
			Workspace workspace = getWorkspace();
			long size = workspace.getTotalItems();
			workspaceLogger.info("returning total items value: "+size);
			return size;
		}catch(Exception e){
			workspaceLogger.error("Error on UserWorkspaceSize",e);
			String error = ConstantsExplorer.SERVER_ERROR +" getting total items";
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#loadGcubeItemProperties(java.lang.String)
	 */
	@Override
	public Map<String, String> loadGcubeItemProperties(String itemId) throws Exception {
		workspaceLogger.info("Getting GcubeItemProperties for itemId: "+itemId);
		if(itemId==null || itemId.isEmpty()){
			workspaceLogger.warn("Getting GcubeItemProperties identifier is null or empty, returning null");
			return null;
		}

		try {
			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(itemId);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			Map<String, String> mapProperties = builder.getGcubeItemProperties(item);
			if(mapProperties!=null)
				workspaceLogger.info("Returning "+mapProperties.size()+" properties");
			else
				workspaceLogger.info("Returning null properties");

			return mapProperties;
		} catch (Exception e) {
			workspaceLogger.error("Error in server GcubeItemProperties: ", e);
			String error = ConstantsExplorer.SERVER_ERROR +" getting gcube item properties for item id: "+itemId;
			throw new Exception(error);
		}
	}


	/**
	 * Gets the HTML gcube item properties.
	 *
	 * @param itemId the item id
	 * @return The Gcube Item Properties in HTML format if itemId is a GcubeItem and contains properties, null otherwise
	 * @throws Exception the exception
	 */
	@Override
	public String getHTMLGcubeItemProperties(String itemId) throws Exception {
		workspaceLogger.info("Getting FormattedGcubeItemProperties for itemId: "+itemId);
		if(itemId==null || itemId.isEmpty()){
			workspaceLogger.warn("Getting FormattedGcubeItemProperties identifier is null or empty, returning null");
			return null;
		}

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(itemId);
			GWTWorkspaceBuilder builder = getGWTWorkspaceBuilder();
			return builder.getFormatHtmlGcubeItemProperties(item);

		} catch (Exception e) {
			workspaceLogger.error("Error in server FormattedGcubeItemProperties: ", e);
			String error = ConstantsExplorer.SERVER_ERROR +" getting gcube item properties for item id: "+itemId;
			throw new Exception(error);
		}
	}

	/**
	 * Sets the gcube item properties.
	 *
	 * @param itemId the item id
	 * @param properties the properties
	 * @throws Exception the exception
	 */
	@Override
	public void setGcubeItemProperties(String itemId, Map<String, String> properties) throws Exception {
		workspaceLogger.info("Set GcubeItemProperties for itemId: "+itemId);
		if(itemId==null || itemId.isEmpty()){
			workspaceLogger.warn("Set GcubeItemProperties, identifier is null or empty, returning null");
			throw new Exception("The item id is null or empty");
		}

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(itemId);
			if(item instanceof GCubeItem){
				workspaceLogger.info("Adding "+properties.size()+" properties to GCubeItem: "+itemId);
				GCubeItem gItem = (GCubeItem) item;
				for (String key : properties.keySet()) {
					//ADD PROPERTIES
					workspaceLogger.trace("Adding property: ["+key+","+properties.get(key)+"]");
					gItem.getProperties().addProperty(key, properties.get(key));
				}
				gItem.getProperties().update();
			}else
				throw new NoGcubeItemTypeException("The item is not a Gcube Item");

		} catch (NoGcubeItemTypeException e){
			workspaceLogger.error("Error in server FormattedGcubeItemProperties: ", e);
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			workspaceLogger.error("Error in server FormattedGcubeItemProperties: ", e);
			String error = ConstantsExplorer.SERVER_ERROR +" setting gcube item properties for item id: "+itemId;
			throw new Exception(error);
		}
	}

	/**
	 * Retrieve an instance of the library for the scope.
	 *
	 * @param scope if it is null it is evaluated from the session
	 * @return the ckan utils obj
	 */
	public DataCatalogue getCatalogue(String scope){
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		ASLSession asl = WsUtil.getAslSession(httpSession);
		DataCatalogue instance = null;
		try{
			String scopeInWhichDiscover = scope != null && !scope.isEmpty() ? scope : asl.getScope();
			workspaceLogger.debug("Discovering ckan instance into scope " + scopeInWhichDiscover);
			instance = DataCatalogueFactory.getFactory().getUtilsPerScope(scopeInWhichDiscover);
		}catch(Exception e){
			workspaceLogger.error("Unable to retrieve ckan utils", e);
		}
		return instance;
	}


	/**
	 * Check if the current user has publish rights on the data catalogue
	 * @param currentPortletUrl
	 * @return
	 */
	private boolean enablePublishOnCatalogue(String currentPortletUrl) {

		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		ASLSession asl = WsUtil.getAslSession(httpSession);
		String username = asl.getUsername();

		if(!isWithinPortal()){
			workspaceLogger.warn("OUT FROM PORTAL DETECTED RETURNING TRUE");
			return false;
		}

		if(username.equals(WsUtil.TEST_USER)){
			workspaceLogger.warn("Session expired");
			return false;
		}

		// retrieve scope per current portlet url
		String scopePerCurrentUrl = ApplicationProfileScopePerUrlReader.getScopePerUrl(currentPortletUrl);

		// save it
		this.getThreadLocalRequest().getSession().setAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL, scopePerCurrentUrl);

		// get key per scope
		String keyPerScopeRole = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_PUBLISH_WORKSPACE, scopePerCurrentUrl);
		String keyPerScopeOrganizations = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_ORGANIZATIONS_PUBLISH_KEY, scopePerCurrentUrl);

		// check if this information was already into the ASL Session (true means the user has at least in one org
		// the role editor), false that he is just a member so he cannot publish
		Boolean role = (Boolean)httpSession.getAttribute(keyPerScopeRole);

		// if the attribute was already set..
		if(role != null)
			return role;
		else{

			try{
				role = false;

				GroupManager gm = new LiferayGroupManager();
				String groupName = gm.getGroup(gm.getGroupIdFromInfrastructureScope(scopePerCurrentUrl)).getGroupName();

				// we build up also a list that keeps track of the scopes (orgs) in which the user has role ADMIN/EDITOR
				List<OrganizationBean> orgsInWhichAtLeastEditorRole = new ArrayList<OrganizationBean>();
				role = UserUtil.getHighestRole(scopePerCurrentUrl, username, groupName, this, orgsInWhichAtLeastEditorRole);

				// if he is an admin/editor preload:
				// 1) organizations in which he can publish (the widget will find these info in session)
				if(role){
					httpSession.setAttribute(keyPerScopeOrganizations, orgsInWhichAtLeastEditorRole);
					workspaceLogger.info("Set organizations in which he can publish to " + orgsInWhichAtLeastEditorRole + " into session for user " + username);
				}
			}catch(Exception e){
				workspaceLogger.error("Unable to retrieve the role information for this user. Returning FALSE", e);
				role = false;
			}
		}

		// set role in session for this scope
		httpSession.setAttribute(keyPerScopeRole, role);

		workspaceLogger.info("Does the user have the right to publish on the catalogue? " + role);

		// return false
		return role;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#markFolderAsPublicForFolderItemId(java.lang.String, boolean)
	 */
	@Override
	public PublicLink markFolderAsPublicForFolderItemId(String itemId, boolean setPublic) throws SessionExpiredException, Exception {

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {
			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(itemId);
			if(item instanceof WorkspaceFolder){
				WorkspaceFolder folder = (WorkspaceFolder) item;
				if(setPublic){
					folder.setPublic(true);
					String folderId = item.getId();
					workspaceLogger.info("HL returning folder link id: "+folderId);
					ApplicationProfileReader apReader = new ApplicationProfileReader("Workspace-Explorer-App", "org.gcube.portlets.user.workspaceexplorerapp.server.WorkspaceExplorerAppServiceImpl");
					ApplicationProfile ap = apReader.readProfileFromInfrastrucure();

					String encriptedFId = StringEncrypter.getEncrypter().encrypt(folderId);
					workspaceLogger.info("Encrypted folder Id: "+encriptedFId);
					String encodedFId = StringUtil.base64EncodeStringURLSafe(encriptedFId);
					workspaceLogger.info("Encoded in Base 64: "+encodedFId);
					workspaceLogger.info("Application profile returning url: "+ap.getUrl());
					String folderLink = ap.getUrl()+"?folderId="+encodedFId;
					String shortURL = null;
					try{
						shortURL = getShortUrl(folderLink);
						shortURL = shortURL!=null?shortURL:"not available";
					}catch(Exception e){
						workspaceLogger.warn("Short url error, skipping");
						shortURL = "not available";
					}
					return new PublicLink(folderLink, shortURL);
				}else{
					folder.setPublic(false);
					return null;
				}
			}else
				throw new NoGcubeItemTypeException("The item is not a Gcube Item");

		} catch (NoGcubeItemTypeException e){
			workspaceLogger.error("Error in server FormattedGcubeItemProperties: ", e);
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			workspaceLogger.error("Error in server FormattedGcubeItemProperties: ", e);
			String error = ConstantsExplorer.SERVER_ERROR +" reading Folder Link for id: "+itemId;
			throw new Exception(error);
		}
	}

	@Override
	public String getServletContextPath(String protocol) {
		HttpServletRequest req = getThreadLocalRequest();

		String scheme = protocol;

		String serverName = req.getServerName();     // hostname.com
		int serverPort = req.getServerPort();        // 80
		String contextPath = req.getServletContext().getContextPath();  // /mywebapp


		// Reconstruct original requesting URL
		StringBuffer url =  new StringBuffer();
		url.append(scheme).append("//").append(serverName);

		if (serverPort != 80 && serverPort != 443) {
			url.append(":").append(serverPort);
		}

		workspaceLogger.debug("server: "+url);
		workspaceLogger.debug("omitted contextPath: "+contextPath);


		url.append(contextPath);
		workspaceLogger.debug("getServletContextPath=" + url.toString());
		return url.toString();
	}
}

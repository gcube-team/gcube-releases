package org.gcube.portlets.widgets.wsexplorer.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemCategory;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemInterface;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;
import org.gcube.portlets.widgets.wsexplorer.shared.WorkspaceNavigatorServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The Class WorkspaceExplorerServiceImpl.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Massimiliano Assante, CNR-ISTI
 */
@SuppressWarnings("serial")
public class WorkspaceExplorerServiceImpl extends RemoteServiceServlet implements WorkspaceExplorerService {

	public static final Logger logger = LoggerFactory.getLogger(WorkspaceExplorerServiceImpl.class);
	public static final String USERNAME_ATTRIBUTE = "username";
	public static final String TEST_USER = "test.user";
	public static final String TEST_SCOPE = "/gcube"; //DEV
//	public static final String PRODUCTION_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps"; //PRODUCTION

	/**
 
	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 * @throws Exception the exception
	 */
	protected Workspace getWorkspace() throws Exception	{
		PortalContext pContext = PortalContext.getConfiguration();
		Workspace workspace;
		try{
			ScopeProvider.instance.set(pContext.getCurrentScope(getThreadLocalRequest()));
			workspace = HomeLibrary.getUserWorkspace(pContext.getCurrentUser(getThreadLocalRequest()).getUsername());
		}catch(InternalErrorException | HomeNotFoundException | WorkspaceFolderNotFoundException e){
			String msg = "Sorry, an error occurred when retrieving workspace item, Refresh an try again";
			logger.error("HL error: ",e);
			throw new Exception(msg);
		}
		return workspace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item getRoot(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceNavigatorServiceException {
		logger.trace("getRoot showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+ filterCriteria);

		try {

			Workspace workspace = getWorkspace();
			logger.trace("Start getRoot...");

			WorkspaceItem root = workspace.getRoot();

			logger.trace("GetRoot  - Replyiing root");
			long startTime = System.currentTimeMillis();
			logger.trace("start time - " + startTime);

			Item rootItem = ItemBuilder.getItem(null, root, root.getPath(), showableTypes, filterCriteria, true, false);
			rootItem.setName(WorkspaceExplorerConstants.HOME_LABEL);
			rootItem.setIsRoot(true);

			/* SPECIAL FOLDERS
			Item specialFolders = ItemBuilder.getItem(null, specials, showableTypes, filterCriteria, 2);
			specialFolders.setShared(true);
			rootItem.addChild(specialFolders);
			 */
			if (purgeEmpyFolders) {
				rootItem = ItemBuilder.purgeEmptyFolders(rootItem);
			}

			logger.trace("Returning:");
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			logger.info("end time - " + time);

			Collections.sort(rootItem.getChildren(), new ItemComparator());
			logger.info("Returning children size: "+rootItem.getChildren().size());

			return rootItem;

		} catch (Exception e) {
			logger.error("Error during root retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get root");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item getFolder(ItemInterface item, List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria, boolean loadGcubeProperties) throws WorkspaceNavigatorServiceException {
		logger.trace("getFolder folderId: "+item.getId()+" showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+filterCriteria);

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem folder = workspace.getItem(item.getId());
			logger.trace("GetFolder - Replyiing folder");
			long startTime = System.currentTimeMillis();
			logger.trace("start time - " + startTime);

			//TO AVOID SLOW CALL getPATH()
			String folderPath = item.getPath()!=null && !item.getPath().isEmpty()?item.getPath():folder.getPath();
			Item itemFolder = ItemBuilder.getItem(null, folder, folderPath, showableTypes, filterCriteria, true, loadGcubeProperties);
//			_log.trace("Only showable types:");

			if (purgeEmpyFolders) {
				itemFolder = ItemBuilder.purgeEmptyFolders(itemFolder);
			}

			logger.trace("Returning:");
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			logger.info("end time - " + time);

			Collections.sort(itemFolder.getChildren(), new ItemComparator());

			return itemFolder;

		} catch (Exception e) {
			logger.error("Error during folder retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get folder");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#getItemByCategory(org.gcube.portlets.widgets.wsexplorer.shared.ItemCategory)
	 */
	/**
	 * Gets the item by category.
	 *
	 * @param category the category
	 * @return the item by category
	 * @throws WorkspaceNavigatorServiceException the workspace navigator service exception
	 */
	@Override
	public Item getItemByCategory(ItemCategory category) throws WorkspaceNavigatorServiceException{
		logger.trace("GetItemByCategory category: "+category);
		try {
			Workspace workspace = getWorkspace();
			Item item = null;

			switch(category){
				case HOME:{
					WorkspaceItem root = workspace.getRoot();
					PortalContext pContext = PortalContext.getConfiguration();					
					String fullName = pContext.getCurrentUser(getThreadLocalRequest()).getFullname();
					if(fullName.indexOf(" ")>0){
						fullName = fullName.substring(0, fullName.indexOf(" "));
					}else if(fullName.indexOf(".")>0){
						fullName = fullName.substring(0, fullName.indexOf("."));
					}
					item = new Item(null, root.getId(), fullName+"'s", ItemType.FOLDER, root.getPath(), root.getOwner().getPortalLogin(), null, true, true);
					break;
				}
				case VRE_FOLDER:{
					WorkspaceItem folder = workspace.getMySpecialFolders();
					item = new Item(null, folder.getId(), WorkspaceExplorerConstants.VRE_FOLDERS_LABEL, ItemType.FOLDER, folder.getPath(), folder.getOwner().getPortalLogin(), null, true, false);
					//SET SPECIAL FOLDER  /Workspace/MySpecialFolders
					item.setSpecialFolder(true);
					break;
				}
			}
			return item;
		} catch (Exception e) {
			logger.error("Error during get item by category", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get item by category");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item getMySpecialFolder(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceNavigatorServiceException {
		logger.trace("GetMySpecialFolder showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+filterCriteria);

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem folder = workspace.getMySpecialFolders();

			long startTime = System.currentTimeMillis();
			logger.trace("start time - " + startTime);

			Item itemFolder = ItemBuilder.getItem(null, folder, folder.getPath(), showableTypes, filterCriteria, true, false);
			//OVERRIDING VRE FOLDERS NAME - SET SPECIAL FOLDER  /Workspace/MySpecialFolders
			itemFolder.setName(WorkspaceExplorerConstants.VRE_FOLDERS_LABEL);
			itemFolder.setSpecialFolder(true);

			logger.trace("Builded MySpecialFolder: "+itemFolder);

			logger.trace("Only showable types:");
			//printName("", folderItem);

			if (purgeEmpyFolders) {
				itemFolder = ItemBuilder.purgeEmptyFolders(itemFolder);
			}

			logger.trace("Returning:");

			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			logger.trace("end time - " + time);

			//printName("", folderItem);
			Collections.sort(itemFolder.getChildren(), new ItemComparator());
			return itemFolder;

		} catch (Exception e) {
			logger.error("Error during special folders retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get My Special Folder");
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkName(String name) throws WorkspaceNavigatorServiceException {
		logger.trace("checkName name: "+name);
		try {
			PortalContext pContext = PortalContext.getConfiguration();
			Workspace workspace = HomeLibrary.getUserWorkspace(pContext.getCurrentUser(getThreadLocalRequest()).getUsername());
			return workspace.isValidName(name);
		} catch (Exception e) {
			logger.error("Error during folder retrieving", e);
			throw new WorkspaceNavigatorServiceException(e.getMessage());
		}
	}

	/*protected void printName(String indentation, Item item)
	{
		if(item!=null){
			_log.trace(indentation+item.getName());
			for (Item child:item.getChildren()) printName(indentation+"\t", child);
		}
	}*/

	/**
	 * Gets Breadcrumbs (the list of parents) by item identifier.
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent -  if parameter is true and item passed in input is a folder, the folder is included in path returned as last parent
	 * @return the list parents by item identifier
	 * @throws Exception the exception
	 */
	@Override
	public List<Item> getBreadcrumbsByItemIdentifier(String itemIdentifier, boolean includeItemAsParent) throws Exception {
		logger.trace("ListParents By Item Identifier "+ itemIdentifier);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemIdentifier);
			logger.trace("workspace retrieve item name: "+wsItem.getName());
			List<WorkspaceItem> parents = workspace.getParentsById(itemIdentifier);
			logger.trace("parents size: "+parents.size());
			Item[] arrayParents;

			if(includeItemAsParent==true && wsItem.isFolder()){
				arrayParents = new Item[parents.size()];
				arrayParents[parents.size()-1] = ItemBuilder.buildFolderForBreadcrumbs((WorkspaceFolder) wsItem, null);
			}
			else {
				arrayParents = new Item[parents.size()-1];
			}

			/** HANDLE MY_SPECIAL_FOLDER TO AVOID COMPLETE PATH WORKSPACE/MY_SPECIAL_FOLDER
			 * BUT RETURNING ONLY /MY_SPECIAL_FOLDER
			 */
			if(wsItem.isFolder()){
				if(ItemBuilder.isSpecialFolder((WorkspaceFolder) wsItem)){
					return new ArrayList<Item>(Arrays.asList(arrayParents));
				}
			}

			//CONVERTING PATH
			logger.trace("converting path from second-last..");
			for (int i =  parents.size()-2; i >= 0; i--) {
				WorkspaceFolder wsParentFolder = (WorkspaceFolder) parents.get(i);
				arrayParents[i] = ItemBuilder.buildFolderForBreadcrumbs(wsParentFolder, null);
				if(arrayParents[i].isSpecialFolder()){ //SKIP HOME PARENT FOR MY_SPECIAL_FOLDER
					logger.info("arrayParents index "+i+" is special folder, exit");
					break;
				}
			}

			//SET PARENTS
			logger.trace("setting parents..");
			for(int i=0; i<arrayParents.length-1; i++){

				Item parent = arrayParents[i];
				Item fileModel = arrayParents[i+1];
				fileModel.setParent(parent);
			}

			logger.trace("ListParents return size: "+arrayParents.length);
			if(arrayParents[0]==null){ //EXIT BY BREAK IN CASE OF SPECIAL FOLDER
				List<Item> breadcrumbs = new ArrayList<Item>(arrayParents.length-1);
				for (int i=1; i<arrayParents.length; i++) {
					breadcrumbs.add(arrayParents[i]);
				}
				return breadcrumbs;
			}
			else {
				return new ArrayList<Item>(Arrays.asList(arrayParents));
			}

		} catch (Exception e) {
			logger.error("Error in get List Parents By Item Identifier ", e);
			throw new Exception("Sorry, an error occurred during path retrieving!");
		}
	}

	/**
	 * Gets the parents by item identifier to limit.
	 *
	 * @param itemIdentifier            the item identifier
	 * @param parentLimit the parent limit
	 * @param includeItemAsParent            the include item as parent
	 * @return the parents by item identifier to limit
	 * @throws Exception             the exception
	 */
	@Override
	public List<Item> getBreadcrumbsByItemIdentifierToParentLimit(String itemIdentifier, String parentLimit, boolean includeItemAsParent) throws Exception {
		logger.trace("getBreadcrumbsByItemIdentifierToParentLimit by Item Identifier " + itemIdentifier +" and limit: "+parentLimit);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemIdentifier);
			logger.trace("workspace retrieve item name: "+wsItem.getName());
			List<WorkspaceItem> parents = workspace.getParentsById(itemIdentifier);
			logger.trace("parents size: "+parents.size());
			Item[] arrayParents;

			if(includeItemAsParent==true && wsItem.isFolder()){
				arrayParents = new Item[parents.size()];
				arrayParents[parents.size()-1] = ItemBuilder.buildFolderForBreadcrumbs((WorkspaceFolder) wsItem, null);
			}
			else {
				arrayParents = new Item[parents.size()-1];
			}

			parentLimit = parentLimit!=null?parentLimit:"";

			/** HANDLE MY_SPECIAL_FOLDER TO AVOID COMPLETE PATH WORKSPACE/MY_SPECIAL_FOLDER
			 * BUT RETURNING ONLY /MY_SPECIAL_FOLDER
			 */
			if(wsItem.isFolder()){
				if(ItemBuilder.isSpecialFolder((WorkspaceFolder) wsItem)){
					logger.debug("item id is special folder, returning");
					return new ArrayList<Item>(Arrays.asList(arrayParents));
				}

				if(itemIdentifier.compareTo(parentLimit)==0){
					logger.debug("item and parent limit are identical element, returning");
					return new ArrayList<Item>(Arrays.asList(arrayParents));
				}

			}

			//CONVERTING PATH
			logger.trace("converting path from second-last..");
			for (int i =  parents.size()-2; i >= 0; i--) {
				WorkspaceFolder wsParentFolder = (WorkspaceFolder) parents.get(i);
				arrayParents[i] = ItemBuilder.buildFolderForBreadcrumbs(wsParentFolder, null);
				if(arrayParents[i].isSpecialFolder()){ //SKIP HOME PARENT FOR MY_SPECIAL_FOLDER
					logger.info("arrayParents index "+i+" is special folder, break");
					break;
				}else if(parentLimit.compareTo(arrayParents[i].getId())==0){
					logger.info("reached parent limit "+parentLimit+", break");
					break;
				}
			}

			//SET PARENTS
			logger.trace("setting parents..");
			for(int i=0; i<arrayParents.length-1; i++){

				Item parent = arrayParents[i];
				Item fileModel = arrayParents[i+1];

				if(fileModel!=null) {
					fileModel.setParent(parent);
				}
			}

			logger.trace("ListParents return size: "+arrayParents.length);
			if(arrayParents[0]==null){ //EXIT BY BREAK IN CASE OF SPECIAL FOLDER OR REACHED PARENT LIMIT
				List<Item> breadcrumbs = new ArrayList<Item>();
				for (int i=1; i<arrayParents.length; i++) {
					if(arrayParents[i]!=null) {
						breadcrumbs.add(arrayParents[i]);
					}
				}
				return breadcrumbs;
			}
			else {
				return new ArrayList<Item>(Arrays.asList(arrayParents));
			}

		} catch (Exception e) {
			logger.error("Error in get List Parents By Item Identifier ", e);
			throw new Exception("Sorry, an error occurred during path retrieving!");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#loadSizeByItemId(java.lang.String)
	 */
	/**
	 * Gets the size by item id.
	 *
	 * @param itemId the item id
	 * @return the size by item id
	 * @throws Exception the exception
	 */
	@Override
	public Long getSizeByItemId(String itemId) throws Exception {

		logger.info("get Size By ItemId "+ itemId);
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
			logger.info("returning size: " +size);
			return size;

		} catch (Exception e) {
			logger.error("get Size By ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#getMimeType(java.lang.String)
	 */
	/**
	 * Gets the mime type.
	 *
	 * @param itemId the item id
	 * @return the mime type
	 * @throws Exception the exception
	 */
	@Override
	public String getMimeType(String itemId) throws Exception {

		logger.info("get MimeType By ItemId "+ itemId);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemId);

			if(!wsItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)) {
				return null;
			}

			FolderItem folderItem = (FolderItem) wsItem;

			return folderItem.getMimeType();

		} catch (Exception e) {
			logger.error("get MimeType By ItemId ", e);
			throw new Exception(e.getMessage());
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
	public String getUserACLForFolderId(String folderId) throws Exception{
		try {
			logger.info("Get user ACL to FOLDER id: "+folderId);
			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(folderId);

			if(!isASharedFolder(wsItem, false)) {
				return "OWNER";
			}
			else {
				return wsItem.getACLUser().toString();
			}
		} catch (Exception e) {
			logger.error("Error in server get UserACLForFolderId", e);
			String error = "An error occurred when getting ACL rules for selected folder. "+e.getMessage();
			throw new Exception(error);
		}
	}


	/**
	 * Checks if is a shared folder.
	 *
	 * @param item the item
	 * @param asRoot the as root
	 * @return true, if is a shared folder
	 */
	private boolean isASharedFolder(WorkspaceItem item, boolean asRoot){
		try {

			if(item!=null && item.isFolder() && item.isShared()){ //IS A SHARED SUB-FOLDER
				if(asRoot)
				 {
					return item.getType().equals(WorkspaceItemType.SHARED_FOLDER); //IS ROOT?
				}

				return true;
			}
			return false;
		}catch(Exception e){
			logger.error("Error in server isASharedFolder", e);
			return false;
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#getFormattedSizeByItemId(java.lang.String, org.gcube.portlets.widgets.wsexplorer.shared.SizeFormatter)
	 */
	/**
	 * Gets the readable size by item id.
	 *
	 * @param itemId the item id
	 * @return the readable size by item id
	 * @throws Exception the exception
	 */
	@Override
	public String getReadableSizeByItemId(String itemId) throws Exception {

		try{
		logger.info("getFormattedSize ByItemId "+ itemId);
				long size = getSizeByItemId(itemId);
				return StringUtil.readableFileSize(size);
		} catch (Exception e) {
			logger.error("getFormattedSize By ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Creates the folder.
	 *
	 * @param nameFolder the name folder
	 * @param description the description
	 * @param parentId the parent id
	 * @return the item
	 * @throws Exception the exception
	 */
	@Override
	public Item createFolder(String nameFolder, String description, String parentId) throws Exception {

		logger.debug("creating folder: "+nameFolder +", parent id: "+parentId);

		try {

			if(parentId==null || parentId.isEmpty())
				throw new Exception("Parent id is null or empty");

			if(nameFolder == null)
				nameFolder = "Empty Folder";

			Workspace workspace = getWorkspace();
			WorkspaceFolder wsFolder = workspace.createFolder(nameFolder, description, parentId);

//			_log.info("Path returned by HL: "+wsFolder.getPath());

			List<ItemType> allTypes = Arrays.asList(ItemType.values());

			Item parent = null;
			try{
				String parentPath = wsFolder.getParent()!=null?wsFolder.getParent().getPath():"";
				parent = ItemBuilder.getItem(null, wsFolder.getParent(), parentPath, allTypes, null, false, false);
			}catch(Exception e){
				logger.error("Get parent thown an exception, is it the root id? "+parentId);
			}

			//TODO PATCH TO AVOID PROBLEM ON GETPATH. FOR EXAMPLE WHEN PARENT IS ROOT
			String itemPath = null;
			try{
				itemPath = wsFolder.getPath();
				logger.info("itemPath: "+itemPath);
			}catch(Exception e){
				logger.error("Get path thrown an exception, for id: "+wsFolder.getId() +" name: "+wsFolder.getName(), e);
//				itemPath= wsFolder.isFolder()?workspace.getRoot().getPath()+"/"+wsFolder.getName():workspace.getRoot().getPath();
				//PATCH TO RETURN ABSOLUTE PATH
				itemPath= workspace.getRoot().getPath()+"/"+wsFolder.getName();
				logger.warn("returning base path: "+itemPath);
			}

			return ItemBuilder.getItem(parent, wsFolder, itemPath, allTypes, null, false, false);

		} catch(InsufficientPrivilegesException e){
			String error = "Insufficient Privileges to create the folder";
			logger.error(error, e);
			throw new Exception(error);
		} catch (ItemAlreadyExistException e) {
			String error = "An error occurred on creating folder, "  +e.getMessage();
			logger.error(error, e);
			throw new Exception(error);
		} catch (Exception e) {
			String error = "An error occurred on the sever during creating folder. Try again";
			logger.error(error, e);
			throw new Exception(error);
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#getGcubePropertiesForWorspaceId(java.lang.String)
	 */
	/**
	 * Gets the gcube properties for worspace id.
	 *
	 * @param id the id
	 * @return the gcube properties for worspace id
	 * @throws Exception the exception
	 */
	@Override
	public Map<String, String> getGcubePropertiesForWorspaceId(String id) throws Exception {
		logger.trace("getGcubePropertiesForWorspaceId "+id);
		try {

			if(id==null || id.isEmpty()){
				logger.info(id +" is null or empty returing empty map as GcubeProperties");
				return new HashMap<String, String>(1);
			}

			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(id);

			return ItemBuilder.getGcubePropertiesForItem(item);

		} catch (Exception e) {
			logger.error("Error during folder retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get folder");
		}
	}
}

package org.gcube.portlets.widgets.wsexplorer.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
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
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 */
@SuppressWarnings("serial")
public class WorkspaceExplorerServiceImpl extends RemoteServiceServlet implements WorkspaceExplorerService {

	/**
	 *
	 */
	public static final Logger _log = LoggerFactory.getLogger(WorkspaceExplorerServiceImpl.class);
	public static final String USERNAME_ATTRIBUTE = "username";
	public static final String TEST_USER = "test.user";
	public static final String TEST_SCOPE = "/gcube/devsec/devVRE"; //DEV
//	public static final String PRODUCTION_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps"; //PRODUCTION


	/**
	 * Gets the ASL session.
	 *
	 * @param httpSession the http session
	 * @return the ASL session
	 */
	private ASLSession getASLSession(HttpSession httpSession)	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);

		//TODO we check for the older attribute name
		if (user == null) {
			user = (String) httpSession.getAttribute("user");
		}

		if (user == null) {

			_log.error("WORKSPACE PORTLET STARTING IN TEST MODE - NO USER FOUND");

			//for test only
//			user = "test.user";
			user = TEST_USER;
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(TEST_SCOPE);

			return session;
		}
		else {
			_log.trace("user found in session "+user);
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}


	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	protected Workspace getWorkspace() throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException	{
		ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
		ScopeProvider.instance.set(session.getScope());
		Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());
		return workspace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item getRoot(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceNavigatorServiceException {
		_log.trace("getRoot showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+ filterCriteria);

		try {

			Workspace workspace = getWorkspace();
			_log.trace("Start getRoot...");

			WorkspaceItem root = workspace.getRoot();

			_log.trace("GetRoot  - Replyiing root");
			long startTime = System.currentTimeMillis();
			_log.trace("start time - " + startTime);

			Item rootItem = ItemBuilder.getItem(null, root, root.getPath(), showableTypes, filterCriteria, true);
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

			_log.trace("Returning:");
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			_log.info("end time - " + time);

			Collections.sort(rootItem.getChildren(), new ItemComparator());
			_log.info("Returning children size: "+rootItem.getChildren().size());

			return rootItem;

		} catch (Exception e) {
			_log.error("Error during root retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get root");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item getFolder(ItemInterface item, List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceNavigatorServiceException {
		_log.trace("getFolder folderId: "+item.getId()+" showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+filterCriteria);

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem folder = workspace.getItem(item.getId());

			Item itemCast = (Item) item;

			System.out.println("itemCast"  +itemCast);

			_log.trace("GetFolder - Replyiing folder");
			long startTime = System.currentTimeMillis();
			_log.trace("start time - " + startTime);

			//TO AVOID SLOW CALL getPATH()
			String folderPath = item.getPath()!=null && !item.getPath().isEmpty()?item.getPath():folder.getPath();

			Item itemFolder = ItemBuilder.getItem(null, folder, folderPath, showableTypes, filterCriteria, true);
//			_log.trace("Only showable types:");

			if (purgeEmpyFolders) {
				itemFolder = ItemBuilder.purgeEmptyFolders(itemFolder);
			}

			_log.trace("Returning:");
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			_log.info("end time - " + time);

			Collections.sort(itemFolder.getChildren(), new ItemComparator());

			return itemFolder;

		} catch (Exception e) {
			_log.error("Error during folder retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get folder");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#getItemByCategory(org.gcube.portlets.widgets.wsexplorer.shared.ItemCategory)
	 */
	@Override
	public Item getItemByCategory(ItemCategory category) throws WorkspaceNavigatorServiceException{
		_log.trace("GetItemByCategory category: "+category);
		try {
			Workspace workspace = getWorkspace();
			Item item = null;

			switch(category){
				case HOME:{
					WorkspaceItem root = workspace.getRoot();
					ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
					String fullName = UserUtil.getUserFullName(session.getUsername());
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
			_log.error("Error during get item by category", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get item by category");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item getMySpecialFolder(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceNavigatorServiceException {
		_log.trace("GetMySpecialFolder showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+filterCriteria);

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem folder = workspace.getMySpecialFolders();

			long startTime = System.currentTimeMillis();
			_log.trace("start time - " + startTime);

			Item itemFolder = ItemBuilder.getItem(null, folder, folder.getPath(), showableTypes, filterCriteria, true);
			//OVERRIDING VRE FOLDERS NAME - SET SPECIAL FOLDER  /Workspace/MySpecialFolders
			itemFolder.setName(WorkspaceExplorerConstants.VRE_FOLDERS_LABEL);
			itemFolder.setSpecialFolder(true);

			_log.trace("Builded MySpecialFolder: "+itemFolder);

			_log.trace("Only showable types:");
			//printName("", folderItem);

			if (purgeEmpyFolders) {
				itemFolder = ItemBuilder.purgeEmptyFolders(itemFolder);
			}

			_log.trace("Returning:");

			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			_log.trace("end time - " + time);

			//printName("", folderItem);

			Collections.sort(itemFolder.getChildren(), new ItemComparator());

			return itemFolder;

		} catch (Exception e) {
			_log.error("Error during special folders retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get My Special Folder");
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkName(String name) throws WorkspaceNavigatorServiceException {
		_log.trace("checkName name: "+name);
		try {
			ASLSession session = getASLSession(this.getThreadLocalRequest().getSession());
			Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());
			return workspace.isValidName(name);
		} catch (Exception e) {
			_log.error("Error during folder retrieving", e);
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
		_log.trace("ListParents By Item Identifier "+ itemIdentifier);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemIdentifier);
			_log.trace("workspace retrieve item name: "+wsItem.getName());
			List<WorkspaceItem> parents = workspace.getParentsById(itemIdentifier);
			_log.trace("parents size: "+parents.size());
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
			_log.trace("converting path from second-last..");
			for (int i =  parents.size()-2; i >= 0; i--) {
				WorkspaceFolder wsParentFolder = (WorkspaceFolder) parents.get(i);
				arrayParents[i] = ItemBuilder.buildFolderForBreadcrumbs(wsParentFolder, null);
				if(arrayParents[i].isSpecialFolder()){ //SKIP HOME PARENT FOR MY_SPECIAL_FOLDER
					_log.info("arrayParents index "+i+" is special folder, exit");
					break;
				}
			}

			//SET PARENTS
			_log.trace("setting parents..");
			for(int i=0; i<arrayParents.length-1; i++){

				Item parent = arrayParents[i];
				Item fileModel = arrayParents[i+1];
				fileModel.setParent(parent);
			}

			_log.trace("ListParents return size: "+arrayParents.length);
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
			_log.error("Error in get List Parents By Item Identifier ", e);
			throw new Exception("Sorry, an error occurred during path retrieving!");
		}
	}

	/**
	 * Gets the parents by item identifier to limit.
	 *
	 * @param itemIdentifier
	 *            the item identifier
	 * @param includeItemAsParent
	 *            the include item as parent
	 * @return the parents by item identifier to limit
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<Item> getBreadcrumbsByItemIdentifierToParentLimit(String itemIdentifier, String parentLimit, boolean includeItemAsParent) throws Exception {
		_log.trace("getBreadcrumbsByItemIdentifierToParentLimit by Item Identifier " + itemIdentifier +" and limit: "+parentLimit);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemIdentifier);
			_log.trace("workspace retrieve item name: "+wsItem.getName());
			List<WorkspaceItem> parents = workspace.getParentsById(itemIdentifier);
			_log.trace("parents size: "+parents.size());
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
					_log.debug("item id is special folder, returning");
					return new ArrayList<Item>(Arrays.asList(arrayParents));
				}

				if(itemIdentifier.compareTo(parentLimit)==0){
					_log.debug("item and parent limit are identical element, returning");
					return new ArrayList<Item>(Arrays.asList(arrayParents));
				}

			}

			//CONVERTING PATH
			_log.trace("converting path from second-last..");
			for (int i =  parents.size()-2; i >= 0; i--) {
				WorkspaceFolder wsParentFolder = (WorkspaceFolder) parents.get(i);
				arrayParents[i] = ItemBuilder.buildFolderForBreadcrumbs(wsParentFolder, null);
				if(arrayParents[i].isSpecialFolder()){ //SKIP HOME PARENT FOR MY_SPECIAL_FOLDER
					_log.info("arrayParents index "+i+" is special folder, break");
					break;
				}else if(parentLimit.compareTo(arrayParents[i].getId())==0){
					_log.info("reached parent limit "+parentLimit+", break");
					break;
				}
			}

			//SET PARENTS
			_log.trace("setting parents..");
			for(int i=0; i<arrayParents.length-1; i++){

				Item parent = arrayParents[i];
				Item fileModel = arrayParents[i+1];

				if(fileModel!=null) {
					fileModel.setParent(parent);
				}
			}

			_log.trace("ListParents return size: "+arrayParents.length);
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
			_log.error("Error in get List Parents By Item Identifier ", e);
			throw new Exception("Sorry, an error occurred during path retrieving!");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#loadSizeByItemId(java.lang.String)
	 */
	@Override
	public Long getSizeByItemId(String itemId) throws Exception {

		_log.info("get Size By ItemId "+ itemId);
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
			_log.info("returning size: " +size);
			return size;

		} catch (Exception e) {
			_log.error("get Size By ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#getMimeType(java.lang.String)
	 */
	@Override
	public String getMimeType(String itemId) throws Exception {

		_log.info("get MimeType By ItemId "+ itemId);
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(itemId);

			if(!wsItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)) {
				return null;
			}

			FolderItem folderItem = (FolderItem) wsItem;

			return folderItem.getMimeType();

		} catch (Exception e) {
			_log.error("get MimeType By ItemId ", e);
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
			_log.info("Get user ACL to FOLDER id: "+folderId);
			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(folderId);

			if(!isASharedFolder(wsItem, false)) {
				return "OWNER";
			}
			else {
				return wsItem.getACLUser().toString();
			}
		} catch (Exception e) {
			_log.error("Error in server get UserACLForFolderId", e);
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
			_log.error("Error in server isASharedFolder", e);
			return false;
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#getFormattedSizeByItemId(java.lang.String, org.gcube.portlets.widgets.wsexplorer.shared.SizeFormatter)
	 */
	@Override
	public String getReadableSizeByItemId(String itemId) throws Exception {

		try{
		_log.info("getFormattedSize ByItemId "+ itemId);
				long size = getSizeByItemId(itemId);
				return StringUtil.readableFileSize(size);
		} catch (Exception e) {
			_log.error("getFormattedSize By ItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Item createFolder(String nameFolder, String description, String parentId) throws Exception {

		_log.debug("creating folder: "+nameFolder +", parent id: "+parentId);

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
				parent = ItemBuilder.getItem(null, wsFolder.getParent(), parentPath, allTypes, null, false);
			}catch(Exception e){
				_log.error("Get parent thown an exception, is it the root id? "+parentId);
			}

			//TODO PATCH TO AVOID PROBLEM ON GETPATH. FOR EXAMPLE WHEN PARENT IS ROOT
			String itemPath = null;
			try{
				itemPath = wsFolder.getPath();
				_log.info("itemPath: "+itemPath);
			}catch(Exception e){
				_log.error("Get path thrown an exception, for id: "+wsFolder.getId() +" name: "+wsFolder.getName(), e);
//				itemPath= wsFolder.isFolder()?workspace.getRoot().getPath()+"/"+wsFolder.getName():workspace.getRoot().getPath();
				//PATCH TO RETURN ABSOLUTE PATH
				itemPath= workspace.getRoot().getPath()+"/"+wsFolder.getName();
				_log.warn("returning base path: "+itemPath);
			}

			return ItemBuilder.getItem(parent, wsFolder, itemPath, allTypes, null, false);

		} catch(InsufficientPrivilegesException e){
			String error = "Insufficient Privileges to create the folder";
			_log.error(error, e);
			throw new Exception(error);
		} catch (ItemAlreadyExistException e) {
			String error = "An error occurred on creating folder, "  +e.getMessage();
			_log.error(error, e);
			throw new Exception(error);
		} catch (Exception e) {
			String error = "An error occurred on the sever during creating folder. Try again";
			_log.error(error, e);
			throw new Exception(error);
		}

	}
}

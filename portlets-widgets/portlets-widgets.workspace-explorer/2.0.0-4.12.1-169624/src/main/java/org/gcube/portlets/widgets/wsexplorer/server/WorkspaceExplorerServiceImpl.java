package org.gcube.portlets.widgets.wsexplorer.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService;
import org.gcube.portlets.widgets.wsexplorer.server.stohub.StorageHubServiceUtil;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemCategory;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemInterface;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;
import org.gcube.portlets.widgets.wsexplorer.shared.SearchedFolder;
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

	public static final Logger _log = LoggerFactory.getLogger(WorkspaceExplorerServiceImpl.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.gcube.portlets.widgets.wsexplorer.shared.Item getRoot(
			List<ItemType> showableTypes, 
			boolean purgeEmpyFolders, 
			FilterCriteria filterCriteria) 
					throws WorkspaceNavigatorServiceException {
		_log.trace("getRoot showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+ filterCriteria);
		try {
			PortalContext pContext = PortalContext.getConfiguration();
			String userName = pContext.getCurrentUser(getThreadLocalRequest()).getUsername();
			String scope = pContext.getCurrentScope(getThreadLocalRequest());
			String authorizationToken = pContext.getCurrentUserToken(scope, userName);
			SecurityTokenProvider.instance.set(authorizationToken);

			_log.trace("Start getRoot...");
			Item root = StorageHubServiceUtil.getRoot(getThreadLocalRequest());

			org.gcube.portlets.widgets.wsexplorer.shared.Item rootItem = ItemBuilder.getItem(null, root, root.getPath(), showableTypes, filterCriteria, true, false);
			rootItem.setName(WorkspaceExplorerConstants.HOME_LABEL);
			rootItem.setIsRoot(true);

			if (purgeEmpyFolders) {
				rootItem = ItemBuilder.purgeEmptyFolders(rootItem);
			}
			Collections.sort(rootItem.getChildren(), new ItemComparator());
			_log.info("->Returning children size: "+rootItem.getChildren().size());
			return rootItem;
		} catch (Exception e) {
			_log.error("Error during root retrieving", e);
			e.printStackTrace();
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get root");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.gcube.portlets.widgets.wsexplorer.shared.Item getFolder(
			ItemInterface item, List<ItemType> showableTypes, 
			boolean purgeEmpyFolders, 
			FilterCriteria filterCriteria, 
			boolean loadGcubeProperties) 
					throws WorkspaceNavigatorServiceException {
		_log.trace("getFolder folderId: "+item.getId()+" showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+filterCriteria);

		try {

			PortalContext pContext = PortalContext.getConfiguration();
			String userName = pContext.getCurrentUser(getThreadLocalRequest()).getUsername();
			String scope = pContext.getCurrentScope(getThreadLocalRequest());
			String authorizationToken = pContext.getCurrentUserToken(scope, userName);
			SecurityTokenProvider.instance.set(authorizationToken);	
			Item folder = StorageHubServiceUtil.getItem(getThreadLocalRequest(), item.getId());
			_log.trace("GetFolder - Replying folder");

			//TO AVOID SLOW CALL getPATH()
			String folderPath = item.getPath()!=null && !item.getPath().isEmpty()?item.getPath():folder.getPath();
			org.gcube.portlets.widgets.wsexplorer.shared.Item itemFolder = ItemBuilder.getItem(null, folder, folderPath, showableTypes, filterCriteria, true, loadGcubeProperties);
			//			_log.trace("Only showable types:");

			if (purgeEmpyFolders) {
				itemFolder = ItemBuilder.purgeEmptyFolders(itemFolder);
			}

			Collections.sort(itemFolder.getChildren(), new ItemComparator());
			_log.info("Returning children size: "+itemFolder.getChildren().size());

			return itemFolder;

		} catch (Exception e) {
			_log.error("Error during folder retrieving", e);
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
	public org.gcube.portlets.widgets.wsexplorer.shared.Item getItemByCategory(ItemCategory category) throws WorkspaceNavigatorServiceException{
		_log.trace("GetItemByCategory category: "+category);
		PortalContext pContext = PortalContext.getConfiguration();
		String userName = pContext.getCurrentUser(getThreadLocalRequest()).getUsername();
		String scope = pContext.getCurrentScope(getThreadLocalRequest());
		String authorizationToken = pContext.getCurrentUserToken(scope, userName);
		SecurityTokenProvider.instance.set(authorizationToken);

		try {

			org.gcube.portlets.widgets.wsexplorer.shared.Item item = null;

			switch(category){
			case HOME:{
				Item root = StorageHubServiceUtil.getRoot(getThreadLocalRequest());
				String fullName = pContext.getCurrentUser(getThreadLocalRequest()).getFullname();
				if(fullName.indexOf(" ")>0){
					fullName = fullName.substring(0, fullName.indexOf(" "));
				}else if(fullName.indexOf(".")>0){
					fullName = fullName.substring(0, fullName.indexOf("."));
				}
				item = new org.gcube.portlets.widgets.wsexplorer.shared.Item(null, root.getId(), fullName+"'s", ItemType.FOLDER, root.getPath(), root.getOwner(), null, true, true);
				break;
			}
			case VRE_FOLDER:{
				String vreFolderId = StorageHubServiceUtil.getVREFoldersId(getThreadLocalRequest());
				Item folder = StorageHubServiceUtil.getItem(getThreadLocalRequest(), vreFolderId);
				item = new org.gcube.portlets.widgets.wsexplorer.shared.Item(
						null, 
						folder.getId(), 
						WorkspaceExplorerConstants.VRE_FOLDERS_LABEL, 
						ItemType.FOLDER, 
						folder.getPath(), 
						folder.getOwner(), 
						null, 
						true, 
						false);
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
	public org.gcube.portlets.widgets.wsexplorer.shared.Item getMySpecialFolder(
			List<ItemType> showableTypes, 
			boolean purgeEmpyFolders, 
			FilterCriteria filterCriteria) 
					throws WorkspaceNavigatorServiceException {
		_log.trace("GetMySpecialFolder showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+filterCriteria);

		try {
			PortalContext pContext = PortalContext.getConfiguration();
			String userName = pContext.getCurrentUser(getThreadLocalRequest()).getUsername();
			String scope = pContext.getCurrentScope(getThreadLocalRequest());
			String authorizationToken = pContext.getCurrentUserToken(scope, userName);
			SecurityTokenProvider.instance.set(authorizationToken);
			String vreFolderId = StorageHubServiceUtil.getVREFoldersId(getThreadLocalRequest());
			Item folder = StorageHubServiceUtil.getItem(getThreadLocalRequest(), vreFolderId);

			org.gcube.portlets.widgets.wsexplorer.shared.Item itemFolder = ItemBuilder.getItem(null, folder, folder.getPath(), showableTypes, filterCriteria, true, false);
			//OVERRIDING VRE FOLDERS NAME - SET SPECIAL FOLDER  /Workspace/MySpecialFolders
			itemFolder.setName(WorkspaceExplorerConstants.VRE_FOLDERS_LABEL);
			itemFolder.setSpecialFolder(true);

			_log.trace("Builded MySpecialFolder: "+itemFolder);

			_log.trace("Only showable types:");

			if (purgeEmpyFolders) {
				itemFolder = ItemBuilder.purgeEmptyFolders(itemFolder);
			}
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
		_log.warn("checkName name NOT IMPLEMENTED: "+name);
		return true; //TODO:
	}

	/**
	 * Gets Breadcrumbs (the list of parents) by item identifier and name ( the name is added as last item of the breadcrumb)
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent -  if parameter is true and item passed in input is a folder, the folder is included in path returned as last parent
	 * @return the list parents by item identifier
	 * @throws Exception the exception
	 */
	@Override
	public ArrayList<org.gcube.portlets.widgets.wsexplorer.shared.Item> getBreadcrumbsByItemIdentifier(
			String itemIdentifier, String itemName, boolean includeItemAsParent) 
					throws Exception {
		System.out.println("ListParents By Item id "+ itemIdentifier + " name="+itemName);
		try {
			List<? extends Item> parents = StorageHubServiceUtil.getParents(getThreadLocalRequest(), itemIdentifier);
			ArrayList<org.gcube.portlets.widgets.wsexplorer.shared.Item> toReturn = new ArrayList<>(parents.size());
			for (Item item : parents) {
				if (item instanceof FolderItem)
					toReturn.add( ItemBuilder.buildFolderForBreadcrumbs((FolderItem) item, null));
				System.out.println("->"+item.getTitle());
			}
			Collections.reverse(toReturn);

			String theClickedFolderName = (itemName == null || itemName.equals("")) ? "current folder" : itemName;
			toReturn.add(new org.gcube.portlets.widgets.wsexplorer.shared.Item(itemIdentifier, theClickedFolderName, true)); //this is the last non clickable item on the BC
			return toReturn;
		} catch (Exception e) {
			_log.error("Error in get List Parents By Item Identifier ", e);
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
	public List<org.gcube.portlets.widgets.wsexplorer.shared.Item> getBreadcrumbsByItemIdentifierToParentLimit(String itemIdentifier, String parentLimit, boolean includeItemAsParent) throws Exception {
		_log.trace("getBreadcrumbsByItemIdentifierToParentLimit by Item Identifier " + itemIdentifier +" and limit: "+parentLimit);

		parentLimit = parentLimit!=null?parentLimit:"";
		try {
			List<? extends Item> parents = StorageHubServiceUtil.getParents(getThreadLocalRequest(), itemIdentifier);
			ArrayList<org.gcube.portlets.widgets.wsexplorer.shared.Item> toWorkOn = new ArrayList<>(parents.size());
			for (Item item : parents) {
				if (item instanceof FolderItem)
					toWorkOn.add(ItemBuilder.buildFolderForBreadcrumbs((FolderItem) item, null));
			}
			Collections.reverse(toWorkOn);

			Item folderItem = StorageHubServiceUtil.getItem(getThreadLocalRequest(), itemIdentifier);
			String theClickedFolderName = folderItem.getTitle();
			if((theClickedFolderName.compareTo(WorkspaceExplorerConstants.SPECIAL_FOLDERS_NAME) == 0)){
				theClickedFolderName = WorkspaceExplorerConstants.VRE_FOLDERS_LABEL;
			} else {
				ItemType type = ItemBuilder.getItemType(folderItem);
				boolean isSharedFolder = (type.equals(ItemType.SHARED_FOLDER) || type.equals(ItemType.VRE_FOLDER )) ? true : false;
				if(isSharedFolder){
					SharedFolder shared = (SharedFolder) folderItem;
					theClickedFolderName = shared.isVreFolder() ? shared.getDisplayName() : folderItem.getTitle();
				}
			}

			toWorkOn.add(new org.gcube.portlets.widgets.wsexplorer.shared.Item(itemIdentifier, theClickedFolderName, true)); //this is the last non clickable item on the BC
			_log.debug("parentLimit id is="+parentLimit);
			boolean found = false;
			ArrayList<org.gcube.portlets.widgets.wsexplorer.shared.Item> toReturn = new ArrayList<>(parents.size());
			for (org.gcube.portlets.widgets.wsexplorer.shared.Item item : toWorkOn) {
				if (item.getId().compareTo(parentLimit)==0) found = true;
				if (found)
					toReturn.add(item);				
			}
			return toReturn;
		} catch (Exception e) {
			_log.error("Error in get List Parents By Item Identifier ", e);
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
		_log.debug("get Size By ItemId "+ itemId);
		Item wsItem = StorageHubServiceUtil.getItem(getThreadLocalRequest(), itemId);
		_log.trace("workspace retrieved item name: "+wsItem.getName());
		if((wsItem instanceof FolderItem)) //if is a folder no Size
			return 0L;

		AbstractFileItem file = (AbstractFileItem) wsItem;
		return file.getContent().getSize();
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
		_log.debug("get MimeType By ItemId "+ itemId);
		Item wsItem = StorageHubServiceUtil.getItem(getThreadLocalRequest(), itemId);
		_log.trace("workspace retrieved item name: "+wsItem.getName());
		if((wsItem instanceof FolderItem)) //if is a folder no mime
			return null;
		AbstractFileItem file = (AbstractFileItem) wsItem;
		return file.getContent().getMimeType();
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
			return StorageHubServiceUtil.getUserACLForFolderId(getThreadLocalRequest(), folderId);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "UNKNOWN";
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
			_log.info("getFormattedSize ByItemId "+ itemId);
			long size = getSizeByItemId(itemId);
			return StringUtil.readableFileSize(size);
		} catch (Exception e) {
			_log.error("getFormattedSize By ItemId ", e);
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
	public org.gcube.portlets.widgets.wsexplorer.shared.Item createFolder(String folderName, String description, String parentId) throws Exception {

		_log.debug("creating folder: "+folderName +", parent id: "+parentId);
		if(parentId==null || parentId.isEmpty())
			throw new Exception("Parent id is null or empty");
		if(folderName == null)
			folderName = "New Folder";
		try {
			FolderItem createdFolder = StorageHubServiceUtil.createFolder(getThreadLocalRequest(), parentId, folderName, description);
			_log.info("Path returned by StoHub: "+createdFolder.getPath());
			List<ItemType> allTypes = Arrays.asList(ItemType.values());
			return ItemBuilder.getItem(null, createdFolder, createdFolder.getPath(), allTypes, null, false, false);
			//		} catch(InsufficientPrivilegesException e){
			//			String error = "Insufficient Privileges to create the folder";
			//			_log.error(error, e);
			//			throw new Exception(error);
			//		} catch (ItemAlreadyExistException e) {
			//			String error = "An error occurred on creating folder, "  +e.getMessage();
			//			_log.error(error, e);
			//			throw new Exception(error);
		} catch (Exception e) {
			String error = "An error occurred on the sever during creating folder. Try again";
			_log.error(error, e);
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
	public Map<String, String> getGcubePropertiesForWorspaceId(String itemId) throws Exception {
		_log.trace("getGcubePropertiesForWorspaceId "+itemId);
		try {
			StorageHubClient shc = new StorageHubClient();
			Item item = shc.open(itemId).asItem().get();
			return ItemBuilder.getGcubePropertiesForItem(item);

		} catch (Throwable e) {
			_log.error(e.getLocalizedMessage(), e);
			return new HashMap<String, String>();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.rpc.WorkspaceExplorerService#getFolder(org.gcube.portlets.widgets.wsexplorer.shared.Item, java.util.List, boolean, org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria, boolean, int, int)
	 */
	@Override
	public SearchedFolder getFolder(
			org.gcube.portlets.widgets.wsexplorer.shared.Item item, List<ItemType> showableTypes, boolean purgeEmpyFolders,
			FilterCriteria filterCriteria, boolean loadGcubeProperties,
			final int startIndex, final int limit, final int serverStartIndex) throws WorkspaceNavigatorServiceException {

		_log.trace("getFolder folderId: "+item.getId()+" showableTypes: "+showableTypes+" purgeEmpyFolders: "+purgeEmpyFolders+" filterCriteria: "+filterCriteria);

		try {

			Item folder = StorageHubServiceUtil.getItem(getThreadLocalRequest(), item.getId());

			int searchStartIndex = startIndex < serverStartIndex? serverStartIndex : startIndex;
			_log.debug("MyLg getFolder searchIndex: "+searchStartIndex+", limit: "+limit);

			//TO AVOID SLOW CALL getPATH()
			String folderPath = item.getPath()!=null && !item.getPath().isEmpty()?item.getPath():folder.getPath();
			org.gcube.portlets.widgets.wsexplorer.shared.Item itemFolderToReturn = ItemBuilder.getItem(null, folder, folderPath, showableTypes, filterCriteria, true, loadGcubeProperties, searchStartIndex, limit);

			SearchedFolder sf = new SearchedFolder(itemFolderToReturn, startIndex, limit, searchStartIndex, false);
			int currentListCount = sf.getFolder().getChildren().size();
			_log.debug("MyLg Total item returning is: "+currentListCount);

			FolderItem hlFolder = (FolderItem) folder;
			int folderChildrenCount = StorageHubServiceUtil.getItemChildrenCount(getThreadLocalRequest(), hlFolder.getId());
			_log.debug("MyLg Folder children count is: "+folderChildrenCount);

			if(currentListCount == limit || folderChildrenCount==0){
				_log.debug("Page completed returning "+currentListCount+ " items");
				int offset = searchStartIndex+limit;
				Collections.sort(sf.getFolder().getChildren(), new ItemComparator());
				sf.setServerSearchFinished(offset>folderChildrenCount || folderChildrenCount == 0);
				_log.debug("is Search finished: "+sf.isServerSearchFinished());
				return sf;
			}

			ArrayList<org.gcube.portlets.widgets.wsexplorer.shared.Item> childrenToReturn = new ArrayList<org.gcube.portlets.widgets.wsexplorer.shared.Item>(limit);
			childrenToReturn.addAll(sf.getFolder().getChildren());

			int offsetStartIndex = searchStartIndex;
			boolean pageOffsetOut = false;
			while(currentListCount < limit && !sf.isServerSearchFinished() && !pageOffsetOut){ //&& SEARCH NOT ULTIMATED
				_log.debug("MyLg new WHILE Items count: "+currentListCount+" is less than limit..");

				int newstartIndex = offsetStartIndex+limit+1;
				_log.debug("MyLg NewStartIndex is startIndex+limit: "+newstartIndex);

				//THERE ARE OTHER CHILDREN OVER NEW START INDEX
				if(newstartIndex < folderChildrenCount){
					//newLimit = limit - childrenToReturn.size();
					_log.debug("MyLg getting items with index start: "+newstartIndex + ", limit: "+limit);
					org.gcube.portlets.widgets.wsexplorer.shared.Item newItemFolder = ItemBuilder.getItem(null, folder, folderPath, showableTypes, filterCriteria, true, loadGcubeProperties, newstartIndex, limit);
					int diff = limit - currentListCount; //How items are remaining
					//int offset = 0;
					_log.debug("MyLg new search start: "+newstartIndex + ", diff: "+diff+ ", retrieved: "+newItemFolder.getChildren().size());
					if(diff >= newItemFolder.getChildren().size()){
						_log.debug("MyLg Adding sublist from 0 to 'diff' "+diff+" to children");
						childrenToReturn.addAll(newItemFolder.getChildren().subList(0, newItemFolder.getChildren().size()));
						//offset = diff;
					}else{
						_log.debug("MyLg PageOffsetOut, the sublist size: "+newItemFolder.getChildren().size()+ " is greather than (limit-currentListCount)"+diff+" leaving WHILE...");
						//childrenToReturn.addAll(newItemFolder.getChildren().subList(0, newItemFolder.getChildren().size()));
						//offset = newItemFolder.getChildren().size();
						pageOffsetOut = true;
					}
					offsetStartIndex = newstartIndex;
					currentListCount = childrenToReturn.size();
					//int realServerEndIndex = newstartIndex+offset;
					_log.debug("MyLg New items count is: "+currentListCount + " serverEndIndex: "+offsetStartIndex);
					sf.setServerEndIndex(offsetStartIndex);
				}else{
					_log.debug("MyLg New start index (oldStartIndex+limit) is grather than folder children count, search is finished");
					sf.setServerSearchFinished(true);
				}
			}

			sf.getFolder().setChildren(childrenToReturn);
			//sf.setServerEndIndex(sf.getServerEndIndex());

			if (purgeEmpyFolders) {
				itemFolderToReturn = ItemBuilder.purgeEmptyFolders(sf.getFolder());
			}

			Collections.sort(itemFolderToReturn.getChildren(), new ItemComparator());
			_log.debug("Returning: "+sf);
			return sf;

		} catch (Exception e) {
			_log.error("Error during folder retrieving", e);
			throw new WorkspaceNavigatorServiceException("Sorry, an error occurred when performing get folder");
		}
	}


	@Override
	public int getFolderChildrenCount(org.gcube.portlets.widgets.wsexplorer.shared.Item item){
		return StorageHubServiceUtil.getItemChildrenCount(getThreadLocalRequest(), item.getId());
	}


}

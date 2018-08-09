/**
 *
 */
package org.gcube.common.storagehubwrapper.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.types.GenericItemType;
import org.gcube.common.storagehubwrapper.server.converter.HLMapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.ACLType;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceVREFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InsufficientPrivilegesException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemAlreadyExistException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemNotFoundException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.WrongDestinationException;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.WrongItemTypeException;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.items.URLFileItem;
import org.gcube.common.storagehubwrapper.shared.tohl.trash.WorkspaceTrashItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class WorkspaceStorageHubClientService.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 20, 2018
 */
public final class WorkspaceStorageHubClientService implements Workspace{

	//public static final String ACCOUNTING_HL_NODE_NAME = "hl:accounting";
	private static Logger logger = LoggerFactory.getLogger(WorkspaceStorageHubClientService.class);
	private StorageHubClientService storageHubClientService;
	private boolean withAccounting;
	private boolean withFileDetails;
	private boolean withMapProperties;

	/**
	 * Gets the storage hub client service.
	 *
	 * @return the storageHubClientService
	 */
	public StorageHubClientService getStorageHubClientService() {

		return storageHubClientService;
	}


	/**
	 * @return the withAccounting
	 */
	public boolean isWithAccounting() {

		return withAccounting;
	}


	/**
	 * @return the withFileDetails
	 */
	public boolean isWithFileDetails() {

		return withFileDetails;
	}


	/**
	 * @return the withMapProperties
	 */
	public boolean isWithMapProperties() {

		return withMapProperties;
	}


	/**
	 * Instantiates a new workspace storage hub client service.
	 *
	 * @param storageHubClientService the storage hub client service
	 */
	private WorkspaceStorageHubClientService(StorageHubClientService storageHubClientService) {
		this.storageHubClientService = storageHubClientService;
	}

	/**
	 * Instantiates a new workspace storage hub client service.
	 *
	 * @param storageHubClientService the storage hub client service
	 * @param withAccounting the with accounting
	 * @param withFileDetails the with file details
	 * @param withMapProperties the with map properties
	 */
	private WorkspaceStorageHubClientService(
		StorageHubClientService storageHubClientService,
		boolean withAccounting, boolean withFileDetails,
		boolean withMapProperties) {

		super();
		this.storageHubClientService = storageHubClientService;
		this.withAccounting = withAccounting;
		this.withFileDetails = withFileDetails;
		this.withMapProperties = withMapProperties;
	}


	/**
	 * The Class WorkspaceStorageHubClientServiceBuilder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * Jun 22, 2018
	 */
	public static class WorkspaceStorageHubClientServiceBuilder{
		private StorageHubClientService storageHubClientService;
		private boolean withAccounting;
		private boolean withFileDetails;
		private boolean withMapProperties;

		/**
		 * Instantiates a new workspace storage hub client service builder.
		 *
		 * @param storageHubClientService the storage hub client service
		 * @param withAccounting the with accounting
		 * @param withFileDetails the with file details
		 * @param withMapProperties the with map properties
		 */
		public WorkspaceStorageHubClientServiceBuilder(
			final StorageHubClientService storageHubClientService,
			final boolean withAccounting, final boolean withFileDetails,
			final boolean withMapProperties) {

			this.storageHubClientService = storageHubClientService;
			this.withAccounting = withAccounting;
			this.withFileDetails = withFileDetails;
			this.withMapProperties = withMapProperties;
		}

		/**
		 * Instantiates a new workspace storage hub client service builder.
		 *
		 * @param storageHubClientService the storage hub client service
		 * @param withAccounting the with accounting
		 * @param withFileDetails the with file details
		 * @param withMapProperties the with map properties
		 */
		public WorkspaceStorageHubClientServiceBuilder(
			final StorageHubClientService storageHubClientService) {

			this.storageHubClientService = storageHubClientService;
			this.withAccounting = false;
			this.withFileDetails = false;
			this.withMapProperties = false;
		}

		/**
		 * Sets the with accounting.
		 *
		 * @param withAccounting the withAccounting to set
		 * @return the workspace storage hub client service builder
		 */
		public WorkspaceStorageHubClientServiceBuilder withAccounting(final boolean withAccounting) {

			this.withAccounting = withAccounting;
			return this;
		}

		/**
		 * Sets the with file details.
		 *
		 * @param withFileDetails the withFileDetails to set
		 * @return the workspace storage hub client service builder
		 */
		public WorkspaceStorageHubClientServiceBuilder withFileDetails(final boolean withFileDetails) {

			this.withFileDetails = withFileDetails;
			return this;
		}

		/**
		 * Sets the with map properties.
		 *
		 * @param withMapProperties the withMapProperties to set
		 * @return the workspace storage hub client service builder
		 */
		public WorkspaceStorageHubClientServiceBuilder withMapProperties(final boolean withMapProperties) {

			this.withMapProperties = withMapProperties;
			return this;
		}

		/**
		 * Builds the WorkspaceStorageHubClientService
		 *
		 * @return the workspace storage hub client service
		 */
		public WorkspaceStorageHubClientService build(){
			return new WorkspaceStorageHubClientService(storageHubClientService,
				withAccounting,
				withFileDetails,
				withMapProperties);
		}
	}



	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getOwner()
	 */
	@Override
	public String getOwner()
		throws InternalErrorException {

		WorkspaceFolder root = getRoot();
		return root.getOwner();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getRoot()
	 */
	@Override
	public WorkspaceFolder getRoot() throws InternalErrorException{
		logger.debug("Getting root");
		FolderItem root;
		try {
			root = storageHubClientService.getRoot();
		}
		catch (Exception e) {
			logger.error("Error on getting root: ", e);
			throw new InternalErrorException("Sorry an error occurred when getting the workspace root. Refresh and try again");
		}
		WorkspaceFolder workspaceFolder = (WorkspaceFolder) HLMapper.toWorkspaceItem(root);
		workspaceFolder.setRoot(true);
		return workspaceFolder;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.Workspace#getChildren(java.lang.String)
	 */
	public List<? extends WorkspaceItem> getChildren(String id){

		Validate.notNull(id,"The input id is null");
		List<? extends Item> children = storageHubClientService.getChildren(id);
		List<WorkspaceItem> toChildren = new ArrayList<WorkspaceItem>(children.size());

		for (Item item : children) {
			WorkspaceItem child = HLMapper.toWorkspaceItem(item);
			toChildren.add(child);
		}

		return toChildren;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getParentsById(java.lang.String)
	 */
	@Override
	public List<? extends WorkspaceItem> getParentsById(String id)
		throws InternalErrorException {

		Validate.notNull(id,"The input id is null");
		List<? extends Item> parents = storageHubClientService.getParents(id);
		List<WorkspaceItem> toParents = new ArrayList<WorkspaceItem>(parents.size());

		for (Item item : parents) {
			WorkspaceItem child = HLMapper.toWorkspaceItem(item);
			toParents.add(child);
		}

		return toParents;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#createFolder(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public WorkspaceFolder createFolder(
		String name, String description, String destinationFolderId)
		throws InternalErrorException, InsufficientPrivilegesException,
		ItemAlreadyExistException, WrongDestinationException,
		ItemNotFoundException, WorkspaceFolderNotFoundException {

		Validate.notNull(destinationFolderId,"The destinationFolderId id is null");
		Validate.notNull(name,"The folderName is null");
		Item item;
		try {
			item = storageHubClientService.createFolder(destinationFolderId, name, description);
			return (WorkspaceFolder) HLMapper.toWorkspaceItem(item);
		}
		catch (Exception e) {
			logger.error("Error on creting the folde: ",e);
			throw new InternalErrorException(e.getMessage());
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getItem(java.lang.String)
	 */
	@Override
	public WorkspaceItem getItem(String itemId) throws ItemNotFoundException, InternalErrorException {

		/*Validate.notNull(itemId,"The input itemId is null");
		Item item;
		try {
			item = storageHubClientService.getItem(itemId);
		}
		catch (Exception e) {
			logger.error("Error during get item with id: "+itemId,e);
			throw new InternalErrorException(e.getMessage());
		}
		return HLMapper.toWorkspaceItem(item, withAccounting, withFileDetails, withMapProperties);*/

		return getItem(itemId, withAccounting, withFileDetails, withMapProperties);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.server.tohl.Workspace#getItem(java.lang.String, boolean, boolean, boolean)
	 */
	@Override
	public WorkspaceItem getItem(
		String itemId, boolean withAccounting, boolean withFileDetails,
		boolean withMapProperties)
		throws ItemNotFoundException, InternalErrorException {

		Validate.notNull(itemId,"The input itemId is null");
		Item item;
		try {
			item = storageHubClientService.getItem(itemId);
		}
		catch (Exception e) {
			logger.error("Error during get item with id: "+itemId,e);
			throw new InternalErrorException(e.getMessage());
		}
		return HLMapper.toWorkspaceItem(item, withAccounting, withFileDetails, withMapProperties);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#createFolder(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public WorkspaceFolder createFolder(
		String name, String description, String destinationFolderId,
		Map<String, String> properties)
		throws InternalErrorException, InsufficientPrivilegesException,
		ItemAlreadyExistException, WrongDestinationException,
		ItemNotFoundException, WorkspaceFolderNotFoundException {

		WorkspaceFolder folder = createFolder(name, description, destinationFolderId);
		//TODO set gcube properties
		return folder;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.server.tohl.Workspace#uploadFile(java.lang.String, java.io.InputStream, java.lang.String, java.lang.String)
	 */
	@Override
	public WorkspaceItem uploadFile(
		String folderId, InputStream is, String fileName, String fileDescription)
		throws InsufficientPrivilegesException,
		WorkspaceFolderNotFoundException, InternalErrorException,
		ItemAlreadyExistException, WrongDestinationException {

		WorkspaceItem wsItem = null;
		try {
			Item item = storageHubClientService.uploadFile(folderId, is, fileName, fileDescription);
			wsItem = HLMapper.toWorkspaceItem(item);
		}
		catch (Exception e) {
			logger.error("Error during upload file ",e);
			throw new InternalErrorException(e.getMessage());
		}

		return wsItem;
	}




	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.server.tohl.Workspace#getSharedFolderMembers(java.lang.String)
	 */
	@Override
	public List<String> getSharedFolderMembers(String folderId) throws Exception {

		Validate.notNull(folderId,"The input folderid is null");
		return storageHubClientService.getSharedFolderMembers(folderId);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#exists(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean exists(String name, String folderId)
		throws InternalErrorException, ItemNotFoundException,
		WrongItemTypeException {

		List<WorkspaceItem> foundItems = find(name, folderId);

		return foundItems!=null && foundItems.size()>0?true:false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#find(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WorkspaceItem> find(String name, String folderId)
		throws InternalErrorException, ItemNotFoundException,
		WrongItemTypeException {

		Validate.notNull(folderId,"The input folderid is null");
		try {
			List<? extends Item> items = storageHubClientService.findByName(name, folderId);
			List<WorkspaceItem> wsItems = null;
			if(items!=null){
				wsItems = new ArrayList<WorkspaceItem>(items.size());
				for (Item item : items) {
					wsItems.add(HLMapper.toWorkspaceItem(item));
				}
			}
			return wsItems;
		}
		catch (Exception e) {
			logger.error("Error during search items with name "+name+" in the parent id: "+folderId, e);
			throw new InternalErrorException(e.getMessage());
		}
	}






























	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#createExternalUrl(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public URLFileItem createExternalUrl(
		String name, String description, String url, String destinationFolderId)
		throws InsufficientPrivilegesException,
		WorkspaceFolderNotFoundException, InternalErrorException,
		ItemAlreadyExistException, WrongDestinationException, IOException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#createExternalUrl(java.lang.String, java.lang.String, java.io.InputStream, java.lang.String)
	 */
	@Override
	public URLFileItem createExternalUrl(
		String name, String description, InputStream url,
		String destinationfolderId)
		throws InsufficientPrivilegesException, InternalErrorException,
		ItemAlreadyExistException, WrongDestinationException,
		WorkspaceFolderNotFoundException, IOException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#removeItem(java.lang.String)
	 */
	@Override
	public void removeItem(String itemId)
		throws ItemNotFoundException, InternalErrorException,
		InsufficientPrivilegesException {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#moveItem(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkspaceItem moveItem(String itemId, String destinationFolderId)
		throws ItemNotFoundException, WrongDestinationException,
		InsufficientPrivilegesException, InternalErrorException,
		ItemAlreadyExistException, WorkspaceFolderNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#renameItem(java.lang.String, java.lang.String)
	 */
	@Override
	public void renameItem(String itemId, String newName)
		throws ItemNotFoundException, InternalErrorException,
		ItemAlreadyExistException, InsufficientPrivilegesException {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#changeDescription(java.lang.String, java.lang.String)
	 */
	@Override
	public void changeDescription(String itemId, String newDescription)
		throws ItemNotFoundException, InternalErrorException {

		// TODO Auto-generated method stub

	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getItemByPath(java.lang.String)
	 */
	@Override
	public WorkspaceItem getItemByPath(String path)
		throws ItemNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#remove(java.lang.String, java.lang.String)
	 */
	@Override
	public void remove(String itemName, String folderId)
		throws ItemNotFoundException, InternalErrorException,
		InsufficientPrivilegesException, WrongItemTypeException {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#copy(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public WorkspaceItem copy(
		String itemId, String newName, String destinationFolderId)
		throws ItemNotFoundException, WrongDestinationException,
		InternalErrorException, ItemAlreadyExistException,
		InsufficientPrivilegesException, WorkspaceFolderNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#copy(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkspaceItem copy(String itemId, String destinationFolderId)
		throws ItemNotFoundException, WrongDestinationException,
		InternalErrorException, ItemAlreadyExistException,
		InsufficientPrivilegesException, WorkspaceFolderNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#searchByName(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WorkspaceItem> searchByName(String name, String folderId)
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#searchByMimeType(java.lang.String)
	 */
	@Override
	public List<WorkspaceItem> searchByMimeType(String mimeType)
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getFolderItems(org.gcube.common.storagehub.model.types.GenericItemType)
	 */
	@Override
	public List<WorkspaceItem> getFolderItems(GenericItemType type)
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#createSharedFolder(java.lang.String, java.lang.String, java.util.List, java.lang.String)
	 */
	@Override
	public WorkspaceSharedFolder createSharedFolder(
		String name, String description, List<String> users,
		String destinationFolderId)
		throws InternalErrorException, InsufficientPrivilegesException,
		ItemAlreadyExistException, WrongDestinationException,
		ItemNotFoundException, WorkspaceFolderNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#createSharedFolder(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public WorkspaceSharedFolder createSharedFolder(
		String name, String description, String groupId,
		String destinationFolderId, String displayName, boolean isVREFolder)
		throws InternalErrorException, InsufficientPrivilegesException,
		ItemAlreadyExistException, WrongDestinationException,
		ItemNotFoundException, WorkspaceFolderNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#shareFolder(java.util.List, java.lang.String)
	 */
	@Override
	public WorkspaceSharedFolder shareFolder(
		List<String> users, String destinationFolderId)
		throws InternalErrorException, InsufficientPrivilegesException,
		ItemAlreadyExistException, WrongDestinationException,
		ItemNotFoundException, WorkspaceFolderNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#share(java.util.List, java.lang.String)
	 */
	@Override
	public WorkspaceSharedFolder share(List<String> users, String itemId)
		throws InternalErrorException, InsufficientPrivilegesException,
		ItemAlreadyExistException, WrongDestinationException,
		ItemNotFoundException, WorkspaceFolderNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#createGcubeItem(java.lang.String, java.lang.String, java.util.List, java.lang.String, java.lang.String, java.util.Map, java.lang.String)
	 */
	@Override
	public WorkspaceItem createGcubeItem(
		String name, String description, List<String> scopes, String creator,
		String itemType, Map<String, String> properties,
		String destinationFolderId)
		throws InsufficientPrivilegesException,
		WorkspaceFolderNotFoundException, InternalErrorException,
		ItemAlreadyExistException, WrongDestinationException,
		ItemNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#unshare(java.lang.String)
	 */
	@Override
	public WorkspaceItem unshare(String itemId)
		throws InternalErrorException, ItemNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getTrash()
	 */
	@Override
	public WorkspaceTrashItem getTrash()
		throws InternalErrorException, ItemNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getMySpecialFolders()
	 */
	@Override
	public WorkspaceFolder getMySpecialFolders()
		throws InternalErrorException, ItemNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#searchByProperties(java.util.List)
	 */
	@Override
	public List<WorkspaceItem> searchByProperties(List<String> properties)
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getVREFolderByScope(java.lang.String)
	 */
	@Override
	public WorkspaceSharedFolder getVREFolderByScope(String scope)
		throws ItemNotFoundException, InternalErrorException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getDiskUsage()
	 */
	@Override
	public long getDiskUsage()
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getTotalItems()
	 */
	@Override
	public int getTotalItems()
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#removeItems(java.lang.String[])
	 */
	@Override
	public Map<String, String> removeItems(String... id)
		throws ItemNotFoundException, InternalErrorException,
		InsufficientPrivilegesException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#createVREFolder(java.lang.String, java.lang.String, java.lang.String, org.gcube.portal.storagehubwrapper.shared.ACLType)
	 */
	@Override
	public WorkspaceVREFolder createVREFolder(
		String scope, String description, String displayName, ACLType privilege)
		throws InternalErrorException, InsufficientPrivilegesException,
		ItemAlreadyExistException, WrongDestinationException,
		ItemNotFoundException, WorkspaceFolderNotFoundException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getGroup(java.lang.String)
	 */
	@Override
	public String getGroup(String groupId)
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#isGroup(java.lang.String)
	 */
	@Override
	public boolean isGroup(String groupId)
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getPublicFolders()
	 */
	@Override
	public List<WorkspaceItem> getPublicFolders()
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return null;
	}




}

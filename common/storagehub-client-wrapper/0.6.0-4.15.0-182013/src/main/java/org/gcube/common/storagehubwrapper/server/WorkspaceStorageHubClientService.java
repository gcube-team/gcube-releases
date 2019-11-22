/**
 *
 */
package org.gcube.common.storagehubwrapper.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.ExternalLink;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.nodes.ImageContent;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry;
import org.gcube.common.storagehub.model.service.Version;
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
import org.gcube.common.storagehubwrapper.shared.tohl.impl.GcubeItem;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFileVersion;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.items.ItemStreamDescriptor;
import org.gcube.common.storagehubwrapper.shared.tohl.items.PropertyMap;
import org.gcube.common.storagehubwrapper.shared.tohl.items.URLItem;
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
	private static Logger LOGGER = LoggerFactory.getLogger(WorkspaceStorageHubClientService.class);
	private StorageHubClientService storageHubClientService;
	private boolean withAccounting;
	private boolean withFileDetails;
	private boolean withMapProperties;
	
	private WorkspaceFolder workspaceRoot = null; 

	/**
	 * Gets the storage hub client service.
	 *
	 * @return the storageHubClientService
	 */
	public StorageHubClientService getStorageHubClientService() {

		return storageHubClientService;
	}


	/**
	 * Checks if is with accounting.
	 *
	 * @return the withAccounting
	 */
	public boolean isWithAccounting() {

		return withAccounting;
	}


	/**
	 * Checks if is with file details.
	 *
	 * @return the withFileDetails
	 */
	public boolean isWithFileDetails() {

		return withFileDetails;
	}


	/**
	 * Checks if is with map properties.
	 *
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
		 * Builds the WorkspaceStorageHubClientService.
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
		throws InternalErrorException, Exception {
		
		LOGGER.debug("called get Owner");
		WorkspaceFolder root = getRoot();
		return root.getOwner();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getRoot()
	 */
	@Override
	public WorkspaceFolder getRoot() throws InternalErrorException, Exception{
		
		LOGGER.debug("called get Root");
		FolderItem root;
		try {
			root = storageHubClientService.getRoot();
		}
		catch (Exception e) {
			LOGGER.error("Error on getting root: ", e);
			throw new InternalErrorException("Sorry an error occurred when getting the workspace root. Refresh and try again");
		}
		WorkspaceFolder workspaceFolder = (WorkspaceFolder) HLMapper.toWorkspaceItem(root, withAccounting, withFileDetails, withMapProperties);
		workspaceFolder.setRoot(true);
		workspaceRoot = workspaceFolder;
		return workspaceFolder;
	}


	/**
	 * We need to perform this method to set the root attribute.
	 * No other way to get this information by SHUB
	 *
	 * @param item the item
	 * @return the workspace item
	 * @throws InternalErrorException the internal error exception
	 * @throws Exception the exception
	 */
	private WorkspaceItem setIsRoot(org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceItem item) throws InternalErrorException, Exception {
		
		LOGGER.debug("called set Is Root");
		Validate.notNull(item,"The input item is null");
		if(workspaceRoot==null) {
			LOGGER.debug("Workspace root instance is null into "+WorkspaceStorageHubClientService.class.getSimpleName()+", instancing it as first time");
			getRoot();
		}
		boolean isRoot = workspaceRoot.getId().compareTo(item.getId())==0;
		item.setRoot(isRoot);
		LOGGER.info("Is the item '"+item.getName()+"' with id '"+item.getId()+"' the root? " +item.isRoot());
		return item;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.Workspace#getChildren(java.lang.String)
	 */
	public List<? extends WorkspaceItem> getChildren(String id) throws Exception{
		
		LOGGER.debug("called get Children");
		Validate.notNull(id,"The input id is null");
		List<? extends Item> children = storageHubClientService.getChildren(id, withAccounting, withMapProperties);
		List<WorkspaceItem> toChildren = new ArrayList<WorkspaceItem>(children.size());

		for (Item item : children) {
			WorkspaceItem child = HLMapper.toWorkspaceItem(item, withAccounting, withFileDetails, withMapProperties);
			toChildren.add(child);
		}

		return toChildren;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.tohl.Workspace#getChildren(java.lang.String)
	 */
	public List<? extends WorkspaceItem> getFilteredChildren(String id, Class<? extends Item> aType) throws Exception{
		
		LOGGER.debug("called get Filtered Children");
		Validate.notNull(id,"The input id is null");
		List<? extends Item> children = storageHubClientService.getFilteredChildren(id, aType, withAccounting, withMapProperties);
		List<WorkspaceItem> toChildren = new ArrayList<WorkspaceItem>(children.size());

		for (Item item : children) {
			WorkspaceItem child = HLMapper.toWorkspaceItem(item, withAccounting, withFileDetails, withMapProperties);
			toChildren.add(child);
		}

		return toChildren;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getParentsById(java.lang.String)
	 */
	@Override
	public List<? extends WorkspaceItem> getParentsById(String id)
		throws InternalErrorException, Exception {
		
		LOGGER.debug("called get Parents by Id");
		Validate.notNull(id,"The input id is null");
		List<? extends Item> parents = storageHubClientService.getParents(id);
		List<WorkspaceItem> toParents = new ArrayList<WorkspaceItem>(parents.size());

		for (Item item : parents) {
			WorkspaceItem child = HLMapper.toWorkspaceItem(item, withAccounting, withFileDetails, withMapProperties);
			setIsRoot((org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceItem) child);
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
		
		LOGGER.debug("called create Folder");
		Validate.notNull(destinationFolderId,"The destinationFolderId id is null");
		Validate.notNull(name,"The folderName is null");
		Item item;
		try {
			item = storageHubClientService.createFolder(destinationFolderId, name, description);
			return (WorkspaceFolder) HLMapper.toWorkspaceItem(item, withAccounting, withFileDetails, withMapProperties);
		}
		catch (Exception e) {
			LOGGER.error("Error on creating the folder: ",e);
			throw new InternalErrorException(e.getMessage());
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getItem(java.lang.String)
	 */
	@Override
	public WorkspaceItem getItem(String itemId) throws ItemNotFoundException, InternalErrorException, Exception {

		return getItem(itemId, withAccounting, withFileDetails, withMapProperties);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.server.tohl.Workspace#getItem(java.lang.String, boolean, boolean, boolean)
	 */
	@Override
	public WorkspaceItem getItem(
		String itemId, boolean withAccounting, boolean withFileDetails,
		boolean withMapProperties)
		throws ItemNotFoundException, InternalErrorException, Exception {
		
		LOGGER.debug("called get Item");
		Validate.notNull(itemId,"The input itemId is null");
		Item item;
		try {
			item = storageHubClientService.getItem(itemId, withAccounting, withMapProperties);
		}
		catch (Exception e) {
			LOGGER.error("Error during get item with id: "+itemId,e);
			throw new InternalErrorException(e.getMessage());
		}
		WorkspaceItem workspaceItem = HLMapper.toWorkspaceItem(item, withAccounting, withFileDetails, withMapProperties);
		setIsRoot((org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceItem) workspaceItem);
		return workspaceItem;
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
		throws Exception {
		
		LOGGER.debug("called upload File");
		WorkspaceItem wsItem = null;
		try {
			Item item = storageHubClientService.uploadFile(folderId, is, fileName, fileDescription);
			wsItem = HLMapper.toWorkspaceItem(item);
		}
		catch (Exception e) {
			LOGGER.error("Error uploading the file: "+fileName+" in the folderId: "+folderId, e);
			throw e;
		}

		return wsItem;
	}




	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.server.tohl.Workspace#getSharedFolderMembers(java.lang.String)
	 */
	@Override
	public List<String> getSharedFolderMembers(String folderId) throws Exception {

		try{
			LOGGER.debug("called Shared Folder Members");
			Validate.notNull(folderId,"The input folderid is null");
			return storageHubClientService.getSharedFolderMembers(folderId);
		}catch (Exception e) {
			LOGGER.error("Error during get shared folder members with id: "+folderId,e);
			throw new ItemNotFoundException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#exists(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean exists(String name, String folderId)
		throws InternalErrorException, ItemNotFoundException,
		WrongItemTypeException {
		
		LOGGER.debug("called exists");
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

		LOGGER.debug("called find");
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
			LOGGER.error("Error during search items with name "+name+" in the parent id: "+folderId, e);
			throw new InternalErrorException(e.getMessage());
		}
	}


	/**
	 * Gets the root shared folder.
	 *
	 * @param itemId the item id
	 * @return the root shared folder
	 * @throws Exception the exception
	 */
	public WorkspaceItem getRootSharedFolder(String itemId) throws Exception {
		
		LOGGER.debug("called get Root Shared Folder");
		Validate.notNull(itemId,"The input itemId is null");

		try{
			FolderItem folderItem = storageHubClientService.getRootSharedFolder(itemId);
			return HLMapper.toWorkspaceItem(folderItem);
		}catch(Exception e){
			LOGGER.error("Get root shared folder error: ", e);
			throw new Exception("Error on getting the root shared folder. "+e.getMessage());
		}
	}



	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#removeItem(java.lang.String)
	 */
	@Override
	public void deleteItem(String itemId)
		throws Exception {
		
		LOGGER.debug("called deleteItem");
		Validate.notNull(itemId,"The input itemId is null");

		try{
			storageHubClientService.deleteItemById(itemId);

		}catch(Exception e){
			LOGGER.error("Delete item by id error: "+e.getMessage());
			throw new Exception("Erro on deleting the item. "+e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#removeItems(java.lang.String[])
	 */
	@Override
	public Map<String, String> removeItems(String... id)
		throws ItemNotFoundException, InternalErrorException,
		InsufficientPrivilegesException {

		Map<String, String> errors = new HashMap<String, String>();

		for (int i = 0; i < id.length; i++) {
			try{
				deleteItem(id[i]);
			}catch(Exception e){
				errors.put(id[i], e.getMessage());
			}
		}

		return errors;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#uploadArchive(java.lang.String, java.io.InputStream, java.lang.String)
	 */
	@Override
	public	WorkspaceItem uploadArchive(
		String folderId, InputStream is, String extractionFolderName)
		throws Exception{

		WorkspaceItem wsItem = null;
		try {
			Item item = storageHubClientService.uploadArchive(folderId, is, extractionFolderName);
			wsItem = HLMapper.toWorkspaceItem(item);
		}
		catch (Exception e) {
			LOGGER.error("Error occurred uploading the archive: "+extractionFolderName+" in the folderId: "+folderId, e);
			throw e;
		}

		return wsItem;
	}


	/**
	 * Checks if is item shared.
	 *
	 * @param itemId the item id
	 * @return true, if is item shared
	 * @throws Exception the exception
	 */
	@Override
	public boolean isItemShared(String itemId) throws Exception {
		Validate.notNull(itemId,"The input itemId is null");
		try{
			return storageHubClientService.isItemShared(itemId);

		}catch(Exception e){
			throw new Exception("Error on cheking if the item is shared: "+e.getMessage());
		}

	}


	/**
	 * Gets the VRE folders id.
	 *
	 * @return the VRE folders id
	 * @throws Exception the exception
	 */
	@Override
	public String getVREFoldersId() throws Exception{
		try{
			return storageHubClientService.getVREFoldersId();

		}catch(Exception e){
			throw new Exception("Error on getting vre folder id: "+e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#getTrash()
	 */
	@Override
	public WorkspaceItem getTrash()
		throws Exception {
		try{
			Item baseFolderTrash = storageHubClientService.openTrash();
			return HLMapper.toWorkspaceItem(baseFolderTrash);

		}catch(Exception e){
			throw new Exception("Error on getting the Trash content: "+e.getMessage());
		}
	}


	/**
	 * Empty trash.
	 *
	 * @throws Exception the exception
	 */
	@Override
	public void emptyTrash() throws Exception{

		try{
			storageHubClientService.emptyTrash();

		}catch(Exception e){
			throw new Exception("Error on emptying the Trash: "+e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#restoreThrashItem(java.lang.String)
	 */
	@Override
	public WorkspaceItem restoreThrashItem(String itemId) throws Exception{

		try{
			 Item theItem = storageHubClientService.restoreThrashItem(itemId);
			 return HLMapper.toWorkspaceItem(theItem);

		}catch(Exception e){
			throw new Exception("Error on restoring the Trash Item: "+e.getMessage());
		}

	}


	/**
	 * Download file.
	 *
	 * @param itemId the item id
	 * @param fileName the file name
	 * @param versionName the version name
	 * @param nodeIdsToExclude the node ids to exclude
	 * @return the file stream descriptor
	 * @throws Exception the exception
	 */
	@Override
	public ItemStreamDescriptor downloadFile(String itemId, String fileName, String versionName, String nodeIdsToExclude) throws Exception{

		try {
			StreamDescriptor streamDesc = storageHubClientService.downloadFile(itemId, versionName, nodeIdsToExclude);
			return new org.gcube.common.storagehubwrapper.shared.tohl.impl.StreamDescriptor(streamDesc.getStream(), streamDesc.getFileName(), null, null);
		} catch (Exception e) {
			LOGGER.error("Error on downloading the file: "+fileName+ " with id: "+itemId, e);
			String error = e.getMessage()!=null?e.getMessage():"";
			throw new Exception("Error on downloading the file: "+fileName+". "+error);
		}
	}



	/**
	 * Download folder.
	 *
	 * @param folderId the folder id
	 * @param folderName the folder name
	 * @param nodeIdsToExclude the node ids to exclude
	 * @return the file stream descriptor
	 * @throws Exception the exception
	 */
	@Override
	public ItemStreamDescriptor downloadFolder(String folderId, String folderName, String nodeIdsToExclude) throws Exception{

		try {
			StreamDescriptor streamDesc = storageHubClientService.downloadFolder(folderId, nodeIdsToExclude);
			return new org.gcube.common.storagehubwrapper.shared.tohl.impl.StreamDescriptor(streamDesc.getStream(), streamDesc.getFileName(), null, null);
		} catch (Exception e) {
			LOGGER.error("Error on downloading the folder: "+folderName+ " with id: "+folderId, e);
			String error = e.getMessage()!=null?e.getMessage():"";
			throw new Exception("Error on downloading the folder: "+folderName+". "+error);
		}
	}



	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#moveItems(java.util.List, java.lang.String)
	 */
	@Override
	public List<WorkspaceItem> moveItems(List<String> itemIds, String folderDestinationId) throws ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, InternalErrorException, ItemAlreadyExistException, Exception {

		FolderContainer destFolderContainer = null;

		try {

			if(itemIds==null || itemIds.size()==0)
				throw new Exception("Input list of id for moving is null or empty");

			Validate.notNull(folderDestinationId,"The folderDestinationId is null");
			destFolderContainer = storageHubClientService.getFolderContainer(folderDestinationId);
			List<WorkspaceItem> toReturnItems = new ArrayList<WorkspaceItem>(itemIds.size());

			for (String itemId : itemIds) {

				try{
					Item movedItem = storageHubClientService.moveItem(itemId, destFolderContainer);
					toReturnItems.add(HLMapper.toWorkspaceItem(movedItem));
				}catch(Exception e){
					LOGGER.error("Error on moving the item with id: "+itemId+ " in the folder id: "+destFolderContainer.get().getId(), e);
					throw e;
				}
			}

			return toReturnItems;

		} catch (Exception e) {
			LOGGER.error("Error on moving item in the folder with id: "+folderDestinationId + e.getMessage());
			String error = e.getMessage()!=null?e.getMessage():"";
			throw new Exception("Error on moving item/s. "+error);
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#copyFileItems(java.util.List, java.lang.String)
	 */
	@Override
	public List<WorkspaceItem> copyFileItems(List<String> itemIds, String folderDestinationId) throws ItemNotFoundException, WrongDestinationException, InternalErrorException, ItemAlreadyExistException, InsufficientPrivilegesException, Exception {


		FolderContainer destFolderContainer = null;

		try {

			if(itemIds==null || itemIds.size()==0)
				throw new Exception("Input list of id for copying is null or empty");

			Validate.notNull(folderDestinationId,"The folderDestinationId is null");
			destFolderContainer = storageHubClientService.getFolderContainer(folderDestinationId);
			List<WorkspaceItem> toReturnItems = new ArrayList<WorkspaceItem>(itemIds.size());

			for (String itemId : itemIds) {

				try{
					AbstractFileItem toReturnItem = storageHubClientService.copyFileItem(itemId, destFolderContainer, null);
					toReturnItems.add(HLMapper.toWorkspaceItem(toReturnItem));
				}catch(Exception e){
					LOGGER.error("Error on copying the item with id: "+itemId+ " in the folder id: "+destFolderContainer.get().getId(), e);
				}
			}

			return toReturnItems;

		} catch (Exception e) {
			LOGGER.error("Error on copying item/items in the folder with id: "+folderDestinationId, e);
			String error = e.getMessage()!=null?e.getMessage():"";
			throw new Exception("Error on copying item/s. "+error);
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#copyFile(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkspaceItem copyFile(String itemId, String folderDestinationId) throws ItemNotFoundException, WrongDestinationException, InternalErrorException, ItemAlreadyExistException, InsufficientPrivilegesException, Exception {

		List<WorkspaceItem> list = copyFileItems(Arrays.asList(itemId), folderDestinationId);
		return list==null||list.isEmpty()?null:list.get(0);

	}


	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#moveItem(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkspaceItem moveItem(String itemId, String folderDestinationId) throws ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, Exception{

		List<WorkspaceItem> list = moveItems(Arrays.asList(itemId), folderDestinationId);
		return list==null||list.isEmpty()?null:list.get(0);

	}


	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#renameItem(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkspaceItem renameItem(String itemId, String newName)
		throws ItemNotFoundException, InternalErrorException,
		ItemAlreadyExistException, InsufficientPrivilegesException, Exception{

		try{
			Item item = storageHubClientService.renameItem(itemId, newName);
			return HLMapper.toWorkspaceItem(item);
		}catch(Exception e){
			LOGGER.error("Error on renaming item with id: "+itemId, e);
			String error = e.getMessage()!=null?e.getMessage():"Operation not allowed";
			throw new Exception("Error on renaming. "+error);
		}

	}


	/**
	 * Gets the public link for file.
	 *
	 * @param fileItemId the file item id
	 * @return the public link for file
	 * @throws Exception the exception
	 */
	public URL getPublicLinkForFile(String fileItemId) throws Exception{

		try{
			return storageHubClientService.getPublicLinkForFile(fileItemId);
		}catch(Exception e){
			LOGGER.error("Error on getting public link: "+fileItemId, e);
			String error = e.getMessage()!=null?e.getMessage():"Operation not allowed";
			throw new Exception("Error on getting public link. "+error);
		}
	}


	/**
	 * Gets the public link for file.
	 *
	 * @param fileItemId the file item id
	 * @param version the version
	 * @return the public link for file
	 * @throws Exception the exception
	 */
	public URL getPublicLinkForFile(String fileItemId, String version) throws Exception{

		try{
			Validate.notNull(fileItemId,"The fileItemId is null");
			return storageHubClientService.getPublicLinkForFileVersion(fileItemId, version);
		}catch(Exception e){
			LOGGER.error("Error on getting public link for file: "+fileItemId +" with version: "+version, e);
			String error = e.getMessage()!=null?e.getMessage():"Operation not allowed";
			throw new Exception("Error on getting public link for file: "+fileItemId +" with version: "+version+". Error: "+error);
		}
	}


	/**
	 * Gets the list versions for file.
	 *
	 * @param fileItemId the file item id
	 * @return the list versions for file
	 * @throws Exception the exception
	 */
	public List<WorkspaceFileVersion> getListVersionsForFile(String fileItemId) throws Exception{
		
		try{
			Validate.notNull(fileItemId,"The fileItemId is null");
			List<Version> versions = storageHubClientService.getListVersions(fileItemId);

			if(versions==null || versions.size()==0){
				LOGGER.info("No version found for fileItemId: "+fileItemId);
				return new ArrayList<WorkspaceFileVersion>(1);
			}

			List<WorkspaceFileVersion> listVersions = new ArrayList<WorkspaceFileVersion>(versions.size());

			for (Version version : versions) {
				listVersions.add(HLMapper.toWorkspaceFileVersion(version));
			}

			return listVersions;

		}catch(Exception e){
			LOGGER.error("Error on getting list of versions for: "+fileItemId, e);
			String error = e.getMessage()!=null?e.getMessage():"Operation not allowed";
			throw new Exception("Error on getting public link. "+error);
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#getThumbnailData(java.lang.String)
	 */
	public ItemStreamDescriptor getThumbnailData(String itemId) throws Exception{

		try{
			ImageContent imgContent =  storageHubClientService.getImageContent(itemId);
			byte[] thumbBytes = imgContent.getThumbnailData();
    		if(thumbBytes==null || thumbBytes.length==0)
    			throw new Exception("Thumbnail Data for image with id: "+itemId +" is not available (null or empty) on SHUB");

			return new org.gcube.common.storagehubwrapper.shared.tohl.impl.StreamDescriptor(new ByteArrayInputStream(thumbBytes), null, new Long(thumbBytes.length),imgContent.getMimeType());

		}catch(Exception e){
			LOGGER.warn("Error on getting thumbnail data for: "+itemId);
			throw new Exception("Error on getting the thumbnail. "+e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#getMetadata(java.lang.String)
	 */
	@Override
	public Map<String, Object> getMetadata(String itemId) throws Exception {

		try{
			return storageHubClientService.getMetadata(itemId);

		}catch(Exception e){
			LOGGER.error("Error on getting Metadata for: "+itemId, e);
			throw new Exception("Error on getting Metadata for: "+itemId);
		}
	}
	
	/**
	 * Gets the total items.
	 *
	 * @return the total items
	 * @throws Exception the exception
	 */
	@Override
	public long getTotalItems() throws Exception{

		try{
			return storageHubClientService.getTotalItems();
		}catch(Exception e){
			String error = "Error on getting total items: ";
			LOGGER.error(error,e);
			throw new Exception(error,e);
		}
	}
	
	
	/**
	 * Gets the disk usage.
	 *
	 * @return the disk usage
	 * @throws Exception the exception
	 */
	@Override
	public long getDiskUsage() throws Exception{
		try{
			return storageHubClientService.getDiskUsage();
		}catch(Exception e){
			String error = "Error on getting disk usage: ";
			LOGGER.error(error,e);
			throw new Exception(error,e);
		}
	}
	

	/**
	 * Gets the accounting.
	 *
	 * @param itemId the item id
	 * @return the accounting
	 * @throws Exception the exception
	 */
	public List<AccountEntry> getAccounting(String itemId) throws Exception{

		try{
			Item theItem = storageHubClientService.getItem(itemId, withAccounting, withMapProperties);
			return HLMapper.toAccountingEntries(theItem);
		}catch(Exception e){
			LOGGER.error("Error on getAccounting for: "+itemId, e);
			throw new Exception("Error on getAccounting for: "+itemId);
		}
	}


	/**
	 * Gets the gcube item properties.
	 *
	 * @param gcubeItemId the gcube item id
	 * @return the gcube item properties
	 * @throws Exception the exception
	 */
	public PropertyMap getGcubeItemProperties(String gcubeItemId) throws Exception {
		
		try{
			Metadata theMetadata = storageHubClientService.getGcubeItemProperties(gcubeItemId);
			return HLMapper.toPropertyMap(theMetadata);
		}catch(Exception e){
			String error = "Error on getting gcube item properties for: "+gcubeItemId;
			LOGGER.error(error, e);
			throw new Exception(error);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#updateMetadata(java.lang.String, java.util.Map)
	 */
	public void updateMetadata(String itemId, Map<String,Object> mapObjs) throws Exception {
		
		try{
			storageHubClientService.setMetadata(itemId,mapObjs);
		}catch(Exception e){
			String error = "Error on adding properties to "+GcubeItem.class.getSimpleName()+" with id: "+itemId;
			LOGGER.error(error, e);
			throw new Exception(error);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#createExternalUrl(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public URLItem createURL(
		String fileName, String fileDescription, String url, String destinationFolderId)
		throws Exception {
		
		Validate.notNull(url, "The input URL is null");
		Validate.notEmpty(url, "The input URL is empty");
		URL theURL = null;
		try {
			theURL = new URL(url);
		}catch (Exception e) {
			String error = "The input URL "+url+" seems not to be a valid URL";
			LOGGER.error(error, e);
			throw new Exception(error);
		}
		ExternalLink extLink = storageHubClientService.addURL(destinationFolderId, theURL, fileName, fileDescription);
		return HLMapper.toURLFile(extLink);
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.common.storagehubwrapper.server.tohl.Workspace#setFolderAsPublic(java.lang.String, boolean)
	 */
	@Override
	public boolean setFolderAsPublic(String folderId, boolean setPublic) throws Exception{
		
		WorkspaceItem item =  getItem(folderId);
		if (item.isFolder()) {
			//TODO
			WorkspaceFolder folder = (WorkspaceFolder) item;
			
			//Changing access to folder only if needed
			if(setPublic!=folder.isPublicFolder()) {
				LOGGER.info("Changing access to folder: "+folderId+" as "+setPublic);
				return storageHubClientService.setFolderAsPublic(folderId, setPublic);
			}else {
				LOGGER.info("Access to folder: "+folderId+" is already "+setPublic+" skipping operation");
				return setPublic;
			}
		}else
			throw new Exception("The item with id '"+folderId+"' is not a folder");
	}
	
	/**
	 * Can user write into folder.
	 *
	 * @param folderId the folder id
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Override
	public boolean canUserWriteIntoFolder(String folderId) throws Exception{
		
		WorkspaceItem item =  getItem(folderId);
		if (item.isFolder()) {
			return storageHubClientService.canWrite(folderId);
		}else
			throw new Exception("The item with id '"+folderId+"' is not a folder");
	}

	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * TODO
	 * 
	 * FOLLOWING METHODS ARE NOT IMPLEMENTED;
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

























	/* (non-Javadoc)
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#changeDescription(java.lang.String, java.lang.String)
	 */
	@Override
	public void changeDescription(String itemId, String newDescription)
		throws ItemNotFoundException, InternalErrorException {

		// TODO Auto-generated method stub

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
	 * @see org.gcube.portal.storagehubwrapper.shared.Workspace#getPublicFolders()
	 */
	@Override
	public List<WorkspaceItem> getPublicFolders()
		throws InternalErrorException {

		// TODO Auto-generated method stub
		return null;
	}



}

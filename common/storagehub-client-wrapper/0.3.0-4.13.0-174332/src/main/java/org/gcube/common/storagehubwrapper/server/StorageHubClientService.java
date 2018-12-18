package org.gcube.common.storagehubwrapper.server;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.GenericItemContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.ListResolver;
import org.gcube.common.storagehub.client.dsl.ListResolverTyped;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.VreFolder;
import org.gcube.common.storagehub.model.items.nodes.ImageContent;
import org.gcube.common.storagehub.model.service.Version;
import org.gcube.common.storagehubwrapper.server.converter.ObjectMapper;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class StorageHubClientService.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 22, 2018
 */
public class StorageHubClientService {


	private static Logger logger = LoggerFactory.getLogger(StorageHubClientService.class);
	public static final String ACCOUNTING_HL_NODE_NAME = "hl:accounting";
	private String scope;
	private String authorizationToken;
	private StorageHubClient shcClient;


	/**
	 * Instantiates a new storage hub service util.
	 *
	 * @param scope the scope
	 * @param authorizationToken the authorization token
	 */
	public StorageHubClientService(String scope, String authorizationToken) {
		Validate.notNull(scope, "The scope is null");
		Validate.notNull(authorizationToken, "The authorizationToken is null");
		this.scope = scope;
		this.authorizationToken = authorizationToken;
		setContextProviders(scope, authorizationToken);
		shcClient = new StorageHubClient();
		logger.info("Instancied the "+StorageHubClientService.class.getSimpleName()+" as: "+this.toString());
	}

	/**
	 * Sets the context providers.
	 *
	 * @param scope the scope
	 * @param authorizationToken the authorization token
	 */
	private void setContextProviders(String scope, String authorizationToken){
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(authorizationToken);
		logger.debug("Saved the contexts [scope: "+scope+", token: "+authorizationToken+"]");
	}

	/**
	 * Gets the root.
	 *
	 * @return the root
	 * @throws Exception the exception
	 */
	public FolderItem getRoot() throws Exception {
		setContextProviders(scope, authorizationToken);
		FolderContainer root = shcClient.getWSRoot();
		return root.get();
	}

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public Item getTrash() {
		setContextProviders(scope, authorizationToken);
		WorkspaceManagerClient client = AbstractPlugin.workspace().build();
		Item trash = client.getTrashFolder();
		return trash;
	}


	/**
	 * Gets the children.
	 *
	 * @param id the id
	 * @param withAccounting the with accounting
	 * @param withMapProperties the with map properties
	 * @return the children
	 */
	public List<? extends Item> getChildren(String id, boolean withAccounting, boolean withMapProperties){
		setContextProviders(scope, authorizationToken);
		logger.trace("Requesting getChildren for id: "+id+" [withAccounting: "+withAccounting+", withMapProperties: "+withMapProperties+"]");
		ListResolverTyped resolverTyped = shcClient.open(id).asFolder().list();
		ListResolver theResolver = resolverTyped.withContent();

		if(withAccounting)
			theResolver = theResolver.withAccounting();

		if(withMapProperties)
			theResolver = theResolver.withMetadata();

		return theResolver.getItems();
	}


	/**
	 * Gets the filtered children.
	 *
	 * @param id the id
	 * @param aType the a type
	 * @param withAccounting the with accounting
	 * @param withMapProperties the with map properties
	 * @return the filtered children
	 */
	public List<? extends Item> getFilteredChildren(String id, Class<? extends Item> aType, boolean withAccounting, boolean withMapProperties){
		setContextProviders(scope, authorizationToken);

		ListResolver resolverTyped = shcClient.open(id).asFolder().list().ofType(aType);
		ListResolver theResolver = resolverTyped.withContent();

		if(withAccounting)
			theResolver = theResolver.withAccounting();

		if(withMapProperties)
			theResolver = theResolver.withMetadata();

		return theResolver.getItems();
	}


	/**
	 * Gets the item.
	 *
	 * @param itemId the item id
	 * @param withAccounting the with accounting
	 * @param withMetadata the with metadata
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item getItem(String itemId, boolean withAccounting, boolean withMetadata) throws Exception{
		setContextProviders(scope, authorizationToken);
		ItemContainer<Item> itemCont;

		//MUST BE CHANGED. MOVE IT TO BUILDER?
		if(withMetadata)
			itemCont = shcClient.open(itemId).asItem(); //TODO
		else if(withAccounting){
			itemCont = shcClient.open(itemId).asItem(); //TODO
		}
		else
			itemCont = shcClient.open(itemId).asItem();

		return itemCont.get();
	}

	/**
	 * Gets the item.
	 *
	 * @param itemId the item id
	 * @param withAccounting the with accounting
	 * @param withMetadata the with metadata
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item getItem(String itemId) throws Exception{
		return getItem(itemId,false,true);
	}



	/**
	 * Sets the metadata and returns the Item with metadata updated.
	 *
	 * @param itemId the item id
	 * @param metadata the metadata
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item setMetadata(String itemId, Metadata metadata) throws Exception{
		setContextProviders(scope, authorizationToken);
		ItemContainer<Item> itemCont = shcClient.open(itemId).asItem();
		itemCont.setMetadata(metadata);
		return itemCont.get();
	}


	/**
	 * Gets the folder container.
	 *
	 * @param itemId the item id
	 * @return the folder container
	 * @throws Exception the exception
	 */
	public FolderContainer getFolderContainer(String itemId) throws Exception{
		setContextProviders(scope, authorizationToken);
		return shcClient.open(itemId).asFolder();
	}


	/**
	 * Gets the parents.
	 *
	 * @param itemId the item id
	 * @return the parents
	 */
	public List<? extends Item> getParents(String itemId) {
		setContextProviders(scope, authorizationToken);
		ListResolver toReturn = shcClient.open(itemId).asItem().getAnchestors();
		if(toReturn==null || toReturn.getItems()==null){
			logger.warn("Parent List of item id "+itemId+" is null");
			return null;
		}
		return toReturn.getItems();
	}

	//TODO MAP OF SHARED ROOT ID


	/**
	 * Gets the id shared folder.
	 *
	 * @param itemId the item id
	 * @return the id shared folder
	 * @throws Exception the exception
	 */
	public String getIdSharedFolder(String itemId) throws Exception {

		return getRootSharedFolder(itemId).getId();
	}


	/**
	 * Gets the root shared folder.
	 *
	 * @param itemId the item id
	 * @return the root shared folder
	 * @throws Exception the exception
	 */
	public FolderItem getRootSharedFolder(String itemId) throws Exception {
		setContextProviders(scope, authorizationToken);
		return getRootSharedFolder(shcClient.open(itemId).asItem());

	}


	/**
	 * Checks if is item shared.
	 *
	 * @param itemId the item id
	 * @return true, if is item shared
	 * @throws Exception the exception
	 */
	public boolean isItemShared(String itemId) throws Exception {
		setContextProviders(scope, authorizationToken);
		return shcClient.open(itemId).asItem().get().isShared();

	}

	/**
	 * Gets the root shared folder.
	 *
	 * @param itemContainer the item container
	 * @return the root shared folder
	 * @throws Exception the exception
	 */
	public FolderItem getRootSharedFolder(ItemContainer<Item> itemContainer) throws Exception {

		FolderContainer rootSharedFolder = null;
		Item item = itemContainer.get();
		if(item.isShared()){
			rootSharedFolder = itemContainer.getRootSharedFolder();
		}else
			throw new Exception("The item with id: "+item.getId() +" is not shared");

		Validate.notNull(rootSharedFolder, "The root shared folder with children id "+item.getId()+" does not exist");
		return rootSharedFolder.get();
	}

	/**
	 * Creates the folder.
	 *
	 * @param parentId the parent id
	 * @param folderName the folder name
	 * @param folderDescription the folder description
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item createFolder(String parentId, String folderName, String folderDescription) throws Exception {
		setContextProviders(scope, authorizationToken);
		FolderContainer folderContainer = shcClient.open(parentId).asFolder().newFolder(folderName, folderDescription);
		return getItem(folderContainer.get().getId(), false, true);
	}


	/**
	 * Gets the VRE folders id.
	 *
	 * @return the VRE folders id
	 */
	public String getVREFoldersId() {

		setContextProviders(scope, authorizationToken);
		String toReturn = "";
		try {
			WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
			try {
				List<? extends Item> list = wsclient.getVreFolders(ACCOUNTING_HL_NODE_NAME);
				toReturn =list.iterator().next().getParentId();
			} catch (Exception e) {
				logger.info("This user has no VRE Folders", e);
				return null;
			}
		}catch (Exception e) {
			logger.error("Get VRE Folders Id ",e);
			//e.printStackTrace();
		}
		return toReturn;
	}




	/**
	 * Gets the user acl for folder id.
	 *
	 * @param infrastructureName the infrastructure name
	 * @param userName the user name
	 * @param folderId the folder id
	 * @return the user acl for folder id
	 * @throws Exception the exception
	 */
	public String getUserACLForFolderId(String infrastructureName, String userName, String folderId) throws Exception {

		setContextProviders(scope, authorizationToken);
		Item theFolder = getItem(folderId, false, true);
		if (!theFolder.isShared()) {
			return "OWNER";
		} else {
			ItemManagerClient client = AbstractPlugin.item().build();
			List<ACL> acls = client.getACL(folderId);
			SharedFolder sharedFolder = (SharedFolder) theFolder;

			boolean found = false; //this is needed because in case o VRE Foder the permission is assigned to the group and not to the user.
			for (ACL acl : acls) {
				if (acl.getPricipal().compareTo(userName) == 0) {
					found = true;
					return acl.getAccessTypes().get(0).toString();
				}
			}
			if (!found && sharedFolder.isVreFolder()) {
				for (ACL acl : acls) {
					if (acl.getPricipal().startsWith(infrastructureName));
						return acl.getAccessTypes().get(0).toString();
				}
			}
		}
		return "UNDEFINED";
	}


	/**
	 * Gets the item children count.
	 *
	 * @param itemId the item id
	 * @return the item children count
	 */
	public int getItemChildrenCount(String itemId) {

		setContextProviders(scope, authorizationToken);
		ItemManagerClient client = AbstractPlugin.item().build();
		return client.childrenCount(itemId);
	}

	/**
	 * Upload file.
	 *
	 * @param folderId the folder id
	 * @param is the is
	 * @param fileName the file name
	 * @param fileDescription the file description
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item uploadFile(String folderId, InputStream is, String fileName, String fileDescription) throws Exception{

		setContextProviders(scope, authorizationToken);
		FileContainer fileCont = shcClient.open(folderId).asFolder().uploadFile(is, fileName, fileDescription);
		return fileCont.get();
	}



	/**
	 * Download file.
	 *
	 * @param itemId the item id
	 * @param versionName the version name. If is null or empty returns the latest version of file
	 * @param nodeIdsToExclude the node ids to exclude
	 * @return the stream descriptor
	 * @throws Exception the exception
	 */
	public StreamDescriptor downloadFile(String itemId, String versionName, String... nodeIdsToExclude) throws Exception{

		setContextProviders(scope, authorizationToken);
		StreamDescriptor streamDesc;
		if(versionName!=null && !versionName.isEmpty()){
			streamDesc = shcClient.open(itemId).asFile().downloadSpecificVersion(versionName);
		}else{
			streamDesc = shcClient.open(itemId).asFile().download(nodeIdsToExclude);
		}
		return new StreamDescriptor(streamDesc.getStream(), streamDesc.getFileName());

	}


	/**
	 * Download folder.
	 *
	 * @param folderId the folder id
	 * @param nodeIdsToExclude the node ids to exclude
	 * @return the stream descriptor
	 * @throws Exception the exception
	 */
	public StreamDescriptor downloadFolder(String folderId, String nodeIdsToExclude) throws Exception{

		setContextProviders(scope, authorizationToken);
		StreamDescriptor streamDesc = shcClient.open(folderId).asFolder().download(nodeIdsToExclude);
		return new StreamDescriptor(streamDesc.getStream(), streamDesc.getFileName());

	}



	/**
	 * Upload archive.
	 *
	 * @param folderId the folder id
	 * @param is the is
	 * @param extractionFolderName the extraction folder name
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item uploadArchive(String folderId, InputStream is, String extractionFolderName) throws Exception{

		setContextProviders(scope, authorizationToken);
		FolderContainer folderCont = shcClient.open(folderId).asFolder().uploadArchive(is, extractionFolderName);
		return folderCont.get();
	}



	/**
	 * Gets the shared folder members.
	 *
	 * @param folderId the folder id
	 * @return the shared folder members
	 * @throws Exception the exception
	 */
	public List<String> getSharedFolderMembers(String folderId) throws Exception {

		setContextProviders(scope, authorizationToken);
		Item item = getItem(folderId, false, true);
		if(item instanceof SharedFolder){
			return ObjectMapper.toListLogins((SharedFolder)item);
		}else
			throw new Exception("The item with "+folderId+ " is not a Shared Folder");

	}


	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @param folderId the folder id
	 * @return the list<? extends item>
	 * @throws Exception the exception
	 */
	public List<? extends Item> findByName(String name, String folderId) throws Exception {

		Item item;
		try {
			item = getItem(folderId, false, true);
		}catch (Exception e) {
			logger.error("Error during get item with id: "+folderId,e);
			throw new ItemNotFoundException(e.getMessage());
		}

		if(item instanceof FolderItem || item instanceof SharedFolder || item instanceof VreFolder){
			return shcClient.open(folderId).asFolder().findByName(name).withContent().getItems();
		}else
			throw new Exception("The input folder id is not a folder");
	}



	/**
	 * Delete item by id.
	 *
	 * @param itemId the item id
	 * @throws Exception the exception
	 */
	public void deleteItemById(String itemId) throws Exception{

		Validate.notNull(itemId, "Bad request of deleteItemById, the itemId is null");
		setContextProviders(scope, authorizationToken);
		shcClient.open(itemId).asItem().delete();

	}



	/**
	 * Open trash.
	 *
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item openTrash() throws Exception{

		setContextProviders(scope, authorizationToken);
		return shcClient.openTrash().get();

	}



	/**
	 * Empty trash.
	 *
	 * @throws Exception the exception
	 */
	public void emptyTrash() throws Exception{

		setContextProviders(scope, authorizationToken);
		shcClient.emptyTrash();

	}


	/**
	 * Restore thrash item.
	 *
	 * @param itemId the item id
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item restoreThrashItem(String itemId) throws Exception{

		Validate.notNull(itemId, "Bad request of restoreThrashItem, the itemId is null");
		setContextProviders(scope, authorizationToken);
		GenericItemContainer container = shcClient.restoreThrashItem(itemId);

		if(container!=null){
			Item item = container.get();
			if(item!=null){
				return item;
			}else
				throw new Exception("Restoring failed, FolderItem is null");
		}else
			throw new Exception("Restoring failed, contanier is null");

	}


	/**
	 * Move item.
	 *
	 * @param itemId the item id
	 * @param destFolderContainer the dest folder container
	 * @return the abstract file item
	 * @throws Exception the exception
	 */
	public Item moveItem(String itemId, FolderContainer destFolderContainer) throws Exception{

		Validate.notNull(itemId, "Bad request of moveItem, the itemId is null");
		Validate.notNull(destFolderContainer, "Bad request of moveItem, the itemId is null");
		setContextProviders(scope, authorizationToken);
		shcClient.open(itemId).asItem().move(destFolderContainer);
		return shcClient.open(itemId).asItem().get();

	}


	/**
	 * Copy item.
	 *
	 * @param fileItemId the copy item id
	 * @param destFolderContainer the dest folder container
	 * @param newFileName the new file name
	 * @return the abstract file item
	 * @throws Exception the exception
	 */
	public AbstractFileItem copyFileItem(String fileItemId, FolderContainer destFolderContainer, String newFileName) throws Exception{

		Validate.notNull(fileItemId, "Bad request of copyFileItem, the fileItemId is null");
		Validate.notNull(destFolderContainer, "Bad request of copyFileItem, the destFolderContainer is null");
		setContextProviders(scope, authorizationToken);
		FileContainer copyingItem = shcClient.open(fileItemId).asFile();
		String newName = newFileName!=null && !newFileName.isEmpty()?newFileName:"Copy of "+copyingItem.get().getName();
		FileContainer newItem = copyingItem.copy(destFolderContainer, newName);
		return newItem.get();
	}


	/**
	 * Rename item.
	 *
	 * @param itemId the item id
	 * @param newName the new name
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item renameItem(String itemId, String newName) throws Exception{

		Validate.notNull(itemId, "Bad request of renameItem, the itemId is null");
		setContextProviders(scope, authorizationToken);
		shcClient.open(itemId).asItem().rename(newName);
		return shcClient.open(itemId).asItem().get();

	}


	/**
	 * Gets the file public link.
	 *
	 * @param fileItemId the file item id
	 * @return the file public link
	 * @throws Exception the exception
	 */
	public URL getPublicLinkForFile(String fileItemId) throws Exception{

		Validate.notNull(fileItemId, "Bad request of getPublicLinkForFile, the fileItemId is null");
		setContextProviders(scope, authorizationToken);
		return shcClient.open(fileItemId).asFile().getPublicLink();

	}


	/**
	 * Gets the list versions.
	 *
	 * @param fileItemId the file item id
	 * @return the list versions
	 * @throws Exception the exception
	 */
	public List<Version> getListVersions(String fileItemId) throws Exception{

		Validate.notNull(fileItemId, "Bad request of getListVersions, the fileItemId is null");

		return shcClient.open(fileItemId).asFile().getVersions();

	}

	/**
	 * Gets the image content.
	 *
	 * @param itemId the item id
	 * @return the image content
	 * @throws Exception the exception
	 */
	public ImageContent getImageContent(String itemId) throws Exception{

		Validate.notNull(itemId, "Bad request of getThumbnailData, the itemId is null");
		setContextProviders(scope, authorizationToken);
		ItemContainer<Item> itemCont = shcClient.open(itemId).asItem();
		Item item = itemCont.get();
		if(item instanceof org.gcube.common.storagehub.model.items.ImageFile){

    		org.gcube.common.storagehub.model.items.ImageFile imgFI = (org.gcube.common.storagehub.model.items.ImageFile) item; //??
    		return imgFI.getContent();
		}else
			throw new Exception("Thumbnail Data is not available for type: "+item.getClass().getSimpleName());
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("StorageHubClientService [scope=");
		builder.append(scope);
		builder.append(", authorizationToken=");
		builder.append(authorizationToken.substring(0, authorizationToken.length()-5)+"XXXXX");
		builder.append(", itemManagerClient=");
		builder.append(shcClient);
		builder.append("]");
		return builder.toString();
	}



}

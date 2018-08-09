package org.gcube.common.storagehubwrapper.server;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.VreFolder;
import org.gcube.common.storagehubwrapper.server.converter.ObjectMapper;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.ItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class StorageHubClientService.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 20, 2018
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
//		WorkspaceManagerClient client = AbstractPlugin.workspace().build();
//		Item itemRoot = client.getWorkspace(ACCOUNTING_HL_NODE_NAME);
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
	 * @return the children
	 */
	public List<? extends Item> getChildren(String id){
		setContextProviders(scope, authorizationToken);
		return shcClient.open(id).asFolder().list().withContent().getItems();
	}


	/**
	 * Gets the item.
	 *
	 * @param itemId the item id
	 * @return the item
	 * @throws Exception the exception
	 */
	public Item getItem(String itemId) throws Exception{
		setContextProviders(scope, authorizationToken);
		ItemContainer<Item> itemCont = shcClient.open(itemId).asItem();//TODO
		return itemCont.get();
	}


	/**
	 * Gets the parents.
	 *
	 * @param itemId the item id
	 * @return the parents
	 */
	public List<? extends Item> getParents(String itemId) {
		setContextProviders(scope, authorizationToken);
		List<? extends Item> toReturn = shcClient.open(itemId).asItem().getAnchestors();
		return toReturn;
	}

	//TODO MAP OF SHARED ROOT ID


	/**
	 * Gets the id shared folder.
	 *
	 * @param itemId the item id
	 * @return the id shared folder
	 * @throws Exception
	 */
	public String getIdSharedFolder(String itemId) throws Exception {
		setContextProviders(scope, authorizationToken);
		return getRootSharedFolder(itemId).getId();
	}


	/**
	 * Gets the root shared folder.
	 *
	 * @param itemId the item id
	 * @return the root shared folder
	 * @throws Exception
	 */
	public FolderItem getRootSharedFolder(String itemId) throws Exception {
		setContextProviders(scope, authorizationToken);

		ItemContainer<Item> item = shcClient.open(itemId).asItem();
		FolderContainer rootSharedFolder = null;
		if(item.get().isShared()){
			rootSharedFolder = item.getRootSharedFolder();
		}else
			throw new Exception("The item with id: "+itemId +" is not shared");

		Validate.notNull(rootSharedFolder, "The root shared folder with id "+itemId+" does not exist");
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
		return getItem(folderContainer.get().getId());
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
		Item theFolder = getItem(folderId);
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

		try {
			FileContainer fileCont = shcClient.open(folderId).asFolder().uploadFile(is, fileName, fileDescription);
			return fileCont.get();
		} catch (Exception e) {
			logger.error("Error during uploading the file: "+fileName+" in the folderId: "+folderId, e);
			throw new Exception("Error during uploading the file: "+fileName+". Try again");
		}
	}


	/**
	 * Gets the shared folder members.
	 *
	 * @param folderId the folder id
	 * @return the shared folder members
	 * @throws Exception the exception
	 */
	public List<String> getSharedFolderMembers(String folderId) throws Exception {
		Item item;
		try {
			item = getItem(folderId);
			if(item instanceof SharedFolder){
				return ObjectMapper.toListLogins((SharedFolder)item);
			}else
				throw new Exception("The item with "+folderId+ " is not a Shared Folder");

		}catch (Exception e) {
			logger.error("Error during get item with id: "+folderId,e);
			throw new ItemNotFoundException(e.getMessage());
		}
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
			item = getItem(folderId);
		}catch (Exception e) {
			logger.error("Error during get item with id: "+folderId,e);
			throw new ItemNotFoundException(e.getMessage());
		}

		if(item instanceof FolderItem || item instanceof SharedFolder || item instanceof VreFolder){
			return shcClient.open(folderId).asFolder().findByName(name).getItems();
		}else
			throw new Exception("The input folder id is not a folder");
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

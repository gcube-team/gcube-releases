package org.gcube.storagehub;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.common.storagehub.client.dsl.ContainerType;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.ListResolver;
import org.gcube.common.storagehub.client.dsl.ListResolverTyped;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageHubManagement {
	
	private static final Logger logger = LoggerFactory.getLogger(StorageHubManagement.class);
	
	protected MetadataMatcher metadataMatcher;
	
	protected final StorageHubClient storageHubClient;
	
	protected FileContainer createdFile;
	protected String mimeType;
	
	public StorageHubManagement() {
		storageHubClient = new StorageHubClient();
	}
	
	public void setCheckMetadata(MetadataMatcher checkMetadata) {
		this.metadataMatcher = checkMetadata;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public FileContainer getCreatedFile() {
		return createdFile;
	}
	
	protected void recursiveList(FolderContainer folder, int level) {
		ListResolverTyped listResolverTyped = folder.list();
		List<ItemContainer<? extends Item>> containers = listResolverTyped.includeHidden().getContainers();
		for(ItemContainer<? extends Item> itemContainer : containers) {
			Item item = itemContainer.get();
			String name = item.getName();
			ContainerType containerType = itemContainer.getType();
			StringWriter indent = new StringWriter(level + 1);
			for(int i = 0; i < level + 1; i++) {
				indent.append('-');
			}
			logger.debug("{} {} {} (ID:{}) {}", indent.toString(), containerType, name, itemContainer.getId(), item.isHidden() ? " (hidden)" : "");
			switch(containerType) {
				case FOLDER:
					FolderContainer folderContainer = (FolderContainer) itemContainer;
					//if(item.getName().compareTo("553095a0-a14a-4e41-b014-2e6f3a1aeac7")!=0)
					recursiveList(folderContainer, level + 1);
					break;
				
				case FILE:
					break;
				
				case GENERIC_ITEM:
					break;
				
				default:
					break;
			}
		}
	}
	
	protected FolderContainer getWorkspaceRoot() {
		try {
			return storageHubClient.getWSRoot();
		} catch(Exception e) {
			String username = ContextUtility.getUsername();
			logger.info("Unable to obtain the Workspace Root for {}. Going to create it.", username);
			try {
				HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
				HomeManager manager = factory.getHomeManager();
				User user = manager.createUser(username);
				@SuppressWarnings("deprecation")
				Home home = manager.getHome(user);
				Workspace ws = home.getWorkspace();
				ws.getRoot();
				return storageHubClient.getWSRoot();
			} catch(Exception ex) {
				logger.info("Unable to create the Workspace Root for {}.", username);
				throw e;
			}
		}
	}
	
	protected FolderContainer getOrCreateFolder(FolderContainer parent, String name, String description, boolean hidden)
			throws Exception {
		FolderContainer destinationFolder = null;
		ListResolverTyped listResolverTyped = parent.list();
		List<ItemContainer<? extends Item>> containers = listResolverTyped.includeHidden().getContainers();
		for(ItemContainer<? extends Item> itemContainer : containers) {
			if(itemContainer instanceof FolderContainer) {
				if(itemContainer.get().getName().compareTo(name) == 0) {
					destinationFolder = (FolderContainer) itemContainer;
				}
			}
		}
		if(destinationFolder == null) {
			if(hidden) {
				destinationFolder = parent.newHiddenFolder(name, description);
			} else {
				destinationFolder = parent.newFolder(name, description);
			}
		}
		return destinationFolder;
	}
	
	protected FolderContainer getContextFolder() throws Exception {
		FolderContainer destinationFolder = getWorkspaceRoot();
		String currentContext = ContextUtility.getCurrentContext();
		ScopeBean scopeBean = new ScopeBean(currentContext);
		switch(scopeBean.type()) {
			case INFRASTRUCTURE:
			case VO:
				String folderName = currentContext.replaceFirst("/", "").replace("/", "_");
				destinationFolder = getOrCreateFolder(destinationFolder, folderName, "", false);
				break;
			
			case VRE:
				String username = ContextUtility.getUsername();
				try {
					destinationFolder = storageHubClient.openVREFolder();
				}catch (Exception e) {
					try {
						
						HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
						UserManager userManager = factory.getUserManager();
						userManager.associateUserToGroup(currentContext, username);	
						HomeManager manager = factory.getHomeManager();
						@SuppressWarnings("deprecation")
						Home home = manager.getHome(username);
						Workspace ws = home.getWorkspace();
						WorkspaceSharedFolder vreFolder=  ws.getVREFolderByScope(currentContext);
						String id = vreFolder.getId();
						// destinationFolder = storageHubClient.openVREFolder();
						OpenResolver openResolver = storageHubClient.open(id);
						destinationFolder = openResolver.asFolder();
					}catch (Exception ex) {
						throw e;
					}					
				}
				break;
			
			default:
				break;
		}
		
		return destinationFolder;
	}
	
	public FolderContainer getApplicationFolder() throws Exception {
		FolderContainer destinationFolder = getContextFolder();
		String currentContext = ContextUtility.getCurrentContext();
		ScopeBean scopeBean = new ScopeBean(currentContext);
		if(scopeBean.is(Type.VRE)) {
			String username = ContextUtility.getUsername();
			destinationFolder =  getOrCreateFolder(destinationFolder, username, "Folder Created for user/application", true);
		}
		return destinationFolder;
	}
	
	public FolderContainer getDestinationFolder(String mimeType) throws Exception {
		FolderContainer destinationFolder = getApplicationFolder();
		String[] splittedMimeType = mimeType.split("/");
		for(String name : splittedMimeType) {
			destinationFolder = getOrCreateFolder(destinationFolder, name, "Folder Created using mimetype", false);
		}
	return destinationFolder;
	}
	
	protected boolean isPersistedFile(FileContainer fileContainer, String filename) {
		// Checking if the file is already a persisted file of the workspace
		if(fileContainer.get().getName().startsWith(filename)) {
			if(metadataMatcher != null) {
				Metadata metadata = fileContainer.get().getMetadata();
				return metadataMatcher.check(metadata);
			} else {
				return true;
			}
		}
		return false;
	}
	
	protected void tree(FolderContainer folderContainer) throws Exception {
		logger.debug("{} (ID:{})", folderContainer.get().getName(), folderContainer.getId());
		recursiveList(folderContainer, 0);
	}
	
	public URL persistFile(InputStream inputStream, String fileName, String mimeType, Metadata metadata)
			throws Exception {
		this.mimeType = mimeType;
		FolderContainer destinationFolder = getDestinationFolder(mimeType);
		createdFile = destinationFolder.uploadFile(inputStream, fileName,
				"This file has been created to ensure persistence");
		
		if(metadata != null) {
			createdFile.setMetadata(metadata);
		}
		
		URL finalURL = createdFile.getPublicLink();
		logger.debug("File persistence has been ensured. The file is available at {}", finalURL);
		return finalURL;
	}
	
	public void removePersistedFile(String filename, String mimeType) throws Exception {
		FolderContainer destinationFolder = getDestinationFolder(mimeType);
		ListResolver listResolver = destinationFolder.findByName(filename);
		List<ItemContainer<? extends Item>> itemContainers = listResolver.getContainers();
		for(ItemContainer<? extends Item> itemContainer : itemContainers) {
			if(itemContainer.getType()==ContainerType.FILE) {
				if(isPersistedFile((FileContainer) itemContainer, filename)) {
					itemContainer.delete();
				}
			}
		}
	}
	
}

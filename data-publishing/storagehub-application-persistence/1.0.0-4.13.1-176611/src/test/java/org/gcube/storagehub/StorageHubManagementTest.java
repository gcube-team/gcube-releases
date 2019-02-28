package org.gcube.storagehub;

import java.util.List;
import java.util.Map;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.service.Version;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageHubManagementTest extends ContextTest {
	
	private static final Logger logger = LoggerFactory.getLogger(StorageHubManagementTest.class);
	
	@Test
	public void testHL() throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(ContextTest.GCUBE_PRE_PROD_PREVRE_APP_TOKEN);
		applicationMode.start();
		String username = ContextUtility.getUsername();
		HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
		HomeManager manager = factory.getHomeManager();
		User user = manager.createUser(username);
		@SuppressWarnings("deprecation")
		Home home = manager.getHome(user);
		Workspace ws = home.getWorkspace();
		WorkspaceFolder workspaceFolder = ws.getRoot();
		workspaceFolder = ws.getVREFolderByScope(ContextUtility.getCurrentContext());
		// logger.debug("VRE Folder ID : {} - Owner: {}", workspaceFolder.getId(), workspaceFolder.getOwner().getPortalLogin());
		List<WorkspaceItem> workspaceItems = workspaceFolder.getChildren(true);
		for(WorkspaceItem workspaceItem : workspaceItems) {
			logger.debug("{} {}{} ID:{}", workspaceFolder.getType(), workspaceItem.getName(), workspaceItem.isHidden()? " (hidden)":"", workspaceItem.getId());
		}
	}
	
	@Test
	public void myTest() throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(ContextTest.GCUBE_PRE_PROD_PREVRE_APP_TOKEN);
		applicationMode.start();
		StorageHubManagement storageHubManagement = new StorageHubManagement();
		FolderContainer contextFolder = storageHubManagement.getContextFolder();
		logger.debug("Context Folder ID : {} - Name : {}", contextFolder.getId(), contextFolder.get().getName());
	}
	
	@Test
	public void test() throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(ContextTest.GCUBE_PRE_PROD_PREVRE_APP_TOKEN);
		applicationMode.start();
		StorageHubManagement storageHubManagement = new StorageHubManagement();
		OpenResolver openResolver = storageHubManagement.storageHubClient.open("71394bdc-296f-46d4-ab7b-ecc9abc36bdd");
		openResolver.asItem().delete();
		/*
		openResolver = storageHubManagement.storageHubClient.open("656cd713-bd79-4659-abd6-9f1baaedb5bc");
		openResolver.asItem().delete();
		openResolver = storageHubManagement.storageHubClient.open("bd44d81e-0e2f-4527-b634-2e26e8908f36");
		openResolver.asItem().delete();
		*/
		applicationMode.end();
	}
	
	@Test
	public void listFolders() throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(ContextTest.GCUBE_PRE_PROD_PREVRE_APP_TOKEN);
		applicationMode.start();
		StorageHubManagement storageHubManagement = new StorageHubManagement();
		FolderContainer root = storageHubManagement.getWorkspaceRoot();
		FolderContainer contextFolder = storageHubManagement.getContextFolder();
		// FolderContainer dstFolder = 
		storageHubManagement.getDestinationFolder("application/pdf");
		storageHubManagement.tree(root);
		storageHubManagement.tree(contextFolder);
		// storageHubManagement.tree(dstFolder);
		applicationMode.end();
	}
	
	@Test
	public void getFileInfo() throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(ContextTest.GCUBE_PRE_PROD_PREVRE_APP_TOKEN);
		applicationMode.start();
		StorageHubManagement storageHubManagement = new StorageHubManagement();
		String id = "71394bdc-296f-46d4-ab7b-ecc9abc36bdd";
		OpenResolver openResolver = storageHubManagement.storageHubClient.open(id);
		FileContainer fileContainer = (FileContainer) openResolver.asFile();
		logger.debug("StorageHub ID {} - File Name {}", id, fileContainer.get().getName());
		
		/*
		ListResolver listResolver = fileContainer.getAnchestors();
		List<ItemContainer<? extends Item>> itemContainers = listResolver.getContainers();
		for(ItemContainer<? extends Item> itemContainer : itemContainers) {
			logger.debug("{}", itemContainer.get().getName());
		}
		*/
		
		Metadata metadata = fileContainer.get().getMetadata();
		Map<String,Object> map = metadata.getMap();
		logger.debug("{}", map);
		
		List<Version> versions = fileContainer.getVersions();
		for(Version version : versions){
			logger.debug("Version {} {}", version.getId(), version.getName());
		}
		applicationMode.end();
	}
	
}

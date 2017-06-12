package org.gcube.portlets.user.homelibrary.jcr.test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MyTests {
//	private static final String MY_TEST_FOLDER = "/home/valentina/testFolder";
	//	private static final String ITEM_ID = null;
	JCRWorkspace ws;
	WorkspaceFolder folder;

	@Before
	public void getMyWorkspace() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException {
		String scope = "/d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject";

		ScopeProvider.instance.set(scope);

		//		ScopeProvider.instance.set("/gcube");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		//		String user = "valentina.marioli";
		String user = "valentina.marioli";
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

	}

	@Ignore
	public void check() throws InternalErrorException, ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, ItemAlreadyExistException, WorkspaceFolderNotFoundException {
		String itemId = "ab70eb95-bf8f-4a57-b411-d7c9e5f7ecfc";
		WorkspaceItem item = ws.getItem(itemId);
		System.out.println(item.getName());
		System.out.println(item.getType().toString());
		System.out.println(item.getParent().getId());
	}


	@Ignore
	public void run() throws InternalErrorException, ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, ItemAlreadyExistException, WorkspaceFolderNotFoundException {
		WorkspaceItem item = ws.getItemByPath("/Workspace/AA");
		WorkspaceItem destination = ws.getItemByPath("/Workspace/B");
		printRemotePath(item);
		ws.moveItem(item.getId(), destination.getId());
		printRemotePath(item);
	}


	public void printRemotePath(WorkspaceItem item) throws InternalErrorException, ItemNotFoundException {
		System.out.println("******* " + item.getRemotePath());
		if (item.isFolder()){
			List<? extends WorkspaceItem> children = item.getChildren();
			for (WorkspaceItem child: children)
				printRemotePath(child);			
		}
	}


	@Test
	public void smartFolders() throws InternalErrorException {
		List<WorkspaceSmartFolder> smartFolders = ws.getAllSmartFolders();
		System.out.println(smartFolders.size() + " SmartFolders found");
		for (WorkspaceSmartFolder smart: smartFolders){
			System.out.println("\n");
			System.out.println("getId: " + smart.getId());
			System.out.println("getDescription: " + smart.getDescription());
			System.out.println("getName: " + smart.getName());
			System.out.println("getCreationTime: " + smart.getCreationTime().getTime());
			System.out.println("getLastAction: " + smart.getLastAction().toString());
			System.out.println("getOwner: " + smart.getOwner().getPortalLogin());
			System.out.println("getType: " + smart.getType());

			//			List<? extends SearchItem> list = smart.getSearchItems();
			//			for(SearchItem item: list){
			//				System.out.println("\n");
			//				System.out.println("getId: " + item.getId());
			//				System.out.println("getName: " + item.getName());
			//				System.out.println("getType: " + item.getType());
			//			}
		}
	}


	@Ignore
	public void VREFolder() throws InternalErrorException, ItemNotFoundException {
		WorkspaceFolder vreFolders = ws.getMySpecialFolders();
		List<WorkspaceItem> list = vreFolders.getChildren();
		System.out.println(list.size() + " VREFolders found");
		for(WorkspaceItem item: list){	
			System.out.println("\n");
			System.out.println("getId: " + item.getId());
			System.out.println("getDescription: " + item.getDescription());
			System.out.println("getName: " + item.getName());
			System.out.println("getCreationTime: " + item.getCreationTime().getTime());
			System.out.println("getLastAction: " + item.getLastAction().toString());
			System.out.println("getOwner: " + item.getOwner().getPortalLogin());
			System.out.println("getType: " + item.getType());
		}
	}


	@Test
	public void upload() throws InternalErrorException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException, IOException {

		//		System.out.println(ws.getStorage().getRemoteFileSize("/Home/valentina.marioli/Workspace/B/ipy_fixed42p_2.0l_IPY20070823.tar"));


		folder = ws.createFolder("Test-"+ UUID.randomUUID().toString(), "my test folder", ws.getRoot().getId());

		//		System.out.println(folder.getPath());
		//		File directory = new File(MY_TEST_FOLDER);
		//
		//		// get all the files from a directory
		//		File[] fList = directory.listFiles();
		//		System.out.println("Upload " + fList.length + " files");
		//		for (File file : fList) {
		//			if (file.isFile()) {
		//				System.out.println("Local file: " + file.getAbsolutePath());
		//				FolderItem item = WorkspaceUtil.createExternalFile(folder, file.getName(), "my file", null, new FileInputStream(file));
		//				System.out.println("\n");
		//				System.out.println("getId: " + item.getId());
		//				System.out.println("getDescription: " + item.getDescription());
		//				System.out.println("getName: " + item.getName());
		//				System.out.println("getCreationTime: " + item.getCreationTime().getTime());
		//				System.out.println("getLastAction: " + item.getLastAction().toString());
		//				System.out.println("getOwner: " + item.getOwner().getPortalLogin());
		//				System.out.println("getType: " + item.getType());
		//				System.out.println("getMimeType: " + item.getMimeType());
		//			} 
		//		}
	}


	@Ignore
	public void move() throws InternalErrorException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException, IOException {


		WorkspaceFolder subfolder = folder.createFolder("subfolder", "test subfolder");
		List<WorkspaceItem> children = folder.getChildren();

		for (WorkspaceItem item : children) {

			System.out.println("\n");
			System.out.println("getId: " + item.getId());
			System.out.println("getDescription: " + item.getDescription());
			System.out.println("getName: " + item.getName());
			System.out.println("getCreationTime: " + item.getCreationTime().getTime());
			System.out.println("getLastAction: " + item.getLastAction().toString());
			System.out.println("getOwner: " + item.getOwner().getPortalLogin());
			System.out.println("getType: " + item.getType());

			subfolder.move(subfolder);

		}


	}

}
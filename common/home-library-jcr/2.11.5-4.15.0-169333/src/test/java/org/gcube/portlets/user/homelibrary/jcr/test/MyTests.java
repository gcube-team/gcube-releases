package org.gcube.portlets.user.homelibrary.jcr.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

public class MyTests {
	private static final String MY_TEST_FOLDER = "/home/valentina/testFolder";
	private static final String FOLDER_NAME = "mytestfolder2";
	private static final String SUB_FOLDER_NAME = "mytestsubfolder2";
	private static String SUBFOLDER_TO_SHARE_ID = "62486420-fb21-4ebe-a16c-60ec880b0a39";
	//	private static final String ITEM_ID = null;
	Workspace ws;
	WorkspaceFolder folder;

	//@Before
	public void before() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException{

		ScopeProvider.instance.set("/gcube");
		//		SecurityTokenProvider.instance.set("");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		//		String user = "valentina.marioli";
		String user = "costantino.perciante";
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

		System.out.println("Got it!");

	}
	//@Test
	public void getMyWorkspace() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException {
		//		String scope = "/d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject";
		//
		//		ScopeProvider.instance.set(scope);

		try{

			System.out.println("start get root");
			WorkspaceItem theRoot = ws.getRoot();

			System.out.println("Path of the root is " + theRoot.getPath());

			System.out.println("\n\n CREATING folder: "+FOLDER_NAME + "\n\n");

			WorkspaceFolder myCourseFolder = ws.createFolder(FOLDER_NAME, "", theRoot.getId());

			System.out.println("\n\n FOLDER id: "+myCourseFolder.getId() +"\n\n");

			System.out.println("\n\n CREATING sub folder: "+SUB_FOLDER_NAME+"\n\n");

			WorkspaceFolder subFolderMyCourse = ws.createFolder(SUB_FOLDER_NAME, "", myCourseFolder.getId());

			System.out.println("\n\n SUB_FOLDER id: "+subFolderMyCourse.getId() +"\n\n");
			SUBFOLDER_TO_SHARE_ID = subFolderMyCourse.getId();



		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//@Test
	public void shareAndSetACL(){
		try{
			System.out.println("*****************  SHARING");
			Thread.sleep(2000);
			List<String> users = new ArrayList<String>();
			users.add("andrea.rossi");
			users.add("francesco.mangiacrapa");
			users.add("massimiliano.assante");
			WorkspaceSharedFolder subShareFolder = ws.share(users, SUBFOLDER_TO_SHARE_ID);
			System.out.println("SHARED!!!!!!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//@Test
	public void setACL(){

		try{
			System.out.println("*****************  SETTING ACL");
			List<String> users = new ArrayList<String>();
			users.add("andrea.rossi");
			users.add("francesco.mangiacrapa");
			users.add("massimiliano.assante");
			WorkspaceSharedFolder subShareFolder = (WorkspaceSharedFolder)ws.getItem(SUBFOLDER_TO_SHARE_ID);
			subShareFolder.setACL(users, ACLType.READ_ONLY);
			System.out.println("\n\n SHARED SUB_FOLDER id: "+subShareFolder.getId() +"\n\n");
			// get back the shared folder
			System.out.println("SET ACL!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//	@Test
	public void getShared(){

		try {
			WorkspaceItem item = ws.getItemByPath("/Workspace/mytestfolder/mytestsubfolder");
			System.out.println("Item is " + item.getName() + " and is it shared? " + item.isShared());

			// if shared, get it
			if(item.isShared()){

				System.out.println("Shared id is " + item.getIdSharedFolder());

				JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder)ws.getItem(item.getIdSharedFolder());
				System.out.println("Shared folder is " + shared.getUsers());

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	//@Test
	public void check() throws InternalErrorException, ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, ItemAlreadyExistException, WorkspaceFolderNotFoundException {
		List<WorkspaceItem> children = ws.getRoot().getChildren();
		for (WorkspaceItem child: children){
			String itemId = child.getId();
			WorkspaceItem item = ws.getItem(itemId);
			System.out.println(item.getName());
			System.out.println(item.getType().toString());
			System.out.println(item.getParent().getId());
		}
	}


	//@Test
	public void run() throws InternalErrorException, ItemNotFoundException, WrongDestinationException, InsufficientPrivilegesException, ItemAlreadyExistException, WorkspaceFolderNotFoundException {
		WorkspaceFolder item = ws.createFolder("A-"+ UUID.randomUUID().toString(), "my test folder", ws.getRoot().getId());
		WorkspaceFolder destination = ws.createFolder("B-"+ UUID.randomUUID().toString(), "my test folder", ws.getRoot().getId());

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


	//@Test
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


	//@Test
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


	//@Ignore
	public void upload() throws InternalErrorException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException, IOException {

		//		System.out.println(ws.getStorage().getRemoteFileSize("/Home/valentina.marioli/Workspace/B/ipy_fixed42p_2.0l_IPY20070823.tar"));


		//		folder = ws.createFolder("Test-"+ UUID.randomUUID().toString(), "my test folder", ws.getRoot().getId());

		//				System.out.println(folder.getPath());
		//				File directory = new File(MY_TEST_FOLDER);
		//		
		//				// get all the files from a directory
		//				File[] fList = directory.listFiles();
		//				System.out.println("Upload " + fList.length + " files");
		//				for (File file : fList) {
		//					if (file.isFile()) {
		//						System.out.println("Local file: " + file.getAbsolutePath());
		//						FolderItem item = WorkspaceUtil.createExternalFile(folder, file.getName(), "my file", null, new FileInputStream(file));
		//						System.out.println("\n");
		//						System.out.println("getId: " + item.getId());
		//						System.out.println("getDescription: " + item.getDescription());
		//						System.out.println("getName: " + item.getName());
		//						System.out.println("getCreationTime: " + item.getCreationTime().getTime());
		//						System.out.println("getLastAction: " + item.getLastAction().toString());
		//						System.out.println("getOwner: " + item.getOwner().getPortalLogin());
		//						System.out.println("getType: " + item.getType());
		//						System.out.println("getMimeType: " + item.getMimeType());
		//					} 
		//				}
	}


	//@Test
	public void move() throws InternalErrorException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException, IOException {

		folder = ws.createFolder("Test-"+ UUID.randomUUID().toString(), "my test folder", ws.getRoot().getId());
		WorkspaceFolder subfolder = ws.createFolder("subfolder", "test subfolder", ws.getRoot().getId());
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

			subfolder.move(folder);

		}


	}

	//@Test
	public void createSharedAndCopyFolder() throws InternalErrorException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException, WorkspaceFolderNotFoundException, IOException {

		WorkspaceSharedFolder myFolder = ws.createSharedFolder("Test-"+ UUID.randomUUID().toString(), "my test folder", Arrays.asList("andrea.rossi"),ws.getRoot().getId());

		WorkspaceSharedFolder vrefolder = ws.getVREFolderByScope("/gcube/devNext/NextNext");
		ws.copy(myFolder.getId(), vrefolder.getId());

	}


}
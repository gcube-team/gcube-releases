package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.JCRUser;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessage;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalUrl;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRFile;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class UploadFile {
	static JCRWorkspace ws = null;
	//	static WorkspaceFolder folder;
	static InputStream in = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, WrongItemTypeException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException, WrongItemTypeException {
		//		ScopeProvider.instance.set("/gcube");
		//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		String user = "anasfkhan81";
		//		String user1 = "roberto.cirillo";
		//		String user = "gianpaolo.coro";


		//				Workspace ws = HomeLibrary.getUserWorkspace(user);


		Home home = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user);
		ws = (JCRWorkspace) home.getWorkspace();

//		String scope = "/d4science.research-infrastructures.eu/gCubeApps/Parthenos";
//		WorkspaceSharedFolder vreFolder = ws.getVREFolderByScope(scope);
//		List<WorkspaceItem> children = vreFolder.getChildren();
//		for (WorkspaceItem child: children){
//
//			if (!child.isFolder()){
//				System.out.println(child.getPath());
//				download(child);
//			}
//		}

//		System.out.println("---> " +vreFolder.getACLUser().toString());
		
		WorkspaceItem child = ws.getItem("cbbd3b8a-1e39-42e8-b786-a74508c87862");
		System.out.println(child.getPath());
		download(child);
		//		JCRUserManager um = (JCRUserManager) HomeLibrary
		//				.getHomeManagerFactory().getUserManager();
		////		um.removeUserFromGroup(scope, "anasfkhan81");
		//		um.associateUserToGroup(scope, "anasfkhan81");
		//		JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BlueBridgeProject/Work Packages/WP7 Blue Environment/Deliverables/D7.3/old/BlueBRIDGE_D7.3_M18_V0.1.docx");
		//		
		//		String remotePath = item.getRemotePath();
		//		System.out.println("---> " + remotePath);
		//		try {
		//			item.setRemotePath(remotePath);
		//		} catch (org.gcube.common.homelibrary.model.exceptions.RepositoryException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//			System.out.println(item.getPath());
		//		File initialFile = new File("/home/valentina/Downloads/pisa.html");
		//	    InputStream fileData = FileUtils.openInputStream(initialFile);
		//	    ws.updateItem(item.getId(), fileData);
		//		System.out.println(item.getStorageID());
		//		System.out.println(item.getRemotePath());
		//		System.out.println(item.getPublicLink(false));
		//		System.out.println(ws.getStorage().getRemotePathByStorageId(item.getStorageID()));



		//		JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItem("169e62b7-f128-49f6-b33a-46c2195adf2c");
		//		System.out.println(item.getDelegate());


		//		WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-SmartArea-SmartBuilding/smartbuildinglogo.png");
		//System.out.println(item.getRemotePath());
		//		String name = "gCube-"+ UUID.randomUUID();
		//		WorkspaceFolder myfolder = ws.createFolder(name, "description", ws.getRoot().getId());
		//
		//		
		//		List<String> users = new ArrayList<>();
		//		users.add("roberto.cirillo");
		//		WorkspaceSharedFolder folder = myfolder.share(users);
		//		//		WorkspaceSharedFolder folder = ws.createSharedFolder("gCube-"+ UUID.randomUUID(), "description", users, ws.getRoot().getId());
		//		folder.setACL(users, ACLType.WRITE_ALL);
		//		//		WorkspaceSharedFolder folder = (WorkspaceSharedFolder) ws.getItemByPath("/Workspace/gCubeItems(0)");
		//		folder.setSystemFolder(true);
		//		System.out.println("** " + folder.isSystemFolder());


		//		Home home1 = HomeLibrary
		//				.getHomeManagerFactory()
		//				.getHomeManager()
		//				.getHome(user1);
		//		JCRWorkspace ws1 = (JCRWorkspace) home1.getWorkspace();
		//		WorkspaceSharedFolder folder1 = (WorkspaceSharedFolder) ws1.getItemByPath("/Workspace/"+name);

		//		WorkspaceFolder unshared = folder.unShare();
		//		
		//		
		////		WorkspaceSharedFolder folder2 = (WorkspaceSharedFolder) ws.getItemByPath("/Workspace/"+name);
		//		System.out.println("** " + unshared.isSystemFolder());

		//		ws.getItemByPath("/Workspace/gCubeItems").remove();
		////
		////		WorkspaceFolder folder = ws.getRoot().createFolder("gCubeItems", "test system folder");
		//		
		////		WorkspaceFolder folder1 = ws.createFolder("gCubeItems", "test system folder", ws.getRoot().getId());
		////		
		////		System.out.println(shared.getName());
		////
		////		shared.setSystemFolder(true);
		////		System.out.println("** " + shared.getName());
		////		
		////		
		//		List<String> users = new ArrayList<>();
		//		users.add("valentina.marioli");
		//		WorkspaceSharedFolder shared = ws.createSharedFolder("gCubeItems", "test system folder", users, ws.getRoot().getId());
		////WorkspaceSharedFolder shared = folder.share(users);
		////		
		//		shared.setACL(users, ACLType.WRITE_ALL);
		//		
		//		System.out.println(shared.getName());
		//
		//		shared.setSystemFolder(true);
		//		System.out.println("** " + shared.getName());

		//		System.out.println(folder.getACLUser().toString());

		//		Map<ACLType, List<String>> map = folder.getACLOwner();
		//		Set<ACLType> set = map.keySet();
		//		for (ACLType type : set){
		//			System.out.println(type + " - " +map.get(type));
		//		}
		//		
		//				String name = "Google"+ UUID.randomUUID();
		//				String description = "test";
		//				String url = "https://www.google.it/";
		//				String destinationFolderId = ws.getRoot().getId();
		//				JCRExternalUrl myUrl = (JCRExternalUrl) ws.createExternalUrl(name, description, url, shared.getId());
		//				System.out.println(myUrl.getUrl());

		//		WorkspaceFolder folder = null;
		//
		//			folder = (WorkspaceFolder) ws.find("AcAA");
		//		if (folder==null)
		//			System.out.println("item not found");
		//		folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/aaa");
		//		List<String> users = new ArrayList<>();
		//		users.add("roberto.cirillo");
		//		WorkspaceSharedFolder shared = folder.share(users);
		//		shared.setACL(users, ACLType.WRITE_ALL);

		//		List<String> scopes = new ArrayList<>();
		//		scopes.add("/gcube");
		//		String itemType = "test-type";
		//		Map<String, String> properties = null;
		//		String destinationFolderId = folder.getId();
		//	JCRWorkspaceItem gcubeItem = ws.createGcubeItem("test-"+ UUID.randomUUID(), "description", scopes, user, itemType, properties, destinationFolderId);
		//System.out.println(gcubeItem.getPublicLink(false));


		//		int i = 0;
		//		for(WorkspaceItem child: folder.getChildren()){
		//			//					System.out.println(child.getRemotePath());
		//			//			System.out.println(child.getPath());
		//			//			System.out.println(child.getCreationTime().getTime());
		//			//
		//			if (!child.isFolder()){
		//								System.out.println("*** " + child.getRemotePath());
		////				try{
		////					System.out.println(i + ")*** " + child.getPublicLink(false));	
		////				} catch (Exception e) {
		////					System.out.println("*** -> Error");
		////					System.out.println(child.getRemotePath());
		////					//												e.printStackTrace();
		////				}
		//				i++;
		//			}
		//
		//		}
		//		
		//		System.out.println(i);






		//WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-StockAssessment/Notes/BlueASSESSMENT160428.docx");
		//System.out.println(item.getPath());
		//JCRExternalFile file = (JCRExternalFile) item;
		//System.out.println("*** " +file.getPublicLink());
		//System.out.println("*** " + file.getStorageId());

		//System.out.println(item.getStorageID());
		//System.out.println(item.getRemotePath());

		//		JCRWorkspaceMessageManager messageManager = (JCRWorkspaceMessageManager) ws.getWorkspaceMessageManager();
		//		List<WorkspaceMessage> list = messageManager.getReceivedMessages();
		//		
		//		for (WorkspaceMessage msg : list){
		//			System.out.println(msg.getSubject());
		//			System.out.println(msg.getId());
		//			System.out.println(msg.getSender().getPortalLogin());
		//			System.out.println("**");
		//		
		//			JCRWorkspaceMessage mymsg = (JCRWorkspaceMessage) msg;
		//			String requestId = mymsg.getId();
		//			System.out.println("** GET RECEIVED MESSAGE BY ID " + requestId);
		//			WorkspaceMessage message = messageManager.getReceivedMessage(requestId);
		//			System.out.println(message.getSubject());
		//			
		//		}

		//		 [query: /jcr:root/Home/4.facchini/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu//element(*,nthl:workspaceItem) order by @jcr:lastModified descending - lang: xpath - login: 4.facchini - limit: 6]
		//		  javax.jcr.query.InvalidQueryException: Encountered "/" at line 1, column 36.


		//		System.out.println("ROOT " + ws.getRoot().getId());

		//		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/BBB");

		//		 List<WorkspaceItem> children = ws.getRoot().getChildren();
		//		 for (WorkspaceItem child: children){
		//			 System.out.println(child.getId());
		//		 }

		//		System.out.println(ws.getRoot().getChildren().size());

		//				String url = "http://data-d.d4science.org/Q0IvYW5jaFphUXJHak9iQ0ZuUnZsU0pveHhNYzhYMm5HbWJQNStIS0N6Yz0";
		//				
		//		
		//				try{
		//					in = new URL(url).openStream();
		//		
		//		
		//		//		String name = WorkspaceUtil.getUniqueName("data", ws.getRoot());
		//					
		//					String name = "doc-" + UUID.randomUUID().toString() + ".odt";
		//					Map<String, String> properties1 = new HashMap<String, String>();
		//					properties1.put("key0", "value0");
		//					properties1.put("key1", "value1");
		//					properties1.put("key2", "value2");
		//					String mimetype = "application/vnd.oasis.opendocument.text";
		//					FolderItem fileItem = WorkspaceUtil.createExternalFile(folder, name, "de", in, properties1, mimetype, 18000);
		//					System.out.println("*************** " + fileItem.getPath());
		//					
		//					
		//					System.out.println(fileItem.getRemotePath());
		//		//			System.out.println(fileItem.getProperties().getProperties().size());
		//		
		//				} catch (Exception e) {
		//									e.printStackTrace();
		//				}finally{
		//					if (in!=null)
		//						in.close();
		//				}


	}

	private static void download(WorkspaceItem item) throws InternalErrorException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
	

		try{
//			WorkspaceItem item = ws.getItemByPath("/Workspace/Proposal/731001-AGINFRA PLUS-Evaluation Summary Report.pdf");
		
			JCRExternalFile file = (JCRExternalFile) item;
			inputStream = file.getData();

			outputStream = new FileOutputStream(new File("/home/valentina/Downloads/TEST_REST/"+ item.getName()));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}	
		
	}





	//		private static void getItem(WorkspaceItem item, Writer writer) throws InternalErrorException, IOException {
	//			WorkspaceFolder folder = (WorkspaceFolder) item;
	//			for(WorkspaceItem child: folder.getChildren()){
	//				//			System.out.println(child.getRemotePath());
	//				//			System.out.println(child.getPath());
	//				//			System.out.println(child.getCreationTime().getTime());
	//
	//				if (!child.isFolder()){
	//					//				System.out.println("*** " + child.getRemotePath());
	//					try{
	//						System.out.println("*** " + child.getPublicLink(false));	
	//					} catch (Exception e) {
	//						System.out.println("*** -> Error");
	//						System.out.println(child.getRemotePath()  + "\t" + child.getCreationTime().getTime());
	//
	//						writer.write(child.getRemotePath()  + "\t" + child.getCreationTime().getTime());
	//						writer.write("\n");
	//						//												e.printStackTrace();
	//					}
	//
	//				}else
	//					getItem(child, writer);
	//
	//			}
	//		}




}

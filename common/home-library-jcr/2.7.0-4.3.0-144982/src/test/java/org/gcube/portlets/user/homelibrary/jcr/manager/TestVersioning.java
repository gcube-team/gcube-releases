package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.PathNotFoundException;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
//import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;

public class TestVersioning {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {

		WorkspaceFolder folder = null;
		try {
			//								String scope = "/gcube/devsec";
			//			String username = "valentina.marioli";
//						ScopeProvider.instance.set("/gcube/devNext/NextNext");
			//						ScopeProvider.instance.set("/gcube/preprod/preVRE");
//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
			//			SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

					SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");
			//						SecurityTokenProvider.instance.set("aac13fb7-d074-45ae-aa47-61718956a5e6-98187548");

			// Obtained the factory you can retrieve the HomeManager:
			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();

			Home home = manager.getHome("valentina.marioli");

			JCRWorkspace ws = (JCRWorkspace) home.getWorkspace();
			
			String scope = "/gcube/devNext/Test03";
			
//			WorkspaceVREFolder shared = ws.createVREFolder(scope, "test", "Test00", ACLType.WRITE_ALL);
			UserManager uManager = HomeLibrary.getHomeManagerFactory().getUserManager();
			
			String groupID = "gcube-devNext-Test03";
//			uManager.associateUserToGroup(groupID, "valentina.marioli");
			uManager.setAdministrator(scope, "valentina.marioli");
//			uManager.createGroup("gcube-devNext-Test03");
//
////			uManager.associateUserToGroup(scope, "francesco.mangiacrapa");
//			 WorkspaceSharedFolder shared = ws.createSharedFolder(scope, "", "gcube-devNext-Test03", ws.getMySpecialFolders().getId(), "Test03", true);
//			List<String> users = new ArrayList<String>();
//			users.add("gcube-devNext-Test03");
//			shared.share(users);
//			
//			shared.setACL(users, ACLType.WRITE_ALL);
//			List<String> logins = new ArrayList<String>();
//			logins.add("valentina.marioli");
//			shared.setAdmins(logins);
//			shared.setAdmins(logins)
			
//			WorkspaceSharedFolder vre = ws.getVREFolderByScope(scope);
//			
//			vre.addAdmin("valentina.marioli");
//			WorkspaceItem item = ws.getItemByPath("/Workspace/MySpecialFolders/gcube-devNext-Test");
//			item
			
			
//			JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Workspace/versions/14853811557_c2363c1d0b_k.jpg");
////			ws.removeItem(item.getId());
//			item.remove();
			
//			ws.getTrash().restoreAll();
//			List<WorkspaceItem> children = ws.getTrash().getChildren();
//			for (WorkspaceItem child: children){
//				System.out.println(child.getId() + " - " + child.getName());
//				ws.getTrash().restoreById(child.getId());
//			}
			

			//			folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BlueBridgeProject/Meetings/Project Meetings/");
			//			List<WorkspaceItem> children = folder.getChildren();
			//			for (WorkspaceItem child: children){
			//				System.out.println(child.getId());
			//				System.out.println(child.getPath() );
			////				System.out.println(child.getRemotePath());
			//			}


//			WorkspaceItem item = ws.getItemByPath("/Workspace/versions/103529784_062ff7b562_b.jpg");
//			ws.moveItem(item.getId(), ws.getRoot().getId());
//			System.out.println(item.getPath());
//			String remote = ws.getStorage().getRemotePathByStorageId(item.getStorageID());
//			String remotePath = item.getRemotePath();
//			System.out.println(" ******* " + remotePath);
//			System.out.println( " ******* " + remote);
//			if (!remotePath.equals(remote)){
//				System.out.println("*** " + item.getPath());
//				//			JCRWorkspaceItem mychild = (JCRWorkspaceItem)child;
//				//			mychild.setRemotePath(remote);
//			}

//						folder = (WorkspaceFolder) ws.getItem("0a5bd703-212c-4d46-891d-08ce5d3bc1f0");
//						List<WorkspaceItem> children = folder.getChildren();
//						for (WorkspaceItem child: children){
//			//				//				System.out.println(child.getId());
//							System.out.println(child.getPath() );
//							
//							if(child.getName().equals("20170308 Exploiting the Resource Catalogue.pptx")){
//								child.rename("5. 20170308 Exploiting the Resource Catalogue.pptx");
//							}
//			//				System.out.println(child.getRemotePath());
////							String path = child.getPath();
////							String remotePath = child.getRemotePath();
////			//				String wsName = path.substring(path.lastIndexOf('/') + 1);
////			//				String wsStorage = remotePath.substring(remotePath.lastIndexOf('/') + 1);
////			//				System.out.println(wsName + " - " + wsStorage);
////							
////							String storageID = child.getStorageID();
////			//				System.out.println(storageID);
////							String remote = ws.getStorage().getRemotePathByStorageId(storageID);
////			//				System.out.println(" ******* " + remotePath);
////			//				System.out.println( " ******* " + remote);
////							if (!remotePath.equals(remote)){
////								System.out.println("*** " + path);
////			//					JCRWorkspaceItem mychild = (JCRWorkspaceItem)child;
////			//					mychild.setRemotePath(remote);
////							}
//						}
			//			List<WorkspaceItem> list = ws.getTrash().getChildren();
			//			
			//			ws.getTrash().emptyTrash();
			//			for(WorkspaceItem item: list){
			//				System.out.println(item.getName() + " - " + item.getId());
			//				ws.getTrash().deletePermanentlyById("d2efd576-d57b-439d-9cd0-f29755bca523");
			//			}

			//			System.out.println(ws.getOwner().getPortalLogin());
			//			
			//			ws.getTrash().emptyTrash();

			//			ws.getItemByPath("/Workspace/MySpecialFolders/gcube-devsec-SmartCamera/A-af85030f-69b9-47df-b318-4c3cdcce152b").remove();
			//		WorkspaceItem test = ws.getItem("1fede4e2-5859-4f19-bddb-aec7dd5b632f");
			//		System.out.println(test.getPath());


			//			File initialFile = new File("/home/valentina/hs_err_pid14068.log");
			//			InputStream fileData = FileUtils.openInputStream(initialFile);
			//			String name = "test" + UUID.randomUUID()+ ".log";
			//			ExternalFile file = ws.createExternalFile(name, "", null, fileData, ws.getItemByPath("/Workspace/").getId());
			//			System.out.println(file.getPath());

			//			WorkspaceFolder item = (WorkspaceFolder)ws.getItem("67581e69-8a0e-415b-acc5-39c75e777b56");

			//			String scope = "/d4science.research-infrastructures.eu/D4Research/FisheriesandEcosystemAtMII";
			//			WorkspaceSharedFolder shared = ws.getVREFolderByScope(scope);
			//			System.out.println(shared.getUsers().toString());
			////			System.out.println(shared.getLastItems(10).size());
			//			
			//			List<WorkspaceItem> last = shared.getLastItems(10);
			//			for (WorkspaceItem item: last){
			//				System.out.println(item.getPath());
			//			}
			//			JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Workspace/versions/version.png");


			//			System.out.println(item.getId());
			//			item.markAsRead(true);
			//item.rename("version-vale-png");
			//
			//			List<? extends WorkspaceItem> children = item.getChildren();
			//			for (WorkspaceItem child: children){
			////				String path = child.getPath();
			////				String remotePath = child.getRemotePath();
			////				String wsName = path.substring(path.lastIndexOf('/') + 1);
			////				String wsStorage = remotePath.substring(remotePath.lastIndexOf('/') + 1);
			////				if (!wsStorage.equals(wsName)){
			////					System.out.println(path);
			////				}
			////
			////				String realPath = ws.getStorage().getRemotePathByStorageId(child.getStorageID());
			////				if (!realPath.equals(remotePath)){
			////					System.out.println("**************** " +path);
			////				}
			//				
			//				System.out.println(child.getPublicLink(false));
			//
			//				//						System.out.println("***");
			//			}
			//						System.out.println(item.getStorageID());

			//						System.out.println(ws.getStorage().getRemotePathByStorageId(item.getStorageID()));
			//						item.setRemotePath(ws.getStorage().getRemotePathByStorageId(item.getStorageID()));

			//						/Home/valentina.marioli/Workspace/versions/version.png
			//						ExternalFile file = (ExternalFile) item;
			//						file.removeVersion("1.0");
			////						System.out.println(item.getCreationTime().getTime());
			//			ExternalFile file = (ExternalFile) item;
			//			System.out.println(file.getName());


			//						List<AccountingEntry> accounting = item.getAccounting();
			//						for (AccountingEntry entry: accounting){
			//							System.out.println(entry.toString());
			//						}
			//			System.out.println(item.getId() + " <----");
			//			System.out.println(item.getPath());
			//			
			////			System.out.println("**MOVE**");
			////			WorkspaceItem myitem = ws.moveItem(item.getId(), ws.getRoot().getId());
			////			System.out.println(myitem.getPath());
			////			System.out.println("**RENAME**");
			////			item.rename("b.log");
			//			
			//		JCRExternalFile file = (JCRExternalFile) item;
			//			System.out.println("current");
			//			System.out.println("REMOTE PATH "+ file.getRemotePath());
			////			System.out.println("storage id " +item.getStorageID());
			//			//			System.out.println("***" +ws.getStorage().getRemotePathByStorageId(item.getStorageID()));
			//			System.out.println("**********");
			//			System.out.println(file.getId());

			//		System.out.println("**GET A SINGLE VERSION**");
			//		System.out.println(file.getVersion("1.2").getName());
			//	System.out.println(file.getCurrentVersion().toString());
			//						System.out.println("**HISTORY**");
			//						List<WorkspaceVersion> list = file.getVersionHistory();
			//						for (WorkspaceVersion version: list){
			//							System.out.println(version.getName());
			//							
			//							InputStream inputStream = file.downloadVersion(version.getName()); 
			//							System.out.println(version.getName() + " ---> " + inputStream.available() + " ---> " + version.getRemotePath());
			//							
			//			//				InputStream stream = file.downloadVersion("1.9");
			//							System.out.println(inputStream.available());
			//							
			//							FileOutputStream outputStream = new FileOutputStream(new File("/home/valentina/Downloads/TEST_REST/versioning/"+version.getName()));
			//				
			//							int read = 0;
			//							byte[] bytes = new byte[1024];
			//				
			//							while ((read = inputStream.read(bytes)) != -1) {
			//								outputStream.write(bytes, 0, read);
			//							}
			//				//
			//							System.out.println("Done!");
			//							
			//						}

			//			file.restoreVersion("1.0");



			//			folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/test 5 gennaio");
			//			System.out.println("ID " + folder.getId());
			//			navigate(folder);

			//			WorkspaceItem item = ws.getItemByPath("/Workspace/TestShare/UCF_-_SOLIDWORKS_V.ppt");
			//			ws.moveItem(item.getId(), folder.getId());

			//			System.out.println(item.getAdministrators().toString());
			//			List<String> admins = new ArrayList<String>();
			//			admins.add("massimiliano.assante");
			//			item.setAdmins(admins);
			//			System.out.println(item.getAdministrators().toString());
			//			item.setACL(item.getUsers(), ACLType.WRITE_ALL);
			//			System.out.println(item.getAdministrators().toString());

			//			System.out.println("**UPDATE**");
			//			File initialFile = new File("/home/valentina/Downloads/AAA-version/1.1/version.png");
			//			InputStream fileData = FileUtils.openInputStream(initialFile);
			//			ws.updateItem(item.getId(), fileData);


			//												List<AccountingEntry> account = item.getAccounting();
			//												for (AccountingEntry entry: account){
			//													System.out.println(entry.toString());
			//												}
			////			System.out.println("**RESTORE**");
			////			String versionToRestore = "1.0";
			////			System.out.println("Restore Version " + versionToRestore);
			////			
			////			file.restoreVersion(versionToRestore);
			////			System.out.println("REMOTE PATH "+ file.getRemotePath());
			//						System.out.println("**HISTORY**");
			//						WorkspaceItem newItem = ws.getItem(item.getId());
			//						System.out.println("--> " + newItem.getPath());
			//						List<String> list1 = file.getVersionHistory();
			//						for (String version: list1){
			//							System.out.println(version);
			//						}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void navigate(WorkspaceItem item) throws InternalErrorException {
		//		if (!item.isFolder()){
		System.out.println(item.isFolder());
		System.out.println(item.getId());
		System.out.println(item.getPath());
		System.out.println(item.getName());
		System.out.println(item.getDescription());
		//		}else{
		if (item.isFolder()){
			WorkspaceFolder folder = (WorkspaceFolder) item;
			List<WorkspaceItem> children = folder.getChildren();
			for (WorkspaceItem child: children){
				navigate(child);

			}
		}

	}


}

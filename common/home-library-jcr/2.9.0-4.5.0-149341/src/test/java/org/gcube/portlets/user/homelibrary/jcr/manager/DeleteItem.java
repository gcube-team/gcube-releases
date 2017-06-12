package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;

import org.gcube.common.homelibrary.home.HomeLibrary;
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
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class DeleteItem {
	//	static JCRWorkspace ws = null;

	//	private static void uploadFile(WorkspaceFolder subFolder) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException {
	//		String fileName = "img-" + UUID.randomUUID()+ ".jpg";
	//		InputStream is = null;
	//
	//		try {
	//			is = new FileInputStream("/home/valentina/Downloads/4737062744_9dd84a2df2_z.jpg");
	//			ExternalFile f = subFolder.createExternalImageItem(fileName, "test", "image/jpg", is);
	//			System.out.println(f.getPath());
	//
	//		} catch (FileNotFoundException e) {
	//			e.printStackTrace();
	//		}finally{
	//			if (is!=null)
	//				is.close();
	//		}
	//
	//	}
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		//				ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ScopeProvider.instance.set("/gcube/devsec");
		UserManager um = HomeLibrary
				.getHomeManagerFactory().getUserManager();
		//				.getHomeManager()
		//				.getHome("valentina.marioli").getWorkspace();

		ArrayList<String> groups = new ArrayList<String>();
		for (GCubeGroup group : um.getGroups()){
			String displayName = group.getDisplayName();
			if (!groups.contains(displayName))
				groups.add(displayName);
			//			System.out.println(group.getName());
			//			if (group.getName().startsWith("gcube-VREVale")){
			//				um.deleteAuthorizable(group.getName());
			//				System.out.println(group.getName() + " removed.");
			//			}
		}

		System.out.println(groups.toString());


				for ( String user : um.getUsers()){
					  Workspace ws = HomeLibrary
								.getHomeManagerFactory()
								.getHomeManager()
								.getHome(user).getWorkspace();
					 for (WorkspaceItem folder : ws.getMySpecialFolders().getChildren()){
						 WorkspaceSharedFolder vre = (WorkspaceSharedFolder) folder;
						 System.out.println(vre.getDisplayName());
						 if (!groups.contains(vre.getDisplayName())){
							 try{
							 vre.unShare();
							 } catch (Exception e) {
												System.out.println("catch");
											}
						 }
					 }
					  
		
				}

		//		try{
		//
		//			String name = "test-" + UUID.randomUUID().toString();
		//			String description = "";
		//			List<String> users = new ArrayList<String>();
		//			users.add("francesco.mangiacrapa");
		//			
		//			
		//			WorkspaceSharedFolder item = (WorkspaceSharedFolder) ws.createSharedFolder(name, description, users, ws.getRoot().getId());
		//			WorkspaceFolder destinationFolder = (WorkspaceFolder) ws.getItemByPath("/Workspace/Destination");
		//			item.move(destinationFolder);
		//			//			System.out.println(item.getPath());
		//			//			List<? extends WorkspaceItem> chidren = item.getChildren();
		//
		////			uploadFile(item);
		//		} catch (Exception e) {
		//			System.out.println("catch");
		//		}

		//		List<String> users = new ArrayList<String>();
		//		users.add("francesco.mangiacrapa");
		//		users.add("massimiliano.assante");
		//		users.add("roberto.cirillo");
		//
		//		String description = "desc";
		//		String name = "testShare-"+ UUID.randomUUID().toString();
		//
		//		WorkspaceSharedFolder item = ws.createSharedFolder(name, description, users, ws.getRoot().getId());
		//		item.setACL(users, ACLType.WRITE_ALL);
		//
		//	
		////		System.out.println("*** share *** ");
		////		List<String> shareUsersList = new ArrayList<String>();
		////		shareUsersList.add("francesco.mangiacrapa");
		////		shareUsersList.add("valentina.marioli");
		////		item.share(shareUsersList);
		////		item.setACL(shareUsersList, ACLType.WRITE_ALL);
		//		
		//		
		//		System.out.println("*** unshare *** ");
		//		List<String> unshareUsersList = new ArrayList<String>();
		//		unshareUsersList.add("massimiliano.assante");
		//		unshareUsersList.add("roberto.cirillo");
		//		for (String user: unshareUsersList)
		//			item.unShare(user);



		//				WorkspaceSharedFolder item = (WorkspaceSharedFolder) ws.getItemByPath("/Workspace/Experiments/EwE console apps");
		//			System.out.println(item.getUsers().toString());


		//		JCRUserManager um = (JCRUserManager) HomeLibrary
		//				.getHomeManagerFactory().getUserManager();
		//
		//		String groupId = "d4science.research-infrastructures.eu-gCubeApps-ENVRIPlus";
		//		GCubeGroup group = um.getGroup(groupId);
		//		System.out.println(group.getMembers().toString());
		//		//		String scope = "/Test00/Test01/TestPlus";
		//		String username = "gianpaolo.coro";
		//		final String portalLogin = "massimiliano.assante";
		//		String scope = "/d4science.research-infrastructures.eu/gCubeApps/ENVRIPlus";
		//		um.associateUserToGroup(scope, username, portalLogin);		
		//		um.removeUserFromGroup(scope, username, portalLogin);

		//		GCubeGroup group = um.createGroup(scope);
		//		WorkspaceSharedFolder folder = ws.createSharedFolder("Test00-Test01-TestPlus", "desc", group.getName(), ws.getRoot().getId(), "TestPlus", true);
		//
		//		folder.setACL(new ArrayList<String>(){/**
		//		 * 
		//		 */
		//			private static final long serialVersionUID = 1L;
		//
		//			{add(portalLogin);}}, ACLType.ADMINISTRATOR);
		//		folder.setACL(folder.getUsers(), ACLType.WRITE_ALL);
	}


}

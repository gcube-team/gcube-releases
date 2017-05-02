package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQueryBuilder;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRPrivilegesInfo;
import org.gcube.common.homelibrary.jcr.workspace.privilegemanager.JCRPrivilegeManager;
import org.gcube.common.scope.api.ScopeProvider;

public class GuestLoginModule {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		createLibrary();

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
		//		ScopeProvider.instance.set("/gcube");
		//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		//		ws = (JCRWorkspace) HomeLibrary
		//				.getHomeManagerFactory()
		//				.getHomeManager().getGuestLogin().getWorkspace();

		String scope = "/d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject";

ScopeProvider.instance.set(scope);

//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();
		
		
		WorkspaceFolder file = (WorkspaceFolder) ws.getItemByPath("/Workspace/condivisaconvale");
		file.setPublic(false);
//		System.out.println(file.getOwner().getPortalLogin());
		
		List<WorkspaceItem> list = ws.getPublicFolders();
		for (WorkspaceItem item: list){
			System.out.println(item.getName());
		}
		
//		ws.removeItem(file.getId());
//		WorkspaceItem folder = ws.getItemByPath("/Workspace/TEST_ACL/WRITE_ANY");
//		ws.moveItem(file.getId(), folder.getId());
//		ws.renameItem(file.getId(), "mytest-"+ UUID.randomUUID() +".jpg");
		
//		System.out.println(ws.getRoot().getPath());

//		WorkspaceSharedFolder file = (WorkspaceSharedFolder) ws.getItemByPath("/Workspace/a8d1a2db-f5f0-43e3-8fa2-be745d2d3aaa");
//		System.out.println(file.getPath());
//
//		WorkspaceItem folder = ws.getItem(file.getId());



		//								JCRWorkspaceFolder folders = (JCRWorkspaceFolder) ws.getMySpecialFolders();
		//								folders.remove();


		//		UserManager um = HomeLibrary
		//				.getHomeManagerFactory().getUserManager();
		//		
		//		um.deleteAuthorizable("valentina.marioli");
		//		um.createUser("valentina.marioli", "3.1.1");


		//		List<String> users = um.getUsers();
		//		for(String user: users){
		//			if (user.startsWith("gcube-devsec-TEST-TEST")){
		//				System.out.println("Remove " + user);
		//				um.deleteAuthorizable(user);
		//				
		//		}
		//		}


		//		List<GCubeGroup> groups = um.getGroups();
		//		for (GCubeGroup group: groups) {
		////			try {
		////				
		////				if(group.getName().startsWith("gcube-devsec-testVRE")){
		////					System.out.println("Remove " + group.getName());
		//////					um.deleteAuthorizable(group.getName());
		////				}
		//
		//				//										GCubeGroup group = um.getGroup("vreScope219f92b9-cdaa-40d3-9669-6dac42889a3f");
		//														System.out.println(group.getName());
		////			} catch (Exception e) {
		////
		//			} 
		////
		////		}
		//				String itemId = "1fd83387-839d-48d4-92ef-600f31a6c00a";
		//				WorkspaceItem item = ws.getItem(itemId);
		//				System.out.println(item.getName());
		//				//		System.out.println(item.getPath());
		//				//		WorkspaceItem item = ws.createFolder(UUID.randomUUID().toString(), "desc", ws.getRoot().getId());
		//				JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) item;
		//				
		//				folder.setPublic(false);


//		List<WorkspaceItem> list = ws.getRoot().getChildren();
		//				for (WorkspaceItem publicFolder : publicFolders){
		////					System.out.println("----> " + publicFolder.getPath());
		//					if (publicFolder.isFolder()){
		//						WorkspaceFolder myf = (WorkspaceFolder) publicFolder;
		//						if (myf.isPublic())
		//						System.out.println(myf.getName());
		//					}
		//					
		//				}

		//		System.out.println(folder.getACLOwner());
		//		System.out.println(folder.getPath() +" IS ROOT-> " + folder.isRoot());
		//		folder.setPublic(false);
		//		System.out.println("IS PUBLIC? " + folder.isPublic());

		//
		//		System.out.println(item.getPath());
		//		System.out.println(item.getId());

		//		System.out.println("PARENTS BY ID");
		//		List<WorkspaceItem> list = ws.getParentsById(itemId);
		//		System.out.println(list.size());
//		for(WorkspaceItem item: list){
//			
//			if (!item.isShared())
//				continue;
//			JCRWorkspaceItem child = (JCRWorkspaceItem) item;
//			
////			System.out.println("-> " + child.getPath());
////			System.out.println("IS ROOT-> " + child.isRoot());
//			System.out.println("** " + child.getPath());
//			String owner = child.getOwner().getPortalLogin();
//			String currentUser = ws.getOwner().getPortalLogin();
//			String absPath = child.getAbsolutePath();	
//			System.out.println("canAddChildren? " + JCRPrivilegesInfo.canAddChildren(owner, currentUser, absPath));
//			System.out.println("canDelete? " + JCRPrivilegesInfo.canDelete(owner, currentUser, absPath, false));
//			System.out.println("canDeleteChildren? " + JCRPrivilegesInfo.canDeleteChildren(currentUser, absPath));
//			System.out.println("canModifyProperties? " + JCRPrivilegesInfo.canModifyProperties(owner, currentUser, absPath, false));
//			System.out.println("canReadNode? " + JCRPrivilegesInfo.canReadNode(owner, currentUser, absPath));
//
//		}
		//		JCRWorkspaceItem parent =  (JCRWorkspaceItem) item.getParent();
		//
		//		System.out.println(parent.getId());
		//
		//		System.out.println(parent.getACLOwner());
		//
		//		//JCRWorkspaceItem parent1 =  (JCRWorkspaceItem) parent.getParent();
		//		//		
		//		//		System.out.println(parent1.getACLOwner());
		//
		//		String owner = parent.getOwner().getPortalLogin();
		//		String currentUser = ws.getOwner().getPortalLogin();
		//		//		String currentUser = "guest";
		//		String absPath = parent.getAbsolutePath();	
		//		System.out.println("canAddChildren? " + JCRPrivilegesInfo.canAddChildren(owner, currentUser, absPath));
		//		System.out.println("canDelete? " + JCRPrivilegesInfo.canDelete(owner, currentUser, absPath, false));
		//		System.out.println("canDeleteChildren? " + JCRPrivilegesInfo.canDeleteChildren(currentUser, absPath));
		//		System.out.println("canModifyProperties? " + JCRPrivilegesInfo.canModifyProperties(owner, currentUser, absPath, false));
		//		System.out.println("canReadNode? " + JCRPrivilegesInfo.canReadNode(owner, currentUser, absPath));

		//		System.out.println(parent.getPublicLink(true));

		//		System.out.println("CHILDREN");
		//		List<? extends WorkspaceItem> list1 = folder.getChildren();
		//		for(WorkspaceItem child: list1){
		//			System.out.println("** " +child.getName());
		////			System.out.println("** " + child.getPath() + " id " + child.getId());
		//			if (child.isFolder()){
		//				System.out.println(child.getPath() + " is folder");
		//			
		//				JCRWorkspaceFolder myF = (JCRWorkspaceFolder) child;
		//				List<WorkspaceItem> children = myF.getChildren();
		//				for(WorkspaceItem cc: children){
		//					System.out.println(cc.getName());
		//				}
		//			}
		//			}

		//			String absPath = parent.getAbsolutePath();	
		//			System.out.println("canAddChildren? " + JCRPrivilegesInfo.canAddChildren(owner, currentUser, absPath));
		//			System.out.println("canDelete? " + JCRPrivilegesInfo.canDelete(owner, currentUser, absPath, false));
		//			System.out.println("canDeleteChildren? " + JCRPrivilegesInfo.canDeleteChildren(currentUser, absPath));
		//			System.out.println("canModifyProperties? " + JCRPrivilegesInfo.canModifyProperties(owner, currentUser, absPath, false));
		//			System.out.println("canReadNode? " + JCRPrivilegesInfo.canReadNode(owner, currentUser, absPath));

		//		}


		//		http://ws-repo-test.d4science.org/home-library-webapp/CanReadNode?adminId=workspacerep.imarine&adminPassword=gcube2010*onan&login=guest&absPath=%2FWorkspace%2FPreprod%2Fbbb%2Faaaadsfsd%2F16954170355_ca2db200d6_q.jpg
		//		http://ws-repo-test.d4science.org/home-library-webapp/CanReadNode?adminId=workspacerep.imarine&adminPassword=gcube2010*onan&login=guest&absPath=%2FHome%2Fvalentina.marioli%2FWorkspace%2FPreprod%2Fbbb%2Faaaadsfsd

	}

}

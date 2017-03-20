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
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.scope.api.ScopeProvider;

public class ShareFolder {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		ScopeProvider.instance.set("/gcube");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();

//		List<WorkspaceItem> children = ws.getRoot().getChildren();
//		for (WorkspaceItem child: children){
//			System.out.println(child.getPath());
//		}
		
		
//		WorkspaceSharedFolder shared = (WorkspaceSharedFolder) ws.getItemByPath("/Workspace/47c62174-f2a9-412e-a63e-24ebd834b4ce");
//		System.out.println(shared.getUsers().toString());
//		
//		WorkspaceSharedFolder myshare = (WorkspaceSharedFolder) ws.getItem(shared.getId());
//		System.out.println(myshare.getUsers().toString());
		 WorkspaceItem folder = ws.createFolder(UUID.randomUUID().toString(), "desc", ws.getRoot().getId());
		 
		 WorkspaceItem subfolder = ws.createFolder("test", "desc", folder.getId());
		System.out.println(folder.getPath());
		List<String> users = new ArrayList<String>();
		users.add("roberto.cirillo");
		WorkspaceSharedFolder shared = ws.share(users, folder.getId());
		System.out.println("SET ACL");
		shared.setACL(users, ACLType.WRITE_ALL);
		System.out.println(shared.getPath());


		
//		JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder) ws.getItemByPath("/Workspace/AMDataSets");
//		System.out.println(sharedFolder.getACLUser());
//		sharedFolder.unShare();
//		System.out.println(sharedFolder.getUsers().toString());
//		System.out.println(sharedFolder.getRemotePath());
//		System.out.println(sharedFolder.getACLOwner().toString());
//		System.out.println(sharedFolder.getACLUser().toString());

		//		Session session = JCRRepository.getSession();
		////		Node root = session.getRootNode();
		//	Node sharedNode = 	session.getNode("/Share/");
		//	NodeIterator children = sharedNode.getNodes();
		//	while (children.hasNext()){
		//		Node node = children.nextNode();
		//		System.out.println(node.getPath());
		////		WorkspaceFolder folder = (WorkspaceFolder) ws.getItem(node.getIdentifier());
		//
		//	}

		//		WorkspaceFolder root = (WorkspaceFolder) ws.getItemByPath("/Workspace");
		//		System.out.println(ws.getMySpecialFolders().getPath());
		//		List<WorkspaceItem> children = ws.getMySpecialFolders().getChildren();
		//		for (WorkspaceItem item : children){
		//			
		//			WorkspaceSharedFolder sharedFolder = (WorkspaceSharedFolder) item;
		//			System.out.println(sharedFolder.getPath());
		//			if (sharedFolder.getPath().equals("/Workspace/MySpecialFolders/gcube-test-test")){
		//				List<String> users = new ArrayList<String>();
		//				users.add("test.user007");
		//				sharedFolder.share(users);
		//			}
		////				sharedFolder.move(destination)
		//			}
		//		
		////			System.out.println(sharedFolder.g);
		//		}
		//		WorkspaceFolder folder = root.createFolder("testShareGroup", "test");
		//		
		//		List<String> useers = new ArrayList<String>();
		//		useers.add("gcube-test-test");
		//		folder.share(useers);


	}




}

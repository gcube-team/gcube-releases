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
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.scope.api.ScopeProvider;

public class CreateSharedFolder {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ScopeProvider.instance.set("/gcube/devsec");
		ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("roberto.cirillo").getWorkspace();

		

//		List<String> users = new ArrayList<String>();
//		users.add("roberto.cirillo");
//		users.add("valentina.marioli");
//		
//		JCRWorkspaceFolder folder = (JCRWorkspaceFolder) ws.getItemByPath("/Home/valentina.marioli/Workspace/00000/");
//		String name = "test-" +UUID.randomUUID();
//		String description = "test";
//		String destinationFolderId = folder.getId();
//		System.out.println("destinationFolderId: " + destinationFolderId);
//		
//		WorkspaceSharedFolder sharedFolder = ws.createSharedFolder(name, description, users, destinationFolderId);
//		System.out.println(sharedFolder.getPath());
		
//		JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) ws.getItemByPath("/Home/valentina.marioli/Workspace/00000/test");
//List<WorkspaceItem> children = folder.getChildren();
//for (WorkspaceItem child: children){
//	System.out.println("getACLUser *********** " + child.getACLUser().toString());
//	System.out.println("getACLOwner *********** " +  child.getACLOwner().toString());
//}
		
		
		JCRWorkspaceItem folder = (JCRWorkspaceItem) ws.getItemByPath("/Home/roberto.cirillo/Workspace/.applications/StatisticalManager/OccFile17 07 2015 16_38_38");
		System.out.println(folder.getPublicLink(true));
		
		//		System.out.println(folder.getDisplayName());
//		
//				List<WorkspaceItem> items = folder.getLastItems(5);
//		for (WorkspaceItem item: items){
//			System.out.println(item.getName());
//		}
		

	}




}

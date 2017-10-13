package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class ShareTest {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		ScopeProvider.instance.set("/gcube/devsec");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();

//		System.out.println(ws.getRoot().getPath());
//		List<WorkspaceItem> children =  (List<WorkspaceItem>) ws.getItemByPath("/Workspace/00000/attachments/attach/").getChildren();
//		for (WorkspaceItem item: children){
//			System.out.println(item.getPath() + " - " + item.getId());
//		}

				WorkspaceFolder root = (WorkspaceFolder) ws.getItemByPath("/Workspace/00000/attachments/attach");
				System.out.println(root.getPath());
//				List<String> useers = new ArrayList<String>();
//				useers.add("valentina.marioli");
//				root.deleteACL(useers);

		//		JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder) ws.shareFolder(useers, root.getId());
		//		sharedFolder.setACL(useers, ACLType.WRITE_ALL);


		//		WorkspaceSharedFolder share = (WorkspaceSharedFolder) ws.getItemByPath("/Workspace/test02");
		//		System.out.println(share.getPath());
		//		System.out.println("list: " + share.getUsers().toString());
	}



}

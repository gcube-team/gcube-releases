package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRFile;
import org.gcube.common.scope.api.ScopeProvider;

public class RemoveDuplicateInUsers {
	static int i = 0;
	static int j = 0;

	public static void main(String[] args) throws InternalErrorException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
//			ScopeProvider.instance.set("/gcube");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();

		List<String> users = um.getUsers();
//		String user = "leonardo.candela";
		for(String user:users){
			System.out.println(user);
			JCRWorkspace ws = (JCRWorkspace) getWorkspace(user);

			try{
			getFiles(ws.getRoot(), user, ws);
			}catch (Exception e){
				System.out.println("no children");
			}
		}

		System.out.println("found " + j + "/" + i);

	}



	private static void getFiles(WorkspaceItem item, String portalLogin, JCRWorkspace ws) throws InternalErrorException {
		
//		System.out.println(item.getPath() + " is shared? " + item.isShared() + " - idSharedRoot "+ item.getIdSharedFolder() + " owner " + item.getOwner().getPortalLogin() );
		if (item.isFolder() && (item.isShared()) && (item.getOwner().getPortalLogin().equals(portalLogin))){
			if (item.getIdSharedFolder()==item.getId()){
//				System.out.println("*************** check " + item.getPath());
				JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) item;

				List<String> list = new ArrayList<String>();
				List<String> users = shared.getMembers();
				System.out.println(users);
				for (String user: users){
					if (!list.contains(user))
						list.add(user);
					else
						System.out.println("*********** remove " + user + " from " + item.getPath());
				}
				shared.setMembers(list);
				
			}
		}else if (item.isFolder() && !(item.isShared())){ 
			List<? extends WorkspaceItem> children = null;
			try{
				children = item.getChildren();
				for (WorkspaceItem child: children)
					getFiles(child, portalLogin, ws);
			}catch (Exception e){
				System.out.println("no children for " + item.getPath());
			}

		}
	}



	private static Workspace getWorkspace(String user) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

		return ws;

	}
}

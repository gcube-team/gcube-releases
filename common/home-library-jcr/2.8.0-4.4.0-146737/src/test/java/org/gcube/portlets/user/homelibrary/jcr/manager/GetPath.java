package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.List;

import javax.jcr.PathNotFoundException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class GetPath {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		ScopeProvider.instance.set("/gcube/devsec");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();
		
		


		checkPath(ws.getRoot());
		
//		System.out.println(ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/.applications").isHidden());


	}

	private static void checkPath(WorkspaceItem item) throws InternalErrorException, ItemNotFoundException, RepositoryException {

		//		if (!root.isRoot())
		//			System.out.println("parent " + root.getParent().getPath());
		System.out.println(item.getPath());

		if (item.isFolder()){
			for (WorkspaceItem child: item.getChildren()){
				System.out.println(child.getName() + " - hidden? " + child.isHidden());
//				if (child.getName().equals(".applications"))
//					child.setHidden(true);
//				System.out.println("hidden? " + child.isHidden());
//				if (!child.isFolder()){
//					System.out.println("short*** " + child.getPublicLink(true));
//					System.out.println("normal*** " + child.getPublicLink(false));
//				}
//				checkPath(child);
			
			}
		}


	}


}

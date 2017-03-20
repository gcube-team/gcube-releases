package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;

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
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class RemoveVREUsers {



	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		String user = "valentina.marioli";
		//		ScopeProvider.instance.set("/gcube/devsec");
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();
		System.out.println(ws.getRoot().getPath());
		for (WorkspaceItem child: ws.getRoot().getChildren()){
			System.out.println(child.getPath());
		}


		//		String user = "d4science.research-infrastructures.eu-gCubeApps-ORI-Manager";
		//		um.deleteAuthorizable(user);
		//		 int i =0;
		//		 for (String user: um.getUsers()){
		//			 System.out.println(i++ + ") " + user);
		//		 }

	}


}

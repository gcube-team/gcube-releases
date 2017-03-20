package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.scope.api.ScopeProvider;

public class GetPathFile {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/gcube");
		//							ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();
//System.out.println(ws.getRootId());
//		
//		JCRWorkspaceItem sharedfolder = (JCRWorkspaceItem) ws.getItemByPath("/Home/valentina.marioli/Workspace/Trash/de481d5e-3cf9-44f9-bfbe-96ef197525ae/");
//		System.out.println(sharedfolder.getPath());
		
		ws.getTrash();
		
		ws.getAllSmartFolders();
		System.out.println("*** get item by path ****");
		JCRWorkspaceFolder sharedfolder = (JCRWorkspaceFolder) ws.getItemByPath("/Home/valentina.marioli/Workspace/");
		System.out.println(sharedfolder.getPath());
		System.out.println("*** get children ****");
		for(WorkspaceItem child: sharedfolder.getChildren()){
			JCRWorkspaceItem item = (JCRWorkspaceItem) child;
			System.out.println("*** get path ****");
			System.out.println(item.getPath());
			System.out.println("");
		}


	}



}

package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.scope.api.ScopeProvider;

public class TestNewMethods {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		createLibrary();

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
	ScopeProvider.instance.set("/gcube/devNext/NextNext");
//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();
		

	WorkspaceItem root =ws.getRoot();
	
	
	WorkspaceItem folder = ws.getItemByPath("/Home/valentina.marioli/Workspace/9327906026_da729492e6_z.jpg");
	

//	WorkspaceItem reference = ws.createReference(folder.getId(), root.getId());
//	System.out.println(reference.getPath());
//	
//	
//	List<String> list = folder.getReferences();
//	for (String path: list ){
//		System.out.println(path);
//	}

	
//	WorkspaceSharedFolder vre = ws.getVREFolderByScope("/d4science.research-infrastructures.eu/gCubeApps/FAO_TunaAtlas");
//	List<WorkspaceItem> list = vre.getLastItems(5);
//	for(WorkspaceItem item: list){
////		System.out.println(item.getPath());
//		System.out.println(item.getPublicLink(true));
//	}

	}
}

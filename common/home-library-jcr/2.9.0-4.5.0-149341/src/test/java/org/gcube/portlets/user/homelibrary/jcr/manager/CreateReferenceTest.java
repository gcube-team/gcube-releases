package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.List;
import javax.jcr.PathNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;

public class CreateReferenceTest {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		ScopeProvider.instance.set("/gcube/devsec");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();

		WorkspaceFolder item = (WorkspaceFolder) ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/test01/");
		System.out.println(item.getId()+ " " + item.getName());
		WorkspaceFolder destination = (WorkspaceFolder) ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/B/");
		System.out.println(destination.getId() + " " + destination.getName());
		

//		WorkspaceItem item = ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/00000/aaa/");
//		System.out.println(item.getId()+ " " + item.getName());
//		WorkspaceItem destination = ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/00000/bbb/");
//		System.out.println(destination.getId() + " " + destination.getName());
		
		WorkspaceItem link = ws.copyAsLink(item.getId(), destination.getId());
		System.out.println("REFERENCE: " + link.getPath());
		
		List<String> list = item.getReferences();
		System.out.println("GET REFERENCE OF " + item.getPath());
		for(String ref: list){
			System.out.println(ref);
		}
		

		
		//	List<? extends WorkspaceItem> children = folder.getChildren();
//	for (WorkspaceItem item: children){
		
//	System.out.println(item.getId() + " " + item.getName());
//	WorkspaceItem copied = ws.copy(item.getId(), folder.getId());
////	WorkspaceItem copied = ws.moveItem(item.getId(), folder.getId());
//	System.out.println(copied.getPath());
//	}

	}


}

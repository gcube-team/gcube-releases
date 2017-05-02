package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.List;

import javax.jcr.PathNotFoundException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.trash.JCRWorkspaceTrashItem;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class CopyItem {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();
		
		System.out.println(ws.getRoot().getPath());

		
		WorkspaceTrashFolder trash = ws.getTrash();
		List<WorkspaceItem> children = trash.getChildren();
		for (WorkspaceItem item: children){
			
			JCRWorkspaceTrashItem trashItem = (JCRWorkspaceTrashItem) item;
			trashItem.deletePermanently();
			System.out.println("Remove "+ item.getPath());
		}

//		System.out.println(ws.getTrash().emptyTrash());
//		WorkspaceItem item = ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/00000/folder/");
		
//		 JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) ws.getItemByAbsPath("/Home/valentina.marioli/Workspace/AAAAAA");
//		 System.out.println(folder.unShare());
//		ws.copy(folder.getId(), folder.getParent().getId());
//folder.unShare();
		
		//	List<? extends WorkspaceItem> children = folder.getChildren();
//	for (WorkspaceItem item: children){
		
//	System.out.println(item.getId() + " " + item.getName());
//	WorkspaceItem copied = ws.copy(item.getId(), folder.getId());
////	WorkspaceItem copied = ws.moveItem(item.getId(), folder.getId());
//	System.out.println(copied.getPath());
//	}

	}


}

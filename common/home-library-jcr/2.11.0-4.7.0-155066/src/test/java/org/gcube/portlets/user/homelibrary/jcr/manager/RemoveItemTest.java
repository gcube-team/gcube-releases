package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
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
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.scope.api.ScopeProvider;

public class RemoveItemTest {
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
//					ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();
		
		WorkspaceItem children = ws.getItemByPath("/Workspace/Trash/3bad06d9-6f7d-4605-88be-f49c4fc5af13/B/");
	
//		
//		System.out.println(ids.size());
//		String[] strarray = (String[]) ids.toArray(new String[ids.size()]); 
//		ws.removeItems(strarray);
		
		
//	HomeLibrary.getHomeManagerFactory()
//				.getUserManager().deleteAuthorizable("d4science.research-infrastructures.eu-gCubeApps-EGI_Engage-Manager");

//		List<String> users = new ArrayList<String>();
//		users.add("roberto.cirillo");
//
//		WorkspaceSharedFolder share = ws.createSharedFolder("ccc", "", users, ws.getRoot().getId());
//share.setACL(users, ACLType.WRITE_ALL);
//
//share.unShare("roberto.cirillo");
//		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/bbb/");
//		ws.removeItem(folder.getId());
//		ws.unshare(share.getId());
//		ws.removeItem(share.getId());
//		System.out.println(folder.getACLUser());
//		List<WorkspaceItem> list = folder.getChildren();
//		for (WorkspaceItem item: list){
//			System.out.println(item.getName());
//
//		}
		
//	WorkspaceFolder wsFolder = (WorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-Parthenos/");
//		if (wsFolder.isShared()){
//		WorkspaceSharedFolder shared = (WorkspaceSharedFolder) ws.getItem(wsFolder.getIdSharedFolder());
//		String name = shared.isVreFolder()?shared.getDisplayName():wsFolder.getName();
//		System.out.println("isVreFolder? " + shared.isVreFolder() + " - name: " +name);
//		}
		
//		String name;
//		String specialFolderName = null;
//		//MANAGEMENT SHARED FOLDER NAME
//		if(wsFolder.isShared() && wsFolder.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
//	    	WorkspaceSharedFolder shared = (WorkspaceSharedFolder) wsFolder;
//	    	name = shared.isVreFolder()?shared.getDisplayName():wsFolder.getName();
//	    	
//	    	//MANAGEMENT SPECIAL FOLDER
//		}else if(wsFolder.getName().compareTo("MySpecialFolders")==0 && wsFolder.getParent()!=null && wsFolder.getParent().isRoot()){
//			//MANAGEMENT SPECIAL FOLDER
//			System.out.println("MANAGEMENT SPECIAL FOLDER NAME REWRITING AS: "+specialFolderName);
//			if(specialFolderName!=null && !specialFolderName.isEmpty())
//				name = specialFolderName;
//			else
//				name = wsFolder.getName();
//		}else
//			name = wsFolder.getName();
//		
//		System.out.println(name);
		
//		WorkspaceTrashFolder trash = ws.getTrash();
//
//		List<WorkspaceTrashItem> items = trash.listTrashItems();
//		for(WorkspaceTrashItem item: items){
//			System.out.println(item.getName() + " " + item.getId());
////			System.out.println(item.getDeletedBy());
//		}
	}

}

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
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRFile;
import org.gcube.common.scope.api.ScopeProvider;

public class UpdateMimeType {
	static int i = 0;
	static int j = 0;

	public static void main(String[] args) throws InternalErrorException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
				ScopeProvider.instance.set("/gcube");
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();

		List<String> users = um.getUsers();
		//		String user = "valentina.marioli";

		for(String user:users){

			System.out.println(user);
			JCRWorkspace ws = (JCRWorkspace) getWorkspace(user);
			getFiles(ws.getMySpecialFolders(), user, ws.getStorage());

		}

		System.out.println("found " + j + "/" + i);
	}



	private static void getFiles(WorkspaceItem item, String user, GCUBEStorage gcubeStorage) throws InternalErrorException {
		if (!item.isFolder() && (item.getOwner().getPortalLogin().equals(user))){
			try{		
				JCRExternalFile file = (JCRExternalFile) item;
				String remotePath = file.getRemotePath();
				if (remotePath!=null){

//					//					System.out.println(i++ +") " +  remotePath + " - " + file.getMimeType());
//					try{
//						//					if (i<1){
//						//														if (gcubeStorage.getMetaInfo("mimetype", remotePath)!=null){
//						System.out.println(i++ +") " +  remotePath + " - " + file.getMimeType());
//						gcubeStorage.setMetaInfo("mimetype", file.getMimeType(), remotePath);
//						System.out.println(j++ +") " +  remotePath + " - " + file.getMimeType());
//						//}
//						//else
//						//System.out.println("mimetype not set for " + remotePath);
//
//					}catch (RemoteBackendException e){
//						System.out.println(" file not found in storage " + item.getPath());
//
//					}



				}
			}catch (Exception e){
				System.out.println(" Error getting file " + item.getPath());
			}
		}
		else if (item.isFolder()){ 
			List<? extends WorkspaceItem> children = null;
			try{
				children = item.getChildren();
				for (WorkspaceItem child: children)
					getFiles(child, user, gcubeStorage);
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

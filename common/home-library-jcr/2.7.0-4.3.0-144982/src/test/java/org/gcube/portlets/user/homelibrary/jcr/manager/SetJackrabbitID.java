package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.scope.api.ScopeProvider;

public class SetJackrabbitID {
	static int i = 0;

	public static void main(String[] args) throws InternalErrorException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/gcube");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();

		
		//		String user = "valentina.marioli";

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("check-storage-jackrabbitID00.txt"), "utf-8"));

			List<String> users = um.getUsers();
			
			for(String user:users){

				System.out.println(user);
				JCRWorkspace ws = (JCRWorkspace) getWorkspace(user);
				getFiles((JCRWorkspaceItem) ws.getRoot(), user, ws.getStorage(), false, writer);

			}

		} catch (IOException ex) {
			// report
		} finally {
			try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
		}
		System.out.println("found " + i + " files");
	}



	private static void getFiles(JCRWorkspaceItem item, String user, GCUBEStorage gcubeStorage, Boolean found, Writer writer) throws InternalErrorException {
		if (!found && !item.isFolder() && (item.getOwner().getPortalLogin().equals(user))){
			try{		
				JCRExternalFile file = (JCRExternalFile) item;
				String remotePath = file.getRemotePath();
				if (remotePath!=null){
					try{
						System.out.println(i++ +") " +  remotePath + " - " + user);
//						gcubeStorage.setMetaInfo("jr_id", item.getId(), remotePath);
//						item.setStorageID(gcubeStorage.getStorageId(remotePath));
					}catch (Exception e){
						System.out.println(" file not found in storage " + item.getPath());
					}

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
					getFiles((JCRWorkspaceItem)child, user, gcubeStorage, found, writer);
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

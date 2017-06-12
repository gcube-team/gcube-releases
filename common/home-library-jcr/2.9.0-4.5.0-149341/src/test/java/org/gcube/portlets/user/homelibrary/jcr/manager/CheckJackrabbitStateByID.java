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
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;

public class CheckJackrabbitStateByID {
	static int i = 0;
	static int j = 0;

	public static void main(String[] args) throws InternalErrorException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/gcube");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();

		List<String> users = um.getUsers();
		//		String user = "valentina.marioli";

		Writer writer = null;
		String myUser= "test.mytest02";
//		getWorkspace(myUser);
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("check-storage.txt"), "utf-8"));
			boolean flag = false;
			for(String user:users){
				
//				if (user.equals(myUser)){				
//					um.deleteAuthorizable(myUser);
//					flag = true;
//					continue;
//				}
//				if (flag){
					System.out.println(user);
					JCRWorkspace ws = (JCRWorkspace) getWorkspace(user);
					getFiles(ws, ws.getRoot(), user, writer);
					//			ws.getTrash().emptyTrash();

//				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
		}
		System.out.println("found " + j + "/" + i + " different");
	}



	private static void getFiles(JCRWorkspace ws, WorkspaceItem item, String user, Writer writer) throws InternalErrorException {
		if (!item.isFolder() && (item.getOwner().getPortalLogin().equals(user))){
			String storageID = null;
			i++;
			try{		
				JCRExternalFile file = (JCRExternalFile) item;

				storageID = file.getStorageId();

				if (storageID == null){
					System.out.println(j++ + "/" + i + " no storage id");
					JCRWorkspaceItem myItem = (JCRWorkspaceItem) item;
					writer.write(myItem.getAbsolutePath()  + "\t" + user);
					writer.write("\n");

					String remotePath = file.getRemotePath();
					if (remotePath!=null){
						storageID = ws.getStorage().getStorageId(remotePath);
						file.setStorageId(storageID);

						System.out.println("found! " + remotePath);
					}

					//				String remotePath = file.getRemotePath();
					//				if (remotePath!=null && !remotePath.isEmpty()){
					//					System.out.println(i++ +") " +  remotePath + " - " + user);
					//					System.out.println("found " + j + "/" + i++ + " files not in storage");
					//					try{
					//						String link = item.getPublicLink(false);
					//						System.out.println(link);
					//						
					//					}catch (Exception e){

					//						item.remove();
					//						System.out.println(" file not found in storage " + item.getPath());
					//						System.out.println("found " + j++ + "/" + i + " files not in storage");
					//						
					//						writer.write(remotePath  + "\t" + user);
					//						writer.write("\n");
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
					getFiles(ws, child, user, writer);
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

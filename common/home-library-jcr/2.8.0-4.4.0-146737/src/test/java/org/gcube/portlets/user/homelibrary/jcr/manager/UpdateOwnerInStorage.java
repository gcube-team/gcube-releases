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
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.scope.api.ScopeProvider;


public class UpdateOwnerInStorage {
	static int i = 0;
	static int j = 0;

	public static void main(String[] args) throws InternalErrorException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		//				ScopeProvider.instance.set("/gcube");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();

		List<String> users = um.getUsers();
		//		String user = "valentina.marioli";

		Writer writer = null;
		Writer writerOk = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("check-storage-prod-bad.txt"), "utf-8"));
			writerOk = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("check-storage-prod-ok.txt"), "utf-8"));

//			boolean flag = false;
			for(String user:users){
				System.out.println(user);
//				if (user.equals("Gerhard.Brey"))
//					flag = true;
//
//				if (flag){
					try{
					JCRWorkspace ws = (JCRWorkspace) getWorkspace(user);
					getFiles(ws.getRoot(), user, ws.getStorage(), false, writer, writerOk);
					}catch(Exception e) {
						System.out.println(e.getMessage());
					}
//				}

			}

		} catch (Exception ex) {
			// report
		} finally {
			try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
		}
		System.out.println("found " + j + "/" + i + " different");
	}



	private static void getFiles(WorkspaceItem item, String user, GCUBEStorage gcubeStorage, Boolean found, Writer writer, Writer writerOk) throws InternalErrorException {
		if (!found && !item.isFolder() && (item.getOwner().getPortalLogin().equals(user))){
			try{		
				JCRExternalFile file = (JCRExternalFile) item;
				String remotePath = file.getRemotePath();
				if (remotePath!=null){

					//					System.out.println(i++ +") " +  remotePath + " - " + file.getMimeType());
//					try{
//						//					if (i<1){
//						//														if (gcubeStorage.getMetaInfo("mimetype", remotePath)!=null){
//						System.out.println("found " + j + "/" + i + " different");
//
//						System.out.println(i++ +") " +  remotePath + " - " + user);
//
////						String ownerStorage = gcubeStorage.getMetaInfo("owner", remotePath);
//						if (!ownerStorage.equals(user)){
//
//							System.out.println(j++ +") " +  remotePath + " - " + ownerStorage);
////							gcubeStorage.setMetaInfo("owner", user, remotePath);
//							writer.write(remotePath  + "\t" + user + "\t" + ownerStorage);
//							writer.write("\n");
//							//							found = true;
//						}else{
//							writerOk.write(remotePath  + "\t" + user + "\t" + ownerStorage);
//							writerOk.write("\n");
//						}
//							
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
					getFiles(child, user, gcubeStorage, found, writer, writerOk);
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

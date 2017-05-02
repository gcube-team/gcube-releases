package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.Validate;
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
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

public class CheckJackrabbitState {
	static int i = 0;
	static int j = 0;

	public static void main(String[] args) throws InternalErrorException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		//		ScopeProvider.instance.set("/gcube");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		//		UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
		//
		//		List<String> users = um.getUsers();
		//		//		String user = "valentina.marioli";
		//
		//		Writer writer = null;
		//
		try {

			readFile("/home/valentina/Downloads/vres.txt");


			//			writer = new BufferedWriter(new OutputStreamWriter(
			//					new FileOutputStream("test-differenze.txt"), "utf-8"));
			//
			//			for(String user:users){
			//
			//				System.out.println(user);
			//				JCRWorkspace ws = (JCRWorkspace) getWorkspace(user);
			//				getFiles( ws.getStorage(), ws.getRoot(), user, writer);
			//				//			ws.getTrash().emptyTrash();
			//			}

		} catch (IOException ex) {
			// report
		} finally {
			//			try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
		}
		System.out.println("found " + j + "/" + i + " different");
	}



	private static void getFiles( GCUBEStorage gcubeStorage, WorkspaceItem item, String user, Writer writer) throws InternalErrorException {
//		if (!item.isFolder() && (item.getOwner().getPortalLogin().equals(user))){
		System.out.println(item.getPath());
		if (!item.isFolder()){
			System.out.println("not a folder");
			try{		
				JCRExternalFile file = (JCRExternalFile) item;

				String remotePath = file.getRemotePath();
				String remotePathName = remotePath.substring(remotePath.lastIndexOf('/') + 1);
				//				System.out.println(remotePath);
				if (remotePath!=null && !remotePath.isEmpty()){
String inStorage = null;
					//					String inStorage = gcubeStorage.getRemotePathByStorageId(item.getStorageID());
					String inStorageName = inStorage.substring(inStorage.lastIndexOf('/') + 1);
					if (!inStorageName.equals(remotePathName)){
						System.out.println("difference " + j++ + "/" + i + " found.");

						writer.write(remotePath  + "\t" + inStorage + "\t" + user);
						writer.write("\n");
					}
					//					System.out.println(i++ +") " +  remotePath + " - " + user);
					////					System.out.println("found " + j + "/" + i++ + " files not in storage");
					//					try{
					////						String link = item.getPublicLink(false);
					////						System.out.println(link);
					//						
					//					}catch (Exception e){
					//						
					////						item.remove();
					////						System.out.println(" file not found in storage " + item.getPath());
					//						System.out.println("found " + j++ + "/" + i + " files not in storage");
					//						
					//						writer.write(remotePath  + "\t" + gcubeStorage.getRemotePathByStorageId(item.getStorageID())+ "\t" + user);
					//						writer.write("\n");
					//					}

				}
			}catch (Exception e){
				System.out.println(" Error getting file " + item.getPath());
			}
		} else if (item.isFolder()){ 
			System.out.println("is a folder");
			List<? extends WorkspaceItem> children = null;
			try{
				children = item.getChildren();
				for (WorkspaceItem child: children)
					getFiles(gcubeStorage, child, user, writer);
			}catch (Exception e){
				System.out.println("no children for " + item.getPath());
			}

		}
	}



	private static void readFile(String file) throws IOException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, RepositoryException, InterruptedException {

		Writer writer = null;
		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("test-differenze-VRE.txt"), "utf-8"));

			// Open the file
			FileInputStream fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String vreScope;

			while ((vreScope = br.readLine()) != null)   {
				System.out.println (vreScope);
				String vre = getVRENameByScope(vreScope);
				String verManager = vre+ "-Manager";
				JCRWorkspace ws = (JCRWorkspace) getWorkspace(verManager);
				try{
					WorkspaceSharedFolder vreFolder = ws.getVREFolderByScope(vreScope);
				
					getFiles( ws.getStorage(), vreFolder, verManager, writer);

				}catch (Exception e){
					System.out.println("******** Error getting VRE folder " + vre);
				}
			}

			br.close();


		} catch (IOException ex) {
			// report
		} finally {
			try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
		}


	}



	private static Workspace getWorkspace(String user) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

		return ws;

	}

	private static String getVRENameByScope(String scope) {

		Validate.notNull(scope, "scope must be not null");

		String newName;
		if (scope.startsWith(JCRRepository.PATH_SEPARATOR))			
			newName = scope.replace(JCRRepository.PATH_SEPARATOR, "-").substring(1);
		else
			newName = scope.replace(JCRRepository.PATH_SEPARATOR, "-");
		return newName;
	}



}

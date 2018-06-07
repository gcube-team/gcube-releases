package org.gcube.portlets.user.homelibrary.jcr.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
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
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;

public class MultiThreadUpload implements Runnable {

	private Thread t;
	private String threadName;

	MultiThreadUpload( String name) {
		threadName = name;
		System.out.println("Creating " +  threadName );
	}

	public void run() {
		System.out.println("Running " +  threadName );
		try {

			String scope = "/gcube/devsec";
			String username = "valentina.marioli";

			ScopeProvider.instance.set(scope);

			JCRWorkspace ws = (JCRWorkspace) getWorkspace(username);

			WorkspaceItem item = ws.getItemByPath("/Workspace/versions/version.png");
			
			
//			String remotePath = "/Home/valentina.marioli/Workspace/versions/version.png";
//			System.out.println("*** " + ws.getStorage().getRemotePathByStorageId(item.getStorageID()));
			File initialFile = new File("/home/valentina/Downloads/AAA-version/1.0/version.png");
			InputStream fileData = FileUtils.openInputStream(initialFile);
			
//			ws.getStorage().putStream(fileData, remotePath);
			ws.updateItem(item.getId(), fileData);
//			System.out.println("*** " + ws.getStorage().getStorageId(remotePath));
			System.out.println("done");
//			Thread.sleep(1000);


		}catch (InterruptedException | InsufficientPrivilegesException | WorkspaceFolderNotFoundException | ItemAlreadyExistException | WrongDestinationException | ItemNotFoundException | InternalErrorException | HomeNotFoundException | UserNotFoundException | IOException | RepositoryException e) {
			System.out.println("Thread " +  threadName + " interrupted." );
			e.printStackTrace();
		}
		System.out.println("Thread " +  threadName + " exiting.");
	}

	public void start () {
		System.out.println("Starting " +  threadName );
		if (t == null) {
			t = new Thread (this, threadName);
			t.start ();
		}
	}



	private static Workspace getWorkspace(String user) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/gcube");
		//							ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

		return ws;

	}
}




//	public static void main(String args[]) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException {
//
//		String scope = "/gcube/devsec";
//		String username = "valentina.marioli";
//
//		ScopeProvider.instance.set(scope);
//
//		Thread t = Thread.currentThread();
//		t.setName("Thread principale");
//		t.setPriority(10);
//		System.out.println("Thread in esecuzione: " + t);
//		try {
//			for (int n = 5; n > 0; n--) {
//
//				JCRWorkspace ws = (JCRWorkspace) getWorkspace(username);
//
//				System.out.println("Upload file n. " + n);
//				WorkspaceItem item = ws.getItemByPath("/Workspace/versions/version.png");
//				File initialFile = new File("/home/valentina/Downloads/AAA-version/1.0/version.png");
//				InputStream fileData = FileUtils.openInputStream(initialFile);
//				ws.updateItem(item.getId(), fileData);
//				System.out.println("done");
//				Thread.sleep(1000);
//			}
//		}
//		catch (InterruptedException e) {
//			System.out.println("Thread interrotto");
//		}
//	}
//
//

//}

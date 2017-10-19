package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

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
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;

public class TestGetChildren100 {
	private static final int USERS = 1; // maximum # of threads


	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, WrongItemTypeException {

		try {

			createLibrary();
			System.out.println(Thread.activeCount());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException, WrongItemTypeException {
		ScopeProvider.instance.set("/gcube");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		String name = "100KB";
		final String file = "/home/valentina/Downloads/DSC_0371.jpg";
		final List<Long> times = new ArrayList<Long>();

		for(int i=0; i< USERS; i++){

			final String user = "test-"+i;

			JCRWorkspace ws = (JCRWorkspace) HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(user).getWorkspace();

			final WorkspaceFolder folder;		

			//			WorkspaceSharedFolder shareFolder = null;

			//			if (ws.exists(name, ws.getRoot().getId())){
			folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/"+ name);
			//			System.out.println(folder.getPath());
			//			}
			//			else{
			//				folder = ws.createFolder(name, "test", ws.getRoot().getId());
			//				List<String> users = new ArrayList<String>();
			//				for(int k=0; k< 100; k++){
			//					users.add("test-"+k);					
			//				}
			//				WorkspaceSharedFolder shareFolder = folder.share(users);
			//				shareFolder.setACL(users, ACLType.WRITE_ALL);
			//				
			//			}


			new Thread("" + i){
				public void run(){
					//					try {
					//						Thread.sleep((long)(Math.random() * 1000));
					//					} catch (InterruptedException e1) {
					//						// TODO Auto-generated catch block
					//						e1.printStackTrace();
					//					}


					System.out.println("Thread: " + getName() + " running with user " + user);

					long startTime = System.currentTimeMillis();
					System.out.println("Thread: " + getName() + " running with user " + user);

					List<WorkspaceItem> children = null;
					try {
						children = folder.getChildren();
						for (WorkspaceItem child: children)
							System.out.println(child.getName());


					} catch (InternalErrorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}



					long stopTime = System.currentTimeMillis();
					long elapsedTime = stopTime - startTime;
					times.add(elapsedTime);
					System.out.println("Thread: " + getName() + " running with user " + user + " - Time: " + elapsedTime);
					System.out.println(times.toString());

					Long max = Collections.max(times);
					System.out.println("MAX " + max);
					times.clear();
					times.add(max);
					System.out.println("SIZE " + children.size());

				}
			}.start();


		}	
	}



}





package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

import ij.gui.WaitForUserDialog;

public class TestUpload100 {
	private static final int USERS = 20; // maximum # of threads
	protected static final int FILES = 100;
		protected static final String SIZE = "100KB";
//	protected static final String SIZE = "1MB";
	//	protected static final String SIZE = "5MB";
	static boolean isShared = false;


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

		//100 KB
				final String file = "/home/valentina/Downloads/DSC_0371.jpg";
		//1 MB
//		final String file = "/home/valentina/Downloads/IMG_20160419_104813.jpg";
		//5 mb
		//		final String file = "/home/valentina/Downloads/Snake_River_(5mb).jpg";

		String name = SIZE;

		final List<Long> times = new ArrayList<Long>();

		for(int i=0; i< USERS; i++){

			final String user = "test-"+i;

			JCRWorkspace ws = (JCRWorkspace) HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(user).getWorkspace();

			final WorkspaceFolder folder;		
			//			WorkspaceSharedFolder shareFolder = null;

			if (ws.exists(name, ws.getRoot().getId()))
				folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/"+ name);
			else{
				folder = ws.createFolder(name, "test", ws.getRoot().getId());
				if (isShared){
					List<String> users = new ArrayList<String>();
					for(int k=0; k< USERS; k++){
						users.add("test-"+k);					
					}
					WorkspaceSharedFolder shareFolder = folder.share(users);
					shareFolder.setACL(users, ACLType.WRITE_ALL);
				}
			}



			new Thread("" + i){
				public void run(){
					//					try {
					//						Thread.sleep((long)(Math.random() * 1000));
					//					} catch (InterruptedException e1) {
					//						// TODO Auto-generated catch block
					//						e1.printStackTrace();
					//					}
					long startTime = System.currentTimeMillis();
					System.out.println("Thread: " + getName() + " running with user " + user);

					for(int j=0; j<FILES; j++){
						InputStream in = null;
						try{

							in  = new FileInputStream(file);	
							FolderItem fileItem = WorkspaceUtil.createExternalFile(folder, getName() + "-USERS-"+ USERS + "-J-" + j + "-" + UUID.randomUUID().toString()+ ".jpg", "de", null, in);

						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							if (in!=null)
								try {
									in.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	

						}

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


				}
			}.start();


		}	
	}



}





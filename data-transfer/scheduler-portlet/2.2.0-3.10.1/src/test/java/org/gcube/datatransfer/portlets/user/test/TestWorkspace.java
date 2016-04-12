package org.gcube.datatransfer.portlets.user.test;


import java.util.concurrent.TimeUnit;
import org.gcube.datatransfer.portlets.user.server.workers.WorkspaceWorker;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;



public class TestWorkspace {

	public static void main(String[] args) throws InternalErrorException{
		Workspace workspace = getWorkspace("nikolaos.drakopoulos","/gcube/devsec/devVRE");
		if(workspace ==null ){System.out.println("workspace=null");return;}
		
		
	//	testingWorkspace(workspace,null);
		
		//testingWorkspace(workspace,"30002ff6-ffde-4fe5-94de-15d5942c5d59");
		//testingWorkspace(workspace,"22d57d23-9c68-42bf-b61f-c0208fc2ad50");

	}
	
	
	static public Workspace getWorkspace(String username, String scope){
		HomeManagerFactory factory=null;
		HomeManager manager=null;
		User user=null;
		try {
			factory = HomeLibrary.getHomeManagerFactory();
			manager = factory.getHomeManager();
			user = manager.createUser(username);
		} catch (InternalErrorException e) {
			e.printStackTrace();
		}
		
		Home home=null;
		try {
			home = manager.getHome(user);
		} catch (InternalErrorException e) {
			e.printStackTrace();
		} catch (HomeNotFoundException e) {
			e.printStackTrace();
		} 

		if(home==null){
			System.out.println("home=null");
			return null;
		}
		
		Workspace w=null;

		try {
			w = home.getWorkspace();
		} catch (WorkspaceFolderNotFoundException e) {
			e.printStackTrace();
		} catch (InternalErrorException e) {
			e.printStackTrace();
		}
		
		if(w!=null)return w;
		else return null;
	}
	
	
	static public void testingWorkspace(Workspace w, String folderId) throws IllegalArgumentException,InternalErrorException {
		long startTime = System.currentTimeMillis();
		System.out.println("start time - " + startTime);
		
		WorkspaceFolder root=null;
		if(folderId==null){
			root = w.getRoot();
		}
		else {
			try {
				
				root = (WorkspaceFolder) w.getItem(folderId);
			} catch (ItemNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(root==null){System.out.println("GET WORKSPACE MANUALLY: root= null");return ;}
		else System.out.println("GET WORKSPACE MANUALLY: fold="+root.getId());
		
		
		
		//	if(true)return ;
		
		WorkspaceWorker wsWorker = new WorkspaceWorker();
		System.out.println("GET WORKSPACE MANUALLY: UrlWebDav()="+w.getUrlWebDav());
		
		FolderDto folder= wsWorker.createTree(root, w.getUrlWebDav());		
		if(folder==null){System.out.println("GET WORKSPACE MANUALLY: folder= null");return ;}
		else {
			wsWorker.printFolder(folder,0);
		}
		
		System.out.println("Returning:");
		Long endTime = System.currentTimeMillis() - startTime;
		String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
		System.out.println("end time - " + time);
		
	}
	
		

	
}

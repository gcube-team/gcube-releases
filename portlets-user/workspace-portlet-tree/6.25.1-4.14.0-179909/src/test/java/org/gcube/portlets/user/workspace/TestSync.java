/**
 * 
 */
package org.gcube.portlets.user.workspace;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jan 29, 2014
 *
 */
public class TestSync {
	
	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
	public static String TEST_USER = "francesco.mangiacrapa";
	public static String FOLDER_SYNC = "d6dae663-91d1-4da7-b13d-959de6fb2f86"; //TestSync is root Folder
	
	public static String FOLDER_A = "983d4ab9-e8fd-4c6d-869e-734a730e3e50";
	public static String FOLDER_B = "94995b10-6fcb-4ed8-a3ea-4a6dd7d33a7a";
	public static String FOLDER_C = "e945fcfd-9da9-45a0-98a9-d1940d1720bb";
	
	protected static int i=0;
	protected static int foldersCounter = 0;
	protected static int sharedFoldersCounter = 0;
	protected static int folderItemCounter = 0;
	protected static int othersCounter = 0;
	
	public static void main(String[] args) {
		
		ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
		ScopeProvider.instance.set(scope.toString());
		
		System.out.println("init HL");
		try {
			
			Workspace ws = getWorkspace();
//			List<? extends WorkspaceItem> children = ws.getItem(FOLDER_A).getChildren();
//			for (WorkspaceItem workspaceItem : children) {
//				printItem(workspaceItem);
//			}
			WorkspaceItem item_40MB = ws.getItem(FOLDER_A).getChildren().get(0);
			threadCopy(ws, item_40MB, FOLDER_B);

			System.out.println("THREAD MAIN SLEEPING 10 sec.");
			Thread.sleep(10000);
			System.out.println("THREAD MAIN ACTIVE..");
//			WorkspaceItem cp_item_260MB = ws.getItem(FOLDER_B).getChildren().get(0);

			List<? extends WorkspaceItem> children = ws.getItem(FOLDER_B).getChildren();
			System.out.println("FOLDER_B children..");
			for (WorkspaceItem workspaceItem : children) {
				printItem(workspaceItem);
			}
			
			threadMove(ws, children.get(0), FOLDER_C);

			Thread.sleep(60000);
			System.out.println("THREAD MAIN TERMINATED");
//			retrieveFirstLevel(ws);
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	public static void threadCopy(final Workspace ws, final WorkspaceItem item, final String destinationFolderId){
		
		new Thread(){
			@Override
			public void run() {
				try {
					long time = System.currentTimeMillis();
					long diff;
					System.out.println("Starting copy "+item.getName()+", time: "+time);
					ws.copy(item.getId(), destinationFolderId);
					diff = (System.currentTimeMillis()-time)/1000;
					System.out.println("End copy! [time diff: "+diff+" sec.]");
				} catch (Exception e) {
					System.out.println("Copy Exception: "+e.getMessage());
				}
			}
		}.start();
	}
	
	
	public static void threadMove(final Workspace ws, final WorkspaceItem item, final String destinationFolderId){
		
		new Thread(){
			@Override
			public void run() {
				try {
					long time = System.currentTimeMillis();
					long diff;
					System.out.println("Starting move "+item.getName()+", time: "+time);
					ws.moveItem(item.getId(), destinationFolderId);
					diff = (System.currentTimeMillis()-time)/1000;
					System.out.println("End move! [time diff: "+diff+" sec.]");
				} catch (Exception e) {
					System.out.println("Move Exception: "+e.getMessage());
				}
			}
		}.start();
	}
	
	public static Workspace getWorkspace() throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		
		System.out.println("Get Workspace scope: "+DEFAULT_SCOPE + " username: "+TEST_USER);
		ScopeProvider.instance.set(DEFAULT_SCOPE);
		Workspace workspace = HomeLibrary.getUserWorkspace(TEST_USER);
		return workspace;
	}
	
	
	private static void retrieveFirstLevel(Workspace ws){
		
		try {

			//TEST TIME
			Long startTime =  System.currentTimeMillis();
			Long endTime = System.currentTimeMillis() - startTime;
			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
			
			startTime =  System.currentTimeMillis();
			System.out.println("Start get root at time: "+startTime);
//			WorkspaceItem root = ws.getItem(ITEMID);
			WorkspaceItem root = ws.getRoot();
	
			System.out.println("start get children");
			List<? extends WorkspaceItem> children = root.getChildren();
			System.out.println("tree getChildren() returning "+children.size()+" elements in " + time);
			
			System.out.println("children size: "+children.size());
			

			for (WorkspaceItem workspaceItem : children) {
				printItem(workspaceItem);
			}
			
			//TEST TIME
			endTime = System.currentTimeMillis();
			long difference = endTime - startTime;
			time = String.format("%d msc %d sec", difference, TimeUnit.MILLISECONDS.toSeconds(difference));
			System.out.println("End time is "+endTime+ " difference is "+time);
			System.out.println("Folders are: "+foldersCounter);
			System.out.println("Shared Folders are: "+sharedFoldersCounter);
			System.out.println("Folder Item are: "+folderItemCounter);
			System.out.println("Others are: "+othersCounter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printItem(WorkspaceItem workspaceItem) throws InternalErrorException{
		
		switch (workspaceItem.getType()) {
		
		case FOLDER:
			
			WorkspaceFolder folder = (WorkspaceFolder) workspaceItem;

			System.out.println(++i+") "+folder.getId() +" folder name: "+folder.getName() + " owner "+folder.getOwner());
			foldersCounter++;
			
			break;

		case SHARED_FOLDER:
			
			WorkspaceSharedFolder shared = (WorkspaceSharedFolder) workspaceItem;

			System.out.println(++i+")  "+shared.getId() +" shared folder name: "+shared.getName() + " owner "+shared.getOwner()+" isVRE "+shared.isVreFolder() +" ACLOwner: "+shared.getACLOwner()+" AclUser: "+shared.getACLUser());
			
//			System.out.println(++i+") shared folder name: "+shared.getName() + " owner "+shared.getOwner().getPortalLogin());
			sharedFoldersCounter++;
			
			break;
			
		case FOLDER_ITEM:
			
			FolderItem folderItem = (FolderItem) workspaceItem;
			
			System.out.println(++i+") folderItem id: "+folderItem.getId() +", name: "+folderItem.getName() + ", own: "+folderItem.getOwner());
			folderItemCounter++;
			
			break;
		default:
			System.out.println(++i+") DEFAULT - item id: "+workspaceItem.getId() +", name: "+workspaceItem.getName() + ", own: "+workspaceItem.getOwner());
			othersCounter++;
			break;
		}
		
	}
}

//TODO IT MUST BE MOVED TO SHUB
///**
// * 
// */
//package org.gcube.portlets.user.workspace;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
//import org.gcube.common.homelibrary.home.HomeLibrary;
//import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
//import org.gcube.common.homelibrary.home.workspace.Workspace;
//import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
//import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
//import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
//import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
//import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.common.scope.impl.ScopeBean;
//import org.gcube.portlets.user.workspace.client.model.FileGridModel;
//
///**
// * 
// * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
// * Oct 28, 2015
// */
///*
// * 
// * 
// * IT MUST BE MOVED TO SHUB
// * 
// * 
// * 
//public class DeleteTest {
//	
//	
//	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
////	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps/DESCRAMBLE";
//	public static String TEST_USER = "francesco.mangiacrapa";
//	
//	public static String PARENT_ID = "a9ead58c-d645-4699-addd-5c6e5f1aff06";
//	private static Workspace ws;
//	
//	private static GWTWorkspaceBuilder builder =   new GWTWorkspaceBuilder();
//	
//	public static void main(String[] args) {
//
//		try {
//
//			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
//			ScopeProvider.instance.set(scope.toString());
//			
//			ws = HomeLibrary
//					.getHomeManagerFactory()
//					.getHomeManager()
//					.getHome(TEST_USER)
//					.getWorkspace();
//			//
//
//			final List<String> children = getIdsOnlyFolderForID(PARENT_ID);
//
//			
//			if(children.size()<2){
//				System.err.println("children size < 2, add others children");
//				return;
//			}
//
//			final int medium = children.size()/2;
//
//			Thread t1 = new Thread("T1"){
//				public void run() {
//					System.out.println("T1 start...");
//					removeRange(children, 0, medium);
//					System.out.println("T1 end");
//					
//				};
//			};
//			
//			t1.start();
//			t1.join();
//
//			List<? extends WorkspaceItem> childrenNEW = getChildrenForID(PARENT_ID);
//			
//			print(childrenNEW);
//			
//			
//			Thread t2 = new Thread("T2"){
//				public void run() {
//					System.out.println("T2 start...");
//					removeRange(children, medium, children.size());
//					System.out.println("T2 end");
//				};
//			};
//			
//			t2.start();
//			t2.join();
//
//			childrenNEW = getChildrenForID(PARENT_ID);
//			print(childrenNEW);
//
//
//			System.out.println("\n\nDONE!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	private static void print(final List<? extends WorkspaceItem> children){
//		int i=0;
//		System.out.println("\n\n");
//		for (WorkspaceItem workspaceItem : children) {
//
//			if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER) || workspaceItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
//				
//				WorkspaceFolder folder = (WorkspaceFolder) workspaceItem;
//
//				try {
//					System.err.println(++i+") folder id: "+folder.getId() +", folder name: "+folder.getName());
//				} catch (InternalErrorException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}else{
//				
//			
//				if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
//				
//				FolderItem folderItem = (FolderItem) workspaceItem;
//									
//				try {
//					System.err.println(++i+") folderItem id: "+folderItem.getId() +", name: "+folderItem.getName() + ", size: "+folderItem.getLength());
//				} catch (InternalErrorException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				}
//				
//			}
//		}
//		
//		System.out.println("\n\n");
//	}
//	
//	private static List<? extends WorkspaceItem> getChildrenForID(String id){
//		
//		WorkspaceFolder folder;
//		try {
//			System.out.println("start getItem on id "+id);
//			folder = (WorkspaceFolder) ws.getItem(id);
//			System.out.println("start get children on folder "+folder.getName());
//			final List<? extends WorkspaceItem> children = folder.getChildren();
//			
//			builder.buildGXTListFileGridModelItem(folder.getChildren(), null);
//			
////			builder.buildGXTFileGridModelItem(folder, null);
//			
//			System.out.println("children size: "+children.size());
//			return children;
//		} catch (ItemNotFoundException | InternalErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	private static List<String> getIdsForID(String id){
//		
//
//		try {
//			List<? extends WorkspaceItem> children = getChildrenForID(id);
//			List<String> ids = new ArrayList<String>(children.size());
//			for (WorkspaceItem workspaceItem : children) {
//				ids.add(workspaceItem.getId());
//			}
//			return ids;
//		} catch (InternalErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	private static List<String> getIdsOnlyFolderForID(String id){
//		
//
//		try {
//			List<? extends WorkspaceItem> children = getChildrenForID(id);
//			List<String> ids = new ArrayList<String>(children.size());
//			for (WorkspaceItem workspaceItem : children) {
//				if(workspaceItem.isFolder())
//					ids.add(workspaceItem.getId());
//			}
//			return ids;
//		} catch (InternalErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	
//	
//	private static void removeRange(List<String> children, int start, int end) {
//		
//		System.out.println("start "+start);
//		System.out.println("end "+end);
////		System.out.println("(end - start)"+(end - start));
//	
//		List<String> sub = children.subList(start, end);
//		String[] array = new String[sub.size()];
//		array = sub.toArray(array);
//		
//		System.out.println("array lenght: "+array.length);
//		
//		for (String id : array)
//			System.out.println("Removing "+id);
//		
//		try {
//			ws.removeItems(array);
//			
//			System.out.println("Remove OK ");
//		} catch (ItemNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InsufficientPrivilegesException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InternalErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
//
//*/

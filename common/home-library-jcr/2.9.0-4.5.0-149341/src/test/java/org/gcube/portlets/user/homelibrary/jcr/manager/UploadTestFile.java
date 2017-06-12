package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryAdd;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryDelete;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRemoval;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQueryBuilder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalImage;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRImage;
import org.gcube.common.homelibrary.jcr.workspace.trash.JCRWorkspaceTrashFolder;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class UploadTestFile {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		//		ScopeProvider.instance.set("/gcube");
//				ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		//		SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");
		SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");


		String user = "valentina.marioli";

		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();
		
//		System.out.println(ws.getTrash().getPath());
		
//		System.out.println("getDiskUsageByUser "+ ws.getStorage().getDiskUsageByUser());
//		System.out.println("getTotalItemsByUser "+ ws.getStorage().getTotalItemsByUser());

		//		System.out.println(ws.getRoot().getPath());


		//		String path = "/Workspace/stress-test-multiple-upload";
		//				WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath(path);
		//			System.out.println(ws.getRoot().getPath());
		//		
		//		List<WorkspaceItem> list = folder.getChildren();
		//		for (WorkspaceItem item:list){
		//			System.out.println(item.getName());
		//		}

		//		String path = "/Workspace/AAA/aaa/novice_drustvo_delovna_akcija_03_2014.jpg";
		//		WorkspaceItem item = ws.getItemByPath(path);
		//		
		//		System.out.println(item.getRemotePath());
		//		

		////		ws.copy(item.getId(), UUID.randomUUID().toString(), item.getParent().getId());
		//		
		//

		//		final WorkspaceFolder folder = ws.createFolder("BBB", "desc", ws.getRoot().getId());
		String pathFolder = "/Workspace/PreProd";
		//		String pathsubFolder = "/Workspace/BBB/move/copy";
		final WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath(pathFolder); 
		//		final WorkspaceFolder subfolder = (WorkspaceFolder) ws.getItemByPath(pathsubFolder); 

		//		List<WorkspaceItem> children = folder.getChildren();
		//		for(WorkspaceItem child:children){
		//			System.out.println(child.getPublicLink(true));
		//		}
		//			try{
		//			child.remove();
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//				
		//		}

	
		//		for(int i=0; i<1; i++){
		//			new Thread("" + i){
		//				public void run(){
		//					System.out.println("Thread: " + getName() + " running");

		List<String> ids = new ArrayList<String>();
		
//		for(int i=0; i<5; i++){
			InputStream in = null;
			try{
//				Thread.sleep((long)(Math.random() * 1000));
				in  = new FileInputStream("/home/valentina/Downloads/managingsharing.pdf");	

				String itemName = WorkspaceUtil.getUniqueName("managingsharing.pdf", folder);
				System.out.println(itemName);
//
//				FolderItem fileItem = WorkspaceUtil.createExternalFile(folder, itemName, "tab", in);
//				ids.add(fileItem.getId());

				
				ws.createExternalFile(itemName, "", null, in, folder.getId());
				//							FolderItem fileItem = WorkspaceUtil.createExternalFile(folder, "img-"+ UUID.randomUUID().toString()+ ".jpg", "de", in);

				//							System.out.println("*************** getStorageID " +fileItem.getStorageId());
//				System.out.println("*************** getName " +itemName.getName());
				//							if (fileItem!=null){
				//								WorkspaceItem mycopy = ws.copy(fileItem.getId(), "copy-"+ fileItem.getId()+ ".jpg", fileItem.getParent().getId());
				//								ws.moveItem(mycopy.getId(), subfolder.getId());
				//								System.out.println("*************** " + fileItem.getPath());
				//								}
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
//		}
		//				}
		//			}.start();
		//		}

//		System.out.println("getDiskUsageByUser "+ ws.getStorage().getDiskUsageByUser());
//		System.out.println("getTotalItemsByUser "+ ws.getStorage().getTotalItemsByUser());
//		
//		String[] strarray = (String[]) ids.toArray(new String[ids.size()]);  
//		ws.removeItems(strarray);
//		
//		JCRWorkspaceTrashFolder trash = (JCRWorkspaceTrashFolder) ws.getTrash();
//		trash.emptyTrash();
//
//		System.out.println("getDiskUsageByUser "+ ws.getStorage().getDiskUsageByUser());
//		System.out.println("getTotalItemsByUser "+ ws.getStorage().getTotalItemsByUser());
		//		FolderItem fileItem = null;
		//		InputStream in = null;
		//
		//		try{
		//			in  = new FileInputStream("/home/valentina/Downloads/novice_drustvo_delovna_akcija_03_2014.jpg");	
		//			fileItem = folder.createExternalGenericItem("img-"+ UUID.randomUUID().toString()+".jpg", "de", in);
		//			//					fileItem = WorkspaceUtil.createExternalFile(folder, "test-"+ UUID.randomUUID().toString(), "de", null, in);
		//			System.out.println("*************** Remote path " + fileItem.getRemotePath());
		//			System.out.println("*************** jcr path " + fileItem.getRemotePath());
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}finally{
		//			if (in!=null)
		//				in.close();
		//		}
		//
		//		System.out.println("+++++++++++++++++++++++++++");
		//		JCRExternalFile item1 =  (JCRExternalFile) ws.getItem(fileItem.getId());
		//		System.out.println("*************** " + item1.getRemotePath());
		//
		//		JCRWorkspaceItem item = (JCRWorkspaceItem) ws.moveItem(item1.getId(), item1.getParent().getParent().getId());
		//		System.out.println("RemotePath " + item.getRemotePath());
		//	}
	}


}

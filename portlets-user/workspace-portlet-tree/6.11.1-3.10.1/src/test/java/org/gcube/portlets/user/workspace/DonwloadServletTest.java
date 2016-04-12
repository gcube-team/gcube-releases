/**
 * 
 */
package org.gcube.portlets.user.workspace;

import java.util.List;

import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 18, 2013
 * 
 */
public class DonwloadServletTest {

	public static void main(String[] args) {

//		InputStream is = null;
//
//		System.out.println("start");
//
//		 is = GCUBEStorage.getRemoteFile("/Home/francesco.mangiacrapa/Workspace284ee688-e6fb-4080-bbcb-cc7c8cc5c381");

		try {

			Workspace ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome("francesco.mangiacrapa")
					.getWorkspace();
			//
			
			WorkspaceItem root = ws.getRoot();
			
			List<? extends WorkspaceItem> children = root.getChildren();
			
			
			for (WorkspaceItem workspaceItem : children) {
				
				if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
					
					FolderItem folderItem = (FolderItem) workspaceItem;
					
					if(folderItem.getFolderItemType().equals(FolderItemType.EXTERNAL_FILE)){
						
						ExternalFile f = (ExternalFile) folderItem;
						
						System.out.println("folderItem name: "+f.getName() + ", public link: "+f.getPublicLink());
					}
					
				}
				
				
			}
			
			
//			WorkspaceItem item = ws.getItem("8ad4e104-0f34-413e-a88c-e754a81104e7");
//			
//			
//			ExternalFile f = (ExternalFile) item;
//		
//			
//			System.out.println("Public link: "+f.getPublicLink());
//			FileOutputStream out = new FileOutputStream(new File("/tmp/bla"));
//			// byte[] buffer = new byte[1024];
//			// int len;
//			// while ((len = is.read(buffer)) != -1) {
//			// out.write(buffer, 0, len);
//			// }
//
			
			
//			is = f.getData();
			

//			IOUtils.copy(is, out);
//			is.close();
//
//			out.close();

			// System.out.println("Sleeping");
			// Thread.sleep(20000);
			// System.out.println("Alive");

			System.out.println("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void depthVisit(WorkspaceItem item) throws InternalErrorException{

		
		if(item.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
			
			WorkspaceFolder folder = (WorkspaceFolder) item;
			System.out.println("Visit Folder: "+folder.getName());
			List<? extends WorkspaceItem> children = folder.getChildren();
			
			for (WorkspaceItem workspaceItem : children) {
				depthVisit(workspaceItem);
			}
		}else{
			
			System.out.println("Item name: "+item.getName());
			
		}
	}

}

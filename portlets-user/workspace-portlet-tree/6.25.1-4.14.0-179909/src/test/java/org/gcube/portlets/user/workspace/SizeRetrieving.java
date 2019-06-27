/**
 * 
 */
package org.gcube.portlets.user.workspace;

import java.util.List;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.workspace.server.GWTWorkspaceBuilder;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jan 29, 2014
 *
 */
public class SizeRetrieving {
	
	
	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
//	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps";
	public static String TEST_USER = "francesco.mangiacrapa";
//	public static String FOLDER_ID = "4f0ff79d-3c1e-4d2a-bc74-6f731edcac98";
	
	public static void main(String[] args) {

		try {

			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
			ScopeProvider.instance.set(scope.toString());
			
			Workspace ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(TEST_USER)
					.getWorkspace();
			//
			
//			System.out.println("start get root");
			WorkspaceItem folder = ws.getRoot();
//			List<WorkspaceItem> children = (List<WorkspaceItem>) root.getChildren();

			System.out.println("start get children");
			
//			WorkspaceFolder folder = (WorkspaceFolder) ws.getItem(FOLDER_ID);	
			List<WorkspaceItem> children = (List<WorkspaceItem>) folder.getChildren();
//			List<? extends WorkspaceItem> children = root.getChildren();

			System.out.println("children size: "+children.size());

//			GWTWorkspaceBuilder builder = new GWTWorkspaceBuilder();
//			
//			builder.buildGXTListFileGridModelItem(children, null);
			
			
			
			int i=0;
			for (WorkspaceItem workspaceItem : children) {
				
				
				if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER) || workspaceItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
					
					folder = (WorkspaceFolder) workspaceItem;
					
				
					System.out.println(++i+") folder name: "+folder.getName() +", id: "+folder.getId());
				}else{
					
				
					if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)){
					
					FolderItem folderItem = (FolderItem) workspaceItem;
										
					System.out.println(++i+") folderItem id: "+folderItem.getId() +", name: "+folderItem.getName() + ", size: "+folderItem.getLength());
					}
					
				}
			}
			System.out.println("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

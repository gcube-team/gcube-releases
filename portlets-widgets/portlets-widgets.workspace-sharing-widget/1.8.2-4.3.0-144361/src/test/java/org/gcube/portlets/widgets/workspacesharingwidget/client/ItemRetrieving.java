/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 29, 2014
 *
 */
public class ItemRetrieving {
	
	
//	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps"; //PRODUCTION
	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
	public static String TEST_USER = "francesco.mangiacrapa";
//	public static String ITEMID = "63832213-098d-42d1-8774-89b6349764c0"; //Activity T3.4 working drafts/T2-EC-IMAR-HO-14-015  iMarine Sustainability WP - Business Model tools.pdf
//	public static String ITEMID = "56a9aa27-2150-4409-b5da-cc96c5c4eb70";
	
	
	public static void main(String[] args) {

		try {

			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
			ScopeProvider.instance.set(scope.toString());
			
			System.out.println("init HL");
			Workspace ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(TEST_USER)
					.getWorkspace();
			//
			
			
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
			
			int i=0;
			int foldersCounter = 0;
			int sharedFoldersCounter = 0;
			int folderItemCounter = 0;
			int othersCounter = 0;
			for (WorkspaceItem workspaceItem : children) {
				
				switch (workspaceItem.getType()) {
				
				case FOLDER:
					
					WorkspaceFolder folder = (WorkspaceFolder) workspaceItem;

					System.out.println(++i+") folder name: "+folder.getName() + " owner "+folder.getOwner().getPortalLogin());
					foldersCounter++;
					
					break;

				case SHARED_FOLDER:
					
					WorkspaceSharedFolder shared = (WorkspaceSharedFolder) workspaceItem;

					System.out.println(++i+") shared folder name: "+shared.getName() + " owner "+shared.getOwner().getPortalLogin() +" isVRE "+shared.isVreFolder());
					
//					System.out.println(++i+") shared folder name: "+shared.getName() + " owner "+shared.getOwner().getPortalLogin());
					sharedFoldersCounter++;
					
					break;
					
				case FOLDER_ITEM:
					
					FolderItem folderItem = (FolderItem) workspaceItem;
					
					System.out.println(++i+") folderItem id: "+folderItem.getId() +", name: "+folderItem.getName() + ", own: "+folderItem.getOwner().getPortalLogin());
					folderItemCounter++;
					
					break;
				default:
					System.out.println(++i+") DEFAULT - item id: "+workspaceItem.getId() +", name: "+workspaceItem.getName() + ", own: "+workspaceItem.getOwner().getPortalLogin());
					othersCounter++;
					break;
				}
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

}

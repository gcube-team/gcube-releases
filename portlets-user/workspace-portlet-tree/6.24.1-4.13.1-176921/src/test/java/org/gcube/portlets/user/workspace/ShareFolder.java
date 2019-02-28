package org.gcube.portlets.user.workspace;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;

public class ShareFolder {
	
	private static final String SUB_FOLDER_NAME = "subfoldermycourse";
	private static final String FOLDER_NAME = "_mycourse";
	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
//	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps";
	public static String TEST_USER = "francesco.mangiacrapa";
//	public static String FOLDER_ID = "4f0ff79d-3c1e-4d2a-bc74-6f731edcac98";
	
	public static void main(String[] args) {

		try {

//			System.out.println("start");
//			ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
			ScopeProvider.instance.set(DEFAULT_SCOPE);
			
			Workspace workspace = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome(TEST_USER)
					.getWorkspace();
			//
			
			System.out.println("start get root");
			WorkspaceItem theRoot = workspace.getRoot();
			
			System.out.println("\n\n CREATING folder: "+FOLDER_NAME + "\n\n");
			
			WorkspaceFolder myCourseFolder = workspace.createFolder(FOLDER_NAME, "", theRoot.getId());
			
			System.out.println("\n\n FOLDER id: "+myCourseFolder.getId() +"\n\n");
			
			System.out.println("\n\n CREATING sub folder: "+SUB_FOLDER_NAME+"\n\n");
			
			WorkspaceFolder subFolderMyCourse = workspace.createFolder(SUB_FOLDER_NAME, "", myCourseFolder.getId());
			
			System.out.println("\n\n SUB_FOLDER id: "+subFolderMyCourse.getId() +"\n\n");
			
			List<String> users = new ArrayList<String>();
			users.add("costantino.perciante");
			WorkspaceSharedFolder subShareFolder = workspace.shareFolder(users, subFolderMyCourse.getId());
			subShareFolder.setACL(users, ACLType.READ_ONLY);
			System.out.println("\n\n SHARED SUB_FOLDER id: "+subShareFolder.getId() +"\n\n");
			
//			WorkspaceSharedFolder sharedFolder = subFolderMyCourse.share(users);

			
//			System.out.println("\n\n SHARED subFolderMyCourse id: "+sharedFolder.getId());
//			
//
//			WorkspaceFolder folder = (WorkspaceFolder) workspace.getItem(sharedFolder.getId());
//			
//			System.out.println("\n\n FINAL id: "+folder.getId());
			
			System.out.println("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package org.gcube.portlets.user.homelibrary.jcr.manager;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author valentina
 *
 */
public class TestACLUnshare {


	protected static Logger logger = LoggerFactory.getLogger(TestACLUnshare.class);

	public static void main(String[] args) {

		try {

			ScopeProvider.instance.set("/gcube/devsec");


			Workspace ws = HomeLibrary
					.getHomeManagerFactory()
					.getHomeManager()
					.getHome("valentina.marioli")
					.getWorkspace();

			WorkspaceItem origFolder = ws.getItemByPath("/Workspace/vale");
			WorkspaceItem destFolder = ws.getItemByPath("/Workspace/myPersonalTrash");
			ws.copy(origFolder.getId(), destFolder.getId());
			
//			WorkspaceSharedFolder sharedFolder = (WorkspaceSharedFolder) ws.getItem("c014f52e-d10b-4c90-8191-f6a13b89855b");
//			sharedFolder.unShare();
//			System.out.println(sharedFolder.getId());
//			System.out.println(sharedFolder.getACLUser());
//			System.out.println(sharedFolder.getACLOwner().toString());

			System.out.println("end");


		}catch (Exception e) {
			e.printStackTrace();
		}
	}


}




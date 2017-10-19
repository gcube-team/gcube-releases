package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.jcr.PathNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
//import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceVREFolder;
import org.gcube.common.scope.api.ScopeProvider;

public class AddUserToVREFolder {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {

		WorkspaceFolder folder = null;
		try {
			String scope = "/d4science.research-infrastructures.eu/gCubeApps/SIASPA";
			//			String scope = "/gcube/devsec";
			//			String username = "valentina.marioli";
//			String scope = "/gcube/preprod/preVRE";
					ScopeProvider.instance.set(scope);
			//			SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

			// Obtained the factory you can retrieve the HomeManager:
					
					
			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();

//			UserManager userManager = HomeLibrary.getHomeManagerFactory().getUserManager();
//			
//			List<String> list = userManager.getUsers();
//			boolean flag = false;
//			for (String user:list){
//				System.out.println(user);
//				if (user.equals("denispyr"))
//					flag = true;
//				if (!flag)
//					continue;
					
			String user = "d4science.research-infrastructures.eu-gCubeApps-SIASPA-Manager";
				
			
				try{
					Home home = manager.getHome(user);

					Workspace ws = home.getWorkspace();
//					System.out.println(ws.getRoot().getPath());
					JCRWorkspaceVREFolder vreFolder = (JCRWorkspaceVREFolder) ws.getVREFolderByScope(scope);
					System.out.println(vreFolder.getPath());
					System.out.println(vreFolder.getUsers());
				
//			vreFolder.removeUserFromVRE("valentina.marioli");
//				vreFolder.addUser("massimiliano.assante");
				
				} catch (Exception e) {
					e.printStackTrace();
				}
//			}

			//			folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/MySpecialFolders/gcube-preprod-Dorne");
			//			System.out.println(folder.getPath());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}

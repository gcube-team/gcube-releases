package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.List;

import javax.jcr.PathNotFoundException;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class TestWrongVREManager {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {

		try {

			//						ScopeProvider.instance.set("/gcube/devNext/NextNext");
			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
			//		ScopeProvider.instance.set("/gcube/preprod/preVRE");
			//			SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();

			List<String> users = HomeLibrary.getHomeManagerFactory().getUserManager().getUsers();

			for (String user: users){

				if (user.startsWith("d4science.research-infrastructures.eu-") && (user.endsWith("-Manager"))){
//					System.out.println("* " + user);
					
					Home home = manager.getHome(user);
					JCRWorkspace ws = (JCRWorkspace) home.getWorkspace();
					List<WorkspaceItem> folders = ws.getMySpecialFolders().getChildren();
					for(WorkspaceItem vre: folders){
						if (vre.getOwner().getPortalLogin().endsWith("-Manager")){
							System.out.println(vre.getName() + " - " + vre.getOwner().getPortalLogin());

						}
			
						
					}
	
//					String scope = "/d4science.research-infrastructures.eu/gCubeApps/TabularDataLab";
//					JCRWorkspaceSharedFolder vre = (JCRWorkspaceSharedFolder) ws.getVREFolderByScope(scope);
//					JCRWorkspaceVREFolder myvre = (JCRWorkspaceVREFolder) vre;
//					//						myvre.changeOwner("pasquale.pagano");
	//
//					System.out.println(myvre.getOwner().getPortalLogin());
					
				}


			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}

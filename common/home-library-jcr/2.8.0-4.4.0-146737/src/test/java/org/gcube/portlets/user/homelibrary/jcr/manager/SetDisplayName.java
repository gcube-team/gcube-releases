
package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Ignore;
import org.junit.Test;

public class SetDisplayName {

	private UserManager getUserManager() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		//		ScopeProvider.instance.set("/gcube");
		//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		//							ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ScopeProvider.instance.set("/gcube/preprod/preVRE");
//		String scope = "/d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject";
//		ScopeProvider.instance.set(scope);

		UserManager um = HomeLibrary
				.getHomeManagerFactory().getUserManager();

		return um;
	}


	@Test
	public void getMyHome() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException, org.gcube.common.homelibrary.model.exceptions.RepositoryException {

//		String prefix = "gcube-preprod-";
		String prefix = "d4science.research-infrastructures.eu-Ecosystem-";

		UserManager um = getUserManager();
		List<GCubeGroup> list = um.getGroups();
		for (GCubeGroup group: list){
			if (group.getDisplayName()==null){
				if(group.getName().startsWith(prefix)){
					String name = group.getName();
					System.out.println(group.getName());
					String mystring = name.replace(prefix, "");
					System.out.println(group.getName() + " - D: " +  mystring);
					group.setDisplayName(mystring);
				}
			}

		}

	}





}

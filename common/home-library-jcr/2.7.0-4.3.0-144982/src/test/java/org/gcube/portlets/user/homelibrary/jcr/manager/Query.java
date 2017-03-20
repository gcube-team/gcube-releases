package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.List;

import javax.jcr.PathNotFoundException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

public class Query {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
//		ScopeProvider.instance.set("/gcube/devsec");
		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("massimiliano.assante").getWorkspace();


		String path = "/Home/massimiliano.assante/Workspace/MySpecialFolders/";
		query((WorkspaceFolder) ws.getItemByPath(path));


	}

	private static void query(WorkspaceFolder root) throws InternalErrorException {
		List<WorkspaceItem> items = root.getLastItems(5);
		for(WorkspaceItem item: items){
			System.out.println(item.getPath());
		}
		
	}




}

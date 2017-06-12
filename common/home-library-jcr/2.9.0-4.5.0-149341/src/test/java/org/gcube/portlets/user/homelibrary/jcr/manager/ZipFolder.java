package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.IOException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class ZipFolder {


	public static void main(String[] args) throws InternalErrorException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
				ScopeProvider.instance.set("/gcube");
//	ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		Workspace ws = getWorkspace("denispyr");
		
		String scope = "/gcube/devsec/devVRE";
		WorkspaceSharedFolder folder = ws.getVREFolderByScope(scope);
		System.out.println(folder.getPath());
		
		
//		WorkspaceItem item = ws.getItemByPath("/Workspace/BBB");
//		JCRWorkspaceFolder folder = (JCRWorkspaceFolder) item;
//		
////		File zip = folder.zipFolder("test00.zip");
//		File zip = ZipUtil.zipFolder(folder);
//		System.out.println(zip.getAbsolutePath());
//		zip.delete();
	}



	private static Workspace getWorkspace(String user) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

		return ws;

	}
}

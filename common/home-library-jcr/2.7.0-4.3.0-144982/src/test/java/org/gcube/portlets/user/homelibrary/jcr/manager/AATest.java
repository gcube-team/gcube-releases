package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.scope.api.ScopeProvider;

public class AATest {

	public static void main(String[] args) throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, org.gcube.common.homelibrary.model.exceptions.RepositoryException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException {
		unShareTest();

	}

	
	public static void unShareTest() throws InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, PathNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, IOException, RepositoryException, InterruptedException, org.gcube.common.homelibrary.model.exceptions.RepositoryException {

		JCRWorkspace ws = (JCRWorkspace) getWorkspace("valentina.marioli");
		
		 WorkspaceFolder folder = ws.createFolder(UUID.randomUUID().toString(),"description", ws.getRoot().getId());
				
				
		String user = "test-00";
		List<String> users = new ArrayList<String>();
		users.add("valentina.marioli");
		users.add(user);
		WorkspaceSharedFolder sharedFolder = ws.createSharedFolder(UUID.randomUUID().toString(), "description", users, folder.getId());
		sharedFolder.setACL(users, ACLType.WRITE_ALL);
		
		uploadFile(sharedFolder);
//		JCRWorkspace ws1 = (JCRWorkspace) getWorkspace(user);
////		JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder) ws1.getItem(item.getId());
//	JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder) ws1.getItemByAbsPath("/Home/"+user+"/Workspace/"+ item.getName());
		JCRWorkspaceItem unsharedFolder = (JCRWorkspaceItem) sharedFolder.unShare();
//		WorkspaceItem folder = ws1.unshare(sharedFolder.getId());
		//		WorkspaceFolder folder = sharedFolder.unShare();
//		WorkspaceFolder folder = item.unShare("test.user");
		if (folder==null)
			System.out.println("folder == null");
		else
			System.out.println(folder.getPath());

	}

	private static void uploadFile(WorkspaceSharedFolder subFolder) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, IOException {
		String fileName = "img-" + UUID.randomUUID()+ ".jpg";
		InputStream is = null;

		try {
			is = new FileInputStream("/home/valentina/Downloads/4737062744_9dd84a2df2_z.jpg");
			ExternalFile f = subFolder.createExternalImageItem(fileName, "test", "image/jpg", is);
			System.out.println(f.getPath());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (is!=null)
				is.close();
		}
		
	}


	private static Workspace getWorkspace(String user) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		ScopeProvider.instance.set("/gcube");
		//							ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

		return ws;

	}
	
}

package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
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
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class UploadFile {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
		//		ScopeProvider.instance.set("/gcube");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");


		//		System.out.println(escapeIllegalJcrChars("**************testprova.jpg"));
		ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();
		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/preProd");
		System.out.println(folder.getPath());
		List<String> users = new ArrayList<String>();
		users.add("francesco.mangiacrapa");
		JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder) ws.createSharedFolder(UUID.randomUUID().toString()+"-test", "description", users, folder.getId());
		sharedFolder.setACL(users, ACLType.WRITE_ALL);
		
//		JCRWorkspaceFolder sharedFolder = (JCRWorkspaceFolder) ws.createFolder("folder-"+UUID.randomUUID().toString(), "description", folder.getId());
		System.out.println(sharedFolder.getPath());
		
		JCRWorkspaceFolder subfolder = (JCRWorkspaceFolder) ws.createFolder("subFolder-"+UUID.randomUUID().toString(), "description", sharedFolder.getId());
		System.out.println(subfolder.getPath());
		
		
		//		InputStream is = null;
		//		FolderItem file = null;
		//		try {
		//			is = new FileInputStream("/home/valentina/BlueBRIDGE_Part_B_1.3_20141217.docx");
		//			file = WorkspaceUtil.createExternalFile(ws.getRoot(), "test-upload00.docx", "description", null, is);
		//			System.out.println(file.getPath());
		//
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}finally{
		//			if (is!=null)
		//				is.close();
		//		}



	}

}

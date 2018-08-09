package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSmartFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryAdd;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryDelete;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRemoval;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.search.SearchItem;
import org.gcube.common.homelibrary.home.workspace.search.util.SearchQueryBuilder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashFolder;
import org.gcube.common.homelibrary.home.workspace.trash.WorkspaceTrashItem;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalImage;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRImage;
import org.gcube.common.homelibrary.jcr.workspace.trash.JCRWorkspaceTrashFolder;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class UploadTestFile {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {

		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		//		SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");
		//		SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");


		String user = "valentina.marioli";

		ws = (JCRWorkspace) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user).getWorkspace();

		String pathFolder = "/Workspace/Clustering";

		final WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath(pathFolder); 



		InputStream in = null;
		try{

			in  = new FileInputStream("/home/valentina/Downloads/ubuntu-17.04-desktop-amd64.iso");	

			String itemName = WorkspaceUtil.getUniqueName("ubuntu-17.04-desktop-amd64.iso", folder);
			System.out.println(itemName);
			//
			FolderItem fileItem = WorkspaceUtil.createExternalFile(folder, itemName, "tab", in);
			System.out.println(fileItem.getPath());

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (in!=null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}


}

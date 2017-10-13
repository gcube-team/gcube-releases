package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
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
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.scope.api.ScopeProvider;

public class TestStatistical {

	static Workspace ws = null;
	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {

		createLibrary();

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException {
//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
						ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli").getWorkspace();

		
//		JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath("/Home/valentina.marioli/Workspace/.applications/StatisticalManager");
////		WorkspaceFolder root = ws.getRoot();
//		System.out.println(item.getPath());
		
		JCRWorkspaceFolder appFolder = (JCRWorkspaceFolder) getWorkspaceSMFolder("valentina.marioli");
		WorkspaceFolder subFolder = appFolder.createFolder("dir-"+ UUID.randomUUID(),"SM Image");
		String fileName = "img-" + UUID.randomUUID()+ ".jpg";
		InputStream is = null;

		try {
			is = new FileInputStream("/home/valentina/Downloads/4737062744_9dd84a2df2_z.jpg");
//			ExternalFile f = subFolder.createExternalFileItem(fileName, "test", "image/png", is);
			ExternalFile f = subFolder.createExternalImageItem(fileName, "test", "image/jpg", is);
			System.out.println(f.getPath());
			System.out.println(f.getPublicLink(true));
			is.close(); 

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}




	public static WorkspaceFolder getWorkspaceSMFolder(String userName) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException
	{
		WorkspaceFolder appfolder= null;
		try{
			System.out.println("get Workspace Application Folder for the user "+ userName);
			Home home = HomeLibrary.getHomeManagerFactory().getHomeManager()
					.getHome(userName);

			System.out.println("get home");
			appfolder= home.getDataArea().getApplicationRoot("StatisticalManager");
			System.out.println("foldere created");

		}
		catch (Exception e) {

			e.printStackTrace();


		}
		return appfolder;
	}

}

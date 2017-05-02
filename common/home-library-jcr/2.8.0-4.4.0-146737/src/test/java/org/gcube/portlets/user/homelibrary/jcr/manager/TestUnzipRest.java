package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jcr.PathNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.zip.UnzipUtil;
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
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;

public class TestUnzipRest {
	static Workspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {

		WorkspaceFolder folder = null;
		try {
			//			String scope = "/gcube/devsec";
			//			String username = "valentina.marioli";
			ScopeProvider.instance.set("/gcube/devNext/NextNext");
			//			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
			//		ScopeProvider.instance.set("/gcube/preprod/preVRE");
			//					SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();

			Home home = manager.getHome("valentina.marioli");


			JCRWorkspace ws = (JCRWorkspace) home.getWorkspace();

			//			List<WorkspaceItem> children = ws.getRoot().getChildren();
			//			for (WorkspaceItem child: children){
			//				System.out.println(child.getPath());
			//			}

						System.out.println(ws.getRoot().getPath());

			folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/test-vv");

			File file = new File("/home/valentina/Downloads/Student_SolidWorks_Files_2010.zip");
			//			byte[] archive = Files.readAllBytes(file.toPath());
			InputStream inputStream = new FileInputStream(file);
			long startTime = System.currentTimeMillis();
			WorkspaceFolder unzip = UnzipUtil.unzip(folder, inputStream, "email-address-encoder.1.0.5", true, false);
			long endTime = System.currentTimeMillis();
			System.out.println(unzip.getPath());
			System.out.println(endTime - startTime);
			
			
//			String scope = "/gcube/devNext/NextNext";
//			folder = ws.getVREFolderByScope(scope);
//			System.out.println(folder.getPath());
			
//			UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
//			String scope= "/gcube/devsec/BasicVRETest";
//			String userToAssociate = "valentina.marioli";
//			um.associateUserToGroup(scope, userToAssociate);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



}

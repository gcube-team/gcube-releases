package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import ij.plugin.MeasurementsWriter;

//import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceVREFolder;


public class CreateMyVREFolders {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
	
		try {

//			SecurityTokenProvider.instance.set("f068c06b-dad9-4007-a3c6-020560116a59|98187548");

			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();
			Home home = manager.getHome();
			ws = (JCRWorkspace) home.getWorkspace();

			String vreString = "MyVRE"+ UUID.randomUUID();
			String scope = "/gcube/test/"+vreString;
			String description = "test";
			String displayName = vreString;
			
			List<String> users = new ArrayList<String>();
			users.add("valentina.marioli");
			users.add("roberto.cirillo");
			
			JCRWorkspaceVREFolder vre = (JCRWorkspaceVREFolder) ws.createVREFolder(scope, description, displayName, ACLType.WRITE_ALL);
			
			vre.addUser("test.user");
	
			
//			List<String> logins = new ArrayList<String>();
//			users.add("valentina.marioli");
//			vre.setAdmins(logins);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	
	
	}


}

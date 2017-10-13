package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.scope.api.ScopeProvider;

import ij.plugin.MeasurementsWriter;

//import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceVREFolder;


public class CreateMyVREFolders {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException {
	
		try {
			
			ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
//			ScopeProvider.instance.set("/gcube/devsec");
//			SecurityTokenProvider.instance.set("f068c06b-dad9-4007-a3c6-020560116a59|98187548");

			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();
			Home home = manager.getHome("gianpaolo.coro");
			ws = (JCRWorkspace) home.getWorkspace();
			
			WorkspaceItem item = ws.getItemByPath("/Workspace/DataMiner/Output Data Sets");
			System.out.println(item.getPath());
			item.remove();
//			String scope = "/d4science.research-infrastructures.eu/D4Research/RubRIcA";
//		JCRWorkspaceSharedFolder share = (JCRWorkspaceSharedFolder) ws.getVREFolderByScope(scope);
////		[d4science.research-infrastructures.eu-D4Research-RubRIcA-Manager, maurizio.sanesi, massimiliano.assante, edi, leonardo.candela, roberta]
//		System.out.println(share.getUsers().toString());
////		ws.removeItem(share.getId());
////		System.out.println(share.getOwner().getPortalLogin());
//		List<String> users = share.getUsers();
//		for (String user: users){
//			if (user.equals("d4science.research-infrastructures.eu-D4Research-RubRIcA-Manager")){
//				
//				
//				ws.removeItem(share.getId());
//				System.out.println("remove clone for user " + user);
//			}
//		}
		
			
//			String vreString = "MyVRE"+ UUID.randomUUID();
//			String scope = "/d4science.research-infrastructures.eu/ParthenosVO/RubRIcA";
//			String description = "test";
//			String displayName = vreString;
//			String groupId = "d4science.research-infrastructures.eu-ParthenosVO-RubRIcA";
//			List<String> users = new ArrayList<String>();
////			users.add("valentina.marioli");
//			users.add(groupId);
//			
//			HomeLibrary.getHomeManagerFactory().getUserManager().createGroup(groupId);
//			WorkspaceSharedFolder vre = ws.createSharedFolder(scope, description, groupId, ws.getMySpecialFolders().getId(), displayName, true);
//			vre.setACL(users, ACLType.WRITE_OWNER);
//			JCRWorkspaceVREFolder vre = (JCRWorkspaceVREFolder) ws.createVREFolder(scope, description, displayName, ACLType.WRITE_ALL);
//			
//			vre.addUser("test.user");
	
			
//			List<String> logins = new ArrayList<String>();
//			users.add("valentina.marioli");
//			vre.setAdmins(logins);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	
	
	}


}

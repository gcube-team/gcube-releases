package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.accessmanager.AccessManager;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.accessmanager.JCRPrivilegesInfo;
import org.gcube.common.scope.api.ScopeProvider;

public class TestAccessManager {

	static AccessManager am = null;
	//	static String absPath = "/Workspace/gcube-test-test05/";
	static String absPath = "/Share/c9566f48-fd4d-4a92-af2d-90a19d862663";
	static String path = "/Workspace/test00";
	static String file = "/Workspace/test/animali.jpg";
	//	static String absPath = "/Share/cebdb076-ed0f-49e7-8054-8f962651e4e1/";
	//	static String absPath = "/Home/roberto.cirillo/Workspace/girl.jpg(0)";

	public static void main(String[] args) throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, PathNotFoundException, RepositoryException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException {

		ScopeProvider.instance.set("/gcube/devsec");

		Workspace wspace = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("valentina.marioli")
				.getWorkspace();

		am = HomeLibrary
				.getHomeManagerFactory().getAccessManager();
		
		//		am.getACL(absPath);
		//		List<String> privileges = new ArrayList<String>();
		//		privileges.add("jcr:addChildNodes=denied");
		//		privileges.add("jcr:removeNode=granted");
		//		privileges.add("jcr:read=granted");

//		Session session = JCRRepository.getSession();
//		Node myNode = session.getNode("/Home/valentina.marioli"+file);
//		System.out.println("isLocked? " + myNode.get);
		
		
//				List<String> users = new ArrayList<String>();
//				users.add("angela.italiano");
//				am.setWriteOwnerACL(users, absPath);

		//	am.setWriteAllACL(users, absPath);
		//		am.setReadOnlyACL(users, absPath);
		//		am.setAdminACL(users, absPath);
		//		am.modifyAce(users, absPath, privileges, "first");
		//		am.deleteAces(absPath, users);

		
//		WorkspaceItem item0 = wspace.getItemByPath(
		System.out.println("/Home/valentina.marioli"+file);
		System.out.println(am.getEACL("/Home/valentina.marioli"+file));
		
		
//		Session session = JCRRepository.getSession();
//		Node node = session.getNode("/Home/valentina.marioli"+absPath);
		WorkspaceSharedFolder item = (WorkspaceSharedFolder) wspace.getItemByPath(path);
//		item.setACL(item.getUsers(),ACLType.WRITE_OWNER );
//		System.out.println(item.getUsers().toString());
		System.out.println(item.getACLOwner());
//		//		item.getOwner().getPortalLogin()
//		System.out.println(JCRPrivilegesInfo.getACLByUser("valentina.marioli", node.getPath()));
//
//
//
//		WorkspaceFolder folder = wspace.createFolder("CheckFolder02", "check", wspace.getRoot().getId());
//		List<String> useers = new ArrayList<String>();
//		useers.add("roberto.cirillo");
//		useers.add("lucio.lelii");
//		WorkspaceSharedFolder sharedFolder = folder.share(useers);
//		sharedFolder.setACL(useers, ACLType.WRITE_OWNER);
//		System.out.println("users of folder " + useers.toString());
//		System.out.println("--> " + sharedFolder.getUsers().toString());
//		
//		System.out.println("\n");
//		System.out.println(sharedFolder.getACLOwner());
//		//		System.out.println(item.getACL());
//	System.out.println("**** " + am.getEACL(absPath).toString());
//		System.out.println("\n");



		//	am.getACL(absPath);

		//	String user = "valentina.marioli";
		//	String pass = "";
		//	String user = "workspacerep.imarine";
		//	String pass="gcube2010*onan";

		//		Session session = JCRRepository.getSession(user, pass);
		//		System.out.println(session.getUserID());
		//		Node node = session.getNode(absPath);
		//		System.out.println(node.getPath());
		//		
		//		
		//		JCRPrivilegesInfo info = new JCRPrivilegesInfo();
		//		System.out.println(info.canAddChildren(session, absPath));



	}

}

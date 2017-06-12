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
import org.gcube.common.homelibrary.home.workspace.WorkspaceVREFolder;
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
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceVREFolder;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalImage;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRImage;
import org.gcube.common.homelibrary.jcr.workspace.trash.JCRWorkspaceTrashFolder;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class ChangeVREFolderName {
	static JCRWorkspace ws = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, org.gcube.common.homelibrary.model.exceptions.RepositoryException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException, org.gcube.common.homelibrary.model.exceptions.RepositoryException {
//		ScopeProvider.instance.set("/gcube");
				ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

//				String user = "valentina.marioli";
		String manager = "d4science.research-infrastructures.eu-gCubeApps-SmartArea-Manager";

	

		 UserManager um = HomeLibrary
				.getHomeManagerFactory().getUserManager();


		
		String path = "/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-SmartArea";
//		JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) ws.getItemByPath(path);
//		folder.rename("");
//		JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) ws.getItemByPath(path);
//		
//		folder.remove();
//		String newName = "d4science.research-infrastructures.eu-SmartArea-SmartApps-Manager";
//		String oldGroup = "d4science.research-infrastructures.eu-gCubeApps-SmartArea";
//		List<String> users = new ArrayList<>();
//		users.add(oldGroup);
//		folder.setACL(folder.getUsers(), ACLType.WRITE_ALL);
//		System.out.println(folder.getName());
//		
////		folder.unShare("d4science.research-infrastructures.eu-gCubeApps-SmartArea-Manager");
//		
		
		String groupname = "d4science.research-infrastructures.eu-SmartArea-SmartApps";
	GCubeGroup group = um.getGroup(groupname);
	
	
//	mygroup.addMembers(group.getMembers());
//	System.out.println(group.getMembers());
	for (String member: group.getMembers()){
		
//		ws = (JCRWorkspace) HomeLibrary
//				.getHomeManagerFactory()
//				.getHomeManager()
//				.getHome(member).getWorkspace();
		
//		System.out.println(ws.getRoot().getPath());
		System.out.println(member);
//		try{
//			JCRWorkspaceSharedFolder folder = (JCRWorkspaceSharedFolder) ws.getItemByPath(path);
//			folder.remove();
//			} catch (Exception e) {
//				System.out.println("impossible to unshare " + member);
//				e.printStackTrace();
//				
//			
//			}
	}
	
	
//		for (String member: folder.getUsers()){
//			System.out.println("* " + member);
//			try{
//			folder.unShare(member);
//			} catch (Exception e) {
//				System.out.println("impossible to unshare " + member);
//			
//			}
//			JCRWorkspace myws = (JCRWorkspace) HomeLibrary
//					.getHomeManagerFactory()
//					.getHomeManager()
//					.getHome(member).getWorkspace();
//			WorkspaceItem item = myws.getItemByPath("/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-SmartArea");
//			item.rename("d4science.research-infrastructures.eu-SmartArea-SmartApps");
		}
		
		
//		folder.changeOwner(user);
//		String newName = "d4science.research-infrastructures.eu-SmartArea-SmartApps-Manager";
//		String oldGroup = "d4science.research-infrastructures.eu-gCubeApps-SmartArea-Manager";
//		String scope = "/d4science.research-infrastructures.eu/SmartArea/SmartApps";
//		
//		folder.unShare();
		
		
//		um.removeUserFromGroup(scope, oldGroup);
//		um.associateUserToGroup(scope, newName);
//	
//		folder.removeUserFromVRE(oldGroup);
//		folder.addUserToVRE(newName);
//		folder.setACL(users, privilege);
//	folder.rename(newName);
//		folder.setDisplayName("SmartApps");
//		System.out.println("getDisplayName " + folder.getDisplayName());
		
		
//		folder.changeOwner("leonardo.candela");
		

		
		
	


}

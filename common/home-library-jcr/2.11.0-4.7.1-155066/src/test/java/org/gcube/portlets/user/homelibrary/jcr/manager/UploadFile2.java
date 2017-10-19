package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceInternalLink;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessage;
import org.gcube.common.homelibrary.home.workspace.sharing.WorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.JCRUser;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessage;
import org.gcube.common.homelibrary.jcr.sharing.JCRWorkspaceMessageManager;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceInternalLink;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalUrl;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRFile;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.scope.api.ScopeProvider;

public class UploadFile2 {
	static JCRWorkspace ws = null;
	//	static WorkspaceFolder folder;
	static InputStream in = null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, WrongItemTypeException {

		try {
			createLibrary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createLibrary() throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, RepositoryException, InterruptedException, WrongItemTypeException {
		ScopeProvider.instance.set("/gcube");
		//				ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		String user = "valentina.marioli";

		Home home = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user);
		ws = (JCRWorkspace) home.getWorkspace();

//		WorkspaceFolder item = (WorkspaceFolder) ws.getItemByPath("/Workspace/TestShareUnshare");	
//		List<String> users = new ArrayList<>();
//		users.add("roberto.cirillo");
//		WorkspaceSharedFolder shared = item.share(users);
//		shared.setACL(users, ACLType.WRITE_ALL);
		
		WorkspaceSharedFolder shared = (WorkspaceSharedFolder) ws.getItemByPath("/Workspace/TestShareUnshare");	
		shared.unShare();
//		WorkspaceFolder item = (WorkspaceFolder) ws.getItemByPath("/Workspace/bbb");		
//		//		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/Folder");
//		//		WorkspaceFolder folder = ws.createFolder("Folder", "", ws.getRoot().getId());
//		//		JCRWorkspaceReference ref = (JCRWorkspaceReference) ws.copyAsLink(item.getId(), folder.getId());
//
//		WorkspaceFolder folder = (WorkspaceFolder) ws.getItemByPath("/Workspace/Folder");
//		List<? extends WorkspaceItem> children = folder.getChildren();
//		for (WorkspaceItem child:children){
////			JCRWorkspaceReference ref = (JCRWorkspaceReference) child;
//			for (WorkspaceItem myref:child.getChildren()){
//				System.out.println(myref.getPath());
//			}
//		}


		//		System.out.println("nAME: " + ref.getName());
		//		
		//		List<String> list = item.getReferences();
		//		for(String refer: list){
		//			System.out.println(refer);
		//		}

//				item.remove();
	}


}

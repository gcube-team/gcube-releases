package org.apache.jackrabbit.j2ee.oak;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest.Utils;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

public class Check {

	private static final String URL = "http://workspace-repository-prod1.d4science.org/home-library-webapp";
	private static final String URL_PROD = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp";

	private static final String USER ="workspacerep.imarine";
	private static final String PASS ="workspacerep.imarine";
	private static final String PASS1 = "gcube2010*onan";
	private static final String VRE_PATH = "/Workspace/MySpecialFolders/";
	private static final String HOME = "Home";
	private static final String SEPARATOR = "/";
	private static final Object MY_SPECIAL_FOLDER = "MySpecialFolders";

	static Session session =  null;
	static Session sessionProd =  null;

	private static Workspace workspace;
	private static Workspace workspace_prod;


	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {

		try {

			try{
				URLRemoteRepository repository_psql = new URLRemoteRepository(URL + "/rmi");
				session = repository_psql.login( 
						new SimpleCredentials(USER, PASS.toCharArray()));
				workspace = session.getWorkspace();


				URLRemoteRepository repository_prod = new URLRemoteRepository(URL_PROD + "/rmi");
				sessionProd = repository_prod.login( 
						new SimpleCredentials(USER, PASS1.toCharArray()));


				//				SecurityTokenProvider.instance.set("fea75a5a-d84c-495f-b0ca-09cdd95bacce-843339462");				
				//				Map<String, Boolean> children = new HashMap<String, Boolean>();
				//				Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
				//				 System.out.println("*** Get workspace of " + workspace.getRoot().getPath());
				//
				//				String absPath = "/Home/statistical.manager/Workspace/DataMinerAlgorithms";
				//				absPath = cleanPath(workspace, absPath);
				//				System.out.println("absPath " +absPath);
				//				WorkspaceItem item = workspace.getItemByPath(absPath);
				//				System.out.println("item "+  item.getPath());
				//				if(item.isFolder()){
				//					System.out.println("is folder? " + item.isFolder());
				//					WorkspaceFolder folder = (WorkspaceFolder) item;
				//					java.util.List<WorkspaceItem> list = folder.getAllChildren(false);
				//					System.out.println(list.toString());
				//					for(WorkspaceItem child: list){			
				//						String name = null;
				//						if (child.getId().equals(child.getIdSharedFolder())){
				//							JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) child;
				//							if (shared.isVreFolder())
				//								name = shared.getDisplayName();
				//						}
				//						if (name==null)
				//							name = child.getName();
				//						children.put(name, child.isFolder());
				//						System.out.println(name + " is folder? " + child.isFolder());
				//					}
				//				}


				workspace_prod = sessionProd.getWorkspace();

				//				NodeIterator iterator = sessionProd.getRootNode().getNodes();
				//				while(iterator.hasNext()){
				//					
				//					Node myNode = iterator.nextNode();
				//					if (myNode.getPath().endsWith("]"))
				//						System.out.println(myNode.getPath());
				//				}
				checker(session.getNode("/Home/valentina.marioli"));
			} finally {
				if (session!=null)
					session.logout();

				if (sessionProd!=null)
					sessionProd.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//	public static String cleanPath(Workspace workspace, String absPath) throws ItemNotFoundException, InternalErrorException {
	//
	//		String myVRE = null;
	//		String longVRE = null;
	//		
	//		String [] splitPath = absPath.split(SEPARATOR);
	//		if(absPath.contains(VRE_PATH) && (!splitPath[splitPath.length-1].equals(MY_SPECIAL_FOLDER))){
	//					
	//			if (splitPath[1].equals(HOME))
	//				myVRE = splitPath[5];
	//			else
	//				myVRE = splitPath[3];
	//
	//			java.util.List<WorkspaceItem> vres = workspace.getMySpecialFolders().getChildren();
	//			for (WorkspaceItem vre: vres){
	//				if (vre.getName().endsWith(myVRE)){
	//					longVRE = vre.getName();
	//					break;
	//				} 
	//			}
	//
	//			if (longVRE!=null)
	//				absPath = absPath.replace(myVRE, longVRE);
	//		}
	//		
	////		System.out.println("CLEAN PATH " + absPath);
	//		return absPath;
	//
	//	}


	private static void checker(Node node_psql) throws ItemNotFoundException, org.gcube.common.homelibrary.model.exceptions.RepositoryException, InternalErrorException, WorkspaceFolderNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, javax.jcr.RepositoryException {
		Node node_prod =null;
		boolean exists = true;
		try{
			node_prod = sessionProd.getNode(node_psql.getPath());
		} catch (PathNotFoundException e) {
			node_psql.remove();
			session.save();
			exists = false;

		} 		
		//		System.out.println("Check " + node_prod.getPath() );
		if (exists){
			if (!node_psql.getPath().equals(node_prod.getPath())){
				System.out.println(node_psql.getPath() + " != " + node_prod.getPath());

			}

			try {
				NodeIterator iter_psql = node_psql.getNodes();
				while (iter_psql.hasNext()){
					Node child_psql = iter_psql.nextNode();
					if (child_psql.getName().startsWith("hl:") || child_psql.getName().startsWith("rep:"))
						continue;
					checker(child_psql);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} 		
		}
	}


}

package org.apache.jackrabbit.j2ee.oak;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.catalina.websocket.WsOutbound;
import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

public class STEP4 {
//	private static final String URL = "http://workspace-repository-prod1.d4science.org/home-library-webapp";
		private static final String URL = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp";
//			private static final String URL = "http://localhost:8080/jackrabbit-webapp-2.14.0";
	private static final String USER ="workspacerep.imarine";
//	private static final String PASS ="workspacerep.imarine";
	private static final String PASS = "gcube2010*onan";

	static Session session =  null;
	private static Workspace workspace;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {
		System.out.println("start");
		try {

			try{


				URLRemoteRepository repository = new URLRemoteRepository(URL + "/rmi");
				session = repository.login( 
						new SimpleCredentials(USER, PASS.toCharArray()));

				workspace = session.getWorkspace();


				reader();

			} finally {
				if (session!=null)
					session.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void reader() throws ItemNotFoundException, org.gcube.common.homelibrary.model.exceptions.RepositoryException, InternalErrorException, WorkspaceFolderNotFoundException, HomeNotFoundException, UserNotFoundException, IOException, javax.jcr.RepositoryException {

		String user = "gianpaolo.coro";

		try {
			Node folder = session.getNode("/Home/gianpaolo.coro/Workspace/MyExperiments/");
			
			System.out.println("new ID: " + folder.getIdentifier());
			NodeIterator sharedFolders = folder.getNodes();

			String idParent = folder.getIdentifier();


			while ( sharedFolders.hasNext()) {

				Node sharedFolder = sharedFolders.nextNode();

				if (sharedFolder.getName().startsWith("hl:"))
					continue;
				
				if (!sharedFolder.getPrimaryNodeType().getName().equals("nthl:workspaceSharedItem"))
					continue;

				String id = sharedFolder.getIdentifier();
				String path = sharedFolder.getPath();
//				System.out.println(id);
				System.out.println("__________________________________");
				System.out.println("-> id " + id);
				System.out.println("-> path " + path);

//				Node usersNode = sharedFolder.getNode("hl:users");
//				PropertyIterator users = usersNode.getProperties();
//				while(users.hasNext()){
//
//					Property prop = users.nextProperty();
//
//					if (prop.getName().startsWith("jcr:"))
//						continue;
//
//					if (prop.getName().equals(user)){
//						System.out.println(prop.getName() + " - " +prop.getString());
//
//						String newstring = prop.getString().replace("d14d415a-c996-49dc-8314-b3012917f750", idParent);
//						System.out.println(newstring);
//						usersNode.setProperty(user, newstring);
//						session.save();
//
//
//					}
//				}

//								Map<String, String> userMap = getMap(sharedFolder);
//								String value = userMap.get(user);
//				
//								System.out.println("**** " + user + " -> "+  value);
//				
//								String[] values = null;
//								if (value!=null){
//									values = value.split("/");
//									if (values.length < 2)
//										throw new InternalErrorException("Path node corrupt");
//								}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}


	public static Map<String, String> getMap(Node node) throws RepositoryException,
	InternalErrorException, ItemNotFoundException, HttpException, IOException, javax.jcr.RepositoryException {

		try{
			Map<String, String> map = new HashMap<String, String>();

			PropertyIterator users = node.getNode("hl:users").getProperties();
			while(users.hasNext()){

				Property prop = users.nextProperty();

				if (prop.getName().startsWith("jcr:"))
					continue;
				map.put(prop.getName(), prop.getString());

			}
			return map;

		} finally {

		}
	}


}

package org.apache.jackrabbit.j2ee.oak;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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

public class STEP3 {
	private static final String URL = "http://workspace-repository-prod1.d4science.org/home-library-webapp";
	//	private static final String URL = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp";
	//		private static final String URL = "http://localhost:8080/jackrabbit-webapp-2.14.0";
	//	private static final String USER ="admin";
	//	private static final String PASS = "admin";
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

		try {


			List<String> list = new ArrayList<String>();
			list.add("1945a85f-2f0b-4e9f-a677-cf3967bc0cad");
			list.add("2ff3986f-d202-4b3b-920d-e6674e61e53e");
			list.add("36ad54b0-b2d7-4220-be1d-715bbdf89a80");
			list.add("4d7bbbab-fb0a-4ad1-8d46-55638802c053");
			list.add("52f81b77-effc-4139-934c-a5109b281cec");
			list.add("a3146fcd-bd71-4b6b-8770-92cbb7f502b7");
			list.add("ab061510-04c9-431c-8e23-e72082e38f95");
			list.add("be854f52-ac05-4cbe-8c8f-d88c8ddf9705");

			//			NodeIterator sharedFolders = session.getNode("/Share").getNodes();
			//
			//			
			////			System.out.println(session.getNodeByIdentifier("4d7bbbab-fb0a-4ad1-8d46-55638802c053").getPath());
			//			while ( sharedFolders.hasNext()) {
			//
			//				Node sharedFolder = sharedFolders.nextNode();
			//
			//				if (!sharedFolder.getIdentifier().equals("4d7bbbab-fb0a-4ad1-8d46-55638802c053"))
			//					continue;


			
			for (String id : list){
			Node sharedFolder = session.getNodeByIdentifier(id);
//			String id = sharedFolder.getIdentifier();
			String path = sharedFolder.getPath();

			System.out.println("__________________________________");
			System.out.println("-> id " + id);
			System.out.println("-> path " + path);

			try{

				Node sharedNode = session.getNodeByIdentifier(id);
				Map<String, String> userMap = getMap(sharedNode);
				Set<String> keys = userMap.keySet();

				if (keys.size()==0)
					System.out.println("no users");

				System.out.println(sharedNode.getProperty("jcr:created").getDate().getTime());
				try{
					System.out.println("Owner " + sharedNode.getNode("hl:owner").getProperty("hl:portalLogin").toString());
				} catch (Exception e) {

				}
				try{
					System.out.println("Owner " + sharedNode.getProperty("hl:portalLogin").getString());
				} catch (Exception e) {

				}
				for (String user: keys){

//					String my = session.getNode("/Home/gianpaolo.coro/Workspace/MyExperiments").getIdentifier();
//					System.out.println("MyExperiments ID"+ my);

					if (!user.equals("gianpaolo.coro"))
						continue;

					String value = userMap.get(user);

					System.out.println("**** " + user + " -> "+  value);

					String[] values = null;
					if (value!=null){
						values = value.split("/");
						if (values.length < 2)
							throw new InternalErrorException("Path node corrupt");
					}

					//						String parentId = "d14d415a-c996-49dc-8314-b3012917f750";

					String parentId = values[0];
					String nodeName = values[1];

					System.out.println("Parent ID " + parentId + " - nodeName " + nodeName);


					try{
						//							String parentNode = getItemById(parentId);
						String parentNode = "/Home/gianpaolo.coro/Workspace/MyExperiments";
						System.out.println("Clone to " + parentNode+ "/"+nodeName);
//																				clone(sharedNode, parentNode+ "/"+nodeName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			//				System.out.println("__________________________________");
		}

	} catch (Exception e) {
		e.printStackTrace();
	} 
}


private static String getItemById(String parentId) throws MalformedURLException, javax.jcr.RepositoryException {

	try{
		Node parent = session.getNodeByIdentifier(parentId);
		return parent.getPath();

	} finally {

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


public static void clone(Node node,  String destAbsPath) throws RepositoryException,
InternalErrorException, ItemNotFoundException, HttpException, IOException, javax.jcr.RepositoryException {

	try{

		String srcAbsPath = node.getPath();
		Node srcNode = session.getNode(srcAbsPath);
		//			System.out.println("srcNode: "+ srcAbsPath + " - id:" + srcNode.getIdentifier());
		System.out.println("Clone from "+srcAbsPath + " to " + destAbsPath);

		try{
			workspace.clone(workspace.getName(), srcAbsPath, destAbsPath, false);
		} catch (ItemExistsException e) {
			System.out.println("Already exists");
		} 

	} finally {

	}

}
}

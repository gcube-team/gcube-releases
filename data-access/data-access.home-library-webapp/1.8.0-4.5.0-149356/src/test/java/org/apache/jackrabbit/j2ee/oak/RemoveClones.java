package org.apache.jackrabbit.j2ee.oak;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

public class RemoveClones {
//	private static final String URL = "http://localhost:8080/jackrabbit-webapp-2.14.0";
	private static final String URL = "http://workspace-repository-prod.d4science.org:8080/home-library-webapp";
	private static final String USER ="workspacerep.imarine";
	private static final String PASS = "gcube2010*onan";

	static Session session =  null;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {

		try {

			try{
				URLRemoteRepository repository = new URLRemoteRepository(URL + "/rmi");
				System.out.println(repository);
				session = repository.login( 
						new SimpleCredentials(USER, PASS.toCharArray()));

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

			NodeIterator sharedFolders = session.getNode("/Share").getNodes();

			while ( sharedFolders.hasNext()) {

				Node sharedFolder = sharedFolders.nextNode();
				String id = sharedFolder.getIdentifier();
				String path = sharedFolder.getPath();

				System.out.println("__________________________________");
				System.out.println("-> id " + id);
				System.out.println("-> path " + path);

				try{
					Node sharedNode = session.getNodeByIdentifier(id);

					Map<String, String> userMap = getMap(sharedNode);
					Set<String> keys = userMap.keySet();
					for (String user: keys){

						String value = userMap.get(user);
						System.out.println("**** " + user + " -> "+  value);
						//			logger.debug("value "+  value + " for delegate " + delegate.getPath());
						String[] values = null;
						if (value!=null){
							values = value.split("/");
							if (values.length < 2)
								throw new InternalErrorException("Path node corrupt");
						}

						String parentId = values[0];
						String nodeName = values[1];

						try{
							String parentNode = getItemById(parentId);
							remove(parentNode+ "/"+nodeName);
						} catch (Exception e) {
							System.out.println("Error removing node ");
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("__________________________________");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	
	private static void remove(String absPath) throws javax.jcr.ItemNotFoundException, javax.jcr.RepositoryException, RepositoryException, ItemNotFoundException, HttpException, InternalErrorException, IOException {
		try{

			System.out.println("Remove " + absPath);
			session.getItem(absPath).remove();
			session.save();
		} finally {

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



}

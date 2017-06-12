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
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.commons.httpclient.HttpException;
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
//	private static final String URL = "http://localhost:8080/jackrabbit-webapp-2.14.0";
//	private static final String USER ="admin";
//	private static final String PASS = "admin";
	private static final String USER ="workspacerep.imarine";
	private static final String PASS ="workspacerep.imarine";
	//	private static final String PASS = "gcube2010*onan";
	
	static Session session =  null;
	private static Workspace workspace;

	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {

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

			NodeIterator sharedFolders = session.getNode("/Share").getNodes();

			while ( sharedFolders.hasNext()) {

				Node sharedFolder = sharedFolders.nextNode();
				String id = sharedFolder.getIdentifier();
				String path = sharedFolder.getPath();

				System.out.println("__________________________________");
				System.out.println("-> id " + id);
				System.out.println("-> path " + path);

				
//				if (true)
//					continue;
				try{

					Node sharedNode = session.getNodeByIdentifier(id);

//					String newShared = copy(sharedNode.getPath());
//					System.out.println("Copy is " + newShared);



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

						String parentNode = getItemById(parentId);
//						try{
//							remove(parentNode+ "/"+nodeName);
//						} catch (Exception e) {
//							System.out.println("Error removing " +parentNode+ "/"+nodeName);
//						}
//
//
//						try{
//							session.refresh(true);
//							sharedNode.remove();
//							session.save();
//							//							remove(path);
//						} catch (Exception e) {
//							System.out.println("Error removing " +path);
//						}

						//						try{
						//						remove(path);
						//					} catch (Exception e) {
						//						System.out.println("Error removing " +path);
						//					}
				
						//						clone(copied, parentNode+ "/"+nodeName);
						clone(sharedNode, parentNode+ "/"+nodeName);

						//						System.out.println("Remove " + absPath);
						//						remove(absPath);

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

	private static String createNewShared(Node parent, String lastAction, String groupID, String scope, String displayName, boolean isVRE, List<String> users, List<String> members, boolean isSystem) throws InsufficientPrivilegesException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, WorkspaceFolderNotFoundException, InternalErrorException, RepositoryException, AccessDeniedException, ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, javax.jcr.ItemNotFoundException, javax.jcr.RepositoryException, MalformedURLException {

		String newpath;
		Node newNode = null;
		try{

			String id = UUID.randomUUID().toString();
			newNode = parent.addNode(id, "nthl:workspaceSharedItem");
			System.out.println("Added " + id + " to " + parent.getPath());

			newNode.setProperty("hl:lastAction", lastAction);

			session.save();
			System.out.println("ID new folder " + newNode.getIdentifier());

			try{
				newNode.setProperty(NodeProperty.GROUP_ID.toString(), groupID);
			}catch (Exception e) {
				System.out.println(NodeProperty.GROUP_ID + " not present");
			}
			try{
				newNode.setProperty(NodeProperty.SCOPE.toString(), scope);
			}catch (Exception e) {
				System.out.println(NodeProperty.SCOPE + " not present");
			}


			try{
				newNode.setProperty(NodeProperty.DISPLAY_NAME.toString(), displayName);
			}catch (Exception e) {
				System.out.println("Problem setting " + NodeProperty.DISPLAY_NAME);
			}
			try{
				newNode.setProperty(NodeProperty.IS_VRE_FOLDER.toString(), isVRE);
			}catch (Exception e) {
				System.out.println("Problem setting " + NodeProperty.IS_VRE_FOLDER);
			} 

			try{
				Node usersNode;
				if (newNode.hasNode(NodeProperty.USERS.toString()))
					usersNode = newNode.getNode(NodeProperty.USERS.toString());
				else				
					usersNode = newNode.addNode(NodeProperty.USERS.toString());

				for(String user: users)
					usersNode.setProperty(user, user);


			}catch (Exception e) {
				System.out.println("Problem setting " + NodeProperty.USERS);
			}
			try{

				Node membersNode = newNode.addNode(NodeProperty.MEMBERS.toString());
				for (String member: members)
				{
					if(!membersNode.hasNode(member))
						membersNode.addNode(member);
				}
			}catch (Exception e) {
				System.out.println("Problem setting " + NodeProperty.MEMBERS );
			}


			try{
				newNode.setProperty(NodeProperty.IS_SYSTEM_FOLDER.toString(), isSystem);
			}catch (Exception e) {
				System.out.println(NodeProperty.IS_SYSTEM_FOLDER + " not present.");
			}	

			session.save();
			newpath = newNode.getPath();
		} finally {

		}
		return newpath;
	}



	private static String copy(String absPath) throws javax.jcr.ItemNotFoundException, javax.jcr.RepositoryException, RepositoryException, ItemNotFoundException, HttpException, InternalErrorException, IOException {
		try{

			Node node = session.getNode(absPath);

			String parentPath = node.getParent().getPath();
			String tmpFolder = parentPath+ "/" +UUID.randomUUID().toString();
			System.out.println("Copy from "+ absPath + " to " + tmpFolder);
			session.getWorkspace().copy(absPath, tmpFolder);
			session.save();
			return tmpFolder;

			//			System.out.println("Move " + tmpFolder + " to  " + absPath);
			//			
			//			session.move(tmpFolder, absPath);
			//			session.save();

		} finally {

		}

	}
	private static void remove(String absPath) throws javax.jcr.ItemNotFoundException, javax.jcr.RepositoryException, RepositoryException, ItemNotFoundException, HttpException, InternalErrorException, IOException {
		try{

			System.out.println("Remove " + absPath);

			session.getItem(absPath).remove();
			//			session.removeItem(absPath);
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


	public static void clone(Node node,  String destAbsPath) throws RepositoryException,
	InternalErrorException, ItemNotFoundException, HttpException, IOException, javax.jcr.RepositoryException {

		try{

			String srcAbsPath = node.getPath();
			Node srcNode = session.getNode(srcAbsPath);
			System.out.println("srcNode: "+ srcAbsPath + " - id:" + srcNode.getIdentifier());
			//			Node destNode = session.getNode(destAbsPath);
			//			System.out.println("destNode: "+ destNode + " - id:" + destNode.getIdentifier());

			System.out.println("Clone from "+srcAbsPath + " to " + destAbsPath);


			workspace.clone(workspace.getName(), srcAbsPath, destAbsPath, false);


		} finally {

		}

	}
}

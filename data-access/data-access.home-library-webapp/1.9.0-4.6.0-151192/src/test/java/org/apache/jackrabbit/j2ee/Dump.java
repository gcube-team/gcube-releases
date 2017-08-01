package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.accounting.AccoutingNodeWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.j2ee.workspacemanager.util.MetaInfo;
import org.apache.jackrabbit.j2ee.workspacemanager.util.Util;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.thoughtworks.xstream.XStream;


public class Dump {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {



		//		String rootScope = "/gcube";
		String rootScope ="/d4science.research-infrastructures.eu";

		ScopeProvider.instance.set(rootScope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

										String url = ap.address();
System.out.println(url);
//					String url = "http://ws-repo-test.d4science.org/home-library-webapp";
//					String url = "http://workspace-repository-prod.d4science.org/home-library-webapp";
					//					String url = "http://node11.d.d4science.research-infrastructures.eu:80/home-library-webapp";
					String user = ap.username();			
//					String pass = "workspacerep.imarine";
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());

					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

					Node node = session.getNodeByIdentifier("04eb0b50-5ad0-4aa4-b201-4f1cc7ffa075");
					
//					Node node = session.getRootNode().getNode("Home[2]");
					System.out.println(node.getPath());
					//					 Repository repository = new TransientRepository(new File("path_to_jackrabbit_home_dir"));
//					try {
//						//				            Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
//
//						final UserManager userManager = ((JackrabbitSession)
//								session).getUserManager();
//						Authorizable authorizable = userManager.getAuthorizable("workspacerep.imarine");
//						User myuser = (User) authorizable;
//						myuser.changePassword("gcube2010*onan");
//
//						session.save();
//					
//					} catch (RepositoryException e) {
//						System.out.println("Auth error.");
//						e.printStackTrace();
//					}finally {
//						session.logout();
//					}


					//					System.out.println(session.getRootNode().getPath());
					//					NodeIterator nodes = session.getNode("/Home/scarponi/Workspace/DataMinerAlgorithms").getNodes();
					//
					//					LockManager lockManager = session.getWorkspace().getLockManager();
					//
					//					while (nodes.hasNext()){
					//						Node child = nodes.nextNode();
					//
					//						if (lockManager.isLocked(child.getPath())){
					//							System.out.println(child.getPath());
					////							
					////							child.remove();
					////							session.save();
					//
					//							//Returns the Lock object that applies to the node at the specified absPath.
					////							Lock lock = lockManager.getLock(child.getPath());
					////							String ltoken = lock.getLockToken();
					////							System.out.println(ltoken);
					////
					////							//Adds the specified lock token to the current Session.
					////							lockManager.addLockToken(ltoken);
					////
					////							//Removes the lock on the node at absPath.
					////							lockManager.unlock(child.getPath());
					////							//Removes the specified lock token from this Session.
					////							lockManager.removeLockToken(ltoken);
					////							session.save();							
					//						}
					//					}
					//					System.out.println(nodes.getSize());
					//					System.out.println(session.getNodeByIdentifier("b2ad0a71-a2c3-4fa9-9b88-b6d3a436cf0e").getPath());

					//
					//					Boolean flag = false;
					//					String id = "01667d30-7087-44a5-8bf8-77d858825168";
					//					try{
					//
					//						LockManager lockManager = session.getWorkspace().getLockManager();
					//						Node node = session.getNodeByIdentifier(id);


					//						Lock nodeLock = lockManager.getLock(node.getPath());
					//						String lockToken = nodeLock.getLockToken();
					////						if (lockToken != null) {
					////							lockManager.addLockToken(lockToken); 
					////							System.out.println("add token");
					////						}
					////						lockManager.unlock(node.getPath()); 
					////						session.save();
					//						lockManager.removeLockToken(lockToken);
					//						session.save();

					//
					//						Lock lock = lockManager.lock(node.getPath(), true, true, 0, session.getUserID());
					//						System.out.println(lockManager.isLocked(session.getNodeByIdentifier(id).getPath()));
					//						System.out.println("LOCK on Node: " + lock			}
					//
					//					System.out.println(flag);
					//					Thread.sleep(5000);
					//					session.logout();
					//					System.out.println("Done!");.getNode().getPath());
					//						session.save();
					//						flag = true;
					//
					//					} catch (Exception e) {
					//						e.printStackTrace();
					//						//						logger.error("Error locking item with id: " + id + " in session " + sessionId, e);
					//					}
					//
					//					System.out.println(flag);
					//					Thread.sleep(5000);
					//					session.logout();
					//					System.out.println("Done!");

					//										NodeIterator homes = session.getRootNode().getNode("Home").getNodes();
					//					String scope = rootScope;
					//					String serviceName = "home-library";

					//															Node node =  session.getNodeByIdentifier("d09b190c-3703-46d3-a21a-89857ea21c22");
					//															System.out.println(node.getPath());




					//
					//					System.out.println("CHECK....");
					//					Node content = node.getNode("jcr:content");
					//					if (content.hasProperty("jcr:data")){
					//						//	System.out.println(node.getPath());
					//						Binary bin = content.getProperty("jcr:data").getBinary();
					//						InputStream stream = bin.getStream();
					//						System.out.println(stream.available());
					//						stream.close();
					//					}
					//										while(homes.hasNext()){
					//											
					//											Node userNode = homes.nextNode();
					//											String login = userNode.getName();
					//											
					//											if (login.startsWith("rep:"))
					//												continue;
					//											System.out.println(login);
					//											
					//											Node node = session.getRootNode().getNode("Home/"+ login +"/OutBox/");
					//
					//											System.out.println(node.getPath());
					////											String login = "cristina.garilao";
					//											GCUBEStorage storage = new GCUBEStorage(login);
					//											getChildren(login, node, storage, session);
					//											
					//											
					//	
					//					//
					//					//						GCUBEStorage storage = new GCUBEStorage(login, scope, serviceName);
					//					//
					//					//						if (!login.startsWith("rep:")){
					//					//							getChildren(login, userNode, storage, session);
					//					//						}
					//					//
					//											
					//											
					//										}




					//					File f=new File("392.csv");
					//					System.out.println(f.getAbsolutePath());







					//
					//					OutputStream out = new FileOutputStream(f);
					//					
					//					int read = 0;
					//					byte[] bytes = new byte[1024];
					//
					//					while ((read = stream.read(bytes)) != -1) {
					//						out.write(bytes, 0, read);
					//					}




					//					byte buf[]=new byte[1024];
					//					int len;
					//					while((len=stream.read(buf))>0)
					//						out.write(buf,0,len);
					//					out.close();
					//					stream.close();
					//					System.out.println("\nFile is created...................................");


					//					 InputStream fileStream = node.getProperty("jcr:data").getStream();
					//					 System.out.println(fileStream.available());
					//					 fileStream.close();

				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}


	}
	private static void getChildren(String login, Node userNode, GCUBEStorage storage, Session session) throws RepositoryException, IOException {
		//		if (userNode.hasProperty("hl:portalLogin") && (!userNode.getName().equals("hl:owner"))){
		//			String portalLogin = userNode.getProperty("hl:portalLogin").getString();
		//			System.out.println(portalLogin);
		//			if (login.equals(portalLogin)){
		System.out.println(userNode.getPath());
		if (userNode.hasNode("jcr:content")){

			Node content = userNode.getNode("jcr:content");

			if (content.hasProperty("jcr:data")){

				Binary bin = null;
				InputStream stream = null;

				try{
					bin = content.getProperty("jcr:data").getBinary();
					stream = bin.getStream();
					int size = stream.available();
					System.out.println(userNode.getPath() + " - " + size);

					if (size>200){
						String mimeType = content.getProperty("jcr:mimeType").getString();
						String storageId = null;
						try {
							storageId = storage.putStream(stream, userNode.getPath(), mimeType);
							System.out.println("Saved into storage " + userNode.getPath() + " with mimetype " + mimeType);
						} catch (RemoteBackendException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if(storageId!=null)
							saveToStorage(content, storageId, userNode.getPath(), session);
					}
				}finally{
					if (stream!=null)
						stream.close();
					if (bin!=null)
						bin.dispose();
				}

			}
		}
		//			}
		//		}

		NodeIterator iterator = userNode.getNodes();
		while(iterator.hasNext()){
			Node node = iterator.nextNode();
			getChildren(login, node, storage, session);
		}

	}
	private static void saveToStorage(Node nodeContent, String storageId, String remotePath, Session session) throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException, ConstraintViolationException, InvalidItemStateException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
		try{
			nodeContent.setProperty(NodeProperty.STORAGE_ID.toString(), storageId);
		}catch (Exception e) {
			System.out.println("error setting propery " + NodeProperty.STORAGE_ID);
		}
		try{
			nodeContent.setProperty(NodeProperty.REMOTE_STORAGE_PATH.toString(), remotePath);
		}catch (Exception e) {
			System.out.println("error setting propery " + NodeProperty.REMOTE_STORAGE_PATH);
		}
		try{
			ByteArrayInputStream  binaryUrl = new ByteArrayInputStream(nodeContent.getPath().getBytes());
			Binary binary = nodeContent.getSession().getValueFactory().createBinary(binaryUrl);
			nodeContent.setProperty(NodeProperty.DATA.toString(), binary);
		}catch (Exception e) {
			System.out.println("error setting propery " + NodeProperty.DATA);
		}

		session.save();

	}







}

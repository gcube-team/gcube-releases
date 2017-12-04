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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import org.apache.jackrabbit.j2ee.oak.CleanerTask;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.accounting.AccoutingNodeWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.j2ee.workspacemanager.util.MetaInfo;
import org.apache.jackrabbit.j2ee.workspacemanager.util.Util;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;import org.gcube.common.homelibrary.home.HomeLibrary;
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



		String rootScope = "/gcube";
		//		String rootScope ="/d4science.research-infrastructures.eu";

		ScopeProvider.instance.set(rootScope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);
		Session session = null;
		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					String url = ap.address();
					//										System.out.println(url);
					//					String url = "http://ws-repo-test.d4science.org/home-library-webapp";
					//					String url = "http://workspace-repository-prod1.d4science.org/home-library-webapp";
					//					String url = "http://node11.d.d4science.research-infrastructures.eu:80/home-library-webapp";
					String user = ap.username();			
					//					String pass = "workspacerep.imarine";
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());

					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));
					//					session.getNode("/Home/scarponi/Workspace/Trash").remove();
					//					session.save();



					//					Node node = session.getNode("/Home/d4science.research-infrastructures.eu-SmartArea-SmartCamera-Manager/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-SmartArea-SmartCamera");

					//					String id = "2b504c5f-5698-478d-949d-9dfd9cf38128";
					//					Node node1 = session.getNodeByIdentifier(id);
					//					System.out.println(node1.getPath());

					String path = "/Home/raspberry.camera/Workspace/MySpecialFolders/gcube-devsec-SmartCamera/camera-test/";
					Node node = session.getNode(path);
					node.remove();
//					NodeIterator it = session.getNode(path).getNodes();
//					while (it.hasNext()){
//						Node node = it.nextNode();
//						System.out.println(node.getPath());
//						node.remove();
//					}
					session.save();
					//					System.out.println(node.isLocked());

					//					LockManager lm = session.getWorkspace().getLockManager();
					//					Lock token = lm.getLock(node.getPath());
					//					session.addLockToken(token.getLockToken());
					//					System.out.println(token.getSecondsRemaining());
					//					try {
					//					    lm.unlock(node.getPath());
					//					}catch (Exception e) {
					//					    System.out.println(e);
					//					}

					//					NodeIterator nodes = session.getNode("/Share").getNodes();
					//					while (nodes.hasNext()){
					//						Node node = nodes.nextNode();
					////						PropertyIterator properties = node.getProperties();
					////						while (properties.hasNext()){
					////							System.out.println(properties.nextProperty().getName());
					////						}
					//						
					//						String owner;
					//						if (node.hasProperty("hl:portalLogin")){
					//							owner = node.getProperty("hl:portalLogin").getString();
					//						if (owner.equals("scarponi"))
					//							System.out.println(node.getPath() + " - " + owner);
					//						
					//						}
					//						
					//					}

					//					ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
					//
					//
					//					NodeIterator children = session.getNode("/Home/scarponi/Workspace/Trash/").getNodes();
					//					System.out.println(children.getSize());
					//					int i = 0;
					//					while(children.hasNext()){
					//						Node child = children.nextNode();
					//						System.out.println(i++ + " - Removing " + child.getPath());
					//
					//						RemoveTask task = new RemoveTask(child.getIdentifier());
					//						System.out.println("A new task has been added : " + task.getName());
					//						executor.execute(task);
					//						System.out.println();
					//
					//					}		
					//					
					//					System.out.println("Maximum threads inside pool " + executor.getMaximumPoolSize());
					//					executor.shutdown();
					//
					//					int max = (int) executor.getTaskCount(); // its gonna be somewhere around 600-700 I think
					//
					//					// as long as the excutor isnt dead yet.
					//					while (!executor.awaitTermination(2, TimeUnit.SECONDS))
					//					{
					//						int done = (int) executor.getCompletedTaskCount();
					//						System.out.println("Current status: "+done+"/"+max+"   "+(int) ((double) done / max * 100)+"%");
					//					}
					//
					//					//			while (!executor.isTerminated()) {}  
					//
					//					System.out.println("Finished all threads");  

				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			session.logout();
		}


	}

}

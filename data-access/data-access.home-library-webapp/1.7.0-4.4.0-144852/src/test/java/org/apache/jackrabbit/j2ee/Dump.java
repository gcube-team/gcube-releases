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
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.api.security.user.Authorizable;
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



		String rootScope = "/gcube";


		ScopeProvider.instance.set(rootScope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					//					String url = ap.address();

					String url = "http://ws-repo-test.d4science.org/home-library-webapp";
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";
					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());

					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

										NodeIterator homes = session.getRootNode().getNode("Home").getNodes();
					String scope = rootScope;
					String serviceName = "home-library";

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
										while(homes.hasNext()){
											
											Node userNode = homes.nextNode();
											String login = userNode.getName();
											
											if (login.startsWith("rep:"))
												continue;
											System.out.println(login);
											
											Node node = session.getRootNode().getNode("Home/"+ login +"/OutBox/");

											System.out.println(node.getPath());
//											String login = "cristina.garilao";
											GCUBEStorage storage = new GCUBEStorage(login);
											getChildren(login, node, storage, session);
											
											
	
					//
					//						GCUBEStorage storage = new GCUBEStorage(login, scope, serviceName);
					//
					//						if (!login.startsWith("rep:")){
					//							getChildren(login, userNode, storage, session);
					//						}
					//
											
											
										}




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
					session.logout();
					System.out.println("Done!");



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

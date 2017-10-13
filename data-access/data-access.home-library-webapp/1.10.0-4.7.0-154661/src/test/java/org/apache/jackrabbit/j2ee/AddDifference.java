package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.privilegemanager.PrivilegeManager;
import org.gcube.common.homelibrary.home.workspace.usermanager.UserManager;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import lombok.patcher.Symbols;


public class AddDifference {
	private static final String nameResource 				= "HomeLibraryRepository";

	private static final String NAME = "ISExporter";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {



		String rootScope = "/gcube";
//								String rootScope ="/d4science.research-infrastructures.eu";


		ScopeProvider.instance.set(rootScope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);
		//		https://workspace-repository-dev.research-infrastructures.eu

		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

//									String url = ap.address();
//										System.out.println(url);
										String	url = "http://node11.d.d4science.research-infrastructures.eu:80/home-library-webapp";
//					String url = "http://ws-repo-test.d4science.org/home-library-webapp";
									
//									String url = "http://workspace-repository-prod1.d4science.org/home-library-webapp";
									
//									String url_old = "http://localhost:8080/jackrabbit-webapp-2.14.0";
//										String admin = ap.username();						
//										String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
					//
					//					
					//					
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					String admin = "workspacerep.imarine";
					String pass = "gcube2010*onan";

					Session session = repository.login( 
							new SimpleCredentials(admin, pass.toCharArray()));
					
					Node item = session.getNode("/Home/valentina.marioli/Workspace/exportTest/");
//					session.getWorkspace().copy(item.getPath(), "/Home/valentina.marioli/Workspace/exportTest00/");
//					
//					session.save();
					
//					 /Share/042efb85-240b-4a04-93d3-bca3924b7503/camera02 (copy)
					 //export the data
				    ByteArrayOutputStream baos = new ByteArrayOutputStream();
				    System.out.println("export " + item.getPath());
				    session.exportDocumentView(item.getPath(), baos, true, false);
					
				    System.out.println("done");
				    System.out.println("import");
				    session.importXML("/Home/valentina.marioli/Workspace/importTest/", new ByteArrayInputStream(baos.toByteArray()),
		                      ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
				    session.save();
				    System.out.println("done");
		    
//					URLRemoteRepository repository_old = new URLRemoteRepository(url_old + "/rmi");
//					Session session_old = repository.login( 
//							new SimpleCredentials(admin, admin.toCharArray()));
					
//					System.out.println(session.getNodeByIdentifier("92cbadba-a8a7-4567-a8c2-194b1d416722").getPath());
//					String path = "/Home/valentina.marioli/Workspace/test[00]";
//					System.out.println(Text.getRelativeParent(path, 1));
//					System.out.println(Text.getName(path));
//					String parent = Text.getRelativeParent(path, 1);
//					String name = (Text.escapeIllegalJcrChars(Text.getName("/Home/valentina.marioli/Workspace/test[00]")));
//					Node node = session.getNode(parent + "/"+ name);
//					System.out.println(node.getPath());
					//create version and users
//					NodeIterator homes = session.getRootNode().getNode("Home").getNodes();
//					while(homes.hasNext()){
//						Node login = homes.nextNode();
//			
//						String user = login.getName();
//						if (user.startsWith("rep:"))
//							continue;
//						System.out.println(user);
//						UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
//						String version = "3.1.1";
//						String pwd = getSecurePassword(user);
//						if (um.createUser(user, pwd, version))
//							System.out.println(user + " created");
//
//					}

					
					
					//create privileges
//					PrivilegeManager am = HomeLibrary.getHomeManagerFactory().getPrivilegeManager();
//					
//					am.createCostumePrivilege("hl:noOwnershipLimit", new String[] {});
//					am.createCostumePrivilege("hl:writeAll", new String[] {"jcr:write", "hl:noOwnershipLimit"});
//					am.createCostumePrivilege("hl:removeSharedRoot", new String[] {});
//					System.out.println("done");



					//					JCRAccessControlManager accessManager = new JCRAccessControlManager(session, login);
					//					 Map<String, List<String>> acls = accessManager.getACL(absPath);
					//					 System.out.println(acls.toString());
					//					String acl = accessManager.getACLByUser(absPath);
					//					System.out.println(acl);

					//					Session session = repository.login(new GuestCredentials());	

					//					String nodeID = "0ad6cb49-bb1e-49bf-82da-f1f30588f40d";
					//					Node node = session.getNodeByIdentifier(nodeID);
					//					Node node = session.getNode("/Home/statistical.manager/Workspace/DataMiner/Computations");
					//					System.out.println(node.getPath());
					//					System.out.println();
					//					session.refresh(true);
					//					node.save();
					//					session.save();

					//					getDelegateItemByPath(session, "/Home/valentina.marioli/Workspace/Trash/d92c445c-9a6b-455e-906e-e5d4f3a4ca58/shot-20170308-22459-1anhiop (1).jpeg" ,"valentina.marioli");

					//					System.out.println(node.isLocked());

					//					JCRAccessControlManager accessManager = new JCRAccessControlManager(session, "valentina.marioli");
					//					String acl = accessManager.getACLByUser("valentina.marioli", "/Home/valentina.marioli/Workspace/versions/version.png");
					//					System.out.println(acl);


					//					JCRTrash trash = new JCRTrash(session, "valentina.marioli");
					//					trash.emptyTrash();
					//					String nodeID = node.getIdentifier();

					//					Node nodeContent = node.getNode(NodeProperty.CONTENT.toString());			
					//					NodeType[] iterator = nodeContent.getMixinNodeTypes();
					//					int size = iterator.length;
					//					System.out.println("size " + size);
					//					int i=0;
					//					while(i<=size -1){
					//						System.out.println(iterator[i].getName());
					//						i++;
					//					}
					//					System.out.println(node.getPath());
					//					JCRVersioning versioning = new JCRVersioning(session, "andrea.rossi");
					//					versioning.removeVersion(nodeID, "1.2");
					//					JCRAccessControlManager accessManager = new JCRAccessControlManager(session, "valentina.marioli");
					//					System.out.println(accessManager.getACLByUser("valentina.marioli", node.getPath()));
					//					System.out.println(accessManager.getEACL(node.getPath()));
					//					


					//					versioning.restoreVersion(nodeID, node.getPath(), "1.0");
					//					Node content = node.getNode(NodeProperty.CONTENT.toString());	
					//					String remotePath = content.getProperty(NodeProperty.REMOTE_STORAGE_PATH.toString()).getString();
					//					String versionID = "1.0";
					////					versioning.restoreVersion(nodeID, remotePath, versionID);
					//					
					//					long currentSize = content.getProperty(NodeProperty.SIZE.toString()).getLong();
					//					System.out.println(currentSize);
					//					
					//									WorkspaceVersion myVersion = versioning.getVersion(nodeID, "1.1");
					//									System.out.println(myVersion.toString());
					//					System.out.println(myVersion.getName());
					//					WorkspaceVersion currentVersion = versioning.getLastVersion(nodeID);
					//					System.out.println(currentVersion.getName());


					//										List<WorkspaceVersion> history = versioning.getVersionHistory(nodeID);
					//										for (WorkspaceVersion version : history){
					//											System.out.println(version.toString());
					//										}



				}
			}
		}finally{}
	}

	//	private static ItemDelegate getDelegateItemByPath(Session session, String absPath, String login) throws Exception {
	//
	//		Node node = session.getNode(absPath);
	//		System.out.println(node.getPath());
	//		NodeManager wrap = new NodeManager(node, login);
	//
	//		return wrap.getItemDelegate();
	////		return null;
	//
	//	}


	public static Session newSession(String login, URLRemoteRepository rep) throws Exception{

		System.out.println("Getting a new session for user " + login);

		Session session = null;
		try{
			session = rep.login( 
					new SimpleCredentials(login, getSecurePassword(login).toCharArray()));


		} catch (Exception e) {
			throw new Exception("Error getting a new session");
		} 

		return session;
	}

	//create a password
	public static String getSecurePassword(String user) throws Exception {
		String digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(user.getBytes("UTF-8"));

			//converting byte array to Hexadecimal String
			StringBuilder sb = new StringBuilder(2*hash.length);
			for(byte b : hash){
				sb.append(String.format("%02x", b&0xff));
			}
			digest = sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return digest;
	}


	protected static String getUsername() throws Exception {
		String token = SecurityTokenProvider.instance.get();
		return Constants.authorizationService().get(token).getClientInfo().getId();
	}

	
}


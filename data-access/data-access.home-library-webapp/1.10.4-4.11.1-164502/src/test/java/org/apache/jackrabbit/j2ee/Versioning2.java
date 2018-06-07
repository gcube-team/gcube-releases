package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;

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


public class Versioning2 {
	private static final String nameResource 				= "HomeLibraryRepository";

	private static final String NAME = "ISExporter";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {



		//		String rootScope = "/gcube";
		String rootScope ="/d4science.research-infrastructures.eu";


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

					String url = ap.address();
					System.out.println(url);
					//										String	url = "http://node11.d.d4science.research-infrastructures.eu:80/home-library-webapp";
					//					String url = "http://ws-repo-test.d4science.org/home-library-webapp";

					//									String url = "http://workspace-repository-prod1.d4science.org/home-library-webapp";


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


					NodeIterator homes = session.getNode("/Home").getNodes();

					while(homes.hasNext()){
						Node home = homes.nextNode();
						//						System.out.println(home.getPath());
						if (!home.getName().startsWith("rep:")){
							Node node = home.getNode("Workspace");
							//							System.out.println(node.getPath());
							//						//					session.getWorkspace().move(node.getPath(), "/Home/massimiliano.assante/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BlueBridgeProject/Work Packages/WP5 Blue Assessment/D5.3 Blue Assessment VRE Specification - Revised Version");
							//						//					session.save();
							//						//					System.out.println(node.getPath());
							//
							visit(node);
						}
					}



					//					/Home/massimiliano.assante/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BlueBridgeProject/Proposal/Contributes/Pillars and Use Cases Development/Pillar E. SMEs & Innovation%20
					//					/Home/massimiliano.assante/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BlueBridgeProject/Meetings/Project Meetings/2017.01.12 First Project Review/Final version of Review Slides%20
					//					/Home/massimiliano.assante/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-BlueBridgeProject/Work Packages/WP7 Blue Environment/MS17 Blue Environment VRE Software%3A I Major Release%20


				}
			}
		}finally{}
	}


	private static void visit(Node node) throws RepositoryException {


		if(node.getName().endsWith("%20")){
			System.out.println(node.getPath());
			String newName = node.getPath().substring(0, node.getPath().length() - 3);
//			System.out.println(newName);
			try{
			node.getSession().move(node.getPath(), newName);
			node.getSession().save();
			} catch (Exception e) {
				System.out.println("Error moving node " + node.getPath());
			
			}
		}
		NodeIterator iterator = node.getNodes();
		while (iterator.hasNext()){
			Node child = iterator.nextNode();
			if(child.getName().equals(".catalogue"))
				continue;
			visit(child);
		}

	}

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


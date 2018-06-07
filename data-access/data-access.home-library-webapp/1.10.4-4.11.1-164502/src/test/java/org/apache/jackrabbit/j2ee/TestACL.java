package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class TestACL {
	private static final String nameResource 				= "HomeLibraryRepository";
	private static final String WRITE_ALL 		= "hl:writeAll";
	private static final String READ 			= "jcr:read";
	private static final String WRITE 			= "jcr:write";	
	private static final String ADMINISTRATOR 	= "jcr:all";;
	
	private static final String READ_ONLY 		= "jcr:read";
	private static final String WRITE_OWNER 	= "jcr:write";

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


		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

//					String url = ap.address();
//					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";
//
//					String user = ap.username();						
//					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
					


					String	url = "http://ws-repo-test.d4science.org:8080/home-library-webapp";
					String user = "workspacerep.imarine";						
					String pass = "gcube2010*onan";
	


					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));
					String absPath = "/Home/scarponi/Workspace";
					
			
					System.out.println(getACLByUser("scarponi",absPath, session));
					

				}
			}
		}finally{}
	}

	
	public static String getACLByUser(String login, String absPath, Session session) throws Exception {

		try {
			
			AccessControlManager accessControlManager = session.getAccessControlManager();

			Node node = session.getNode(absPath);
			String owner = getOwner(node);
			if (owner.equals(login)){
				return ADMINISTRATOR;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(ADMINISTRATOR)
			})){
				return ADMINISTRATOR;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(WRITE_ALL)
			})){
				return WRITE_ALL;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(WRITE)
			})){
				return WRITE_OWNER;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(READ)
			})){
				return READ_ONLY;
			}
		} catch (RepositoryException e) {
			throw new Exception("ACLType Unknown " + e);
		}
		return null;

	}
	
	private static String getOwner(Node node) throws PathNotFoundException, RepositoryException {
		String portalLogin;
		try{
			portalLogin = node.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		}catch (Exception e) {
			Node nodeOwner = node.getNode(NodeProperty.OWNER.toString());		
			portalLogin = nodeOwner.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		}
		return portalLogin;

	}

}


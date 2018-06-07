package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.MalformedURLException;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class GetVersionHistory {
	private static final String nameResource 				= "HomeLibraryRepository";

	public static final String VERSION_LABEL					= "hl:version";
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

					String url = ap.address();
					System.out.println(url);
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";

					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());


					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

					String absPath = "/Home/valentina.marioli/Workspace/";
					Node node = session.getNode(absPath);
					System.out.println(node.getIdentifier());
					VersionManager vm = session.getWorkspace().getVersionManager();

//					Version version = vm.getBaseVersion(absPath);
//					System.out.println(version.getPath());
					//print version history
								          VersionHistory history = vm.getVersionHistory(absPath);
								          for (VersionIterator it = history.getAllVersions(); it.hasNext();) {
								            Version version = (Version) it.next();
								            System.out.println("**************");
//								            System.out.println(version.getPrimaryNodeType().getName());
					////			            Node node = version.getFrozenNode();
//					version.addNode("test").setProperty("hl:remotePath", "test");
//					session.save();
					//			           for(PropertyIterator iterator = version.getProperties(); iterator.hasNext();){
					//			        	   Property property = (Property) iterator.next();
					//			        	   System.out.println(property.getName());
					//			           }
								            System.out.println(version.getName() + " - " + version.getCreated().getTime());
//								            System.out.println(version.getCreated().getTime());
								          }		




				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}


	}
	public void versioningBasics (Node parentNode, Session session) throws RepositoryException
	{
		VersionManager vm = session.getWorkspace().getVersionManager();
		//create versionable node
		Node node = parentNode.addNode("childNode", "nt:unstructured");
		node.addMixin(JcrConstants.MIX_VERSIONABLE);
		node.setProperty("anyProperty", "prop1");
		session.save();
		
		Version firstVersion = vm.checkin(node.getPath());

		//add new version
		Node child = parentNode.getNode("childNode");
		vm.checkout(child.getPath());
		child.setProperty("anyProperty", "prop2");
		session.save();
		vm.checkin(child.getPath());

		//print version history
		VersionHistory history = vm.getVersionHistory(child.getPath());
		for (VersionIterator it = history.getAllVersions(); it.hasNext();) {
			Version version = (Version) it.next();
			System.out.println("Version: " + version.getName() + " created on " +  version.getCreated().getTime());
		}

		//restoring old version
		vm.checkout(child.getPath());
		vm.restore(firstVersion, true);
	}


}

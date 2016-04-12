package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.model.exceptions.InternalErrorException;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.globus.wsrf.client.GetProperties;

import com.thoughtworks.xstream.XStream;


public class GetItemById {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {



		String rootScope = "/gcube";
//		String rootScope = ("/d4science.research-infrastructures.eu");
		ScopeProvider.instance.set(rootScope);

		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);
		Session session =null;

		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					String url = ap.address();
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";

					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());


					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));
					
					
					String id = "d84ff106-c64f-4f04-95a1-55ac8827ab71";
					
					Node node = session.getNodeByIdentifier(id);
					ItemDelegate item = null;
					NodeManager wrap = new NodeManager(node, "");
					
						item = wrap.getItemDelegate();
						System.out.println(item.getPath());
						System.out.println(item.isTrashed());
					
					
					

//					
//					Node node = session.getNode("/Home/d4science.research-infrastructures.eu-gCubeApps-EGI_Engage-Manager/");
//					System.out.println(node.getPath());

//					node.remove();
//					session.save();
					
//					Node myNode = node.getParent().addNode(Text.escapeIllegalJcrChars("èèè*testattaaaa"), node.getParent().getPrimaryNodeType().getName());
//					myNode.setProperty("hl:lastAction", node.getProperty("hl:lastAction").getString());
//					session.save();
//					System.out.println(Text.unescapeIllegalJcrChars(myNode.getName()));
//					System.out.println(myNode.getPath());
//
//					ItemDelegate item = null;
//					NodeManager wrap = new NodeManager(node, "");
//
//					item = wrap.getItemDelegate();
//					System.out.println(item.getPath());


				}
			}
		}finally{
			if (session!=null)
				session.logout();
		}
	}


}

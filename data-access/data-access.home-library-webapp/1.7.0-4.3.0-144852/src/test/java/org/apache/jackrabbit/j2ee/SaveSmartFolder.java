package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class SaveSmartFolder {
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

					String url = ap.address();
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";
					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());

					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

					ItemDelegate delegate = new ItemDelegate();
					delegate.setId(null);
					delegate.setName("test");
					delegate.setTitle("test");
					delegate.setDescription("aa");
					delegate.setLastModifiedBy("valentina.marioli");
					delegate.setParentId("21e227fa-3710-44e2-8618-f7e82267a4fb");
					delegate.setLastModificationTime(null);
					delegate.setCreationTime(null);
					delegate.setProperties(null);
					delegate.setPath(null);
					delegate.setOwner("valentina.marioli");
					delegate.setPrimaryType("nthl:workspaceLeafItem");
					delegate.setLastAction(WorkspaceItemAction.CREATED);
					delegate.setShared(false);
					delegate.setLocked(false);
					delegate.setAccounting(null);
					delegate.setMetadata(null);
					HashMap<NodeProperty, String> map = new HashMap<NodeProperty, String>();
					map.put(NodeProperty.QUERY, "test");
					map.put(NodeProperty.CONTENT, "nthl:smartFolderContent");
					delegate.setContent(map);

					System.out.println("item delegate: " + delegate);
					ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, "");
					delegate = wrapper.save(session, false);
					System.out.println("NEW ITEM: " + delegate.getPath());
//					String absPath = "/Home/valentina.marioli/Workspace/testGcubeItem";
//					NodeWrapper wrapNode = new NodeWrapper(session.getNode(absPath));
//					System.out.println("----> " + wrapNode.getItemDelegate().toString());



				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}


	}

}

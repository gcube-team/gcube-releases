package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class CreateClone {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
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

//					String srcId = "433ae7a9-e25d-4239-b7dd-5e767009a744";
//					String destId = "c1321c58-a86a-4584-8b11-066ee395b54d";
		
					
					String login = "valentina.marioli";
					String srcPath = "/Home/valentina.marioli/Workspace/00000/aaa/";
					String destPath = "/Home/valentina.marioli/Workspace/00000/bbb/clone";
					ItemDelegate item = createClone(session, srcPath, destPath, login);
//					System.out.println("********* " + item.getPath());

				}
			}
		}finally{}
	}
	
	
	
	private static ItemDelegate createClone(Session session, String srcPath, String destPath, String login) {
		

		Node srcNode = null;
//		Node destNode = null;
		ItemDelegate item = null;
		try{
			srcNode = session.getNode(srcPath);
			System.out.println("src node " + srcNode.getPath());
//		destNode = session.getNode(destPath);
//			System.out.println("destNode node " + destNode.getPath());
//			
//			System.out.println("DEST: " +destNode.getPath() + "/clone00");
			session.getWorkspace().clone(session.getWorkspace().getName(), srcNode.getPath(), destPath, false);
			session.save();
			
			NodeIterator sharedSet = srcNode.getSharedSet();
			while (sharedSet.hasNext()){
				Node set = sharedSet.nextNode();
				System.out.println("get Shared Set: "+ set.getPath());
				set.removeShare();
				
			}
			Node link = session.getNode(destPath);
//			link.


//
			NodeManager wrap = new NodeManager(link, login);
			item = wrap.getItemDelegate();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}
}

package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.lang.Validate;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.thoughtworks.xstream.XStream;


public class GetParentsByID {
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


					//					String id = "4c8fb663-3cd4-4f23-a2e2-e9adc8dd9610";
					//					
					//					Node node = session.getNodeByIdentifier(id);
					//					ItemDelegate item = null;
					//					NodeManager wrap = new NodeManager(node, "");
					//				
					//						item = wrap.getItemDelegate();
					//						System.out.println(item.getPath());
					//						System.out.println(item.isTrashed());
					//					

					//					Node root = session.getNodeByIdentifier("36255291-1157-488a-8b8c-99fe8c94f599");
					//					System.out.println(root.getPath());

					Node root = session.getNode("/Home/valentina.marioli/Workspace/TestPreProd");
					NodeIterator iterator = root.getNodes();
					while(iterator.hasNext()){
						Node node = iterator.nextNode();
						if(!node.getName().startsWith("rep:") && !node.getName().startsWith("hl:")){
							System.out.println(node.getPath());
					
								List<ItemDelegate> test = getParentsById(session, node.getIdentifier(), "test.user");
								System.out.println(test.size());
							
						}
					}

					//					Node root = session.getNode("/Home/valentina.marioli/Workspace/StoragePatch/test");
					//					
					//					List<ItemDelegate> test = getParentsById(session, root.getIdentifier(), "valentina.marioli");
					//					System.out.println(test.size());


				}
			}
		}finally{
			if (session!=null)
				session.logout();
		}
	}




//	private static List<ItemDelegate> getParentsById(Session session, String identifier,
//			String login) throws Exception {
//
//		Validate.notNull(identifier, "Item id must be not null");
//		Validate.notNull(login, "Login must be not null");
//
//		Node node = session.getNodeByIdentifier(identifier);
//
//		List<ItemDelegate> parents = new ArrayList<ItemDelegate>();
//
//		while(!isRoot(node) && (node!=null)) {
//
//			System.out.println("* " + node.getPath());
//			ItemDelegate item = null;
//			try {
//				NodeManager wrap = new NodeManager(node, login);
//				item = wrap.getItemDelegate();
//
//				parents.add(item);
//
//				if ((login!=null) && (item.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER))){
//					@SuppressWarnings("unchecked")
//					Map<String, String> users =  (Map<String, String>) new XStream().fromXML(item.getProperties().get(NodeProperty.USERS));
//					String[] user = users.get(login).split("/");
//					String parentId = user[0];
//					node = session.getNodeByIdentifier(parentId);
////					String userPath = null;
////					String[] user = null;
////					String parentId = null;
////
////					if (users!=null)
////						userPath = users.get(login);
////					if (userPath!=null)
////						user = userPath.split("/");					
////					if(user!=null)
////						parentId = user[0];
////					if(parentId!=null)
////						node = session.getNodeByIdentifier(parentId);
////					else
////						break;
//				}else
//					node = node.getParent();
//
//			} catch (Exception e) {
//				throw new Exception("Error getting parents by id: " + identifier, e);
//			}
//
//		}
//		return parents;
//	}


	
	private static List<ItemDelegate> getParentsById(Session session, String identifier,
			String login) throws Exception {

		Validate.notNull(identifier, "Item id must be not null");
		Validate.notNull(login, "Login must be not null");

		List<ItemDelegate> parents = new ArrayList<ItemDelegate>();

//		if (login.equals("test.user"))
//			return parents;

		Node node = session.getNodeByIdentifier(identifier);
		try {
		
			while(!isRoot(node) && (node!=null)) {
				
				System.out.println("** " + node.getPath());
				
				ItemDelegate item = null;
				NodeManager wrap = new NodeManager(node, login);

				item = wrap.getItemDelegate();

				parents.add(item);

				if (item.getPrimaryType().equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)){
					@SuppressWarnings("unchecked")
					Map<String, String> users =  (Map<String, String>) new XStream().fromXML(item.getProperties().get(NodeProperty.USERS));
//					if (users.containsKey(login)){
						String[] user = users.get(login).split("/");
						String parentId = user[0];
						node = session.getNodeByIdentifier(parentId);
//					}else
//						throw new Exception(node.getPath() + " does not contain user " + login);
				}else
					node = node.getParent();
				
			
			}

			} catch (Exception e) {
				System.out.println("Error getting parents by id: " + identifier);
//				throw new Exception(e);
			}
		return parents;
	}
	/**
	 * Check if the node is root in Jackrabbit
	 * @param node
	 * @return true if the node is root
	 * @throws InternalErrorException
	 * @throws ItemNotFoundException
	 * @throws RepositoryException
	 */
	public static boolean isRoot(Node node) throws RepositoryException { 
		Node parent = null;
		try{
			parent = node.getParent();
		}catch (ItemNotFoundException e){
			return true;
		}
		return (parent == null);
	}



}

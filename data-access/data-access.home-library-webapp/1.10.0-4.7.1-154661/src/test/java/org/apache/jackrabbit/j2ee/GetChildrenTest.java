package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;
import java.util.Properties;

import javax.jcr.GuestCredentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.api.JackrabbitRepositoryFactory;
import org.apache.jackrabbit.api.management.DataStoreGarbageCollector;
import org.apache.jackrabbit.api.management.RepositoryManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.RepositoryFactoryImpl;
import org.apache.jackrabbit.core.gc.GarbageCollector;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class GetChildrenTest {
	private static final String nameResource 				= "HomeLibraryRepository";
	private static final String DIR = "/home/gcube/tomcat/jackrabbit_derby";
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

		Session session = null;
		RepositoryManager rm = null;
		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					//					String url = ap.address();

					String url ="https://workspace-repository-prod.d4science.org/home-library-webapp";
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";

					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
					//					String user = "admin";						
					//					String pass = "admin";
					//					String url = "http://ws-repo-test.d4science.org/home-library-webapp";

					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					//					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");




//					JackrabbitRepository repository =  (JackrabbitRepository) JcrUtils.getRepository(url);

//					JackrabbitRepositoryFactory rf = new RepositoryFactoryImpl();
//										Properties prop = new Properties();
//										prop.setProperty("org.apache.jackrabbit.repository.home", DIR);
//										prop.setProperty("org.apache.jackrabbit.repository.conf", DIR + "/repository.xml");
//										JackrabbitRepository rep = (JackrabbitRepository) rf.getRepository(prop);
//							
//					
//					rm = rf.getRepositoryManager(rep);
//					session = rep.login( 
//							new SimpleCredentials(user, pass.toCharArray()));
//
//
//					DataStoreGarbageCollector gc = rm.createDataStoreGarbageCollector();
//					try {
//						gc.mark();
//						gc.sweep();
//					} finally {
//						gc.close();
//					}





					//					Node node = session.getNodeByIdentifier("d0ba527d-477a-45a8-882d-97d84f8a3ea9");
					//					System.out.println(node.getPath());
					////					System.out.println(node.getProperty("hl:portalLogin").getString());
					////					System.out.println(node.getName().equals("hl:owner"));
					//					long size = node.getProperty(NodeProperty.DATA.toString()).getBinary().getSize();
					//					System.out.println(size);
					//					node.remove();
					//					session.save();
					//					System.out.println(node.getPath());
					//					
					//
					//					System.out.println(node.getParent().getProperty("jcr:created").getDate());
					//					Node node = session.getNode("/Home/gianpaolo.coro/Workspace/");
					//					NodeIterator iterator = node.getNodes();
					//					
					//					while(iterator.hasNext()){
					//						Node child = iterator.nextNode();
					////						System.out.println(child.getPath());
					////						String nodeName = Text.unescapeIllegalJcrChars(child.getName());
					//					PropertyIterator properties = child.getProperties();
					//					while (properties.hasNext()){
					//						Property property = properties.nextProperty();
					////						System.out.println(property.getName());
					//						
					//						if (property.getName().equals("jcr:title"))
					//							System.out.println(property.getString());
					//						
					//					}
					//						System.out.println(child.getProperty("jcr:title").getString());
					//						if (nodeName.contains("[")){
					//							String name = nodeName.replace("[", "(");
					//							String name1 = name.replace("]", ")");
					////							System.out.println("rename " + nodeName + " in " + Text.escapeIllegalJcrChars(name1));
					//							System.out.println("** "  +child.getPath());
					//							System.out.println("** " + child.getParent().getPath() + "/" +Text.escapeIllegalJcrChars(name1));
					//							
					//							String newPath =child.getParent().getPath() + "/" +Text.escapeIllegalJcrChars(name1);
					//							
					//							
					////							session.move(arg0, arg1);
					////							session.move(child.getPath(), newPath);
					////							session.save();
					//						}
				}

				//					Session session = repository.login(new GuestCredentials());	

				//					Node node = session.getNodeByIdentifier("34a3e8fd-8866-42c2-bb52-673960f76020");
				//					System.out.println(node.getPath());


				//					NodeIterator iterator = session.getRootNode().getNodes();
				//					while(iterator.hasNext()){
				//						Node node = iterator.nextNode();
				//						System.out.println(node.getPath());
				////						if (node.getPath().equals("/VREFolders")){
				////							System.out.println("REMOVE " + node.getPath());
				//////							node.remove();
				//////							session.save();
				////						}
				//						
				//						
				//					}

				//					List<ItemDelegate> list = getChildren(session, "21e227fa-3710-44e2-8618-f7e82267a4fb");
				//					
				//					System.out.println(list.size());
				//					for (ItemDelegate item: list){
				//						System.out.println(item.toString());
				//					}

				//				}
			}
		}finally{
			session.logout();
			rm.stop();
		}
	}


	//	private static List<ItemDelegate> getChildren(Session session, String identifier) throws Exception {
	//
	//		Node folderNode = session.getNodeByIdentifier(identifier);
	//		NodeIterator iterator = folderNode.getNodes();
	//		List<ItemDelegate> children = new ArrayList<ItemDelegate>();
	//		while(iterator.hasNext()) {
	//			//
	//			Node node = iterator.nextNode();
	//			String path = null;
	//			try {
	//				path = node.getPath();
	//				//				String title = node.getName();
	//				String name = path.substring(path.lastIndexOf('/') + 1);
	//				if ((name.equals("Trash") || (name.equals("MySpecialFolders") ||(name.startsWith("rep:")) || (name.startsWith("hl:")) || (name.startsWith(".")))))
	//					continue;
	//			} catch (RepositoryException e1) {
	//				// TODO Auto-generated catch block
	//				e1.printStackTrace();
	//			}
	//			//			String name = path.substring(path.lastIndexOf('/') + 1);
	//
	//			ItemDelegate item = null;
	//			NodeManager wrap = new NodeManager(node, "valentina.marioli");
	//			try {
	//				item = wrap.getItemDelegate();
	////				wrap.setProperties(item);
	//				children.add(item);
	//				item.getParentPath();
	//			} catch (Exception e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//
	//
	//		}
	//		return children;
	//	}
}


package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.thoughtworks.xstream.XStream;


public class GetGCubeItems {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {



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

					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());

					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

//					String query1 = "/jcr:root/Home//element(*,hthl:documentItemContent)";
					QueryManager queryManager = null;
					try {
						queryManager = session.getWorkspace().getQueryManager();
					} catch (RepositoryException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
					try{

						javax.jcr.query.Query q = queryManager.createQuery("/jcr:root/Home"+
								"//element(*,nthl:report)",
								javax.jcr.query.Query.XPATH);

						QueryResult result = q.execute();

						NodeIterator iterator = result.getNodes();

						while (iterator != null && iterator.hasNext()) {

							Node node = iterator.nextNode();

							ItemDelegate item = null;
							NodeManager wrap = new NodeManager(node, "");
							try {
								item = wrap.getItemDelegate();
								
								NodeIterator nodes = node.getNodes();
								while(nodes.hasNext()){
									Node child = nodes.nextNode();
									System.out.println(child.getPath());
									PropertyIterator properties = child.getProperties();
									while(properties.hasNext()){
										Property prop = properties.nextProperty();
										System.out.println("* " + prop.getName());
									}
								}
								Map<NodeProperty, String> properties = item.getProperties();
								
								//				wrap.setProperties(item);
								System.out.println(item.toString());

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}finally{}
	}
}

//					System.out.println("getPrimaryNodeType: " + root.getPrimaryNodeType().getName());
//				
//					PropertyIterator iterator = root.getProperties();
//					System.out.println("______________________");
//					while(iterator.hasNext()){
//						  Property pro = iterator.nextProperty();
//						 System.out.println(pro.getName());
//					}
//					System.out.println("______________________");
//					
////					Node root =	session.getNodeByIdentifier("0f714ff1-1ead-4959-9026-7e9716f2a9c0");
////					System.out.println(root.getPath());
//					ItemDelegate item = new Wrap(root).getItemDelegate();
//					
////					System.out.println(item.getId());
//					System.out.println(item.toString());
//				System.out.println("USERS? " + item.getItemProperties().get("USERS").toString());


//					Map<NodeProperty, String> properties = item.getItemProperties();
//					System.out.println("----> " + properties.size());
//					Set<NodeProperty> set = properties.keySet();
//					for(NodeProperty prop: set){
//						System.out.println(prop.name() + " - " + properties.get(prop).toString());
//					}


//					NodeIterator children1 = root.getNodes();
//					while(children1.hasNext()){
//						Node child = children1.nextNode();
//						System.out.println(child.getPath());
//						try{
//							Wrap  wrapNode = new Wrap(child);
//							ItemDelegate itemNode = wrapNode.getItemDelegate();
//							System.out.println(itemNode.getName());
////							System.out.println("id: " + itemNode.getId());
////							System.out.println("isShared " + itemNode.isShared());
////							System.out.println("getPrimaryType " + itemNode.getPrimaryType());
////							System.out.println("IS_VRE_FOLDER? " + new XStream().fromXML(itemNode.getItemProperties().get("IS_VRE_FOLDER")));
////							System.out.println("***");
////							Map<NodeProperty, String> properties = itemNode.getItemProperties();
////							System.out.println("----> " + properties.size());
////							Set<NodeProperty> set = properties.keySet();
////							for(NodeProperty prop: set){
////								System.out.println(prop.name() + " - " + properties.get(prop).toString());
////							}
////							System.out.println("users: " + new XStream().fromXML(itemNode.getItemProperties().get("USERS").toString()));
//							//								System.out.println("members: " + itemNode.getMembers());
//						}catch (Exception e) {
//							//									e.printStackTrace();
//						}
//					}
//				}
//			}
//		}catch (Exception e) {
//			// TODO: handle exception
//		}

//		Long size = (Long) new XStream().fromXML("<long>161095</long>");
//		System.out.println("--------> " + size);
//	}

//}

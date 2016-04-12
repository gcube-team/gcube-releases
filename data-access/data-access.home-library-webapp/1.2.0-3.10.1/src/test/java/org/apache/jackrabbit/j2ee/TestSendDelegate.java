package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.WrapManager;
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

import com.thoughtworks.xstream.XStream;


public class TestSendDelegate {
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

										String absPath = "/Home/valentina.marioli/Workspace/Trash/1.jpg";
//										Node root = session.getNode(absPath);
//
//										System.out.println(root.getPath());
//////					//					root.remove();
//////					//					session.save();
//////
//										PropertyIterator iterator = root.getProperties();
//										while(iterator.hasNext()){
//											  Property pro = iterator.nextProperty();
//											 System.out.println(pro.getName());
//										}

//					Node node = root.addNode("testHttp-Node", "nthl:externalImage");
//					node.setProperty("hl:lastAction", WorkspaceItemAction.CREATED.toString());
//					Node content = node.addNode("jcr:content", "nthl:file");
//					content.setProperty("jcr:data", "");
//					node.getSession().save();
//
//					PropertyIterator iterator = node.getProperties();
//					while(iterator.hasNext()){
//						  Property pro = iterator.nextProperty();
//						 System.out.println(pro.getName());
//					}
					
					
//					System.out.println(node.getPath());
										
										
//										e(id=null, name=testHttp.jpg, description=test description, lastModifiedBy=valentina.marioli, parentId=cf6caaeb-09ba-48e3-9945-f22bbde9abe0, lastModificationTime=null, creationTime=null, itemProperties=null, path=/Home/valentina.marioli/Workspace/testHttp.jpg, owner=valentina.marioli, primaryType=nthl:externalImage, lastAction=CREATED, shared=false, locked=false, accounting=null, metadata=null, content={FOLDER_ITEM_TYPE=EXTERNAL_IMAGE, CONTENT_TYPE=nthl:image, PORTAL_LOGIN=valentina.marioli, IMAGE_HEIGHT=<int>640</int>, MIME_TYPE=image/jpeg, SIZE=<long>161095</long>, REMOTE_STORAGE_PATH=/Home/valentina.marioli/Workspace/testHttp.jpg, IMAGE_WIDTH=<int>640</int>, THUMBNAIL_WIDTH=<int>400</int>, THUMBNAIL_HEIGHT=<int>400</int>})

									
					Node node = session.getNode(absPath);
//					node.remove();
//					session.save();
					
					WrapManager manager = new WrapManager(session, "");
					ItemDelegate item =	manager.getItemDelegateByNode(node);
					
//					Wrap wrap = new Wrap(node);
//					ItemDelegate item = wrap.getItemDelegate();
//					System.out.println(item.toString());
//					item.setDescription("test description");
//					item.getContent().put(NodeProperty.REMOTE_STORAGE_PATH, "/Home/valentina.marioli/Workspace/test/1.jpg");
					
					ItemDelegate new_item = manager.save(item);
//					ItemDelegateWrapper wrapper = new ItemDelegateWrapper(item, "");
//					ItemDelegate new_item = wrapper.save(session);
					System.out.println("NEW ITEM: " + new_item);					
					
					
//					ItemDelegate delegate = new ItemDelegate();
//					delegate.setId("6fc45c1e-f65a-4813-9d31-978046a51f20");
//					delegate.setName("interni01.jpg");
//					delegate.setTitle("interni01.jpg");
//					delegate.setDescription("test description interni");
//					delegate.setLastModifiedBy("valentina.marioli");
//					delegate.setParentId("cf6caaeb-09ba-48e3-9945-f22bbde9abe0");
////					delegate.setLastModificationTime(null);
////					delegate.setCreationTime(null);
////					delegate.setItemProperties(null);
//					delegate.setPath("/Home/valentina.marioli/Workspace/interni.jpg");
//					delegate.setOwner("valentina.marioli");
//					delegate.setPrimaryType("nthl:externalImage");
//					delegate.setLastAction(WorkspaceItemAction.RENAMED);
//					delegate.setShared(false);
//					delegate.setLocked(false);
////					delegate.setAccounting(null);
////					delegate.setMetadata(null);
//					delegate.setContent(new HashMap<NodeProperty, String>());
//					delegate.getContent().put(NodeProperty.CONTENT_TYPE, "nthl:image");
//					long size = 161095;
//					delegate.getContent().put(NodeProperty.SIZE, new XStream().toXML(size));
//					delegate.getContent().put(NodeProperty.MIME_TYPE, "image/jpeg");
//					delegate.getContent().put(NodeProperty.PORTAL_LOGIN, "valentina.marioli");
//					delegate.getContent().put(NodeProperty.FOLDER_ITEM_TYPE, "EXTERNAL_FILE");
//					delegate.getContent().put(NodeProperty.REMOTE_STORAGE_PATH, "/Home/valentina.marioli/Workspace/interni.jpg");
//					delegate.getContent().put(NodeProperty.IMAGE_HEIGHT, new XStream().toXML((int) 640));
//					delegate.getContent().put(NodeProperty.IMAGE_WIDTH, new XStream().toXML((int) 640));
//					delegate.getContent().put(NodeProperty.THUMBNAIL_WIDTH, new XStream().toXML((int) 400)); 
//					delegate.getContent().put(NodeProperty.THUMBNAIL_HEIGHT, new XStream().toXML((int) 400)); 
//
//					System.out.println("item delegate: " + delegate);
//					ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, "");
//					ItemDelegate new_item = wrapper.save(session);
//					System.out.println("NEW ITEM: " + new_item);
//					
//					Wrap wrapNode = new Wrap(session.getNode(absPath));
//					System.out.println("----> " + wrapNode.getItemDelegate().toString());
//					System.out.println(new_item.toString());

			
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}


	}

}

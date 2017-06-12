package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import javax.jcr.GuestCredentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class RemoveItems {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {


//		ScopeProvider.instance.set("/gcube/preprod/preVRE");
		String rootScope = "/gcube/devNext";
//		//				String rootScope ="/d4science.research-infrastructures.eu";
//
//
		ScopeProvider.instance.set(rootScope);

		//		SimpleQuery query = queryFor(ServiceEndpoint.class);
		//
		//		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");
		//
		//		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		//
		//		List<ServiceEndpoint> resources = client.submit(query);
		//
		//
		//		try {
		//			ServiceEndpoint resource = resources.get(0);
		//
		//			for (AccessPoint ap:resource.profile().accessPoints()) {
		//
		//				if (ap.name().equals("JCR")) {

		//					String url = ap.address();
		String url = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp/";
//	String url = "http://node11.d.d4science.research-infrastructures.eu:80/home-library-webapp";
		//
		//					String user = ap.username();						
		//					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
		//					String user = "admin";						
		//					String pass = "admin";
//							String url = "http://ws-repo-test.d4science.org/home-library-webapp";

		String user ="workspacerep.imarine";
		String pass ="gcube2010*onan";
//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp/";
		URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
		Session session = repository.login( 
				new SimpleCredentials(user, pass.toCharArray()));
		System.out.println(session.getRootNode().getPath());

		Node trahs = session.getNode("/Home/d4science.research-infrastructures.eu-D4Research-AnalyticsLab-Manager/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-D4Research-AnalyticsLab/d4science.research-infrastructures.eu-D4Research-AnalyticsLab");
		System.out.println(trahs.getPath());
		trahs.remove();
		session.save();
//	session.move(trahs.getPath(), "/Home/TO_REMOVE/");
		
//		Node trahs = session.getRootNode().getNode("Home/valentina.marioli/Workspace/Trash");
//		System.out.println(trahs.getPath());
//		NodeIterator iterator = trahs.getNodes();
//		while (iterator.hasNext()){
//			Node node = iterator.nextNode();
//
//			try{
////				System.out.println(node.getPath());
////				node.remove();
////				session.save();
////				trahs.refresh(true);
//				node.remove();
//				session.save();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		

		//					Session session = repository.login(new GuestCredentials());	
		//Node myNode = session.getRootNode();
		//					NodeIterator iterator = myNode.getNodes();
		//					while(iterator.hasNext()){
		//						Node node = iterator.nextNode();
		//						if (node.getName().equals("Groups")){
		//							System.out.println(node.getPrimaryNodeType().getName());
		//							System.out.println("FOUND!");
		//							
		//							NodeIterator nodes = node.getNodes();
		//							while(nodes.hasNext()){
		//								Node group = nodes.nextNode();
		//								System.out.println("REMOVE " + group.getName() + " - "+group.getPrimaryNodeType().getName());
		//								group.setPrimaryType("nt:folder");
		//								node.getSession().save();
		//								group.remove();
		//								node.getSession().save();
		//								
		//							}
		//							
		//						}
		//					}
		//					Node root = session.getNode("/Groups");
		//					System.out.println(root.getPrimaryNodeType().getName());
		//					root.setPrimaryType("nt:folder");
		//					System.out.println(root.getPath());


		//					 ItemDelegate(id=2e10f8ac-777e-4274-994c-ab2ac908f6e5, name=2e10f8ac-777e-4274-994c-ab2ac908f6e5, title=ba0036b1-a125-49d2-80f3-6844a842bd14, description=desc, lastModifiedBy=valentina.marioli, parentId=0d1d865c-9366-47c6-94cc-cd6c6982bdbc, parentPath=null, lastModificationTime=java.util.GregorianCalendar[time=1473864638152,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="GMT+02:00",offset=7200000,dstSavings=0,useDaylight=false,transitions=0,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=2016,MONTH=8,WEEK_OF_YEAR=38,WEEK_OF_MONTH=3,DAY_OF_MONTH=14,DAY_OF_YEAR=258,DAY_OF_WEEK=4,DAY_OF_WEEK_IN_MONTH=2,AM_PM=1,HOUR=4,HOUR_OF_DAY=16,MINUTE=50,SECOND=38,MILLISECOND=152,ZONE_OFFSET=7200000,DST_OFFSET=0], creationTime=java.util.GregorianCalendar[time=1473864638147,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="GMT+02:00",offset=7200000,dstSavings=0,useDaylight=false,transitions=0,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=2016,MONTH=8,WEEK_OF_YEAR=38,WEEK_OF_MONTH=3,DAY_OF_MONTH=14,DAY_OF_YEAR=258,DAY_OF_WEEK=4,DAY_OF_WEEK_IN_MONTH=2,AM_PM=1,HOUR=4,HOUR_OF_DAY=16,MINUTE=50,SECOND=38,MILLISECOND=147,ZONE_OFFSET=7200000,DST_OFFSET=0], properties={hl:IsSystemFolder=<boolean>false</boolean>, hl:users=<map>
		//					  <entry>
		//					    <string>valentina.marioli</string>
		//					    <string>cf6caaeb-09ba-48e3-9945-f22bbde9abe0/ba0036b1-a125-49d2-80f3-6844a842bd14</string>
		//					  </entry>
		//					  <entry>
		//					    <string>roberto.cirillo</string>
		//					    <string>b09be9bc-b5a9-4940-aee0-53332daf1283/ba0036b1-a125-49d2-80f3-6844a842bd14</string>
		//					  </entry>
		//					</map>, hl:members=<list>
		//					  <string>roberto.cirillo</string>
		//					</list>}, path=/Share/2e10f8ac-777e-4274-994c-ab2ac908f6e5, owner=valentina.marioli, primaryType=nthl:workspaceSharedItem, lastAction=CREATED, trashed=false, shared=false, locked=true, hidden=false, accounting=null, metadata={}, content=null)

		//					System.out.println("* " + root.getPrimaryNodeType().getName());
		//					System.out.println("");
		//					System.out.println("PROPERTIES");
		//					PropertyIterator properties = root.getProperties();
		//					while(properties.hasNext()){
		//						Property prop = properties.nextProperty();
		//						System.out.println(prop.getName());
		//					}
		//					System.out.println("");
		//					System.out.println("NODES");
		//					NodeIterator nodes = root.getNodes();
		//					while(nodes.hasNext()){
		//						Node node = nodes.nextNode();
		//						System.out.println(node.getName());
		//					}
		//					root.setProperty("hl:IsSystemFolder", false);

		//					node.remove();
		//					session.save();
		//				Node test = session.getRootNode().addNode("MyTest", "nt:folder");

		//				Node test = session.getNode("/MyTest");
		//				test.setPrimaryType("nt:folder");
		//				
		//	
		//				System.out.println(test.getPrimaryNodeType().getName());
		//				
		////				session.move(node.getPath(), test.getPath());
		//				
		//				test.remove();
		//				session.save();
		//					NodeIterator iterator = node.getNodes();
		////					boolean flag = false;
		//					while(iterator.hasNext()){
		//						Node child = iterator.nextNode();
		//						System.out.println(child.getPath());
		////						if(child.getPath().equals("/Home/roberto.cirillo/Workspace/sa(2)")){
		//						System.out.println(child.getPrimaryNodeType().getName());
		//							
		////							session.save();
		////						}
		////						//							flag = true;
		////						//						if (flag){
		////						//						if(child.getName().startsWith("sa")){
		////						//							System.out.println(child.getPath());
		////						////							child.remove();
		////						////							session.save();
		////						//						}
		//					}

		//					node.getSession().save();
		//					System.out.println(Reports.getPath());
		//										root.remove();
		////										//root.save();
		//					session.save();


		//					NodeIterator iterator = root.getNodes();
		//					while(iterator.hasNext()){
		//						Node node = iterator.nextNode();
		//						if (!node.getName().startsWith("rep:") && !node.getName().startsWith("hl:")){
		//							System.out.println(node.getPath());
		//							if (node.hasProperty("hl:portalLogin"))
		//							if (node.getProperty("hl:portalLogin").getString().equals("valentina.marioli")){
		//								System.out.println(node.getPath());
		////							try{
		////								System.out.println(node.getProperty("hl:portalLogin").getString());
		////							} catch (Exception e) {
		////												// TODO Auto-generated catch block
		////												System.out.println("owner not found");
		////											}
		//								node.remove();
		//								session.save();
		//							}
		//						}
		//						//						if (node.getPath().equals("/VREFolders")){
		//						//							System.out.println("REMOVE " + node.getPath());
		//						////							node.remove();
		//						////							session.save();
		//						//						}
		//
		//
		//					}

		//					List<ItemDelegate> list = getChildren(session, "21e227fa-3710-44e2-8618-f7e82267a4fb");
		//					
		//					System.out.println(list.size());
		//					for (ItemDelegate item: list){
		//						System.out.println(item.toString());
		//					}

	}
	//			}
	//		}finally{}
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
//}


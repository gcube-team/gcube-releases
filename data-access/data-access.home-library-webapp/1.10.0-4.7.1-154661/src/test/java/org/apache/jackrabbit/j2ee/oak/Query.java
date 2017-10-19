package org.apache.jackrabbit.j2ee.oak;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.folder.items.JCRExternalFile;
import org.gcube.common.scope.api.ScopeProvider;


public class Query {
	private static final String nameResource 				= "HomeLibraryRepository";
	public static final String PATH_SEPARATOR 				= "/";
	public static final String HOME_FOLDER 					= "Home";
	public static final String SHARED_FOLDER				= "Share";	
	public static final String USERS 						= "hl:users";


	//	private static final String URL = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp";

	//	private static final String URL = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp";
	private static final String newUrl = "http://workspace-repository-prod1.d4science.org:8080/home-library-webapp";
	private static final String USER ="workspacerep.imarine";
	private static final String PASS = "gcube2010*onan";
	static JCRWorkspace ws;
	static String login = "valentina.marioli";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		try {

			Session session = null;
			//			Session session_old = null;
			try{
				URLRemoteRepository repository = new URLRemoteRepository(newUrl + "/rmi");
				//				System.out.println(repository);
				session = repository.login( 
						new SimpleCredentials(USER, PASS.toCharArray()));

				
				String scope = "/d4science.research-infrastructures.eu";
				//		String scope = "/gcube";
				//		String scope = "/gcube/preprod/preVRE";
				ScopeProvider.instance.set(scope);
				
				HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();
				ws = (JCRWorkspace) manager.getHome(login).getWorkspace();
				//				Node node = session.getNodeByIdentifier("b9d70187-8c03-4756-8c75-ebb503894514");
				//				System.out.println(node.getPath());
				//				NodeIterator children = session.getNode("/Home/statistical.manager/Workspace/DataMiner/Computations/BIONYM_LOCAL_ID_7f5a64bd-423b-46c8-915b-b05733347912").getNodes();
				//				while (children.hasNext()){
				//					Node child = children.nextNode();
				//					
				//					System.out.println(child.getPath());
				//					PropertyIterator childs = child.getProperties();
				//					while (childs.hasNext()){
				//						System.out.println(childs.nextProperty().getName());Input
				//					}
				//			
				////					if (child.hasNode("jcr:content")){
				////						if (child.getNode("jcr:content").hasProperty("hl:remotePath")){
				////							String remotePath = child.getNode("jcr:content").getProperty("hl:remotePath").getString();
				////							System.out.println(remotePath);
				////						}
				////					}
				//				}

				//				System.out.println(children.getSize());
				query(session);

				//				URLRemoteRepository repository_old = new URLRemoteRepository(URL + "/rmi");
				//				session_old = repository_old.login( 
				//						new SimpleCredentials(USER, PASS.toCharArray()));

				//				Node node = session_old.getNodeByIdentifier("55e675ae-af4c-4a68-94d6-96559d6546f8");

				//				System.out.println(session_old.getUserID());
				//				System.out.println(session_old.getRootNode().getPath());


				//				NodeIterator nodes = session_old.getNode("/Home/statistical.manager/Workspace/DataMiner/Computations/").getNodes();
				//				System.out.println(nodes.getSize());
				//
				//				int i =0;
				//				while (nodes.hasNext()){
				//
				//					Node node = nodes.nextNode();
				//
				//					i++;
				//
				//					if (i<247367){
				//						continue;
				//					}
				//
				//					if (node.hasProperty("jcr:created")){
				//						try{
				//							Node mynode = session.getNodeByIdentifier(node.getIdentifier());
				//							System.out.println(mynode.getName()+ " FOUND.");
				//						} catch (ItemNotFoundException e) {
				//							Date date = node.getProperty("jcr:created").getDate().getTime();
				//							System.out.println(node.getIdentifier() + " - " + node.getName() + " - " + date);
				//
				////							copy(node, session);
				////							session.save();
				//
				//							Node nodeCheck = session.getNode(node.getPath());
				//							check(nodeCheck);
				//						}			
				//					}					
				//				}

			} finally {
				if (session!=null)
					session.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}




	private static void copy(Node node, Session session) throws RepositoryException {


		String parentPath = node.getParent().getPath();

		Node folder = session.getNode(parentPath);
		System.out.println("Add " + node.getName() + " - " + node.getPrimaryNodeType().getName() + " to " +folder.getPath());
		Node mynode;
		try{
			mynode = folder.addNode(node.getName(), node.getPrimaryNodeType().getName());
		} catch (ItemExistsException e) {
			mynode = folder.getNode(node.getName());
		}

		PropertyIterator properties = node.getProperties();
		while (properties.hasNext()){
			Property prop = properties.nextProperty();

			if (prop.getDefinition().isProtected())
				continue;


			if (prop.isMultiple()){
				Value[] values = prop.getValues();
				System.out.println("Add multi-valued property " + prop.getName());
				mynode.setProperty(prop.getName(), values);
			}
			else{
				Value value = prop.getValue();
				System.out.println("Add prop " + prop.getName() + " - " + value);
				mynode.setProperty(prop.getName(), value);
			}


		}



		NodeIterator nodes = node.getNodes();

		while(nodes.hasNext()){			
			Node child = nodes.nextNode();

			//			System.out.println(child.getPath());
			copy(child, session);
		}


	}



	private static void check(Node node) throws RepositoryException {

		System.out.println(node.getPath());

		NodeIterator nodes = node.getNodes();

		while(nodes.hasNext()){			
			Node child = nodes.nextNode();

			//			System.out.println(child.getPath());
			check(child);
		}


	}


	private static void query(Session session) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM [nthl:workspaceItem] WHERE ISCHILDNODE('/Home/statistical.manager/Workspace/DataMiner/Computations')");
		//		query.append("SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('/Home/statistical.manager/Workspace/DataMiner/Computations/')");
		//		query.append("SELECT * FROM [nt:base] WHERE ISCHILDNODE('/Home/')");
		//			
		//		String start = "2016-09-01T00:00:00.000+02:00";
		//		String end = "2016-09-30T00:00:00.000+02:00";

		//		Calendar cal = Calendar.getInstance();
		//		String today = ValueFactoryImpl.getInstance().createValue(cal).getString();
		//		System.out.println(today);

		//		query.append("and [jcr:lastModified] > cast('2017-08-22T00:00:00.000+02:00' as date) and [jcr:lastModified] < cast('2017-08-23T22:00:00.000+02:00' as date) ");
		//			query.append(" and @jcr:created >= xs:dateTime('" + start + "') and @jcr:created <= xs:dateTime('" + end + "')");

		//			String query00 = ISO9075.encodePath("/jcr:root/Home/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu-Manager/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu") +
		//					"//element(*)[@jcr:created >= xs:dateTime('2015-09-01T00:00:00.000+02:00')]";
		////			String query1 =ISO9075.encodePath("/jcr:root/Home/valentina.marioli/Workspace//element(*,nthl:workspaceItem)[@hl:workspaceItemType = 'DOCUMENT']");
		//					String query1 =ISO9075.encodePath("/jcr:root/Home/massimiliano.assante/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-FAO_TunaAtlas") + "//element(*,nthl:workspaceItem) order by @jcr:lastModified descending";
		System.out.println(query);
		//		System.out.println(item.getCreationTime().getTime());
		//		JCRExternalFile file = (JCRExternalFile) item;
		//		System.out.println(file.getLength());
		//					String query1 = "/jcr:root/Home/valentina.marioli/Workspace//element()[@hl:workspaceItemType = 'EXTERNAL_FILE']";
		//					String query1 = "SELECT * FROM [nthl:workspaceItem] AS node WHERE ISDESCENDANTNODE('/Home/francesco.mangiacrapa/Workspace') AND (UPPER([jcr:title]) LIKE '%GATTINO%') AND NOT(ISDESCENDANTNODE ('/Home/francesco.mangiacrapa/Workspace/Trash/'))";

		String lang = "JCR-SQL2";
		//			String lang = "xpath";
		
		List<SearchItemDelegate> list = execute(session, query.toString(), lang, login, 0);


		//			SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");



		//		for(SearchItemDelegate item: list){
		//			System.out.println(item.toString());
		//			
		//		
		//			//			Node node = session.getNodeByIdentifier(item.getId());
		//
		//			//			session_old.getRootNode().addNode(arg0)
		//			//session.getWorkspace().clone(session_old.getWorkspace().getName(), item.getPath(), item.getPath(), true);
		//			//			String itemId = item.getId();
		//			//
		//			//			try{

		//			//
		//			//				WorkspaceItem myItem = ws.getItem(itemId);
		//			//				System.out.println(myItem.getName() + " " + myItem.getLastAction().toString());
		//			//			}catch (Exception e){
		//			//				e.printStackTrace();
		//			//			}
		//		}

	}



	//	private static void iter(Node node) throws RepositoryException, IOException {
	//
	//
	//
	//		if (!node.getName().startsWith("jcr:system") && (!node.getName().startsWith("hl:")) && (!node.getName().startsWith("rep:"))){
	//			//			System.out.println(node.getPath());
	//
	//			if (node.hasProperty("jcr:data")){
	//
	//				System.out.println(node.getPath());
	//				//				System.out.println("****");
	//				//				InputStream input = node.getProperty("jcr:data").getStream();
	//				//				System.out.println(input.available());
	//
	//				//				try{
	//				//					VersionManager versionManager = node.getSession().getWorkspace().getVersionManager();
	//				//					if (!versionManager.isCheckedOut(node.getPath()))
	//				//						versionManager.checkout(node.getPath());
	//				//				}catch (Exception e){
	//				//					e.printStackTrace();
	//				//				}
	//				try{
	//					byte[] source = new byte[1];
	//					ByteArrayInputStream bis = new ByteArrayInputStream(source);
	//					node.setProperty("jcr:data", bis);
	//					node.getSession().save();
	//				}catch (Exception e){
	//					e.printStackTrace();
	//				}
	//
	//			}
	//			NodeIterator children = node.getNodes();
	//			while(children.hasNext()){
	//				Node child = children.nextNode();
	//				iter(child);
	//			}
	//		}
	//
	//	}




	private static List<SearchItemDelegate> execute(Session session, String query, String lang, String login, int limit) throws Exception {

		List<SearchItemDelegate> list = null;

		QueryManager queryManager = session.getWorkspace().getQueryManager();	
		try{
			javax.jcr.query.Query q = null;

			switch (lang) {
			case "JCR-SQL2":
				q = queryManager.createQuery(query, javax.jcr.query.Query.JCR_SQL2);
				break;
			case "xpath":
				q = queryManager.createQuery(query, javax.jcr.query.Query.XPATH);
				break;
			case "sql":
				q = queryManager.createQuery(query, javax.jcr.query.Query.SQL);
				break;
			case "JCR_JQOM":
				q = queryManager.createQuery(query, javax.jcr.query.Query.JCR_JQOM);
				break;
			default:
				System.out.println("lang unknown");
				break;
			}


			QueryResult result = q.execute();

			NodeIterator iterator = result.getNodes();
			list = new LinkedList<SearchItemDelegate>();
			//			int i = 0;
			//			if (limit<1)
			//				limit = 1000;
			int count = 0;
			int bionym = 0;
			System.out.println("Results: " + iterator.getSize());
			while (iterator.hasNext() ) {

				//								System.out.println("found " + i);
				//count++;
				Node node = iterator.nextNode();
				//				if (node.getName().startsWith("BIONYM_")){
				//					bionym++;
				String path =node.getPath();

				try{
					System.out.println("remove from storage " + path);
					ws.getStorage().removeRemoteFolder(path);
					System.out.println("done.");
				}catch (Exception e){
					e.printStackTrace();
				}	

				try{
					System.out.println(bionym++ +  ") remove from jackrabbit " + path);
					node.remove();
					if (bionym % 50 == 0){
						session.save();
						System.out.println("saved.");
					}	
				}catch (Exception e){
					e.printStackTrace();
				}					
			}
			//				System.out.println(node.getProperty("jcr:created").getDate().getTime());
			//				System.out.println(node.getPath());
			//				if (node.hasProperty("jcr:data")){
			//					long size = node.getProperty("jcr:data").getLength();
			//					if (size>0)
			//						count = (int) (count + size);
			//				}

			//								if (!node.hasNode("hl:metadata"))
			//									continue;
			//				
			//								Node metadata = node.getNode("hl:metadata");
			//								PropertyIterator properties = metadata.getProperties();
			//								//				Map<String,String> map = new HashMap<String, String>() ;
			//				
			//								while (properties.hasNext()){
			//									Property prop = properties.nextProperty();
			//									String key = prop.getName();
			//									Value value = prop.getValue();
			//				
			//									//					System.out.println("**********************************");
			//									//					System.out.println(key + ": " + value );
			//									count = count + value.getString().length() + key.length();
			//									//					map.put(key, value.toString());
			//								}


			//				String itemName = node.getName();
			////				String itemName = isValidSearchResult(node, login);
			////				if (itemName == null) {
			////					System.out.println("Search result is not valid :" + node.getPath());
			////					continue;
			////				}
			//
			//				SearchItemDelegate item = null;
			//				NodeManager wrap = new NodeManager(node, login);
			//				try {
			//					item = wrap.getSearchItem(itemName);
			//					if (!list.contains(item)){		
			//						list.add(item);
			//						i++;
			//					}
			//
			//				} catch (Exception e) {
			//					throw new RepositoryException("Error adding item: " + e.getMessage());
			//				}

			//			}

			System.out.println("Count: " + count);
			System.out.println("BIONYM: " + bionym);
			//			session.save();
		}catch (Exception e) {
			e.printStackTrace();
			throw new RepositoryException("Error executing query  " + query +" : " + e.getMessage());
		}
		return list;

	}

	public static String isValidSearchResult(Node node, String login) throws RepositoryException {

		//		System.out.println(node.getPath());
		String sharePath = PATH_SEPARATOR + SHARED_FOLDER;
		String userPath = PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + login;

		try {
			String nodePath = node.getPath();
			//			System.out.println("nodepath " + nodePath);
			//			System.out.println("userPath " + userPath);
			if (nodePath.startsWith(userPath)){
				//			System.out.println("*** userPath " + nodePath );
				return node.getProperty(NodeProperty.TITLE.toString()).getString();
			}

			if (nodePath.startsWith(sharePath)) {
				//		System.out.println("*** sharePath");
				Node sharedNode = (Node) node.getAncestor(2);

				if (node.getPath().equals(sharedNode.getPath())) {
					Node usersNode = sharedNode.getNode(NodeProperty.USERS.toString());
					String prop = (usersNode.getProperty(login)).getValue().getString();
					String[] value = prop.split(PATH_SEPARATOR);
					//					System.out.println(value[1] + " " + node.getPath());
					return value[1];
				}				
				else 
					return node.getName();

			}	
			return null;
		} catch (RepositoryException e) {
			return null;
		}
	}

	//	public static String isValidSearchResult(Node node, String login) throws RepositoryException {
	//		//		System.out.println(node.getPath() +" is valid??");
	//		String sharePath = PATH_SEPARATOR + SHARED_FOLDER;
	//		String userPath = PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + login;
	//
	//		try {
	//			String nodePath = node.getPath();
	//			if (nodePath.startsWith(userPath)){
	//				//				System.out.println("*** userPath");
	//				return node.getProperty(NodeProperty.TITLE.toString()).getString();
	//			}
	//
	//			if (nodePath.startsWith(sharePath)) {
	//				//				System.out.println("*** sharePath");
	//				Node sharedNode = (Node) node.getAncestor(2);
	//
	//				if (node.getPath().equals(sharedNode.getPath())) {
	//					Node usersNode = sharedNode.getNode(NodeProperty.USERS.toString());
	//					String prop = (usersNode.getProperty(login)).getValue().getString();
	//					String[] value = prop.split(PATH_SEPARATOR);
	//					//					System.out.println("prop " + value[1]);
	//					return value[1];
	//				}				
	//				else 
	//					return node.getName();
	//
	//			}	
	//			return null;
	//		} catch (RepositoryException e) {
	//			return null;
	//		}
	//	}


}


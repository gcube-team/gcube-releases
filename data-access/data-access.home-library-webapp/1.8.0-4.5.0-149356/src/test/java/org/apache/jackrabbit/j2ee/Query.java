package org.apache.jackrabbit.j2ee;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.scope.api.ScopeProvider;


public class Query {
	private static final String nameResource 				= "HomeLibraryRepository";
	public static final String PATH_SEPARATOR 				= "/";
	public static final String HOME_FOLDER 					= "Home";
	public static final String SHARED_FOLDER				= "Share";	
	public static final String USERS 						= "hl:users";


	private static final String URL = "http://workspace-repository-prod.d4science.org:8080/home-library-webapp";
	private static final String USER ="workspacerep.imarine";
	private static final String PASS = "gcube2010*onan";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		try {

			Session session = null;
			try{
				URLRemoteRepository repository = new URLRemoteRepository(URL + "/rmi");
				System.out.println(repository);
				session = repository.login( 
						new SimpleCredentials(USER, PASS.toCharArray()));

				iter(session.getRootNode());
			} finally {
				if (session!=null)
				session.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//		String rootScope = "/gcube/devsec";
		//		String rootScope ="/d4science.research-infrastructures.eu";

		//		ScopeProvider.instance.set(rootScope);

		//		SimpleQuery query = queryFor(ServiceEndpoint.class);
		//
		//		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");
		//
		//		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		//
		//		List<ServiceEndpoint> resources = client.submit(query);

//		Session session = null;
//		try {
//
//			URLRemoteRepository repository = new URLRemoteRepository(URL + "/rmi");
//
//			session = repository.login( 
//					new SimpleCredentials(USER, PASS.toCharArray()));
//
//			//						Node node = session.getNodeByIdentifier("000a2e6e663cd9a701f98ffe8b3d3265c3f778b1");
//			////						node.getProperty("jcr:data").getStream();
//			//						
//			//										System.out.println("****");
//			//										System.out.println(node.getPath());
//			//										InputStream input = node.getProperty("jcr:data").getStream();
//			//										System.out.println(input.available());
//			////						System.out.println(node.getPath());
//
//			iter(session.getRootNode());
//			//			System.out.println(node.);
//
//			//			session.getRootNode().addNode("test");
//			//			session.save();
//			//			NodeIterator nodes = session.getRootNode().getNodes();
//			//			while(nodes.hasNext()){
//			//				Node userNode = nodes.nextNode();
//			//				System.out.println("user " + userNode.getName());
//
//
//
//			//				NodeIterator folders = userNode.getNodes();
//			//				while(folders.hasNext()){
//			//					Node folder = folders.nextNode();
//			//				
//			//					if (folder.getName().equals("HiddenFolder") ||folder.getName().equals("Bookmarks") ){
//			//						System.out.println("* remove * " + folder.getPath());
//			////						folder.remove();
//			////						session.save();
//			//					}
//			//				}
//
//			//				if (child.getPath().equals("/Groups"))
//			//				{
//			//					child.remove();
//			//					session.save();
//			//				}
//
//			//			}
//
//			//			StringBuilder query = new StringBuilder();
//			//			query.append("SELECT * FROM [nthl:workspaceItem] AS node WHERE ISDESCENDANTNODE('/Home/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu-Manager/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu/')");
//			////			
//			//			String start = "2016-09-01T00:00:00.000+02:00";
//			//			String end = "2016-09-30T00:00:00.000+02:00";
//			//
//			//			Calendar cal = Calendar.getInstance();
//			//			String today = ValueFactoryImpl.getInstance().createValue(cal).getString();
//			//			System.out.println(today);
//			//			
//			//			query.append(" AND node.[jcr:created] >= cast('2017-02-13T12:04:25.218+01:00' as date) ");
//			////			query.append(" and @jcr:created >= xs:dateTime('" + start + "') and @jcr:created <= xs:dateTime('" + end + "')");
//			//			
//			////			String query00 = ISO9075.encodePath("/jcr:root/Home/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu-Manager/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu") +
//			////					"//element(*)[@jcr:created >= xs:dateTime('2015-09-01T00:00:00.000+02:00')]";
//			//////			String query1 =ISO9075.encodePath("/jcr:root/Home/valentina.marioli/Workspace//element(*,nthl:workspaceItem)[@hl:workspaceItemType = 'DOCUMENT']");
//			//			//					String query1 =ISO9075.encodePath("/jcr:root/Home/massimiliano.assante/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-FAO_TunaAtlas") + "//element(*,nthl:workspaceItem) order by @jcr:lastModified descending";
//			//			System.out.println(query);
//			//		System.out.println(item.getCreationTime().getTime());
//
//			//			JCRExternalFile file = (JCRExternalFile) item;
//			//			System.out.println(file.getLength());
//			//			//					String query1 = "/jcr:root/Home/valentina.marioli/Workspace//element()[@hl:workspaceItemType = 'EXTERNAL_FILE']";
//			//			//					String query1 = "SELECT * FROM [nthl:workspaceItem] AS node WHERE ISDESCENDANTNODE('/Home/francesco.mangiacrapa/Workspace') AND (UPPER([jcr:title]) LIKE '%GATTINO%') AND NOT(ISDESCENDANTNODE ('/Home/francesco.mangiacrapa/Workspace/Trash/'))";
//			//
//			//			String lang = "JCR-SQL2";
//			////			String lang = "xpath";
//			//			String login = "d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu-Manager";
//			//			List<SearchItemDelegate> list = execute(session, query.toString(), lang, login, 0);
//			//			System.out.println(list.size());
//			//			for(SearchItemDelegate item: list){
//			//				System.out.println(item.toString());
//			//			}
//
//			//				}
//			//			}
//		}finally{
//			if (session!=null)
//				session.logout();
//		}
	}



	private static void iter(Node node) throws RepositoryException, IOException {



		if (!node.getName().startsWith("jcr:system") && (!node.getName().startsWith("hl:")) && (!node.getName().startsWith("rep:"))){
			//			System.out.println(node.getPath());

			if (node.hasProperty("jcr:data")){

				System.out.println(node.getPath());
				//				System.out.println("****");
				//				InputStream input = node.getProperty("jcr:data").getStream();
				//				System.out.println(input.available());

				//				try{
				//					VersionManager versionManager = node.getSession().getWorkspace().getVersionManager();
				//					if (!versionManager.isCheckedOut(node.getPath()))
				//						versionManager.checkout(node.getPath());
				//				}catch (Exception e){
				//					e.printStackTrace();
				//				}
				try{
					byte[] source = new byte[1];
					ByteArrayInputStream bis = new ByteArrayInputStream(source);
					node.setProperty("jcr:data", bis);
					node.getSession().save();
				}catch (Exception e){
					e.printStackTrace();
				}

			}
			NodeIterator children = node.getNodes();
			while(children.hasNext()){
				Node child = children.nextNode();
				iter(child);
			}
		}

	}



	//	private static List<SearchItemDelegate> execute(Session session, String query, String lang, String login) throws Exception {
	//
	//		List<SearchItemDelegate> list = null;
	//
	//		QueryManager queryManager = session.getWorkspace().getQueryManager();	
	//		try{
	//			javax.jcr.query.Query q = null;
	//
	//			if(lang.equals("JCR-SQL2"))
	//				q = queryManager.createQuery(query, javax.jcr.query.Query.JCR_SQL2);
	//			else if (lang.equals("xpath"))
	//				q = queryManager.createQuery(query, javax.jcr.query.Query.XPATH);
	//			else if (lang.equals("sql"))
	//				q = queryManager.createQuery(query, javax.jcr.query.Query.SQL);
	//			else if (lang.equals("JCR_JQOM"))
	//				q = queryManager.createQuery(query, javax.jcr.query.Query.JCR_JQOM);
	//			else
	//				System.out.println("lang unknown");
	//
	//			QueryResult result = q.execute();
	//
	//			NodeIterator iterator = result.getNodes();
	//			//			int count = 0;
	//			list = new LinkedList<SearchItemDelegate>();
	//			while (iterator.hasNext()) {
	//				//				if (limit!=0){
	//				//					if (count<limit){
	//				Node node = iterator.nextNode();
	//
	//				String itemName = isValidSearchResult(node, login);
	//				if (itemName == null) {
	//					System.out.println("Search result is not valid :" + node.getPath());
	//					continue;
	//				}
	//
	//				SearchItemDelegate item = null;
	//				NodeManager wrap = new NodeManager(node, login);
	//				try {
	//					item = wrap.getSearchItem(itemName);
	//					if (!list.contains(item))
	//						list.add(item);
	//					//							count++;
	//				}catch (Exception e) {
	//					e.printStackTrace();
	//				}
	//			}
	//		}catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		return list;
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
			int i = 0;
			if (limit<1)
				limit = 10000000;

			while (iterator.hasNext() && i < limit) {

				//				System.out.println("found " + i);

				Node node = iterator.nextNode();

				String itemName = isValidSearchResult(node, login);
				if (itemName == null) {
					System.out.println("Search result is not valid :" + node.getPath());
					continue;
				}

				SearchItemDelegate item = null;
				NodeManager wrap = new NodeManager(node, login);
				try {
					item = wrap.getSearchItem(itemName);
					if (!list.contains(item)){		
						list.add(item);
						i++;
					}

				} catch (Exception e) {
					throw new RepositoryException("Error adding item: " + e.getMessage());
				}

			}
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


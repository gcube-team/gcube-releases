package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.apache.jackrabbit.oak.jcr.query.QueryImpl;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class Query {
	private static final String nameResource 				= "HomeLibraryRepository";
	public static final String PATH_SEPARATOR 				= "/";
	public static final String HOME_FOLDER 					= "Home";
	public static final String SHARED_FOLDER				= "Share";	
	public static final String USERS 						= "hl:users";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {



//		String rootScope = "/gcube/devsec";
						String rootScope ="/d4science.research-infrastructures.eu";

		ScopeProvider.instance.set(rootScope);

//		SimpleQuery query = queryFor(ServiceEndpoint.class);
//
//		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");
//
//		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
//
//		List<ServiceEndpoint> resources = client.submit(query);


		try {
			//					String url = ap.address();
//			String url = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp";
			String url = "http://node76.p.d4science.research-infrastructures.eu:8080/home-library-webapp/";
			//
			//					String user = ap.username();						
			//					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
			//					String user = "admin";						
			//					String pass = "admin";
			//									String url = "http://ws-repo-test.d4science.org/home-library-webapp";

			String user ="workspacerep.imarine";
			String pass ="gcube2010*onan";
			//				String url = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp/";
			URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");

			Session session = repository.login( 
					new SimpleCredentials(user, pass.toCharArray()));

			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM [nthl:workspaceItem] AS node WHERE ISDESCENDANTNODE('/Home/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu-Manager/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu/')");
//			
			String start = "2016-09-01T00:00:00.000+02:00";
			String end = "2016-09-30T00:00:00.000+02:00";

			Calendar cal = Calendar.getInstance();
			String today = ValueFactoryImpl.getInstance().createValue(cal).getString();
			System.out.println(today);
			
			query.append(" AND node.[jcr:created] >= cast('2017-02-13T12:04:25.218+01:00' as date) ");
//			query.append(" and @jcr:created >= xs:dateTime('" + start + "') and @jcr:created <= xs:dateTime('" + end + "')");
			
//			String query00 = ISO9075.encodePath("/jcr:root/Home/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu-Manager/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu") +
//					"//element(*)[@jcr:created >= xs:dateTime('2015-09-01T00:00:00.000+02:00')]";
////			String query1 =ISO9075.encodePath("/jcr:root/Home/valentina.marioli/Workspace//element(*,nthl:workspaceItem)[@hl:workspaceItemType = 'DOCUMENT']");
			//					String query1 =ISO9075.encodePath("/jcr:root/Home/massimiliano.assante/Workspace/MySpecialFolders/d4science.research-infrastructures.eu-gCubeApps-FAO_TunaAtlas") + "//element(*,nthl:workspaceItem) order by @jcr:lastModified descending";
			System.out.println(query);

			//					String query1 = "/jcr:root/Home/valentina.marioli/Workspace//element()[@hl:workspaceItemType = 'EXTERNAL_FILE']";
			//					String query1 = "SELECT * FROM [nthl:workspaceItem] AS node WHERE ISDESCENDANTNODE('/Home/francesco.mangiacrapa/Workspace') AND (UPPER([jcr:title]) LIKE '%GATTINO%') AND NOT(ISDESCENDANTNODE ('/Home/francesco.mangiacrapa/Workspace/Trash/'))";

			String lang = "JCR-SQL2";
//			String lang = "xpath";
			String login = "d4science.research-infrastructures.eu-gCubeApps-SoBigData.eu-Manager";
			List<SearchItemDelegate> list = execute(session, query.toString(), lang, login, 0);
			System.out.println(list.size());
//			for(SearchItemDelegate item: list){
//				System.out.println(item.toString());
//			}

			//				}
		//			}
		}finally{}
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


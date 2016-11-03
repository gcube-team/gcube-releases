package org.apache.jackrabbit.j2ee.workspacemanager.util;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.security.MessageDigest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.accessmanager.AccessControlUtil;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.apache.jackrabbit.j2ee.workspacemanager.util.CustomPrivilege;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.acl.AccessRights;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import lombok.patcher.Symbols;


public class Test {
	private static final String nameResource 				= "HomeLibraryRepository";
	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";
	private static final String NT_NAMESPACE 				= "nt:";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//		SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");

		//				String rootScope = "/gcube";
		//		//		String rootScope = ("/d4science.research-infrastructures.eu");
//				ScopeProvider.instance.set("/gcube/devNext/NextNext");
		ScopeProvider.instance.set("/gcube/preprod/preVRE");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		//		SimpleQuery query = queryFor(ServiceEndpoint.class);
		//
		//		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");
		//
		//		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		//
		//		List<ServiceEndpoint> resources = client.submit(query);
		//		Session session =null;
		//
		//		try {
		//			ServiceEndpoint resource = resources.get(0);
		//
		//			for (AccessPoint ap:resource.profile().accessPoints()) {
		//
		//				if (ap.name().equals("JCR")) {
		//
		//					String url = ap.address();
		String url = "http://ws-repo-test.d4science.org/home-library-webapp";
//				String url = "https://workspace-repository-dev.research-infrastructures.eu/home-library-webapp";

		//					String user = ap.username();						
		//					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());
		String user = "workspacerep.imarine";						
		String pass = "gcube2010*onan";

		//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
		URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
		//		String user = "test.user";
		//		String pass = getSecurePassword(user);
		Session session = repository.login( 
				new SimpleCredentials(user, pass.toCharArray()));

		List<SearchItemDelegate> list = null;

		QueryManager queryManager = session.getWorkspace().getQueryManager();	
		String query = "SELECT * FROM [nthl:workspaceItem] AS node WHERE ISDESCENDANTNODE('/Home/valentina.marioli/Workspace/') AND (UPPER([jcr:title]) LIKE '%TEST%') AND NOT(ISDESCENDANTNODE ('/Home/valentina.marioli/Workspace/Trash/'))";
			javax.jcr.query.Query q = queryManager.createQuery(query, javax.jcr.query.Query.JCR_SQL2);
		

			QueryResult result = q.execute();

			NodeIterator iterator = result.getNodes();
			list = new LinkedList<SearchItemDelegate>();
			while (iterator.hasNext()) {

				Node node = iterator.nextNode();

				String login = "valentina.marioli";
				String itemName = Utils.isValidSearchResult(node, login);
				if (itemName == null) {
//					System.out.println("Search result is not valid :" + node.getPath());
					continue;
				}

				SearchItemDelegate item = null;
				NodeManager wrap = new NodeManager(node, login);
				try {
					item = wrap.getSearchItem(itemName);
					if (!list.contains(item))
						list.add(item);

				} catch (Exception e) {
					throw new RepositoryException("Error adding item: " + e.getMessage());
				}

			}
			
	}
	
}

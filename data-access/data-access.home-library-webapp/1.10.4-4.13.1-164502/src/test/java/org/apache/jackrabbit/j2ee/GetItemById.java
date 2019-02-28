package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import org.apache.jackrabbit.j2ee.accessmanager.AccessControlUtil;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.apache.jackrabbit.util.Text;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class GetItemById {
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



		//		String rootScope = "/gcube";
		String rootScope = ("/d4science.research-infrastructures.eu");
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

					//															String login = "guest";
					//															String mypass = getSecurePassword(login);
					//															
					//															session = repository.login( 
					//																	new SimpleCredentials(login, mypass.toCharArray()));

					String id = "37fbf824-acf9-4ce8-af65-232cc27941a2";

					//					Node node = session.getNode("/Home/guest");
					Node node = session.getNodeByIdentifier(id);
					System.out.println(node.getPath());
					node.remove();
					session.save();
				}
			}


					//					Map<String, String> list = new HashMap<String, String>();
					////					try {
					//						Node usersNode = node.getNode(NodeProperty.USERS.toString());
					//						for (PropertyIterator iterator = usersNode.getProperties(); iterator.hasNext();) {
					//							Property property  = iterator.nextProperty();
					//							String key = property.getName();
					//							System.out.println(key);
					//							String value = property.getString();
					//							if (!(key.startsWith(JCR_NAMESPACE)) &&
					//									!(key.startsWith(HL_NAMESPACE)) &&
					//									!(key.startsWith(NT_NAMESPACE))){
					//
					//								String[] values = value.split("/");
					//								if (values.length < 2)
					//									throw new RepositoryException("Path node corrupt");
					//
					//								String parentId = values[0];
					//								System.out.println("parent id -> " + parentId);
					//								String nodeName = values[1];
					//								System.out.println("node name -> " + nodeName);
					//								Node parentNode = node.getSession().getNodeByIdentifier(parentId);
					//								System.out.println("parent node -> " + parentNode.getPath());
					//
					//								Node userNode = node.getSession().getNode(parentNode.getPath() + 
					//										"/" + Text.escapeIllegalJcrChars((nodeName)));
					//								
					//								System.out.println("userNode ->" + userNode.getPath() + " UUID " + userNode.getIdentifier());
					//								
					//								list.put(key, userNode.getPath());
					//								
					//							}




					//					System.out.println(node.getPath());
					//
					//					AccessControlManager accessControlManager = session.getAccessControlManager();
					//
					//
					//
					//					boolean canRead = accessControlManager.hasPrivileges(node.getPath(), new Privilege[] {
					//							accessControlManager.privilegeFromName(CustomPrivilege.JCR_READ)
					//					});
					//					System.out.println(canRead);


					//					AccessControlManager aMgr = session.getAccessControlManager();
					//
					////					PrincipalManager pMgr = session.getPrincipalManager();
					//					AccessControlPolicy[] acps = aMgr.getEffectivePolicies(node.getPath());
					//					for (AccessControlPolicy acp : acps) {
					//						AccessControlList acl = (AccessControlList)acp;
					//					
					//						for (AccessControlEntry ace :
					//							acl.getAccessControlEntries()) {
					//
					//							System.out.println("ACE: {}"+ ace.getPrincipal().getName());
					////							Principal everyone = pMgr.getEveryone();
					////							if(ace.getPrincipal().getName().equals("everyone")){
					////								acl.removeAccessControlEntry( ace );	
					////							}
					//						
					//							for (Privilege privileges :
					//								ace.getPrivileges()) {
					//								System.out.println(privileges.getName());
					//
					//							}
					//						}
					//					}
					//					session.save();


					//					Map<Principal, AccessRights> map = getDeclaredAccessRights(session, node.getPath());
					//					Set<Principal> keys = map.keySet();
					//					for (Principal principal: keys){
					//						AccessRights accessPrivileges = map.get(principal);
					//						Set<Privilege> deniedPrivileges = accessPrivileges.getDenied();
					//						for(Privilege priv: deniedPrivileges){
					//							System.out.println("deny - " + principal.getName() + " - " + priv.getName() + " permission");					
					//						}
					//
					//						Set<Privilege> grantedPrivileges = accessPrivileges.getGranted();
					//						for(Privilege priv: grantedPrivileges){
					//							System.out.println("allow - " + principal.getName() + " - " + priv.getName() + " permission");					
					//						}
					//					}
					//					System.out.println(getDeniedMap(node.getPath(), session).toString());
					//					System.out.println(getGrantedMap(node.getPath(), session).toString());
					// create a privilege set with jcr:all
					////					Privilege[] privileges = new Privilege[] { aMgr.privilegeFromName(Privilege.JCR_ALL) };
					//					AccessControlList acl;
					//					String path = node.getPath();
					//					try {
					//					    // get first applicable policy (for nodes w/o a policy)
					//					    acl = aMgr.getEffectivePolicies(path)
					//					} catch (NoSuchElementException e) {
					//					    // else node already has a policy, get that one
					//					
					//					    acl = (AccessControlList) aMgr.getPolicies(path)[0];
					//					}
					//					// remove all existing entries
					//					System.out.println(acl.getAccessControlEntries().length);
					//					for (AccessControlEntry e : acl.getAccessControlEntries()) {
					//						System.out.println(e.getPrincipal() + " : "+ e.getPrivileges().toString());
					//						if (e.getPrincipal().equals(EveryonePrincipal.getInstance()))
					//					    acl.removeAccessControlEntry(e);
					//					    
					//					}
					//					// add a new one for the special "everyone" principal
					////					acl.addAccessControlEntry(EveryonePrincipal.getInstance(), privileges);
					//					
					//
					//					// the policy must be re-set
					//					aMgr.setPolicy(path, acl);
					//
					//					// and the session must be saved for the changes to be applied
					//					session.save();



					//					ItemDelegate item = null;
					//					NodeManager wrap = new NodeManager(node, "");
					//				
					//						item = wrap.getItemDelegate();
					//						System.out.println(item.getPath());
					//						System.out.println(item.isTrashed());
					//					

					//					Node root = session.getNode("/Home/valentina.marioli/Workspace/");
					//					NodeIterator iterator = root.getNodes();
					//					while(iterator.hasNext()){
					//						
					//						Node child = iterator.nextNode();
					//						if (child.getName().startsWith("pdf")){
					//							System.out.println("---> " + child.getName());
					//							child.remove();
					//							session.save();
					//						}
					//					}


					//					
					//					Node node = session.getNode("/Home/d4science.research-infrastructures.eu-gCubeApps-EGI_Engage-Manager/");
					//					System.out.println(node.getPath());

					//					node.remove();
					//					session.save();

					//					Node myNode = node.getParent().addNode(Text.escapeIllegalJcrChars("èèè*testattaaaa"), node.getParent().getPrimaryNodeType().getName());
					//					myNode.setProperty("hl:lastAction", node.getProperty("hl:lastAction").getString());
					//					session.save();
					//					System.out.println(Text.unescapeIllegalJcrChars(myNode.getName()));
					//					System.out.println(myNode.getPath());
					//
					//					ItemDelegate item = null;
					//					NodeManager wrap = new NodeManager(node, "");
					//
					//					item = wrap.getItemDelegate();
					//					System.out.println(item.getPath());


					//						}
//				}
				//			}
								}finally{
									if (session!=null)
										session.logout();
								}
							}

				//create a password
				public static String getSecurePassword(String user) throws Exception {
					String digest = null;
					try {
						MessageDigest md = MessageDigest.getInstance("MD5");
						byte[] hash = md.digest(user.getBytes("UTF-8"));

						//converting byte array to Hexadecimal String
						StringBuilder sb = new StringBuilder(2*hash.length);
						for(byte b : hash){
							sb.append(String.format("%02x", b&0xff));
						}
						digest = sb.toString();

					} catch (Exception e) {
						e.printStackTrace();
					} 
					return digest;
				}

				//	public static Map<String, List<String>> getDeniedMap(String absPath, Session session) throws RepositoryException {
				//
				//		Map<String, List<String>> map = null;
				//		//		AccessControlManager aMgr = session.getAccessControlManager();
				//		try{
				//			map = new HashMap<String, List<String>>();
				//			Map<Principal, AccessRights> accessMap = new LinkedHashMap<Principal, AccessRights>();
				//			AccessControlEntry[] entries = getDeclaredAccessControlEntries(session, absPath);
				//
				//			if (entries != null) {
				//				for (AccessControlEntry ace : entries) {
				//					List<String> privilegesList = null;
				//					Principal principal = ace.getPrincipal();
				//
				//					AccessRights accessPrivileges = accessMap.get(principal);
				//					if (accessPrivileges == null) {
				//						accessPrivileges = new AccessRights();
				//						accessMap.put(principal, accessPrivileges);
				//					}
				//
				//					accessPrivileges.getDenied().addAll(Arrays.asList(ace.getPrivileges()));
				//					Set<Privilege> deniedPrivileges = accessPrivileges.getDenied();
				//					for(Privilege priv: deniedPrivileges){
				//						System.out.println("deny - " + principal.getName() + " - " + priv.getName() + " permission");
				//						if (privilegesList==null)
				//							privilegesList = new ArrayList<String>();
				//						privilegesList.add(priv.getName());
				//					}
				//					map.put(principal.getName(), privilegesList);	
				//				}
				//			}
				//		} catch (Exception e) {
				//			System.out.println("Impossible to get Denied map");
				//
				//		}
				//
				//		return map;
				//	}
				//
				//
				//	public static Map<String, List<String>> getGrantedMap(String absPath, Session session) throws RepositoryException {
				//
				//		Map<String, List<String>> map = null;
				//		//		AccessControlManager aMgr = session.getAccessControlManager();
				//		try{
				//			map = new HashMap<String, List<String>>();
				//			Map<Principal, AccessRights> accessMap = new LinkedHashMap<Principal, AccessRights>();
				//			AccessControlEntry[] entries = getDeclaredAccessControlEntries(session, absPath);
				//
				//			if (entries != null) {
				//				for (AccessControlEntry ace : entries) {
				//
				//					List<String> privilegesList = null;
				//					Principal principal = ace.getPrincipal();
				//					//				System.out.println("Principal " + principal.getName());
				//					AccessRights accessPrivileges = accessMap.get(principal);
				//					if (accessPrivileges == null) {
				//						accessPrivileges = new AccessRights();
				//						accessMap.put(principal, accessPrivileges);
				//					}
				//
				//					accessPrivileges.getGranted().addAll(Arrays.asList(ace.getPrivileges()));
				//
				//					Set<Privilege> deniedPrivileges = accessPrivileges.getGranted();
				//					for(Privilege priv: deniedPrivileges){
				//						System.out.println("allow - " + principal.getName() + " - " + priv.getName() + " permission");
				//						if (privilegesList==null)
				//							privilegesList = new ArrayList<String>();
				//						privilegesList.add(priv.getName());
				//					}
				//					map.put(principal.getName(), privilegesList);	
				//				}
				//			}
				//		} catch (Exception e) {
				//			System.out.println("Impossible to get Denied map");
				//
				//		}
				//
				//		return map;
				//	}


				private static AccessControlEntry[] getDeclaredAccessControlEntries(Session session, String absPath) throws RepositoryException {
					AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
					AccessControlPolicy[] policies = accessControlManager.getPolicies(absPath);
					for (AccessControlPolicy accessControlPolicy : policies) {
						if (accessControlPolicy instanceof AccessControlList) {
							AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
							//							System.out.println(accessControlEntries.toString());
							return accessControlEntries;
						}
					}
					return new AccessControlEntry[0];
				}

				//	public static Map<Principal, AccessRights> getDeclaredAccessRights(Session session, String absPath) throws RepositoryException {
				//		Map<Principal, AccessRights> accessMap = new LinkedHashMap<Principal, AccessRights>();
				//		AccessControlEntry[] entries = getDeclaredAccessControlEntries(session, absPath);
				//		if (entries != null) {
				//			for (AccessControlEntry ace : entries) {
				//				Principal principal = ace.getPrincipal();
				//				System.out.println(principal.getName());
				//
				//				AccessRights accessPrivileges = accessMap.get(principal);
				//
				//				if (accessPrivileges == null) {
				//					accessPrivileges = new AccessRights();
				//					accessMap.put(principal, accessPrivileges);
				//				}
				//				boolean allow = true;
				//				if (allow) {
				//					accessPrivileges.getGranted().addAll(Arrays.asList(ace.getPrivileges()));
				//				} else {
				//					accessPrivileges.getDenied().addAll(Arrays.asList(ace.getPrivileges()));
				//				}
				//			}
				//		}
				//
				//		return accessMap;
				//	}

			}

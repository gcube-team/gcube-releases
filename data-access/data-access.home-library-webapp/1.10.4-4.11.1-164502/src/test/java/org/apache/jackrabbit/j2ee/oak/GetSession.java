//package org.apache.jackrabbit.j2ee.oak;
//import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
//import java.net.UnknownHostException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import javax.jcr.AccessDeniedException;
//import javax.jcr.LoginException;
//import javax.jcr.Node;
//import javax.jcr.NodeIterator;
//import javax.jcr.RepositoryException;
//import javax.jcr.Session;
//import javax.jcr.UnsupportedRepositoryOperationException;
//
//import org.apache.jackrabbit.api.JackrabbitSession;
//import org.apache.jackrabbit.api.security.user.Authorizable;
//import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
//import org.apache.jackrabbit.api.security.user.User;
//import org.apache.jackrabbit.api.security.user.UserManager;
//import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
//import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
//import org.gcube.common.homelibary.model.items.ItemDelegate;
//import org.gcube.common.homelibary.model.items.type.NodeProperty;
//import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
//import org.gcube.common.homelibrary.home.HomeLibrary;
//import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
//import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
//import org.gcube.common.scope.api.ScopeProvider;
//
//import com.thoughtworks.xstream.XStream;
//
//
//public class GetSession {
//	private static final String nameResource 				= "HomeLibraryRepository";
//	/**
//	 * @param args
//	 * @throws MalformedURLException 
//	 */
//	public static void main(String[] args) throws MalformedURLException {
//		ScopeProvider.instance.set("/gcube/preprod");
//		try {
//			long start = System.currentTimeMillis();
//			oak();
//			System.out.println("**** END in milliseconds: "+ (System.currentTimeMillis()-start));
//		} catch (UnknownHostException | RepositoryException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//
//
//
//
//	public static void oak() throws UnknownHostException, LoginException, RepositoryException {
//
//
//		SessionManager sm = null;
//		String sessionID = null;
//		try{
//			//			String newUser = "valentina.marioli";
//			//			String newPassword = getSecurePassword(newUser);
//			//			System.out.println(newPassword);
//
//			sm = SessionManager.getInstance(null);
//			Session session = sm.newSession();
//			sessionID = session.toString();
//
//
//
//
//			UserManager userManager = ((JackrabbitSession)
//					session).getUserManager();
//
//			Authorizable authorizable = userManager.getAuthorizable("admin");
//
//			boolean admin = ((User) authorizable).isAdmin();
//
//			if (admin)
//			{
//				NodeIterator nodes = session.getRootNode().getNode("Home").getNodes();
//				while(nodes.hasNext()){
//					Node node = nodes.nextNode();
//					System.out.println(node.getPath());
//					String newuser = node.getName();
//					String newPassword = getSecurePassword(newuser);
//					System.out.println(newuser + " : " + newPassword);
//					try{
//					User createUser = userManager.createUser(newuser,newPassword); 
//					System.out.println(createUser.getID());
//					} catch (AuthorizableExistsException e) {
//						System.out.println("Authorizable with ID admin already exists " + newuser);
//					}
//				}
//
//				//				String newUser = "valentina.marioli";
//				//				String newPassword = getSecurePassword(newUser);
//				//				System.out.println(newPassword);
//				//				User createUser = userManager.createUser(newUser,newPassword); 
//				//				System.out.println(createUser.getID());
//				//				String newPassword = "gcube2010*onan";
//				//				((User) authorizable).changePassword(newPassword);
//				//				userManager.
//			}
//
//			session.save();
//			session.logout(); 
//			System.out.println("done");
//
//			//			System.out.println(session.getUserID());
//			//			
//			//			ItemDelegate delegate = new ItemDelegate();
//			//			delegate.setId(null);
//			//			delegate.setName("Info.txt");
//			//			delegate.setTitle("Info.txt");
//			//			delegate.setDescription(null);
//			//			delegate.setLastModifiedBy("valentina.marioli");
//			//			delegate.setParentId("81c0f974-fa05-4b51-95c8-4db793c61f04");
//			//			delegate.setParentPath(null);
//			//			delegate.setLastModificationTime(null);
//			//			delegate.setCreationTime(null);
//			//
//			//			Map<NodeProperty, String> properties = new HashMap<NodeProperty, String>();
//			//			delegate.setProperties(properties);
//			//			
//			//			delegate.setPath("/Home/valentina.marioli/Workspace/TestUpload/Info.txt");
//			//			delegate.setOwner("valentina.marioli");
//			//			delegate.setPrimaryType("nthl:externalFile");
//			//			delegate.setLastAction(WorkspaceItemAction.CREATED);
//			//			delegate.setTrashed(false);
//			//			delegate.setShared(false);
//			//			delegate.setHidden(false);
//			//			delegate.setLocked(false);
//			//			delegate.setAccounting(null);
//			//			delegate.setMetadata(null);
//			//			
//			//			
//			//			Map<NodeProperty, String> content = new HashMap<NodeProperty, String>();
//			//
//			//			long size = 479;
//			//			content.put(NodeProperty.SIZE, new XStream().toXML(size));
//			//			content.put(NodeProperty.STORAGE_ID, "582edd5616a43b2b2f8720c8");
//			//			content.put(NodeProperty.PORTAL_LOGIN, "valentina.marioli");
//			//			content.put(NodeProperty.MIME_TYPE, "text/plain");		
//			//			content.put(NodeProperty.ITEM_TYPE, "EXTERNAL_FILE");	
//			//			content.put(NodeProperty.REMOTE_STORAGE_PATH, "/Home/valentina.marioli/Workspace/TestUpload/Info.txt");
//			//			content.put(NodeProperty.CONTENT, new XStream().toXML(org.gcube.common.homelibary.model.items.type.ContentType.GENERAL));
//			//
//			//			delegate.setContent(content);
//			//			
//			//			System.out.println(delegate.toString());
//			//
//			//			ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, delegate.getOwner());
//			//			wrapper.save(session);
//
//
//			//			getChildren(session.getRootNode().getNode("Home/valentina.marioli/"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//
//			System.out.println("Close");
//			if (sessionID!=null)
//				sm.releaseSession(sessionID);
//			//			System.exit(0);
//		}
//	}
//
//
//
//	private static List<String> getUsers(Session session) throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
//		final UserManager userManager = ((JackrabbitSession)
//				session).getUserManager();
//		ArrayList<String> users = new ArrayList<String>();
//
//		Iterator<Authorizable> iter = userManager.findAuthorizables(
//				"jcr:primaryType", "rep:User");
//
//		while (iter.hasNext()) {
//			Authorizable auth = iter.next();
//			if (!auth.isGroup()){
//				users.add(auth.getID());
//			}
//		}
//		if (!userManager.isAutoSave()) {
//			session.save();
//		}
//		return users;
//
//	}
//
//
//
//
//
//	private static void getChildren(Node node) throws RepositoryException {
//
//		if (!node.getName().contains(":")){
//			System.out.println(node.getPath());
//			NodeIterator children = node.getNodes();
//			while(children.hasNext()){
//				Node child = children.nextNode();
//				getChildren(child);
//			}
//		}
//
//	}
//	//create a password
//	public static String getSecurePassword(String message) throws InternalErrorException {
//		String digest = null;
//		try {
//			MessageDigest md = MessageDigest.getInstance("MD5");
//			byte[] hash = md.digest(message.getBytes("UTF-8"));
//
//			//converting byte array to Hexadecimal String
//			StringBuilder sb = new StringBuilder(2*hash.length);
//			for(byte b : hash){
//				sb.append(String.format("%02x", b&0xff));
//			}
//			digest = sb.toString();
//
//		} catch (UnsupportedEncodingException e) {
//			throw new InternalErrorException(e);
//		} catch (NoSuchAlgorithmException e) {
//			throw new InternalErrorException(e);
//		}
//		return digest;
//	}
//
//}

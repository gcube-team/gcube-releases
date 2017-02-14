package org.apache.jackrabbit.j2ee.oak;
import org.apache.jackrabbit.oak.Oak;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import org.apache.jackrabbit.j2ee.accessmanager.privileges.CheckUtil;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.security.SecurityProviderImpl;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.SecurityProvider;
import org.apache.jackrabbit.oak.spi.security.user.UserConfiguration;
import org.apache.jackrabbit.oak.spi.security.user.UserConstants;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import com.google.common.collect.ImmutableMap;
import com.mongodb.DB;
import com.mongodb.MongoClient;


public class ChangePass {
	private static final String nameResource 				= "HomeLibraryRepository";

	private static String adminUser = "admin";
	protected static SecurityProvider securityProvider;
	private static ConfigurationParameters securityParams;
	private static final String WRITE_ALL 		= "hl:writeAll";
	private static final String READ 			= "jcr:read";
	private static final String WRITE 			= "jcr:write";	
	private static final String ADMINISTRATOR 	= "jcr:all";;
	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {

		try {
			oak();
		} catch (UnknownHostException | RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}





	public static void oak() throws UnknownHostException, LoginException, RepositoryException {
		DocumentNodeStore ns = null;
		Session session = null;

		try{


			DB db = new MongoClient("ws-repo-mongo-d.d4science.org", 27017).getDB("jackrabbit");
			//System.out.println(db.getName());
			ns = new DocumentMK.Builder().
					setMongoDB(db).getNodeStore();


			//			Repository repo = new Jcr(new org.apache.jackrabbit.oak.Oak(ns)).createRepository();
			//	
			//
			String user = "workspacerep.imarine";						
			String pass = "gcube2010*onan";
			//
			String userAdmin = "admin";
			String passAdmin = "admin";
			//			String newUser = "workspacerep.imarine";						
			////			String pass = "gcube2010*onan";
			////

			//			
//									Map<String, Object> userParams = new HashMap<String, Object>();
//									userParams.put(UserConstants.PARAM_ADMIN_ID, user);
//									userParams.put(UserConstants.REP_PASSWORD, pass);
//									ConfigurationParameters config =  ConfigurationParameters.of(ImmutableMap.of(UserConfiguration.NAME, ConfigurationParameters.of(userParams)));
//									System.out.println(config.toString());
//									SecurityProvider securityProvider = new SecurityProviderImpl(config);
									Repository repo = new Jcr(new Oak(ns)).with(getSecurityProvider()).createRepository();
			//						
			//						System.out.println("Repository handle acquired for Jackrabbit OAK :: " + repo.getDescriptorKeys().toString());
//			Repository repo = new Jcr(new org.apache.jackrabbit.oak.Oak(ns)).createRepository();
			session = repo.login(new SimpleCredentials("valentina.marioli", (getSecurePassword("valentina.marioli")).toCharArray()));
//
//						if(session instanceof JackrabbitSession)
//						{
//							UserManager um = ((JackrabbitSession) session).getUserManager();
//							User myuser = um.createUser("valentina.marioli", (getSecurePassword("valentina.marioli")));
//							System.out.println(myuser.getID());
//							session.save();
//			
//						}
			System.out.println(session.getRootNode().getPath());


			NodeIterator children = session.getRootNode().getNodes();
			while(children.hasNext()){
				Node child = children.nextNode();
				System.out.println(child.getPath());
			}
			Node node = session.getNode("/Home/valentina.marioli/Workspace/Just4Sharing");
			System.out.println(node.getPath());
			
			System.out.println(CheckUtil.canDeleteChildren(node.getPath(), session));
			
	

//			try {
//				AccessControlManager accessControlManager = session.getAccessControlManager();
//				//				String absPath = "/Home/valentina.marioli/Workspace";
//				System.out.println(accessControlManager.hasPrivileges(node.getPath(), new Privilege[] {
//						accessControlManager.privilegeFromName(CustomPrivilege.ADMINISTRATOR)
//				}));
//			} catch (RepositoryException e) {
//				e.printStackTrace();
//			}


			//						session.save();

			System.out.println(session.getUserID() + " Done!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (session!=null)
				session.logout();
			if (ns!=null)
				ns.dispose();

			System.out.println("Close");
			System.exit(0);
		}
	}


	private static SecurityProvider getSecurityProvider() {
		Map<String, Object> userParams = new HashMap<String, Object>();
		userParams.put(UserConstants.PARAM_ADMIN_ID, adminUser);
		userParams.put(UserConstants.PARAM_OMIT_ADMIN_PW, false);

		securityParams = ConfigurationParameters
				.of(ImmutableMap.of(UserConfiguration.NAME, ConfigurationParameters.of(userParams)));
		securityProvider = new SecurityProviderImpl(securityParams);
		return securityProvider;
	}


	//create a password
		public static String getSecurePassword(String message) throws Exception {
			String digest = null;
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] hash = md.digest(message.getBytes("UTF-8"));

				//converting byte array to Hexadecimal String
				StringBuilder sb = new StringBuilder(2*hash.length);
				for(byte b : hash){
					sb.append(String.format("%02x", b&0xff));
				}
				digest = sb.toString();

			} catch (Exception e) {
				throw new Exception(e);
			} 
			return digest;
		}
		
		

		
}

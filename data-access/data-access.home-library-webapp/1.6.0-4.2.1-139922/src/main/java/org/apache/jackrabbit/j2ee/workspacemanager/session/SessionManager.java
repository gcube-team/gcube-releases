package org.apache.jackrabbit.j2ee.workspacemanager.session;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;

import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.security.SecurityProviderImpl;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.SecurityProvider;
import org.apache.jackrabbit.oak.spi.security.user.UserConfiguration;
import org.apache.jackrabbit.oak.spi.security.user.UserConstants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibary.model.util.MemoryCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;


public class SessionManager {

	private static Logger logger = LoggerFactory.getLogger(SessionManager.class);
	private static MemoryCache<String, MySession> list;
	private static SessionManager sessionManager = null;  
	private static Repository rep = null;  
	
	private static final String ADMIN_USER = "admin";
	private static final String MONGO_CLIENT = "ws-repo-mongo-d.d4science.org";
	private static final String DB = "jackrabbit-preprod";
	private static final int PORT = 27017;
	private static DocumentNodeStore ns = null;
	private static DB db;
	
	//1 hour
	private static long timeToLiveInSeconds = 3600;
	private static long timerIntervalInSeconds = 7200;
	private static int maxItems = 20;


	private SessionManager(){
		list = new MemoryCache<String, MySession>(timeToLiveInSeconds, timerIntervalInSeconds, maxItems);
	}

		public static synchronized SessionManager getInstance(Repository repository) {
			rep = repository;
			if(sessionManager == null)
				sessionManager = new SessionManager();
			return sessionManager;
	
//		return getInstance();
		}

	public static synchronized SessionManager getInstance() {

//		logger.info("Getting Mongo DB....");

		try {
			if (db==null){
				db = new MongoClient(MONGO_CLIENT, PORT).getDB(DB);
//				System.out.println("Get mongo db " + db.getName());
			}
			if (ns==null){
				ns = new DocumentMK.Builder().setMongoDB(db).getNodeStore();
//				System.out.println("Cluster ID " + ns.getClusterId());
			}
			if (rep==null){
				rep = new Jcr(new org.apache.jackrabbit.oak.Oak(ns)).with(getSecurityProvider()).createRepository();
//				System.out.println("Get OAK repository ");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if(sessionManager == null)
			sessionManager = new SessionManager();
		return sessionManager;
	}


	private static SecurityProvider getSecurityProvider() {
		Map<String, Object> userParams = new HashMap<String, Object>();

		userParams.put(UserConstants.PARAM_ADMIN_ID, ADMIN_USER);
		userParams.put(UserConstants.PARAM_OMIT_ADMIN_PW, false);

		ConfigurationParameters securityParams = ConfigurationParameters
				.of(ImmutableMap.of(UserConfiguration.NAME, ConfigurationParameters.of(userParams)));
		SecurityProviderImpl securityProvider = new SecurityProviderImpl(securityParams);
		return securityProvider;
	}


	/**
	 * Get session by id
	 * @param sessionId
	 * @param rep
	 * @param user
	 * @param pass
	 * @return
	 */
	public synchronized Session getSession(String sessionId){

		Session session = null;

		if (list.size() > 0)
			session = list.get(sessionId).getSession();

		return session;                           

	} 

	/**
	 * Log in to Jackrabbit as admin and  create a new session 
	 * @param id
	 * @param rep
	 * @param user
	 * @param pass
	 * @return
	 * @throws Exception 
	 */
	public synchronized Session newSession(HttpServletRequest request) throws Exception{

		String user = request.getSession()
				.getServletContext()
				.getInitParameter("user");	
		char[] pass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();

		Session session = null;

		try {
			session = rep
					.login(new SimpleCredentials(user, pass));	
			list.put(session.toString(), new MySession(getLogin(request), session));

		} catch (Exception e) {
			logger.error("Error getting new session for user "+ user + " : " + e);
		}
		return session;                            
	}

	
	public synchronized Session newSession() throws Exception{

		String user = "admin";
		char[] pass = "gcube2010*onan".toCharArray();

//		String user = "valentina.marioli";
//		char[] pass = "39c4e6f9fcef359428e15cdbcbfc6df8".toCharArray();
		
		Session session = null;

		try {
			session = rep
					.login(new SimpleCredentials(user, pass));	
			list.put(session.toString(), new MySession(user, session));

		} catch (Exception e) {
			logger.error("Error getting new session for user "+ user + " : " + e);
		}
		return session;                            
	}

		public synchronized Session newSession(HttpServletRequest request, String login) throws Exception{
	
			logger.trace("Getting a new session for user " + getLogin(request));
			Session session = null;
			try{
				session = rep.login( 
						new SimpleCredentials(getLogin(request), getSecurePassword(getLogin(request)).toCharArray()));
			} catch (Exception e) {
				e.printStackTrace();
			} 
	
			return session;
		}


	public synchronized Session newSession(String login) throws Exception{

		logger.trace("Getting a new session for user " + login);

		Session session = null;
		try{
			session = rep.login( 
					new SimpleCredentials(login, getSecurePassword(login).toCharArray()));

			list.put(session.toString(), new MySession(login, session));
		} catch (Exception e) {
			throw new Exception("Error getting a new session");
		} 

		return session;
	}

	public String getLogin(HttpServletRequest request) throws Exception {

		String login = request.getParameter(ServletParameter.PORTAL_LOGIN);
		if (login==null){
			AuthorizationEntry entry = null;
			try {
				entry = authorizationService().get(SecurityTokenProvider.instance.get());
				login = entry.getClientInfo().getId();
			} catch (Exception e1) {
				throw new Exception("User not authorize to access Home Library Webapp");
			}		
		}
		return login;

	}

	//create a password
	public String getSecurePassword(String user) throws Exception {
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


	/**
	 * Logout session
	 * @param session
	 * @param id
	 */
	public synchronized void releaseSession(String id) {
		Session session = null;

		try {
			session = getSession(id);
			if (session!=null){		
				session.logout();
			}
			//			if (ns!=null)
			//				ns.dispose();

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
		}
		list.remove(id);
	}

	/**
	 * Return true is a session with the given uuid already exists
	 * @param uuid
	 * @return
	 */
	public boolean sessionExists(String uuid) {

		if (list.containsKey(uuid))
			return true;
		return false;

	}


	public Set<String> getSessionIds() {
		return list.keySet();
	}

	public String getLogin(String sessionId) {
		return list.get(sessionId).getUser();
	}

}

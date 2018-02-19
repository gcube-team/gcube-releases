package org.apache.jackrabbit.j2ee.workspacemanager.session;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.security.MessageDigest;
import java.util.Set;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibary.model.util.MemoryCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages JCR sessions.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SessionManager {

	private static Logger logger = LoggerFactory.getLogger(SessionManager.class);
	private static MemoryCache<String, MySession> cache;
	private static SessionManager sessionManager = null;  
	private static Repository rep = null;  

	//1 hour
	private static long timeToLiveInSeconds = 3600;
	private static long timerIntervalInSeconds = 7200;
	private static int maxItems = 10000;

	private static final Object REPOSITORY_LOCK  = new Object();


	private SessionManager(){
		cache = new MemoryCache<String, MySession>(timeToLiveInSeconds, timerIntervalInSeconds, maxItems);
	}

	public static SessionManager getInstance(Repository repository) {

		if(sessionManager == null){
			synchronized(REPOSITORY_LOCK){
				rep = repository;
				if(sessionManager == null)
					sessionManager = new SessionManager();
			}
		}

		return sessionManager;
	}

	/**
	 * Get session by id
	 * @param sessionId
	 * @param rep
	 * @param user
	 * @param pass
	 * @return
	 */
	public Session getSession(String sessionId){

		Session session = null;

		MySession cacheValue = cache.get(sessionId);
		if(cacheValue != null)
			return cacheValue.getSession();

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
	public Session newSession(HttpServletRequest request) throws Exception{

		//		return newSession();
		String user = request.getSession()
				.getServletContext()
				.getInitParameter("user");	
		char[] pass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();

		Session session = null;

		try {
			synchronized(REPOSITORY_LOCK){
				session = rep
						.login(new SimpleCredentials(user, pass));	
			}
			cache.put(session.toString(), new MySession(getLogin(request), session));

		} catch (Exception e) {
			logger.error("Error getting new session for user "+ user + " : ", e);
		}
		return session;                            
	}


	public Session newSession(HttpServletRequest request, String login) throws Exception{

		logger.trace("Getting a new session for user " + getLogin(request));
		Session session = null;
		try{
			synchronized(REPOSITORY_LOCK){
				session = rep.login( 
						new SimpleCredentials(getLogin(request), getSecurePassword(getLogin(request)).toCharArray()));
			}
		} catch (Exception e) {
			logger.error("Error getting new session for user "+ login, e);
		} 

		return session;
	}


	public Session newSession(String login) throws Exception{

		logger.trace("Getting a new session for user " + login);

		Session session = null;
		try{
			synchronized(REPOSITORY_LOCK){
				session = rep.login( 
						new SimpleCredentials(login, getSecurePassword(login).toCharArray()));
			}
			cache.put(session.toString(), new MySession(login, session));
		} catch (Exception e) {
			throw new Exception("Error getting a new session for user " + login, e);
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
			} catch (Exception e) {
				throw new Exception("User not authorize to access Home Library Webapp", e);
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
	public void releaseSession(String id) {
		//		Session session = null;
		//		try {
		//			session = getSession(id);
		//			if (session!=null){		
		//				session.logout();
		//			}
		//			
		//		} catch (Exception e) {
		//			logger.error("Error releasing session with ID  "+ id,  e);
		//		}
		cache.remove(id);
	}

	/**
	 * Return true is a session with the given uuid already exists
	 * @param uuid
	 * @return
	 */
	public boolean sessionExists(String uuid) {
		return cache.containsKey(uuid);
	}

	public Set<String> getSessionIds() {
		return cache.keySet();
	}

	public String getLogin(String sessionId) {
		return cache.get(sessionId).getUser();
	}

}

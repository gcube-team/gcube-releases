package org.apache.jackrabbit.j2ee.workspacemanager;

import java.util.HashMap;
import java.util.Set;

import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SessionManager {

	private static Logger logger = LoggerFactory.getLogger(SessionManager.class);
	private static HashMap<String, MySession> list;
	private static SessionManager sessionManager = null;  
	private static Repository rep = null;  
	
	private char[] pass = null; 
	private String user = null;  


	private SessionManager(){
		list = new HashMap<String,MySession>();

		Thread thread = new Thread(new Cleaner(list));
		thread.start();
	}

	public static synchronized SessionManager getInstance(Repository repository) {
		rep = repository;
		
		if(sessionManager == null)
			sessionManager = new SessionManager();
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
	public synchronized SessionImpl getSession(String sessionId){

		SessionImpl mySession = null;
		
		if (list.size() > 0)
			mySession = list.get(sessionId).getSession();

		return mySession;                           

	} 

	/**
	 * Create a new session
	 * @param id
	 * @param rep
	 * @param user
	 * @param pass
	 * @return
	 */
	public synchronized SessionImpl newSession(String login, String myUser, char[] myPass){
		this.user = myUser;
		this.pass = myPass;

		SessionImpl session = null;
		try {
			session = (SessionImpl) rep
					.login(new SimpleCredentials(user, pass));	
			list.put(session.toString(), new MySession(login, session));

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
		}
		return session;                            
	}


	/**
	 * Logout session
	 * @param session
	 * @param id
	 */
	public synchronized void releaseSession(String id) {
		try {
			SessionImpl session = getSession(id);
			if (session != null)
				session.logout();
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

}


class Cleaner implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(Cleaner.class);
	private HashMap<String, MySession> list;

	public Cleaner(HashMap<String, MySession> list) {
		this.list = list;
	}

	public void run() {
		while (true) {	
			
			try {
				Thread.sleep(300000);
				logger.info("Running Session Cleaner..");
				logger.info("Active sessions: " + list.size());
//				Set<String> keys = list.keySet();
//				for (String key: keys){
//					MySession mySession = list.get(key);
//					SessionImpl session = mySession.getSession();
//					String user = mySession.getUser();
//					logger.info("Check session " + key + " opened by user " + user + ". Is alive? " + session.isLive() );
//					
//					if (!session.isLive()){
//						logger.info(key + " is not usable anymore, will be removed from the cache");
//						list.remove(key);
//					}
//				}
			
			} catch (InterruptedException e) {
				logger.error("Session Cleaner interrupted.");
			}  
		}
	}
}
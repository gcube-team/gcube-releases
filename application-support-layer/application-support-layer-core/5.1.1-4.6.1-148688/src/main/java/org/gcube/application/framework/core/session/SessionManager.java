package org.gcube.application.framework.core.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

	protected static Thread thread = new CleanSessionThread();
	protected static SessionManager sessionManager = new SessionManager();
	protected HashMap<String, ASLSession> sessions;
	
	protected SessionManager() {
		sessions = new HashMap<String, ASLSession>();
		thread.setDaemon(true);
		thread.start();
	}

	public static SessionManager getInstance() {
		return sessionManager;
	}
	
	public ASLSession getASLSession(String externalSessionID, String username)
	{
		ASLSession session = sessions.get(externalSessionID + "_" + username);
		if(session == null || !session.isValid()  ||  !session.getUsername().equals(username))
		{
			session = new ASLSession(externalSessionID, username);
			sessions.put(externalSessionID + "_" + username, session);
		}
		 if (session.getScope()!=null) //covers first helper's invocation
	         ScopeProvider.instance.set(session.getScopeName());

		if (session.getSecurityToken()!=null){
			logger.debug("Setting SecurityTokenProvider to: "+session.getSecurityToken()+" in thread "+Thread.currentThread().getId());	  
			SecurityTokenProvider.instance.set(session.getSecurityToken());
		}
		return session;
	}

	@Override
	protected void finalize() throws Throwable {
		thread.interrupt();
		logger.debug(new Date(System.currentTimeMillis()) + " clean thread was interrupted");
		thread.join();
		logger.debug(new Date(System.currentTimeMillis()) + " clean thread was joint");
		super.finalize();
	}
	
	
	protected static class CleanSessionThread extends Thread
	{
		public void run()
		{
			while(true)
			{
				try {
					Thread.sleep(300000);
				} catch (InterruptedException e) {
					logger.error("Exception:", e);
					logger.debug(new Date(System.currentTimeMillis()) + " clean thread was interrupted (in clean thread)");
					break;
				}
				//TODO: cleanup invalid sessions: add locks...
				Set<String> keys = sessionManager.sessions.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext())
				{
					String extSessionID = iter.next();
					if(!sessionManager.sessions.get(extSessionID).isValid())
					{
						sessionManager.sessions.remove(extSessionID);
					}
				}
			}
			logger.debug(new Date(System.currentTimeMillis()) + " clean thread was terminated");
		}

	}
}

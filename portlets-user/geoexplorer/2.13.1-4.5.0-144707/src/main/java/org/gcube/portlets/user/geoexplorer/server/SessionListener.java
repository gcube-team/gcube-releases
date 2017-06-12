/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 30, 2013
 *
 */
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.util.DatabaseManagerFile;
import org.gcube.portlets.user.geoexplorer.server.util.HttpSessionUtil;
 
public class SessionListener implements HttpSessionListener {
	
    private int sessionCount = 0;
 
    private final int DELAY = 1000;
    private final int MAXDELAY = 1000*10;
    
    public static Logger logger = Logger.getLogger(SessionListener.class);
    

    
    public void sessionCreated(HttpSessionEvent event) {
        synchronized (this) {
            sessionCount++;
        }
 
//        System.out.println("############Session Created: " + event.getSession().getId());
        logger.trace("Session Created: " + event.getSession().getId());
//        System.out.println("++++++++++++++Total Sessions: " + sessionCount);
    }
 
    public void sessionDestroyed(HttpSessionEvent event) {
    	synchronized (this) {
            sessionCount--;
        }

        logger.trace("Session Destroyed: " + event.getSession().getId());
//        System.out.println("Session Destroyed: " + event.getSession().getId());
        
        String scope = HttpSessionUtil.getScopeInstance(event.getSession());
        
        logger.trace("Session Destroyed return scope: "+scope);
        
        if(scope == null || scope.isEmpty()){
        	logger.trace("Scope is overrided as empty, tentative deleting caching ");
        	scope="";
        	HttpSessionUtil.resetAllHashsForScope(event.getSession(), scope);
        	logger.trace("scope is null, skipping deleting persistente DBs");

        }else{
        	
        	//TODO FIX ISSUE ON java.lang.IllegalStateException: Attempting to execute an operation on a closed EntityManagerFactory
        	/*EntityManagerFactory factoryEM = HttpSessionUtil.closeEntityManagerFactory(event.getSession(), scope);
        	HttpSessionUtil.closeEntityManagerFactoryForGeoParameters(event.getSession(), scope);
        	
        	try {
        		logger.trace("Sleeping 5 sec.");
				Thread.sleep(5000);
        	} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
        	deleteDBs(factoryEM, event.getSession(), scope);
        	
    		HttpSessionUtil.resetAllHashsForScope(event.getSession(), scope);
    		DatabaseManagerFile.deleteFile(event.getSession(.getId(), scope);*/
    		
     		HttpSessionUtil.resetAllHashsForScope(event.getSession(), scope);
    		DatabaseManagerFile.deleteFile(event.getSession().getId(), scope);
        }

    }
    
	protected void deleteDBs(final EntityManagerFactory factory, final HttpSession httpSession, final String scope) {

		new Thread() {
			@Override
			public void run() {
				logger.trace("Run Thread " + Thread.currentThread().getId()+ ", to check factory closing");
				
				int currentDelay = 0;
				boolean exit = false;
				
				while (!exit && currentDelay < MAXDELAY) {

					if (factory.isOpen()) {
						logger.trace("Thread " + Thread.currentThread().getId()+ ", the factory is open");
						try {
							logger.trace("Thread "
									+ Thread.currentThread().getId()
									+ ", sleeping");
							
							Thread.sleep(DELAY);
							currentDelay += DELAY;
							logger.trace("currentDelay is.. " + currentDelay);
						} catch (InterruptedException e) {
							logger.warn("Error on thread sleeping.. ", e);
						}
					} else {
						logger.trace("Thread " + Thread.currentThread().getId()+ ", the factory is closed, exit!");
						DatabaseManagerFile.deleteFile(httpSession.getId(), scope);
						exit = true;
					}
				}
			}
		}.start();
	}
    
    

	public int getSessionCount() {
		return sessionCount;
	}
    
}
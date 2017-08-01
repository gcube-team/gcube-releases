/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server;

/**
 * 
 */
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * The listener interface for receiving gcubeReleasesSession events.
 * The class that is interested in processing a gcubeReleasesSession
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addGcubeReleasesSessionListener<code> method. When
 * the gcubeReleasesSession event occurs, that object's appropriate
 * method is invoked.
 *
 * @see GcubeReleasesSessionEvent
 */
public class GcubeReleasesSessionListener implements HttpSessionListener {
	
    private int sessionCount = 0;
 
    private Logger logger = LoggerFactory.getLogger(GcubeReleasesSessionListener.class);

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent event) {
        synchronized (this) {
            sessionCount++;
        }
        logger.trace("Session Created: " + event.getSession().getId());
    }
 
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDestroyed(final HttpSessionEvent event) {
    	synchronized (this) {
            sessionCount--;
        }

        logger.trace("Session Destroyed: " + event.getSession().getId());
//        System.out.println("Session Destroyed: " + event.getSession().getId());
        
//        ASLSession asl = WsUtil.getAslSession(event.getSession());
        
//        logger.trace("ASLSession is valid: " + (asl!=null));
//        WsUtil.closeDbMangerForRelease(asl);
    }

	/**
	 * Gets the session count.
	 *
	 * @return the session count
	 */
	public int getSessionCount() {
		return sessionCount;
	}
    
}
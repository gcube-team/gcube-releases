package org.gcube.portal.social.networking.liferay.ws;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.gcube.portal.social.networking.caches.UsersInInfrastructureCache;

/**
 * Loaded at start up. This class performs some init - to be done once - operations.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class ServletContextClass implements ServletContextListener
{
	
	private static String notifierToken;
	
	public void contextInitialized(ServletContextEvent arg0) {
		
		// get the token and save it
		notifierToken = arg0.getServletContext().getInitParameter("NOTIFIER_TOKEN");
		
		// start the thread to retrieve infrastructure users (which is, build up the singleton)
		UsersInInfrastructureCache.getSingleton();
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0){ 
		// on shutdown
	}

	/**
	 * Returns the token of the Liferay's User.
	 * @return
	 */
	public static String getNotifierToken() {
		return notifierToken;
	}
}
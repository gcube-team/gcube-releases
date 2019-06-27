/**
 *
 */
package org.gcube.datatransfer.resolver.init;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The listener interface for receiving uriResolverServletContext events.
 * The class that is interested in processing a uriResolverServletContext
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addUriResolverServletContextListener<code> method. When
 * the uriResolverServletContext event occurs, that object's appropriate
 * method is invoked.
 *
 * @see UriResolverServletContextEvent
 */
@WebListener
public class UriResolverServletContextListener implements ServletContextListener {

	private static Logger log = LoggerFactory.getLogger(UriResolverServletContextListener.class);


	private static ServletContext servletContext;

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {

    	servletContext = event.getServletContext();
    	log.info("Context Initialized at context path: "+servletContext.getContextPath());
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Perform action during application's shutdown
    }


	/**
	 * Gets the servlet context.
	 *
	 * @return the servlet context
	 */
	public static ServletContext getServletContext() {

		return servletContext;
	}



}
package org.gcube.smartgears.extensions;

import java.util.Set;

import javax.servlet.Servlet;

import org.gcube.smartgears.configuration.application.Exclude;
import org.gcube.smartgears.context.application.ApplicationContext;

/**
 * A servlet that allows remote management of the application.
 * 
 * @author Fabio Simeoni
 *
 */
public interface ApplicationExtension extends Servlet {

	/**
	 * Initialises the extensions with the context of the application.
	 * @param context the application context
	 * @throws Exception if the extension cannot be initialised
	 */
	void init(ApplicationContext context) throws Exception;
	
	/**
	 * Returns the name of this extension.
	 * @return the name
	 */
	String name();
	
	
	/**
	 * Returns the mapping of this extension. 
	 * @return the mapping
	 */
	String mapping();
	
	/**
	 * Returns the set of request paths that should be excluded from request management. 
	 * @return the set of request paths that should be excluded from request management
	 */
	Set<Exclude> excludes();
}

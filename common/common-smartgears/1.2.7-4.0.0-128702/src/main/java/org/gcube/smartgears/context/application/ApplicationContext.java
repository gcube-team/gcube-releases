package org.gcube.smartgears.context.application;

import javax.servlet.ServletContext;

import org.gcube.common.events.Hub;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.context.Properties;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.lifecycle.application.ApplicationLifecycle;
import org.gcube.smartgears.persistence.Persistence;

/**
 * The management context of an application.
 * 
 * @author Fabio Simeoni
 * 
 */
public interface ApplicationContext {

	/**
	 * Returns the name of the application.
	 * 
	 * @return the name
	 */
	String name();

	/**
	 * Returns the configuration of the application.
	 * 
	 * @return the configuration
	 */
	ApplicationConfiguration configuration();
	
	
	<T> T profile(Class<T> type);

	/**
	 * Returns the lifecycle of the application.
	 * 
	 * @return the lifecycle
	 */
	ApplicationLifecycle lifecycle();

	/**
	 * Returns the event hub of the application
	 * 
	 * @return the hub
	 */
	Hub events();

	/**
	 * Returns the persistence manager of the application.
	 * 
	 * @return the manager
	 */
	Persistence persistence();

	/**
	 * Returns the servlet context of the application.
	 * 
	 * @return the context
	 */
	ServletContext application();

	/**
	 * Returns the management context of the container.
	 * 
	 * @return the context
	 */
	ContainerContext container();

	/**
	 * Returns the properties of the application
	 * 
	 * @return the properties
	 */
	Properties properties();
	
	
	/**
	 * Returns the authorization token for the application
	 * 
	 * @return the properties
	 */
	String authorizationToken();

}

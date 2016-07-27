package org.gcube.smartgears.context.container;

import org.gcube.common.events.Hub;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.Properties;
import org.gcube.smartgears.lifecycle.container.ContainerLifecycle;
import org.gcube.smartgears.persistence.Persistence;

/**
 * The management context of the container.
 * 
 * @author Fabio Simeoni
 *
 */
public interface ContainerContext {


	/**
	 * Returns the configuration of the container.
	 * @return the configuration
	 */
	ContainerConfiguration configuration();
	
	/**
	 * Returns the resource profile of a given type of the container.
	 * @return the profile
	 */
	<T> T profile(Class<T> type);
	
	/**
	 * Returns the lifecycle of the container
	 * @return the lifecycle
	 */
	ContainerLifecycle lifecycle();
	
	/**
	 * Returns the event hub of the container
	 * @return the hub
	 */
	Hub events();
	
	/**
	 * Returns the persistence manager of the container.
	 * @return the manager
	 */
	Persistence persistence();
	
	/**
	 * Returns the properties of the container.
	 * @return the properties
	 */
	Properties properties();
}

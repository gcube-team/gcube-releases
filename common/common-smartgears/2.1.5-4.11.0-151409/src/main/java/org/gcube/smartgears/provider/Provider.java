package org.gcube.smartgears.provider;

import javax.servlet.ServletContext;

import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.smartgears.configuration.application.ApplicationExtensions;
import org.gcube.smartgears.configuration.application.ApplicationHandlers;
import org.gcube.smartgears.configuration.container.ContainerHandlers;
import org.gcube.smartgears.configuration.library.SmartGearsConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;

/**
 * Provides dependencies for container and application management.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Provider {

	//container-level dependencies

	/**
	 * Returns the runtime properties.
	 * @return the properties.
	 */
	SmartGearsConfiguration smartgearsConfiguration();
	
	/**
	 * Assembles and returns the context of the container.
	 * @return the container's context
	 */
	ContainerContext containerContext();
	
	/**
	 * Returns the handlers associated with the container.
	 * @return the handlers
	 */
	ContainerHandlers containerHandlers();
	
	
	/**
	 * Returns an implementation of the IS publisher for the container
	 * @param application the context of the container
	 * @return the publisher implementation
	 */
	ScopedPublisher publisherFor(ContainerContext application);
	
	//application-level dependencies
	
	/**
	 * Assembles and returns the context of a given application.
	 * @param container the context of the container
	 * @param application the servlet context of the application
	 * @return
	 */
	
	ApplicationContext contextFor(ContainerContext container,ServletContext application);
	
	/**
	 * Returns the handlers associated with a given application.
	 * @param application the context of the application
	 * @return the handlers
	 */
	ApplicationHandlers handlersFor(ApplicationContext application);
	
	/**
	 * Returns the API extensions associated with a given application.
	 * @param application the context of the application
	 * @return the extensions
	 */
	ApplicationExtensions extensionsFor(ApplicationContext application);
	
	/**
	 * Returns an implementation of the IS publisher for a given application
	 * @param application the context of the application
	 * @return the publisher implementation
	 */
	ScopedPublisher publisherFor(ApplicationContext application);
	
	/**
	 * Returns an implementation of the IS publisher for a given application
	 * @param application the context of the application
	 * @return the publisher implementation
	 */
	AuthorizationProxy authorizationProxy();

}

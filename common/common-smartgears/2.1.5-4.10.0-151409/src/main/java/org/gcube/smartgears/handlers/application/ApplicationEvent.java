package org.gcube.smartgears.handlers.application;

import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.Event;

/**
 * An {@link Event} that occurs in the lifetime of the application.
 
 * @author Fabio Simeoni
 * 
 * @param <T> the type of handler that handles these events
 * 
 */
public abstract class ApplicationEvent<T extends ApplicationHandler<T>> extends Event<ApplicationContext> {

	/**
	 * Creates an instance with a given application context.
	 * @param context the context
	 */
	ApplicationEvent(ApplicationContext context) {
		super(context);
	}
	
}

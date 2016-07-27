package org.gcube.smartgears;

import org.gcube.smartgears.context.application.ApplicationContext;

/**
 * Embedded in an application, makes available its context as a gCube resource. 
 * 
 * @author Fabio Simeoni
 *
 */
public class ContextProvider {

	private static ApplicationContext context;
	
	/**
	 * Returns the application context.
	 * @return the context.
	 */
	public static ApplicationContext get() {
		return context;
	}
	
	/**
	 * Sets the application context.
	 * @param context the context;
	 * 
	 * @throws IllegalStateException if the context has not been set because the resource is not managed as a gCube resource
	 */
	public static void set(ApplicationContext context) {
		
		if (context==null)
			throw new IllegalStateException("no context set for this application: are you sure the application is managed as a gCube resource?");
		
		ContextProvider.context=context;
	}
}

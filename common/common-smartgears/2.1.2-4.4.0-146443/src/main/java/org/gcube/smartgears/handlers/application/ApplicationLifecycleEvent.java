package org.gcube.smartgears.handlers.application;

import org.gcube.smartgears.context.application.ApplicationContext;

/**
 * An {@link ApplicationEvent} that occurs when the application starts or stops.
 
 * @author Fabio Simeoni
 * 
 */
public abstract class ApplicationLifecycleEvent extends ApplicationEvent<ApplicationLifecycleHandler> {

	/**
	 * An {@link ApplicationEvent} that occurs when the application starts.
	 */
	public static class Start extends ApplicationLifecycleEvent {
	
		/**
		 * Creates an instance for a given {@link ApplicationContext}.
		 * @param context the context
		 */
		public Start(ApplicationContext context) {
			super(context);
		}
	}
	
	/**
	 * An {@link ApplicationEvent} that occurs when the application stops.
	 */
	public static class Stop extends ApplicationLifecycleEvent {
		
		/**
		 * Creates an instance for a given {@link ApplicationContext}.
		 * @param context the context
		 */
		public Stop(ApplicationContext context) {
			super(context);
		}
	}
	
	/**
	 * Creates an instance for a given {@link ApplicationContext}.
	 * @param context the context
	 */
	ApplicationLifecycleEvent(ApplicationContext context) {
		super(context);
	}
	
}
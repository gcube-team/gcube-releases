package org.gcube.smartgears.handlers.container;

import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.handlers.Event;

/**
 * An {@link Event} that occurs in the lifetime of the container.
 
 * @author Fabio Simeoni
 * 
 */
public abstract class ContainerLifecycleEvent extends Event<ContainerContext> {

	/**
	 * An {@link ContainerLifecycleEvent} that occurs when the container starts.
	 */
	public static class Start extends ContainerLifecycleEvent {
	
		/**
		 * Creates an instance for a given {@link ContainerContext}.
		 * @param context the context
		 */
		public Start(ContainerContext context) {
			super(context);
		}
	}
	
	/**
	 * An {@link ContainerLifecycleEvent} that occurs when the container stops.
	 */
	public static class Stop extends ContainerLifecycleEvent {
		
		/**
		 * Creates an instance for a given {@link ContainerContext}.
		 * @param context the context
		 */
		public Stop(ContainerContext context) {
			super(context);
		}
	}
	
	/**
	 * Creates an instance with the context of the container.
	 * @param context the context
	 */
	ContainerLifecycleEvent(ContainerContext context) {
		super(context);
	}
	
}

package org.gcube.smartgears.lifecycle.container;

import org.gcube.common.events.Hub;
import org.gcube.smartgears.lifecycle.DefaultLifecycle;
import org.gcube.smartgears.lifecycle.Lifecycle;

/**
 * The {@link Lifecycle} of the application container.
 * 
 * @author Fabio Simeoni
 *
 */
public class ContainerLifecycle extends DefaultLifecycle<ContainerState> {

	/**
	 * The event qualifier that correspond to the {@link ContainerState#pending} state of the container lifecycle.
	 */
	public static final String pending = "pending";
	
	/**
	 * The event qualifier that correspond to the {@link ContainerState#started} state of the container lifecycle.
	 */
	public static final String start = "start";
	
	/**
	 * The event qualifier that correspond to the {@link ContainerState#partActive} state of the container lifecycle.
	 */
	public static final String activation = "activation";
	
	/**
	 * The event qualifier that correspond to the {@link ContainerState#active} state of the container lifecycle.
	 */
	public static final String part_activation = "part_activation";
	
	/**
	 * The event qualifier that correspond to the {@link ContainerState#stopped} state of the container lifecycle.
	 */
	public static final String stop = "stop";
	
	/**
	 * The event qualifier that correspond to the {@link ContainerState#stopped} state of the container lifecycle.
	 */
	public static final String shutdown = "shutdown";
	
	/**
	 * The event qualifier that correspond to the {@link ContainerState#fa} state of the container lifecycle.
	 */
	public static final String failure = "failure";
	
	/**
	 * Creates an instance with an event {@link Hub}. 
	 * 
	 * @param hub the event hub
	 */
	public ContainerLifecycle(Hub hub){
		super(hub,"container",ContainerState.started);
	}
}

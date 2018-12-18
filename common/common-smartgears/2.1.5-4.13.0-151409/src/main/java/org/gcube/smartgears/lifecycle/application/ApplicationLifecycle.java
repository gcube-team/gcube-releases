package org.gcube.smartgears.lifecycle.application;

import org.gcube.common.events.Hub;
import org.gcube.smartgears.lifecycle.DefaultLifecycle;
import org.gcube.smartgears.lifecycle.Lifecycle;

/**
 * The {@link Lifecycle} of an application.
 * 
 * @author Fabio Simeoni
 *
 */
public class ApplicationLifecycle extends DefaultLifecycle<ApplicationState> {

	/**
	 * The event qualifier that correspond to the {@link ApplicationState#started} state of the service lifecycle.
	 */
	public static final String start = "start";
	
	/**
	 * The event qualifier that correspond to the {@link ApplicationState#active} state of the service lifecycle.
	 */
	public static final String activation = "activation";
	
	/**
	 * The event qualifier that correspond to the {@link ApplicationState#started#stopped} state of the service lifecycle.
	 */
	public static final String stop = "stop";
	
	/**
	 * The event qualifier that correspond to the {@link ApplicationState#failed} state of the service lifecycle.
	 */
	public static final String failure = "failure";
	
	public ApplicationLifecycle(Hub hub, String name){
		super(hub,name,ApplicationState.started);
	}
}

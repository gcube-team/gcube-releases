package org.gcube.smartgears.handlers.container;



import org.gcube.common.clients.stubs.jaxws.handlers.AbstractHandler;
import org.gcube.smartgears.handlers.Handler;

/**
 * A {@link Handler} of {@link ContainerLifecycleEvent}s.
 * 
 * @author Fabio Simeoni
 *
 */
public abstract class ContainerHandler extends AbstractHandler implements Handler<ContainerLifecycleEvent> {
	
	
	/**
	 * Invoked when the container starts.
	 * @param e the event
	 */
	public void onStart(ContainerLifecycleEvent.Start e) {
	
	}

	/**
	 * Invoked when the container stops.
	 * @param e the stop event
	 */
	public void onStop(ContainerLifecycleEvent.Stop e) {
	}
	
	@Override
	public void onEvent(ContainerLifecycleEvent e) {
		
		if (e instanceof ContainerLifecycleEvent.Start)
			onStart(ContainerLifecycleEvent.Start.class.cast(e));
		else
			if (e instanceof ContainerLifecycleEvent.Stop)
				onStop(ContainerLifecycleEvent.Stop.class.cast(e));
	}
}

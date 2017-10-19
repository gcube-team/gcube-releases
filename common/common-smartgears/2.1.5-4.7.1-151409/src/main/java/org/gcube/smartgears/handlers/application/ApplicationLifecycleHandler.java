package org.gcube.smartgears.handlers.application;

import java.util.logging.Handler;

import org.gcube.smartgears.handlers.AbstractHandler;

/**
 * A {@link Handler} of {@link ApplicationLifecycleEvent}s.
 * <p>
 * The handler participates in a {@link ApplicationPipeline} of other handlers registered for notification of the same events.
 * After processing the event, it may or may not propagate the event to the handlers further down in the {@link ApplicationPipeline}
 * {@link ApplicationPipeline#forward(ApplicationEvent)}.
 * @author Fabio Simeoni
 *
 * @see ApplicationLifecycleEvent
 * @see ApplicationPipeline
 */
public abstract class ApplicationLifecycleHandler extends AbstractHandler implements ApplicationHandler<ApplicationLifecycleHandler> {
	
	
	/**
	 * Invoked when the container starts a managed app.
	 * @param pipeline the pipeline in which this handler is registered
	 * @param e the event
	 */
	public void onStart(ApplicationLifecycleEvent.Start e) {
	
	}

	/**
	 * Invoked when the container stops a managed app.
	 * @param pipeline the pipeline in which this handler is registered
	 * @param e the stop event
	 */
	public void onStop(ApplicationLifecycleEvent.Stop e) {
	}
	
	@Override
	public void onEvent(ApplicationEvent<ApplicationLifecycleHandler> e) {
		
		if (e instanceof ApplicationLifecycleEvent.Start)
			onStart(ApplicationLifecycleEvent.Start.class.cast(e));
		else
			if (e instanceof ApplicationLifecycleEvent.Stop)
				onStop(ApplicationLifecycleEvent.Stop.class.cast(e));
	}

	
}

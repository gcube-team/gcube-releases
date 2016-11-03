package org.gcube.smartgears.handlers.application;

import java.util.logging.Handler;

import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.AbstractHandler;

/**
 * A {@link Handler} of {@link RequestEvent}s and {@link ResponseEvent}s.
 * <p>
 * The handler participates in a {@link ApplicationPipeline} of other handlers registered for notification of the same events.
 * After processing the event, it may or may not propagate the event to the handlers further down in the
 * {@link ApplicationPipeline} {@link ApplicationPipeline#forward(ApplicationEvent)}.
 * 
 * @author Fabio Simeoni
 * 
 * @see RequestEvent
 * @see ResponseEvent
 * @see ApplicationPipeline
 */
public abstract class RequestHandler extends AbstractHandler implements ApplicationHandler<RequestHandler> {

	/**
	 * Initialises the handler.
	 * 
	 * @param ctx the servlet context of the managed app.
	 */
	public void start(ApplicationContext ctx) {
	}

	/**
	 * Invoked when the container receives a request for a servlet of a managed app.
	 * 
	 * @param pipeline the pipeline in which this handler is registered
	 * @param e the event
	 */
	public void handleRequest(RequestEvent e) {
	}

	/**
	 * Invoked when a servlet of a managed app has produced a response to a request.
	 * 
	 * @param pipeline the pipeline in which this handler is registered
	 * @param e the event
	 */
	public void handleResponse(ResponseEvent e) {
	}

	@Override
	public void onEvent(ApplicationEvent<RequestHandler> e) {

		// mind the order here, ResponseEvent<RequestEvent so must be checked first
		if (e instanceof ResponseEvent)
			handleResponse(ResponseEvent.class.cast(e));
		else if (e instanceof RequestEvent)
			handleRequest(RequestEvent.class.cast(e));

	}

	/**
	 * Terminates the handler.
	 */
	public void stop() {
	}

}

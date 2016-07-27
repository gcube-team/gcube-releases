package org.gcube.smartgears.handlers.application;

import java.util.Collections;
import java.util.List;

import org.gcube.smartgears.handlers.Pipeline;

/**
 * A {@link Pipeline} of {@link ApplicationHandler}s.
 * 
 * @author Fabio Simeoni
 * 
 * @param <H> the type of the handlers
 * 
 * 
 */
public class ApplicationPipeline<H extends ApplicationHandler<H>> extends Pipeline<ApplicationEvent<H>,H> {

	/**
	 * Creates an instance with a given list of handlers.
	 * <p>
	 * Modification to the input list do not affect the pipeline.
	 * 
	 * @param handlers the handlers
	 */
	public ApplicationPipeline(List<H> handlers) {
		super(handlers);
	}

	/**
	 * Returns a pipeline with the same handlers as this pipeline but in reverse order.
	 * @return the reversed pipeline
	 */
	public ApplicationPipeline<H> reverse() {
		
		List<H> handlers = handlers(); // it's a copy, we're not changing this pipeline
		
		Collections.reverse(handlers);
		
		return new ApplicationPipeline<H>(handlers);
	}
}

package org.gcube.smartgears.handlers.container;

import java.util.Collections;
import java.util.List;

import org.gcube.smartgears.handlers.Pipeline;

/**
 * A {@link Pipeline} of {@link ContainerHandler}s.
 * 
 * @author Fabio Simeoni
 * 
 * 
 */
public class ContainerPipeline extends Pipeline<ContainerLifecycleEvent, ContainerHandler> {

	/**
	 * Creates an instance with a given list of handlers.
	 * <p>
	 * Modification to the input list do not affect the pipeline.
	 * 
	 * @param handlers the handlers
	 */
	public ContainerPipeline(List<ContainerHandler> handlers) {
		super(handlers);
	}

	/**
	 * Returns a pipeline with the same handlers as this pipeline but in reverse order.
	 * @return the reversed pipeline
	 */
	public ContainerPipeline reverse() {
		
		List<ContainerHandler> handlers = handlers(); // it's a copy, we're not changing this pipeline
		
		Collections.reverse(handlers);
		
		return new ContainerPipeline(handlers);
	}
}

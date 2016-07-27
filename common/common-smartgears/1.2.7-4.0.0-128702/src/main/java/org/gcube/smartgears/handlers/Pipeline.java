package org.gcube.smartgears.handlers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An ordered list {@link Handler}s that handle related events.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the type of events
 * @param <H> the type of handlers
 * 
 * 
 */
public abstract class Pipeline<E,H extends Handler<E>> {

	private static Logger log = LoggerFactory.getLogger(Pipeline.class);

	private final List<H> handlers;
	private int cursor = 0;

	/**
	 * Creates an instance with a given list of handlers.
	 * <p>
	 * Modification to the input list do not affect the pipeline.
	 * 
	 * @param handlers the handlers
	 */
	public Pipeline(List<H> handlers) {
		this.handlers = new ArrayList<H>(handlers); // defensive copy
	}

	/**
	 * Returns the handlers in this pipeline.
	 * <p>
	 * Modification to the input list do not affect the pipeline.
	 * 
	 * @return the handlers
	 */
	public List<H> handlers() {
		return new ArrayList<H>(handlers); // defensive copy
	}

	/**
	 * Forwards an event through the pipeline.
	 * 
	 * @param event the event
	 */
	public void forward(E event) {

		if (cursor >= handlers.size()) {
			reset();
			return;
		}
		
		
		H handler = handlers.get(cursor);

		cursor++;

		//log.trace("forwarding {} to {}", event, handler);

		try {
			log.trace("executing handler {} with event {} ", handler, event);
			handler.onEvent(event);
		}
		catch(RuntimeException e) {
			reset();
			throw e;
		}

		forward(event); //make sure it's the last thing we do, or it will keep acting as recursion retracts
	}
	
	private void reset() {
		cursor=0;
	}
}

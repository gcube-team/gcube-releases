package org.gcube.smartgears.handlers;



/**
 * Handles events.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the event type
 */
public interface Handler<E> {

	
	/**
	 * Processes a given event.
	 * 
	 * @param e the event
	 */
	void onEvent(E event);
	
}

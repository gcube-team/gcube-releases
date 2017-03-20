package org.gcube.smartgears.handlers;


/**
 * An event related to a given context.
 * 
 * @author Fabio Simeoni
 *
 * @param <C> the context type
 */
public abstract class Event<C> {
	
	private final C context;

	/**
	 * Creates an instance with the context of the container.
	 * @param context the context
	 */
	public Event(C context) {
		this.context=context;
	}
	
	/**
	 * Returns the context of the container.
	 * @return the context
	 */
	public C context() {
		return context;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

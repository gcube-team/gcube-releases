package org.gcube.smartgears.lifecycle;

/**
 * The lifecycle of an application managed as a gCube service.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Lifecycle<S extends State<S>> {

	/**
	 * Returns the state from which this lifecycle transitioned to its current state.
	 * @return the previous state
	 */
	S previous();
	
	/**
	 * Returns the current state of this lifecycle.
	 * @return the current state.
	 */
	S state();
	
	/**
	 * Transition this lifecycle to a given state.
	 * @param state the state
	 * 
	 * @throws IllegalStateException if the transition is illegal for this lifecycle 
	 */
	void moveTo(S state);
	
	
	/**
	 * Attempts to transition this lifecycle to a given state, but does not fail if the transition is illegal for this lifecycle.
	 * @param state the state
	 * @return <code>true<code> if this lifecycle has transitioned to the given state
	 */
	boolean tryMoveTo(S state);
	
}

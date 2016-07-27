package org.gcube.smartgears.lifecycle;

import java.util.List;

public interface State<S extends State<S>> {

	/**
	 * Returns the list of states to which services can transition to from this state.
	 * @return the states
	 */
	public abstract List<S> next();
	
	/**
	 * Returns the event corresponding to this state.
	 * @return the 
	 */
	public String event();
	
	/**
	 * Returns a serialisation of this state for exchange purposes.
	 * @return
	 */
	public String remoteForm();
}

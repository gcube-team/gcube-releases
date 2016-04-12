package org.gcube.common.core.utils.handlers.lifetime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.common.core.utils.handlers.GCUBEIHandler;
import org.gcube.common.core.utils.handlers.events.Event;
import org.gcube.common.core.utils.handlers.events.Event.LifetimeEvent;

/**
 * Partial implementation for models of distinguished states in the lifetime of a {@link GCUBEIHandler}.
 * <p>
 * A subclass that models a given state:
 * <ul>
 * <li><b>must</b> define a constructor that delegates to the constructor of {@link State} with a list of other states from which a handler may transition to the state;</li>
 * <li><b>must</b> implement {@link #toString()} to facilitate the management of the state;</li>
 * <li><b>must</b> implement {@link #getLifetimeEvent()} to produce a {@link LifetimeEvent} associated with a handler's transition to the state.<br>
 * States that are meant to be private can simply return <code>null</code>.</li>
 * <li><em>may</em> override {@link #onEnter()} and {@link #onExit()} to specify behaviour to be executed immediately after a handler 
 * moves into the state and immediately before a handler leaves the state;</li>
 * </ul> 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 */
public abstract class State {
 
	/** The states from which a handler may transition to this state. */
	private List<State> previous;
	
	/**Creates a new instance.*/
	protected State() {}
	
	/**
	 * Adds the states from which a handler may transition to this state. 
	 * @param previous the previous states.
	 */
	abstract protected void addPrevious(List<State> previous);
	/**
	 * Returns the states from which a handler may transition to this state.
	 * @return the states.
	 */
	 synchronized public final List<State> getPrevious() {
		if (previous==null) {//load previous states on first calls
			previous = new ArrayList<State>();
			this.addPrevious(previous);
		}
		return previous;
	}
	
	/** {@inheritDoc} */
	public String toString() {return this.getClass().getSimpleName().toUpperCase();}
	/**
	 * Invoked by upon transitioning <em>to</em> this state.
	 * @throws Exception if the transition could not occur.
	 */
	public void onEnter() throws Exception {}
	/**
	 * Invoked upon transitioning <em>from</em> this state.
	 * @throws Exception if the transition could not occur.
	 */
	public void onExit() throws Exception {}
	/**
	 * Returns a {@link LifetimeEvent} for the transition to this state.
	 * <p> Return <code>null</code> if the transition should not be notified.
	 * @return the event.
	 */
	 public LifetimeEvent getLifetimeEvent() {return null;}
	
	/** {@inheritDoc} */
	public boolean equals(Object o) {return this.getClass().equals(o.getClass());}
	
	/** {@inheritDoc} */
	public int hashCode() {return this.getClass().hashCode();}
	
	/**Specialises {@link State} for the (private) start state of a handler.*/
	public static class Created extends State {
		protected void addPrevious(List<State> previous) {}//start state
		public static final Created INSTANCE = new Created();
	}

	/**Specialises {@link State} for the state of a handler that is executing.
	 * <p> The state is public and is associated with a {@link org.gcube.common.core.utils.handlers.events.Event.Running Running} event.*/
	public static class Running extends State {
		public static final Running INSTANCE = new Running();
		protected Running(){}
		protected void addPrevious(List<State> previous) {previous.addAll(Arrays.asList(Created.INSTANCE,Suspended.INSTANCE,Done.INSTANCE));}
		public LifetimeEvent getLifetimeEvent() {return new Event.Running();}
	}
	
	/**Specialises {@link State} for the state of a handler that has suspended its execution.
	 *<p> The state is public and is associated with a {@link org.gcube.common.core.utils.handlers.events.Event.Suspended Suspended} event.*/	
	public static class Suspended extends State {
		public static final Suspended INSTANCE = new Suspended();
		protected Suspended(){}
		protected void addPrevious(List<State> previous) {previous.add(Running.INSTANCE);}
		public LifetimeEvent getLifetimeEvent() {return new Event.Suspended();}

	}
	
	/**Specialises {@link State} for the state of a handler that has completed execution.
	 * <p> The state is public and is associated with a {@link org.gcube.common.core.utils.handlers.events.Event.Done Done} event.*/	
	public static class Done extends State {
		public static final Done INSTANCE = new Done();
		protected Done(){}
		protected void addPrevious(List<State> previous) {previous.addAll(Arrays.asList(Running.INSTANCE,Suspended.INSTANCE));}
		public LifetimeEvent getLifetimeEvent() {return new Event.Done();}
	}

	/**Specialises {@link State} for the state of a handler that has failed prior or during its execution.
	 *<p> The state is public and is associated with a {@link org.gcube.common.core.utils.handlers.events.Event.Failed Failed} event.*/	
	public static class Failed extends State {
		public static final Failed INSTANCE = new Failed();
		protected void addPrevious(List<State> previous) {previous.addAll(Arrays.asList(Created.INSTANCE,Running.INSTANCE,Suspended.INSTANCE));}
		public LifetimeEvent getLifetimeEvent() {return new Event.Failed();}
	}
}

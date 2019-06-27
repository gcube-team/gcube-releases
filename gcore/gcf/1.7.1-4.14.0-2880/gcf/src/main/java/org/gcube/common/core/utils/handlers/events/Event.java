package org.gcube.common.core.utils.handlers.events;

import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.events.Topic.LifetimeTopic;

/**
 * Abstract specialisation of {@link GCUBEEvent} for events about {@link Topic Topics}.
 * <p>
 * Subclasses may specialise the topic and payload of events, though the must implement {@link #toString()}
 * for the benefit of generic consumers.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * @see Monitor
 * @param TOPIC the type of the event's topic.
 * @param the type of the event's payload.
 * */
abstract public class Event<TOPIC extends Topic,PAYLOAD> extends GCUBEEvent<TOPIC,PAYLOAD>{
	
	/**{@inheritDoc}*/ public String toString() {return this.getClass().getSimpleName().toUpperCase();}
	
	/** 
	 * Abstract specialisation of {@link Event} for lifetime events.
	 * <p>
	 * A {@link LifetimeEvent} carries the {@link GCUBEHandler} that produced it as its payload. */ 
	public abstract static class LifetimeEvent extends Event<LifetimeTopic,GCUBEHandler<?>>{}
	
	/**Specialises {@link LifetimeEvent} for events that signal that the execution of a handler has started or resumed.*/
	public static class Running extends LifetimeEvent {}
	/**Specialises {@link LifetimeEvent} for events that signal that the execution of a handler is suspended.*/
	public static class Suspended extends LifetimeEvent {}
	/**Specialises {@link LifetimeEvent} for events that signal that the execution of a handler has successfully completed.*/
	public static class Done extends LifetimeEvent {}
	/**Specialises {@link LifetimeEvent} for events that signal that the execution of a handler has failed.*/
	public static class Failed extends LifetimeEvent {}
}


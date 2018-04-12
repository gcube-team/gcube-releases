package org.gcube.common.core.utils.handlers.events;

import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.handlers.events.Event.Done;
import org.gcube.common.core.utils.handlers.events.Event.Failed;
import org.gcube.common.core.utils.handlers.events.Event.LifetimeEvent;
import org.gcube.common.core.utils.handlers.events.Event.Running;
import org.gcube.common.core.utils.handlers.events.Event.Suspended;

/**
 * Abstract specialisation of {@link GCUBEConsumer} for consumers of {@link Event Events}. 
 * <p>
 * {@link Monitor} implements {@link GCUBEConsumer#onEvent(GCUBEEvent...)} to dispatch {@link LifetimeEvent LifetimeEvents}
 * to event-specific callbacks and any other {@link Event} to a catch-all callback (cf. {@link #onAnyEvent(Event)}.<br>
 * Subclasses may also extend {@link #onEvent(GCUBEEvent...)} to extend the callback pattern to other types of {@link Event}.  
 * @author Fabio Simeoni (University of Strathclyde)
 * */
public abstract class Monitor implements GCUBEConsumer<Topic,Object> {
	/**{@inheritDoc}*/
	public <T1 extends Topic, P1> void onEvent(GCUBEEvent<T1, P1>... events) {
		for (GCUBEEvent<T1, P1> e : events)	{
		 if (e instanceof Running) onRunning((Running)e);
		 else if (e instanceof Done) onCompletion((Done)e);
		 else if (e instanceof Failed) onFailure((Failed) e);
		 else if (e instanceof Suspended) onSuspension((Suspended) e);
		 onAnyEvent((Event<?,?>) e);
		}
	}
	/**Callback for a {@link Running} event.
	 * @param e the event.*/ protected void onRunning(Running e) {}
	 /**Callback for a {@link Done} event.
	 * @param e the event.*/ protected void onCompletion(Done e) {}
	 /**Callback for a {@link Failed} event.
	 * @param e the event.*/ protected void onFailure(Failed e) {}
	 /**Callback for a {@link Suspended} event.
	 * @param e the event.*/ protected void onSuspension(Suspended e) {}
	 /**Callback for any handle event.
	 * @param e the event.*/ protected void onAnyEvent(Event<?,?> e){}
}

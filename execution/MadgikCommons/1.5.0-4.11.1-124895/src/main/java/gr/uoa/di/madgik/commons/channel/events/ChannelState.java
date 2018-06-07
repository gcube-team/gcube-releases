package gr.uoa.di.madgik.commons.channel.events;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ChannelState acts as a container for Events regarding a specific channel with which it is associated. Different parts
 * of the synchronization and notification mechanisms that make the channel concept work use the events this class exposes to
 * perform their work. If a thread is accessing the instance events while the dispose method is being called the
 * result may be undefined.
 * 
 * @author gpapanikos
 */
public class ChannelState {

	/** The logger. */
	private static Logger logger=Logger.getLogger(ChannelState.class.getName());
	
	/**
	 * The Events that can be registered for, exposed and send. This enumeration is used so that someone that
	 * wants to throw a specific event can request for it by name. This is a needed simplification because of the
	 * pattern mechanics of Observer-Observable used instead of the Event-Listener one.
	 */
	public enum EventName
	{
		
		/** Typed name for the {@link DisposeChannelEvent} */
		DisposeChannel,
		
		/** Typed name for the {@link BytePayloadChannelEvent} */
		BytePayload,
		
		/** Typed name for the {@link StringPayloadChannelEvent} */
		StringPayload,
		
		/** Typed name for the {@link ObjectPayloadChannelEvent} */
		ObjectPayload
	}
	
	/** The Channel events. */
	private Map<Integer, ChannelStateEvent> ChannelEvents = null;
	
	/**
	 * Creates a new instance of the ChannelState and initializes the StateItems that will be used
	 * for Observers to come and register for notifications on their status
	 */
	public ChannelState()
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Created new ChannelState and initializing Event List");
		this.ChannelEvents = new Hashtable<Integer, ChannelStateEvent>();
		this.InitEvents();
	}

	/**
	 * Retrieves all the available Events one can register for
	 *
	 * @return The available updates one can be asked to be notified on their status change
	 */
	public Collection<ChannelStateEvent> GetChannelEvents()
	{
		return this.ChannelEvents.values();
	}

	/**
	 * Retrieves a specific Event requesting it by name. If the channel is being disposed, then no events might be available. Before
	 * using a returned event it must be checked if the event is null
	 * 
	 * @param EventToRetrieve The Event to retrieve
	 * @return The Event object that can be used to be registered with or send a new Event to the ones 
	 * that have already registered with. If the event is not found, null is returned
	 */
	public ChannelStateEvent GetEvent(ChannelState.EventName EventToRetrieve)
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Retrieving Event from Channel " + EventToRetrieve.toString());
		if (!this.ChannelEvents.containsKey(EventToRetrieve.ordinal()))
		{
			return null;
		}
		return this.ChannelEvents.get(EventToRetrieve.ordinal());
	}

	/**
	 * Initializes the events.
	 */
	private void InitEvents()
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Initializing Event list of Channel");
		if (this.ChannelEvents.size() != 0)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "The Channel State Event vector has already been initialized. Reseting it would cause dangling Observers");
			throw new IllegalStateException("The Channel State Event vector has already been initialized. Reseting it would cause dangling Observers");
		}
		this.ChannelEvents.put(ChannelState.EventName.DisposeChannel.ordinal(), new DisposeChannelEvent());
		this.ChannelEvents.put(ChannelState.EventName.BytePayload.ordinal(), new BytePayloadChannelEvent());
		this.ChannelEvents.put(ChannelState.EventName.StringPayload.ordinal(), new StringPayloadChannelEvent());
		this.ChannelEvents.put(ChannelState.EventName.ObjectPayload.ordinal(), new ObjectPayloadChannelEvent());
	}

	/**
	 * Unregisters everyone that have registered for events and disposes the state of the object
	 */
	public void Dispose()
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing ChannelState and unregistering observers");
		for (Map.Entry<Integer, ChannelStateEvent> registeredEvent : this.ChannelEvents.entrySet())
		{
			registeredEvent.getValue().deleteObservers();
		}
		this.ChannelEvents.clear();
	}
}

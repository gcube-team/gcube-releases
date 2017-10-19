package gr.uoa.di.madgik.commons.channel.nozzle;

import gr.uoa.di.madgik.commons.channel.events.ChannelPayloadStateEvent;
import gr.uoa.di.madgik.commons.channel.events.ChannelState;
import gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistry;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents one of the channel's nozzles. Each channel can have two types of nozzles. One
 * that initiates the channel, publishes an {@link IChannelLocator} for it and acts a the central synchronization
 * point which is the {@link ChannelInlet}, and the other receives an {@link IChannelLocator} to a previously 
 * created channel and can receive events emitted through it and can in turn emit events itself.
 * 
 * @author gpapanikos
 */
public class ChannelInlet implements Observer
{
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(ChannelInlet.class.getName());
	
	/** The Nozzle identifier least significant bits. */
	private long NozzleIdentifierLeastSignificantBits=0;
	
	/** The Nozzle identifier most significant bits. */
	private long NozzleIdentifierMostSignificantBits=0;
	
	/** The Configuration */
	private INozzleConfig Config = null;
	
	/** The State. */
	private ChannelState State = new ChannelState();
	
	/** The Nozzle state. */
	private ChannelState NozzleState=new ChannelState();

	/**
	 * Instantiates a new channel inlet. Creates an identifier for the nozzle through which it can be 
	 * distinguished among the rest of the nozzles, registers itself to receive notifications from the
	 * underlying {@link ChannelState} and registers the created {@link ChannelState} with the
	 * {@link ChannelRegistry}  
	 * 
	 * @param Config the configuration
	 */
	public ChannelInlet(INozzleConfig Config)
	{
		if (Config == null)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Inlet configuration cannot be null");
			throw new IllegalArgumentException("Inlet configuration cannot be null");
		}
		UUID ID = UUID.randomUUID();
		this.NozzleIdentifierLeastSignificantBits = ID.getLeastSignificantBits();
		this.NozzleIdentifierMostSignificantBits = ID.getMostSignificantBits();
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Created inlet with identifier " + this.GetNozzleID());
		this.Config = Config;
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Registering for emited events");
		for(ChannelStateEvent ev : this.State.GetChannelEvents())
		{
			ev.addObserver(this);
		}
		ChannelRegistry.Register(this.State, this.Config, this.GetNozzleID());
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if (!(o.getClass().getName().equals(arg.getClass().getName())))
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Caught event has argument other than the one registered for. Disgarding");
			return;
		}
		if(arg instanceof ChannelStateEvent)
		{
			if(((ChannelStateEvent)arg).GetEmitingNozzleIdentifierLeastSignificantBits()!=this.NozzleIdentifierLeastSignificantBits ||
					((ChannelStateEvent)arg).GetEmitingNozzleIdentifierMostSignificantBits()!=this.NozzleIdentifierMostSignificantBits)
			{
				ChannelStateEvent ev=this.NozzleState.GetEvent(((ChannelStateEvent)arg).GetEventName());
				if(ev!=null)ev.NotifyChange((ChannelStateEvent)arg);
			}
		}
	}

	/**
	 * Gets the nozzle id.
	 * 
	 * @return the nozzle identifier
	 */
	public String GetNozzleID()
	{
		return Long.toString(this.NozzleIdentifierLeastSignificantBits) + "#" + Long.toString(this.NozzleIdentifierMostSignificantBits);
	}

	/**
	 * The nozzle exposes a set of events that are published through the rest of the connected
	 * nozzles. The events exposed through the nozzle are only the ones that the rest of the connected
	 * nozzles emitted and not the ones this nozzle emitted
	 * 
	 * @return The events the client can register for notifications
	 */
	public Collection<ChannelStateEvent> GetNozzleEvents()
	{
		return this.NozzleState.GetChannelEvents();
	}
	
	/**
	 * Disposed the nozzle and all associated state. The nozzle is unregistered from all events,
	 * then the {@link ChannelState} publishing events that the client is registering for is disposed
	 * through {@link ChannelState#Dispose()} and finally the underlying {@link ChannelState} that is used
	 * to synchronize all connected nozzles id disposed through a call to {@link ChannelState#Dispose()}
	 */
	public void Dispose()
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing Inlet Nozzle");
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Unregistering nozzle from state events");
		for(ChannelStateEvent ev : this.State.GetChannelEvents())
		{
			ev.deleteObserver(this);
		}
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing Nozzle config");
		this.Config.Dispose();
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing Nozzle state");
		this.NozzleState.Dispose();
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing internal state");
		this.State.Dispose();
	}
	
	/**
	 * Emits the provided event to all connected nozzles. In case a nozzle is not yet connected, there is no
	 * guarantee that after the nozzle is connected the previously emitted events will be delivered. 
	 * 
	 * @param Event the event to emit
	 */
	public void Push(ChannelPayloadStateEvent Event)
	{
		ChannelStateEvent ev = this.State.GetEvent(Event.GetEventName());
		if (ev != null)
		{
			Event.SetEmitingNozzleIdentifierLeastSignificantBits(this.NozzleIdentifierLeastSignificantBits);
			Event.SetEmitingNozzleIdentifierMostSignificantBits(this.NozzleIdentifierMostSignificantBits);
			ev.NotifyChange(Event);
		}
	}
	
	/**
	 * Retrieves the locator that can be used by other nozzles to connect to the channel
	 * 
	 * @return the channel locator
	 */
	public IChannelLocator GetLocator()
	{
		return this.Config.GetChannelProxy().GetLocator();
	}

}

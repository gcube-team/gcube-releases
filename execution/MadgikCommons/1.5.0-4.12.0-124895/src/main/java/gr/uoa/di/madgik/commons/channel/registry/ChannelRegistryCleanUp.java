package gr.uoa.di.madgik.commons.channel.registry;

import gr.uoa.di.madgik.commons.channel.events.DisposeChannelEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class that manages the cleanup of the {@link ChannelRegistry}. It is being registered by the registry to receive
 * notifications from the channels that are kept in the registry. On {@link DisposeChannelEvent} events it purges the Registry
 * from the associated entry calling {@link ChannelRegistry#CleanUp(ChannelRegistryKey)}
 * passing as parameter the {@link ChannelRegistryKey} retrieved from {@link DisposeChannelEvent#GetRegistryKey()}
 * 
 * @author gpapanikos
 */
public class ChannelRegistryCleanUp implements Observer
{

	/** The logger. */
	private Logger logger = Logger.getLogger(ChannelRegistryCleanUp.class.getName());

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
		if (arg instanceof DisposeChannelEvent)
		{
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Purging registry from disposed channel");
			ChannelRegistry.CleanUp(((DisposeChannelEvent) arg).GetRegistryKey());
		}
	}
}

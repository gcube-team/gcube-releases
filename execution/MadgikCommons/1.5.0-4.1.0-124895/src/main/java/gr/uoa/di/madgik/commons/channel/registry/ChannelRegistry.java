package gr.uoa.di.madgik.commons.channel.registry;

import gr.uoa.di.madgik.commons.channel.events.ChannelState;
import gr.uoa.di.madgik.commons.channel.events.ChannelStateEvent;
import gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ChannelRegistry is a utility class that acts as a container for channels.
 * It is the main connector point so that outlets can locate inlets
 * 
 * @author gpapanikos
 */
public class ChannelRegistry
{
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(ChannelRegistry.class.getName());
	
	/** The Constant lockMe. */
	private static final Object lockMe = new Object();
	
	/** The Dictionary. */
	private static Map<ChannelRegistryKey, ChannelRegistryEntry> Dictionary = new Hashtable<ChannelRegistryKey, ChannelRegistryEntry>();
	
	/** The Clean up. */
	private static ChannelRegistryCleanUp CleanUp = new ChannelRegistryCleanUp();

	/**
	 * Registers a channel and assigns it a referencable unique identifier.
	 * the {@link IChannelProxy#SetChannelRegistryKey(ChannelRegistryKey)}
	 * is called so that now the inlet side of the channel has all the information needed
	 * to complete its instantiation of inlet side proxy able to identify
	 * fully the served channel. The cleanup module {@link ChannelRegistryCleanUp}
	 * is registered with events produced by the registered {@link ChannelState}
	 * so that it can cleanup the registry entries when the channel will be disposed.
	 * 
	 * @param State the state that represents the main synchronization point
	 * @param Config the configuration of the created channel
	 * @param RegisteringNozzle the inlet nozzle
	 * @return the assigned channel registry key
	 */
	public static ChannelRegistryKey Register(ChannelState State,INozzleConfig Config, String RegisteringNozzle)
	{
		synchronized (ChannelRegistry.lockMe)
		{
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Registering channel state");
			String UUIDRegistryKey = UUID.randomUUID().toString();
			ChannelRegistryKey key = new ChannelRegistryKey(UUIDRegistryKey);
			ChannelRegistry.Dictionary.put(key, new ChannelRegistryEntry(key, State,Config,RegisteringNozzle));
			Config.GetChannelProxy().SetChannelRegistryKey(key);
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Registering registry cleanup to events of channel");
			for (ChannelStateEvent event : State.GetChannelEvents())
			{
				event.addObserver(ChannelRegistry.CleanUp);

			}
			return key;
		}
	}

	/**
	 * Retrieves the associated to a registry key entry
	 *
	 * @param Key The registry key of which to retrieve the entry
	 * @return The associated entry or null if the registry does not contain
	 * any record for the specific key
	 */
	public static ChannelRegistryEntry Retrieve(ChannelRegistryKey Key)
	{
		synchronized (ChannelRegistry.lockMe)
		{
			if (Key == null)
			{
				if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Retrieving entry for key (null)");
				return null;
			}
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Retrieving entry for key " + Key.toString());
			if (!ChannelRegistry.Dictionary.containsKey(Key))
			{
				return null;
			}
			return ChannelRegistry.Dictionary.get(Key);
		}
	}

	/**
	 * Cleans up the entry of the provided registry key. Unregisters the
	 * {@link ChannelRegistryCleanUp} module from events by the {@link ChannelState} to
	 * be cleaned up. Then calls {@link ChannelRegistryEntry#Dispose()} for the entry
	 * that is stored for the provided registry key
	 * 
	 * @param RegistryKey the registry key
	 */
	protected static void CleanUp(ChannelRegistryKey RegistryKey)
	{
		synchronized (ChannelRegistry.lockMe)
		{
			if (RegistryKey == null || !ChannelRegistry.Dictionary.containsKey(RegistryKey))
			{
				if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "No Registry Key provided. Disgarding");
				return;
			}
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Unregistering CleanUp from events of channel with key " + RegistryKey);
			for (ChannelStateEvent event : ChannelRegistry.Dictionary.get(RegistryKey).GetState().GetChannelEvents())
			{
				event.deleteObserver(ChannelRegistry.CleanUp);
			}
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing items of registry entry with key " + RegistryKey);
			ChannelRegistry.Dictionary.get(RegistryKey).Dispose();
			if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Cleaning up registry from entry with key " + RegistryKey);
			ChannelRegistry.Dictionary.remove(RegistryKey);
		}
	}
}

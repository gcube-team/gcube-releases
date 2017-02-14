package gr.uoa.di.madgik.commons.channel.registry;

import gr.uoa.di.madgik.commons.channel.events.ChannelState;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelInlet;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelOutlet;
import gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the one kept by the registry connecting an {@link ChannelState}
 * that is registered with a specific {@link ChannelRegistryKey} for which this entry is stored,
 * along with the {@link INozzleConfig} that the {@link ChannelInlet} of the channel
 * connected its channel with. This entry also holds info on all the connected {@link ChannelOutlet}
 * 
 * @author gpapanikos
 */
public class ChannelRegistryEntry
{

	/** The logger. */
	private static Logger logger = Logger.getLogger(ChannelRegistryEntry.class.getName());
	
	/** The synch nozzles. */
	private final Object synchNozzles = new Object();
	
	/** The Key. */
	private ChannelRegistryKey Key = null;
	
	/** The State. */
	private ChannelState State = null;
	
	/** The Inlet nozzle id. */
	private String InletNozzleID = null;
	
	/** The Nozzles. */
	private List<RegisteredNozzle> Nozzles = null;
	
	/** The Configuration */
	private INozzleConfig Config = null;

	/**
	 * Instantiates a new channel registry entry.
	 * 
	 * @param Key the key with which the entry is associated with 
	 * @param State the synchronization point of the channel
	 * @param Config the configuration provided to the {@link ChannelInlet}
	 * @param RegisteringNozzle the inlet nozzle identifier
	 */
	public ChannelRegistryEntry(ChannelRegistryKey Key, ChannelState State, INozzleConfig Config, String RegisteringNozzle)
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Creating new registry entry for key " + Key.toString());
		this.Key = Key;
		this.State = State;
		this.Config = Config;
		this.Nozzles = new ArrayList<RegisteredNozzle>();
		this.InletNozzleID = RegisteringNozzle;
	}

	/**
	 * Gets the registry key.
	 * 
	 * @return the channel registry key
	 */
	public ChannelRegistryKey GetRegistryKey()
	{
		return this.Key;
	}

	/**
	 * Gets the inlet nozzle identifier
	 * 
	 * @return the identifier
	 */
	public String GetInletNozzleID()
	{
		return this.InletNozzleID;
	}

	/**
	 * Gets the synchronization point of the channel
	 * 
	 * @return the channel state
	 */
	public ChannelState GetState()
	{
		return this.State;
	}

	/**
	 * Gets the proxy that acts on behalf of the channel for the inlet nozzle
	 * 
	 * @return the channel proxy
	 */
	public IChannelProxy GetProxy()
	{
		return this.Config.GetChannelProxy();
	}

	/**
	 * Gets whether the channel supports multiple outlets
	 * 
	 * @return true, if it is supported
	 */
	public boolean GetIsBroadcast()
	{
		return this.Config.GetIsBroadcast();
	}
	
	public INozzleConfig GetConfig()
	{
		return this.Config;
	}

	/**
	 * Gets the number of outlet nozzles the channel supports
	 * 
	 * @return the number of outlet nozzles supported
	 */
	public int GetRestrictBroadcast()
	{
		return this.Config.GetRestrictBroadcast();
	}

	//	
	// public List<RegisteredNozzle> GetRegisteredNozzles()
	// {
	// return this.Nozzles;
	// }

	/**
	 * Registers a new outlet nozzle to the channel. The registration may be prohibited depending on 
	 * the values of {@link ChannelRegistryEntry#GetIsBroadcast()} and 
	 * {@link ChannelRegistryEntry#GetRestrictBroadcast()}
	 * 
	 * @param entry the new outlet nozzle information
	 * @return true, if the registration was successful, false if no more nozzles can be registered
	 */
	public boolean RegisterNozzle(RegisteredNozzle entry)
	{
		synchronized (this.synchNozzles)
		{
			int nozzleCount = this.Nozzles.size();
			if (nozzleCount == 0)
			{
				this.Nozzles.add(entry);
				return true;
			}
			if (!this.GetIsBroadcast())
			{
				return false;
			}
			else
			{
				if (this.GetRestrictBroadcast() <= 0)
				{
					this.Nozzles.add(entry);
					return true;
				}
				else if (this.GetRestrictBroadcast() > 0 && this.GetRestrictBroadcast() <= nozzleCount) return false;
				else
				{
					this.Nozzles.add(entry);
					return true;
				}
			}
		}
	}
	
	/**
	 * Checks the number of outlet nozzles connected to the channel 
	 * 
	 * @return true if at least one outlet nozzle has been connected
	 */
	public Boolean HasConnected()
	{
		synchronized (this.synchNozzles)
		{
			return (this.Nozzles.size()>0);
		}
	}
	
	/**
	 * Checks the outlet nozzles that have at some point connected to the channel and checks if
	 * at least one of them is still connected.
	 * 
	 * @return true if at least one nozzle is still connected
	 */
	public Boolean StillConnected()
	{
		synchronized(this.synchNozzles)
		{
			for(RegisteredNozzle rn : this.Nozzles)
			{
				if(rn.GetIsConnected()) return true;
			}
			return false;
		}
	}

	/**
	 * Gets the registered nozzles.
	 * 
	 * @return the registered nozzles
	 */
	public List<RegisteredNozzle> GetRegisteredNozzles()
	{
		synchronized (this.synchNozzles)
		{
			List<RegisteredNozzle> ret=new ArrayList<RegisteredNozzle>();
			for(RegisteredNozzle rn : this.Nozzles)
			{
				ret.add(rn);
			}
			return ret;
		}
	}

	/**
	 * Disposes the entry. The {@link ChannelState#Dispose()} method is called, and then the {@link INozzleConfig#Dispose()}
	 * and for each connected nozzle, the {@link RegisteredNozzle#Dispose()}  
	 */
	public void Dispose()
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing registry entry for registry key " + this.Key.toString());
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing chanel state for registry key " + this.Key.toString());
		this.State.Dispose();
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing chanel inlet config for registry key " + this.Key.toString());
		this.Config.Dispose();
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Disposing registered Nozzles for registry key " + this.Key.toString());
		for (RegisteredNozzle noz : this.Nozzles)
		{
			noz.Dispose();
		}
		this.Nozzles.clear();
	}
}

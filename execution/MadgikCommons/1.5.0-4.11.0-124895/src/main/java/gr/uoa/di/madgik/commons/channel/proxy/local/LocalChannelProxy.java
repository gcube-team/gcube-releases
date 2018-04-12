package gr.uoa.di.madgik.commons.channel.proxy.local;

import gr.uoa.di.madgik.commons.channel.events.ChannelState;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelInlet;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelOutlet;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistry;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryEntry;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey;
import gr.uoa.di.madgik.commons.channel.registry.RegisteredNozzle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines a local proxy capable of mediating between an {@link ChannelInlet} and a number
 * of {@link ChannelOutlet} that reside within the same virtual machine's address space
 * 
 * @author gpapanikos
 */
public class LocalChannelProxy implements IChannelProxy
{
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static Logger logger=Logger.getLogger(LocalChannelProxy.class.getName());
	
	/** The Registry key. */
	private ChannelRegistryKey RegistryKey = null;
	
	/** The Locator. */
	private IChannelLocator Locator = null;
	
	/** The Nozzle id. */
	private String NozzleID=null;
	
	/**
	 * Instantiates a new local channel proxy to be used on the {@link ChannelInlet} side
	 */
	protected LocalChannelProxy()
	{
	}

	/**
	 * Creates a new instance. This is to be used on the {@link ChannelOutlet} side. A call to {@link IChannelProxy#CanHandleProxyLocator(gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator.LocatorType)}
	 * is made to check if the locator provided can be used with this proxy instantiation. The locator that can be used with this
	 * proxy must be of type {@link LocalChannelLocator}. If either of the the two above conditions do not hold, an exception
	 * is thrown. The local registry is then looked through the {@link ChannelRegistry#Retrieve(ChannelRegistryKey)}
	 * using as key the id retrieved by {@link IChannelLocator#GetRegistryKey()}. If the channel is not registered
	 * an exception is thrown. Otherwise, a reference to the {@link ChannelState} is retrieved and stored within the proxy.
	 * Before continuing the {@link ChannelRegistryEntry#RegisterNozzle(RegisteredNozzle)} method is called to
	 * register the outlet nozzle
	 *
	 * @param Locator The locator this proxy should use as returned by the inlet's proxy {@link IChannelProxy#GetLocator()}
	 * @param NozzleID the id of the outlet nozzle 
	 */
	public LocalChannelProxy(IChannelLocator Locator,String NozzleID)
	{
		this.NozzleID=NozzleID;
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Creating Local Proxy");
		if (!this.CanHandleProxyLocator(Locator.GetLocatorType()))
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "LocalProxy cannot handle locators of type " + Locator.GetLocatorType().toString());
			throw new IllegalArgumentException("LocalProxy cannot handle locators of type " + Locator.GetLocatorType().toString());
		}
		if (!(Locator instanceof LocalChannelLocator))
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Incompatible declared and found types of locators");
			throw new IllegalArgumentException("Incompatible declared and found types of locators");
		}
		this.Locator = Locator;
		ChannelRegistryEntry entry = ChannelRegistry.Retrieve(Locator.GetRegistryKey());
		if(entry==null)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Registry does not contain entry for regtistry key " + this.Locator.GetRegistryKey());
			throw new IllegalArgumentException("Registry does not contain etnry for regtistry key " + this.Locator.GetRegistryKey());
		}
		if(!entry.RegisterNozzle(new RegisteredNozzle(this.NozzleID, null)))
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Registry does not permit this registration of this nozzle");
			throw new IllegalArgumentException("Registry does not permit this registration of this nozzle");
		}
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#GetChannelState()
	 */
	public ChannelState GetChannelState()
	{
		return ChannelRegistry.Retrieve(Locator.GetRegistryKey()).GetState();
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#CanHandleProxyLocator(gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator.LocatorType)
	 */
	public Boolean CanHandleProxyLocator(IChannelLocator.LocatorType Locator)
	{
		if (Locator == IChannelLocator.LocatorType.Local) { return true; }
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#Dispose()
	 */
	public void Dispose()
	{
		ChannelRegistryEntry entry= ChannelRegistry.Retrieve(this.RegistryKey);
		if(entry==null || this.NozzleID==null) return;
		for(RegisteredNozzle rn : entry.GetRegisteredNozzles())
		{
			if(rn.GetNozzleID().equals(this.NozzleID)) 
			{
				rn.Dispose();
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#GetLocator()
	 */
	public IChannelLocator GetLocator()
	{
		if (this.Locator == null)
		{
			if (this.RegistryKey == null || this.RegistryKey.GetUniqueID() == null)
			{
				if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Nozzle not yet registered. Cannot create Locator");
				throw new IllegalStateException("Nozzle not yet registered. Cannot create Locator");
			}
			this.Locator = new LocalChannelLocator();
			this.Locator.SetRegistryKey(this.RegistryKey);
		}
		return this.Locator;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#GetProxyType()
	 */
	public ProxyConnector GetProxyType()
	{
		return IChannelProxy.ProxyConnector.Local;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#SetChannelRegistryKey(gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey)
	 */
	public void SetChannelRegistryKey(ChannelRegistryKey RegistryKey)
	{
		this.RegistryKey = RegistryKey;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#HasConnected()
	 */
	public Boolean HasConnected()
	{
		return ChannelRegistry.Retrieve(this.RegistryKey).HasConnected();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#StillConnected()
	 */
	public Boolean StillConnected()
	{
		return ChannelRegistry.Retrieve(this.RegistryKey).StillConnected();
	}
}

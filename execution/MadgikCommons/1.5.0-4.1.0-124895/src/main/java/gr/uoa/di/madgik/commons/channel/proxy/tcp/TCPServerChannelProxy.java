package gr.uoa.di.madgik.commons.channel.proxy.tcp;

import gr.uoa.di.madgik.commons.channel.events.ChannelState;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelInlet;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelOutlet;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator.LocatorType;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistry;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryEntry;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines a local proxy capable of mediating between an {@link ChannelInlet} and a number
 * of {@link ChannelOutlet} that reside either within the same virtual machine's address space
 * or in different machines, through a TCP stream
 * 
 * @author gpapanikos
 */
public class TCPServerChannelProxy implements IChannelProxy
{
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static Logger logger = Logger.getLogger(TCPServerChannelProxy.class.getName());
	
	/** The Registry key. */
	private ChannelRegistryKey RegistryKey = null;
		
	/** The Locator. */
	private IChannelLocator Locator = null;
	
	/** The Inlet protocol */
	private InletProtocol InletProt = null;
	
	/** The Outlet protocol */
	private OutletProtocol OutletProt = null;
	
	/** The Inlet side. */
	private boolean InletSide = false;
	
	/** The Nozzle id. */
	private String NozzleID = null;
	
	/** The State. */
	private ChannelState State = null;

	/**
	 * Instantiates a new TCP server channel proxy to be used on the {@link ChannelInlet} side
	 * 
	 * @param Config the configuration
	 */
	protected TCPServerChannelProxy()
	{
		this.InletSide = true;
	}

	/**
	 * Creates a new instance. This is to be used on the {@link ChannelOutlet} side. A call to {@link IChannelProxy#CanHandleProxyLocator(gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator.LocatorType)}
	 * is made to check if the locator provided can be used with this proxy instantiation. The locator that can be used with this
	 * proxy must be of type {@link TCPChannelLocator}. If either of the the two above conditions do not hold, an exception
	 * is thrown. The protocol thread is started and it is passed to it a socket connected to the end point that the provided 
	 * {@link IChannelLocator} point to. 
	 *
	 * @param Locator The locator this proxy should use as returned by the inlet's proxy {@link IChannelProxy#GetLocator()}
	 * @param NozzleID the id of the outlet nozzle 
	 */
	public TCPServerChannelProxy(IChannelLocator Locator, String NozzleID)
	{
		this.NozzleID = NozzleID;
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Creating TCP Server Proxy");
		if (!this.CanHandleProxyLocator(Locator.GetLocatorType()))
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "TCPServerProxy cannot handle locators of type " + Locator.GetLocatorType().toString());
			throw new IllegalArgumentException("TCPServerProxy cannot handle locators of type " + Locator.GetLocatorType().toString());
		}
		if (!(Locator instanceof TCPChannelLocator))
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Incompatible declared and found types of locators");
			throw new IllegalArgumentException("Incompatible declared and found types of locators");
		}
		this.Locator = Locator;
		Socket clientSock = null;
		try
		{
			clientSock = new Socket(((TCPChannelLocator) this.Locator).GetHostName(), ((TCPChannelLocator) this.Locator).GetPort());
		} catch (Exception ex)
		{
			throw new IllegalArgumentException("Could not establish connection to " + ((TCPChannelLocator) this.Locator).GetHostName() + ":" + ((TCPChannelLocator) this.Locator).GetPort());
		}
		Object synchThreadStart = new Object();
		synchronized (synchThreadStart)
		{
			this.State = new ChannelState();
			this.OutletProt = new OutletProtocol(synchThreadStart, clientSock, this.NozzleID, this.Locator.GetRegistryKey().GetUniqueID(), this.State);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#CanHandleProxyLocator(gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator.LocatorType)
	 */
	public Boolean CanHandleProxyLocator(LocatorType Locator)
	{
		if (Locator == IChannelLocator.LocatorType.TCP) { return true; }
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#GetChannelState()
	 */
	public ChannelState GetChannelState()
	{
		return this.State;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#Dispose()
	 */
	public void Dispose()
	{
		if (this.State != null)
		{
			try
			{
				this.State.Dispose();
			} catch (Exception ex)
			{
			}
		}
		if (this.InletProt != null)
		{
			try
			{
				this.InletProt.Dispose();
			} catch (Exception ex)
			{
			}
		}
		if (this.OutletProt != null)
		{
			try
			{
				this.OutletProt.Dispose();
			} catch (Exception ex)
			{
			}
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#GetLocator()
	 */
	public IChannelLocator GetLocator()
	{
		return this.Locator;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#GetProxyType()
	 */
	public ProxyConnector GetProxyType()
	{
		return IChannelProxy.ProxyConnector.TCP;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#SetChannelRegistryKey(gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey)
	 */
	public void SetChannelRegistryKey(ChannelRegistryKey RegistryKey)
	{
		this.RegistryKey = RegistryKey;
		if (this.InletSide)
		{
			ChannelRegistryEntry entry = ChannelRegistry.Retrieve(this.RegistryKey);
			if (entry == null)
			{
				if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Provided registry entry not found");
				throw new IllegalStateException("Provided registry entry not found");
			}
			this.State = entry.GetState();
			this.Locator = this.CreateProxyLocator();
			Object synchThreadStart = new Object();
			synchronized (synchThreadStart)
			{
				this.InletProt = new InletProtocol(entry, synchThreadStart);
			}
		}
	}

	/**
	 * Creates the proxy locator.
	 * 
	 * @param servSock the server sock
	 * 
	 * @return the channel locator
	 */
	private IChannelLocator CreateProxyLocator()
	{
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Creating locator for proxy");
		if (TCPConnectionManager.GetConnectionManagerPort()<0)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Cannot create a locator as there is no active server socket");
			throw new IllegalStateException("Cannot create a locator as there is no active server socket");
		}
		if (TCPConnectionManager.GetConnectionManagerHostName() == null)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Cannot create a locator as the server socket is not bound");
			throw new IllegalStateException("Cannot create a locator as the server socket is not bound");
		}
		if (this.RegistryKey == null)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Registry Key not yet defined. Cannot create Locator");
			throw new IllegalStateException("Registry Key not yet defined. Cannot create Locator");
		}
		String HostAddress = TCPConnectionManager.GetConnectionManagerHostName();
		int HostPort = TCPConnectionManager.GetConnectionManagerPort();
		TCPChannelLocator loc = new TCPChannelLocator(HostAddress, HostPort);
		loc.SetRegistryKey(this.RegistryKey);
		if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Locator created " + loc.toString());
		return loc;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#HasConnected()
	 */
	public Boolean HasConnected()
	{
		if (this.InletSide)
		{
			return ChannelRegistry.Retrieve(this.RegistryKey).HasConnected();
		}
		else
		{
			return this.OutletProt.HasConnected();
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy#StillConnected()
	 */
	public Boolean StillConnected()
	{
		if (this.InletSide)
		{
			return ChannelRegistry.Retrieve(this.RegistryKey).StillConnected();
		}
		else
		{
			return this.OutletProt.StillConnected();
		}
	}
}

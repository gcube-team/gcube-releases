package gr.uoa.di.madgik.commons.channel.proxy;

import java.io.Serializable;

import gr.uoa.di.madgik.commons.channel.events.ChannelState;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelInlet;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelOutlet;
import gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistry;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey;

/**
 * Interface of the proxy class that will handle the mediation between {@link ChannelInlet} and {@link ChannelOutlet}.
 * Depending on the side where the proxy is initialized, inlet or outlet there is a different
 * initialization procedure.
 * <p>
 * In the <b>inlet side</b> the proxy is created with any configuration it may support.
 * It is then passed to the {@link ChannelInlet} method as part of the configuration argument {@link INozzleConfig}.
 * this method will use the {@link ChannelRegistry#Register(ChannelState, INozzleConfig, String)}
 * method to register its channel and will associate the {@link IChannelProxy} with this channel. It
 * will also call {@link IChannelProxy#SetChannelRegistryKey(ChannelRegistryKey)}. At this point, after the
 * call setting the managed registry entry, the implementation of the {@link IChannelProxy} must be fully initialized and ready
 * to serve requests from a {@link ChannelOutlet}. It must be able to create a {@link IChannelLocator} that can fully identify the channel
 * that was set by the registry using the set channel registry method. Full initialization for a proxy that mediates between remote
 * nozzles and needs to synchronize the channel means also that a protocol implementing thread is instantiated.
 * </p>
 * <p>
 * In the <b>outlet side</b> the proxy is initialized with any configuration it may support but also
 * with the an {@link IChannelLocator} identifying the channel that the outlet will access. Upon initialization
 * the {@link IChannelProxy} must be ready to be used by a {@link ChannelOutlet} to access the identified channel. Full initialization
 * for a proxy that mediates between remote nozzles and needs to synchronize the channel means also that a protocol
 * implementing thread is instantiated. 
 * </p>
 * 
 * @author gpapanikos
 */
public interface IChannelProxy extends Serializable
{
	
	/**
	 * The types of proxy connectors known and usable by the framework
	 */
	public enum ProxyConnector
	{
		
		/**
		 * Only a local proxy should be used. The channel handled by the proxy can only be consumed
		 * locally and in the same address space to the one produced
		 */
		Local,
		
		/**
		 * A TCP proxy will be used which will create a new TCP listener on some Port.
		 * The channel handled by the proxy can by consumed on any location, both local and remote
		 */
		TCP
	}
	
	/**
	 * Retrieves a locator to this proxy
	 *
	 * @return The locator
	 */
	public IChannelLocator GetLocator();
	
	/**
	 * Retrieves the type of proxy
	 *
	 * @return The proxy type
	 */
	public ProxyConnector GetProxyType();
	
	/**
	 * Checks if this proxy can handle the specific type of locator
	 *
	 * @param Locator The type of Locator
	 * @return Whether or not it can handle the provided locator type
	 */
	public Boolean CanHandleProxyLocator(IChannelLocator.LocatorType Locator);
	
	/**
	 * Sets the {@link ChannelRegistryKey} this proxy will manage. This method is called by the
	 * {@link ChannelRegistry#Register(ChannelState, INozzleConfig, String)}
	 * when called by the {@link ChannelInlet} initialization constructor. This proxy instance is
	 * passed to the nozzle's constructor through the {@link INozzleConfig} and is updated by calling 
	 * the {@link IChannelProxy#SetChannelRegistryKey(ChannelRegistryKey)} from the registry's method. 
	 * 
	 * @param RegistryKey the registry key
	 */
	public void SetChannelRegistryKey(ChannelRegistryKey RegistryKey);
	
	/**
	 * Retrieves the {@link ChannelState} that the proxy mediates for and is used to synchronize
	 * between the various instances of the {@link ChannelState} all connected nozzles use 
	 * 
	 * @return the channel state
	 */
	public ChannelState GetChannelState(); 
	
	/**
	 * Depending on the side the {@link IChannelProxy} mediates for, this method has a slightly different
	 * meaning. On the inlet side, it checks if any outlets has been connected to the exposed channel.
	 * On the outlet side, it checks if the outlet has been connected to the inlet.
	 * 
	 * @return Whether or not someone has been connected
	 */
	public Boolean HasConnected();
	
	/**
	 * Depending on the side the {@link IChannelProxy} mediates for, this method has a slightly different
	 * meaning. On the inlet side, it checks if any outlets is still connected to the exposed channel.
	 * On the outlet side, it checks if the outlet is still connected to the inlet. This method should
	 * only be used after the {@link IChannelProxy#HasConnected()} has already been set to <code>true</code>
	 * 
	 * @return Whether or not someone is still connected
	 */
	public Boolean StillConnected();

	/**
	 * Disposes the proxy
	 */
	public void Dispose();
}

package gr.uoa.di.madgik.commons.channel.nozzle;

import java.io.Serializable;

import gr.uoa.di.madgik.commons.channel.proxy.IChannelProxy;
import org.w3c.dom.Node;

/**
 * This class provides configuration on the creation of a new channel through a {@link ChannelInlet}. Implementations
 * of this interface provide different configuration options for the created channel
 * 
 * @author gpapanikos
 */
public interface INozzleConfig extends Serializable
{
	public enum ConfigType
	{
		Local,
		TCP
	}
	
	public ConfigType GetConfigType();
	
	public String ToXML() throws Exception;
	
	public void FromXML(String XML) throws Exception;
	
	public void FromXML(Node XML) throws Exception;
	
	/**
	 * Gets the channel proxy.
	 * 
	 * @return the channel proxy
	 */
	public IChannelProxy GetChannelProxy();
	
	/**
	 * Sets the proxy.
	 * 
	 * @param ChannelProxy the channel proxy
	 */
	public void SetProxy(IChannelProxy ChannelProxy);
	
	/**
	 * Retrieves whether the channel supports multiple connected {@link ChannelOutlet} instances
	 * 
	 * @return true, if the multiple {@link ChannelOutlet} instances can be connected
	 */
	public boolean GetIsBroadcast();
	
	/**
	 * Sets whether the channel supports multiple connected {@link ChannelOutlet} instances
	 * 
	 * @param Broadcast whether the channel supports multiple connected {@link ChannelOutlet} instances
	 */
	public void SetIsBroadcast(boolean Broadcast);
	
	/**
	 * Retrieves the maximum number of connected to the channel {@link ChannelOutlet} instances if the
	 * channel supports multiple instances as set by the {@link INozzleConfig#SetIsBroadcast(boolean)}.
	 * In case a non positive number is provided, no restrictions are enforced.
	 * 
	 * @return the number of simultaneously connected {@link ChannelOutlet} instances to allow
	 */
	public int GetRestrictBroadcast();
	
	/**
	 * Sets the maximum number of connected to the channel {@link ChannelOutlet} instances if the
	 * channel supports multiple instances as set by the {@link INozzleConfig#SetIsBroadcast(boolean)}.
	 * In case a non positive number is provided, no restrictions are enforced.
	 * 
	 * @param RestrictBroadcast the number of simultaneously connected {@link ChannelOutlet} instances to allow
	 */
	public void SetRestrictBroadcast(int RestrictBroadcast);
	
	/**
	 * Disposes the configuration and all underlying state
	 */
	public void Dispose();
}

package gr.uoa.di.madgik.commons.channel.proxy;

import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey;

import java.io.Serializable;
import java.net.URI;

/**
 * Interface of Locators that can identify a channel. The scope of each locator depends on the
 * capabilities of the proxy that creates them. Depending on the scope of the Proxy, different locators
 * can be created.
 * 
 * @author gpapanikos
 */
public interface IChannelLocator extends Serializable
{
	
	/**
	 * Defines the available locators that can be used  by the framework
	 */
	public enum LocatorType
	{
		
		/**
		 * Local locator type, capable of identifying a channel in the same address
		 * space as the one it was created in
		 */
		Local,
		
		/**
		 * A locator that uses a TCP connection to identify the referenced channel, capable of
		 * identifying a channel from wherever it may be used
		 */
		TCP
	}
	
	/**
	 * Retrieves the Locator Type of the proxy locator
	 *
	 * @return The Locator type
	 */
	public LocatorType GetLocatorType();
	
	/**
	 * Retrieves the registry UUID the channel that this locator identifies has been assigned
	 *
	 * @return The registry UUID of the identified channel
	 */
	public ChannelRegistryKey GetRegistryKey();
	
	/**
	 * Sets the registry UUID the channel that this locator identifies has been assigned
	 *
	 * @param RegistryKey The registry UUID of the identified channel
	 */
	public void SetRegistryKey(ChannelRegistryKey RegistryKey);
	
	/**
	 * Marshals the locator in a serialized human readable XML form that can then be passed to {@link IChannelLocator#FromXML(String)}
	 * to create a new instance of the locator. 
	 *
	 * @return The serialized form of the locator
	 * @throws java.lang.Exception The serialization could not be performed
	 */
	public URI ToURI() throws Exception;
	
	/**
	 * Unmarshals a serialization as returned by the {@link IChannelLocator#ToXML()} method
	 *
	 * @param xml The locator serialization
	 * @throws java.lang.Exception The deserialization could not be performed
	 */
	public void FromURI(URI locator) throws Exception;
	
	/**
	 * Marshals the locator in a serialized form that can then be passed to {@link IChannelLocator#Decode(byte[])}
	 * to create a new instance of the locator.
	 *
	 * @return The serialized form of the locator
	 * @throws java.lang.Exception The serialization could not be performed
	 */
	public byte[] Encode() throws Exception;
	
	/**
	 * Unmarshals a serialization as returned by the {@link IChannelLocator#Encode()} method
	 *
	 * @param payload The locator serialization
	 * @throws java.lang.Exception The deserialization could not be performed
	 */
	public void Decode(byte[] payload) throws Exception;
}

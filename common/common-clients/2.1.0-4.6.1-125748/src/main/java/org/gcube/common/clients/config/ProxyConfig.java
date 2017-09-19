package org.gcube.common.clients.config;

import org.gcube.common.clients.delegates.ProxyPlugin;

/**
 * The configuration of service proxies.
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the type of service addresses
 * @param <S> the type of service stubs
 */
public interface ProxyConfig<A,S> {

	/**
	 * Returns the timeout.
	 * @return the timeout
	 */
	public long timeout();
	
	
	/**
	 * Returns the {@link ProxyPlugin}.
	 * @return the plugin
	 */
	public ProxyPlugin<A,S,?> plugin();
	
	/**
	 * Adds a custom property to the configuration.
	 * @param name the name of the property
	 * @param value the value of the property
	 * 
	 * @param <T> the type of the property value
	 */
	public <T> void addProperty(String name, T value);

	/**
	 * Adds a custom property to the configuration.
	 * @param property the property
	 */
	public void addProperty(Property<?> property);

	/**
	 * Returns <code>true</code> if the configuration includes a given custom property.
	 * @param property the name of the property
	 * @return
	 */
	public boolean hasProperty(String property);
	
	/**
	 * Returns the value of a given custom property.
	 * @param property the name of the property
	 * @param clazz the type of the property value
	 * @return the property value
	 * @throws IllegalStateException if the property is not included in the configuration
	 * @throws IllegalArgumentException if the property exists but its value has a different value
	 * 
	 * * @param <T> the type of the property value
	 */
	<T> T property(String property, Class<T> clazz) throws IllegalStateException, IllegalArgumentException;

}

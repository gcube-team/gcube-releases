package org.gcube.common.clients.config;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.clients.delegates.ProxyPlugin;

/**
 * Partial implementation of {@link ProxyConfig}.
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the type of service addresses
 * @param <S> the type of service stubs
 */
public abstract class AbstractConfig<A,S> implements ProxyConfig<A,S> {

	private final ProxyPlugin<A,S,?> plugin;
	private final Map<String,Object> properties = new HashMap<String, Object>();
	
	/**
	 * Creates an instance with a given {@link ProxyPlugin}.
	 * @param plugin the plugin
	 */
	protected AbstractConfig(ProxyPlugin<A,S,?> plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public ProxyPlugin<A,S,?> plugin() {
		return plugin;
	}
	
	@Override
	public long timeout() throws IllegalArgumentException {
		if (!hasProperty(Property.timeout))
			throw new IllegalArgumentException("timeout property is undefined");
		else
			return property(Property.timeout,Long.class);
	}
	
	
	@Override
	public <T> void addProperty(String name, T value) {
		properties.put(name, value);
	}

	@Override
	public void addProperty(Property<?> property) {
		properties.put(property.name(),property.value());
	}

	@Override
	public boolean hasProperty(String property) {
		return properties.containsKey(property);
	}
	
	@Override
	public <T> T property(String property, Class<T> clazz) throws IllegalStateException, IllegalArgumentException {
		if (!hasProperty(property))
			throw new IllegalStateException(property+" is unknown");
		try {
			return clazz.cast(properties.get(property));
		}
		catch(Exception e) {
			throw new IllegalArgumentException("could not retrieve "+property+" as "+clazz,e);
		}
	}
}

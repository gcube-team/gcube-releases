package org.gcube.common.clients.builders;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.config.DiscoveryConfig;
import org.gcube.common.clients.config.EndpointConfig;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.delegates.DirectDelegate;
import org.gcube.common.clients.delegates.DiscoveryDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.delegates.ProxyPlugin;
import org.gcube.common.clients.queries.Query;

/**
 * Partial implementation of proxy builders.
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the type of service addresses
 * @param <S> the type of service stubs
 * @param <P> the type of service proxies
 */
public abstract class AbstractBuilder<A,S,P> {

	/**
	 * Default proxy timeout.
	 */
	public static final int defaultTimeout = (int)TimeUnit.SECONDS.toMillis(10); 
	
	private final ProxyPlugin<A,S,P> plugin;
	private Query<A> query;
	private W3CEndpointReference address;
	private final EndpointCache<A> cache;
	private final Map<String,Object> properties = new HashMap<String, Object>();
    
	/**
	 * Constructs an instance with a given {@link ProxyPlugin}, and {@link EndpointCache}, and zero or more default {@link Property}s.
	 * @param plugin the plugin
	 * @param cache the cache
	 * @param properties the properties
	 */
    protected AbstractBuilder(ProxyPlugin<A,S,P> plugin,EndpointCache<A> cache,Property<?> ... properties) {
		
    	this.plugin=plugin;
    	this.cache=cache;
		
		//sets default timeout, may be overridden by custom properties below
		setTimeout(defaultTimeout);
		
		//add custom properties
		for (Property<?> property : properties)
			addProperty(property);
	}
    
    /**
     * Returns the {@link ProxyPlugin}.
     * @return the plugin
     */
    protected ProxyPlugin<A,S,P> plugin() {
		return plugin;
	}
    /**
     * Sets the query.
     * @param query the query
     */
    protected void setQuery(Query<A> query) {
		this.query = query;
	}
    
    /**
     * Sets the timeout.
     * @param timeout the timout
     */
    protected void setTimeout(int timeout) {
    	addProperty(Property.timeout(timeout));
    }
    
    /**
     * Sets the address.
     * @param address the address
     */
    protected void setAddress(W3CEndpointReference address) {
    	this.address=address;
    }
    
    /**
     * Adds a custom property.
     * @param property the property
     */
    protected void addProperty(Property<?> property) {
    	properties.put(property.name(),property.value());
    }
    
    //shared among subclasses
    public P build() {

    	ProxyDelegate<S> delegate = null;
		if (address==null) {
			DiscoveryConfig<A,S> config = 
					new DiscoveryConfig<A,S>(plugin,query,cache); 
			for (Entry<String,Object> prop : properties.entrySet())
				config.addProperty(prop.getKey(),prop.getValue());
			delegate =  new DiscoveryDelegate<A,S>(config);
		}
		else {
			EndpointConfig<A,S> config = new EndpointConfig<A,S>(plugin,convertAddress(address));
			for (Entry<String,Object> prop : properties.entrySet())
				config.addProperty(prop.getKey(),prop.getValue());
			delegate =  new DirectDelegate<A,S>(config);
		}
		
		return plugin.newProxy(delegate);
    }
    
    /**
     * Converts a {@link W3CEndpointReference} in a service address.
     * @param address the address as a {@link W3CEndpointReference}
     * @return the converted address
     */
    protected abstract A convertAddress(W3CEndpointReference address);
    

    protected abstract String contextPath();
}

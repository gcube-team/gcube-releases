package org.gcube.common.clients.fw.builders;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.clients.builders.AbstractSingletonBuilder;
import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.fw.Utils;
import org.gcube.common.clients.fw.plugin.Plugin;

/**
 * Default implementation of {@link SingletonBuilder}.
 * 
 * @author Fabio Simeoni
 * 
 * @param <S> the type of service stubs
 * @param <P> the type of service proxies
 */
public final class SingletonBuilderImpl<S,P> extends AbstractSingletonBuilder<EndpointReference,S,P> implements SingletonBuilder<P> {

	/**
	 * Constructs an instance with a given {@link Plugin} and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param properties the properties
	 */
    public SingletonBuilderImpl(Plugin<S,P> plugin,Property<?> ... properties) {
		this(plugin,Utils.globalCache,properties);
	}

	/**
	 * Constructs an instance with a given {@link Plugin}, an {@link EndpointCache}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param cache the cache
	 * @param properties the properties
	 */
    public SingletonBuilderImpl(Plugin<S,P> plugin,EndpointCache<EndpointReference> cache,Property<?> ... properties) {
		super(plugin,cache,properties);
	}
    
    @Override
    protected EndpointReference convertAddress(W3CEndpointReference address) {
    	return address;
    }
    
    @Override
    protected String contextPath() {
    	return Utils.contextPath;
    }

}

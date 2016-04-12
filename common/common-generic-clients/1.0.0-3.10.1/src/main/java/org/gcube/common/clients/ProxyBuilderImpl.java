package org.gcube.common.clients;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.clients.builders.AbstractStatelessBuilder;
import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.queries.Query;

/**
 * Default implementation of {@link ProxyBuilder}.
 * 
 * @author Fabio Simeoni
 *
 * @param <S>
 * @param <P>
 */
public class ProxyBuilderImpl<S,P> extends AbstractStatelessBuilder<EndpointReference,S,P> implements ProxyBuilder<P> {

	/**
	 * Creates an instance with a given {@link Plugin} and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param properties the properties
	 */
    public ProxyBuilderImpl(Plugin<S,P> plugin, Property<?> ... properties) {
		this(plugin,new LegacyQuery(plugin),Utils.globalCache,properties);
	}
    
	/**
	 * Creates an instance with a given {@link Plugin}, a {@link Query}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param query the {@link StatelessQuery}
	 * @param properties the properties
	 */
    public ProxyBuilderImpl(Plugin<S,P> plugin, Query<EndpointReference> query, Property<?> ... properties) {
		this(plugin,query,Utils.globalCache,properties);
    }
	
		
	/**
	 * Creates an instance with a given {@link Plugin}, an {@link EndpointCache}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param cache the {@link EndpointCache}
	 * @param properties the properties
	 */
    public ProxyBuilderImpl(Plugin<S,P> plugin,EndpointCache<EndpointReference> cache,Property<?> ... properties) {
		this(plugin,new LegacyQuery(plugin),cache,properties);
	}

	/**
	 * Creates an instance with a given {@link Plugin},a {@link Query}, an {@link EndpointCache}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param query the {@link StatelessQuery}
	 * @param cache the {@link EndpointCache}
	 * @param properties the properties
	 */
    public ProxyBuilderImpl(Plugin<S,P> plugin,Query<EndpointReference> query,EndpointCache<EndpointReference> cache,Property<?> ... properties) {
		super(plugin,cache,query,properties);
	}

    
	@Override
	protected EndpointReference convertAddress(W3CEndpointReference address) {
		return address;
	}

	@Override
	protected String contextPath() {
		return "/";
	}
}
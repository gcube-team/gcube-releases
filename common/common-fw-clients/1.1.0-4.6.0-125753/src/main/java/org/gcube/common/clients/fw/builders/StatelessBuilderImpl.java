package org.gcube.common.clients.fw.builders;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.clients.builders.AbstractStatelessBuilder;
import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.fw.Utils;
import org.gcube.common.clients.fw.plugin.Plugin;
import org.gcube.common.clients.fw.queries.StatelessQuery;

/**
 * Default implementation of {@link StatelessBuilder}.
 * 
 * @author Fabio Simeoni
 *
 * @param <S> the type of service stubs
 * @param <P> the type of service proxies
 */
public class StatelessBuilderImpl<S,P> extends AbstractStatelessBuilder<EndpointReference,S,P> implements StatelessBuilder<P> {

	/**
	 * Constructs an instance with a given {@link Plugin} and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param properties the properties
	 */
    public StatelessBuilderImpl(Plugin<S,P> plugin, Property<?> ... properties) {
		this(plugin,new StatelessQuery(plugin),Utils.globalCache,properties);
	}
    
	/**
	 * Constructs an instance with a given {@link Plugin}, a {@link StatelessQuery}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param query the {@link StatelessQuery}
	 * @param properties the properties
	 */
    public StatelessBuilderImpl(Plugin<S,P> plugin, StatelessQuery query, Property<?> ... properties) {
		this(plugin,query,Utils.globalCache,properties);
    }
	
		
	/**
	 * Constructs an instance with a given {@link Plugin}, an {@link EndpointCache}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param cache the {@link EndpointCache}
	 * @param properties the properties
	 */
    public StatelessBuilderImpl(Plugin<S,P> plugin,EndpointCache<EndpointReference> cache,Property<?> ... properties) {
		this(plugin,new StatelessQuery(plugin),cache,properties);
	}

	/**
	 * Constructs an instance with a given {@link Plugin},a {@link StatelessQuery}, an {@link EndpointCache}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param query the {@link StatelessQuery}
	 * @param cache the {@link EndpointCache}
	 * @param properties the properties
	 */
    public StatelessBuilderImpl(Plugin<S,P> plugin,StatelessQuery query,EndpointCache<EndpointReference> cache,Property<?> ... properties) {
		super(plugin,cache,query,properties);
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

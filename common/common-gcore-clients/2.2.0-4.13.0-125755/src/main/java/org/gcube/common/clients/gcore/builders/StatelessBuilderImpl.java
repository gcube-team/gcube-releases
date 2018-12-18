package org.gcube.common.clients.gcore.builders;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.builders.AbstractStatelessBuilder;
import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.delegates.ProxyPlugin;
import org.gcube.common.clients.gcore.Utils;
import org.gcube.common.clients.gcore.plugins.Plugin;
import org.gcube.common.clients.gcore.plugins.PluginAdapter;
import org.gcube.common.clients.gcore.queries.GCoreQuery;
import org.gcube.common.clients.gcore.queries.StatelessQuery;

/**
 * Proxy builders for stateless gCore services.
 * 
 * @author Fabio Simeoni
 *
 * @param <S> the type of service stubs
 * @param <P> the type of service proxies
 */
public class StatelessBuilderImpl<S,P> extends AbstractStatelessBuilder<EndpointReferenceType,S,P> implements StatelessBuilder<P> {

	/**
	 * Constructs an instance with a given {@link ProxyPlugin} and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param properties the properties
	 */
    public StatelessBuilderImpl(Plugin<S,P> plugin, Property<?> ... properties) {
		this(plugin,GCoreQuery.globalCache,properties);
	}

	/**
	 * Constructs an instance with a given {@link ProxyPlugin}, a {@link StatelessQuery}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param query the {@link StatelessQuery}
	 * @param properties the properties
	 */
    public StatelessBuilderImpl(Plugin<S,P> plugin, StatelessQuery query, Property<?> ... properties) {
		this(plugin,query,GCoreQuery.globalCache,properties);
    }
	
		
	/**
	 * Constructs an instance with a given {@link ProxyPlugin}, an {@link EndpointCache}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param cache the {@link EndpointCache}
	 * @param properties the properties
	 */
    public StatelessBuilderImpl(Plugin<S,P> plugin,EndpointCache<EndpointReferenceType> cache,Property<?> ... properties) {
		this(plugin,new StatelessQuery(plugin),cache,properties);
	}

	/**
	 * Constructs an instance with a given {@link ProxyPlugin},a {@link StatelessQuery}, an {@link EndpointCache}, and zero or more default {@link Property}s.
	 * @param plugin the {@link Plugin}
	 * @param query the {@link StatelessQuery}
	 * @param cache the {@link EndpointCache}
	 * @param properties the properties
	 */
    public StatelessBuilderImpl(Plugin<S,P> plugin,StatelessQuery query,EndpointCache<EndpointReferenceType> cache,Property<?> ... properties) {
		super(new PluginAdapter<S,P>(plugin),cache,query,properties);
	}
    
    @Override
    protected EndpointReferenceType convertAddress(W3CEndpointReference address) {
    	return Utils.convert(address);
    }
    
    @Override
    protected String contextPath() {
    	return Utils.GCORE_CONTEXTPATH;
    }
 
}

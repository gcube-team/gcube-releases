package org.gcube.common.clients.config;

import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.delegates.DiscoveryDelegate;
import org.gcube.common.clients.delegates.ProxyPlugin;
import org.gcube.common.clients.queries.Query;

/**
 * The configuration of a proxy created in discovery mode.
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the type of service addresses
 * @param <S> the type of service stubs
 * 
 * @see DiscoveryDelegate
 *
 */
public class DiscoveryConfig<A,S> extends AbstractConfig<A,S> {

	private final Query<A> query;
	private final EndpointCache<A> cache;
	
	/**
	 * Creates an instance with a given {@link ProxyPlugin}, {@link Query} and call timeout.
	 * @param plugin the plugin
	 * @param query the query
	 * @param the timeout
	 */
	public DiscoveryConfig(ProxyPlugin<A,S,?> plugin,Query<A> query, EndpointCache<A> cache) {
		super(plugin);
		this.query=query;
		this.cache=cache;
	}
	
	/**
	 * Returns the address cache used by the proxy.
	 * @return the cache
	 */
	public EndpointCache<A> cache() {
		return cache;
	}
	
	/**
	 * Returns the query used by the proxy.
	 * @return the query
	 */
	public Query<A> query() {
		return query;
	}
}

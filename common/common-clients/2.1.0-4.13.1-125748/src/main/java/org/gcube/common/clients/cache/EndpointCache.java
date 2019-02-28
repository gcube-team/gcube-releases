package org.gcube.common.clients.cache;

import org.gcube.common.clients.delegates.DiscoveryDelegate;

/**
 * A cross-service cache of endpoint addresses used by {@link DiscoveryDelegate}s.
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @param <A> the type of service addresses
 * 
 * @see Key
 * @see DiscoveryDelegate
 *
 */
public interface EndpointCache<A> {

	/**
	 * Resets the cache for a given {@link Key}.
	 * @param key the key
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	void clear(Key key) throws IllegalArgumentException;
	
	/**
	 * Returns the address cached for a given {@link Key}
	 * @param key the key
	 * @return the endpoint address, or <code>null</code> if there is no endpoint address cached for the service
	 * @throws IllegalArgumentException if the key is <code>null</code>
	 */
	A get(Key key) throws IllegalArgumentException;
	
	/**
	 * Caches an endpoint address for a given {@link Key}
	 * @param key the key
	 * @param address the address
	 * @throws IllegalArgumentException if the key or the address are <code>null</code>
	 */
	void put(Key Key,A address) throws IllegalArgumentException;
}

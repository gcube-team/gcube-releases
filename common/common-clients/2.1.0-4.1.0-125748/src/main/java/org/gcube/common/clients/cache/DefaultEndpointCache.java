package org.gcube.common.clients.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.clients.delegates.DiscoveryDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of {@link EndpointCache}.
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the type of the service addresses
 * 
 * @see DiscoveryDelegate
 */
public class DefaultEndpointCache<A> implements EndpointCache<A> {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultEndpointCache.class);
	
	/** Service map. */
	private Map<Key,A> cache = Collections.synchronizedMap(new HashMap<Key,A>());
	
	@Override
	public void clear(Key key) throws IllegalArgumentException {
		
		assertnotNull(key,"key");
		
		logger.debug("clearing cache {} for {}",this,key);
		
		cache.put(key, null);	
	}

	@Override
	public A get(Key key) throws IllegalArgumentException {
		
		assertnotNull(key,"key");
		
		return cache.get(key);
	}

	@Override
	public void put(Key key, A address) throws IllegalArgumentException {
		
		assertnotNull(key,"key");
		assertnotNull(address,"address");
		
		logger.debug("caching {} for {}",address,key);
		
		cache.put(key,address);
		
	}

	//helper
	private void assertnotNull(Object object, String msg) throws IllegalArgumentException {
		if (object==null)
			throw new IllegalArgumentException(msg+" is null");
	}
	
}

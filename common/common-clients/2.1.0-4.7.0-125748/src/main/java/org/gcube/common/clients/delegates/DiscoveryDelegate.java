package org.gcube.common.clients.delegates;

import static org.gcube.common.clients.cache.Key.*;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.cache.Key;
import org.gcube.common.clients.config.DiscoveryConfig;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.queries.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ProxyDelegate} that discovers service endpoints.
 * 
 * <p>
 * 
 * The delegates attempt to make {@link Call}s to endpoints cached in an {@link EndpointCache}.
 * If the calls fail, or the cache is empty, they execute a {@link Query} for endpoints and call the results in turn until the call succeeds or there are no
 * more endpoints to call. If the call succeeds with one endpoint, the delegates cache the endpoint in the {@link EndpointCache}.
 * 
 * @author Fabio Simeoni
 * 
 * @param <A> the type of service addresses
 * @param<S> the type of service stubs
 * 
 * @see Query
 * @see EndpointCache
 */
public class DiscoveryDelegate<A,S> extends AbstractDelegate<A,S,DiscoveryConfig<A,S>> {

	private static Logger log = LoggerFactory.getLogger(DiscoveryDelegate.class);

	/**
	 * Creates an instance with a {@link ProxyPlugin}, a {@link Query}, and an {@link EndpointCache}.
	 * 
	 * @param plugin the plugin
	 * @param query the query
	 * @param cache the cache
	 */
	public DiscoveryDelegate(DiscoveryConfig<A,S> config) {
		super(config);
	}
	
	//helper
	private boolean isAnchored() {
		return config().hasProperty(Property.sticky_session) ?
						config().property(Property.sticky_session,Boolean.class):
						false;
	}
	
	@Override
	public <V> V make(Call<S, V> call) throws Exception {

		ProxyPlugin<A,S,?> plugin = config().plugin();
		Query<A> query = config().query();
		EndpointCache<A> cache = config().cache();
		
		// create key in call scope
		Key key = key(plugin.name(),query);

		// try with each endpoint in turn
		Exception lastFault = null;

		// use cache first, if any
		A lgeAddress = cache.get(key);

		use_cache:if (lgeAddress != null)
			
			try {		
				
				log.info("calling {} @ {} (cached)", plugin.name(),lgeAddress);
				
				S lge = null;
				
				try {
					lge=plugin.resolve(lgeAddress,config());
				}
				catch(Exception e) {
					log.error("could not resolve "+lgeAddress,e);
					lastFault = e;
					break use_cache;
				}
				
				return call.call(lge);
				
			} catch (Exception fault) {

				cache.clear(key);
				
				fault = plugin.convert(fault,config());

				if (isUnrecoverable(fault) || isAnchored()) // exit now
					throw fault;
				else
					lastFault = fault; //move on to querying
			}

		List<A> results = null;
		
		try {
			
			log.info("executing query for {} endpoints: {}", plugin.name(),query);
			
			// execute query
			results = query.fire();
			
			// exclude cached endpoint (we do not use 'remove' in case list implementation does not support it)
			
			results =  filterResults(lgeAddress, results);
			
			if (results.size() == 0)
				throw new DiscoveryException("no endpoints found for "+query);
			
		}
		catch(DiscoveryException fault) {
			if (lastFault == null)
				throw fault;
			else 
				throw lastFault;
		}
		

		//try with each endpoint in turn
		for (A address : results)
			try {
				
				log.info("calling {} @ {}", plugin.name(), address);
				
				S stub = null;
				
				try {
					stub=plugin.resolve(address,config());
				}
				catch(Exception e) {
					log.error("could not resolve "+address,e);
					throw e;
				}
				
				V result = call.call(stub);
				
				cache.put(key, address);
				
				return result;
			
			} catch (Exception fault) {
				
				lastFault= plugin.convert(fault,config());
				
				if(lastFault==null)
					lastFault=fault;
				
				if (isUnrecoverable(lastFault) || isAnchored()) // exit now
					break;
			}

		throw lastFault;
	}
	
	//helper
	private <CR> boolean isUnrecoverable(Exception e) {

		try {
			return e.getClass().isAnnotationPresent(Unrecoverable.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// helper
	private List<A> filterResults(A cached, List<A> results) {
		List<A> endpoints = new ArrayList<A>();
		for (A result : results)
			if (!result.equals(cached))
				endpoints.add(result);
		return endpoints;
	}
}

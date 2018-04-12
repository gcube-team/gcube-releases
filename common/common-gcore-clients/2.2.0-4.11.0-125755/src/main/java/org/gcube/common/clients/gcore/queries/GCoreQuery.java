package org.gcube.common.clients.gcore.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.cache.DefaultEndpointCache;
import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.delegates.ProxyPlugin;
import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.gcore.plugins.Plugin;
import org.gcube.common.clients.gcore.plugins.PluginAdapter;
import org.gcube.common.clients.queries.Query;
import org.gcube.common.clients.queries.ResultMatcher;

/**
 * Partial implementation of {@link Query}s for gCore services.
 * 
 * @author Fabio Simeoni
 *
 * @param <R> the type of query results
 */
public abstract class GCoreQuery<R> implements Query<EndpointReferenceType> {

	
	private final ProxyPlugin<EndpointReferenceType,?,?> plugin;
	
	public static EndpointCache<EndpointReferenceType> globalCache = new DefaultEndpointCache<EndpointReferenceType>();
	
	private final Map<String,String> conditions = new HashMap<String, String>();
	
	private final ISFacade facade;
	
	/**
	 * Creates an instance with a {@link ISFacade}, a {@link PluginAdapter}, and a type of IS queries
	 * @param facade the facade
	 * @param plugin the plugin
	 * @param queryClass the query type
	 */
	protected GCoreQuery(ISFacade facade, Plugin<?,?> plugin) {
		this.plugin = plugin;
		this.facade=facade;
	}
	
	/**
	 * Returns the {@link ISFacade} used for query execution.
	 * @return the facade
	 */
	protected ISFacade facade() {
		return facade;
	}
	
	
	//default matcher does not filter out any result
	private ResultMatcher<R> matcher = new ResultMatcher<R>() {
		@Override public boolean match(R doc) {
			return true;
		}
	};
	
	/**
	 * Adds a condition to the query.
	 * @param property an expression that identifies a property of service endpoints
	 * @param value the value of the property
	 */
	public void addCondition(String property, String value) {
		this.conditions.put(property,value);
	}
	
	/**
	 * Sets an {@link ResultMatcher} for the query.
	 * @param matcher the matcher.
	 */
	public void setMatcher(ResultMatcher<R> matcher) {
		this.matcher=matcher;
	}
	
	@Override
	public final List<EndpointReferenceType> fire() throws DiscoveryException {
		
		//delegate actual execution to subclass-specific mechanisms
		List<R> results = fire(conditions);

		//from results to addresses
		List<EndpointReferenceType> endpoints = new ArrayList<EndpointReferenceType>();
		
		for (R result : results)
			try {
				if (matcher.match(result)) //should we include this? ask matcher
					endpoints.add(address(result)); //extract address
			}
			catch(IllegalArgumentException e) {
				//skip result, this is just a signal from subclasses
			};

		return endpoints;
	}
	
	/**
	 * Executes the query through implementation-specific means.
	 * @param conditions the conditions to apply on the query prior to its execution
	 * @return the query results
	 * @throws DiscoveryException if the query could not be executed
	 */
	protected abstract List<R> fire(Map<String,String> conditions) throws DiscoveryException;
	
	/**
	 * Returns an endpoint address from a query result.
	 * @param result the result
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the result
	 */
	protected abstract EndpointReferenceType address(R result) throws IllegalArgumentException;
	
	/**
	 * Returns the {@link ProxyPlugin}.
	 * @return the plugin
	 */
	protected ProxyPlugin<EndpointReferenceType,?,?> plugin() {
		return plugin;
	}
	
	//queries are value objects based on properties
	
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GCoreQuery<?> other = (GCoreQuery<?>) obj;
		if (conditions == null) {
			if (other.conditions != null)
				return false;
		} else if (!conditions.equals(other.conditions))
			return false;
		return true;
	}
	
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
		return result;
	}
	
	@Override
	public final String toString() {
		return conditions.toString();
	}
}

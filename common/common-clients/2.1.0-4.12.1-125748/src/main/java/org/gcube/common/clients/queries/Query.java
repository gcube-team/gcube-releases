package org.gcube.common.clients.queries;

import java.util.List;

import org.gcube.common.clients.exceptions.DiscoveryException;



/**
 * A query for the endpoints of a given service.
 * 
 * @author Fabio Simeoni
 *
 * @param <A> the type of service endpoint addresses
 */
public interface Query<A> {

	/**
	 * Executes the query.
	 * 
	 * @return the addresses of the discovered endpoints
	 * @throws DiscoveryException if query execution fails
	 */
	List<A> fire() throws DiscoveryException;
	
	
	//emphasise
	
	@Override
	public boolean equals(Object query);
	
	@Override
	public int hashCode();
	
	@Override
	public String toString();
}

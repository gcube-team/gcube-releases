/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api;

import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public interface Query {

	/**
	 * @param query
	 * @param limit
	 * @param fetchPlan
	 * @return
	 * @throws InvalidQueryException
	 * http://orientdb.com/docs/last/OrientDB-REST.html#query
	 */
	public String query(String query, int limit, String fetchPlan)
			throws InvalidQueryException;
	
}

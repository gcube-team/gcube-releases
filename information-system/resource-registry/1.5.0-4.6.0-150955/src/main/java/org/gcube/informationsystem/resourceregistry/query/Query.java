/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.query;

import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;

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
	public String query(String query, Integer limit, String fetchPlan)
			throws InvalidQueryException;
	
	public String gremlinQuery(String query) throws InvalidQueryException;
	
}

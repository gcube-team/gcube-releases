/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api;

import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface Query {

	/**
	 * @param query
	 * @param fetchPlan
	 * @return
	 * @throws InvalidQueryException
	 * http://orientdb.com/docs/last/OrientDB-REST.html#query
	 */
	String execute(String query, String fetchPlan)
			throws InvalidQueryException;
	
}

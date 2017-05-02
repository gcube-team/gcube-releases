package org.gcube.resources.discovery.client.queries.api;

import java.net.URI;



/**
 * A {@link Query} that can be customised with namespace declarations, conditions on results, and result expressions.
 * 
 */
public interface SimpleQuery extends Query {
	
	/**
	 * Adds a variable to the query. 
	 * 
	 * @param name the name of the variable
	 * @param range the range of the variable
	 * @return the query
	 */
	SimpleQuery addVariable(String name, String range);	
	
	/**
	 * Adds a free-form condition on query results. 
	 * 
	 * @param condition the condition
	 * @return the query
	 */
	SimpleQuery addCondition(String condition);
	
	/**
	 * Adds a namespace to the query. 
	 * @param prefix the namespace prefix
	 * @param uri the namespace URI
	 * @return the query
	 */
	SimpleQuery addNamespace(String prefix, URI uri);

	/**
	 * Adds a result expression to the query.
	 * @param expression the result expression
	 * @return the query
	 */
	SimpleQuery setResult(String expression);
	
}

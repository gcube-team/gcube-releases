package org.gcube.common.core.informationsystem.client;

import java.util.List;

import javax.management.Query;

import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.scope.GCUBEScope;

/**
 * Defines the local interface to query an Information System in a gCube infrastructure.
 * Clients obtain the {@link Query Queries} supported by the underlying implementation from their standard interfaces.
 * These queries are scoped and may return results either by-value or by-reference.
 * 
 * 
 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
 *
 * 
 *  */
public interface ISClient {

	/**
	 * Evaluates a query in a given scope and returns its results a local list.
	 * @param <RESULT> The type of the query results.
	 * @param query the query.
	 * @param scope the scope.
	 * @return the results.
	 * @throws ISMalformedQueryException if the query is incorrectly formulated and cannot produce results.
	 * @throws ISException if the evaluation could not be completed.
	 * 
	 */
	public <RESULT> List<RESULT> execute(ISQuery<RESULT> query, GCUBEScope scope) throws ISMalformedQueryException,ISUnsupportedQueryException,ISException;
	
	/**
	 * Evaluates a query in a given scope and returns the {@link ISInputStream} which contains its results.
	 * @param <RESULT> The type of the query results.
	 * @param query the query.
	 * @param scope the scope.
	 * @return the reader.
	 * @throws ISMalformedQueryException if the query is incorrectly formulated and cannot produce results.
	 * @throws ISException if the evaluation could not be completed.
	 * 
	 */
	public <RESULT> ISInputStream<RESULT> executeByRef(ISQuery<RESULT> query, GCUBEScope scope) throws ISMalformedQueryException,ISUnsupportedQueryException,ISException;
	
	/**
	 * Returns a query from the interface or class which defines its type.
	 * The method is intended as a means to bind query interfaces to concrete implementations provided
	 * along with implementations of this interface.
	 * @param <RESULT> the type of the query results.
	 * @param <QUERY> the type of the query which produces results of type RESULT.
	 * @param type the query interface or class.
	 * @return the query.
	 * @throws ISUnsupportedQueryException
	 */
	public <RESULT, QUERY extends ISQuery<RESULT>> QUERY getQuery(Class<QUERY> type) throws ISUnsupportedQueryException, InstantiationException,IllegalAccessException;

	/**
	 * Returns a {@link GCUBEGenericQuery} from its implementation-defined name.
	 * The method is intended as a means to lookup generic queries pre-defined by 
	 * implementations of this interface.
	 * @param name the query name
	 * @return the query.
	 * @throws ISUnsupportedQueryException
	 */
	public GCUBEGenericQuery getQuery(String name) throws ISUnsupportedQueryException;

	/**
	 * Exception raised when the query expression is not well-formed with respect to the language supported
	 * by the underlying implementation of {@link ISClient}. 
	 *  
	 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
	 *
	 */
	public static class ISMalformedQueryException extends ISException {private static final long serialVersionUID = 1L;}
	
	/**
	 * Exception raised when the implementation {@link ISClient} does not support a given query. 
	 *  
	 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
	 *
	 */
	public static class ISUnsupportedQueryException extends ISException {private static final long serialVersionUID = 1L;}
	
	/**
	 * Exception raised when the results of a query cannot be parsed by the implementation underlying {@link ISClient}.
	 *  
	 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
	 *
	 */
	public static class ISMalformedResultException extends ISException {
		private static final long serialVersionUID = 1L;
		public ISMalformedResultException(Exception cause) {super(cause);}
	}

}

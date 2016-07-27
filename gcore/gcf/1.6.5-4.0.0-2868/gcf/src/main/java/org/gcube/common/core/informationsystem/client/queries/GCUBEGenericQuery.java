package org.gcube.common.core.informationsystem.client.queries;

import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISQuery;
import org.gcube.common.core.informationsystem.client.QueryParameter;
import org.gcube.common.core.informationsystem.client.XMLResult;

/**
 * A specialisation of {@link ISQuery} to free-form queries.
 * Such queries are unconstrained in their expression but are expected
 * to produce results in some XML serialisation. Furthermore, the queries 
 * may be parametric, i.e. necessitate one or more
 * {@link QueryParameter ISQueryParameters} to be fully defined. 
 * 
 * Parametric queries may be introduced by client applications, but they are more commonly
 * pre-defined by implementations of the {@link ISClient} interface and 
 * then instantiated and used within client applications (see {@link ISClient#getQuery(String)}).
 * In any case, the documentation of query parameters is outside the scope of this
 * interface.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 */
public interface GCUBEGenericQuery extends ISQuery<XMLResult> {
	
	   
	   /**
		 * Adds one or more {@link QueryParameter QueryParameters} to the query.
		 * @param parameters the parameters.
		 */
		public void addParameters(QueryParameter ... parameters);
}

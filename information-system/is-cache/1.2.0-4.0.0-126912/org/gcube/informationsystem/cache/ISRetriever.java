/**
 * 
 */
package org.gcube.informationsystem.cache;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISClient.ISUnsupportedQueryException;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEServiceQuery;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;

/**
 * Supporting class that provides some IS artifacts (mainly query objects).
 * 
 * @author UoA
 * 
 */
public class ISRetriever {

	/**
	 * Get an ISClient instance
	 * 
	 * @return an ISClient instance
	 * @throws Exception
	 *             in case of error
	 */
	protected static ISClient getISClient() throws Exception {
		return (org.gcube.common.core.informationsystem.client.ISClient) GHNContext
				.getImplementation(org.gcube.common.core.informationsystem.client.ISClient.class);
	}

	/**
	 * Get an IS generic query object
	 * 
	 * @return the IS generic query object
	 * @throws ISUnsupportedQueryException
	 *             in case of error
	 * @throws InstantiationException
	 *             in case of error
	 * @throws IllegalAccessException
	 *             in case of error
	 * @throws Exception
	 *             in case of error
	 */
	public static GCUBEGenericQuery getGenericQuery()
			throws ISUnsupportedQueryException, InstantiationException,
			IllegalAccessException, Exception {
		return ISRetriever.getISClient().getQuery(GCUBEGenericQuery.class);
	}

	/**
	 * Get an IS WSResource query object
	 * 
	 * @return the IS WSResource query object
	 * @throws ISUnsupportedQueryException
	 *             in case of error
	 * @throws InstantiationException
	 *             in case of error
	 * @throws IllegalAccessException
	 *             in case of error
	 * @throws Exception
	 *             in case of error
	 */
	public static WSResourceQuery getWSResourceQuery()
			throws ISUnsupportedQueryException, InstantiationException,
			IllegalAccessException, Exception {
		return ISRetriever.getISClient().getQuery(WSResourceQuery.class);
	}

	/**
	 * Get an IS RIQuery query object
	 * 
	 * @return the IS RIQuery query object
	 * @throws ISUnsupportedQueryException
	 *             in case of error
	 * @throws InstantiationException
	 *             in case of error
	 * @throws IllegalAccessException
	 *             in case of error
	 * @throws Exception
	 *             in case of error
	 */
	public static GCUBERIQuery getRIQuery() throws ISUnsupportedQueryException,
			InstantiationException, IllegalAccessException, Exception {
		return ISRetriever.getISClient().getQuery(GCUBERIQuery.class);
	}

	/**
	 * Get an IS ServiceQuery query object
	 * 
	 * @return the IS ServiceQuery query object
	 * @throws ISUnsupportedQueryException
	 *             in case of error
	 * @throws InstantiationException
	 *             in case of error
	 * @throws IllegalAccessException
	 *             in case of error
	 * @throws Exception
	 *             in case of error
	 */
	public static GCUBEServiceQuery getServiceQuery()
			throws ISUnsupportedQueryException, InstantiationException,
			IllegalAccessException, Exception {
		return ISRetriever.getISClient().getQuery(GCUBEServiceQuery.class);
	}

	/**
	 * Get the default IS query timeout
	 * 
	 * @return the default IS query timeout
	 */
	protected static long getDefaultISQueryTimeoutPeriod() {
		return 100000L;
	}

}

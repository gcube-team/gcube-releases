package org.gcube.portal.social.networking.ws.utils;

import org.gcube.socialnetworking.social_data_search_client.ElasticSearchClientImpl;
import org.gcube.socialnetworking.social_data_search_client.ElasticSearchClientInterface;
import org.slf4j.LoggerFactory;

/**
 * The class discovers and offer connections to the elastic search cluster.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ElasticSearchConnection {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ElasticSearchConnection.class);

	// databook store (singleton)
	private static ElasticSearchClientInterface es; 

	// lock object used for creating the DatabookStore object once
	private static final Object LOCK = new Object();

	/**
	 * Returns the object to connect to cassandra cluster.
	 * @return connection pool to cassandra cluster
	 * @throws Exception 
	 */
	public static ElasticSearchClientInterface getElasticSearchConnection() throws Exception{

		if(es == null){
			synchronized(LOCK){
				if(es == null){
					logger.info("Creating connection to Elasticsearch");
					es = new ElasticSearchClientImpl(null);
					logger.info("Elasticsearch connection created");
				}
			}
		}

		return es;
	}

}

package org.gcube.portal.social.networking.ws.utils;

import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.socialnetworking.social_data_search_client.ElasticSearchClient;
import org.gcube.socialnetworking.social_data_search_client.ElasticSearchClientImpl;
import org.slf4j.LoggerFactory;

/**
 * The class discovers and offer connections to the elastic search cluster.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ElasticSearchConnection {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ElasticSearchConnection.class);

	// databook store (singleton)
	private ElasticSearchClient es; 

	// singleton
	private static ElasticSearchConnection singleton = new ElasticSearchConnection();

	private ElasticSearchConnection(){
		try {
			ApplicationContext ctx = ContextProvider.get(); // get this info from SmartGears
			logger.info("Creating connection to Elasticsearch");
			es = new ElasticSearchClientImpl(ctx.container().configuration().infrastructure());
			logger.info("Elasticsearch connection created");
		} catch (Exception e) {
			logger.error("Failed to connect to elasticsearch", e);
		}
	}

	public static ElasticSearchConnection getSingleton(){

		return singleton;

	}

	/**
	 * Returns the object to connect to cassandra cluster.
	 * @return connection pool to cassandra cluster
	 * @throws Exception 
	 */
	public ElasticSearchClient getElasticSearchClient(){

		return es;

	}

}

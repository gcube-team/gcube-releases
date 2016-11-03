package org.gcube.portal.social.networking.ws.utils;

import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.slf4j.LoggerFactory;

/**
 * Cassandra connection class.
 * @author Costantino Perciante at ISTI-CNR
 */
public abstract class CassandraConnection {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CassandraConnection.class);

	// databook store (singleton)
	private static DatabookStore store; 

	// lock object used for creating the DatabookStore object once
	private static final Object LOCK = new Object();
	
	/**
	 * Returns the object to connect to cassandra cluster.
	 * @return connection pool to cassandra cluster
	 */
	public static DatabookStore getDatabookStore(){

		if(store == null){
			synchronized(LOCK){
				if(store == null){
					logger.info("Getting connection to cassandra");
					store = new DBCassandraAstyanaxImpl();
					logger.info("Connection to cassandra created");
				}
			}
		}

		return store;
	}

}

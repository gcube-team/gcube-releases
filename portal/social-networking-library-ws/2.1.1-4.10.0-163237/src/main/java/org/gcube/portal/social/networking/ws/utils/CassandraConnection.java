package org.gcube.portal.social.networking.ws.utils;

import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.LoggerFactory;

/**
 * Cassandra connection class.
 * @author Costantino Perciante at ISTI-CNR
 */
public class CassandraConnection {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CassandraConnection.class);

	// databook store (singleton)
	private static DatabookStore store; 
	
	private static CassandraConnection singleton = new CassandraConnection();
	
	private CassandraConnection(){
		ApplicationContext ctx = ContextProvider.get(); // get this info from SmartGears
		logger.info("Getting connection to cassandra");
		store = new DBCassandraAstyanaxImpl(ctx.container().configuration().infrastructure());
		logger.info("Connection to cassandra created");
	}
	
	/**
	 * Returns the object to query the cassandra cluster.
	 * @return connection pool to cassandra cluster
	 */
	public DatabookStore getDatabookStore(){
		
		return store;
		
	}
	
	/**
	 * Get the instance
	 * @return
	 */
	public static CassandraConnection getInstance(){
	
		return singleton;
		
	}

}

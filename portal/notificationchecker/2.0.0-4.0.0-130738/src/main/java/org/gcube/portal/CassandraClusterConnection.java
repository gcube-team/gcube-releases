package org.gcube.portal;

import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Return an object to contact cassandra and query the cluster.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class CassandraClusterConnection {

	private static final Logger logger = LoggerFactory.getLogger(CassandraClusterConnection.class);
	private static final Object LOCK = new Object();
	private static DatabookStore store;

	public static DatabookStore getConnection(){

		if(store == null){
			synchronized (LOCK) {
				if(store == null){
					logger.info("Getting connection to cassandra");
					store = new DBCassandraAstyanaxImpl();
					logger.info("Got connection to cassandra");
				}

			}
		}

		return store;

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(store != null){
			logger.info("Closing connection to cassandra");
			store.closeConnection();
		}
	}
}

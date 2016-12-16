package org.gcube.portal.notifications.database.connections;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;

/**
 * Return an object to contact cassandra and query the cluster.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CassandraClusterConnection {

	private static final Log logger = LogFactoryUtil.getLog(CassandraClusterConnection.class);
	private static final Object LOCK = new Object();
	private static DatabookStore store;

	/**
	 * Retrieve a connection library instance
	 * @return null on error
	 */
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

	/**
	 * Close connection to cassandra
	 * @return 
	 */
	public static void closeConnection(){
		if(store != null){
			logger.info("Closing connection to cassandra");
			store.closeConnection();
			logger.info("Closed connection to cassandra");
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		closeConnection();
	}
}

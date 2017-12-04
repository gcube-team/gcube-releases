package org.gcube.portal.notifications.database.connections;

import java.sql.Connection;
import java.sql.SQLException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;

/**
 * Get a connection to the liferay db
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ConnectionDBLiferay {

	private static Connection conn;
	private static final Object LOCK = new Object();
	private static final Log logger = LogFactoryUtil.getLog(ConnectionDBLiferay.class);
	
	/**
	 * Get connection to liferay
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException{

		if(conn == null){
			synchronized (LOCK) {
				if(conn == null){
					logger.debug("Trying to get Connection to Liferay Database");
					conn = DataAccess.getConnection();
					logger.info("Connection got to Liferay Database");
				}
			}
		}

		return conn;
	}
	
	/**
	 * Close connection to cassandra
	 * @return 
	 */
	public static void closeConnection(){
		if(conn != null){
			logger.debug("Closing connection to liferay db");
			try{
				conn.close();
				logger.info("Closed connection to liferay db");
			}catch(Exception e){
				logger.error("Unable to close connection to liferay db", e);
			}
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		closeConnection();
	}
}

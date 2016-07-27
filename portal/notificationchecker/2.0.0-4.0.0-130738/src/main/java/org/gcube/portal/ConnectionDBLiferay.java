package org.gcube.portal;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;

/**
 * Get a connection to the liferay db
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ConnectionDBLiferay {

	private static Connection conn;
	private static final Object LOCK = new Object();

	private static final Logger logger = LoggerFactory.getLogger(ConnectionDBLiferay.class);

	
	/**
	 * Get connection to liferay
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException{

		if(conn == null){
			synchronized (LOCK) {
				if(conn == null){

					logger.info("Trying to get Connection From API");
					conn = DataAccess.getConnection();
					logger.info("CONNECTED TO LR DB!");

				}
			}
		}

		return conn;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
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

}

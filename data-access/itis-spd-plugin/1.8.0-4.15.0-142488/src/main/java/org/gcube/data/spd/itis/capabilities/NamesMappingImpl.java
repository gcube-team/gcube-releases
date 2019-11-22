package org.gcube.data.spd.itis.capabilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gcube.data.spd.itis.dbconnection.ConnectionPool;
import org.gcube.data.spd.itis.dbconnection.ConnectionPoolException;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class NamesMappingImpl implements MappingCapability{

	static Logger logger = LoggerFactory.getLogger(NamesMappingImpl.class);

	/**
	 * Mapping metod
	 */
	@Override
	public void getRelatedScientificNames(ObjectWriter<String> writer,
			String commonName) {
		
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet rs = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			String query = null;
			query = "select completename from longnames natural join vernaculars where UPPER(vernacular_name) like UPPER(?) order by completename";
			if (query!=null)
				rs =  pool.selectPrestatement(query, "%" + commonName + "%");	
			if (!writer.isAlive()) return;
			if (rs!=null){	
				while(rs.next()) {
					//				logger.trace(rs.getString(1));
					writer.write(rs.getString(1));       
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Error", e);

		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		} finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
			try {
				if (rs != null) 
					rs.close();
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
		}
	}


}

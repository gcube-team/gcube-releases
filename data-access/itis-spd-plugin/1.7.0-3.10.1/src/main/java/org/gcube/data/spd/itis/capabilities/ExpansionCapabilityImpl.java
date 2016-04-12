package org.gcube.data.spd.itis.capabilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.itis.dbconnection.ConnectionPool;
import org.gcube.data.spd.itis.dbconnection.ConnectionPoolException;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public class ExpansionCapabilityImpl implements ExpansionCapability {

	static GCUBELog logger = new GCUBELog(ExpansionCapabilityImpl.class);

	@Override
	public void getSynonyms(ObjectWriter<String> writer, String scientifcName) {
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet rs = null;
		try{
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select tsn from longnames where UPPER(completename) like UPPER(?)";		
			rs =  pool.selectPrestatement(query, "%" + scientifcName + "%");	

			if (rs!=null){	
				while(rs.next()){	
					String tsn = rs.getString(1);   		
					getSynonymnsById(writer, tsn);
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Error", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);			
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
				if ((pool!=null) && (con!=null)){
					pool.releaseConnection(con);
				}
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
		}
	}



	//get all synonyms by id
	public void getSynonymnsById(ObjectWriter<String> writer, String id) {
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select b.completename from synonym_links as a NATURAL join longnames as b where a.tsn_accepted = ?";
			results =  pool.selectPrestatement(query, id);	

			if (results!=null){

				while(results.next()){						
					if (!writer.isAlive()) return;
					writer.write(results.getString(1));
				}
			}
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);			
		} catch (SQLException e) {
			logger.error("SQL Error", e);

		} finally {	
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
	}



}

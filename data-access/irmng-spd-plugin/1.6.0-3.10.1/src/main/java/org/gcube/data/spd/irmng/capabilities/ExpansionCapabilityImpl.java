package org.gcube.data.spd.irmng.capabilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPool;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public class ExpansionCapabilityImpl implements ExpansionCapability {

	GCUBELog logger = new GCUBELog(ExpansionCapabilityImpl.class);

	@Override
	public void getSynonyms(ObjectWriter<String> writer, String scientifcName) {

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String name = "%" + scientifcName + "%";
			String query = "select taxonid from taxon where UPPER(name) like UPPER(?)";	
			results =  pool.selectPrestatement(query, name);
			if (results!=null){

				while (results.next()){
					ResultSet result = null;
					try{
						String  id = results.getString(1);
						String query1 = "select name from taxon where taxonomicstatus = 'synonym' and acceptednameusageid = ?";
						result =  pool.selectPrestatement(query1, id);

						if (result!=null){
							while (result.next()){
								if (!writer.isAlive()) return;
								writer.write(result.getString(1));					
							}
						}
					}catch (Exception e) {
						logger.error("general Error", e);
					}finally{						
						try {
							if (result!=null)
								result.close();
						} catch (SQLException e) {
							logger.error("errore closing resultSet", e);
						}
					}
				}
			}
		}
		catch (Throwable e) {
			logger.error("general Error", e);
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
			try {
				if (results!=null)
					results.close();
			} catch (SQLException e) {
				logger.error("error closing resultSet", e);
			}
		}
	}

}
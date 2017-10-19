package org.gcube.data.spd.ncbi.capabilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.gcube.data.spd.ncbi.Utils;
import org.gcube.data.spd.ncbi.connection.ConnectionPool;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpansionCapabilityImpl implements ExpansionCapability {

	static Logger logger = LoggerFactory.getLogger(ExpansionCapabilityImpl.class);

	@Override
	public void getSynonyms(ObjectWriter<String> writer, String scientifcName) {


		ResultSet rs1 = null;
		try{
			rs1 = Utils.getListScientificNameID(scientifcName);
			if (rs1!=null){

				while(rs1.next()) {	
					String  id = rs1.getString(1);
					getSynById(id, writer);
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}finally{
			try {
				if (rs1 != null)
					rs1.close();
			} catch (SQLException e) {
				logger.error("sql Error", e);
			}
		}


	}


	private void getSynById(String id, ObjectWriter<String> writer) {
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			//			logger.trace("select b.name_txt, a.rank, a.parent_tax_id from nodes as a NATURAL JOIN names as b where b.name_class = 'synonym' and a.tax_id = " + id  );

			String query = ("select b.name_txt from nodes as a NATURAL JOIN names as b where b.name_class = 'synonym' and CAST(a.tax_id as TEXT) like ?");			
			ArrayList<String> terms = new ArrayList<String>();
			terms.add(id);
			rs = pool.selectPrestatement(query, terms, stmt);
			if (rs!=null){

				while(rs.next()) {	
					if (!writer.isAlive()) return;
					writer.write(rs.getString(1));

				}
			}
		}			
		catch (Throwable e) {
			logger.error("general Error", e);
		} finally {

			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
	}

}



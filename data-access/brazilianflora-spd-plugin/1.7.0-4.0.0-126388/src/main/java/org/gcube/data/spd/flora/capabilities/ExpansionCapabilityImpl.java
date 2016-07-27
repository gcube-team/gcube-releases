package org.gcube.data.spd.flora.capabilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.data.spd.flora.FloraPlugin;
import org.gcube.data.spd.flora.dbconnection.ConnectionPool;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpansionCapabilityImpl implements ExpansionCapability {

	Logger logger = LoggerFactory.getLogger(ExpansionCapabilityImpl.class);

	@Override
	public void getSynonyms(ObjectWriter<String> writer, String scientifcName) {
		List<String> ids = null;

		try{

			ids = getSynonymsBySName(scientifcName);
			for (String id : ids){
				if (!writer.isAlive()) return;
				getSynonymsById(id, writer);
			}
		}catch (Exception e) {
			writer.write(new StreamBlockingException("BrazilianFlora",""));
		}

	}


	//get a list of ids by Scientific name
	private List<String> getSynonymsBySName(String scientifcName) {
		ConnectionPool pool = null;
		ResultSet results = null;
		List<String> ids = new ArrayList<String>();
		try {
			String term = "%" + scientifcName + "%";
			pool = ConnectionPool.getConnectionPool();
			String query ="select id from "+ FloraPlugin.tableName + " where UPPER(scientific_name) like UPPER(?)";
			results =  pool.selectPrestatement(query, term);	

			if(results!=null) {	

				while(results.next()) {	
					ids.add(results.getString(1));					
				}
			}
		}
		catch (SQLException sqlExcept) {
			logger.error("sql Error", sqlExcept);
		}catch (Throwable e) {
			logger.error("general Error", e);
		}
		finally {
			try {
				if (results != null) {
					results.close();
				}
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		}

		return ids;
	}


	private void getSynonymsById(String id, ObjectWriter<String> writer) {

		ConnectionPool pool = null;
		ResultSet results1 = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			String query ="select scientific_name from "+ FloraPlugin.tableName + " where acceptednameusageid = ?";			
			results1 =  pool.selectPrestatement(query, id);	
			if(results1!=null) {	

				while(results1.next()) {
					if (writer.isAlive())
						writer.write(results1.getString(1));					
				}
				//				System.out.println("synonyms.size() " + synonyms.size());
			}
		}
		catch (SQLException sqlExcept) {
			logger.error("sql Error", sqlExcept);
		}catch (Throwable e) {
			logger.error("general Error", e);
		}
		finally {
			try {
				if (results1 != null) {
					results1.close();
				}
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		}

	}

}

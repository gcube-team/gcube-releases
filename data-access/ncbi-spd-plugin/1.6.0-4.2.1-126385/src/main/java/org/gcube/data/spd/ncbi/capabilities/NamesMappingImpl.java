package org.gcube.data.spd.ncbi.capabilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.ncbi.connection.ConnectionPool;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;




public class NamesMappingImpl implements MappingCapability{

	private static GCUBELog logger = new GCUBELog(NamesMappingImpl.class);

	@Override
	public void getRelatedScientificNames(ObjectWriter<String> writer,
			String commonName) {
		logger.trace("commonNameToScientificNamesMapping for commonName "+commonName+" in NCBI...");
		
		ResultSet rs = null;
		ResultSet res1 = null;

		ConnectionPool pool = null;
		Connection con = null;
		PreparedStatement stmt = null;
		Statement statement;
		
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			statement = con.createStatement();
			ArrayList<String> terms = new ArrayList<String>();
			terms.add("%" + commonName + "%");
			//			logger.trace("select distinct(tax_id) from names where name_class = 'common name' and UPPER(name_txt) like UPPER('%" + commonName + "%')");
			rs = pool.selectPrestatement("select distinct(tax_id) from names where name_class = 'common name' and UPPER(name_txt) like UPPER(?)", terms, stmt);

			while(rs.next()) {	
				String id = rs.getString(1);
				//				logger.trace("select name_txt from names where name_class = 'scientific name' and tax_id = " + id_cm );
				res1 = statement.executeQuery("select name_txt from names where name_class = 'scientific name' and tax_id = " + id );		
				if(res1.next()){	
					String name = res1.getString(1);
					if (!writer.isAlive()) return;
					writer.write(name);
				}

			}

			terms.clear();
			//			logger.trace("commonNameToScientificNamesMapping finished for commonName "+commonName+" in NCBI");
		}

		catch (SQLException sqlExcept) {
			logger.error("sql Error", sqlExcept);
		}catch (Throwable e) {
			logger.error("general Error", e);
		} finally {
			try {
				if (rs != null) 
					rs.close();

				if (con != null) 
					con.close();

			} catch (SQLException ex) {
				logger.error("sql Error", ex);

			}
		}
		
	}

//	@Override
//	public Set<String> commonNameToScientificNamesMapping(String commonName){
//
//		logger.trace("commonNameToScientificNamesMapping for commonName "+commonName+" in NCBI...");
//		Set<String> set = new HashSet<String>();
//
//		ResultSet rs = null;
//		ResultSet res1 = null;
//
//		ConnectionPool pool = null;
//		Connection con = null;
//		PreparedStatement stmt = null;
//		try {
//			pool = ConnectionPool.getConnectionPool();
//			con = pool.getConnection();
//
//			ArrayList<String> terms = new ArrayList<String>();
//			terms.add("%" + commonName + "%");
//			//			logger.trace("select distinct(tax_id) from names where name_class = 'common name' and UPPER(name_txt) like UPPER('%" + commonName + "%')");
//			rs = pool.selectPrestatement("select distinct(tax_id) from names where name_class = 'common name' and UPPER(name_txt) like UPPER(?)", terms, stmt);
//
//			while(rs.next()) {	
//				String id = rs.getString(1);
//
//				ArrayList<String> terms1 = new ArrayList<String>();
//				terms1.add(id);
//				//				logger.trace("select name_txt from names where name_class = 'scientific name' and tax_id = " + id_cm );
//				res1 = pool.selectPrestatement("select name_txt from names where name_class = 'scientific name' and tax_id = ?", terms1, stmt );
//
//				if(res1.next()){	
//					String name = res1.getString(1);
//					set.add(name);
//				}
//				terms1.clear();
//			}
//
//			terms.clear();
//			//			logger.trace("commonNameToScientificNamesMapping finished for commonName "+commonName+" in NCBI");
//		}
//
//		catch (SQLException sqlExcept) {
//			logger.error("sql Error", sqlExcept);
//		}catch (Throwable e) {
//			logger.error("general Error", e);
//		} finally {
//			try {
//				if (rs != null) 
//					rs.close();
//
//				if (con != null) 
//					con.close();
//
//			} catch (SQLException ex) {
//				logger.error("sql Error", ex);
//
//			}
//		}
//
//		return set;
//	}
//
//	@Override
//	public Set<String> scientificNameToCommonNamesMapping(String scientificName){
//
//		logger.trace("scientificNameToCommonNamesMapping for scientificName "+scientificName+" in NCBI...");
//		Set<String> set = new HashSet<String>();
//
//		ResultSet rs1 = null;
//		ResultSet rs2 = null;
//
//		ConnectionPool pool = null;
//		Connection con = null;
//		PreparedStatement stmt = null;
//		try {
//			pool = ConnectionPool.getConnectionPool();
//			con = pool.getConnection();
//
//			ArrayList<String> terms = new ArrayList<String>();
//			terms.add("%" + scientificName + "%");
//
//			ArrayList<String> terms1 = new ArrayList<String>();
//			//			logger.trace("select distinct(tax_id) from names where name_class = 'scientific name' and UPPER(name_txt) like UPPER('%" + scientificName + "%')");
//			rs1 =  pool.selectPrestatement("select distinct(tax_id) from names where name_class = 'scientific name' and UPPER(name_txt) like UPPER(?)", terms, stmt);
//			if (rs1!=null){
//				while(rs1.next()) {	
//					String id = rs1.getString(1);				
//
//					terms1.add(id);
//					//				logger.trace("select name_txt from names where name_class = 'common name' and tax_id = " + id  );
//					rs2 = pool.selectPrestatement("select name_txt from names where name_class = 'common name' and tax_id = ?", terms1, stmt );
//					if (rs2!= null){
//						while(rs2.next()) {	
//							String name = rs2.getString(1);
//							//					logger.trace(name);
//							set.add(name);
//						}
//					}
//					terms1.clear();
//				}
//
//				terms.clear();
//				//			logger.trace("scientificNameToCommonNamesMapping finished for scientificName "+scientificName+" in NCBI");
//			}
//		}
//		catch (SQLException sqlExcept)
//		{
//			logger.error("sql Error", sqlExcept);
//		}catch (Throwable e) {
//			logger.error("general Error", e);
//		} finally {
//			try {
//				if (rs1 != null) 
//					rs1.close();
//				if (rs2 != null) 
//					rs2.close();
//				if (con != null) 
//					con.close();				
//
//			} catch (SQLException ex) {
//				logger.error("sql Error", ex);
//
//			}
//		}
//
//		return set;
//	}


}

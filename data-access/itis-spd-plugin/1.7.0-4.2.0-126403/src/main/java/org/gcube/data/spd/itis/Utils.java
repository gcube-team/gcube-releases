package org.gcube.data.spd.itis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.itis.dbconnection.ConnectionPool;
import org.gcube.data.spd.itis.dbconnection.ConnectionPoolException;
import org.gcube.data.spd.model.CommonName;


public class Utils {

	static GCUBELog logger = new GCUBELog(Utils.class);
	final static String citation = "Accessed through: the Integrated Taxonomic Information System (ITIS) at http://www.itis.gov on ";
	final static String credits ="This information object has been generated via the Species Product Discovery service on XDATEX by interfacing with ITIS, the Integrated Taxonomic Information System (http://www.itis.gov/)";

	/**
	 * Get citation
	 */
	public static String getCitationItis(){
		StringBuilder cit = new StringBuilder();
		cit.append(citation);
		cit.append(getDateItis());

		return (cit.toString());
	}


	/**
	 * Get credits
	 */
	public static String getCreditsItis(){
		String cred = credits.replace("XDATEX", getDateItis());
		return (cred);
	}

	/**
	 * Get date
	 */
	private static String getDateItis() {
		String date = null;
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
		date = format.format(now.getTime());
		return date;
	}



	/**
	 * Get information (author or rank) by Id
	 * @param con2 
	 */
	public static String getInfoFromId(String id, String type) {
		//		logger.trace("getInfoFromId " + id);

		String info = null;

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet res = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			String query = null;
			if (type.equals("author")) {

				query ="select taxon_author from taxon_authors_lkp where taxon_author_id = ?";			
			}
			else if (type.equals("rank")) {		
				//								logger.trace("select rank_name from taxon_unit_types where rank_id = " + id );
				query ="select rank_name from taxon_unit_types where rank_id = ?";
			}  

			if (query !=null)
				res =  pool.selectPrestatement(query, id);	
			if (res!=null){
				if(res.next()) {		
					info = res.getString(1);		
					//				logger.trace("info: " + info);	
				}  
			}
			else return "";

		} catch (SQLException e) {
			logger.error("SQL Error", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);

		} finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
			try {
				if (res != null) {
					res.close();
				}
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
		}


		return info;		
	}

	/**
	 * Get a list of common names from a id
	 */
	public static List<CommonName> getCommonNameFromId(String id) {
		//		logger.trace("getCommonNameFromId " + id);
		List<CommonName> commonNames = null;

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select vernacular_name, language from vernaculars where tsn = ?";
			results =  pool.selectPrestatement(query, id);	

			if (results!=null){
				commonNames = new ArrayList<CommonName>();
				while(results.next()) {		
					String  name = results.getString(1);	
					String language = results.getString(2);
					//				logger.trace("name: " + name + " language: " + language);
					CommonName commonName = new CommonName(language, name);
					commonNames.add(commonName);
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
				if (results  != null) {
					results.close();
				}
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);

			}
		}
		return commonNames;
	}


	/**
	 * Return true if a table exists
	 */
	public static boolean SQLTableExists(String tableName) {
		boolean exists = false;
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();

			String sqlText = "SELECT tables.table_name FROM information_schema.tables WHERE table_name = '" + tableName + "'";    
			results = statement.executeQuery(sqlText);
			if (results.next()){
				logger.trace(tableName + " already exists");
				exists = true;
			}
			else{
				logger.trace(tableName + " does not exists");
				exists = false;
			}

		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
			try {
				if (results != null) {
					results.close();
				}
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		} 
		logger.trace(exists);
		return exists;
	}





	/**
	 * get next update date
	 */
	public static long lastupdate() {

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		long nextUpdate = 0;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();

			String query = "select date from updates where id = (select max(id) from updates)";    
			results = statement.executeQuery(query);
			Date lastUpdate = null;
			if (results.next()){
				lastUpdate = results.getDate(1);
				Date date = new Date();			
				long days = date.getTime() - lastUpdate.getTime();
				if (days < 2592000000L){
					return (2592000000L - days);
				}
			}	

		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
			try {
				if (results != null) {
					results.close();
				}
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		} 
		return nextUpdate;
	}

}


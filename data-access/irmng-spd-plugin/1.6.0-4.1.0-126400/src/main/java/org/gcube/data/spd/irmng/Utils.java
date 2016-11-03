package org.gcube.data.spd.irmng;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPool;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPoolException;
import org.postgresql.util.PSQLException;

public class Utils {

	//	final static String urlDump = "http://www.cmar.csiro.au/datacentre/downloads/IRMNG_DWC.zip";
	static GCUBELog logger = new GCUBELog(Utils.class);

	
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
//				logger.trace(tableName + " already exists");
				exists = true;
			}

			else{
//				logger.trace(tableName + " does not exists");
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
		return exists;
	}
	

	/**
	 * Metod called by CreateDBThread
	 */
	public static boolean createDB() throws SQLException, IOException {

		ConnectionPool pool = null;
		Connection con = null;
		try{
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();

			BufferedReader br = new BufferedReader(new InputStreamReader(IrmngPlugin.class.getResourceAsStream(IrmngPlugin.dumpDb)));
			String line;
			while ((line = br.readLine()) != null) {
//				logger.trace(line);
				try{
					int updateQuery = statement.executeUpdate(line);	
					if (updateQuery == 1) {
						logger.trace("Error executing : " + line);
						return false;
					}
				}catch (PSQLException e) {
					logger.trace("Exception creating tables", e);
				}
			}

		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		} finally {	
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return true;

	}

	/**
	 * Get date
	 */
	public static Calendar getCalendar(String date1) {

		Calendar cal = null;
		Date date = DateUtil.getInstance().parse(date1);
		if (date != null){
			cal=Calendar.getInstance();
			cal.setTime(new Date(date.getTime()));
		}	

		return cal;
	}

	/**
	 * Set scientific name without author
	 */
	public static String setScName(String id, String rank) {
		//		logger.trace("id " + id + " rank " + rank);
		String scientificName = "";		
		if(rank!=null){
			if (rank.equals("family"))
				scientificName = getScientifiName(id, "family");
			else if (rank.equals("genus"))
				scientificName = getScientifiName(id, "genus");
			else if (rank.equals("species"))
				scientificName = getScientifiName(id, "genus") + " "+ getScientifiName(id, "specificepithet");
			else
				scientificName = getScientifiName(id, "scientificname");
		}
		return scientificName;
	}


	/**
	 * Get scientific name without author
	 */
	private static String getScientifiName(String id, String rank) {

		String scientificName = "";
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			//			logger.trace("select " + rank + " from taxon where taxonid = "+id	);
			String query = "select " + rank + " from taxon where taxonid = ?";	
			results =  pool.selectPrestatement(query, id);

			if (results!=null){
				if(results.next()) {	        	
					scientificName = results.getString(1);	
				}
			}
			results.close();
		}catch (SQLException sqlExcept) {
			logger.error("sql Error",sqlExcept);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException",e);
		} finally{
			try {
				results.close();
			} catch (SQLException e) {
				logger.error("sql Error",e);
			}
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return scientificName;

	}



	//format date
	public static String createDate() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(now.getTime());
		return date;
	}

	public static String createCitation() {
		StringBuilder cit = new StringBuilder();
		cit.append(IrmngPlugin.citation);
		cit.append(createDate());
		return cit.toString();
	}

	public static String createCredits() {
		String cred = IrmngPlugin.credits;
		cred = cred.replace("XDATEX", createDate());	
		return cred;
	}

}


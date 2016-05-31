package org.gcube.data.spd.ncbi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.ncbi.connection.ConnectionPool;
import org.gcube.data.spd.ncbi.connection.ConnectionPoolException;


public class Utils {


	static GCUBELog logger = new GCUBELog(Utils.class);

	/**
	 * Return true if a table exists
	 */
	public static boolean SQLTableExists(String tableName) {
		boolean exists = false;
		ResultSet rs = null;
		ConnectionPool pool = null;
		Connection con = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();

			String sqlText = "SELECT tables.table_name FROM information_schema.tables WHERE table_name = '" + tableName + "'";    
			rs = statement.executeQuery(sqlText);
			if (rs.next()){
				exists = true;
				logger.trace(tableName + " exists");		
			}
			else{
				exists = false;
				logger.trace(tableName + " does not exist");
			}

		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally {

			try {
				if (rs != null) 
					rs.close();					
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		} 
		return exists;
	}


	public static String getOriginalId(String id) {

		//spit id
		String originalID = null;
		String[] originalId = null;

		if (id!=null){
			originalId = id.split("-");
			if (originalId.length > 0)
				originalID = originalId[0];
		}

		return originalID;
	}


	public static String setAuthorship(String idPost, String scName) {
		String id = null;
		try{
			id = Utils.getOriginalId(idPost);
		}
		catch (ArrayIndexOutOfBoundsException e){
			id = idPost;
			//			logger.error("id is already a original one", e);
		}

		String authors = null;
		ConnectionPool pool = null;
		Connection con = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();
			//			logger.trace(("select name_txt from names where name_class='authority' and tax_id = " + id));
			ResultSet rs = statement.executeQuery("select name_txt from names where name_class='authority' and tax_id = " + id);
			if (rs.getFetchSize() > 0){

				//								logger.trace("rs!=null  " + rs.getFetchSize());
				String author;
				int authorLenght = 0;
				int scNameLenght = 0;
				while(rs.next()){
					author = rs.getString(1);
					//								logger.trace("Authority: " + author + " - SCNAME " + scName);
					if (author!= null){

						authorLenght = author.length();
						scNameLenght = scName.length();

						if (authorLenght > scNameLenght+3){

							Boolean flag  = scName.equals(author.substring(0, scNameLenght));

							if (flag) {
								//								logger.trace("Confronta: ***" + scName + "*** con ***" + author.substring(0, scNameLenght) +"***");
								String au = author.substring(scNameLenght+1, authorLenght);
								if ((au.startsWith("(")) || (Character.isUpperCase(au.charAt(0)))){
									//									logger.trace("author " + au + " substring " + au.charAt(0));
									//								logger.trace("ok");
									//								author = author.substring(scNameLenght+1, authorLenght);
									//							logger.trace("author " + author);
									authors = au;
									//									logger.trace(authors + " break");
									continue;
								}
							}
						}
					}
				}
				rs.close();
			}
		} catch (SQLException sqlExcept){        	
			logger.error("sql Error", sqlExcept);
		}catch (Throwable e){
			logger.error("general Error", e);
		} finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return authors;
	}

	//	public static List<String> setAuthorship(String id, String scName) {
	//		List<String> authors = null;
	//		ConnectionPool pool = null;
	//		Connection con = null;
	//		try {
	//			pool = ConnectionPool.getConnectionPool();
	//			con = pool.getConnection();
	//			Statement statement = con.createStatement();
	////			logger.trace(("select name_txt from names where name_class='authority' and tax_id = " + id));
	//			ResultSet rs = statement.executeQuery("select name_txt from names where name_class='authority' and tax_id = " + id);
	//			if (rs.getFetchSize()>0){
	//				authors = new ArrayList<String>();
	////				logger.trace("rs!=null  " + rs.getFetchSize());
	//				String author;
	//				while(rs.next()){
	//					author = rs.getString(1);
	//					//				logger.trace("Authority: " + author + " - SCNAME " + scName);
	//					if (author!= null){
	//
	//						int authorLenght = author.length();
	//						int scNameLenght = scName.length();
	//
	//						if (authorLenght > scNameLenght+3){
	//
	//							Boolean flag  = scName.equals(author.substring(0, scNameLenght));
	//
	//							if (flag) {
	////								logger.trace("Confronta: ***" + scName + "*** con ***" + author.substring(0, scNameLenght) +"***");
	//								String au = author.substring(scNameLenght+1, authorLenght);
	//								if ((au.startsWith("(")) || (Character.isUpperCase(au.charAt(0)))){
	////									logger.trace("author " + au + " substring " + au.charAt(0));
	//									//								logger.trace("ok");
	//									//								author = author.substring(scNameLenght+1, authorLenght);
	//									//							logger.trace("author " + author);
	//									authors.add(au);
	//								}
	//							}
	//						}
	//					}
	//				}
	//				rs.close();
	//			}
	//		} catch (SQLException sqlExcept){        	
	//			logger.error("sql Error", sqlExcept);
	//		}catch (Throwable e){
	//			logger.error("general Error", e);
	//		} finally {
	//			if ((pool!=null) && (con!=null)){
	//				pool.releaseConnection(con);
	//			}
	//		}
	//		return authors;
	//	}


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

			String query = "select date from updates where id= (select max(id) from updates)";    
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

	/**
	 * Convert long in date
	 */
	public static String nextUpdateDate(long input){  
		Date date = new Date(input);  
		Calendar cal = new GregorianCalendar();  
		cal.setTime(date);  
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return(dateFormat.format(date));  

	} 







	public static List<String> getCitation(String idPost) {
		String id = null;
		List<String> citation = new ArrayList<String>();
		try{
			id = Utils.getOriginalId(idPost);
		}
		catch (ArrayIndexOutOfBoundsException e){
			id = idPost;
			//			logger.error("id is already a original one", e);
		}

		ConnectionPool pool = null;
		Connection con = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select distinct(cit_id) from citation where tax_id = " + id );

			if (rs!=null){
				while(rs.next()) {	
					int cit_id = rs.getInt(1);
					//					logger.trace("select cit_key, url, text from citations where cit_id = " + cit_id );
					String cit = createDD(cit_id);
					if (cit!=null)
						citation.add(cit);
				}
				rs.close();
			}
		} catch (SQLException sqlExcept) {
			logger.error("sql Error",sqlExcept);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException",e);
		}finally {

			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		} 
		return citation;	
	}


	private static String createDD(int cit_id) {
		StringBuilder cit = new StringBuilder();
		ConnectionPool pool = null;
		Connection con = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select cit_key, url, text from citations where cit_id = " + cit_id );
			if (rs!=null){
				while(rs.next()) {
					cit.append(rs.getString(1));
					cit.append(", ");
					cit.append(rs.getString(2));
					cit.append(", ");
					cit.append(rs.getString(3));	
				}	
				rs.close();
			}
		} catch (SQLException sqlExcept) {
			logger.error("sql Error",sqlExcept);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException",e);
		}finally {

			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		} 
		return cit.toString();


	}

	
	
	
	public static ResultSet getListScientificNameID(String scientificName) {
		ResultSet rs = null;
		ConnectionPool pool = null;
		Connection con = null;
		try {

			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();
			rs = statement.executeQuery("select distinct(tax_id) from names where name_class = 'scientific name' and UPPER(name_txt) like UPPER('%" + scientificName + "%')");

		}
		catch (SQLException sqlExcept){
			logger.error("sql Error", sqlExcept);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		} finally {

			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return rs;	
	}



	public static List<CommonName> getCommonNames(String idPost) {

		List<CommonName> commonNames = null;

		String id = null;
		try{
			id = Utils.getOriginalId(idPost);
		}
		catch (ArrayIndexOutOfBoundsException e){
			id = idPost;
			//			logger.error("id is already a original one", e);
		}

		if (id!=null){
			commonNames = new ArrayList<CommonName> ();
			String language = "English";

			ConnectionPool pool = null;
			Connection con = null;
			try {
				pool = ConnectionPool.getConnectionPool();
				con = pool.getConnection();
				Statement statement = con.createStatement();
				ResultSet rs = statement.executeQuery("select name_txt from names where name_class = 'common name' and tax_id = " + id);
				if (rs!=null){

					while(rs.next()) {	
						String name = rs.getString(1);
						CommonName a = new CommonName(language,name);
						commonNames.add(a);
					}
					rs.close();
				}
			}
			catch (SQLException sqlExcept) {
				logger.error("sql Error", sqlExcept);
			}catch (Throwable e) {
				logger.error("general Error", e);

			}finally {
				if ((pool!=null) && (con!=null)){
					pool.releaseConnection(con);
				}						
			}
		}
		return commonNames;
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
		cit.append(NcbiPlugin.citation);
		cit.append(createDate());
		return cit.toString();
	}

	public static String createCredits() {
		String cred = NcbiPlugin.credits;
		cred.replace("XDATEX",createDate());	
		return cred;
	}


}

////Create NCBI db
//class CreateDBThread extends Thread {
//
//	static GCUBELog logger = new GCUBELog(CreateDBThread.class);
//
//	CreateDBThread() {
//		super("Thread");
//		start(); // Start the thread
//	}
//
//	// This is the entry point for the child threads
//	public void run() {
//		try {
//			Utils util = new Utils();
//			util.createDB();
//		} catch (IOException e) {
//			logger.error("IO Error", e);
//		} catch (SQLException e) {
//			logger.error("sql Error", e);
//		}
//	} 
//}

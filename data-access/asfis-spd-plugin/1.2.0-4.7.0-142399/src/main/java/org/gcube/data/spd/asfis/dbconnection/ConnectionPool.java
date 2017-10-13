package org.gcube.data.spd.asfis.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.gcube.data.spd.asfis.AsfisPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */
public class ConnectionPool {
	private static Logger log = LoggerFactory.getLogger(ConnectionPool.class);
	public static String dbJdbc;           
	private static String dbDriver;        
	private static String dbLogin;         
	private static String dbPassword;    
	private static ConnectionPool connectionPool = null;  

	private Vector<Connection> freeConnections;      

	private ConnectionPool() throws ConnectionPoolException {		
		loadParameters();          
		freeConnections = new Vector<Connection>();  	
		loadDriver();    
	}

	/**
	 * load parameters
	 **/
	private void loadParameters() {
		dbJdbc = AsfisPlugin.jdbc;
		dbLogin = AsfisPlugin.username;            
		dbPassword = AsfisPlugin.password;
		dbDriver = AsfisPlugin.driver;
	}


	/**
	 * load dbDriver
	 **/ 
	private void loadDriver() throws ConnectionPoolException {
		try {
			Class.forName(dbDriver).newInstance(); 
		}
		catch (Exception e) {
			throw new ConnectionPoolException();
		}
	}


	/**
	 * get connection pool
	 **/ 
	public static synchronized ConnectionPool getConnectionPool() throws ConnectionPoolException {
		if(connectionPool == null) {
			connectionPool = new ConnectionPool();
		}
		return connectionPool;
	}

	/**
	 * get a free connection or create a new one
	 * @throws SQLException 
	 **/ 
	public synchronized Connection getConnection() throws ConnectionPoolException, SQLException {
		Connection con = null;
		if(freeConnections.size() > 0) {     
			con = (Connection)freeConnections.firstElement();  
			freeConnections.removeElementAt(0);
			try {
				if(con.isClosed()) {    
					con = getConnection();
				}
			}
			catch(SQLException e) {           
				con = getConnection();  
			}
		}
		else if (freeConnections.size() < 100){  
			con = newConnection(); 
		}	
		else {

		}
		return con;                          
	} 

	/**
	 * Create a new connection
	 **/ 
	private Connection newConnection() throws ConnectionPoolException {
		Connection con = null;
		try {
			con = DriverManager.getConnection(dbJdbc, dbLogin, dbPassword);

		}
		catch(SQLException e) {  
			//			log.info("dbUrl: " + dbUrl + " - dbLogin: "+ dbLogin + " - dbPassword: "+ dbPassword);
			throw new ConnectionPoolException();      
		}
		return con;                                  
	}


	/**
	 * Release the connection and put it in the queue
	 **/
	public synchronized void releaseConnection(Connection con) {
		freeConnections.add(con);  
	}


	/**
	 * Update
	 * @param stmtInsert 
	 */
	public boolean preStatement( String query, ArrayList<String> terms, PreparedStatement stmt){  

		//		PreparedStatement stmt = null;
		Connection con = null;
		try {  	
			con = getConnection();

			stmt = con.prepareStatement(query);
			for(int i = 0; i < terms.size(); i++){
				
				stmt.setString(i+1, terms.get(i));		
			}
//			System.out.println(stmt.toString());
			stmt.executeUpdate();		
			stmt.clearParameters();

		}  
		catch ( SQLException sqle ) {
			log.info(stmt.toString());
			log.error("Error executing: " + stmt);
			return false;  
		} catch (ConnectionPoolException e) {
			return false; 
		}finally{
			if (con!=null){
				releaseConnection(con);
			}
		}
		return true;  
	} 


	/* Insert
	 */
	public boolean insertPreStatement(String query){  

		PreparedStatement stmt = null;
		Connection con = null;
		try {  	
			con = getConnection();
			stmt = con.prepareStatement(query);
			stmt.executeUpdate();		
			return true;  
		}  
		catch ( SQLException sqle ) {  
			//			log.trace("Error executing: " + stmt);
			return false;  
		} catch (ConnectionPoolException e) {
			return false; 
		}finally{
			if (con!=null){
				releaseConnection(con);
			}
		}

	} 


	/**
	 * Select using preStatement (using the same term for every parameter)
	 */
	public ResultSet selectPrestatement( String query, String term){  

		PreparedStatement stmt = null;
		ResultSet result = null;
		Connection con = null;
		try {  	
			con = getConnection();
			stmt = con.prepareStatement(query);
			int paramCount = 0;
			ParameterMetaData paramMetaData = stmt.getParameterMetaData();
			
			if (paramMetaData != null) {
				paramCount = paramMetaData.getParameterCount();
			}

			//use the same term for every parameter
			if ((term!=null)& (paramCount>0)){
			
				for (int i=0; i < paramCount; i++){
					stmt.setString(i+1, term);
				}
			}
//		System.out.println(stmt.toString());
			result = stmt.executeQuery();
				
		}  
		catch ( SQLException e ) {  
//			System.out.println(e);
			log.error("SQLException", e);

		} catch (ConnectionPoolException e) {
			log.error("ConnectionPoolException", e);
		}finally{
			if (con!=null){
				releaseConnection(con);
			}
		}
		return result;
	} 
}



package org.gcube.data.spd.irmng.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.gcube.data.spd.irmng.IrmngPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPool {

	static Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

	private static ConnectionPool connectionPool = null;  

	private Vector<Connection> freeConnections; 
	private String dbUrl;           
	private String dbDriver;        
	private String dbLogin;         
	private String dbPassword;       

	private ConnectionPool() throws ConnectionPoolException {
		freeConnections = new Vector<Connection>();  	
		loadParameters();                
		loadDriver();    
	}

	/**
	 * load parameters
	 **/
	private void loadParameters() {
		dbUrl = IrmngPlugin.jdbc;
		dbDriver = IrmngPlugin.driver;
		dbLogin = IrmngPlugin.user;            
		dbPassword = IrmngPlugin.password;
	}


	/**
	 * load driver
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
			con = DriverManager.getConnection(dbUrl, dbLogin, dbPassword);
		}
		catch(SQLException e) {                      
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

		Connection con = null;
		try {  	
			con = getConnection();

			stmt = con.prepareStatement(query);
			for(int i = 0; i < terms.size(); i++){
				stmt.setString(i+1, terms.get(i));		
			}
//			logger.trace(stmt);
			stmt.executeUpdate();		
			stmt.clearParameters();

		}  
		catch ( SQLException sqle ) {  
			logger.trace("Error executing: " + stmt);
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
	public boolean insertPreStatement( String query){  

		PreparedStatement stmt = null;
		Connection con = null;
		try {  	
			con = getConnection();
			stmt = con.prepareStatement(query);
			stmt.executeUpdate();		
			return true;  
		}  
		catch ( SQLException sqle ) {  
			logger.trace("Error executing: " + stmt);
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
	 * Select using preStatement
	 */
	public ResultSet selectPrestatement( String query, String term){  
		PreparedStatement stmt = null;
		ResultSet result = null;
		Connection con = null;
		try {  	
			con = getConnection();
			stmt = con.prepareStatement(query);
			stmt.setString(1, term);						
			result = stmt.executeQuery();
//			logger.trace(stmt);
		}  
		catch ( SQLException e ) {  
			logger.error("SQLException", e);

		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally{
			if (con!=null){
				releaseConnection(con);
			}
		}
		return result;
	} 
}



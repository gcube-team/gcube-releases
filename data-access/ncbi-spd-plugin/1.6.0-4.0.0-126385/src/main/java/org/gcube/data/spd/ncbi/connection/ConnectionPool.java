package org.gcube.data.spd.ncbi.connection;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.ncbi.NcbiPlugin;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class ConnectionPool {

	static GCUBELog logger = new GCUBELog(ConnectionPool.class);

	private static ConnectionPool connectionPool = null;  

	private Vector<Connection> freeConnections; 
	public static String dbUrl;           
	private String dbDriver;        
	private static String dbLogin;         
	private static String dbPassword;       

	private ConnectionPool() throws ConnectionPoolException {
		freeConnections = new Vector<Connection>();  	
		loadParameters();                
		loadDriver();    

	}

	/**
	 * load parameters
	 **/
	private void loadParameters() {
		dbUrl = NcbiPlugin.jdbc;
		this.dbDriver = NcbiPlugin.dbDriver;
		dbLogin = NcbiPlugin.username;       
		dbPassword = NcbiPlugin.password;
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
	 **/ 
	public synchronized Connection getConnection() throws ConnectionPoolException {
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
		else {  
			con = newConnection(); 
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
	 * @param stmt2 
	 */
	public boolean preStatement( String query, ArrayList<String> terms, PreparedStatement stmt){  

		//		PreparedStatement stmt = null;
		ConnectionPool pool = null;
		Connection con = null;
		try {  	
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			stmt = con.prepareStatement(query);
			for(int i = 0; i < terms.size(); i++){
				stmt.setString(i+1, terms.get(i));				
			}
			stmt.executeUpdate();		
			return true;  
		}  
		catch ( SQLException sqle ) {  
			logger.trace("Error executing: " + stmt);
			return false;  
		} catch (ConnectionPoolException e) {
			return false; 
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}

	}



	/**
	 * Select using preStatement
	 */
	public ResultSet selectPrestatement( String query, ArrayList<String> terms, PreparedStatement stmt){  
		ResultSet result = null;
//		logger.trace(terms);
		//		PreparedStatement stmt = null;
		ConnectionPool pool = null;
		Connection con = null;
		try {  	
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			stmt = con.prepareStatement(query);
			for(int i = 0; i < terms.size(); i++){
				stmt.setString(i+1, terms.get(i));				
			}		
//						logger.trace(stmt);
			result = stmt.executeQuery();
		}  
		catch ( SQLException e ) {  
			logger.error("SQLException", e);

		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
			try {
				stmt.clearParameters();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}
		}
		return result;

	} 



	/**
	 *  Insert
	 */
	public static boolean insertPreStatement( String query){  

		PreparedStatement stmt = null;
		ConnectionPool pool = null;
		Connection con = null;
		try {  	
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
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
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
	} 

	/**
	 *  Copy csv to table
	 */
	public boolean copy(String query, String file) {

		CopyManager copyManager;

		Connection con = null;

		try {  	
			Class.forName(NcbiPlugin.dbDriver).newInstance(); 
			con = DriverManager.getConnection(NcbiPlugin.jdbc, NcbiPlugin.username, NcbiPlugin.password);					
			copyManager = new CopyManager((BaseConnection) con);			
			FileReader fileReader;
			fileReader = new FileReader(file);
			copyManager.copyIn(query, fileReader );

		} catch (SQLException e) {
			return false; 
		} catch (IOException e) {
			return false; 
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (con!=null)
				try {
					con.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
		return true;
	}



	

}




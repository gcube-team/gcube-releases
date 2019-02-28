//package org.gcube.data.spd.ncbi.connection;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import org.gcube.common.core.utils.logging.GCUBELog;
//import org.gcube.data.spd.ncbi.NcbiPlugin;
//import org.postgresql.copy.CopyManager;
//import org.postgresql.core.BaseConnection;
//
//import java.sql.Statement;
//import java.util.ArrayList;
//
//public class Database {
//
//	static GCUBELog logger = new GCUBELog(Database.class);
//	private Connection connection;  
//	private String user;  
//	private String password;  
//	private String jdbc;  
//	private Statement statement;  
//	private final String driver = "org.postgresql.Driver";
//
//	/**
//	 * Get db connection info
//	 */
//	public Database(){  
//		this.jdbc = NcbiPlugin.jdbc;  
//		this.user = NcbiPlugin.username;  
//		this.password = NcbiPlugin.password;  
//	}
//
//	/**
//	 * Create a db connection
//	 */
//	public boolean connect(){
//		try {
//			Class.forName(driver).newInstance(); 
//			connection = DriverManager.getConnection(jdbc, user, password);
//			return true;  
//		}
//
//		catch (Exception ex) {
//			logger.error("SQL Error", ex); 
//		}      
//		return false;  
//	}
//
//
//	/**
//	 * Execute a query
//	 */
//	public ResultSet get( String query ){  
//		try {  
//			statement = connection.createStatement();  
//			ResultSet rs = statement.executeQuery( query );  
//			return rs;  
//		}  
//		catch ( SQLException sqle ) {  
//			return null;  
//		}  
//	}  
//
//
//
//	/**
//	 * Update
//	 */
//	public boolean update( String query ){  
//		try {  
//			statement = connection.createStatement();  
//			statement.execute(query);
//			return true;
//		}  
//		catch ( SQLException sqle ) {  
//			return false;  
//		}
//
//	}  
//
//
//	/**
//	 * Update
//	 */
//	public boolean preStatement( String query, ArrayList<String> terms, PreparedStatement stmt){  
//
////		PreparedStatement stmt = null;
//		try {  	
//
//			stmt = connection.prepareStatement(query);
//
//			for(int i = 0; i < terms.size(); i++){
//				stmt.setString(i+1, terms.get(i));				
//			}
//			stmt.executeUpdate();		
//			logger.trace("ok " + stmt);
//			return true;  
//		}  
//		catch ( SQLException sqle ) {  
//			logger.trace(stmt);
//			return false;  
//		}
//
//	} 
//	/**
//	 * Close connection
//	 */
//	public boolean shutDown(){  
//		try {  
//			if (connection != null)
//				connection.close();  
//			return true;  
//		}  
//		catch ( SQLException sqlex ) {  
//			return false;  
//		}  
//	}
//
//	public boolean copy(String query, String file) {
//
//		CopyManager copyManager;
//		try {
//			copyManager = new CopyManager((BaseConnection) connection);			
//			FileReader fileReader;
//			fileReader = new FileReader(file);
//			copyManager.copyIn(query, fileReader );
//			return true;
//		} catch (SQLException e) {
//			return false; 
//		} catch (IOException e) {
//			return false; 
//		}
//	}  
//
//}

package org.gcube.data.spd.itis.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.itis.ItisPlugin;

import java.sql.Statement;

public class Database {

	static GCUBELog logger = new GCUBELog(Database.class);
	private Connection connection;  
	private String user;  
	private String password;  
	private String jdbc;  
	private Statement statement;  
	private final String driver = "com.mysql.jdbc.Driver";

	/**
	 * Get db connection info
	 */
	public Database()  
	{  
		this.jdbc = ItisPlugin.jdbc;  
		this.user = ItisPlugin.user;  
		this.password = ItisPlugin.password;  
	}

	/**
	 * Create a db connection
	 */
	public boolean connect()
	{
		try {
			Class.forName(driver).newInstance(); 
			connection = DriverManager.getConnection(jdbc, user, password);
			return true;  
		}

		catch (Exception ex) {
			logger.error("SQL Error", ex);
		}      
		return false;  
	}


	/**
	 * Execute a query
	 */
	public ResultSet get( String query )  
	{  
		try {  
			statement = connection.createStatement();  
			ResultSet rs = statement.executeQuery( query );  
			return rs;  
		}  
		catch ( SQLException sqle ) {  
			return null;  
		}  
	}  

	/**
	 * Update
	 */
	public boolean update( String query )  
	{  
		try {  
			statement = connection.createStatement();  
			statement.executeUpdate( query );  
			return true;  
		}  
		catch ( SQLException sqle ) {  
			return false;  
		}  
	}  

	/**
	 * Close connection
	 */
	public boolean shutDown()  
	{  
		try {  
			if (connection != null)
				connection.close();  
			return true;  
		}  
		catch ( SQLException sqlex ) {  
			return false;  
		}  
	}  


}

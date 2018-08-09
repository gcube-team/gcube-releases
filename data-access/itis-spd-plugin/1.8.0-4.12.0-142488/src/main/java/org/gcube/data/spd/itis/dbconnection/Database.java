package org.gcube.data.spd.itis.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gcube.data.spd.itis.ItisPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {

	static Logger logger = LoggerFactory.getLogger(Database.class);
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

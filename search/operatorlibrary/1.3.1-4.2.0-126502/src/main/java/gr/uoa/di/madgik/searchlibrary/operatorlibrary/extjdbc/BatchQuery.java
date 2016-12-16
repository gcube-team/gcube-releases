package gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to perform queries on the sql driver
 * 
 * @author UoA
 */
public class BatchQuery {
	/**
	 * The Logger the class uses
	 */
	private static Logger logger = LoggerFactory.getLogger(BatchQuery.class.getName());
	/**
	 * The name of the driver to use
	 */
	private String driverName;
	/**
	 * The connection String to use
	 */
	private String connectionString;
	/**
	 * The connection
	 */
	private Connection conn;
	/**
	 * The connection prepared statement
	 */
	private PreparedStatement stmt;
	/**
	 * The result set
	 */
	private int counter = 0;
	private int batchSize;
	
	/**
	 * Creates a new {@link BatchQuery}
	 * 
	 * @param driverName The name of the driver
	 * @param connectionString The connection string
	 * @param sql The SQL query for prepared statement
	 * @param recordsPerPart The number of records to put in each part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public BatchQuery(String driverName, String connectionString, String sql, int recordsPerPart) throws Exception
	{
		this.driverName = driverName;
		this.connectionString = connectionString;
		this.conn = null;
		this.stmt = null;
		this.batchSize = recordsPerPart;
		try{
			// Load the appropriate driver
			Class.forName(this.driverName).newInstance();
			// Connect to database
			this.conn = DriverManager.getConnection(this.connectionString);
			
			// create new prepared statement
			this.stmt = this.conn.prepareStatement(sql);

			// set forward fetch direction and stream granularity
			if(this.conn == null){
				logger.error("QueryBridge constructor: The connection object is null. Throwing Exception");
				throw new Exception("QueryBridge constructor: The connection object is null");
			}
			if(this.stmt == null){
				logger.error("QueryBridge constructor: The prepared statement object is null. Throwing Exception");
				throw new Exception("QueryBridge constructor: The prepared statement object is null");
			}
		}catch(SQLException e){
			logger.error("SQLException thrown in QueryBridge constructor. Throwing Exception",e);
			throw new Exception("SQLException thrown in QueryBridge constructor");
		}catch(Exception e){
			logger.error("Exception thrown in QueryBridge constructor. Throwing Exception",e);
			throw new Exception("Exception thrown in QueryBridge constructor");
		}
	}
	
	/**
	 * Executes the provided query
	 * @param fields the updated fields
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void addBatch(String... fields) throws Exception
	{
		try{
			for(int i = 0; i < fields.length; i++) {
				stmt.setString(i + 1, fields[i]);
			}
			stmt.addBatch();
			
		    if(++counter % batchSize == 0) {
		    	stmt.executeBatch();
		    }
		}catch(SQLException e){
			logger.error("Could not execute Query. Throwing Exception",e);
			logger.error("next: " + e.getNextException());
			throw new SQLException("Could not execute Query", e);
		}
	}
	
	public void executeBatch() throws SQLException {
		stmt.executeBatch();
	}

}

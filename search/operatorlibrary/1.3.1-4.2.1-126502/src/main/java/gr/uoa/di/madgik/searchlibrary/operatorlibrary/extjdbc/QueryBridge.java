package gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to perform queries on the sql driver
 * 
 * @author UoA
 */
public class QueryBridge {
	/**
	 * The Logger the class uses
	 */
	private static Logger logger = LoggerFactory.getLogger(QueryBridge.class.getName());
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
	 * The connection statement
	 */
	private Statement stmt;
	/**
	 * The result set
	 */
	private ResultSet rs;
	
	/**
	 * Creates a new {@link QueryBridge}
	 * 
	 * @param driverName The name of the driver
	 * @param connectionString The connection string
	 * @param recordsPerPart The number of records to put in each part
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public QueryBridge(String driverName, String connectionString,int recordsPerPart) throws Exception
	{
		this.driverName = driverName;
		this.connectionString = connectionString;
		this.conn = null;
		this.stmt = null;
		this.rs = null;
		try{
			// Load the appropriate driver
			Class.forName(this.driverName).newInstance();
			// Connect to database
			this.conn = DriverManager.getConnection(this.connectionString);
			
			// create new statement with the properties ResultSet.TYPE_SCROLL_INSENSITIVE,
			// ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT
			this.conn.setAutoCommit(false);
			this.stmt = this.conn.createStatement();

			// set forward fetch direction and stream granularity
			this.stmt.setFetchDirection(JDBCConnectorProfile.FetchDirection);
			this.stmt.setFetchSize(recordsPerPart);
			if(this.conn == null){
				logger.error("QueryBridge constructor: The connection object is null. Throwing Exception");
				throw new Exception("QueryBridge constructor: The connection object is null");
			}
			if(this.stmt == null){
				logger.error("QueryBridge constructor: The statement object is null. Throwing Exception");
				throw new Exception("QueryBridge constructor: The statement object is null");
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
	 * Exevutes the provided query
	 * 
	 * @param query The query to execute
	 * @return The produced result set
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public ResultSet executeQuery(String query) throws Exception
	{
		try{
			this.stmt.setFetchSize(10);
			boolean isSelect = this.stmt.execute(query);
			if(isSelect == false)
				return null;
			this.rs = stmt.getResultSet();
			return this.rs;
		}catch(Exception e){
			logger.error("Could not execute Query. Throwing Exception",e);
			throw new Exception("Could not execute Query");
		}
	}
	
	/**
	 * Retriueves the names of the available collumns
	 * 
	 * @return The column names
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String[] getColumnNames() throws Exception
	{
		try{
			ResultSetMetaData rsm = this.rs.getMetaData();
			int columncnt = rsm.getColumnCount();
			String[] columnNames = new String[columncnt];
			for(int i=0;i<columncnt;i++)
				columnNames[i] = rsm.getColumnName(i+1);
			return columnNames;
		}catch(Exception e){
			logger.error("Could not get column names. Throwing Exception",e);
			throw new Exception("Could not get column names");
		}
	}
	
	/**
	 * Prints the results in System.err
	 */
	public void printResults()
	{
		if(this.rs == null)
		{
			System.err.println("The result set is null");
			return;
		}
		
	}
}

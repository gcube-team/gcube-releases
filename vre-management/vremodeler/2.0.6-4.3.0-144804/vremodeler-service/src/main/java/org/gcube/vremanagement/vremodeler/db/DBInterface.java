package org.gcube.vremanagement.vremodeler.db;


import java.io.File;
import java.sql.SQLException;
import java.util.Hashtable;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

/**
 * 
 * @author lucio
 *
 */
/**
 * @author lucio
 *
 */
public class DBInterface {
	
	private static Hashtable< String, ConnectionSource> connectionMapping= new Hashtable<String, ConnectionSource>();
	
	private static String dbFileRoot = ServiceContext.getContext().getPersistenceRoot()+ File.separator + "vreModelerDB"; 
	
	private static GCUBELog logger = new GCUBELog(DBInterface.class);
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	
	public static synchronized boolean dbAlreadyCreated(String scope){
		try{
			String dbFile = dbFileRoot+scope.toString().replace("/", "-");
			logger.debug("dbfile to connect is "+dbFile);
			
			new JdbcConnectionSource("jdbc:hsqldb:file:"
							+ dbFile+";ifexists=true", "sa","");
		}catch (Exception e) {
			logger.trace("the database does't exists");
			return false;
		}
		return true;
	}
	
	public static synchronized ConnectionSource connect(String scope) throws SQLException{
		
		if(connectionMapping.get(scope)==null)
		{
			// Load the HSQL Database Engine JDBC driver
	        // hsqldb.jar should be in the class path or made part of the current jar
	        
			try{
	        	 Class.forName("org.hsqldb.jdbcDriver");
	        }catch(ClassNotFoundException e){throw new SQLException(e.getMessage());}	
	        // connect to the database.   This will load the db files and start the
	        // database if it is not alread running.
	        // db_file_name_prefix is used to open or create files that hold the state
	        // of the db.
	        // It can contain directory names relative to the
	        // current working directory
	        	 
			
			String dbFile = dbFileRoot+scope.toString().replace("/", "-");
			
			
	        ConnectionSource connectionSource =
	        		new JdbcConnectionSource("jdbc:hsqldb:file:"
                            +dbFile , "sa","");
   		
	        		
	        
	        connectionMapping.put(scope, connectionSource);
	        
		}
		return connectionMapping.get(scope);
		
	}
	
	public static ConnectionSource connect() throws SQLException{
		return connect(ScopeProvider.instance.get());
	}

}

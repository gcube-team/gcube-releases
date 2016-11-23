package org.gcube.vremanagement.vremodeler.db;


import java.io.File;
import java.sql.SQLException;
import java.util.Hashtable;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
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
	
	private static String dbFile = ServiceContext.getContext().getPersistenceRoot()+ File.separator + "vreModelerDB"; 
	
	private static GCUBELog logger = new GCUBELog(DBInterface.class);
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	
	public static synchronized boolean dbAlreadyCreated(GCUBEScope scope){
		try{
			new JdbcConnectionSource("jdbc:hsqldb:file:"
							+ dbFile+scope.toString().replace("/", "-")+";ifexists=true", "sa","");
		}catch (Exception e) {
			logger.trace("the database does't exists");
			return false;
		}
		return true;
	}
	
	public static synchronized ConnectionSource connect(GCUBEScope scope) throws SQLException{
		
		if(connectionMapping.get(scope.toString())==null)
		{
			// Load the HSQL Database Engine JDBC driver
	        // hsqldb.jar should be in the class path or made part of the current jar
	        
			logger.info(dbFile);
			try{
	        	 Class.forName("org.hsqldb.jdbcDriver");
	        }catch(ClassNotFoundException e){throw new SQLException(e.getMessage());}	
	        // connect to the database.   This will load the db files and start the
	        // database if it is not alread running.
	        // db_file_name_prefix is used to open or create files that hold the state
	        // of the db.
	        // It can contain directory names relative to the
	        // current working directory
	        	 
			
			
	        ConnectionSource connectionSource =
	        		new JdbcConnectionSource("jdbc:hsqldb:file:"
                            + dbFile+scope.toString().replace("/", "-"), "sa","");
   		
	        		
	        
	        connectionMapping.put(scope.toString(), connectionSource);
	        
		}
		return connectionMapping.get(scope.toString());
		
	}
	
	public static ConnectionSource connect() throws SQLException{
		return connect(ServiceContext.getContext().getScope());
	}

}

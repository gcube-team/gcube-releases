package org.gcube.application.perform.service.engine.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.gcube.application.perform.service.LocalConfiguration;
import org.gcube.application.perform.service.engine.DataBaseManager;
import org.gcube.application.perform.service.engine.model.DatabaseConnectionDescriptor;
import org.gcube.application.perform.service.engine.model.ISQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.utils.ISUtils;
import org.gcube.application.perform.service.engine.utils.ScopeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataBaseManagerImpl implements DataBaseManager{

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("PERFORM SERVICE - UNABLE TO REGISTER postgresql DRIVER");
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}
	
	
	private static final Logger log= LoggerFactory.getLogger(DataBaseManagerImpl.class);
	
	// **************************************   INSTANCE 
	
	
	// Endpoint -> datasource
	private static ConcurrentHashMap<String,DataSource> datasources=new ConcurrentHashMap<>();
	
	// Scope -> db
	private static ConcurrentHashMap<String,DatabaseConnectionDescriptor> databases=new ConcurrentHashMap<>();
	
	
	
	/**
	 * Manages db connection pools by scope
	 * 
	 * 
	 */
	
	
	private synchronized DatabaseConnectionDescriptor getDB() throws InternalException {
		if(!databases.containsKey(ScopeUtils.getCurrentScope()))
			databases.put(ScopeUtils.getCurrentScope(), ISUtils.queryForDatabase(new ISQueryDescriptor( 
					LocalConfiguration.getProperty(LocalConfiguration.MAPPING_DB_ENDPOINT_NAME), null,
					LocalConfiguration.getProperty(LocalConfiguration.MAPPING_DB_ENDPOINT_CATEGORY))));
		
		return databases.get(ScopeUtils.getCurrentScope());
	}
	
	@Override
	public Connection getConnection() throws SQLException, InternalException {
		DataSource ds=getDataSource();		
		Connection conn=ds.getConnection();
		conn.setAutoCommit(false);		
		return conn;
	}
	
	
	private synchronized DataSource getDataSource() throws InternalException {		
		DatabaseConnectionDescriptor dbDescriptor=getDB();
		
		if(!datasources.containsKey(dbDescriptor.getUrl())) {			
			datasources.put(dbDescriptor.getUrl(), setupDataSource(dbDescriptor));
		}
		return datasources.get(dbDescriptor.getUrl());
	}
	
	  private static DataSource setupDataSource(DatabaseConnectionDescriptor db) {
		  
		  log.trace("Setting up data source for {} ",db);
		  
		  
		  GenericObjectPoolConfig poolConfig=new GenericObjectPoolConfig();
		  poolConfig.setMaxIdle(Integer.parseInt(LocalConfiguration.getProperty(LocalConfiguration.POOL_MAX_IDLE)));
		  poolConfig.setMaxTotal(Integer.parseInt(LocalConfiguration.getProperty(LocalConfiguration.POOL_MAX_TOTAL)));
		  poolConfig.setMinIdle(Integer.parseInt(LocalConfiguration.getProperty(LocalConfiguration.POOL_MIN_IDLE)));
		  
		  
	   ConnectionFactory connectionFactory =
	            new DriverManagerConnectionFactory(db.getUrl(),db.getUsername(),db.getPassword());

	   
	        PoolableConnectionFactory poolableConnectionFactory =
	            new PoolableConnectionFactory(connectionFactory, null);

	   
	        ObjectPool<PoolableConnection> connectionPool =
	                new GenericObjectPool<>(poolableConnectionFactory);
	        
	       
	        poolableConnectionFactory.setPool(connectionPool);

	       
	        PoolingDataSource<PoolableConnection> dataSource =
	                new PoolingDataSource<>(connectionPool);

	        return dataSource;
	    } 
	
}

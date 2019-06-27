package org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;
import org.gcube.data.publishing.gCatFeeder.service.engine.ConnectionManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.engine.LocalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManagerImpl implements ConnectionManager {

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("PERFORM SERVICE - UNABLE TO REGISTER postgresql DRIVER");
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}
	
	
	private static final Logger log= LoggerFactory.getLogger(ConnectionManagerImpl.class);
	
	// **************************************   INSTANCE 
	
	
	// Endpoint -> datasource
	private ConcurrentHashMap<String,DataSource> datasources=new ConcurrentHashMap<>();
	
	// Scope -> db
	private ConcurrentHashMap<String,DatabaseConnectionDescriptor> databases=new ConcurrentHashMap<>();
	
	
	
	/**
	 * Manages db connection pools by scope
	 * 
	 * 
	 */
	
	@Inject
	private Infrastructure infrastructure;
	@Inject
	private LocalConfiguration configuration;
	
	private synchronized DatabaseConnectionDescriptor getDB() throws InternalError {		
		if(!databases.containsKey(infrastructure.getCurrentContext()))
			databases.put(infrastructure.getCurrentContext(), infrastructure.queryForDatabase(
					configuration.getProperty(LocalConfiguration.DB_ENDPOINT_CATEGORY),
					configuration.getProperty(LocalConfiguration.DB_ENDPOINT_NAME)));
		return databases.get(infrastructure.getCurrentContext());
	}
	
	@Override
	public Connection getConnection() throws SQLException, InternalError {
		log.debug("Getting DB connection in {} ",infrastructure.getCurrentContext());
		DataSource ds=getDataSource();		
		Connection conn=ds.getConnection();
		conn.setAutoCommit(false);		
		return conn;
	}
	
	
	private synchronized DataSource getDataSource() throws InternalError, SQLException {		
		DatabaseConnectionDescriptor dbDescriptor=getDB();
		
		if(!datasources.containsKey(dbDescriptor.getUrl())) {			
			datasources.put(dbDescriptor.getUrl(), setupDataSource(dbDescriptor));
		}
		return datasources.get(dbDescriptor.getUrl());
	}
	
	  private DataSource setupDataSource(DatabaseConnectionDescriptor db) throws SQLException, InternalError {
		  
		  log.trace("Setting up data source for {} ",db);
		  
		  
		  GenericObjectPoolConfig poolConfig=new GenericObjectPoolConfig();
		  poolConfig.setMaxIdle(Integer.parseInt(configuration.getProperty(LocalConfiguration.POOL_MAX_IDLE)));
		  poolConfig.setMaxTotal(Integer.parseInt(configuration.getProperty(LocalConfiguration.POOL_MAX_TOTAL)));
		  poolConfig.setMinIdle(Integer.parseInt(configuration.getProperty(LocalConfiguration.POOL_MIN_IDLE)));
		  
		  
	   ConnectionFactory connectionFactory =
	            new DriverManagerConnectionFactory(db.getUrl(),db.getUsername(),db.getPassword());

	   
	        PoolableConnectionFactory poolableConnectionFactory =
	            new PoolableConnectionFactory(connectionFactory, null);

	   
	        ObjectPool<PoolableConnection> connectionPool =
	                new GenericObjectPool<>(poolableConnectionFactory);
	        
	       
	        poolableConnectionFactory.setPool(connectionPool);

	       
	        PoolingDataSource<PoolableConnection> dataSource =
	                new PoolingDataSource<>(connectionPool);

	        log.trace("Initializing schema...");
	        
	        Connection conn=null;
	        try{
	        	conn=dataSource.getConnection();
	        	conn.setAutoCommit(true);
	        	conn.createStatement().executeUpdate(Queries.getInitDB(db.getFlavor()));	        	
	        }catch(SQLException e) {
	        	throw new InternalError("Unable to Init database "+db,e);
	        }finally {
	        	if(conn!=null) conn.close();
	        }
	        
	        
	        
	        return dataSource;
	    } 

}

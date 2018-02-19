/**
 * 
 */
package org.gcube.common.dbinterface.pool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.common.dbinterface.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Lucio Lelii
 *
 */
public class DBSession {
	
	@SuppressWarnings("rawtypes")
	private static Hashtable<Class, Class> queryMapping;
	protected static GenericObjectPool<DBSession> connectionPool;
	public static String drivers;
	public static String entireUrl;
	private static final int DEFAULT_MAX_CONN=20;
	
	public static void initialize(String basePackage,String username, String passwd,String dbPath) throws Exception{
		initialize(basePackage, username, passwd, dbPath, DEFAULT_MAX_CONN);
	}

	
	@SuppressWarnings("rawtypes")
	public static void initialize(String basePackage,String username, String passwd,String dbPath, int maxActiveConnections) throws Exception{
		DBSession.queryMapping= new Hashtable<Class, Class>();
		Initializer initializer;
		logger.trace(basePackage+".InitializerImpl");
		try{
			initializer=(Initializer) Class.forName(basePackage+".InitializerImpl").newInstance();
		}catch(Exception e){
			logger.error("impossible to retrieve initializer",e);
			throw e;
		}
		
		logger.trace("retrieving query mapping");
		Properties mappingProperties=initializer.getQueryMappingPropertiesStream();
		for (Entry<Object,Object> entry: mappingProperties.entrySet())
			DBSession.queryMapping.put(Class.forName((String)entry.getKey()),Class.forName((String)entry.getValue()));
		
		initializer.initialize(username, passwd, dbPath);
		
		
		logger.debug(initializer.getEntireUrl());
		
		
		connectionPool = new GenericObjectPool<DBSession>(new ObjectPoolFactory(initializer.getDriver(), initializer.getEntireUrl(), username, passwd));
		connectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		connectionPool.setMaxActive(maxActiveConnections);
		drivers= initializer.getDriver();
		entireUrl= initializer.getEntireUrl();
		DBSession session= DBSession.connect();
		initializer.postInitialization(session);
		session.release();
	}
	
	public static DBSession connect() throws Exception{
		return connectionPool.borrowObject();	
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DBSession.class);
	
	private Connection connection;
	
	protected DBSession(Connection conn){
		this.connection= conn;
	}
	
	public void release() {
		try {
			this.connection.setAutoCommit(true);
			connectionPool.returnObject(this);
		} catch (Exception e) {
			logger.warn("error in DBSessionPool");
		}
		
	}
	
	protected void closeConnection() throws Exception{
		this.connection.close();
	}
	
	public boolean isConnectionClosed() throws Exception{
		return this.connection.isClosed();
	}
	
	public void disableAutoCommit() throws Exception{
		this.connection.setAutoCommit(false);
	}
	
	public void commit() throws Exception{
		this.connection.commit();
	}
	
	public void rollback() throws Exception{
		this.connection.rollback();
	}
	
	public ResultSet execute(String query, boolean resultSetReuse) throws SQLException{
		logger.trace("executing query: "+query);
		Statement statement=null;
		if (resultSetReuse)
			statement= connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		else statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
		return statement.executeQuery(query);
	}
		
	public int executeUpdate(String query) throws SQLException{
		Statement statement = connection.createStatement();
		return statement.executeUpdate(query);
	}
	
	public PreparedStatement getPreparedStatement(String sql) throws SQLException{
		return this.connection.prepareStatement(sql);
	}
	
	@SuppressWarnings("unchecked")
	public static <QUERY> QUERY getImplementation(Class<QUERY> type) throws Exception{
		return (QUERY) DBSession.queryMapping.get(type).newInstance();
	}

	public DatabaseMetaData getDBMetadata() throws Exception{
		return this.connection.getMetaData();
	}
	
	public Connection getConnection(){
		return this.connection;
	}
}

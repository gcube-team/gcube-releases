package org.gcube.data.analysis.statisticalmanager.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.exception.ISException;
import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataBaseManager {
	
	private static Logger logger = LoggerFactory.getLogger(DataBaseManager.class);
	public static final String DRIVER="org.postgresql.Driver";
	
	static {		
		try{
			Class.forName(DRIVER).newInstance();
		}catch(Throwable t){
			logger.error("Unable to initialize db driver "+DRIVER,t);
		}
	}
	
	
	private static final ConcurrentHashMap<String, DataBaseManager> dbMap=new ConcurrentHashMap<>();
	
	
	private String url;
	private String username;
	private String password;

	private GenericObjectPool connectionPool;
	private DataSource dataSource;
		
	
	public static synchronized DataBaseManager get() throws ISException {
		String currentScope=ScopeProvider.instance.get();
		logger.trace("Getting DB under scope "+currentScope);
		if(!dbMap.contains(currentScope)) {
			AccessPointDescriptor desc=RuntimeResourceManager.getDatabaseProfile(DatabaseType.OPERATIONAL);
			logger.debug("Initiazlizing with "+desc+" under scope "+currentScope);
			dbMap.put(currentScope, new DataBaseManager(desc));
		}
		return dbMap.get(currentScope);
	}
	
	private DataBaseManager(AccessPointDescriptor desc) {
		this.url = desc.getUrl();
		this.username = desc.getUsername();
		this.password = desc.getPassword();
		
        //
        // Creates an instance of GenericObjectPool that holds our
        // pool of connections object.
        //
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(10);
 
        //
        // Creates a connection factory object which will be use by
        // the pool to create the connection object. We passes the
        // JDBC url info, username and password.
        //
        ConnectionFactory cf = new DriverManagerConnectionFactory(url,
        		username, password);

        //
        // Creates a PoolableConnectionFactory that will wraps the
        // connection object created by the ConnectionFactory to add
        // object pooling functionality.
        //
        PoolableConnectionFactory pcf =
                new PoolableConnectionFactory(cf, connectionPool,
                        null, null, false, true);
        
        dataSource = new PoolingDataSource(pcf.getPool());
	}
	
	
	
	public DataSource getDataSource() throws Exception {
		if (dataSource == null)
			throw new StatisticalManagerException("Data source not initialized ");
		
		return dataSource;
	}
	
	public String getUrlDB() {
		return url;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getDriver() {
		return DRIVER;
	}
	
	public void closeConn(Connection conn) {		
		try {if(conn!=null) conn.close(); } catch (SQLException e) {}
	}
	
	public void rollback(Connection conn) {
		try {if(conn!=null) conn.rollback();} catch (SQLException e) {}
	}
	
	public void closeStatement(Statement stm) {
		try {if(stm!=null) stm.close(); } catch (SQLException e) {}
	}

	public void removeTable(String tableId) throws Exception {
		
		Connection conn = getDataSource().getConnection();
		Statement stmt = null; 
		try {
			String sql = "DROP TABLE " + tableId;
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} finally {
			
			closeStatement(stmt);
			closeConn(conn);
		}
	}	
	
}

package org.gcube.data.analysis.tabulardata.cube.data.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.gcube.common.database.DatabaseEndpointIdentifier;
import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.postgresql.PGConnection;

public class Tomcat7DatabaseConnectionProvider implements DatabaseConnectionProvider {
	
	private final static String DRIVER = "org.postgresql.Driver";
	
	private DataSource dataSource = null;
	
	private DatabaseProvider databaseProvider;
	
	private DatabaseEndpointIdentifier endpointIdentifier;
	
	boolean initialized = false;
	
	public Tomcat7DatabaseConnectionProvider(DatabaseProvider databaseProvider, DatabaseEndpointIdentifier endpointIdentifier){
		this.databaseProvider = databaseProvider;
		this.endpointIdentifier = endpointIdentifier;
	}
	
	private void initializeDatasource(){
		PoolProperties poolConfiguration = buildPoolConfiguration();
        dataSource = new DataSource();
        dataSource.setPoolProperties(poolConfiguration);
        initialized = true;
	}
	
	private PoolProperties buildPoolConfiguration(){
		PoolProperties poolProperties = new PoolProperties();
        poolProperties.setUrl(getConnectionString());
        poolProperties.setDriverClassName(DRIVER);
        poolProperties.setUsername(getDBUsername());
        poolProperties.setPassword(getDBPassword());
        return poolProperties;
	}

	@Override
	public Connection getConnection() throws SQLException {
		initializeDatasourceIfNotInitialized();
		return dataSource.getConnection();
	}

	private void initializeDatasourceIfNotInitialized() {
		if (!initialized) initializeDatasource();
	}
	
	@Override
	public PGConnection getPostgreSQLConnection() throws SQLException {
		initializeDatasourceIfNotInitialized();
		return dataSource.getConnection().unwrap(PGConnection.class);
	}
	
	@Override
	public DatabaseEndpoint getDatabaseEndpoint(){
		return databaseProvider.get(endpointIdentifier);
	}
	
	private String getConnectionString(){
		return getDatabaseEndpoint().getConnectionString();
	}
	
	private String getDBUsername(){
		return getDatabaseEndpoint().getCredentials().getUsername();
	}
	
	private String getDBPassword(){
		return getDatabaseEndpoint().getCredentials().getPassword();
	}


}

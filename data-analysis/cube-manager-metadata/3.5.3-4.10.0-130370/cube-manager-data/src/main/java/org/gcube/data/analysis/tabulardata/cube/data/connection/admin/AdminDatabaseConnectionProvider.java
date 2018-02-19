package org.gcube.data.analysis.tabulardata.cube.data.connection.admin;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.Tomcat7DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.config.DatabaseEndpointIdentifierConfiguration;
import org.postgresql.PGConnection;

@Admin
@Singleton
public class AdminDatabaseConnectionProvider implements DatabaseConnectionProvider {

	DatabaseConnectionProvider delegate;

	DatabaseEndpointIdentifierConfiguration databaseEndpointIdentifierProvider = new DatabaseEndpointIdentifierConfiguration();

	@Inject
	public AdminDatabaseConnectionProvider(DatabaseProvider databaseProvider) {
		this.delegate = new Tomcat7DatabaseConnectionProvider(databaseProvider, databaseEndpointIdentifierProvider.getDataAdmin() );
	}

	@Override
	public Connection getConnection() throws SQLException {
		return delegate.getConnection();
	}

	@Override
	public PGConnection getPostgreSQLConnection() throws SQLException {
		return delegate.getPostgreSQLConnection();
	}

	@Override
	public DatabaseEndpoint getDatabaseEndpoint() {
		return delegate.getDatabaseEndpoint();
	}

}

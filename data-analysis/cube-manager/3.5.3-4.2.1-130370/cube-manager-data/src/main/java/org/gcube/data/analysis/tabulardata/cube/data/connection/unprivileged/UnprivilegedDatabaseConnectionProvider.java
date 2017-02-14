package org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged;

import java.sql.Connection;
import java.sql.SQLException;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.Tomcat7DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.config.DatabaseEndpointIdentifierConfiguration;
import org.postgresql.PGConnection;

@Unprivileged
@Default
@Singleton
public class UnprivilegedDatabaseConnectionProvider implements DatabaseConnectionProvider {

	DatabaseConnectionProvider delegate;

	DatabaseEndpointIdentifierConfiguration databaseEndpointIdentifierProvider = new DatabaseEndpointIdentifierConfiguration();

	@Inject
	public UnprivilegedDatabaseConnectionProvider(DatabaseProvider databaseProvider) {
		this.delegate = new Tomcat7DatabaseConnectionProvider(databaseProvider, databaseEndpointIdentifierProvider.getDataUser());
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

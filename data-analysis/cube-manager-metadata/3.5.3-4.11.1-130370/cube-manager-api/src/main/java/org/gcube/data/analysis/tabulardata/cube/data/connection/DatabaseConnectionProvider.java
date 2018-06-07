package org.gcube.data.analysis.tabulardata.cube.data.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.postgresql.PGConnection;

public interface DatabaseConnectionProvider {

	public abstract Connection getConnection() throws SQLException;

	public abstract PGConnection getPostgreSQLConnection() throws SQLException;

	public abstract DatabaseEndpoint getDatabaseEndpoint();

}
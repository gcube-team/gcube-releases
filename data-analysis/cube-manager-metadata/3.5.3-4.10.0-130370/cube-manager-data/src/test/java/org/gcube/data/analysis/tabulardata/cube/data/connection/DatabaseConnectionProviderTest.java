package org.gcube.data.analysis.tabulardata.cube.data.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;

import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.admin.Admin;
import org.gcube.data.analysis.tabulardata.cube.data.connection.admin.AdminDatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged.Unprivileged;
import org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged.UnprivilegedDatabaseConnectionProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class DatabaseConnectionProviderTest {

	@Inject
	@Unprivileged
	UnprivilegedDatabaseConnectionProvider unprivilegedDatabaseConnectionProvider;

	@Inject
	@Admin
	AdminDatabaseConnectionProvider adminDatabaseConnectionProvider;
	
	@Inject
	DatabaseConnectionProvider defaultDatabaseConnectionProvider;

	@BeforeClass
	public static void beforeClass() {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Test
	public void testUnprivileged() throws SQLException {
		testDatabaseConnectionProvider(unprivilegedDatabaseConnectionProvider);
	}

	@Test
	public void testPrivileged() throws SQLException {
		testDatabaseConnectionProvider(adminDatabaseConnectionProvider);
	}

	public void testDatabaseConnectionProvider(DatabaseConnectionProvider databaseConnectionProvider)
			throws SQLException {
		Connection connection = databaseConnectionProvider.getConnection();
		Assert.assertNotNull(connection);

		PGConnection pgConnection = databaseConnectionProvider.getPostgreSQLConnection();
		Assert.assertNotNull(pgConnection);
		CopyManager copyManager = pgConnection.getCopyAPI();
		Assert.assertNotNull(copyManager);
		DatabaseEndpoint dbEndpoint = databaseConnectionProvider.getDatabaseEndpoint();
		Assert.assertNotNull(dbEndpoint);
	}
	
	@Test
	public void testDefaultDatabaseConnectionProvider() throws SQLException {
		Assert.assertNotNull(defaultDatabaseConnectionProvider);
		Assert.assertEquals(unprivilegedDatabaseConnectionProvider, defaultDatabaseConnectionProvider);
		Connection connection = defaultDatabaseConnectionProvider.getConnection();
		Assert.assertNotNull(connection);
	}

}

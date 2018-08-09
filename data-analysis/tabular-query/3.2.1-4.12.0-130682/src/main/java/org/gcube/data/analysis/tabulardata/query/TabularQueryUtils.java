package org.gcube.data.analysis.tabulardata.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged.Unprivileged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TabularQueryUtils {

	private DatabaseConnectionProvider connectionProvider;

	private static Logger log = LoggerFactory.getLogger(TabularQueryUtils.class);

	@Inject
	public TabularQueryUtils(@Unprivileged DatabaseConnectionProvider connectionProvider) {
		super();
		this.connectionProvider = connectionProvider;
		try {
			this.connectionProvider.getPostgreSQLConnection().addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
		} catch (Exception e) {
			log.error("Geometry type cannot be added",e);
		}
	}

	private Connection getConnection() throws SQLException {
		return connectionProvider.getConnection();
	}

	public ResultSet executeSQLQuery(String sql) {
		log.debug("Executing SQL Query: " + sql);
		Connection connection = null;
		Statement statement = null;
		ResultSet rs;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
		} catch (SQLException e) {
			log.error("Unable to execute SQL query.", e);
			throw new RuntimeException("Unable to query the DB.");
		} finally {
			DbUtils.closeQuietly(connection);
		}
		return rs;
	}

	public void executeSQLCommand(String sql) {
		log.debug("Exceuting SQL command: " + sql);
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			log.error("Unable to execute SQL query.", e);
			throw new RuntimeException("Unable to query the DB.");
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(statement);
		}
	}

}

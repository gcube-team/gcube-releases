package org.gcube.data.analysis.tabulardata.operation.test.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import javax.inject.Inject;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyHandler {
	
	private static final Logger log = LoggerFactory.getLogger(CopyHandler.class);
	
	@Inject
	private DatabaseConnectionProvider connectionProvider;
	
	public void copy(String filename, Table table) {
		try {
			PGConnection conn = getPostgreSQLConnection();
			CopyManager cpManager = conn.getCopyAPI();
			StringBuilder columns = new StringBuilder();
			for (Column c : table.getColumns()) {
				if (!c.getColumnType().equals(new IdColumnType())) {
					columns.append(c.getName() + ",");
				}
			}
			if (columns.length()>0)
			columns.deleteCharAt(columns.length() - 1);

			String sqlCmd = String.format("COPY %s ( %s ) FROM STDIN ( FORMAT CSV );", table.getName(),
					columns.toString());
			log.debug("Executing COPY: " + sqlCmd);
			log.debug("Loading file: " + filename + " from classpath.");
			InputStream is = ClassLoader.getSystemResourceAsStream(filename);
			if (is == null)
				Assert.fail("Unable to open file " + filename);
			InputStreamReader isr = new InputStreamReader(is);
			cpManager.copyIn(sqlCmd, isr);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private PGConnection getPostgreSQLConnection() {
		try {
			return connectionProvider.getPostgreSQLConnection();
		} catch (SQLException e) {
			String msg = "Unable to connect to db";
			throw new RuntimeException(msg, e);
		}
	}

}

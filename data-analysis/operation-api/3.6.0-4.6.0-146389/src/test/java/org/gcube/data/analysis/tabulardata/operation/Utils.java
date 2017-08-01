package org.gcube.data.analysis.tabulardata.operation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.ColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.junit.Assert;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);
	
	
	private ColumnFactory factory=BaseColumnFactory.getFactory(new AttributeColumnType());
	
	@Inject
	public CubeManager cm;
	
	@Inject
	private DatabaseConnectionProvider connectionProvider;
	
	public Table createTable(){
		TableCreator tc = cm.createTable(new GenericTableType());
		Table table = null;

		// Create table structure
		try {
			
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			table = tc.create();
			log.debug("Created species generic table:\n" + table);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copy("cl_species.csv", table);
		return table;
	}
	
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

package org.gcube.data.analysis.tabulardata.query.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.AttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.DimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.MeasureColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.junit.Assert;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;

public class TestUtils {

	public CubeManager cm;

	private DatabaseConnectionProvider connectionProvider;
	
	public TestUtils(CubeManager cm, DatabaseConnectionProvider databaseConnectionProvider) {
		super();
		this.cm = cm;
		this.connectionProvider = databaseConnectionProvider;
	}

	public Table createEmptyTable(){
		System.err.println("Creating codelist");
		TableCreator tc = cm.createTable(new CodelistTableType());
		Table codelist = null;

		// Create table structure
		try {
			tc.addColumn(new CodeNameColumnFactory().createDefault());
			tc.addColumn(new CodeNameColumnFactory().create("en"));
			tc.addColumn(new CodeNameColumnFactory().create("fr"));
			tc.addColumn(new CodeNameColumnFactory().create("es"));
			tc.addColumn(new CodeNameColumnFactory().create("la"));
			tc.addColumn(new CodeNameColumnFactory().create(new ImmutableLocalizedText("author","en"),new DataLocaleMetadata("en")));
			codelist = tc.create();
			System.err.println("Created codelist: " + codelist);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		return codelist;	
	}
	
	public Table createSpeciesCodelist() {
		System.err.println("Creating codelist");
		TableCreator tc = cm.createTable(new CodelistTableType());
		Table codelist = null;

		// Create table structure
		try {
			tc.addColumn(new CodeColumnFactory().createDefault());
			tc.addColumn(new CodeNameColumnFactory().create("en"));
			tc.addColumn(new CodeNameColumnFactory().create("fr"));
			tc.addColumn(new CodeNameColumnFactory().create("es"));
			tc.addColumn(new CodeNameColumnFactory().create("la"));
			tc.addColumn(new CodeNameColumnFactory().create(new ImmutableLocalizedText("author","en"),new DataLocaleMetadata("en")));
			codelist = tc.create();
			System.err.println("Created codelist: " + codelist);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copy("cl_species.csv", codelist);
		return codelist;
	}

	public Table createSampleDataset(Table codelist) {
		System.err.println("Creating dataset");
		TableCreator tc = cm.createTable(new DatasetTableType());
		Table dataset = null;
		try {
			Column col =new AttributeColumnFactory().create(new ImmutableLocalizedText("catcher", "en"), new TextType(30));
			System.out.println("type is "+col.getDataType());
			tc.addColumn(col);
			tc.addColumn(new DimensionColumnFactory().create(new ImmutableLocalizedText("species"), new ImmutableColumnRelationship(codelist)));
			tc.addColumn(new MeasureColumnFactory().create(new ImmutableLocalizedText("quantity"), new IntegerType()));
			tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("date"), new DateType()));
			dataset = tc.create();
			System.err.println("Created dataset: " + dataset);

			fillDataset(dataset);
		} catch (TableCreationException e) {
			Assert.fail(e.getMessage());
		}
		return dataset;
	}

	private void fillDataset(Table table) {
		try {
			String tableName = table.getName();
			List<Column> columns = table.getColumns();
			int tuplesNum = 10000;
			Connection conn = (Connection) getPostgreSQLConnection();
			String sqlCmd = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?,?,?,?)", tableName, columns.get(1).getName(), columns.get(2).getName(), columns.get(3).getName(), columns.get(4).getName());
			PreparedStatement ps = conn.prepareStatement(sqlCmd);
			Random random = new Random();
			for (int i = 0; i < tuplesNum; i++) {
				// Generate values
				String catcher = "Luigi Fortunati";
				int quantity = random.nextInt(1000);
				int species = random.nextInt(11562) + 1;
				// Insert values
				ps.setString(1, catcher);
				ps.setInt(2, quantity);
				ps.setInt(3, species);
				ps.setDate(4, new Date(Calendar.getInstance().getTimeInMillis()));
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			e.getNextException().printStackTrace();
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	void copy(String filename, Table table) {
		try {
			PGConnection conn = getPostgreSQLConnection();
			CopyManager cpManager = conn.getCopyAPI();
			StringBuilder columns = new StringBuilder();
			for (Column c : table.getColumns()) {
				if (!c.getColumnType().equals(new IdColumnType())) {
					columns.append(c.getName() + ",");
				}
			}
			columns.deleteCharAt(columns.length() - 1);

			String sqlCmd = String.format("COPY %s ( %s ) FROM STDIN ( FORMAT CSV );", table.getName(),
					columns.toString());
			System.err.println("Executing COPY: " + sqlCmd);
			System.err.println("Loading file: " + filename + " from classpath.");
			InputStream is = this.getClass().getResourceAsStream(filename);
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
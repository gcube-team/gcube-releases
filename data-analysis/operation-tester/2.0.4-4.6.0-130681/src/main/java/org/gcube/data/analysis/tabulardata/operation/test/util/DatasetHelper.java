package org.gcube.data.analysis.tabulardata.operation.test.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.AttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.DimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.MeasureColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.TimeDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetHelper {
	
	private final static Logger log = LoggerFactory.getLogger(DatasetHelper.class);
	
	@Inject
	public CubeManager cm;
	
	@Inject
	public DatabaseConnectionProvider connectionProvider;

	public Table createSampleDataset(Table codelist) {
		Table dataset = createTable(codelist);
		fillTableWithData(dataset);
		return dataset;
	}

	public Table createTable(Table codelist) {
		TableCreator tc = cm.createTable(new DatasetTableType());
		Table dataset = null;
		try {
			tc.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("catcher"), new TextType(30)));
			tc.addColumn(new DimensionColumnFactory().create(new ImmutableLocalizedText("species"), new ImmutableColumnRelationship(
					codelist.getId(), codelist.getColumnsByType(CodeNameColumnType.class).get(0).getLocalId())));
			
			Column timeColumn = new TimeDimensionColumnFactory().create(PeriodType.YEAR);
			Table timeCodelist = cm.getTimeTable(PeriodType.YEAR);
			Column refColumn  = timeCodelist.getColumnByName(PeriodType.YEAR.getName()); 
			timeColumn.setRelationship(new ImmutableColumnRelationship(timeCodelist.getId(), refColumn.getLocalId()));
			
			tc.addColumn(timeColumn);
			tc.addColumn(new MeasureColumnFactory().create(new ImmutableLocalizedText("quantity"), new IntegerType()));
			dataset = tc.create();
			log.debug("Created sample dataset table:\n" + dataset);
		} catch (TableCreationException e) {
			Assert.fail(e.getMessage());
		}
		return dataset;
	}

	private void fillTableWithData(Table table) {
		try {
			String tableName = table.getName();
			List<Column> columns = table.getColumns();
			int tuplesNum = 10000;
			Connection conn = connectionProvider.getConnection();
			String sqlCmd = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?,?,?, ?)", tableName, columns.get(1)
					.getName(), columns.get(2).getName(), columns.get(3).getName(), columns.get(4).getName());
			PreparedStatement ps = conn.prepareStatement(sqlCmd);
			Random random = new Random();
			for (int i = 0; i < tuplesNum; i++) {
				// Generate values
				String catcher = "Luigi Fortunati";
				int quantity = random.nextInt(1000);
				int species = random.nextInt(11562) + 1;
				int year = 1901 + random.nextInt(113);
				// Insert values
				ps.setString(1, catcher);
				ps.setInt(2, species);
				ps.setInt(4, quantity);
				ps.setInt(3, year);
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

}
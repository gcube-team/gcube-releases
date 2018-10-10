package org.gcube.data.analysis.tabulardata.statistical;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.ColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CopyHandler;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class NamesTest extends OperationTester<StatisticalOperationFactory>{
	
	@Inject
	private StatisticalOperationFactory factory;
	
	
	@Inject
	private CubeManager cm;
	
	@Inject
	private CopyHandler copyHandler;
	
	
	private Table testTable;
	
	
	@Before
	public void setupTestTable(){
		testTable = createTable();
	}
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String,Object> getParameterInstances() {
		HashMap<String,Object> params= new HashMap<String, Object>();
		
		
//		params.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), "DBSCAN");
//		HashMap<String,Object> smEntries=new HashMap<String,Object>();
//		smEntries.put("OccurrencePointsClusterLabel", "OccCluster_");
//		smEntries.put("min_points", "1");
//		smEntries.put("OccurrencePointsTable", testTable.getId().toString());
//		smEntries.put("FeaturesColumnNames", 
//				testTable.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId().getValue());
//		smEntries.put("epsilon", "10");
		
		
//		params.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), "GEO_CHART");
//		HashMap<String,Object> smEntries=new HashMap<String,Object>();
//		smEntries.put("InputTable", testTable.getId().toString());
//		List<Column> columns=testTable.getColumns();
//		smEntries.put("Latitude", columns.get(1).getLocalId().getValue());
//		smEntries.put("Longitude", columns.get(2).getLocalId().getValue());
//		smEntries.put("Quantities", columns.get(4).getLocalId().getValue());
////		smEntries.put("Time",columns.get(3).getLocalId().getValue());
//		
//		
//		params.put(StatisticalOperationFactory.DESCRIPTION.getIdentifier(), "test experiment");
//		params.put(StatisticalOperationFactory.SM_ENTRIES.getIdentifier(), smEntries);
//		params.put(StatisticalOperationFactory.TITLE.getIdentifier(), "The experiment title");
//		params.put(StatisticalOperationFactory.USER.getIdentifier(), "fabio.sinibaldi");
//		params.put(StatisticalOperationFactory.CLEAR_DATASPACE.getIdentifier(), true);
//		params.put(StatisticalOperationFactory.REMOVE_EXPORTED.getIdentifier(), true);
//		
		
		
		
		
		return params;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}
	
	
	private Table createTable(){
		ColumnFactory colFactory=BaseColumnFactory.getFactory(new AttributeColumnType());
			TableCreator tc = cm.createTable(new GenericTableType());
			Table table = null;

			// Create table structure
			try {
				tc.addColumn(colFactory.create(new ImmutableLocalizedText("Latitude")));
				tc.addColumn(colFactory.create(new ImmutableLocalizedText("Longitude")));
				tc.addColumn(colFactory.create(new ImmutableLocalizedText("Year")));
				tc.addColumn(colFactory.create(new ImmutableLocalizedText("Measure")));
				table = tc.create();
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}

			// Fill with data
			copyHandler.copy("measures.txt", table);
			return table;
	}
}
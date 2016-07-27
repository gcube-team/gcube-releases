package org.gcube.data.analysis.tabulardata.statistical;

import java.util.ArrayList;
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
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.CopyHandler;
import org.gcube.data.analysis.tabulardata.operation.test.util.DatasetHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class StatisticalOperationTester extends OperationTester<StatisticalOperationFactory>{
	
	private static class TestTableDescriptor{
		ArrayList<String> colNames=new ArrayList();
		String tableFile;
		private TestTableDescriptor(String tableFile,String... colNames) {
			super();
			for(String col:colNames)
			this.colNames.add(col);
			this.tableFile = tableFile;
		}
		
		public ArrayList<String> getColNames() {
			return colNames;
		}
		
		public String getTableFile() {
			return tableFile;
		}
	}
	
	
	@Inject
	private StatisticalOperationFactory factory;
	
	
	@Inject
	private CubeManager cm;
	
	@Inject
	private CopyHandler copyHandler;
	
	@Inject
	private DatasetHelper dsHelper;
	
	@Inject 
	private CodelistHelper clHelper;
	
	private Table testTable;
	
	@Before
	public void setupTestTable(){
//		DATASET
//		testTable=dsHelper.createSampleDataset(clHelper.createSpeciesCodelist());
		
		testTable=createTable();
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
		
		
		
//		params.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), "MAPS_COMPARISON");
//		HashMap<String,Object> smEntries=new HashMap<String,Object>();
//		smEntries.put("Layer_1", "40198411-9ceb-420f-8f39-a7e1b8128d6b ");
//		smEntries.put("Layer_2", "5947d9b0-6235-49bf-98b1-32f380980829");
//		smEntries.put("Z", "0");
//		smEntries.put("ValuesComparisonThreshold","0.1");
//		smEntries.put("TimeIndex_1", "0");
//		smEntries.put("TimeIndex_2", "0");
//		smEntries.put("KThreshold", "0.5");
//		RESULTS :
//			IMAGE (name : "Error Distribution", description : "The distribution of the error along with variance")
		
		
		
//		params.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), "GENERIC_CHARTS");
//		HashMap<String,Object> smEntries=new HashMap<String,Object>();
//		smEntries.put("InputTable", testTable.getId().toString());
//		smEntries.put("Attributes", testTable.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId().getValue());
//		smEntries.put("Quantities", testTable.getColumnsExceptTypes(IdColumnType.class).get(3).getLocalId().getValue());
//		smEntries.put("TopElementsNumber", "10");
//		RESULTS : 
//			IMAGE (name : "Chart focused on quantity - Histogram of the top ten quantities over the dimensions", description "Charts")
		
		
//		params.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), "TIME_GEO_CHART");
//		HashMap<String,Object> smEntries=new HashMap<String,Object>();
//		smEntries.put("InputTable", testTable.getId().toString());
//		smEntries.put("Longitude", testTable.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId().getValue());
//		smEntries.put("Latitude", testTable.getColumnsExceptTypes(IdColumnType.class).get(1).getLocalId().getValue());
//		smEntries.put("Quantities", testTable.getColumnsExceptTypes(IdColumnType.class).get(3).getLocalId().getValue());
//		smEntries.put("Time", testTable.getColumnsExceptTypes(IdColumnType.class).get(2).getLocalId().getValue());
//		RESULTS : 
//			FILE (name : "2c704b73-2654-4293-947a-9ee862fc79aapoints.gif", description : "Chart focused on fs_attribute_4 - A GIF file displaying the points recorded in the time frames of the dataset")

		
//		params.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), "GEO_CHART");
//		HashMap<String,Object> smEntries=new HashMap<String,Object>();
//		smEntries.put("InputTable", testTable.getId().toString());
//		smEntries.put("Longitude", testTable.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId().getValue());
//		smEntries.put("Latitude", testTable.getColumnsExceptTypes(IdColumnType.class).get(1).getLocalId().getValue());
//		smEntries.put("Quantities", testTable.getColumnsExceptTypes(IdColumnType.class).get(3).getLocalId().getValue());
//		RESULTS : 
//			IMAGE (name : "Chart focused on fs_attribute_4 - Distribution of latitudes and longitudes points", description : "Charts")
//			IMAGE (name : "Chart focused on fs_attribute_4 - Distribution of summed quantities over FAO Major Area delimitations", description : "Charts")
		
		
		// GP TEST
//		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		
//		params.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), "GRID_CWP_TO_COORDINATES");
//		HashMap<String,Object> smEntries=new HashMap<String,Object>();
//		smEntries.put("InputTable", testTable.getId().toString());
//		smEntries.put("Attributes", testTable.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId().getValue());
//		smEntries.put("Quantities", testTable.getColumnsExceptTypes(IdColumnType.class).get(3).getLocalId().getValue());
//		smEntries.put("TopElementsNumber", "10");
		
//		params.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), "LISTDBINFO");
//		HashMap<String,Object> smEntries=new HashMap<String,Object>();
//		smEntries.put("ResourceName", "FishBase");
//		RESULT : 
//		 MAP
		
		
		params.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), "FEED_FORWARD_ANN");
		List<Column> attCols = testTable.getColumnsExceptTypes(IdColumnType.class);
		HashMap<String,Object> smEntries=new HashMap<String,Object>();
		smEntries.put("TrainingDataSet", testTable.getId().toString());
		smEntries.put("TrainingColumns", attCols.get(0).getLocalId().getValue()+"#"+attCols.get(1).getLocalId().getValue());
		smEntries.put("TargetColumn", attCols.get(2).getLocalId().getValue());
		smEntries.put("Reference", "1");
		smEntries.put("LearningThreshold", "0.01");
		smEntries.put("MaxIterations", "100");
		smEntries.put("ModelName", "neuralnet_");
		smEntries.put("LayersNeurons", "0");
		
		
		
		
		
		params.put(StatisticalOperationFactory.DESCRIPTION.getIdentifier(), "test experiment");
		params.put(StatisticalOperationFactory.SM_ENTRIES.getIdentifier(), smEntries);
		params.put(StatisticalOperationFactory.TITLE.getIdentifier(), "experiment TEST");
		params.put(StatisticalOperationFactory.USER.getIdentifier(), "fabio.sinibaldi");
		params.put(StatisticalOperationFactory.CLEAR_DATASPACE.getIdentifier(), true);
		params.put(StatisticalOperationFactory.REMOVE_EXPORTED.getIdentifier(), true);
		
		
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
		
		
		TestTableDescriptor measuresTable=new TestTableDescriptor("measures.txt","Attribute 1","Attribute 1","FS:Attribute 3","FS: Attribute 4");
		TestTableDescriptor FEED_CASE=new TestTableDescriptor("ANDport.txt","a","b","c");		
		
		TestTableDescriptor toUse=FEED_CASE;
		
		ColumnFactory colFactory=BaseColumnFactory.getFactory(new AttributeColumnType());
			TableCreator tc = cm.createTable(new GenericTableType());
			Table table = null;

			// Create table structure
			try {
				for(String colName : toUse.getColNames())
				tc.addColumn(colFactory.create(new ImmutableLocalizedText(colName)));				
				table = tc.create();
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}

			// Fill with data
			copyHandler.copy(toUse.getTableFile(), table);
			return table;
	}
}

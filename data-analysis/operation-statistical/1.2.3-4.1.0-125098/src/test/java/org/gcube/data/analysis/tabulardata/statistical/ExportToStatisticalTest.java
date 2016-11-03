package org.gcube.data.analysis.tabulardata.statistical;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
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
public class ExportToStatisticalTest extends OperationTester<ExportToStatisticalOperationFactory>{

	
	@Inject
	private ExportToStatisticalOperationFactory factory;
	
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
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}
	
	@Override
	protected Map<String,Object> getParameterInstances() {
		HashMap<String,Object> params= new HashMap<String, Object>();
		
		params.put(StatisticalOperationFactory.USER.getIdentifier(), "fabio.sinibaldi");
		
		return params;
	}
	
	
	private Table createTable(){
		ColumnFactory colFactory=BaseColumnFactory.getFactory(new AttributeColumnType());
			TableCreator tc = cm.createTable(new GenericTableType());
			Table table = null;

			// Create table structure
			try {
				tc.addColumn(colFactory.create(new ImmutableLocalizedText("Attribute 1")));
				tc.addColumn(colFactory.create(new ImmutableLocalizedText("Attribute 2")));
				tc.addColumn(colFactory.create(new ImmutableLocalizedText("Attribute 3")));
				table = tc.create();
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}

			// Fill with data
			copyHandler.copy("numbers.csv", table);
			return table;
	}
}

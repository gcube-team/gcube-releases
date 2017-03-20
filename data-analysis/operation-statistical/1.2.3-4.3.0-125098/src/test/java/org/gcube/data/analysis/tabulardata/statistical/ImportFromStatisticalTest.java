package org.gcube.data.analysis.tabulardata.statistical;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ImportFromStatisticalTest extends OperationTester<ImportFromStatisticalOperationFactory>{

	@Inject
	private ImportFromStatisticalOperationFactory factory;
	
	@Inject
	private GenericHelper helper;
	
	private Table testTable;
	
	@Before
	public void setupTestTable(){
		testTable = helper.createSpeciesGenericTable();
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
		
		params.put(ImportFromStatisticalOperationFactory.RESOURCE_ID.getIdentifier(), "hcaf_d");
		
		return params;
	}
	
}

package org.gcube.data.analysis.tabulardata.operation.table;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.DatasetHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class) 
public class CreateDatasetViewTest extends OperationTester<CreateViewFactory> {
	
	@Inject
	CreateViewFactory factory;
	
	@Inject
	CodelistHelper codelistHelper;
	
	@Inject
	DatasetHelper datasetHelper;

	private static Table testCodelist;

	private static Table testDataset;

	@Before
	public void setupTestTables(){
		testCodelist = codelistHelper.createSpeciesCodelist();
		testDataset = datasetHelper.createSampleDataset(testCodelist);
	}

	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		return new HashMap<String, Object>();
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testDataset.getId();
	}

	

}

package org.gcube.data.analysis.tabulardata.operation.column.dimension;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.column.ChangeToDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.DatasetHelper;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class FromDimensionToDimensionTestFail extends OperationTester<ChangeToDimensionColumnFactory> {

	@Inject
	private DatasetHelper datasetHelper;
	
	@Inject
	private CodelistHelper codelistHelper;
	
	@Inject
	private ChangeToDimensionColumnFactory factory;
	
	private Table targetTable;
	
	private Table codelistTable;
	@Before
	public void init(){
		targetTable=datasetHelper.createSampleDataset(codelistHelper.createSpeciesCodelist());
		codelistTable=codelistHelper.createSpeciesCodelist();
	}
	
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map getParameterInstances() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ColumnLocalId columnId = codelistTable.getColumnsByType(CodeNameColumnType.class).get(0).getLocalId();
		parameters.put(ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER.getIdentifier(), new ColumnReference(
				codelistTable.getId(), columnId));
		return parameters;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return targetTable.getColumnsByType(DimensionColumnType.class).get(0).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return targetTable.getId();
	}

}

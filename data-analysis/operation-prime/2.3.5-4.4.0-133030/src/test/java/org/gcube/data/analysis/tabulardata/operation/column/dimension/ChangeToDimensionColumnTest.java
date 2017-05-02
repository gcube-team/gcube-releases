package org.gcube.data.analysis.tabulardata.operation.column.dimension;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.column.ChangeToDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public abstract class ChangeToDimensionColumnTest extends OperationTester<ChangeToDimensionColumnFactory> {

	Table codelistTable;

	Table datasetTable;

	@Inject
	GenericHelper genericHelper;

	@Inject
	CodelistHelper codelistHelper;

	@Inject
	ChangeToDimensionColumnFactory factory;

	@Before
	public void setupTestTables() {
		codelistTable = codelistHelper.createSpeciesCodelist();
		datasetTable = genericHelper.createDatasetWithSpecies();
	}

	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		ColumnLocalId columnId = codelistTable.getColumns().get(getTargetCodelistReferenceColumnIndex()).getLocalId();
		parameters.put(ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER.getIdentifier(), new ColumnReference(
				codelistTable.getId(), columnId));
		return parameters;
	}

	protected abstract int getTargetCodelistReferenceColumnIndex();

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return datasetTable.getColumns().get(getTargetDatasetColumnIndex()).getLocalId();
	}

	protected abstract int getTargetDatasetColumnIndex();

	@Override
	protected TableId getTargetTableId() {
		return datasetTable.getId();
	}

}

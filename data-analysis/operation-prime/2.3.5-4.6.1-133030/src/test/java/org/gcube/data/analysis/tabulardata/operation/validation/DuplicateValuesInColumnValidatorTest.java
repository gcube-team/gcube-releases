package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class DuplicateValuesInColumnValidatorTest extends OperationTester<DuplicateValuesInColumnValidatorFactory>{

	
	@Inject
	CodelistHelper codelistHelper;

	@Inject
	DuplicateValuesInColumnValidatorFactory factory;
	Table testTable;

	@Before
	public void createTestTable() {
		testTable = codelistHelper.createSpeciesCodelist();
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
		return testTable.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}
	
}

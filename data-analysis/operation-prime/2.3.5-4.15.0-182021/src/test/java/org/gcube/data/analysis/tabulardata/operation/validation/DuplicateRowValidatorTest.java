package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
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
public class DuplicateRowValidatorTest extends OperationTester<DuplicateRowValidatorFactory> {

	@Inject
	CodelistHelper codelistHelper;

	@Inject
	DuplicateRowValidatorFactory factory;
	Table testTable;

	@Before
	public void createTestTable() {
		testTable = codelistHelper.createSpeciesCodelist();
	}

	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> getParameterInstances() {
		List<ColumnReference> refs = new ArrayList<ColumnReference>();
		for (Column col: testTable.getColumnsExceptTypes(IdColumnType.class))
			refs.add(new ColumnReference(testTable.getId(), col.getLocalId()));
		return Collections.singletonMap(DuplicateRowValidatorFactory.KEY.getIdentifier(),(Object) refs);
		
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

}

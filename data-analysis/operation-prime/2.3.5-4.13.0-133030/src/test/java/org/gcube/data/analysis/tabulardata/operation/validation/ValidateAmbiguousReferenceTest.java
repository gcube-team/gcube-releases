package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class ValidateAmbiguousReferenceTest extends OperationTester<ValidateAmbiguousReferenceFactory>{

	@Inject
	CodelistHelper codelistHelper;

	@Inject
	GenericHelper genericHelper;

	@Inject
	ValidateAmbiguousReferenceFactory factory;

	Table targetTable;
	Table codelistTable;


	@Override
	protected WorkerFactory<ValidationWorker> getFactory() {
		return factory;
	}

	@Before
	public void createTestTable() {
		codelistTable = codelistHelper.createSpeciesCodelist();
		targetTable = genericHelper.createSpeciesGenericTable();
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return targetTable.getColumnsExceptTypes(IdColumnType.class).get(1).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return targetTable.getId();
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		HashMap<String,Object> params=new HashMap<>();
		Column col=codelistTable.getColumnsExceptTypes(IdColumnType.class).get(1);
		
		params.put(ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER.getIdentifier(), codelistTable.getColumnReference(col));
		
		params.put("mapping", Collections.singletonMap(new TDText("UYE"), 2));
		return params;
	}
}
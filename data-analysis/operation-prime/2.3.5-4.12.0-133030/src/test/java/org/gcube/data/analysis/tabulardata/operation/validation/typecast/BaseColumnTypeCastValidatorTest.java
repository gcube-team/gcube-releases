package org.gcube.data.analysis.tabulardata.operation.validation.typecast;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.validation.ColumnTypeCastValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public abstract class BaseColumnTypeCastValidatorTest extends OperationTester<ColumnTypeCastValidatorFactory> {

	@Inject
	ColumnTypeCastValidatorFactory factory;

	@Inject
	GenericHelper helper;

	Table testTable;

	@Before
	public void setupTestTable() {
		testTable = helper.createComplexTable();
	}

	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(factory.TARGET_TYPE_PARAMETER.getIdentifier(), getTargetType());
		return parameters;
	}

	protected abstract DataType getTargetType();

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testTable.getColumns().get(getColumnIndex()).getLocalId();
	}

	protected int getColumnIndex(){
		return 1;
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

}

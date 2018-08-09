package org.gcube.data.analysis.tabulardata.operation.column.attribute;

import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.column.ChangeToAttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.validation.ColumnTypeCastValidatorFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.google.common.collect.Maps;
import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public abstract class BaseAttributeColumnTypeChangeTest extends OperationTester<ChangeToAttributeColumnFactory> {

	private Table testTable;

	@Inject
	private GenericHelper helper;

	@Inject
	private ChangeToAttributeColumnFactory factory;

	@Before
	public void before() {
		testTable = helper.createComplexTable();
	}

	@Override
	protected ChangeToAttributeColumnFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String, Object> parameters = Maps.newHashMap();
		parameters.put(ColumnTypeCastValidatorFactory.TARGET_TYPE_PARAMETER.getIdentifier(), getTargetDataType());
		return parameters;
	}

	protected abstract DataType getTargetDataType();

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testTable.getColumns().get(getColumnIndex()).getLocalId();
	}

	protected abstract int getColumnIndex();

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

}

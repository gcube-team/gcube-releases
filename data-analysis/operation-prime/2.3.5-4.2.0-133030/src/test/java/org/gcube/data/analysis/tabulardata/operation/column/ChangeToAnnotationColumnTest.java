package org.gcube.data.analysis.tabulardata.operation.column;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ChangeToAnnotationColumnTest extends OperationTester<ChangeToAnnotationColumnFactory> {

	@Inject
	private GenericHelper genericHelper;

	@Inject
	private ChangeToAnnotationColumnFactory factory;

	private Table testTable;

	@Before
	public void before() {
		testTable = genericHelper.createSpeciesGenericTable();
	}

	@Override
	protected ChangeToAnnotationColumnFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		return new HashMap<String, Object>();
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testTable.getColumns().get(2).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

}

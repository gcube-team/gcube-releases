package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.remove.RemoveColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class RemoveColumnTest extends OperationTester<RemoveColumnFactory>{

	@Inject
	RemoveColumnFactory factory;
	
	@Inject
	CodelistHelper codelistHelper;

	private static Table testCodelist;

	@Before
	public void setupTestTables(){
		testCodelist = codelistHelper.createSpeciesCodelist();
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
		return testCodelist.getColumns().get(1).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return testCodelist.getId();
	}

}

package org.gcube.data.analysis.tabulardata.operation.table;

import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.google.common.collect.Maps;
import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ChangeTableTypeTest extends OperationTester<ChangeTableTypeFactory>{ 

	@Inject
	ChangeTableTypeFactory factory;

	private Table testCodelist;
	
	@Inject
	CodelistHelper codelistHelper;

	@Before
	public void setupTestTable(){
		testCodelist = codelistHelper.createSpeciesCodelist();
	}

	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String,Object> parameters = Maps.newHashMap();
		parameters.put(ChangeTableTypeFactory.TABLE_TYPE_PARAMETER.getIdentifier(), new CodelistTableType().getName());
		return parameters;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testCodelist.getId();
	}
}

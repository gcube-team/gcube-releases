package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class AddTableNameTest extends OperationTester<AddTableNameFactory> {

	@Inject
	AddTableNameFactory factory;

	@Inject
	CodelistHelper codelistHelper;

	Table testTable;

	@Override
	protected AddTableNameFactory getFactory() {
		return factory;
	}

	@Before
	public void createTestTable(){
		testTable = codelistHelper.createSpeciesCodelist();
	}


	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(AddTableNameFactory.NAME_LABEL_PARAMETER.getIdentifier(), new ImmutableLocalizedText(
				"cani", "it"));
		return parameterInstances;
	}

}

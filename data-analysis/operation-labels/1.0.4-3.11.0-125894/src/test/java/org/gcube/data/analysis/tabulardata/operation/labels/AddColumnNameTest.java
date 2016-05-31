package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class AddColumnNameTest  extends OperationTester<AddColumnNameFactory> {

	@Inject
	AddColumnNameFactory factory;
	
	@Inject
	CodelistHelper codelistHelper;
	
	Table testTable;
	
	@Before
	public void createTestTable(){
		testTable=codelistHelper.createSpeciesCodelist();
	}

	@Override
	protected AddColumnNameFactory getFactory() {
		return factory;
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
		Map<String,Object> parameterInstances=new HashMap<String, Object>();
		
		Map<ColumnReference, LocalizedText> labels = new HashMap<>();
		labels.put(new ColumnReference(testTable.getId(), testTable.getColumns().get(1).getLocalId()), new ImmutableLocalizedText("prima etichetta", "en"));
		
		parameterInstances.put(AddColumnNameFactory.NAME_LABEL_PARAMETER.getIdentifier(), labels );		
		return parameterInstances;
	}
	
}

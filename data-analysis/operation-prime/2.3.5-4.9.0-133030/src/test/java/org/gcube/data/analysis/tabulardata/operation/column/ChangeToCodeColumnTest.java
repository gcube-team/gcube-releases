package org.gcube.data.analysis.tabulardata.operation.column;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ChangeToCodeColumnTest extends OperationTester<ChangeToCodeColumnFactory>{
	
	private Table testCodelist;

	@Inject
	GenericHelper genericHelper;

	@Inject
	ChangeToCodeColumnFactory factory;

	@Before
	public void before() {
		testCodelist = genericHelper.createSpeciesGenericTable();
	}

	@Override
	protected ChangeToCodeColumnFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		HashMap<String, Object> map=new HashMap<>();
		map.put(ChangeColumnTypeTransformationFactory.ADDITIONAL_META_PARAMETER.getIdentifier(),new DataLocaleMetadata("aa"));
		return map;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testCodelist.getColumns().get(2).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return testCodelist.getId();
	}

}

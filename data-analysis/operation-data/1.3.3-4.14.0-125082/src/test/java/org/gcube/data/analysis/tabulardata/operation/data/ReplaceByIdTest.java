package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.replace.ReplaceByIdFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ReplaceByIdTest extends OperationTester<ReplaceByIdFactory>{

	
	@Inject
	private GenericHelper genericHelper;
	
	@Inject
	private ReplaceByIdFactory factory;
	
	
	private Table testTable;
	
	@Before
	public void before() {
		testTable = genericHelper.createSpeciesGenericTable();
	}
	
	@Override
	protected ReplaceByIdFactory getFactory() {
		return factory;
	}
	
	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testTable.getColumns().get(1).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}
	
	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String,Object> toReturn= new HashMap<String, Object>();
		toReturn.put(ReplaceByIdFactory.ID.getIdentifier(), new Integer(1));
		toReturn.put(ReplaceByIdFactory.VALUE.getIdentifier(), new TDText("CIAO -----  -----"));
		return toReturn;
	}
}

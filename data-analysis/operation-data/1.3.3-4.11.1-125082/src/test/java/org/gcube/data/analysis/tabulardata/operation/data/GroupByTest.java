package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.AggregationFunction;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.GroupByFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class GroupByTest extends OperationTester<GroupByFactory>{

	@Inject
	private GroupByFactory factory;
	
	@Inject
	private GenericHelper helper;
	
	private Table testTable;
	
	@Before
	public void setupTestTable(){
		testTable = helper.createSpeciesGenericTable();
	}
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String,Object> getParameterInstances() {
		HashMap<String,Object> params= new HashMap<String, Object>();
		List<Column> cols=testTable.getColumnsExceptTypes(IdColumnType.class);
		params.put(GroupByFactory.GROUPBY_COLUMNS.getIdentifier(), testTable.getColumnReference(cols.get(0)));
		ArrayList<Map<String,Object>> composite=new ArrayList<Map<String,Object>>();
		
		HashMap<String,Object> firstComposite=new HashMap<String,Object>();
		firstComposite.put(GroupByFactory.FUNCTION_PARAMETER.getIdentifier(), new ImmutableLocalizedText(AggregationFunction.COUNT+""));
		firstComposite.put(GroupByFactory.TO_AGGREGATE_COLUMNS.getIdentifier(), testTable.getColumnReference(cols.get(1)));
		composite.add(firstComposite);
		
		HashMap<String,Object> secondComposite=new HashMap<String,Object>();
		secondComposite.put(GroupByFactory.FUNCTION_PARAMETER.getIdentifier(),new ImmutableLocalizedText(AggregationFunction.COUNT+""));
		secondComposite.put(GroupByFactory.TO_AGGREGATE_COLUMNS.getIdentifier(), testTable.getColumnReference(cols.get(2)));
		composite.add(secondComposite);
		
		
		
		params.put(GroupByFactory.AGGREGATE_FUNCTION_TO_APPLY.getIdentifier(), composite);
		return params;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}
}

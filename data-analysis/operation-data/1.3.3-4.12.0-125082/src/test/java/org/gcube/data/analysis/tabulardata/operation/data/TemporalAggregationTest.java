package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.AggregationFunction;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.TemporalAggregationFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class TemporalAggregationTest extends OperationTester<TemporalAggregationFactory> {

	@Inject
	TemporalAggregationFactory factory;
	
	@Inject 
	GenericHelper helper;
	
	@Inject
	CubeManager cm;
	
	private Table targetTable;

	@Before
	public void initTables(){
		targetTable=helper.createDatasetWithTimeDimension();
	}
	
	@Override
	protected TemporalAggregationFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		HashMap<String,Object> params= new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		List<Column> cols=targetTable.getColumnsExceptTypes(IdColumnType.class);
		params.put(TemporalAggregationFactory.KEY_COLUMNS.getIdentifier(), targetTable.getColumnReference(cols.get(1)));
		
		ArrayList<Map<String,Object>> composite=new ArrayList<Map<String,Object>>();
		
		HashMap<String,Object> firstComposite=new HashMap<String,Object>();
		firstComposite.put(TemporalAggregationFactory.FUNCTION_PARAMETER.getIdentifier(), new ImmutableLocalizedText(AggregationFunction.COUNT+""));
		firstComposite.put(TemporalAggregationFactory.TO_AGGREGATE_COLUMNS.getIdentifier(), targetTable.getColumnReference(cols.get(2)));
		composite.add(firstComposite);
		params.put(TemporalAggregationFactory.AGGREGATE_FUNCTION_TO_APPLY.getIdentifier(), composite);
		
		params.put(TemporalAggregationFactory.TIME_DIMENSION_AGGR.getIdentifier(), PeriodType.MONTH.name());
		
		return params;
		
				
	}
	
	@Override
	protected TableId getTargetTableId() {
		return targetTable.getId();
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return targetTable.getColumnsByType(new TimeDimensionColumnType()).get(0).getLocalId();
	}
	
}

package org.gcube.data.analysis.tabulardata.operation.data.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextChoiceParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

@Singleton
public class GroupByFactory extends TableTransformationWorkerFactory{
	
	private static final OperationId OPERATION_ID = new OperationId(3006);
	
	CubeManager cubeManager;
	
	DatabaseConnectionProvider connectionProvider;
	SQLExpressionEvaluatorFactory sqlExpressionEvaluatorFactory;
	
	@Override
	public DataWorker createWorker(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0,cubeManager);	
		checkParameters(arg0);
		return new GroupBy(arg0, cubeManager, connectionProvider,sqlExpressionEvaluatorFactory);
	}
	
	
	private void checkParameters(OperationInvocation invocation)throws InvalidInvocationException{
		TableId targetTableId=invocation.getTargetTableId();
		
		Object toAggregateObj=invocation.getParameterInstances().get(GroupByFactory.GROUPBY_COLUMNS.getIdentifier());
		if(toAggregateObj instanceof Iterable<?>){
			Iterable<ColumnReference> cols=(Iterable<ColumnReference>) toAggregateObj;
			for(ColumnReference ref:cols)
				if(!ref.getTableId().equals(targetTableId)) throw new InvalidInvocationException(invocation, "Inconsistent target table and aggregation column references");			
		}else{
			if(!((ColumnReference)toAggregateObj).getTableId().equals(targetTableId))throw new InvalidInvocationException(invocation, "Inconsistent target table and aggregation column references");
		}
		
		Object compositeObj=invocation.getParameterInstances().get(GroupByFactory.AGGREGATE_FUNCTION_TO_APPLY.getIdentifier());
		if(compositeObj instanceof Iterable<?>)
			for(Object mapObj:(Iterable<?>)compositeObj){
				if(!((ColumnReference)((Map<String, Object>) mapObj).get(GroupByFactory.TO_AGGREGATE_COLUMNS.getIdentifier())).getTableId().equals(targetTableId)) 
					throw new InvalidInvocationException(invocation, "Inconsistent target table and to aggregate values column references");
			}
		else if(!((ColumnReference)((Map<String, Object>) compositeObj).get(GroupByFactory.TO_AGGREGATE_COLUMNS.getIdentifier())).getTableId().equals(targetTableId)) 
			throw new InvalidInvocationException(invocation, "Inconsistent target table and to aggregate values column references");
	}


	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);	
		checkParameters(invocation);
		List<Column> groupByColumns=new ArrayList<>();
		
		Object toAggregateObj=invocation.getParameterInstances().get(GroupByFactory.GROUPBY_COLUMNS.getIdentifier());
		if(toAggregateObj instanceof Iterable<?>){
			Iterable<ColumnReference> cols=(Iterable<ColumnReference>) toAggregateObj;
			for(ColumnReference ref:cols)
				groupByColumns.add(cubeManager.getTable(ref.getTableId()).getColumnById(ref.getColumnId()));			
		}else{
			ColumnReference ref=(ColumnReference) toAggregateObj;
			groupByColumns.add(cubeManager.getTable(ref.getTableId()).getColumnById(ref.getColumnId()));
		}
		
		return String.format("Group by %s",OperationHelper.getColumnLabelsSnippet(groupByColumns));
	}
	
	
	@Inject
	public GroupByFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlExpressionEvaluatorFactory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlExpressionEvaluatorFactory=sqlExpressionEvaluatorFactory;
	}







	public static TargetColumnParameter GROUPBY_COLUMNS= new TargetColumnParameter("groupByColumns", "Group By Columns", "Columns to use as a key of grouping", 
																		new Cardinality(1, Integer.MAX_VALUE));

	public static LocalizedTextChoiceParameter FUNCTION_PARAMETER=new LocalizedTextChoiceParameter("functionParameter", "Function Parameter", "Aggregation function to apply",
			Cardinality.ONE, Arrays.asList(new ImmutableLocalizedText[]{
				new ImmutableLocalizedText(AggregationFunction.AVG+""),
				new ImmutableLocalizedText(AggregationFunction.COUNT+""),
				new ImmutableLocalizedText(AggregationFunction.MAX+""),
				new ImmutableLocalizedText(AggregationFunction.MIN+""),
				new ImmutableLocalizedText(AggregationFunction.SUM+""),
				new ImmutableLocalizedText(AggregationFunction.ST_EXTENT+""),
			}));
	
	public static TargetColumnParameter TO_AGGREGATE_COLUMNS=new TargetColumnParameter("functionMember", "To Aggregate values", "Aggregation function member",
			Cardinality.ONE);

	
	public static CompositeParameter AGGREGATE_FUNCTION_TO_APPLY= new CompositeParameter("aggregationFunctions", "Aggregation Functions", "Aggregation Function to apply",
			new Cardinality(1, Integer.MAX_VALUE),
			Arrays.asList(new Parameter[]{
					FUNCTION_PARAMETER,
					TO_AGGREGATE_COLUMNS
			}));
			
	
	
	
	private List<Parameter> parameters = Arrays.asList(
			GROUPBY_COLUMNS,
			AGGREGATE_FUNCTION_TO_APPLY
			);
	
	
	
	
	@Override
	protected String getOperationDescription() {
		return "Group rows by selected keys, applying the specified aggregation functions to relative selected member columns";
	}


	@Override
	protected String getOperationName() {
		return "Group by";
	}


	@Override
	protected List<Parameter> getParameters() {		
		return parameters;
	}
	
	@Override
	protected OperationId getOperationId() {		
		return OPERATION_ID;
	}
}

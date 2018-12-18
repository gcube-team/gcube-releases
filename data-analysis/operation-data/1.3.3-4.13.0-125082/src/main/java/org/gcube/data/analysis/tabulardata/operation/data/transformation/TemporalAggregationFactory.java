package org.gcube.data.analysis.tabulardata.operation.data.transformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextChoiceParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class TemporalAggregationFactory extends
ColumnTransformationWorkerFactory {

	private static final OperationId OPERATION_ID = new OperationId(3009);

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	static List<ColumnType> allowedColumn = Collections.singletonList((ColumnType)new TimeDimensionColumnType());


	@Inject
	public TemporalAggregationFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}

	@Override
	public List<ColumnType> getAllowedColumnTypes() {
		return allowedColumn;
	}

	public static TargetColumnParameter KEY_COLUMNS= new TargetColumnParameter("keyColumns", "Key Columns", "Columns to use as a key of grouping", 
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

	public static MultivaluedStringParameter TIME_DIMENSION_AGGR=new MultivaluedStringParameter("timeDimensionAggr", "Time dimension Aggregation Type", "Type of Time dimension for aggregation",
			Cardinality.ONE, Arrays.asList(
					PeriodType.MONTH.name(),
					PeriodType.QUARTER.name(),
					PeriodType.YEAR.name()
					));

	public static TargetColumnParameter TO_AGGREGATE_COLUMNS=new TargetColumnParameter("functionMember", "To Aggregate values", "Aggregation function member",
			Cardinality.ONE);


	public static CompositeParameter AGGREGATE_FUNCTION_TO_APPLY= new CompositeParameter("aggregationFunctions", "Aggregation Functions", "Aggregation Function to apply",
			new Cardinality(1, Integer.MAX_VALUE),
			Arrays.asList(new Parameter[]{
					FUNCTION_PARAMETER,
					TO_AGGREGATE_COLUMNS
			}));

	private static List<Parameter> parameters = Arrays.asList(
			KEY_COLUMNS,
			AGGREGATE_FUNCTION_TO_APPLY,
			TIME_DIMENSION_AGGR
			);

	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		checkParameters(invocation);
		return new TemporalAggregationWorker(invocation, cubeManager, connectionProvider);
	}



	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}



	@Override
	public boolean isRollbackable() {
		return false;
	}


	@SuppressWarnings("unchecked")
	private void checkParameters(OperationInvocation invocation)throws InvalidInvocationException{

		PeriodType selectedPeriod = PeriodType.valueOf((String)invocation.getParameterInstances().get(TIME_DIMENSION_AGGR.getIdentifier()));

		PeriodType sourcePeriod;
		try{
			Table targetTable = cubeManager.getTable(invocation.getTargetTableId());
			Column targetColumn = targetTable.getColumnById(invocation.getTargetColumnId());
			PeriodTypeMetadata periodMetadata =targetColumn.getMetadata(PeriodTypeMetadata.class);
			sourcePeriod = periodMetadata.getType();
		}catch(Exception e){
			throw new InvalidInvocationException(invocation, "error on target table", e);
		}
		if (!PeriodType.getHierarchicalRelation().get(sourcePeriod).contains(selectedPeriod))
			throw new InvalidInvocationException(invocation, "Temporal aggregation from "+sourcePeriod+" to "+selectedPeriod+" not allowed");


		TableId targetTableId=invocation.getTargetTableId();

		Object toAggregateObj=invocation.getParameterInstances().get(KEY_COLUMNS.getIdentifier());
		if (toAggregateObj!=null){
			if(toAggregateObj instanceof Iterable<?>){
				Iterable<ColumnReference> cols=(Iterable<ColumnReference>) toAggregateObj;
				for(ColumnReference ref:cols){
					if(!ref.getTableId().equals(targetTableId)) throw new InvalidInvocationException(invocation, "Inconsistent target table and aggregation column references");			
					if (ref.getColumnId()==invocation.getTargetColumnId())
						throw new InvalidInvocationException(invocation, "Target column cannot be set also as a key");			
				}
			}else{
				if(!((ColumnReference)toAggregateObj).getTableId().equals(targetTableId))throw new InvalidInvocationException(invocation, "Inconsistent target table and aggregation column references");
			}
		}
		Object compositeObj=invocation.getParameterInstances().get(AGGREGATE_FUNCTION_TO_APPLY.getIdentifier());
		if (compositeObj!=null)
			if(compositeObj instanceof Iterable<?>)
				for(Object mapObj:(Iterable<?>)compositeObj){
					if(!((ColumnReference)((Map<String, Object>) mapObj).get(TO_AGGREGATE_COLUMNS.getIdentifier())).getTableId().equals(targetTableId)) 
						throw new InvalidInvocationException(invocation, "Inconsistent target table and to aggregate values column references");
					if (((ColumnReference)((Map<String, Object>) mapObj).get(TO_AGGREGATE_COLUMNS.getIdentifier())).getColumnId()== invocation.getTargetColumnId())
						throw new InvalidInvocationException(invocation, "Target column cannot be set also as an aggregation column");
				}
			else if(!((ColumnReference)((Map<String, Object>) compositeObj).get(TO_AGGREGATE_COLUMNS.getIdentifier())).getTableId().equals(targetTableId)) 
				throw new InvalidInvocationException(invocation, "Inconsistent target table and to aggregate values column references");
			else if (((ColumnReference)((Map<String, Object>) compositeObj).get(TO_AGGREGATE_COLUMNS.getIdentifier())).getColumnId()== invocation.getTargetColumnId())
				throw new InvalidInvocationException(invocation, "Target column cannot be set also as an aggregation column");	
	}



	@Override
	protected String getOperationName() {
		return "Temporal aggregation";
	}

	@Override
	protected String getOperationDescription() {
		return "aggregation based on timedimension";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

}

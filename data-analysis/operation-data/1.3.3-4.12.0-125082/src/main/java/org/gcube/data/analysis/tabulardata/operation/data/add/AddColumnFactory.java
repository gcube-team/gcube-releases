package org.gcube.data.analysis.tabulardata.operation.data.add;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ColumnMetadataParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ColumnTypeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.DataTypeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

@Singleton
public class AddColumnFactory extends TableTransformationWorkerFactory{

	private static final OperationId OPERATION_ID = new OperationId(1005);
	
	
	public static final DataTypeParameter DATA_TYPE=new DataTypeParameter("dataType", "Data Type", "To set data type", Cardinality.OPTIONAL);
	public static final ColumnTypeParameter COLUMN_TYPE=new ColumnTypeParameter("columnType","Column type","The type of the new column",Cardinality.ONE);
	public static final LocalizedTextParameter LABEL= new LocalizedTextParameter("label", "label", "To set label", Cardinality.OPTIONAL);
	public static final ExpressionParameter VALUE_PARAMETER=new ExpressionParameter("value", "To set Value", "The value to be set in new column.", Cardinality.OPTIONAL);
	public static final ExpressionParameter CONDITION_PARAMETER=new ExpressionParameter("condition","Condition Expression","Value on which to set passed value expression",Cardinality.OPTIONAL);
	public static final ColumnMetadataParameter ADDITIONAL_META_PARAMETER=new ColumnMetadataParameter("meta", "Additional Metadata", "Metadata to be se to the new column", new Cardinality(0,Integer.MAX_VALUE));
	
	
	
	private static List<Parameter> parameters=Arrays.asList(new Parameter[]{
		COLUMN_TYPE,
		DATA_TYPE,
		LABEL,
		VALUE_PARAMETER,
		CONDITION_PARAMETER,
		ADDITIONAL_META_PARAMETER
	});
	
	
	@Inject
	private CubeManager cubeManager;
	@Inject
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	@Inject
	private DatabaseConnectionProvider connectionProvider;
	
	
	@Override
	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkAllowedColumnDataCombination(invocation);
		return new AddColumn(invocation, cubeManager,sqlEvaluatorFactory,connectionProvider);
	}

	@Override
	public boolean isRollbackable() {
		return true;
	}

	@Override
	public RollbackWorker createRollbackWoker(Table diffTable,
			Table createdTable, OperationInvocation oldInvocation) {
		return new AddColumnRollbackWorker(diffTable, createdTable, oldInvocation, cubeManager);
	}
	
	@Override
	protected String getOperationName() {
		return "Adds a column";
	}

	@Override
	protected String getOperationDescription() {
		return "Adds a column to a table";
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	
	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}
	
	private void checkAllowedColumnDataCombination(OperationInvocation invocation) throws InvalidInvocationException{
		TableType tableType=cubeManager.getTable(invocation.getTargetTableId()).getTableType();
		Map<String,Object> params=invocation.getParameterInstances();
		ColumnType colType=(ColumnType) params.get(COLUMN_TYPE.getIdentifier());	
		if (colType.equals(new ValidationColumnType())||colType.equals(new IdColumnType()))
			throw new InvalidInvocationException(invocation,String.format("Column type %s cannot be added by user",colType.getName()));
		if(!tableType.getAllowedColumnTypes().contains(colType))	
			throw new InvalidInvocationException(invocation, String.format("Passed column type %s is not allowed for target table type %s. Allowed types are %s.", colType.getName(),tableType.getName(),tableType.getAllowedColumnTypes()));
		
		DataType type=params.containsKey(DATA_TYPE.getIdentifier())?(DataType) params.get(DATA_TYPE.getIdentifier()):null;
		if(type!=null){			
			if(!colType.isDataTypeAllowed(type))
				throw new InvalidInvocationException(invocation, String.format("Incompatible column type %s and data type %s. Allowed data types are %s.", colType.getName(),type.getName(),colType.getAllowedDataTypes()));
		}else type=colType.getDefaultDataType();
		
		Expression valueExpr=params.containsKey(VALUE_PARAMETER.getIdentifier())?(Expression) params.get(VALUE_PARAMETER.getIdentifier()):null;
		if(valueExpr!=null){
			try{
				DataType exprType=valueExpr.getReturnedDataType();
				if(!type.getName().equals(exprType.getName()))
					throw new InvalidInvocationException(invocation, String.format("Incompatible target type %s and value type %s. Allowed data types are %s.", type.getName(),exprType.getName(),colType.getAllowedDataTypes()));
			}catch(NotEvaluableDataTypeException e){
				//unable to evaluate returned data type
			}
		}
		
		Expression conditionExpr=params.containsKey(CONDITION_PARAMETER.getIdentifier())?(Expression) params.get(CONDITION_PARAMETER.getIdentifier()):null;
		if(conditionExpr!=null){
			try{
				DataType exprType=conditionExpr.getReturnedDataType();
				if(!exprType.getName().equals(new BooleanType().getName()))
					throw new InvalidInvocationException(invocation, String.format("Invalid condition type value %s, Boolean expected.", exprType.getName()));
			}catch(NotEvaluableDataTypeException e){
				//unable to evaluate returned data type
			}
		}
	}
	
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkAllowedColumnDataCombination(invocation);
		ColumnType colType=OperationHelper.getParameter(COLUMN_TYPE, invocation);
		DataType dataType=colType.getDefaultDataType();		
		if(invocation.getParameterInstances().containsKey(DATA_TYPE.getIdentifier()))
			dataType=OperationHelper.getParameter(DATA_TYPE, invocation);
		if(invocation.getParameterInstances().containsKey(LABEL.getIdentifier())){
			LocalizedText label=OperationHelper.getParameter(LABEL, invocation);
			return String.format("Add %s (%s) with label %s[%s] ", colType.getName(),dataType,label.getValue(),label.getLocale());
		}else
		return String.format("Add %s (%s)", colType.getName(),dataType);
	}
}

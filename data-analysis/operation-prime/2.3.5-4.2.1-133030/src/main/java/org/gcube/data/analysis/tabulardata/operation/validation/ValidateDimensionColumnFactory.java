package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class ValidateDimensionColumnFactory extends ColumnValidatorFactory {

	private static final OperationId OPERATION_ID=new OperationId(5010);
	
	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	private ValidateDataWithExpressionFactory validationFactory;
	
	public static TargetColumnParameter TARGET_COLUMN_PARAMETER;
	
	public static ExpressionParameter EXTERNAL_CONDITION_PARAMETER=new ExpressionParameter("expression", "Expression",
			"Condition on codelist values", Cardinality.OPTIONAL);
	
	private static List<Parameter> parameters;
	
	
	static {
		ArrayList<TableType> allowedTableTypes=new ArrayList<>();
		allowedTableTypes.add(new CodelistTableType());
	
		
		TARGET_COLUMN_PARAMETER=new TargetColumnParameter("refColumn", "Codelist referenced column",
				"A codelist column containing values that are contained in the target column", Cardinality.ONE, 
				allowedTableTypes);
		parameters=Arrays.asList(new Parameter[]{
				TARGET_COLUMN_PARAMETER,
				EXTERNAL_CONDITION_PARAMETER
			});
	}
	
	
	@Override
	protected String getOperationName() {
		return "Dimension Column Validator ("+OPERATION_ID.getValue()+")";
	}

	@Override
	protected String getOperationDescription() {
		return "Validate the specified dimension column";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	@Inject
	public ValidateDimensionColumnFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			ValidateDataWithExpressionFactory validatorFactory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
		this.validationFactory=validatorFactory;
	}
	
	@Override
	public ValidationWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);		
//		Expression toCheck=generateValidationExpression(invocation);
//		invocation.getParameterInstances().put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), toCheck);
//		invocation.getParameterInstances().put(ValidateDataWithExpressionFactory.DESCRIPTION_PARAMETER.getIdentifier(), "External reference check");
//		invocation.getParameterInstances().put(ValidateDataWithExpressionFactory.VALIDATION_CODE_PARAMETER.getIdentifier(), "104");
//		return new ValidateDataWithExpression(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory,
//				descriptionEvaluatorFactory);
		
		return new ValidateDimensionWorker(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory, validationFactory);
		
	}
	
	public static Expression generateValidationExpression(OperationInvocation invocation, CubeManager cubeManager) throws InvalidInvocationException{
		try{
			Map<String,Object> params=invocation.getParameterInstances();
			ColumnReference referredColumn = (ColumnReference) params.get(TARGET_COLUMN_PARAMETER.getIdentifier());
			DataType targetDataType = cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId()).getDataType();
			ColumnReference targetReference=new ColumnReference(invocation.getTargetTableId(), invocation.getTargetColumnId(), targetDataType);
			DataType referredDataType = cubeManager.getTable(referredColumn.getTableId()).getColumnById(referredColumn.getColumnId()).getDataType();
			
			
			Expression optionalCondition =null;
			if(params.containsKey(EXTERNAL_CONDITION_PARAMETER.getIdentifier()))optionalCondition=(Expression) params.get(EXTERNAL_CONDITION_PARAMETER.getIdentifier());
			else optionalCondition=new TDBoolean(true);
			ExternalReferenceExpression externalRef=new ExternalReferenceExpression(referredColumn, optionalCondition);
			
			if (referredDataType.equals(targetDataType))
				return new ValueIsIn(targetReference, externalRef);
			else return new ValueIsIn(new Cast(targetReference, referredDataType), externalRef);
			
			
			
		}catch(Exception e){
			throw new InvalidInvocationException(invocation, "Unable to generate validation expression", e);
		}
		
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		ColumnReference codelistRef=OperationHelper.getParameter(ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER, invocation);
		Table codelist=cubeManager.getTable(codelistRef.getTableId());
		Column col=codelist.getColumnById(codelistRef.getColumnId());
		Column targetColumn=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());
		return String.format("Check if values in %s are present in %s.%s [%s]",
				OperationHelper.retrieveColumnLabel(targetColumn),OperationHelper.retrieveTableLabel(codelist),
				OperationHelper.retrieveColumnLabel(col),col.getColumnType().getName());		
	}
}

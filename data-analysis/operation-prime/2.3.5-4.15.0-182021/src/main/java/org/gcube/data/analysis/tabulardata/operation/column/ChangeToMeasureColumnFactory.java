package org.gcube.data.analysis.tabulardata.operation.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.datatype.TypeTransitionSQLHandler;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.validation.ColumnTypeCastValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

import com.google.common.collect.Lists;

public class ChangeToMeasureColumnFactory extends ChangeColumnTypeTransformationFactory {

	private static final ColumnType MANAGED_COLUMN_TYPE = new MeasureColumnType();

	private static final OperationId OPERATION_ID = new OperationId(2002);
	
	public final static List<ColumnType> allowedSourceColumnTypes = Lists.newArrayList();

	private DatabaseConnectionProvider connectionProvider;

	private CubeManager cubeManager;

	
	private WorkerFactory<ValidationWorker> fallbackFactory;

	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;

	private final static List<Parameter> parameters = new ArrayList<Parameter>();

	static {
		allowedSourceColumnTypes.add(new AnnotationColumnType());
		allowedSourceColumnTypes.add(new AttributeColumnType());
		allowedSourceColumnTypes.add(new CodeColumnType());
		allowedSourceColumnTypes.add(new CodeNameColumnType());
		allowedSourceColumnTypes.add(new CodeDescriptionColumnType());
		allowedSourceColumnTypes.add(new MeasureColumnType());

		List<Class<? extends DataType>> allowedDataTypes = new ArrayList<Class<? extends DataType>>();
		allowedDataTypes.add(IntegerType.class);
		allowedDataTypes.add(NumericType.class);
		
		
		
		//Needed by validation
		parameters.add(ColumnTypeCastValidatorFactory.TARGET_TYPE_PARAMETER);
		parameters.add(ColumnTypeCastValidatorFactory.FORMAT_ID_PARAMETER);
		parameters.add(ADDITIONAL_META_PARAMETER);
		
	}

	@Inject
	public ChangeToMeasureColumnFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider, ColumnTypeCastValidatorFactory fallbackFactory, SQLExpressionEvaluatorFactory sqlEvaluatorFactory) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.fallbackFactory = fallbackFactory;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
	}

	@Override
	protected ColumnType getManagedColumnType() {
		return MANAGED_COLUMN_TYPE;
	}

	@Override
	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkAllowedColumnTypeTransition(invocation, cubeManager);
		checkTypeTransformationEligibility(invocation);
		return new ChangeToMeasureColumn(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory);
	}

	@Override
	public boolean isRollbackable() {
		return true;
	}

	@Override
	public RollbackWorker createRollbackWoker(Table diffTable, Table createdTable,
			OperationInvocation oldInvocation) {
		return new ChangeTypeRollbackableWorker(diffTable, createdTable, oldInvocation, cubeManager, connectionProvider);
	}
	
	private void checkTypeTransformationEligibility(OperationInvocation invocation) throws InvalidInvocationException {
		Table targetTable = cubeManager.getTable(invocation.getTargetTableId());
		Column targetColumn = targetTable.getColumnById(invocation.getTargetColumnId()); 
		DataType sourceDataType = targetColumn.getDataType();
		DataType newDataType = OperationHelper.getParameter(ColumnTypeCastValidatorFactory.TARGET_TYPE_PARAMETER, invocation);
		if (TypeTransitionSQLHandler.isSupportedTransition(sourceDataType, newDataType, sqlEvaluatorFactory)) return;
		else throw new InvalidInvocationException(invocation,String.format("Unable to transform a %s column into a %s column",sourceDataType.getName(), newDataType.getName()));
	}

	@Override
	protected List<ColumnType> getAllowedSourceColumnTypes() {		
		return allowedSourceColumnTypes;
	}
	
	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@Override
	public List<WorkerFactory<ValidationWorker>> getPrecoditionValidations() {
		return Collections.singletonList(fallbackFactory);
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {		
		performBaseChecks(invocation, cubeManager);
		checkAllowedColumnTypeTransition(invocation, cubeManager);
		checkTypeTransformationEligibility(invocation);
		Column targetColumn=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());
		DataType dataType=OperationHelper.getParameter(ColumnTypeCastValidatorFactory.TARGET_TYPE_PARAMETER, invocation);
		return String.format("Set %s as %s [%s]",OperationHelper.retrieveColumnLabel(targetColumn),getManagedColumnType().getName(),dataType);
	}
}

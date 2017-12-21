package org.gcube.data.analysis.tabulardata.operation.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.time.PeriodTypeHelperProvider;
import org.gcube.data.analysis.tabulardata.operation.validation.TimeDimensionColumnValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

import com.google.common.collect.Lists;

@Singleton
public class ChangeToTimeDimensionColumnFactory extends ChangeColumnTypeTransformationFactory {

	private final static ColumnType MANAGED_COLUMN_TYPE = new TimeDimensionColumnType();

	private static final OperationId OPERATION_ID = new OperationId(2007);
	
	private final static List<Parameter> parameters = new ArrayList<Parameter>();

	private final static List<ColumnType> allowedSourceColumnTypes = Lists.newArrayList();
	
	static {
		
		allowedSourceColumnTypes.add(new CodeColumnType());
		allowedSourceColumnTypes.add(new CodeNameColumnType());
		allowedSourceColumnTypes.add(new CodeDescriptionColumnType());
		allowedSourceColumnTypes.add(new AttributeColumnType());
		allowedSourceColumnTypes.add(new AnnotationColumnType());
		
		parameters.add(TimeDimensionColumnValidatorFactory.PERIOD_FORMAT_PARAMETER);
		parameters.add(TimeDimensionColumnValidatorFactory.FORMAT_ID_PARAMETER);
	}

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private WorkerFactory<ValidationWorker> fallbackFactory;
	
	private PeriodTypeHelperProvider periodTypeHelperProvider;

	private SQLExpressionEvaluatorFactory evaluator;
	
	@Inject
	public ChangeToTimeDimensionColumnFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider,
			TimeDimensionColumnValidatorFactory fallbackFactory,PeriodTypeHelperProvider periodTypeHelperProvider, SQLExpressionEvaluatorFactory evaluator) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.fallbackFactory = fallbackFactory;
		this.periodTypeHelperProvider = periodTypeHelperProvider;
		this.evaluator = evaluator;
	}

	@Override
	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkAllowedColumnTypeTransition(invocation, cubeManager);
		checkTargetColumnEligibility(invocation);
		return new ChangeToTimeDimensionColumn(invocation, cubeManager, connectionProvider, periodTypeHelperProvider, evaluator);
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
	
	private void checkTargetColumnEligibility(OperationInvocation invocation) throws InvalidInvocationException {
		Table targetTable = cubeManager.getTable(invocation.getTargetTableId());
		Column column = targetTable.getColumnById(invocation.getTargetColumnId());
		if (!(column.getDataType() instanceof TextType))
			throw new InvalidInvocationException(invocation,
					"A text type column is needed in order to perform time dimension transformation");
	}

	@Override
	protected ColumnType getManagedColumnType() {
		return MANAGED_COLUMN_TYPE;
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
	protected List<ColumnType> getAllowedSourceColumnTypes() {
		return allowedSourceColumnTypes;
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		checkAllowedColumnTypeTransition(invocation, cubeManager);
		checkTargetColumnEligibility(invocation);
		
		Column targetColumn=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());
		String timePeriodFormat=OperationHelper.getParameter(TimeDimensionColumnValidatorFactory.PERIOD_FORMAT_PARAMETER, invocation);
				
		return String.format("Set %s as %s [%s]",OperationHelper.retrieveColumnLabel(targetColumn),getManagedColumnType().getName(),timePeriodFormat);
	}
}

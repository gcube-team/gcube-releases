package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class DuplicateValuesInColumnValidatorFactory extends ColumnValidatorFactory {

	private static final List<Parameter> parameters = new ArrayList<Parameter>();
	
	private static final OperationId OPERATION_ID = new OperationId(5004);
	
	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private SQLExpressionEvaluatorFactory evaluatorfactory;
	
	
	@Inject
	public DuplicateValuesInColumnValidatorFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,SQLExpressionEvaluatorFactory factory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorfactory=factory;
	}

	@Override
	public ValidationWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new DuplicateValuesInColumnValidator(invocation, cubeManager, connectionProvider,evaluatorfactory);
	}

	@Override
	protected String getOperationName() {
		return "Duplicate Values in Column Validator";
	}

	@Override
	protected String getOperationDescription() {
		return "Checks for duplicate values in selected column.";
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
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		Column col=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());
		return String.format("Check if %s is unique",OperationHelper.retrieveColumnLabel(col));
	}
}

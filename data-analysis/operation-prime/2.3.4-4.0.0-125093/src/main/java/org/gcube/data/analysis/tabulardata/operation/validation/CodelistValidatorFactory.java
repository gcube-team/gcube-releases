package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class CodelistValidatorFactory extends TableValidatorFactory{

	private static final OperationId OPERATION_ID = new OperationId(5001);
	
	private static final List<Parameter> parameters = new ArrayList<Parameter>();
	
	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private DuplicateValuesInColumnValidatorFactory duplicateInColumnFactory;
	private ValidateDataWithExpressionFactory validateDataWithExpressionFactory;
	private DuplicateRowValidatorFactory duplicateRowsFactory;
	
	
	
	@Inject
	public CodelistValidatorFactory(
			CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			DuplicateValuesInColumnValidatorFactory duplicateInColumnFactory,
			ValidateDataWithExpressionFactory validateDataWithExpressionFactory,
			DuplicateRowValidatorFactory duplicateRowsFactory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.duplicateInColumnFactory = duplicateInColumnFactory;
		this.validateDataWithExpressionFactory = validateDataWithExpressionFactory;
		this.duplicateRowsFactory = duplicateRowsFactory;
	}

	@Override
	public ValidationWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new CodelistValidator(invocation, cubeManager, connectionProvider,
				duplicateInColumnFactory,
				validateDataWithExpressionFactory,
				duplicateRowsFactory);
	}

	@Override
	protected String getOperationName() {
		return "Codelist validation";
	}

	@Override
	protected String getOperationDescription() {
		return "Validates the selected table against all constraints defined for a Codelist.";
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
		Table targetTable=cubeManager.getTable(invocation.getTargetTableId());
		return String.format("Check if %s is a valid codelist.",OperationHelper.retrieveTableLabel(targetTable));
	}
}

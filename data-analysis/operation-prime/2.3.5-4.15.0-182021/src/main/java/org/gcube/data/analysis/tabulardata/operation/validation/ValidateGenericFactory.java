package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public class ValidateGenericFactory extends TableValidatorFactory {
	private static final OperationId OPERATION_ID = new OperationId(5013);
	
	private static final List<Parameter> parameters = new ArrayList<Parameter>();
	
	@Inject
	private CubeManager cubeManager;

	@Override
	public ValidationWorker createWorker(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0, cubeManager);
		return new ValidateGeneric(arg0, cubeManager);
	}

	@Override
	protected String getOperationDescription() {
		return "Check if the table is a valid generic table";
	}

	@Override
	protected String getOperationName() {
		return "Validate Generic";
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

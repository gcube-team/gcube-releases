package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class ValidateDatasetFactory extends TableValidatorFactory {

	private static final OperationId OPERATION_ID = new OperationId(5012);
	
	private static final List<Parameter> parameters = new ArrayList<Parameter>();
	
	@Inject
	private CubeManager cubeManager;

	@Override
	public ValidationWorker createWorker(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0, cubeManager);
		return new ValidateDataset(arg0, cubeManager);
	}

	@Override
	protected String getOperationDescription() {
		return "Check if the table is a valid dataset";
	}

	@Override
	protected String getOperationName() {
		return "Validate Dataset";
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
		return String.format("Check if %s is a valid dataset.",OperationHelper.retrieveTableLabel(targetTable));
	}
}

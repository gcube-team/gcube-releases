package org.gcube.data.analysis.tabulardata.operation.utils;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.gcube.common.database.DatabaseProvider;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.ColumnScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.IntegerParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

public class GuesserWorkerFactory extends ColumnScopedWorkerFactory<ResourceCreatorWorker> {
	
	public static final IntegerParameter CODELISTS_PARAMETER = new IntegerParameter("codelists", "Codelists", "List of codelists", new Cardinality(0, Integer.MAX_VALUE));
		
	DatabaseProvider configurator;
	CubeManager cubeManager;
	
	
	
	@Override
	protected OperationType getOperationType() {
	 return OperationType.RESOURCECREATOR;
	}

	@Override
	public Class<ResourceCreatorWorker> getWorkerType() {
		return ResourceCreatorWorker.class;
	}

	@Inject
	public GuesserWorkerFactory(DatabaseProvider configurator,
			CubeManager cubeManager) {
		super();
		this.configurator = configurator;
		this.cubeManager = cubeManager;
	}

	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		return new GuesserWorker(invocation, configurator, cubeManager);
	}
	
	@Override
	protected OperationId getOperationId() {
		return new OperationId(11002);
	}

	@Override
	protected String getOperationName() {
		return "Guesser";
	}

	@Override
	protected String getOperationDescription() {
		return "Guess all possible relation to external table";
	}

	@Override
	protected List<Parameter> getParameters() {
		return Collections.singletonList((Parameter)CODELISTS_PARAMETER);
	}


}

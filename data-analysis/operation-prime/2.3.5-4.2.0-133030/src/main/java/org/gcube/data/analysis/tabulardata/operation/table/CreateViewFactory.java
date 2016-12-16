package org.gcube.data.analysis.tabulardata.operation.table;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableMetadataWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;

import com.google.common.collect.Lists;

@Singleton
public class CreateViewFactory extends TableMetadataWorkerFactory {

	private static final OperationId OPERATION_ID = new OperationId(1003);

	private static final List<Parameter> parameters = Lists.newArrayList();

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	@Inject
	public CreateViewFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}

	public CreateView createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new CreateView(cubeManager, connectionProvider, invocation);
	}

	@Override
	protected String getOperationName() {
		return "Create dataset view";
	}

	@Override
	protected String getOperationDescription() {
		return "Create a dataset materialized view with data from linked codelists";
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters; 
	}

	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {		
		return "Create a dataset materialized view with data from linked codelists";
	}
}

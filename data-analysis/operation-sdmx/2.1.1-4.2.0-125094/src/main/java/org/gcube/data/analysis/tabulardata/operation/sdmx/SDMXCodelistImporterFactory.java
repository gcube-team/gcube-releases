package org.gcube.data.analysis.tabulardata.operation.sdmx;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ImportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.RegexpStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;


@Singleton
public class SDMXCodelistImporterFactory extends ImportWorkerFactory {

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private static OperationId operationId = new OperationId(200);

	private static List<Parameter> parameters = new ArrayList<Parameter>();

	static {
		createParameters();
	}

	@Inject
	public SDMXCodelistImporterFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		if (cubeManager == null)
			throw new IllegalArgumentException("cubeManager cannot be null");
		if (connectionProvider == null)
			throw new IllegalArgumentException("connectionProvider cannot be null");
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}

	private static void createParameters() {
		parameters.add(new RegexpStringParameter(WorkerUtils.REGISTRY_BASE_URL, "Registry Base URL",
				"URL that points to the registry REST base endpoint", Cardinality.ONE,
				"^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"));
		parameters.add(new RegexpStringParameter(WorkerUtils.AGENCY, "Agency", "SDMX Agency", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(WorkerUtils.ID, "Id", "SDMX Codelist id", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(WorkerUtils.VERSION, "Version", "SDMX Codelist version", Cardinality.ONE,
				"[0-9]+(\\.[0-9]+)?"));
	}

	@Override
	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new SDMXCodelistImporter(cubeManager, connectionProvider, invocation);
	}

	@Override
	protected String getOperationName() {
		return "SDMX Codelist import";
	}

	@Override
	protected String getOperationDescription() {
		return "Import a codelist from a SDMX registry";
	}

	@Override
	protected OperationId getOperationId() {
		return operationId;
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

}

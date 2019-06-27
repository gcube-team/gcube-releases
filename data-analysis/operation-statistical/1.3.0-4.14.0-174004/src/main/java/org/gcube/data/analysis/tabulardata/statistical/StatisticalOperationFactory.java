package org.gcube.data.analysis.tabulardata.statistical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableResourceCreatorWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MapParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class StatisticalOperationFactory extends TableResourceCreatorWorkerFactory {
	public static final Logger log = LoggerFactory.getLogger(StatisticalOperationFactory.class);

	private static OperationId OPERATION_ID = new OperationId(10001);

	public static MapParameter SM_ENTRIES = new MapParameter(Constants.OPERATOR_KEY, "Operator Holder",
			"This map is expected to contain only one Operator object under key " + Constants.OPERATOR_KEY,
			Cardinality.ONE, String.class, Operator.class, Collections.singletonList(Constants.OPERATOR_KEY));

	public static BooleanParameter CLEAR_DATASPACE = new BooleanParameter("clear", "Clear Dataspace",
			"Remove all generated resources from dataspace", Cardinality.OPTIONAL);
	public static BooleanParameter REMOVE_EXPORTED = new BooleanParameter("remove", "Remove Exported",
			"Remove table from dataspace", Cardinality.OPTIONAL);

	private static List<Parameter> parameters = new ArrayList<Parameter>();

	static {
		parameters.add(SM_ENTRIES);
		parameters.add(CLEAR_DATASPACE);
		parameters.add(REMOVE_EXPORTED);
		parameters.add(ExportToStatisticalOperationFactory.toEscapeFieldNamesParam);
	}

	private ExportToStatisticalOperationFactory exportFactory;
	private ImportFromStatisticalOperationFactory importFactory;
	private CubeManager cubeManager;

	@Inject
	public StatisticalOperationFactory(ExportToStatisticalOperationFactory exportFactory,
			ImportFromStatisticalOperationFactory importFactory, CubeManager cubeManager) {
		super();
		this.exportFactory = exportFactory;
		this.importFactory = importFactory;
		this.cubeManager = cubeManager;
	}

	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		// check service availability
		SClient dmClient = null;

		try {
			dmClient = Common.getDMClient();
		} catch (Exception e) {
			throw new InvalidInvocationException(invocation, Constants.SERVICE_NOT_FOUND, e);
		}

		// check SM Parameters
		checkSMParameters(dmClient, invocation);

		// check StorageHub availability
		try {
			log.info("Check User Home by StorageHub");
			StorageHubClient shc = new StorageHubClient();
			shc.getWSRoot();
		} catch (Exception e) {
			throw new InvalidInvocationException(invocation, "Unable to contact workspace root of user by StorageHub", e);
		}

		// return worker

		return new StatisticalOperation(invocation, dmClient, exportFactory, importFactory, cubeManager);

	}

	private static void checkSMParameters(SClient dmClient, OperationInvocation invocation)
			throws InvalidInvocationException {
		try {
			Operator operator = Common.getOperator(invocation);
			// check algorithm presence in context
			dmClient.getOperatorById(operator.getId());

			// check parameters in operator
			Set<String> passedParametersName = new HashSet<>();
			for (org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter param : operator
					.getOperatorParameters()) {
				passedParametersName.add(param.getName());
			}

			for (org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter param : dmClient
					.getInputParameters(operator)) {
				if (!passedParametersName.contains(param.getName()))
					throw new InvalidInvocationException(invocation,
							"Expected parameter " + param.getName() + " not specified in operator.");
			}

		} catch (InvalidInvocationException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidInvocationException(invocation,
					"Unable to check parameters for selected algorithm : " + e.getMessage());
		}
	}

	@Override
	protected String getOperationName() {
		return "Statistical Operation";
	}

	@Override
	protected String getOperationDescription() {
		return "Execute a Statistical Manager experiment against the selected target table";
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
	public String describeInvocation(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		String algorithmId = Common.getOperator(invocation).getName();
		return String.format("Execute %s Algorithm", algorithmId);
	}

	@Override
	protected OperationType getOperationType() {
		return OperationType.RESOURCECREATOR;
	}
}

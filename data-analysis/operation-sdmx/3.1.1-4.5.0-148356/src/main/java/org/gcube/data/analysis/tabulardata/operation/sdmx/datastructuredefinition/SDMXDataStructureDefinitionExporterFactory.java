package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ExportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.RegexpStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;


@Singleton
public class SDMXDataStructureDefinitionExporterFactory extends ExportWorkerFactory {

	private static final OperationId operationId = new OperationId(203);

	private static final List<Parameter> parameters = new ArrayList<Parameter>();

	CubeManager cubeManager;

	DatabaseConnectionProvider connectionProvider;

	static {
		parameters.add(new RegexpStringParameter(DataStructureDefinitionWorkerUtils.REGISTRY_BASE_URL, "Registry REST URL",
				"Target SDMX Registry REST Service base URL", Cardinality.ONE,
				"^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"));
		parameters.add(new RegexpStringParameter(DataStructureDefinitionWorkerUtils.AGENCY, "Agency", "SDMX Agency", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(DataStructureDefinitionWorkerUtils.ID, "Id", "SDMX DSD id", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(DataStructureDefinitionWorkerUtils.VERSION, "Version", "SDMX Data set version", Cardinality.ONE,
				"[0-9]+(\\.[0-9]+)?"));
		parameters.add(new RegexpStringParameter(DataStructureDefinitionWorkerUtils.OBS_VALUE_COLUMN, "Observation Value", "Observation Value column", Cardinality.ONE,
				"[0-9a-z-]+"));
	}

	@Inject
	public SDMXDataStructureDefinitionExporterFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}

//	@SuppressWarnings("unchecked")
//	private void checkTargetTableEligibility(OperationInvocation invocation) throws InvalidInvocationException {
//		Table table = cubeManager.getTable(invocation.getTargetTableId());
//		if (!table.getTableType().equals(new DatasetTableType()))
//			throw new InvalidInvocationException(invocation,"The table is not a dataset");
//		if (table.getColumnsByType(DimensionColumnType.class).isEmpty())
//			throw new InvalidInvocationException(invocation,"The table does not have any dimension");
//	}

	@SuppressWarnings("unchecked")
	private void checkTargetTableEligibility(OperationInvocation invocation) throws InvalidInvocationException {
		Table table = cubeManager.getTable(invocation.getTargetTableId());
	
		
		if (!table.getTableType().equals(new DatasetTableType()))
			throw new InvalidInvocationException(invocation,"The table is not a dataset");
		if (table.getColumnsByType(DimensionColumnType.class).isEmpty())
			throw new InvalidInvocationException(invocation,"The table does not have any dimension");
		if (table.getColumnsByType(TimeDimensionColumnType.class).isEmpty())
			throw new InvalidInvocationException(invocation,"The table does not have any time dimension");
	}

	
	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		//System.out.println("Cube Manager "+cubeManager.getClass());

		performBaseChecks(invocation,cubeManager);
		checkTargetTableEligibility(invocation);
		
		Table table = cubeManager.getTable(invocation.getTargetTableId());

		return new SDMXDataStructureDefinitionExporter(table, this.connectionProvider,invocation,this.cubeManager);
	}

	@Override
	protected String getOperationName() {
		return "Export Data Structure Definition and Concepts to SDMX registry";
	}

	@Override
	protected String getOperationDescription() {
		return "Retrieve the Data Structure Deginition and the Concepts among the metadata of the Table and exports them to the remote SDMX registry";
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

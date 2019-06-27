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
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.executors.SDMXExcelGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.resource.SDMXDataStructureDefinitionExcelResourceBuilder;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class SDMXDataStructureDefinitionExcelExporterFactory extends ExportWorkerFactory {

	private Logger logger;
	private static final OperationId operationId = new OperationId(405);

	
	private static final List<Parameter> parameters = new ArrayList<Parameter>();

	CubeManager cubeManager;

	DatabaseConnectionProvider connectionProvider;

	static {

		parameters.add(new RegexpStringParameter(DataStructureDefinitionWorkerUtils.AGENCY, "Agency", "SDMX Agency", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(DataStructureDefinitionWorkerUtils.ID, "Id", "SDMX DSD id", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(DataStructureDefinitionWorkerUtils.VERSION, "Version", "SDMX Data set version", Cardinality.ONE,
				"[0-9]+(\\.[0-9]+)?"));
		parameters.add(new RegexpStringParameter(DataStructureDefinitionWorkerUtils.OBS_VALUE_COLUMN, "Observation Value", "Observation Value column", Cardinality.ONE,
				"[0-9a-z-]+"));
	}

	@Inject
	public SDMXDataStructureDefinitionExcelExporterFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.logger.debug("Generating DSD Factory");
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
		this.logger.debug("Generating the worker");
		performBaseChecks(invocation,cubeManager);
		checkTargetTableEligibility(invocation);
		this.logger.debug("Check completed");
		Table table = cubeManager.getTable(invocation.getTargetTableId());
		this.logger.debug("Table generated");
		SDMXDataOperationManagerBuilder dataOperationManagerBuilder = new SDMXDataOperationManagerBuilder();
		dataOperationManagerBuilder.setTable(table);
		dataOperationManagerBuilder.setConnectionProvider(this.connectionProvider);
		dataOperationManagerBuilder.setInvocation(invocation);
		dataOperationManagerBuilder.setCubeManager(cubeManager);
		dataOperationManagerBuilder.addExecutor(new SDMXExcelGenerator(true));
		dataOperationManagerBuilder.setResourceBuilder(new SDMXDataStructureDefinitionExcelResourceBuilder());
		return dataOperationManagerBuilder.build();
	}

	@Override
	protected String getOperationName() {
		this.logger.debug("Get operation name");
		return "Export Data Structure Definition and Concepts to SDMX registry";
	}

	@Override
	protected String getOperationDescription() {
		this.logger.debug("Get operation description");
		return "Retrieve the Data Structure Deginition and the Concepts among the metadata of the Table and exports them to the remote SDMX registry";
	}

	@Override
	protected OperationId getOperationId() {
		this.logger.debug("Get operation id");
		return operationId;
	}

	@Override
	protected List<Parameter> getParameters() {
		this.logger.debug("Get operation parameters");
		return parameters;
	}
	



	

}

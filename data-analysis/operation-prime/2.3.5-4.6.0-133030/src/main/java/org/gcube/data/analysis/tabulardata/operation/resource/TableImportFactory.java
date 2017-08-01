package org.gcube.data.analysis.tabulardata.operation.resource;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ImportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetTableParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class TableImportFactory extends ImportWorkerFactory {

	public static final TargetTableParameter targetTableImportParameter = new TargetTableParameter("table", "Table", "the table to import", Cardinality.ONE);
	public static final BooleanParameter useExistingTableParameter = new BooleanParameter("useExistingTable", "useExistingTable", "false if a copy of the table should be created (default false)", Cardinality.OPTIONAL);
	private CubeManager cubeManager;
	
	@Inject
	public TableImportFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider) {
		if (cubeManager == null)
			throw new IllegalArgumentException("cubeManager cannot be null");
		if (connectionProvider == null)
			throw new IllegalArgumentException("connectionProvider cannot be null");
		this.cubeManager = cubeManager;
	}
	
	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		checkExisting(invocation);
		return new TableImport(invocation, cubeManager);
	}
	
	private void checkExisting(OperationInvocation invocation) throws InvalidInvocationException{
		Boolean existingTable = (Boolean)invocation.getParameterInstances().get(useExistingTableParameter.getIdentifier());
		if (existingTable!=null && existingTable){
			TableId tableId = (TableId) invocation.getParameterInstances().get(targetTableImportParameter.getIdentifier());
			Table table = cubeManager.getTable(tableId);
			if (table.contains(TableDescriptorMetadata.class))
				throw new InvalidInvocationException(invocation, String.format("table with id %s is already linked to a tabular resource", tableId));
		}
	}

	@Override
	protected String getOperationName() {
		return "Table Import";
	}
	
	@Override
	protected OperationId getOperationId() {
		return new OperationId(102);
	}

	@Override
	protected String getOperationDescription() {
		return "Import from an existing table";
	}

	@Override
	protected List<Parameter> getParameters() {
		return Arrays.asList((Parameter)targetTableImportParameter, (Parameter) useExistingTableParameter);
	}

}

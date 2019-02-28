package org.gcube.data.analysis.tabulardata.operation.sdmx.codelist;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ExportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.RegexpStringParameter;
import org.gcube.data.analysis.tabulardata.operation.sdmx.WorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

@Singleton
public class SDMXCodelistExporterFactory extends ExportWorkerFactory {

	private static final OperationId operationId = new OperationId(201);

	private static final List<Parameter> parameters = new ArrayList<Parameter>();

	CubeManager cubeManager;

	DatabaseConnectionProvider connectionProvider;

	static {
		parameters.add(new RegexpStringParameter(WorkerUtils.REGISTRY_BASE_URL, "Registry REST URL",
				"Target SDMX Registry REST Service base URL", Cardinality.ONE,
				"^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"));
		parameters.add(new RegexpStringParameter(WorkerUtils.AGENCY, "Agency", "SDMX Agency", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(WorkerUtils.ID, "Id", "SDMX Codelist id", Cardinality.ONE, "[A-z0-9_-]+"));
		parameters.add(new RegexpStringParameter(WorkerUtils.VERSION, "Version", "SDMX Codelist version", Cardinality.ONE,
				"[0-9]+(\\.[0-9]+)?"));
	
	}

	@Inject
	public SDMXCodelistExporterFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}

	@SuppressWarnings("unchecked")
	private void checkTargetTableEligibility(OperationInvocation invocation) throws InvalidInvocationException {
		Table table = cubeManager.getTable(invocation.getTargetTableId());
		if (!table.getTableType().equals(new CodelistTableType()))
			throw new InvalidInvocationException(invocation,"The table is not a codelist");
		if (table.getColumnsByType(CodeColumnType.class).isEmpty())
			throw new InvalidInvocationException(invocation,"The table does not have a Code column");
		if (table.getColumnsByType(CodeNameColumnType.class).isEmpty())
			throw new InvalidInvocationException(invocation,"The table does not have Code name columns");
		List<Column> columnsToCheckForDataLocale = table.getColumnsByType(CodeNameColumnType.class, CodeDescriptionColumnType.class, AnnotationColumnType.class);
		try {
			for (Column column : columnsToCheckForDataLocale) {
				column.getMetadata(DataLocaleMetadata.class);
			}
		} catch (NoSuchMetadataException e) {
			throw new InvalidInvocationException(invocation,"A column is missing data locale metadata");
		}
	}

	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkTargetTableEligibility(invocation);
		Table table = cubeManager.getTable(invocation.getTargetTableId());
		return new SDMXCodelistExporter(table, invocation, connectionProvider);
	}

	@Override
	protected String getOperationName() {
		return "Export Codelist to SDMX registry";
	}

	@Override
	protected String getOperationDescription() {
		return "Export a tabular data codelist to a remote SDMX registry";
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

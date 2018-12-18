package org.gcube.data.analysis.tabulardata.operation.export.csv.exporter;

import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.*;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.export.StorageResourceRemoverWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.RegexpStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

import com.google.common.collect.Lists;

public class CSVExportFactory extends StorageResourceRemoverWorkerFactory{

	private static final String OPERATION_NAME = "CSV Export";
	
	private static final OperationId OPERATION_ID = new OperationId(101);
	
	CubeManager cubeManager;
	
	DatabaseConnectionProvider connectionProvider;

	private static RegexpStringParameter separatorParameter= new RegexpStringParameter(SEPARATOR, "Separator", "Char separator", Cardinality.ONE, "\\W$");
	
	private static RegexpStringParameter encodingParameter = new RegexpStringParameter(ENCODING, "Encoding", "Document Encoding", Cardinality.ONE, ".*");

	private static RegexpStringParameter columnsParameter = new RegexpStringParameter(COLUMNS, "Columns", "Selected Columns", new Cardinality(1,Integer.MAX_VALUE) , ".*");
	
	private static SimpleStringParameter resourceName = new SimpleStringParameter(RESOURCE_NAME, "Name", "Resource name", Cardinality.OPTIONAL);
	
	private static SimpleStringParameter resourceDescription = new SimpleStringParameter(RESOURCE_DESCRIPTION, "Description", "Resource description", Cardinality.OPTIONAL);
	
	private static BooleanParameter viewExportParameter = new BooleanParameter(VIEW, "View Columns", "export the related view column", Cardinality.OPTIONAL);

	
	@Inject
	public CSVExportFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider) {
		if (cubeManager == null)
			throw new IllegalArgumentException("cubeManager cannot be null");
		if (connectionProvider == null)
			throw new IllegalArgumentException("connectionProvider cannot be null");
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}
	
	public ResourceCreatorWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		return new CSVExport(invocation, cubeManager, connectionProvider);
	}

	@Override
	protected String getOperationName() {
		return OPERATION_NAME;
	}

	@Override
	protected String getOperationDescription() {
		return "Export a table to a CSV file";
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	
	@Override
	public List<Parameter> getParameters(){
		List<Parameter> parameters = Lists.newArrayList();
		Collections.addAll(parameters, separatorParameter, encodingParameter, columnsParameter, viewExportParameter, resourceName, resourceDescription);
		return parameters;
	}

	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		return getOperationDescription();
	}


}

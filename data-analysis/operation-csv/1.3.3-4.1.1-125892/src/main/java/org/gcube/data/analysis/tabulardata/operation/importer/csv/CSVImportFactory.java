package org.gcube.data.analysis.tabulardata.operation.importer.csv;

import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.ENCODING;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.FIELDMASK;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.HASHEADER;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.SEPARATOR;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.URL;

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
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.RegexpStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

import com.google.common.collect.Lists;

@Singleton
public class CSVImportFactory extends ImportWorkerFactory {

	//private static final OperationId OPERATION_ID = new OperationId(100);

	private static final List<Parameter> parameters = Lists.newArrayList();

	private static OperationId operationId = new OperationId(100);
	
	private static RegexpStringParameter urlParameter =  new RegexpStringParameter(URL, "Document URL",
			"URL that points to a location where the document can be downloaded.", Cardinality.ONE,
			".*");
	
	private static RegexpStringParameter separatorParameter= new RegexpStringParameter(SEPARATOR, "Separator", "Char separator as string", Cardinality.ONE, "^\\W$");
	
	private static RegexpStringParameter encodingParameter = new RegexpStringParameter(ENCODING, "Encoding", "Document Encoding", Cardinality.ONE, ".*");
	
	private static BooleanParameter headerParameter = new BooleanParameter(HASHEADER, "Header", "Tells if the document has header or not",
			Cardinality.ONE);

	private static BooleanParameter fieldMaskParameter = new BooleanParameter(FIELDMASK, "FieldMask", "Filter for imported columns",
			new Cardinality(0, Integer.MAX_VALUE));
	
	
	static {
		parameters.add(urlParameter);
		parameters.add(separatorParameter);
		parameters.add(encodingParameter);
		parameters.add(headerParameter);
		parameters.add(fieldMaskParameter);
	}

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;
	
	@Inject
	public CSVImportFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider) {
		if (cubeManager == null)
			throw new IllegalArgumentException("cubeManager cannot be null");
		if (connectionProvider == null)
			throw new IllegalArgumentException("connectionProvider cannot be null");
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}

	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation, cubeManager);
		return new CSVImport(invocation, cubeManager, connectionProvider);
	}

	
	
	@Override
	protected String getOperationName() {
		return "CSV Import";
	}

	@Override
	protected String getOperationDescription() {
		return "Create a new table with a CSV file";
	}

	@Override
	protected OperationId getOperationId() {
		return operationId;
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	public String describeInvocation(OperationInvocation arg0)
			throws InvalidInvocationException {
		return getOperationDescription();
	}

	
	
}

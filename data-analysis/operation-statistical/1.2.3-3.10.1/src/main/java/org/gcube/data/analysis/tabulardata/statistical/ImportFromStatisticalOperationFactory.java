package org.gcube.data.analysis.tabulardata.statistical;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ImportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.importer.csv.CSVImportFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;


@Singleton
public class ImportFromStatisticalOperationFactory extends ImportWorkerFactory{

	
	private static OperationId OPERATION_ID = new OperationId(10003);

	private static List<Parameter> parameters = new ArrayList<Parameter>();
	
	public static final SimpleStringParameter RESOURCE_ID=new SimpleStringParameter("resourceId", "Resource id", "The table id inside the dataspace", Cardinality.ONE);

	public static final SimpleStringParameter RESOURCE_NAME=new SimpleStringParameter("resourceName", "Resource Name", "The label to be given to the table", Cardinality.OPTIONAL);
	
	public static final BooleanParameter DELETE_REMOTE_RESOURCE=new BooleanParameter("delete_remote", "Delete remote", "True to delete remote resource after importing", Cardinality.OPTIONAL);
	
	static{
		parameters.add(RESOURCE_ID);
		parameters.add(RESOURCE_NAME);
		parameters.add(DELETE_REMOTE_RESOURCE);
	}
	
	private CubeManager cubeManager;	
	private CSVImportFactory csvImportFactory;
	
	@Inject
	public ImportFromStatisticalOperationFactory(CubeManager cubeManager,
			CSVImportFactory csvImportFactory) {
		super();
		this.cubeManager = cubeManager;
		this.csvImportFactory = csvImportFactory;
	}
	
	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		StatisticalManagerDataSpace dataSpace =null;
		try{
			dataSpace = Common.getSMDataSpace();
		}catch (Exception e){
			throw new InvalidInvocationException(invocation,Constants.SERVICE_NOT_FOUND,e);
		}
		IClient client=new StorageClient("StatisticalOperations", "StatisticalOperations", "service", AccessType.SHARED).getClient();
		return new ImportFromStatisticalOperation(cubeManager,invocation, dataSpace, csvImportFactory, client);
	}
	
	@Override
	protected String getOperationName() {
		return "Import from Statistical";
	}

	@Override
	protected String getOperationDescription() {
		return "Import the selected resource as CSV generic table.";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}
	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
}

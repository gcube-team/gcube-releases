package org.gcube.data.analysis.tabulardata.operation.comet;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableMetadataWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ImportCodeListMappingFactory extends  TableMetadataWorkerFactory{
	
	private static final OperationId OPERATION_ID=new OperationId(12001);
	
	private static final Logger logger = LoggerFactory.getLogger(ImportCodeListMappingFactory.class);
	
	public static SimpleStringParameter ID_PARAMETER =  new SimpleStringParameter("id", "Document id",
			"Storage ID of the mapping file", Cardinality.ONE);
	
	public static TargetColumnParameter PREVIOUS_VERSION_CODELIST_PARAMETER=
			new TargetColumnParameter("old_codes", "Code column before curation", "The code column of the previous version of this codelist, from which the mapping is generated", 
					Cardinality.ONE);
	
	
	private static final List<Parameter> parameters=Arrays.asList(new Parameter[]{
			ID_PARAMETER,
			PREVIOUS_VERSION_CODELIST_PARAMETER
	});
	
	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	
		
	@Inject
	public ImportCodeListMappingFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory evaluatorFactory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorFactory = evaluatorFactory;
	}





	@Override
	public MetadataWorker createWorker(OperationInvocation arg0)
			throws InvalidInvocationException {
		performBaseChecks(arg0, cubeManager);
		String mappingId=OperationHelper.getParameter(ID_PARAMETER, arg0.getParameterInstances());
		try{
			getInputStreamById(mappingId);		
		}catch(Exception e){
			logger.debug("Incorrect uri : "+mappingId,e);
			throw new InvalidInvocationException(arg0, "Invalid smp id "+mappingId);
		}
		return new ImportCodelistMappingWorker(arg0, cubeManager, connectionProvider, evaluatorFactory);
	}
	
	static InputStream getInputStreamById(String smpID)throws Exception{
			StorageClient client = new StorageClient(ImportCodeListMappingFactory.class.getName(), ImportCodeListMappingFactory.class.getSimpleName(), ImportCodeListMappingFactory.class.getName(), AccessType.PUBLIC);
			IClient icClient = client.getClient();
			return icClient.get().RFileAsInputStream(smpID);
	}
	
	
	
	@Override
	protected String getOperationDescription() {
		return "Imports a comet file and infers harmonization rules.";
	}

	@Override
	protected String getOperationName() {
		return "Import comet mapping";
	}
	
	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}


	@Override
	protected OperationType getOperationType() {
		return OperationType.RESOURCECREATOR;
	}
	
	@Override
	public OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	
}
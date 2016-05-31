package org.gcube.data.analysis.tabulardata.statistical;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

import org.apache.commons.io.IOUtils;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationConfig;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMAbstractResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntries;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMInputEntry;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMOperationInfo;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GcubeServiceReferenceMetadata;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.export.Utils;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StatisticalOperation extends ResourceCreatorWorker {

	private static Logger logger = LoggerFactory.getLogger(StatisticalOperation.class);

	private StatisticalManagerFactory statisticalManagerFactory;
	private StatisticalManagerDataSpace dataSpace;
	private ExportToStatisticalOperationFactory exportFactory;
	private ImportFromStatisticalOperationFactory importFactory;
	private Home home;
	private CubeManager cubeManager;

	public StatisticalOperation(OperationInvocation sourceInvocation,
			StatisticalManagerFactory statisticalManagerFactory,
			ExportToStatisticalOperationFactory exportFactory,
			ImportFromStatisticalOperationFactory importFactory, Home home, CubeManager cubeManager) {
		super(sourceInvocation);
		this.statisticalManagerFactory = statisticalManagerFactory;
		this.exportFactory = exportFactory;
		this.importFactory = importFactory;
		this.cubeManager = cubeManager;
		this.home=home;

	}

	// Invocation parameters
	private Map<String, Object> algorithmParameters;
	private String user;
	private String algorithmId;
	private String experimentTitle = null;
	private String experimentDescription = null;
	private Table targetTable;

	private String dataSpaceTableId;

	private boolean clearDataspace=false;
	private boolean removeExported=false;
	//	private Table resultTable;



	private Map<String,String> toSerializeValues=new HashMap<String,String>();


	//	private ArrayList<Table> collaterals=new ArrayList<Table>();

	private List<ResourceDescriptorResult> results=new ArrayList<ResourceDescriptorResult>();

	// Execution

	String computationId;

	@Override
	protected ResourcesResult execute() throws WorkerException,OperationAbortedException {
		try{
			getParameters();
			updateProgress(0.05f,"Checking if already exported to dataspace..");		
			dataSpaceTableId=getSMTable();
			checkAborted();
			updateProgress(0.2f,"Submitting computation");
			submitComputation();
			checkAborted();
			updateProgress(0.4f,"Waiting computation");
			waitForComputation();
			checkAborted();
			updateProgress(0.6f,"Importing results");
			generateTableFromResult();
			if(removeExported){
				updateProgress(0.8f,"Cleaning up");
				deleteExternalReference();
			}
			updateProgress(0.9f,"Finalizing");
			if(results.size()==0) throw new WorkerException("No resources were successfully imported from SM");
			return new ResourcesResult(results);
		}catch(WorkerException e){
			throw e;
		}catch(OperationAbortedException e){
			throw e;
		}catch(Exception e){
			throw new WorkerException("Unexpected internal error. Please contact support",e);
		}
	}

	private void serializeResultMap(){
		if(!toSerializeValues.isEmpty()){
			PrintWriter writer=null;
			try{
				File serialized=File.createTempFile("result", ".txt");
				writer=new PrintWriter(serialized);
				for(Entry<String,String> entry:toSerializeValues.entrySet())
					writer.println(entry.getKey()+" : "+entry.getValue());
				IOUtils.closeQuietly(writer);				
				IClient client = Utils.getStorageClient();
				String remotePath = "/SM_Integration/"+serialized.getName();
				client.put(true).LFile(serialized.getAbsolutePath()).RFile(remotePath);
				String id = client.put(true).LFile(serialized.getAbsolutePath()).RFile(remotePath);

				results.add(new ImmutableURIResult(
						new InternalURI(new URI(id),"text/plain"), (experimentTitle == null ? algorithmId
								: experimentTitle)+" General results", "Text file containig etherogeneus results", ResourceType.GENERIC_FILE));
			}catch(Exception e){
				logger.warn("Unable to write results to file ",e);
			}finally{
				if(writer!=null) IOUtils.closeQuietly(writer);
			}
		}

	}


	public void getParameters() {
		Map<String, Object> params = getSourceInvocation()
				.getParameterInstances();
		algorithmParameters = (Map<String, Object>) params
				.get(StatisticalOperationFactory.SM_ENTRIES.getIdentifier());
		user = (String) params.get(StatisticalOperationFactory.USER
				.getIdentifier());
		algorithmId = (String) params.get(StatisticalOperationFactory.ALGORITHM
				.getIdentifier());
		if (params.containsKey(StatisticalOperationFactory.TITLE
				.getIdentifier()))
			experimentTitle = (String) params
			.get(StatisticalOperationFactory.TITLE.getIdentifier());
		if (params.containsKey(StatisticalOperationFactory.DESCRIPTION
				.getIdentifier()))
			experimentDescription = (String) params
			.get(StatisticalOperationFactory.DESCRIPTION
					.getIdentifier());
		targetTable = cubeManager.getTable(getSourceInvocation()
				.getTargetTableId());

		try{
			clearDataspace=OperationHelper.getParameter(StatisticalOperationFactory.CLEAR_DATASPACE, getSourceInvocation());
		}catch(Throwable t){
			// noty specified
		}
		try{
			removeExported=OperationHelper.getParameter(StatisticalOperationFactory.REMOVE_EXPORTED, getSourceInvocation());
		}catch(Throwable t){
			// noty specified
		}
	}

	public void submitComputation() throws ParseException, IOException, ProcessingException {
		SMComputationRequest request = new SMComputationRequest();
		SMComputationConfig config = new SMComputationConfig();
		Table toUseTable=targetTable;
		if(toUseTable.contains(DatasetViewTableMetadata.class)){
			DatasetViewTableMetadata dsMeta = toUseTable.getMetadata(DatasetViewTableMetadata.class);
			toUseTable= cubeManager.getTable(dsMeta.getTargetDatasetViewTableId());
		}

		Map<ColumnLocalId,String> curatedColumnLabels=Common.curateLabels(toUseTable);


		List<SMInputEntry> entryList = new ArrayList<SMInputEntry>();
		for (Entry<String, Object> mapEntry : algorithmParameters.entrySet()){
			String paramValue=(String) mapEntry.getValue();


			if (paramValue.equals(toUseTable.getId().toString()))
				entryList.add(new SMInputEntry(mapEntry.getKey(),dataSpaceTableId));
			else {
				//checking if there are instances of old column names
				for(Entry<ColumnLocalId,String> curatedEntry:curatedColumnLabels.entrySet())
					paramValue=paramValue.replaceAll(curatedEntry.getKey().getValue(), curatedEntry.getValue());

				entryList.add(new SMInputEntry(mapEntry.getKey(), paramValue));
			}
		}
		config.parameters(new SMEntries(entryList
				.toArray(new SMInputEntry[entryList.size()])));

		config.algorithm(algorithmId);
		request.user(user);
		request.title((experimentTitle == null ? algorithmId
				: experimentTitle));
		request.description((experimentDescription == null ? "Submission via TDM"
				: experimentDescription));
		request.config(config);
		computationId = statisticalManagerFactory.executeComputation(request);
	}

	public void waitForComputation() throws WorkerException, OperationAbortedException {
		boolean complete = false;
		while (!complete) {
			checkAborted();
			SMComputation computation = statisticalManagerFactory
					.getComputation(computationId);
			SMOperationStatus status = SMOperationStatus.values()[computation
			                                                      .operationStatus()];
			switch (status) {
			case FAILED:
				throw new WorkerException("Failed Experiment.");
			case COMPLETED:
				complete = true;
				break;
			default:
				SMOperationInfo infos = statisticalManagerFactory
				.getComputationInfo(computationId, user);
				float smPercent = Float.parseFloat(infos.percentage()) / 100;
				updateProgress(0.10f + smPercent * 0.8f,"Waiting for computation to complete");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void generateTableFromResult() throws WorkerException {
		SMComputation computation = statisticalManagerFactory
				.getComputation(computationId);
		SMAbstractResource abstractResource = computation.abstractResource();
		SMResource smResource = abstractResource.resource();

		//		HashMap<String,String> metaMap=new HashMap<String,String>();

		Common.handleSMResource(smResource,results,toSerializeValues,createWorkerWrapper(importFactory),clearDataspace,user,home);

		serializeResultMap();
	}

	


	


	



	private String getSMTable()throws WorkerException, OperationAbortedException{
		try{
			GcubeServiceReferenceMetadata meta=targetTable.getMetadata(GcubeServiceReferenceMetadata.class);
			if(meta.getServiceClass().equals(Constants.STATISTICAL_SERIVCE_CLASS)&&meta.getServiceName().equals(Constants.STATISTICAL_SERVICE_NAME))
				return meta.getExternalId();
			else throw new NoSuchMetadataException(GcubeServiceReferenceMetadata.class);
		}catch(NoSuchMetadataException e){
			updateProgress(0.1f,"Transferring to dataspace");
			return importIntoDataSpace().getExternalId();
		}
	}

	private GcubeServiceReferenceMetadata importIntoDataSpace() throws WorkerException, OperationAbortedException{
		WorkerWrapper<ResourceCreatorWorker,ResourcesResult> wrapper=createWorkerWrapper(exportFactory);
		try{
			WorkerStatus status=wrapper.execute(targetTable.getId(), null, getSourceInvocation().getParameterInstances());
			if(!status.equals(WorkerStatus.SUCCEDED)) throw new WorkerException("Failed export to dataspace");
			return cubeManager.getTable(targetTable.getId()).getMetadata(GcubeServiceReferenceMetadata.class);
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unable to export table to dataspace.",e);
		} catch (NoSuchMetadataException e) {
			throw new WorkerException("Unable to get dataspace table id for exported table",e);
		}
	}

	private void deleteExternalReference(){
		Common.getSMDataSpace().removeTable(dataSpaceTableId);
		cubeManager.modifyTableMeta(targetTable.getId()).removeTableMetadata(GcubeServiceReferenceMetadata.class).create();
	}
	
}

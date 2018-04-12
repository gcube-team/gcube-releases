package org.gcube.data.analysis.tabulardata.statistical;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.IOUtils;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitor;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitorListener;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ParameterType;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
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

import lombok.extern.slf4j.Slf4j;
import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

@Slf4j
public class StatisticalOperation extends ResourceCreatorWorker {


	private static SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	private SClient dmClient;
	private ExportToStatisticalOperationFactory exportFactory;
	private ImportFromStatisticalOperationFactory importFactory;
	private Home home;
	private CubeManager cubeManager;

	public StatisticalOperation(OperationInvocation sourceInvocation,
			SClient dmClient,
			ExportToStatisticalOperationFactory exportFactory,
			ImportFromStatisticalOperationFactory importFactory, Home home, CubeManager cubeManager) {
		super(sourceInvocation);
		this.dmClient=dmClient;
		this.exportFactory = exportFactory;
		this.importFactory = importFactory;
		this.cubeManager = cubeManager;
		this.home=home;

	}

	// Invocation parameters
	private Operator operator;
	private Table targetTable;

	private GcubeServiceReferenceMetadata dataSpaceTable;

	private boolean clearDataspace=false;
	private boolean removeExported=false;
	//	private Table resultTable;



	private Map<String,String> toSerializeValues=new HashMap<String,String>();


	//	private ArrayList<Table> collaterals=new ArrayList<Table>();

	private List<ResourceDescriptorResult> results=new ArrayList<ResourceDescriptorResult>();

	// Execution

	ComputationId computationId=null;

	@Override
	protected ResourcesResult execute() throws WorkerException,OperationAbortedException {
		try{
			getParameters();
			updateProgress(0.05f,"Checking if already exported to dataspace..");		
			dataSpaceTable=getSMTable();
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
				deleteExternalReference(dataSpaceTable);
			}
			updateProgress(0.9f,"Finalizing");
			if(results.size()==0) throw new WorkerException("No resources were successfully imported from SM");
			return new ResourcesResult(results);
		}catch(WorkerException e){
			throw e;
		}catch(OperationAbortedException e){
			log.debug("Aborting operation");
			if (computationId!=null) {
				try {
					dmClient.cancelComputation(computationId);
				} catch (Exception e1) {
					log.warn("Unexpected error while cancelling DM operation",e1);
				}
			}			
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
						new InternalURI(new URI(id),"text/plain"), operator.getName()+" General results", "Text file containig etherogeneus results", ResourceType.GENERIC_FILE));
			}catch(Exception e){
				log.warn("Unable to write results to file ",e);
			}finally{
				if(writer!=null) IOUtils.closeQuietly(writer);
			}
		}

	}


	public void getParameters() {
		targetTable = cubeManager.getTable(getSourceInvocation()
				.getTargetTableId());

		operator=Common.getOperator(getSourceInvocation());
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

	public void submitComputation() throws ParseException, IOException, ProcessingException,Exception {
		// Use view table if present
		Table toUseTable=targetTable;
		if(toUseTable.contains(DatasetViewTableMetadata.class)){
			DatasetViewTableMetadata dsMeta = toUseTable.getMetadata(DatasetViewTableMetadata.class);
			toUseTable= cubeManager.getTable(dsMeta.getTargetDatasetViewTableId());
			log.debug("DataSetViewMetadata found with ID {} ",toUseTable.getId());			
		}

		// Curate column labels
		Map<ColumnLocalId,String> curatedColumnLabels=Common.curateLabels(toUseTable);
		
		String exportedId=null;
		try{
			new URL(dataSpaceTable.getExternalId());
			exportedId=dataSpaceTable.getExternalId();
		}catch(MalformedURLException e){
			log.debug("Exported ID is not url, getting it from storage.. ");
			exportedId=Common.getURLFromStorageId(dataSpaceTable.getExternalId()).toString();
		}
		
		
		String toSubstituteTableId=toUseTable.getId().getValue()+"";
		
		log.debug("Going to adjust operator parameters. Operator {}",operator);
		log.debug("Reference table id {} ",toSubstituteTableId);
		log.debug("Curated labels : {} ",curatedColumnLabels.entrySet());
		
		for(Parameter param:operator.getOperatorParameters()){
			String paramValue=param.getValue();
			ParameterType type=param.getTypology();
			if(type.equals(ParameterType.FILE)||type.equals(ParameterType.TABULAR)||type.equals(ParameterType.TABULAR_LIST)){
				param.setValue(paramValue.replaceAll(toSubstituteTableId, exportedId));
			}else if(type.equals(ParameterType.COLUMN)||type.equals(ParameterType.COLUMN_LIST)){
				for(Entry<ColumnLocalId,String> curatedEntry:curatedColumnLabels.entrySet())
					paramValue=paramValue.replaceAll(curatedEntry.getKey().getValue(), curatedEntry.getValue());
				
				param.setValue(paramValue);
			}
		}
		log.debug("Gonna submit operator {} ",operator);
		computationId= dmClient.startComputation(operator);
	}

	

	private void generateTableFromResult() throws WorkerException {
		try{
			OutputData output=dmClient.getOutputDataByComputationId(computationId);
			Resource theResource=output.getResource();			
			String storageBasePath=operator.getName()+"/"+dateFormatter.format(new Date(System.currentTimeMillis()));
			
			
			Common.handleSMResource(storageBasePath,theResource,results,toSerializeValues,createWorkerWrapper(importFactory),clearDataspace,home);
			serializeResultMap();
		}catch(Exception e){
			throw new WorkerException("Unable to retrieve result", e);
		}

		//		HashMap<String,String> metaMap=new HashMap<String,String>();

	}










	/**
	 * Checks export reference existence. If not present it calls Export To Statistical Operation 
	 * 
	 * @return
	 * @throws WorkerException
	 * @throws OperationAbortedException
	 */
	private GcubeServiceReferenceMetadata getSMTable()throws WorkerException, OperationAbortedException{
		try{
			GcubeServiceReferenceMetadata meta=targetTable.getMetadata(GcubeServiceReferenceMetadata.class);
			if(meta.getServiceClass().equals(Constants.DM_SERIVCE_CLASS)&&meta.getServiceName().equals(Constants.DM_SERVICE_NAME))
				return meta;
			else throw new NoSuchMetadataException(GcubeServiceReferenceMetadata.class);
		}catch(NoSuchMetadataException e){
			updateProgress(0.1f,"Transferring to dataspace");
			return importIntoDataSpace();
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

	private void deleteExternalReference(GcubeServiceReferenceMetadata meta){
		IClient client = Utils.getStorageClient();
		client.remove(meta.getExternalId());		
		cubeManager.modifyTableMeta(targetTable.getId()).removeTableMetadata(GcubeServiceReferenceMetadata.class).create();
	}

	
	//******************************************* Async Computation Management
	
	
	//SEMAPHRE LOGIC
		private Semaphore sem=new Semaphore(0);
		
		private static enum OperationStatus{
			WAITING,COMPLETED,FAILED, ABORTED 
		}
		
		private OperationStatus status;
		private String errorMsg="";
	
		private void onComputationFinished(OperationStatus toSetStatus,String msg){
			status=toSetStatus;
			errorMsg=msg;
			sem.release();
		}
		
	private void waitForComputation() throws WorkerException, OperationAbortedException {
		
		
		
		DMMonitorListener listener = new DMMonitorListener() {

			@Override
			public void running(double percentage) {
				Float progress=new Float(percentage);
				updateProgress(0.4f + (progress/100 )* 0.2f,"Waiting for computation to complete");
				log.debug("Operation Running: " + percentage);
			}

			@Override
			public void failed(String message, Exception exception) {
				log.error("Operation Failed");
				log.error(message, exception);
				onComputationFinished(OperationStatus.FAILED, message);
			}

			@Override
			public void complete(double percentage) {
				log.debug("Operation Completed");
				log.debug("Perc: " + percentage);
				onComputationFinished(OperationStatus.COMPLETED,"");

			}

			@Override
			public void cancelled() {
				log.debug("Operation Cancelled");
				onComputationFinished(OperationStatus.ABORTED, "CANCELLED");
			}

			@Override
			public void accepted() {
				log.debug("Operation Accepted");
				
			}
		};

		DMMonitor dmMonitor = new DMMonitor(computationId, dmClient);
		dmMonitor.add(listener);
		dmMonitor.start();
		
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			log.debug("Woke up. Computation should be finished");
		}
		
		switch(status){
		case ABORTED : throw new OperationAbortedException();
		case FAILED : throw new WorkerException("Failed experiment. Cause "+errorMsg);
		case WAITING : throw new WorkerException("Incoherent status once finished monitoring. This should never happen. Please contact developer.");
		case COMPLETED : log.debug("Operation is complete");
		}
		
	}
	
	
	
}

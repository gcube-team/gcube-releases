package org.gcube.data.analysis.tabulardata.statistical;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.importer.csv.CSVImportFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class ImportFromStatisticalOperation extends DataWorker{

	private SClient dmClient;
	private CSVImportFactory csvImportFactory;
	private IClient storageClient;
	private CubeManager cubeManager;



	public ImportFromStatisticalOperation(CubeManager cubeManager,OperationInvocation sourceInvocation,
			SClient dmClient,
			CSVImportFactory csvImportFactory, IClient storageClient) {
		super(sourceInvocation);
		this.cubeManager=cubeManager;
		this.dmClient=dmClient;
		this.csvImportFactory = csvImportFactory;
		this.storageClient = storageClient;
	}


	private String resourceId;
	private Table importedTable;
	private boolean delete=false;


	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException {
		resourceId=OperationHelper.getParameter(ImportFromStatisticalOperationFactory.RESOURCE_ID, getSourceInvocation());
		try{
			delete=OperationHelper.getParameter(ImportFromStatisticalOperationFactory.DELETE_REMOTE_RESOURCE, getSourceInvocation());
		}catch(Throwable t){
			// not specified
		}

		updateProgress(0.1f,"Transfering data");
		//			importSMTable();
		checkAborted();
		updateProgress(0.5f,"Parsing data");
		importedTable=importCSV(resourceId);
		checkAborted();
		updateProgress(0.9f,"Finalizing");
		String toApplyLabel="SM Table "+resourceId;
		if(getSourceInvocation().getParameterInstances().containsKey(ImportFromStatisticalOperationFactory.RESOURCE_NAME.getIdentifier()))
			toApplyLabel=OperationHelper.getParameter(ImportFromStatisticalOperationFactory.RESOURCE_NAME, getSourceInvocation());
		TableMetaCreator tmc=cubeManager.modifyTableMeta(importedTable.getId());
		tmc.setTableMetadata(new NamesMetadata(Collections.singletonList((LocalizedText)new ImmutableLocalizedText(toApplyLabel))));
		importedTable=tmc.create();
		checkAborted();
		if(delete)removeSMTable();
		return new ImmutableWorkerResult(importedTable);		
	}

	private void removeSMTable(){
		throw new RuntimeException ("HOWWW");
	}

	//	private void importSMTable() throws WorkerException {
	//		try {			
	//			csvFile = statisticalDataSpace.exportTable(resourceId);			
	//		} catch (ResourceNotFoundException e) {
	//			throw new WorkerException(
	//					"Unable to import result table from dataspace ", e);
	//		}
	//	}

	private Table importCSV(String fileUri) throws WorkerException, OperationAbortedException {
		Character delimiter=',';
		Boolean hasHeader=true;

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.HASHEADER, hasHeader);
		parameters.put(org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.URL, fileUri);
		parameters.put(org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.SEPARATOR, delimiter+"");
		parameters.put(org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.ENCODING, Charset.defaultCharset().toString());

		WorkerWrapper<DataWorker,WorkerResult> wrapper = createWorkerWrapper(csvImportFactory);
		try{
			WorkerStatus status=wrapper.execute(null, null, parameters);
			if(status.equals(WorkerStatus.SUCCEDED)){
				return wrapper.getResult().getResultTable();
			}else throw new WorkerException("Failed to import file from Statistical Manager");
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unable to import SM Table from File "+fileUri,e);
		}
	}


	//	private String sendToStorage(File csv) {
	//		String rPath="/csv/"+UUID.randomUUID().toString();
	//		String id = storageClient.put(true).LFile(csv.getAbsolutePath()).RFile(rPath);
	//		return id;
	////		return storageClient.getUrl().RFile(id).toString();
	//	}

}

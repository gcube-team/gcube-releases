package org.gcube.data.analysis.tabulardata.statistical;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GcubeServiceReferenceMetadata;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.export.csv.exporter.CSVExportFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

public class ExportToStatisticalOperation extends ResourceCreatorWorker{

	
	private CSVExportFactory csvExportFactory;
	private CubeManager cubeManager;
//	private SClient dmClient;
	
	
	public ExportToStatisticalOperation(OperationInvocation sourceInvocation,
			CSVExportFactory csvExportFactory, CubeManager cubeManager
//			,SClient dmClient
			) {
		super(sourceInvocation);
		this.csvExportFactory = csvExportFactory;
		this.cubeManager = cubeManager;
//		this.dmClient = dmClient;
	}

	
	private Table targetTable;
	private Set<String> toEscapeFieldNames=new HashSet<String>();
	
	private ResourcesResult exportedTable;
	
	
	@Override
	public ResourcesResult execute() throws WorkerException,OperationAbortedException {
		try{
			loadParameters();
		updateProgress(0.1f,"Creating csv");
		exportCSV();
		updateProgress(0.9f,"Finalizing");
		
		TableMetaCreator tmc=cubeManager.modifyTableMeta(getSourceInvocation().getTargetTableId());
		tmc.setTableMetadata(new GcubeServiceReferenceMetadata(Constants.DM_SERIVCE_CLASS, 
				Constants.DM_SERVICE_NAME, new Date(), exportedTable.getResources().iterator().next().getResource().getStringValue()));
		tmc.create();
		
		return exportedTable;
		
		
//		importIntoDataSpace();
//		updateProgress(0.9f,"Finalizing");
//		TableMetaCreator tmc=cubeManager.modifyTableMeta(getSourceInvocation().getTargetTableId());
//		tmc.setTableMetadata(new GcubeServiceReferenceMetadata(Constants.STATISTICAL_SERIVCE_CLASS, 
//				Constants.STATISTICAL_SERVICE_NAME, new Date(), dataSpaceTableId));
//		tmc.create();
//		return new ResourcesResult(new ImmutableStringResource(
//				new StringResource(Constants.STATISTICAL_URI_PREFIX+":"+dataSpaceTableId),OperationHelper.retrieveTableLabel(targetTable),"Statistical dataspace CSV version",ResourceType.CSV));
//		
		}catch(WorkerException e){
			throw e;
		}catch(OperationAbortedException e){
			throw e;
		}catch(Exception e){
			throw new WorkerException("Unexpected internal error. Please contact support",e);
		}
	}
	
	private void loadParameters(){
		Map<String, Object> params = getSourceInvocation()
				.getParameterInstances();		
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		if(params.containsKey(ExportToStatisticalOperationFactory.toEscapeFieldNamesParam.getIdentifier())){
			Object paramValue=OperationHelper.getParameter(ExportToStatisticalOperationFactory.toEscapeFieldNamesParam, getSourceInvocation());
			if(paramValue instanceof Iterable<?>)
				for(String s:(Iterable<String>)paramValue)
					toEscapeFieldNames.add(s);
			else toEscapeFieldNames.add((String) paramValue);
		}		
	}
	
	
	private void exportCSV() throws WorkerException, OperationAbortedException, ParseException, IOException, ProcessingException {
		// prepare parameters
		HashMap<String, Object> exportParams = new HashMap<String, Object>();
		List<String> columns = new ArrayList<String>();
		
		HashMap<ColumnLocalId,String> toRestoreLabels=new HashMap<ColumnLocalId,String>();
				 
		
		if(targetTable.contains(DatasetViewTableMetadata.class)){
			DatasetViewTableMetadata dsMeta = targetTable.getMetadata(DatasetViewTableMetadata.class);
			targetTable = cubeManager.getTable(dsMeta.getTargetDatasetViewTableId());
		}
		
		
		// need to change labels where not valid
		TableMetaCreator tmc=cubeManager.modifyTableMeta(targetTable.getId());
		
		
		
		Map<ColumnLocalId,String> curatedLabels=Common.curateLabels(targetTable,toEscapeFieldNames.toArray(new String[toEscapeFieldNames.size()]));
		
		boolean changedMeta=false;
		
		
		for(Entry<ColumnLocalId,String> entry:curatedLabels.entrySet()){
			columns.add(entry.getKey().getValue());
			String originalLabel=OperationHelper.retrieveColumnLabel(targetTable.getColumnById(entry.getKey()));
			if(!originalLabel.equals(entry.getValue())){
				toRestoreLabels.put(entry.getKey(), originalLabel);
				tmc.setColumnMetadata(entry.getKey(), new NamesMetadata(
						Collections.singletonList((LocalizedText)new ImmutableLocalizedText(entry.getValue()))));
				changedMeta=true;
			}
		}
				
		if(changedMeta)	tmc.create();			
		
		
		
		exportParams.put(org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.COLUMNS, columns);
		exportParams.put(org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.ENCODING, Charset.defaultCharset().toString());
		exportParams.put(org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.SEPARATOR, ",");
		exportParams.put(org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.VIEW,new Boolean(false));
		WorkerWrapper<ResourceCreatorWorker,ResourcesResult> wrapper = createWorkerWrapper(csvExportFactory);
		try {
			WorkerStatus status = wrapper.execute(targetTable.getId(), null,
					exportParams);
			if (status.equals(WorkerStatus.SUCCEDED)) {
				exportedTable = wrapper.getResult();				
			} else
				throw new WorkerException(
						"Failed export to CSV, worker status was " + status);
		} catch (InvalidInvocationException e) {
			throw new WorkerException("Unable to export table as CSV", e);
		}
		
		if(changedMeta){			
			for(Entry<ColumnLocalId,String> entry:toRestoreLabels.entrySet())
				tmc.setColumnMetadata(entry.getKey(), new NamesMetadata(Collections.singletonList((LocalizedText)new ImmutableLocalizedText(entry.getValue()))));
			
			tmc.create();
		}
		
	}
	
//	private void importIntoDataSpace() throws WorkerException {
//		String template = "GENERIC";
//		TableTemplates tableTemplate = null;
//		for (TableTemplates t : TableTemplates.values())
//			if (template.contentEquals(t.toString())) {
//				tableTemplate = t;
//				break;
//			}
//		boolean hasHeader = true;
//		String delimiter = ",";
//		File f=null;
//		try {
//			f = getInputFile();
//			String importID=statisticalDataSpace.createTableFromCSV(f,
//					hasHeader, delimiter, "#",
//					"TDM - " + OperationHelper.retrieveTableLabel(targetTable), tableTemplate,
//					"Exported from TDM", user);
//			
//			// poll request
//			
//			
//			boolean complete=false;
//			while(!complete){
//				SMImport smImport=statisticalDataSpace.getImporter(importID);
//				SMOperationStatus status=SMOperationStatus.values()[smImport.operationStatus()];
//				switch(status){
//				case FAILED : 
//					throw new WorkerException("Unable to send table to data space");
//				case COMPLETED : 
//					complete=true;
//					dataSpaceTableId=smImport.abstractResource().resource().resourceId();
//					break;
//				default :
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//					}
//				}
//			}
//			
//			
//		}catch(WorkerException e){
//			throw e;
//		} catch (NoSuchMetadataException e) {
//			throw new WorkerException("Unable to locate exported CSV file", e);
//		} catch (Exception e) {
//			throw new WorkerException("Unable to locate exported CSV file", e);
//		}finally{
//			if(f!=null) f.delete();
//		}
//	}
	
	
//	private File getInputFile() throws Exception{
//
//		File tempFile = File.createTempFile("import", ".csv");
//		Utils.getStorageClient().get().LFile(tempFile.getAbsolutePath()).RFile(exportedTable.getResource().getStringValue());
//		
//		return tempFile;
//	}
}

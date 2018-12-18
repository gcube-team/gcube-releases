package org.gcube.usecases.ws.thredds.engine.impl.threads;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.data.transfer.library.TransferResult;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.ExecutionReport;
import org.gcube.data.transfer.model.ExecutionReport.ExecutionReportFlag;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.model.RemoteFileDescriptor;
import org.gcube.data.transfer.model.plugins.thredds.DataSet;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.spatial.data.sdi.interfaces.Metadata;
import org.gcube.spatial.data.sdi.model.metadata.MetadataPublishOptions;
import org.gcube.spatial.data.sdi.model.metadata.MetadataReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocationBuilder;
import org.gcube.spatial.data.sdi.plugins.SDIAbstractPlugin;
import org.gcube.usecases.ws.thredds.Constants;
import org.gcube.usecases.ws.thredds.NetUtils;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus;
import org.gcube.usecases.ws.thredds.engine.impl.ThreddsController;
import org.gcube.usecases.ws.thredds.engine.impl.WorkspaceUtils;
import org.gcube.usecases.ws.thredds.faults.CancellationException;
import org.gcube.usecases.ws.thredds.faults.DataTransferPluginError;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.RemoteFileNotFoundException;
import org.gcube.usecases.ws.thredds.model.StepReport;
import org.gcube.usecases.ws.thredds.model.StepReport.OperationType;
import org.gcube.usecases.ws.thredds.model.StepReport.Status;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;
import org.gcube.usecases.ws.thredds.model.SynchronizedElementInfo.SynchronizationStatus;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizationThread implements Runnable {

	private SynchronizationRequest theRequest;

	public SynchronizationThread(SynchronizationRequest theRequest) {
		super();
		this.theRequest = theRequest;
	}

	@Override
	public void run() {
		ProcessIdProvider.instance.set(theRequest.getProcess().getDescriptor().getProcessId());
		log.debug("Executing Transfer request {} ",theRequest);
		RequestLogger.get().log(theRequest);
		String reportMessage="Never started";
		String reportItemName="Still Unknown";
		Status toSetStatus=Status.ERROR;
		WorkspaceFolder parentFolder=theRequest.getLocation();
		try {
			checkCancelledProcess();
			SynchFolderConfiguration synchConfig=WorkspaceUtils.loadConfiguration(theRequest.getLocation());
			ThreddsController controller=new ThreddsController(synchConfig.getRemotePath(), synchConfig.getTargetToken());

			if(theRequest instanceof TransferToThreddsRequest) {
				TransferToThreddsRequest request=(TransferToThreddsRequest) theRequest;
				WorkspaceItem item=request.getToTransfer();

				//look for metadata in same folder
				String itemName=item.getName();
				reportItemName=itemName;
				String toLookMetadataName=itemName.substring(0, itemName.lastIndexOf("."))+".xml";
				WorkspaceItem metadataItem=getFileByName(item.getParent(),false,toLookMetadataName);

				// if not present, generate with sis/geotk
				Destination toSetDestination=new Destination();
				toSetDestination.setCreateSubfolders(true);
				toSetDestination.setDestinationFileName(itemName);
				toSetDestination.setOnExistingFileName(DestinationClashPolicy.REWRITE);
				toSetDestination.setOnExistingSubFolder(DestinationClashPolicy.APPEND);
				toSetDestination.setPersistenceId(synchConfig.getRemotePersistence());

				//NB ITEM IS SUPPOSED TO HAVE REMOTE PATH 
				String fileLocation=request.getLocation().getProperties().getPropertyValue(Constants.WorkspaceProperties.REMOTE_PATH);
				toSetDestination.setSubFolder(fileLocation);


				checkCancelledProcess();


				Set<PluginInvocation> invocations=null;						
				if(metadataItem==null) {
					log.debug("Metadata not found, asking SIS/GEOTK for generation..");
					invocations=Collections.singleton(new PluginInvocation(Constants.SIS_PLUGIN_ID));
				}
				log.info("Transferring to {} with invocations {} ",toSetDestination,invocations);

				ThreddsInfo info=controller.getThreddsInfo();

				DataSet dataset=info.getDataSetFromLocation(info.getLocalBasePath()+"/"+fileLocation);

				//				ThreddsCatalog catalog=controller.getCatalog();

				checkCancelledProcess();

				TransferResult result=controller.transferFile(toSetDestination, item.getPublicLink(false), invocations);


				Map<String,String> toSetProperties=new HashMap<String,String>();


				String toSetMetadataUUID=null;

				Boolean validateMetadata=synchConfig.getValidateMetadata();
				
				
				checkCancelledProcess();
				if(metadataItem==null) {
					ExecutionReport report=result.getExecutionReports().get(Constants.SIS_PLUGIN_ID);
					if(!report.getFlag().equals(ExecutionReportFlag.SUCCESS)) throw new  DataTransferPluginError("Unable to Extract Metadata for "+itemName+" Message is "+report.getMessage());
					else toSetMetadataUUID=report.getMessage(); 

				}else {
					MetadataReport metaReport=publishMetadata(metadataItem,info.getHostname(),itemName,dataset.getPath(),validateMetadata);
					toSetMetadataUUID=metaReport.getPublishedUUID();
				}

				toSetProperties.put(Constants.WorkspaceProperties.LAST_UPDATE_TIME, controller.getFileDescriptor(itemName).getLastUpdate()+"");
				toSetProperties.put(Constants.WorkspaceProperties.METADATA_UUID, toSetMetadataUUID);			

				toSetProperties.put(Constants.WorkspaceProperties.SYNCHRONIZATION_STATUS, SynchronizationStatus.UP_TO_DATE+"");
				item.getProperties().addProperties(toSetProperties);

				reportMessage="Successfully transferred and published.";
				// End ws->th
			} else if(theRequest instanceof TransferFromThreddsRequest) {
				Map<String,String> toSetProperties=new HashMap<String,String>();
				TransferFromThreddsRequest importRequest=(TransferFromThreddsRequest) theRequest;
				String toImportName=null;
				WorkspaceItem targetItem=null;
				if(importRequest.getTargetItem()==null) {
					//Target Item will be created
					toImportName=importRequest.getRemoteFilename();
					reportMessage="Importing remote file..";
				}else {
					//Target Item already exists
					toImportName=importRequest.getTargetItem().getName();
					targetItem=importRequest.getTargetItem();
					reportMessage="Updating file..";
				}

				reportItemName=toImportName;
				
				RemoteFileDescriptor toImport=controller.getFileDescriptor(toImportName);

				toSetProperties.put(Constants.WorkspaceProperties.LAST_UPDATE_TIME, toImport.getLastUpdate()+"");
				InputStream source=null;
				try {
					source=controller.getInputStream(toImportName);
					if(targetItem==null)
						targetItem=parentFolder.createExternalFileItem(toImportName, "Imported from Thredds", null, source);
					else
						targetItem.updateItem(source);
					targetItem.getProperties().addProperties(toSetProperties);
				}finally {
					if(source!=null)
						source.close();
				}
				reportMessage="File successfully imported";
				
			}else if(theRequest instanceof DeleteRemoteRequest) {
				DeleteRemoteRequest deleteRequest=(DeleteRemoteRequest) theRequest;
				reportItemName=deleteRequest.getToRemoveName();

				log.debug("Going to delete {} from ",reportItemName,synchConfig.getRemotePath());

				RemoteFileDescriptor desc=controller.getFileDescriptor(reportItemName);
				if(desc.isDirectory()) {
					log.debug("Remote {} is directory.. Cleaning it up, first.",reportItemName);
					controller.createEmptyFolder(null);
				}
				controller.deleteThreddsFile(reportItemName);
				reportMessage="Successfully removed";				
			}
			log.info("Synchronization of {} successful.",reportItemName);
			toSetStatus=Status.OK;
		}catch(CancellationException e) {	
			log.debug("Process cancelled.. ",e);
			reportMessage="CancelledProcess";
			toSetStatus=Status.CANCELLED;
		}catch(DataTransferPluginError e) {
			log.debug("Unable to extract metadata ",e);
			reportMessage="Unable to extract metadata : "+e.getMessage();
			toSetStatus=Status.ERROR;
		}catch(RemoteFileNotFoundException e) {
			log.debug("Remote File not found ",e);
			reportMessage="Remote File not found : "+e.getMessage();
			toSetStatus=Status.ERROR;
		}catch(InternalErrorException e) {
			log.debug("Internal generic exception ",e);
			reportMessage="Internal error : "+e.getMessage();
			toSetStatus=Status.ERROR;
		}catch(Throwable t) {			
			log.debug("Internal generic exception ",t);
			reportMessage="Unexpected exception : "+t.getMessage();
			toSetStatus=Status.ERROR;
		}finally {
			updateParentProperty(parentFolder, toSetStatus);
			submitReport(reportItemName,reportMessage,toSetStatus);
			ProcessIdProvider.instance.reset();
		}
	}


	@Synchronized
	private static void updateParentProperty(WorkspaceFolder folder,StepReport.Status toSetStatus) {
		try {
			String currentValue=folder.getProperties().getProperties().get(Constants.WorkspaceProperties.LAST_UPDATE_STATUS);
			if(currentValue==null||currentValue.isEmpty()||currentValue.equals("null"))
				folder.getProperties().addProperties(Collections.singletonMap(Constants.WorkspaceProperties.LAST_UPDATE_STATUS, toSetStatus+""));
			else {
				StepReport.Status currentWSStatus=StepReport.Status.valueOf(currentValue);
				if(currentWSStatus.equals(StepReport.Status.OK)&&!toSetStatus.equals(currentWSStatus))			
					folder.getProperties().addProperties(Collections.singletonMap(Constants.WorkspaceProperties.LAST_UPDATE_STATUS, toSetStatus+""));			
			}
		}catch(Throwable t) {
			log.warn("Unable to update folder status ",t);
		}
	}


	private void checkCancelledProcess() throws CancellationException{
		if(theRequest.getProcess().getStatus().
				getStatus().equals(ProcessStatus.Status.STOPPED)) 
			throw new CancellationException("Process "+theRequest.getProcess().getDescriptor().getProcessId()+" has been cancelled");
	}


	private void submitReport(String elementName,String message,StepReport.Status status) {
		StepReport report=new StepReport(elementName,message,status,OperationType.WS_TO_TH,System.currentTimeMillis());

		if(theRequest instanceof TransferToThreddsRequest) report.setOperationType(OperationType.WS_TO_TH);
		else if(theRequest instanceof TransferFromThreddsRequest) report.setOperationType(OperationType.TH_TO_WS);
		else if(theRequest instanceof DeleteRemoteRequest) report.setOperationType(OperationType.DELETE_REMOTE);
		else throw new RuntimeException("Unknown operation request "+theRequest);		
		theRequest.getProcess().onStep(report);
	}




	private static MetadataReport publishMetadata(WorkspaceItem toPublish, String threddsHostname,String filename,String publicPath,Boolean validate) throws Exception{
		File tempMetaFile=null;
		try {
			Metadata meta=SDIAbstractPlugin.metadata().build();
			tempMetaFile=NetUtils.download(toPublish.getPublicLink(false));

			log.debug("Publishing metadata {} ",filename);

			MetadataPublishOptions opts=new MetadataPublishOptions(
					new TemplateInvocationBuilder().threddsOnlineResources(threddsHostname, filename, publicPath).get());
			opts.setGeonetworkCategory("Datasets");
			opts.setValidate(validate);
			return meta.pushMetadata(tempMetaFile, opts);
		}catch(Throwable t) {
			if(tempMetaFile!=null) Files.deleteIfExists(tempMetaFile.toPath());
			throw new Exception("Something went wrong while publishing metadata for "+filename+". Cause : "+t.getMessage(),t); 
		}
	}


	private static final WorkspaceItem getFileByName(WorkspaceFolder toLookIntoFolder,boolean recursive,String toLookForName) throws InternalErrorException {
		log.debug("Looking for {} into {} [recursive {} ]",toLookForName,toLookIntoFolder.getPath()+" ID "+toLookIntoFolder.getId(),recursive);
		for(WorkspaceItem item : toLookIntoFolder.getChildren()) 
			if(!item.isFolder()&&item.getName().equals(toLookForName)) return item;

		if(recursive) {
			for(WorkspaceItem item : toLookIntoFolder.getChildren())
				if(item.isFolder()) return getFileByName((WorkspaceFolder) item, recursive, toLookForName);
		}

		return null;
	}
}

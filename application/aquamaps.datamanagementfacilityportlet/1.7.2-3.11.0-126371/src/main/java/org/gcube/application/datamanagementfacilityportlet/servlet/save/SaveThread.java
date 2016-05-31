package org.gcube.application.datamanagementfacilityportlet.servlet.save;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.utils.AppZip;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationProgress;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationState;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveRequest;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveThread extends Thread implements SaveHandler {

	
	private static final Logger logger = LoggerFactory.getLogger(SaveThread.class);
	
	
	private SaveRequest request=null;
	private SaveOperationProgress progress=new SaveOperationProgress();
	private ASLSession session=null;
	
	@Override
	public SaveOperationProgress getProgress() {
		return progress;
	}

	@Override
	public void setRequest(ASLSession session,SaveRequest request) {
		this.session=session;
		this.request=request;
	}

	@Override
	public void startProcess() {
		start();
	}
	
	@Override
	public void run() {
		try{
			if(request==null)throw new Exception("Request was not set");
			if(session==null)throw new Exception("Session was not set");
			ScopeProvider.instance.set(session.getScope()+"");
			logger.debug("Starting request "+request);
			switch(request.getType()){
				case ANALYSIS : saveAnalysis(request.getToSaveId(), request.getToSaveName(), request.getDestinationBasketId(), session, progress);
								break;
				case RESOURCE : exportResourceToCsv(Integer.parseInt(request.getToSaveId()), request.getToSaveName(), request.getDestinationBasketId(), session, progress);
								break;
				case CUSTOM_QUERY: exportCustomQueryToCsv(session.getUsername(), request.getToSaveName(), request.getDestinationBasketId(), session, progress);
				break;
			}
			progress.setState(SaveOperationState.COMPLETED);			
		}catch(Throwable t){
			progress.setState(SaveOperationState.ERROR);
			progress.setFailureReason(t.getMessage());
			progress.setFailureDetails(t.getLocalizedMessage());			
		}
	}
	
	private static void saveAnalysis(String id,String toSaveFolderName,String destinationBasketId,ASLSession session,SaveOperationProgress progress)throws Exception{
		
		//************* RETRIEVE zip file
		progress.setState(SaveOperationState.RETRIEVING_FILES);		
		File tar=dataManagement().build().loadAnalysisResults(id);
		
		//************ Uncompress
		
		File temp=new File(System.getProperty("java.io.tmpdir"),"analysis"+System.currentTimeMillis());
		
		temp.mkdirs();
		
		int extractedCount=AppZip.unzipToDirectory(tar.getAbsolutePath(), temp);
		logger.debug("Extracted Files : "+extractedCount+" into temp folder "+temp.getAbsolutePath());
		

		//******init progress saving state
		progress.setState(SaveOperationState.SAVING_FILES);
		progress.setToSaveCount(extractedCount);
		progress.setSavedCount(0);
		
		
		//**ACTUALLY save to workspace		
		Workspace wa = HomeLibrary.getUserWorkspace(session.getUsername());
		WorkspaceFolder selectedFolder=(WorkspaceFolder) wa.getItem(destinationBasketId);
		WorkspaceFolder newFolder=selectedFolder.createFolder(toSaveFolderName, "Sources Anlysis data");
		copyDirectoryContentToWorkspaceFolder(newFolder, temp,progress);
		FileUtils.delete(temp);
		FileUtils.delete(tar);
	}
	
	private static void copyDirectoryContentToWorkspaceFolder(WorkspaceFolder folder,File directory,SaveOperationProgress progress) throws InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, FileNotFoundException{		
		for(File f:directory.listFiles()){
			if(f.isDirectory()) {
				WorkspaceFolder subFolder=folder.createFolder(f.getName(), "Sources Analysis Data");
				copyDirectoryContentToWorkspaceFolder(subFolder, f,progress);
			}else {
				if(f.getName().endsWith(".png"))folder.createExternalImageItem(f.getName(), "Sources analysis graph", Tags.IMAGE_PNG, new FileInputStream(f));
					else logger.debug("Skipping unknown file "+f);
				progress.setSavedCount(progress.getSavedCount()+1);
			}
		}
	}
	
	
	private static void exportResourceToCsv(Integer resourceId,String toSaveName,String destinationBasketId,ASLSession session,SaveOperationProgress progress)throws Exception{

		//******init progress saving state
		progress.setState(SaveOperationState.SAVING_FILES);
		progress.setToSaveCount(1);
		progress.setSavedCount(0);
		DataManagement dm=dataManagement().build();
		String resourceTableName=dm.loadResource(resourceId).getTableName();
		dm.exportTableAsCSV(resourceTableName,destinationBasketId,session.getUsername(),toSaveName,ExportOperation.SAVE);
		
		
		progress.setSavedCount(1);		
	}
	
	private static void exportCustomQueryToCsv(String userId,String toSaveName,String destinationBasketId,ASLSession session,SaveOperationProgress progress)throws Exception{

		//******init progress saving state
		progress.setState(SaveOperationState.SAVING_FILES);
		progress.setToSaveCount(1);
		progress.setSavedCount(0);
			
		if(!session.hasAttribute(Tags.currentDirectQuery)) throw new Exception ("No Query is currently setted");
		CustomQueryDescriptorStubs desc=(CustomQueryDescriptorStubs) session.getAttribute(Tags.currentDirectQuery);
		dataManagement().build().exportTableAsCSV(desc.actualTableName(),destinationBasketId,session.getUsername(),toSaveName,ExportOperation.SAVE);
		
		progress.setSavedCount(1);

	}
}

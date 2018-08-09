package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Date;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.ImageItem;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveCompoundMapRequest;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveOperationProgress;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveOperationState;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save.SaveRequest;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils.CompoundMapMeta;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

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
			logger.debug("Starting request "+request);
			switch(request.getType()){
				case COMPOUND_MAP: saveCompoundMap(((SaveCompoundMapRequest)request).getToSave(), request.getToSaveName(), request.getDestinationBasketId(), session, progress);
				break;
			}
			progress.setState(SaveOperationState.COMPLETED);			
		}catch(Throwable t){
			progress.setState(SaveOperationState.ERROR);
			progress.setFailureReason(t.getMessage());
			progress.setFailureDetails(t.getLocalizedMessage());			
		}
	}
	
	
	
	private static void saveCompoundMap(CompoundMapItem item, String toSaveFolderName,String destinationBasketId, ASLSession session, SaveOperationProgress progress)throws Exception{
		//************* RETRIEVE zip file
		progress.setState(SaveOperationState.SAVING_FILES);
		progress.setToSaveCount(item.getImageCount()+1);
		
		//**ACTUALLY save to workspace		
		Workspace wa = HomeLibrary.getUserWorkspace(session.getUsername());
		WorkspaceFolder selectedFolder=(WorkspaceFolder) wa.getItem(destinationBasketId);
		WorkspaceFolder newFolder=selectedFolder.createFolder(toSaveFolderName, "Sources Anlysis data");
		int count=0;
		for(String imgUri:CSVUtils.CSVToStringList(item.getImageList())){
			try{
				ImageItem imageItem=new ImageItem(imgUri);
				URL uri=new URL(imgUri);
				newFolder.createExternalImageItem((String) imageItem.get(ImageItem.LABEL), "AquaMap image", Tags.IMAGE_JPEG, uri.openStream());
				count++;
				progress.setSavedCount(count);
			}catch(Exception e){
				logger.error("Unexpected Exception reading image",e);
				throw new Exception("Unable to read image "+imgUri);
			}
		}
		try{
			newFolder.createExternalFileItem(item.getTitle()+"_meta.xml", "Compound Map Meta File", "text/xml", new FileInputStream(formCompoundMeta(item)));
		}catch(Exception e){
			logger.error("Unable to form meta data", e);
			throw new Exception("Unable to form meta data");
		}
		
	}
	
	private static String formCompoundMeta(CompoundMapItem toSave)throws Exception{
		//***** Form Object
		CompoundMapMeta meta=new CompoundMapMeta();
		meta.setAlgorithm(toSave.getAlgorithm());
		meta.setAuthor(toSave.getAuthor());
		meta.setCreationDate(new Date(toSave.getCreationDate()));
		meta.setCustom(toSave.isCustom());
		meta.setDataGenerationTime(new Date(toSave.getDataGenerationTime()));
		meta.setGis(toSave.isGis());
		meta.setImageCount(toSave.getImageCount());
		meta.setLayerId(toSave.getLayerId());
		meta.setLayerUrl(toSave.getLayerUrl());
		meta.setMapType(toSave.getType());
		meta.setResourceId(toSave.getResourceId());
		meta.setSpeciesList(CSVUtils.CSVToStringList(toSave.getSpeciesList()));
		meta.setTitle(toSave.getTitle());
		
		XStream stream=new XStream();
		stream.processAnnotations(CompoundMapMeta.class);
		File metaFile=File.createTempFile(toSave.getTitle()	, ".xml");
		ObjectOutputStream oStream=stream.createObjectOutputStream(new FileWriter(metaFile));
		oStream.writeObject(meta);
		oStream.flush();
		oStream.close();
		logger.debug("Wrote File "+metaFile.getAbsolutePath());
		return metaFile.getAbsolutePath();
	}
}

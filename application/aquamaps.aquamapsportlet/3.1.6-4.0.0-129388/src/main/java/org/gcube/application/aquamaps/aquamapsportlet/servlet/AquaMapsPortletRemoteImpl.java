package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.maps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.AquaMapsPortletRemoteService;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientEnvelope;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientObject;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.GISViewerParameters;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.SettingsDescriptor;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.ModelTranslation;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.log.AquaMapsObjectGenerationLogEntry;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.log.SavedAquaMapsItemLogEntry;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AreaType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FileType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.framework.accesslogger.library.AccessLoggerI;
import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;



public class AquaMapsPortletRemoteImpl extends RemoteServiceServlet implements
AquaMapsPortletRemoteService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8119786173899305965L;

	private static final Logger logger = LoggerFactory.getLogger(AquaMapsPortletRemoteImpl.class);


	private static final String layersPath=File.separator+"config"+File.separator+"layers.xml";


	public ClientEnvelope getEnvelope(String speciesId,boolean loadCustomizations) throws Exception {
		logger.debug("getEnvelop : "+speciesId);
		ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
		int hspenId=(Integer) session.getAttribute(ResourceType.HSPEN+"");
		try{		
			Envelope toReturn=maps().build().loadEnvelope(speciesId, hspenId).extractEnvelope();
			logger.debug("loading customizations for species "+speciesId);
			if(loadCustomizations) toReturn =Utils.loadCustomizations(toReturn,speciesId,session);
			return ModelTranslation.toClient(toReturn,speciesId);
		}catch(Exception e){
			logger.error("Exception occurred while retrieving envelope",e);
			throw new Exception(e.getMessage());
		}				
	}

	public Msg submitJob(String title)throws Exception{
		try{
		ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
		Job job= Utils.loadSettings(session, true, true);
		job.setName(title);
		SettingsDescriptor desc=Utils.getStats(job);
		if(desc.getSubmittable().getStatus()){
			maps().withTimeout(2, TimeUnit.MINUTES).build().submitJob(job);
			
			//**** ACCESS LOGGER
			
			AccessLoggerI aLogger=AccessLogger.getAccessLogger();
			for(AquaMapsObject obj:job.getAquaMapsObjectList())
				aLogger.logEntry(session.getUsername(), session.getScopeName(), new AquaMapsObjectGenerationLogEntry(obj, job.getSourceHSPEC()));
			
			
			return new Msg(true, "Submitted job "+title);
		}else return desc.getSubmittable();
		}catch(Exception e){
			logger.error("Error while submitting job :", e);
			throw new Exception("Unable to contact service, please retry leter or contact support.");
		}
	}
	public ClientObject getAquaMapsObject(int id,boolean onlineDBMode) throws Exception {
		try{
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			AquaMapsObject loaded=maps().build().loadObject(id);
			
			//Set additional details
			String layerName=null;
			String layerUrl=null;
			String basePath=null;
			if(loaded.getGis()&&loaded.getLayers().size()>0){
				layerName=loaded.getLayers().get(0).getTitle();
				layerUrl=loaded.getLayers().get(0).getUrl();
			}
			
			if(loaded.getImages().size()>0){
				String uri=loaded.getImages().get(0).getUuri();
				basePath=uri.substring(0,uri.lastIndexOf('/'));
			}
			
//			if(Boolean.parseBoolean(utils.getParam(Tags.gatherImagesOnPublicPort))){
//				for (org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File f :loaded.getImages()){
//					logger.debug("Replacing "+f.getUuri()+" , public HOST : "+ utils.getParam(Tags.publicHost));
//					f.setUuri(f.getUuri().replaceFirst("http://.*:..../", utils.getParam(Tags.publicHost)));
//					logger.debug("Done URI : "+f.getUuri());
//				}
//			}
			
			
			if(onlineDBMode){
				ArrayList<String> specIds=new ArrayList<String>();
				for(Species sp:loaded.getSelectedSpecies()){
					specIds.add(sp.getId());
				}
				DBManager.getInstance(session.getScope()).fetchGeneratedObjRelatedSpecies(loaded.getId(), specIds);
//				toReturn.getSelectedSpecies().clear();				
				Utils.addFetchedBasketId(session, id);
			}			
			
			ClientObject toReturn=ModelTranslation.toClient(loaded);
			
			toReturn.setLayerName(layerName);
			toReturn.setLayerUrl(layerUrl);
			toReturn.setLocalBasePath(basePath);
			return toReturn;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}


	//********************************* SAVE INTO WORKSPACE *********************************************************

	public Integer saveAquaMapsItem(List<Integer> objectId, String name, String destinationBasketId) throws Exception{
		int count=0;
		ArrayList<Integer> toMark=new ArrayList<Integer>();
		for(int i= 0; i<objectId.size();i++){
			try{

				saveAquaMapsItem(maps().build().loadObject(objectId.get(i)), name+i, destinationBasketId);
				count++;
				toMark.add(objectId.get(i));
			}catch(Exception e){
				logger.error("Unable to save "+objectId.get(i)+" with name "+name+i+" into "+destinationBasketId, e);
				throw new Exception(e.getMessage());
			}
		}
		try{
			maps().build().markSaved(toMark);
		}catch(Exception e){
			logger.error("Service was unable to mark saved objs",e);
			throw new Exception(e.getMessage());
		}
		return new Integer(count);
	}


	private void saveAquaMapsItem(AquaMapsObject obj, String name, String destinationBasketId) throws Exception {
		logger.debug("saveAquaMapsItem objectId: "+obj.getId()+" name: "+name+" destinationBasketId: "+destinationBasketId);

		
		
		File metaFile=File.createTempFile("AQ_"+obj.getId(), ".xml");
		AquaMapsXStream.serialize(metaFile.getAbsolutePath(), obj);
		
		

		ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());

		
		//AquaMapsPortletlogger.debug("*** getting related resources");

		//AquaMapsPortletlogger.debug("*** retrieved "+urls.size()+" urls");

		Map<String, File> files = retrieveFiles(obj.getImages());

		try {
			

			Workspace wa = HomeLibrary.getUserWorkspace(session.getUsername());
			
			WorkspaceFolder objFolder=wa.createFolder(obj.getName(),"AquaMaps object files",destinationBasketId);
			
			for (Entry<String,File> file:files.entrySet())
				objFolder.createExternalImageItem(file.getKey(), file.getKey(), null, file.getValue());
			
			
//			objFolder.createExternalFileItem(obj.getName(), "Metadata file", null, metaFile);
			
			
			
			AccessLogger.getAccessLogger().logEntry(session.getUsername(), session.getScopeName(), new SavedAquaMapsItemLogEntry(obj));
			
			
			for (File file:files.values()) file.delete();
			metaFile.delete();
		} catch (Exception e) {
			logger.error("error during aquamaps creation",e);
		}
	}

	protected Map<String, File> retrieveFiles(List<org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File> urls)
	{
		Map<String, File> files = new LinkedHashMap<String, File>();

		for (org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File f:urls)
		{
			if((!f.getType().equals(FileType.XML))&&(!f.getType().equals(FileType.ExternalMeta))){
				String fileName = f.getName();
				String fileUrl = f.getUuri();
				logger.debug("*** processing fileName: "+fileName+" fileUrl: "+fileUrl);
				try {
					File file = retrieveFile(fileUrl);
					//AquaMapsPortletlogger.debug("*** content saved on: "+file.getAbsolutePath());
					files.put(fileName, file);
				} catch (IOException e) {
					logger.error("error processing fileUrl: "+fileUrl, e);
				}
			}

		}

		return files;
	}

	protected File retrieveFile(String urlValue) throws IOException
	{
		File tmp = File.createTempFile("aquamaps", "tmp");
		URL url = new URL(urlValue);
		URLConnection connection = url.openConnection();
		connection.connect();
		IOUtils.copy(connection.getInputStream(), new FileOutputStream(tmp));
		return tmp;
	}



	//*********************************************** END OF SAVE METHODS ***********************************	





	public ClientEnvelope reCalculateEnvelopeFromCellIds(List<String> cellsId,String speciesId) throws Exception {
		try{
			return ModelTranslation.toClient(maps().build().calculateEnvelopeFromCellSelection(cellsId, speciesId),speciesId);
		}catch(Exception e){
			logger.error("Set Species Filter exception", e);
			throw new Exception(e.getMessage());
		}
	}

	public ClientEnvelope reCalculateGoodCells(String bb, String faoSelection,String speciesId, boolean useBottom, boolean useBounding,boolean useFAO) throws Exception {
		try{
			BoundingBox bounds=new BoundingBox();
			bounds.parse(bb);

			String[] areasString=faoSelection.split(",");
			List<Area> areas=new ArrayList<Area>();
			for(String code:areasString)
				areas.add(new Area(AreaType.FAO, code.trim()));

			return ModelTranslation.toClient(maps().build().calculateEnvelope(bounds, areas, speciesId, useBottom, useBounding, useFAO),speciesId);
		}catch(Exception e){
			logger.error("Set Species Filter exception", e);
			throw new Exception(e.getMessage());
		}
	}
	public Integer deleteSubmittedById(List<Integer> submittedId) throws Exception {
		try{
			int count=0;		
			try{
				count+=maps().build().deleteSubmitted(submittedId);
			}catch(Exception e){
				logger.error("Unable to delete all ids size = "+submittedId.size(), e);
			}
			return new Integer(count);
		}catch(Exception e){
			logger.error("Set Species Filter exception", e);
			throw new Exception(e.getMessage());
		}
	}
	public GISViewerParameters checkGIS(int objectId) throws Exception {
		try{
			
			Submitted obj=maps().build().loadSubmittedById(objectId);
			List<String> defaultLayers=(List<String>) AquaMapsXStream.deSerialize(getServletContext().getRealPath("")+layersPath);
			if(!obj.getGisEnabled())return new GISViewerParameters(defaultLayers,false,"Object was not GIS enabled.");
			if(!obj.getStatus().equals(SubmittedStatus.Error)&&!obj.getStatus().equals(SubmittedStatus.Completed))
				return new GISViewerParameters(defaultLayers,false,"Please wait for object to complete.");
			
			defaultLayers.add(obj.getGisPublishedId());
			return new GISViewerParameters(defaultLayers, true, "retrieved list");
			
		}catch(Exception e){
			logger.error("Check Gis Exception", e);
			throw new Exception(e.getMessage());
		}
	}
	public Msg saveLayerItem(String url, String mimeType, String name,
			String destinationBasketId)  throws Exception{
		try{
			ASLSession session=Utils.getSession(this.getThreadLocalRequest().getSession());
			Workspace wa= HomeLibrary.getUserWorkspace(session.getUsername());

			WorkspaceFolder basket=(WorkspaceFolder) wa.getItem(destinationBasketId);
			URL urlObject = new URL(url);
			URLConnection connection = urlObject.openConnection();
			connection.connect();			
			WorkspaceUtil.createExternalFile(basket, name, "", mimeType,connection.getInputStream());
			return new Msg(true, " Layer Saved correctly ");
		} catch (MalformedURLException e) {
			logger.error("", e);
			return new Msg(false, "Sorry, unable to retrieve layer data");
		} catch (IOException e) {
			logger.error("", e);
			return new Msg(false, "Sorry, unable to load layer data");
		} catch (InsufficientPrivilegesException e) {
			logger.error("", e);
			return new Msg(false, "Sorry, unable to save layer data. Please check your privileges");
		} catch (ItemAlreadyExistException e) {
			logger.error("", e);
			return new Msg(false, "Sorry, unable to save layer data : an Item with the chosen name already exists.");
		} catch (Exception e){
			logger.error("", e);
			return new Msg(false, "Sorry, unable to save layer. Unexpected Exception occurred. Try again or notify to administrator.");
		}
	}


}

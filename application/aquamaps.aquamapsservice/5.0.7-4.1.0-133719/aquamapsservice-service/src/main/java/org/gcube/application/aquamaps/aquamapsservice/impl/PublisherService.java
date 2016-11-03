package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.BulkReportsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetBulkUpdatesStatusResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetJSONSubmittedByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.PrepareBulkUpdatesFileRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.PublisherServicePortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.RetrieveMapsByCoverageRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FileType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.impl.model.CoverageDescriptor;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType;
import org.gcube_system.namespaces.application.aquamaps.types.FileArray;
import org.gcube_system.namespaces.application.aquamaps.types.MapArray;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublisherService extends GCUBEPortType implements
PublisherServicePortType {

	private static Logger logger = LoggerFactory.getLogger(PublisherService.class);
	
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}


	@Override
	public MapArray retrieveMapsByCoverage(
			RetrieveMapsByCoverageRequestType arg0) throws RemoteException,
			GCUBEFault {
		try{
			long starttime=System.currentTimeMillis();
			if(arg0.getSpeciesList()==null||arg0.getSpeciesList().getItems()==null||arg0.getSpeciesList().getItems().length==0) throw new Exception("No species specified");
			String[] speciesArray=arg0.getSpeciesList().getItems();

			Publisher publisher=ServiceContext.getContext().getPublisher();



			List<FileSet> foundFileSet=new ArrayList<FileSet>();
			List<Layer> foundLayers=new ArrayList<Layer>();





			//**************** Load layers by species
//			logger.debug("Checking maps by coverage from Publisher, species Selection is "+Arrays.toString(speciesArray));

			foundLayers.addAll(publisher.getLayersBySpeciesIds(speciesArray[0]));

			foundFileSet.addAll(publisher.getFileSetsBySpeciesIds(speciesArray[0]));


//			logger.debug("Found "+foundFileSet.size()+" related FileSet and "+foundLayers.size()+" layers, gonna form maps information..");


			HashMap<CoverageDescriptor,AquaMap> formedMaps=new HashMap<CoverageDescriptor, AquaMap>();
			HashMap<Integer,AquaMap> formedCustomMaps=new HashMap<Integer, AquaMap>();


			// forming filters for custom maps..
			Submitted s = new Submitted(0);			
			ArrayList<Field> fileSetFilter=new ArrayList<Field>();
			fileSetFilter.add(s.getField(SubmittedFields.filesetid));
			ArrayList<Field> layerFilter=new ArrayList<Field>();
			layerFilter.add(s.getField(SubmittedFields.gispublishedid));

			PagedRequestSettings pagedSettings=new PagedRequestSettings(1, 0, OrderDirection.ASC, SubmittedFields.searchid+""); 

			for(FileSet fSet:foundFileSet){
				CoverageDescriptor descr=new CoverageDescriptor(fSet.getTableId(), fSet.getParameters());
				if(fSet.isCustomized()){
					try{
						fileSetFilter.get(0).value(fSet.getId());
						Submitted found=SubmittedManager.getList(fileSetFilter, pagedSettings).get(0);
						formedCustomMaps.put(found.getSearchId(), formMap(fSet)); 
					}catch(Exception e){
						logger.warn("Unable to find an object for FS ID "+fSet.getId());
						logger.debug("Exception was ",e);
					}
				}else {
					if(formedMaps.containsKey(descr)){
						logger.warn("Multiple FileSet found for Coverage, current FS ID :  "+fSet.getId()+", previous : "+formedMaps.get(descr).getFileSetId());
					}else{
						formedMaps.put(descr, formMap(fSet));
					}
				}
			}


			for(Layer l:foundLayers){				
				CoverageDescriptor descr=new CoverageDescriptor(l.getTableId(), l.getParameters());
				if(l.isCustomized()){
					try{
						layerFilter.get(0).value(l.getId());
						Submitted found=SubmittedManager.getList(layerFilter, pagedSettings).get(0);
						if(formedCustomMaps.containsKey(found.getSearchId())){
							AquaMap toUpdate=formedCustomMaps.get(found.getSearchId());
							toUpdate.setLayer(l.getLayerInfo());
							toUpdate.setGis(true);
							toUpdate.setLayerId(l.getId()); 
						}else formedCustomMaps.put(found.getSearchId(), formMap(l));
					}catch(Exception e){
						logger.warn("Unable to find an object for layer ID "+l.getId());
						logger.debug("Exception was ",e);
					}
				}else {	
					if(formedMaps.containsKey(descr)){
						if(formedMaps.get(descr).isGis())
							logger.warn("Multiple Layer found for Coverage, current layer ID :  "+l.getId()+", previous : "+formedMaps.get(descr).getLayerId());
						else{
							AquaMap toUpdate=formedMaps.get(descr);
							toUpdate.setLayer(l.getLayerInfo());
							toUpdate.setGis(true);
							toUpdate.setLayerId(l.getId());
						}
					}else{
						formedMaps.put(descr, formMap(l));
					}
				}
			}
			ArrayList<AquaMap> toReturn=new ArrayList<AquaMap>(formedMaps.values());
			toReturn.addAll(formedCustomMaps.values());
//			logger.debug("Found "+toReturn.size()+" Maps ("+formedCustomMaps.size()+" custom)in "+(System.currentTimeMillis()-starttime)+" ms");
			return PortTypeTranslations.toMapArray(toReturn);			
		}catch(Exception e){
			logger.error("Unable to get Maps ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	@Override
	public FileArray getFileSetById(String arg0) throws RemoteException,
	GCUBEFault {
		try{
			Publisher publisher=ServiceContext.getContext().getPublisher();
			String publisherHost=ServiceContext.getContext().getPublisher().getWebServerUrl();
			FileSet fSet=publisher.getById(FileSet.class, arg0);
			if(fSet!=null){
				ArrayList<File> list=new ArrayList<File>();
				for(org.gcube.application.aquamaps.publisher.impl.model.File f: fSet.getFiles())
					list.add(new File(FileType.valueOf(f.getType()+""),publisherHost+f.getStoredUri(),f.getName()));
				return PortTypeTranslations.toFileArray(list);
			}else throw new Exception("FileSet with Id "+arg0+" not found");
		}catch(Exception e){
			logger.error("Unable to get FileSet ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	@Override
	public String getJSONSubmittedByFilters(
			GetJSONSubmittedByFiltersRequestType arg0) throws RemoteException,
			GCUBEFault {
		try{
			return SubmittedManager.getJsonList(PortTypeTranslations.fromStubs(arg0.getFilters()), arg0.getSettings());			
		}catch(Exception e){
			logger.error("Unable to get Submitted ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	@Override
	public LayerInfoType getLayerById(String arg0) throws RemoteException,
	GCUBEFault {
		try{
			Publisher publisher=ServiceContext.getContext().getPublisher();
			Layer layer=publisher.getById(Layer.class, arg0);
			if(layer!=null){
				return PortTypeTranslations.toStubs(layer.getLayerInfo());
			}else throw new Exception("Layer with Id "+arg0+" not found");
		}catch(Exception e){
			logger.error("Unable to get Layer ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	protected AquaMap formMap(CoverageDescriptor desc)throws Exception{
		AquaMap toAdd=new AquaMap();		
		//****** FAKE DATA TODO
		
		



		toAdd.setCoverage(desc.getParameters());
		Resource source=SourceManager.getById(SourceManager.getDefaultId(ResourceType.HSPEC));
		try{
			source=SourceManager.getById(Integer.parseInt(desc.getTableId()));
		}catch(Exception e){logger.warn("Unable to load resource from coverage , "+desc.getTableId(),e);}
		toAdd.setResource(source);		



		if(desc instanceof FileSet){
			String publisherHost=ServiceContext.getContext().getPublisher().getWebServerUrl();
			FileSet fSet=(FileSet)desc;
			toAdd.setAuthor(fSet.getMetaInfo().getAuthor());
			toAdd.setFileSetId(fSet.getId());
			ArrayList<File> files=new ArrayList<File>();
			for(org.gcube.application.aquamaps.publisher.impl.model.File f: fSet.getFiles())
				files.add(new File(FileType.valueOf(f.getType()+""),publisherHost+f.getStoredUri(),f.getName()));
			toAdd.setFiles(files);
			toAdd.setTitle(fSet.getMetaInfo().getTitle());
			toAdd.setSpeciesCsvList(CSVUtils.listToCSV(Arrays.asList(fSet.getSpeciesIds())));
			toAdd.setCreationDate(fSet.getMetaInfo().getDate());
			toAdd.setMapType(fSet.getSpeciesIds().length>1?ObjectType.Biodiversity:ObjectType.SpeciesDistribution);
			toAdd.setCustom(fSet.isCustomized());
		}else if(desc instanceof Layer){
			Layer l=(Layer)desc;
			toAdd.setAuthor(l.getMetaInfo().getAuthor());
			toAdd.setGis(true);
			toAdd.setLayer(l.getLayerInfo());
			toAdd.setSpeciesCsvList(CSVUtils.listToCSV(Arrays.asList(l.getSpeciesIds())));
			toAdd.setLayerId(l.getId());
			toAdd.setTitle(l.getMetaInfo().getTitle());
			toAdd.setSpeciesCsvList(CSVUtils.listToCSV(Arrays.asList(l.getSpeciesIds())));
			toAdd.setCreationDate(l.getMetaInfo().getDate());
			toAdd.setMapType(l.getSpeciesIds().length>1?ObjectType.Biodiversity:ObjectType.SpeciesDistribution);
			toAdd.setCustom(l.isCustomized());
		}
		return toAdd;
	}


	@Override
	public GetBulkUpdatesStatusResponseType getBulkUpdatesStatus(String arg0)
			throws RemoteException, GCUBEFault {
		try{
			return BulkReportsManager.getStatus(arg0);
		} catch (Exception e) {
			logger.error("Unable to insert request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	@Override
	public String prepareBulkUpdatesFile(PrepareBulkUpdatesFileRequestType arg0)
			throws RemoteException, GCUBEFault {
		try {
			return BulkReportsManager.insertRequest(arg0.getFromTime(), arg0.isIncludeGisLayers(), arg0.isIncludeCustomMaps());
		} catch (Exception e) {
			logger.error("Unable to insert request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
}

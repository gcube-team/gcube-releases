package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.AquaMapsObjectData;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.BiodiversityObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.DistributionObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.GISUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.GISUtils.GISPublishedItem;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.LayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.StyleGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.StyleGenerationRequest.ClusterScaleType;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HCAF_SFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HSPECFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ObjectType;
import org.gcube.application.aquamaps.publisher.MetaInformations;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.impl.datageneration.ObjectManager;
import org.gcube.application.aquamaps.publisher.impl.model.CoverageDescriptor;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.FileType;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.application.aquamaps.publisher.impl.model.WMSContext;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generator<T> implements ObjectManager<T> {
	final static Logger logger= LoggerFactory.getLogger(Generator.class);
	
	final static Publisher publisher=ServiceContext.getContext().getPublisher();
	
	private static final String GENERATION_DATA_TABLE = "generationdata";
	private static final String GENERATION_ID = "id";
	private static final String GENERATION_csv = "csv";
	private static final String GENERATION_max = "max";
	private static final String GENERATION_min = "min";
	private static final String GENERATION_csq = "csq";
	private static final String GENERATION_path = "path";
	private static final String GENERATION_SPECIES="species";
	
	
	// ***********INSTANCE
	protected GenerationRequest request;
	protected Class<T> clazz;
	
	public Generator(GenerationRequest request, Class<T> toGenerateClazz) {
		this.request = request;
		this.clazz=toGenerateClazz;
	}

	@Override
	public T generate() throws Exception {		
		if(clazz.equals(FileSet.class)){
			AquaMapsObjectExecutionRequest req=(AquaMapsObjectExecutionRequest) request;
			return (T) generateFileSet(getData(req), req.getObject());
		}else if(clazz.equals(Layer.class)){
			AquaMapsObjectExecutionRequest req=(AquaMapsObjectExecutionRequest) request;
			return (T) generateLayer(getData(req), req.getObject());
		}
		else if(clazz.equals(WMSContext.class)){
			throw new Exception("WMS CONTEXT ARE NOT SUPPORTED ANYMORE");
//			return (T) generateWMSContext(((WMSGenerationRequest)request).getJobId());
		}
		else throw new Exception("Invalid class, generator not implemented, class was "+clazz.getClass());
	}
	@Override
	public void destroy(T toDestroy) throws Exception {
		if(clazz.equals(FileSet.class))remove((FileSet) toDestroy);
		else if(clazz.equals(Layer.class)) remove((Layer)toDestroy);
		else if(clazz.equals(WMSContext.class))remove((WMSContext)toDestroy);
		else throw new Exception("Invalid class, destroyer not implemented, class was "+toDestroy.getClass());
	}
	@Override
	public T update(T toUpdate) throws Exception {
		destroy(toUpdate);		
		return generate();
	}

	
	// **************** T REMOVE 
	
	protected static void remove(FileSet toDestroy)throws Exception{		
		String physicalBasePath=publisher.getServerPathDir().getAbsolutePath();
		for(org.gcube.application.aquamaps.publisher.impl.model.File f:toDestroy.getFiles()){
			logger.debug("Deleting file "+f.getStoredUri()+" from FileSet "+toDestroy.getId());
			String path=physicalBasePath+File.separator+f.getStoredUri();
			try{				
				ServiceUtils.deleteFile(path);
			}catch(Exception e){
				logger.warn("Unable to delete "+path,e);
				throw e;
			}
		}
	}
	protected static void remove(Layer toDestroy)throws Exception{
		GISUtils.deleteLayer(toDestroy.getLayerInfo());
	}
	protected static void remove(WMSContext toDestroy)throws Exception{
		GISUtils.deleteWMSContext(toDestroy.getWmsContextInfo());
	}
	
	// **************** T GENERATION

	protected static Layer generateLayer(AquaMapsObjectData data,
			Submitted object) throws Exception {
		List<StyleGenerationRequest> toGenerateStyle=new ArrayList<StyleGenerationRequest>();
		List<String> toAssociateStyleList=new ArrayList<String>();
		if(object.getType().equals(ObjectType.Biodiversity)){
			toGenerateStyle.add(StyleGenerationRequest.getBiodiversityStyle(data.getMin(), data.getMax(), ClusterScaleType.linear, object.getTitle()));
			toGenerateStyle.add(StyleGenerationRequest.getBiodiversityStyle(data.getMin(), data.getMax(), ClusterScaleType.logarithmic, object.getTitle()));
		}else{
			toAssociateStyleList.add(StyleGenerationRequest.getDefaultDistributionStyle());
		}

		//Meta Data
		logger.debug("Loading metadata details for OBJ ID "+object.getSearchId());
		Map<String,Object> meta=AquaMapsManager.getMetaForGIS(object);
		
		
		//Filling meta with additional details
		//Static image uris
		
		ArrayList<String> imgUris=new ArrayList<String>();
		FileSet fs=ServiceContext.getContext().getPublisher().getById(FileSet.class, object.getFileSetId());
		String publisherHost=ServiceContext.getContext().getPublisher().getWebServerUrl();
		for(org.gcube.application.aquamaps.publisher.impl.model.File f:fs.getFiles()){
			imgUris.add(publisherHost+f.getStoredUri());
		}
		meta.put(AquaMapsManager.META_FILESET_URIS, imgUris);
		//Set geometryCount
		meta.put(AquaMapsManager.META_GEOMETRY_COUNT, new Integer(CSVUtils.countCSVRows(data.getCsvFile(), ',', true).intValue()));
		//Set keywords
		HashMap<String,HashSet<String>> keyMap=new HashMap<String, HashSet<String>>();
		
		for(String s:CSVUtils.CSVToStringList(data.getSpeciesCSVList())){
			Map<String,String> speciesNames=SpeciesManager.getSpeciesNamesById(s);
			for(Entry<String,String> speciesName:speciesNames.entrySet()){
				if(speciesName.getValue()!=null){
					if(!keyMap.containsKey(speciesName.getKey())) keyMap.put(speciesName.getKey(), new HashSet<String>());
					keyMap.get(speciesName.getKey()).add(speciesName.getValue());
				}
			}
		}
		HashSet<String> generalKeys=new HashSet<String>();
		generalKeys.add("AquaMaps");
		generalKeys.add("iMarine");
		generalKeys.add("Ecological niche modelling");
		generalKeys.add(object.getType()+"");
		keyMap.put("General", generalKeys);
		
		meta.put(AquaMapsManager.META_KEYWORDS_MAP, keyMap);
		
		//RETRY POLICY IN CASE OF GEOSERVER FAIL
		boolean generated=false;
		int attemptCount=0;
		int maxAttempt=ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.GEOSERVER_MAX_ATTEMPT);
		LayerInfo layerInfo=null;
		String layerId=null;
		do{
			try{
				attemptCount++;
				GISPublishedItem response=GISUtils
				.generateLayer(new LayerGenerationRequest(		
						data.getCsvFile(),
						object.getType().equals(ObjectType.Biodiversity) ? AquaMapsManager.maxSpeciesCountInACell: HSPECFields.probability + "",
						object.getType().equals(ObjectType.Biodiversity) ? "integer": "real",						
						object.getTitle(),
						object.getType().equals(ObjectType.Biodiversity) ?LayerType.Biodiversity:LayerType.Prediction,
						toGenerateStyle,
						toAssociateStyleList,
						0,meta));
				layerInfo=response.getLayerInfo();
				layerId=response.getMetaId();
				generated=true;
			}catch(Exception e){
				if(attemptCount<=maxAttempt){
					//retry
					long toWaitTimeMinutes=attemptCount*ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.GEOSERVER_WAIT_FOR_RETRY_MINUTES);
					logger.warn("Layer generation failed, this was attempt N "+attemptCount+", going to retry in "+toWaitTimeMinutes+" minutes..");
					try{Thread.sleep(toWaitTimeMinutes*60*1000);}catch(InterruptedException e1){}
				}else throw e;				
			}
		}while(attemptCount<=maxAttempt&&generated==false);
		
		
		ArrayList<String> speciesList=CSVUtils.CSVToStringList(data.getSpeciesCSVList());
		Resource source=SourceManager.getById(object.getSourceHSPEC());
		MetaInformations publisherMeta= new MetaInformations(object.getAuthor(), "", "", object.getTitle(), System.currentTimeMillis(), source.getAlgorithm()+"", source.getGenerationTime());
		Layer toReturn=new Layer(layerInfo.getType(), object.getIsCustomized(),
				layerInfo, new CoverageDescriptor(object.getSourceHSPEC() + "",
						object.getSpeciesCoverage()), publisherMeta);
		toReturn.setSpeciesIds(speciesList.toArray(new String[speciesList.size()]));
		toReturn.setCustomized(object.getIsCustomized());
		toReturn.setId(layerId);
		return toReturn;
	}

	protected static FileSet generateFileSet(AquaMapsObjectData data,
			Submitted object) throws Exception {
		List<org.gcube.application.aquamaps.publisher.impl.model.File> files = new ArrayList<org.gcube.application.aquamaps.publisher.impl.model.File>();
		for (Entry<String, String> toPublishEntry : FileSetUtils.generateFileMap(data.getCsq_str(), object.getTitle()).entrySet()) {
			files.add(new org.gcube.application.aquamaps.publisher.impl.model.File(FileType.Image, toPublishEntry.getValue(), toPublishEntry.getKey()));
		}
		if(files.size()>0){
		logger.debug("Generated FileSet, passed base path "+data.getPath());
		ArrayList<String> speciesList=CSVUtils.CSVToStringList(data.getSpeciesCSVList());
		Resource source=SourceManager.getById(object.getSourceHSPEC());
		MetaInformations meta=new MetaInformations(object.getAuthor(), "", "", object.getTitle(), System.currentTimeMillis(), source.getAlgorithm()+"", source.getGenerationTime());
		
		FileSet toReturn=new FileSet(files, new CoverageDescriptor(
				object.getSourceHSPEC() + "", object.getSpeciesCoverage()),
				data.getPath(),
				meta);
		toReturn.setSpeciesIds(speciesList.toArray(new String[speciesList.size()]));
		toReturn.setCustomized(object.getIsCustomized());
		return toReturn;
		}else throw new Exception ("NO IMAGES WERE GENERATED FOR OBJECT "+object.getSearchId());
	}

//	protected static WMSContext generateWMSContext(int jobId)throws Exception{
//		WMSContext toReturn=null;
//		logger.trace("Creating group for "+jobId);
//		Submitted job= SubmittedManager.getSubmittedById(jobId);
//		ArrayList<String> layerIds=new ArrayList<String>();
//		 
//		for(Submitted obj:JobManager.getObjects(jobId)){
//			if(obj.getGisEnabled()&&obj.getStatus().equals(SubmittedStatus.Completed))
//				layerIds.add(obj.getGisPublishedId());
//		}
//		if(layerIds.isEmpty()){
//			logger.trace("No layer found, skipping group generation for job id : "+jobId);			
//		}else{
//			HashMap<String,String> geoserverLayersAndStyle=new HashMap<String, String>(); 
//			
//			for(String layerId:layerIds){
//				Layer layer=ServiceContext.getContext().getPublisher().getById(Layer.class, layerId);
//				geoserverLayersAndStyle.put(layer.getLayerInfo().getName(), layer.getLayerInfo().getDefaultStyle());
//			}
//			WMSContextInfo wmsContextInfo=GISUtils.generateWMSContext(new GroupGenerationRequest(geoserverLayersAndStyle, ServiceUtils.generateId("WMS_"+job.getTitle(), ""), layerIds));
//			toReturn=new WMSContext(wmsContextInfo, layerIds);
//		}
//		return toReturn;
//	}
	
	
	
	// *********************** DATA RETRIEVAL / STORE

	public static void cleanData(Submitted object)throws Exception{
		DBSession session=null;
		try{
			session = DBSession.getInternalDBSession();
			List<Field> filter = new ArrayList<Field>();
			filter.add(new Field(GENERATION_ID,object.getSearchId()+"",FieldType.INTEGER));
			ResultSet rs=session.executeFilteredQuery(filter, GENERATION_DATA_TABLE, GENERATION_ID, OrderDirection.ASC);
			if(rs.next()){
				logger.info("Deleting generation data for "+object.getSearchId());
				try{ServiceUtils.deleteFile(rs.getString(GENERATION_csq));}catch(Exception e){logger.warn("Unable to delete "+rs.getString(GENERATION_csq),e);}				
				try{ServiceUtils.deleteFile(rs.getString(GENERATION_csv));}catch(Exception e){logger.warn("Unable to delete "+rs.getString(GENERATION_csv),e);}
				for(String path:FileSetUtils.getTempFiles(object.getTitle()))					
					try{ServiceUtils.deleteFile(path);}catch(Exception e){logger.warn("Unable to delete "+path,e);}								
				session.deleteOperation(GENERATION_DATA_TABLE, filter);
			}else logger.info("Unable to detect generation data for submitted "+object.getSearchId());
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}
	
	
	protected static AquaMapsObjectData getData(
			AquaMapsObjectExecutionRequest request) throws Exception {
		DBSession session = null;
		try {
			session = DBSession.getInternalDBSession();
			List<Field> filter = new ArrayList<Field>();
			filter.add(new Field(GENERATION_ID, request.getObject()
					.getSearchId() + "", FieldType.INTEGER));
			ResultSet rs = session.executeFilteredQuery(filter,
					GENERATION_DATA_TABLE, GENERATION_ID, OrderDirection.ASC);
			if (rs.next()) {
				return new AquaMapsObjectData(rs.getInt(GENERATION_ID),
						rs.getString(GENERATION_csq),
						rs.getInt(GENERATION_min), rs.getInt(GENERATION_max),
						rs.getString(GENERATION_csv),
						rs.getString(GENERATION_path),
						rs.getString(GENERATION_SPECIES));
			} else {
				// ************* DATA NOT FOUND, GOING TO GENERATE
				session.close();
				AquaMapsObjectData toStore = null;
				if (request instanceof BiodiversityObjectExecutionRequest) {
					BiodiversityObjectExecutionRequest theRequest = (BiodiversityObjectExecutionRequest) request;
					toStore = getBiodiversityData(theRequest.getObject(),theRequest.getSelectedSpecies(),					
							JobManager.getWorkingHSPEC(theRequest.getObject().getJobId()), theRequest.getThreshold());
				} else {
					DistributionObjectExecutionRequest theRequest = (DistributionObjectExecutionRequest) request;
					Species s=theRequest.getSelectedSpecies().iterator().next();				
					toStore = getDistributionData(theRequest.getObject(),s.getId(),
							JobManager.getWorkingHSPEC(theRequest.getObject()
									.getJobId()));
				}
				return storeData(toStore);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	private static AquaMapsObjectData storeData(AquaMapsObjectData toStore)
	throws Exception {
		DBSession session = null;
		try {
			session = DBSession.getInternalDBSession();
			List<List<Field>> rows = new ArrayList<List<Field>>();
			List<Field> row = new ArrayList<Field>();
			row.add(new Field(GENERATION_ID, toStore.getSubmittedId() + "",FieldType.INTEGER));
			row.add(new Field(GENERATION_csq, toStore.getCsq_str(),FieldType.STRING));
			row.add(new Field(GENERATION_csv, toStore.getCsvFile(),FieldType.STRING));
			row.add(new Field(GENERATION_max, toStore.getMax() + "",FieldType.INTEGER));
			row.add(new Field(GENERATION_min, toStore.getMin() + "",FieldType.INTEGER));
			row.add(new Field(GENERATION_path,toStore.getPath()+"",FieldType.STRING));
			row.add(new Field(GENERATION_SPECIES,toStore.getSpeciesCSVList(),FieldType.STRING));
			rows.add(row);
			session.insertOperation(GENERATION_DATA_TABLE, rows);
			return toStore;
		} catch (Exception e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	
	
	
	// ****************** DATA GENERATION

	private static AquaMapsObjectData getBiodiversityData(
			Submitted objectDescriptor, Set<Species> selectedSpecies,
			String hspecTable, float threshold) throws Exception {
		DBSession session = null;
		try {
			logger.debug("DISTRIBUTION DATA FOR "
					+ objectDescriptor.getSearchId() + ".... STARTED");
			session = DBSession.getInternalDBSession();

			String tableName = ServiceUtils.generateId("s", "");
			PreparedStatement prep = null;

			session.createTable(tableName,
					new String[] { SpeciesOccursumFields.speciesid
					+ " varchar(50) PRIMARY KEY" });

			JobManager.addToDropTableList(objectDescriptor.getJobId(),
					tableName);
			List<List<Field>> toInsertSpecies = new ArrayList<List<Field>>();
			ArrayList<String> scientificNames=new ArrayList<String>();
			
			for (Species spec : selectedSpecies) {
				List<Field> row = new ArrayList<Field>();
				Species s=SpeciesManager.getSpeciesById(true, false, spec.getId(), 0);
				row.add(new Field(SpeciesOccursumFields.speciesid + "", s
						.getId(), FieldType.STRING));
				toInsertSpecies.add(row);
				String scientificName=s.getScientificName();
				scientificNames.add(scientificName);
			}
			session.insertOperation(tableName, toInsertSpecies);

			prep = session.preparedStatement(clusteringBiodiversityQuery(
					hspecTable, tableName));
			prep.setFloat(1, threshold);
			ResultSet rs = prep.executeQuery();

			if (rs.first()) {

				String path = SpeciesManager.getCommonTaxonomy(selectedSpecies)+File.separator+ServiceUtils.generateId(objectDescriptor.getTitle(), "");

				// ******PERL
				String clusterFile = FileSetUtils.createClusteringFile(
						objectDescriptor.getSearchId(),
						objectDescriptor.getJobId(),
						FileSetUtils.clusterize(rs, 2, 1, 2, true),
						objectDescriptor.getTitle());

				rs.first();
				Integer max = rs.getInt(AquaMapsManager.maxSpeciesCountInACell);
				rs.last();
				Integer min = rs.getInt(AquaMapsManager.maxSpeciesCountInACell);

				logger.info("Biodiversity query for object ID "+objectDescriptor.getSearchId()+" FOUND min : "+min+" max : "+max);
				
				
				String csvFile = ServiceContext.getContext()
				.getPersistenceRoot()
				+ File.separator
				+ objectDescriptor.getJobId()
				+ File.separator
				+ objectDescriptor.getTitle() + ".csv";
				FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
				CSVUtils.resultSetToCSVFile(rs, csvFile, false);

				// ******GIS

				Collections.sort(scientificNames);
				
				
				return new AquaMapsObjectData(objectDescriptor.getSearchId(),
						clusterFile, min, max, csvFile, path,CSVUtils.listToCSV(scientificNames));
			} else
				return null;

		} catch (Exception e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	private static AquaMapsObjectData getDistributionData(Submitted objectDescriptor,String speciesId, String hspecTable)throws Exception{
		DBSession session=null;
		try{
			logger.debug("DISTRIBUTION DATA FOR "+objectDescriptor.getSearchId()+".... STARTED");
			session=DBSession.getInternalDBSession();
			String clusteringQuery=clusteringDistributionQuery(hspecTable);
			PreparedStatement ps= session.preparedStatement(clusteringQuery);
			ps.setString(1,speciesId);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){

				Species s=SpeciesManager.getSpeciesById(true, false, speciesId, 0);
				String path=objectDescriptor.getSourceHSPEC()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.kingdom+"").value()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.phylum+"").value()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.classcolumn+"").value()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.ordercolumn+"").value()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.familycolumn+"").value()+File.separator+
				s.getId()+(objectDescriptor.getIsCustomized()?ServiceUtils.generateId(objectDescriptor.getAuthor(), ""):"");
				
				String scientificName=s.getScientificName();
				
				String clusterFile=FileSetUtils.createClusteringFile(objectDescriptor.getSearchId(),objectDescriptor.getJobId()
						,FileSetUtils.clusterize(rs, 2, 1, 2,false),objectDescriptor.getTitle());

				String csvFile=ServiceContext.getContext().getPersistenceRoot()+File.separator+
				objectDescriptor.getJobId()+File.separator+objectDescriptor.getTitle()+".csv";
				FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
				CSVUtils.resultSetToCSVFile(rs, csvFile,false);

				logger.debug("DISTRIBUTION DATA FOR "+objectDescriptor.getSearchId()+".... COMPLETED");
				return new AquaMapsObjectData(objectDescriptor.getSearchId(), clusterFile, 0, 0, csvFile,path,scientificName);
			}else return null;
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}

	// ************************ QUERIES

	public static String clusteringDistributionQuery(String hspecName) {
		String query = "Select " + HCAF_SFields.csquarecode + ", "
		+ HSPECFields.probability + "  FROM " + hspecName + " where "
		+ hspecName + "." + SpeciesOccursumFields.speciesid
		+ "=?  ORDER BY " + HSPECFields.probability + " DESC";
		logger.debug("clusteringDistributionQuery: " + query);
		return query;
	}

	private static String clusteringBiodiversityQuery(String hspecName,
			String tmpTable) {

		String query = "Select " + HCAF_SFields.csquarecode + ", count(k."
		+ SpeciesOccursumFields.speciesid + ") AS "
		+ AquaMapsManager.maxSpeciesCountInACell + " FROM " + hspecName
		+ " as k Where  k." + SpeciesOccursumFields.speciesid
		+ " in (select " + SpeciesOccursumFields.speciesid + " from "
		+ tmpTable + " ) and " + HSPECFields.probability
		+ " > ? GROUP BY " + HCAF_SFields.csquarecode + " order by "
		+ AquaMapsManager.maxSpeciesCountInACell + " DESC";

		logger.debug("clusteringBiodiversityQuery: " + query);
		return query;
	}

}

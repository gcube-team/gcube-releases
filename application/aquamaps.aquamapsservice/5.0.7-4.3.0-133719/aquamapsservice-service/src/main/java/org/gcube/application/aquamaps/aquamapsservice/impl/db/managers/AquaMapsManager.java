package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.AquaMapsObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;

public class AquaMapsManager extends SubmittedManager{

	public static String maxSpeciesCountInACell="maxspeciescountinacell";
	
	public static final String META_ALGORITHM="ALGORITHM";
	public static final String META_SOURCE_TITLE="SOURCE_TITLE";
	public static final String META_SOURCE_TABLENAME="SOURCE_TABLENAME";
	public static final String META_SOURCE_TIME="SOURCE_TIME";
	public static final String META_DATE="META_DATE";
	public static final String META_TITLE="META_TITLE";
	public static final String META_OBJECT_TYPE="META_OBJECT_TYPE";
	public static final String META_AUTHOR="META_AUTHOR";
	
	//****** Generated at gis Generation Time
	public static final String META_FILESET_URIS="META_FILESET_URIS";
	public static final String META_GEOMETRY_COUNT="META_GEOMETRY_COUNT";
	public static final String META_KEYWORDS_MAP="META_KEYWORDS_MAP";
	
	
	public static int insertRequests(
			List<AquaMapsObjectExecutionRequest> requests) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			session.disableAutoCommit();
			ArrayList<List<Field>> rows=new ArrayList<List<Field>>();
			ArrayList<List<Field>> keys=new ArrayList<List<Field>>();
			for(AquaMapsObjectExecutionRequest request:requests){
				ArrayList<Field> fields=new ArrayList<Field>();
				fields.add(request.getObject().getField(SubmittedFields.serializedrequest));
				fields.add(request.getObject().getField(SubmittedFields.status));
				rows.add(fields);
				ArrayList<Field> key=new ArrayList<Field>();
				key.add(request.getObject().getField(SubmittedFields.searchid));
				keys.add(key);
			}
			int toReturn= session.updateOperation(submittedTable, keys, rows);
			session.commit();
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}

	public static List<Submitted> getObjectsByCoverage(Integer hspecId, String md5SpeciesCoverage, Boolean isGIS, boolean includeCustomized)throws Exception{
		DBSession session=null;
		try{
			logger.debug("looking for objects by coverage HSPEC ID="+hspecId+", md5="+md5SpeciesCoverage+", GIS="+isGIS+", custom="+includeCustomized);
			session=DBSession.getInternalDBSession();
			List<Field> filter=new ArrayList<Field>();
			if(hspecId!=null)filter.add(new Field(SubmittedFields.sourcehspec+"",hspecId+"",FieldType.INTEGER));
			if(md5SpeciesCoverage!=null)filter.add(new Field(SubmittedFields.speciescoverage+"",md5SpeciesCoverage,FieldType.STRING));
			if(!includeCustomized)filter.add(new Field(SubmittedFields.iscustomized+"",false+"",FieldType.BOOLEAN));
			filter.add(new Field(SubmittedFields.status+"",SubmittedStatus.Completed+"",FieldType.STRING));
			filter.add(new Field(SubmittedFields.isaquamap+"",true+"",FieldType.BOOLEAN));
			if(isGIS!=null)filter.add(new Field(SubmittedFields.gisenabled+"",isGIS+"",FieldType.BOOLEAN));
			return Submitted.loadResultSet(session.executeFilteredQuery(filter, submittedTable, SubmittedFields.searchid+"", OrderDirection.ASC));
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}
	
	
	public static AquaMapsObject loadObject(int objId,boolean loadFileSet,boolean loadLayers)throws Exception{
		if(!isAquaMap(objId)) throw new Exception("Selected ID "+objId+" doesn't refere to an AquaMapsObject");
		Submitted submittedObj=getSubmittedById(objId);
		logger.info("Loading object "+submittedObj);
		AquaMapsObject toReturn=(AquaMapsObject) AquaMapsXStream.deSerialize(submittedObj.getSerializedObject());
		toReturn.setId(objId);
		toReturn.setAlgorithmType(SourceManager.getById(submittedObj.getSourceHSPEC()).getAlgorithm());
//		toReturn.setAuthor(submittedObj.getAuthor());
//		toReturn.setDate(submittedObj.getSubmissionTime());
//		toReturn.setGis(submittedObj.getGisEnabled());
		toReturn.setStatus(submittedObj.getStatus());
//		toReturn.setThreshold()
//		toReturn.setSelectedSpecies(selectedSpecies)
		if(loadFileSet){
			try{
				String publisherHost=ServiceContext.getContext().getPublisher().getWebServerUrl();
			FileSet fileSet=ServiceContext.getContext().getPublisher().getById(FileSet.class, submittedObj.getFileSetId());
			for(org.gcube.application.aquamaps.publisher.impl.model.File f: fileSet.getFiles())
			toReturn.getImages().add(new org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File(
					org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FileType.valueOf(f.getType()+""),
					publisherHost+f.getStoredUri(),f.getName()));
			}catch(Exception e){
				logger.warn("Unablet o load fileset for obj "+objId);
			}
		}
		if(loadLayers&&submittedObj.getGisEnabled()){
				try{					
					Layer layer=ServiceContext.getContext().getPublisher().getById(Layer.class, submittedObj.getGisPublishedId());
					toReturn.getLayers().add(layer.getLayerInfo());
				}catch(Exception e){
					logger.warn("Unable to load Layer "+submittedObj.getGisPublishedId());
				}
			}
		return toReturn;
	}
	
	public static Map<String,Object> getMetaForGIS(Submitted obj) throws Exception{
		HashMap<String,Object> toReturn=new HashMap<String,Object>();	
		toReturn.put(META_AUTHOR, obj.getAuthor());
		toReturn.put(META_DATE, new Date(System.currentTimeMillis()));
		toReturn.put(META_OBJECT_TYPE, obj.getType());
		toReturn.put(META_TITLE, obj.getTitle());
		
		Resource hspec = SourceManager.getById(obj.getSourceHSPEC());
		toReturn.put(META_ALGORITHM, hspec.getAlgorithm());
		toReturn.put(META_SOURCE_TITLE, hspec.getTitle());
		toReturn.put(META_SOURCE_TIME, new Date(hspec.getGenerationTime()));
		toReturn.put(META_SOURCE_TABLENAME, hspec.getTableName());		
		
		return toReturn;
	}
	
}

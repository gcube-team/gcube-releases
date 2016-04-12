package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.stubs.HspecGroupGenerationRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AreaType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.PerturbationType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FileType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FilterType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.common.gis.datamodel.enhanced.BoundsInfo;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube.common.gis.datamodel.enhanced.TransectInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PortTypeTranslations {

	private static Logger logger = LoggerFactory.getLogger(PortTypeTranslations.class);


	//********************* FROM Stubs

	public static final Field fromStubs(org.gcube_system.namespaces.application.aquamaps.types.Field stub){
		if(stub==null) return new Field();
		return new Field(stub.getName(), stub.getValue(), FieldType.valueOf(stub.getType()+""));
	}

	public static final ArrayList<Field> fromStubs(org.gcube_system.namespaces.application.aquamaps.types.FieldArray stub){	
		ArrayList<Field> toReturn=new ArrayList<Field>();
		if((stub!=null)&&(stub.getFields()!=null))
			for(org.gcube_system.namespaces.application.aquamaps.types.Field f:stub.getFields())
				toReturn.add(fromStubs(f));
		return toReturn;
	}

	public static final Job fromStubs(org.gcube_system.namespaces.application.aquamaps.types.Job toLoad){
		Job toReturn=new Job();
		toReturn.setAuthor(toLoad.getAuthor());
		toReturn.setDate(toLoad.getDate());

		if((toLoad.getWeights()!=null)&&(toLoad.getWeights().getEnvelopeWeightList()!=null))
			for(org.gcube_system.namespaces.application.aquamaps.types.EnvelopeWeights weights:toLoad.getWeights().getEnvelopeWeightList()){
				String speciesID=weights.getSpeciesId();
				if(!toReturn.getEnvelopeWeights().containsKey(speciesID)) 
					toReturn.getEnvelopeWeights().put(speciesID, new HashMap<EnvelopeFields, Field>());
				for(Field f: fromStubs(weights.getWeights()))
					toReturn.getEnvelopeWeights().get(speciesID).put(EnvelopeFields.valueOf(f.name()), f);
			}
		if((toLoad.getEnvelopCustomization()!=null)&&(toLoad.getEnvelopCustomization().getPerturbationList()!=null))
			for(org.gcube_system.namespaces.application.aquamaps.types.Perturbation pert:toLoad.getEnvelopCustomization().getPerturbationList()){
				String speciesID=pert.getToPerturbId();
				if(!toReturn.getEnvelopeCustomization().containsKey(speciesID))
					toReturn.getEnvelopeCustomization().put(speciesID, new HashMap<String, Perturbation>());
				toReturn.getEnvelopeCustomization().get(speciesID).put(pert.getField(), fromStubs(pert));
			}

		toReturn.setRelated(fromStubs(toLoad.getRelatedResources()));
		toReturn.setSelectedAreas(fromStubs(toLoad.getSelectedAreas()));
		toReturn.setId(toLoad.getId());
		toReturn.setName(toLoad.getName());
		toReturn.setSourceHCAF(fromStubs(toLoad.getHcaf()));
		toReturn.setSourceHSPEN(fromStubs(toLoad.getHspen()));
		toReturn.setSourceHSPEC(fromStubs(toLoad.getHspec()));
		toReturn.setStatus(SubmittedStatus.valueOf(toLoad.getStatus()));

		toReturn.setAquaMapsObjectList(fromStubs(toLoad.getAquaMapList()));
		toReturn.getSelectedSpecies().addAll(fromStubs(toLoad.getSelectedSpecies()));


		toReturn.setIsGis(toLoad.isGis());
		toReturn.setWmsContextId(toLoad.getGroupId());
		return toReturn;
	}


	public static final Perturbation fromStubs(org.gcube_system.namespaces.application.aquamaps.types.Perturbation toLoad){
		Perturbation pert=new Perturbation();
		pert.setPerturbationValue(toLoad.getValue());
		pert.setType(PerturbationType.valueOf(toLoad.getType()));
		return pert;
	}


	public static final Resource fromStubs(org.gcube_system.namespaces.application.aquamaps.types.Resource toLoad){
		Resource res=new Resource(ResourceType.HCAF,0);

		try{res.setAlgorithm(AlgorithmType.valueOf(toLoad.getAlgorithm()));}
		catch(Exception e){res.setAlgorithm(Resource.getDefaultAlgorithmType());}
		res.setAuthor(toLoad.getAuthor());
		res.setGenerationTime(toLoad.getDate());
		res.setDescription(toLoad.getDescription());
		res.setDisclaimer(toLoad.getDisclaimer());
		try{
			res.getParameters().addAll(Field.fromJSONArray(new JSONArray(toLoad.getParameters())));			
		}catch(Exception e){
			logger.warn("Unable to parse parameters",e);
		}
		res.setProvenance(toLoad.getProvenance());
		res.setSearchId(toLoad.getSearchId());
		try{
			res.setSourceHCAFIds(CSVUtils.CSVTOIntegerList(toLoad.getSourceHCAFIds()));			
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.getSourceHCAFIds(),e);
		}
		try{
			res.setSourceHSPENIds(CSVUtils.CSVTOIntegerList(toLoad.getSourceHSPENIds()));
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.getSourceHSPENIds(),e);
		}
		try{
			res.setSourceHSPECIds(CSVUtils.CSVTOIntegerList(toLoad.getSourceHSPECIds()));
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.getSourceHSPECIds(),e);
		}
		try{
			res.setSourceOccurrenceCellsIds(CSVUtils.CSVTOIntegerList(toLoad.getSourceOccurrenceCellsIds()));
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.getSourceOccurrenceCellsIds(),e);
		}

		try{
			res.setSourceHCAFTables(CSVUtils.CSVToStringList(toLoad.getSourceHCAFTables()));
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.getSourceHCAFTables(),e);
		}
		try{
			res.setSourceHSPECTables(CSVUtils.CSVToStringList(toLoad.getSourceHSPECTables()));
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.getSourceHSPECTables(),e);
		}
		try{
			res.setSourceHSPENTables(CSVUtils.CSVToStringList(toLoad.getSourceHSPENTables()));
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.getSourceHSPENTables(),e);
		}
		try{
			res.setSourceOccurrenceCellsTables(CSVUtils.CSVToStringList(toLoad.getSourceOccurrenceCellsTables()));
		}catch(Exception e){
			logger.warn("Unable to load CSVLIST "+toLoad.getSourceOccurrenceCellsTables(),e);
		}
		res.setStatus(ResourceStatus.valueOf(toLoad.getStatus()));
		res.setTableName(toLoad.getTableName());
		res.setTitle(toLoad.getTitle());
		res.setType(ResourceType.valueOf(toLoad.getType()));
		res.setDefaultSource(toLoad.isDefaultSource());		
		res.setRowCount(toLoad.getPercent());
		return res;
	}


	public static final File fromStubs(org.gcube_system.namespaces.application.aquamaps.types.File toLoad){
		return new File(FileType.valueOf(toLoad.getType()),toLoad.getUrl(),toLoad.getName());
	}

	public static ArrayList<File> fromStubs(org.gcube_system.namespaces.application.aquamaps.types.FileArray toLoad){
		ArrayList<File> toReturn= new ArrayList<File>();
		if((toLoad!=null)&&(toLoad.getFileList()!=null))
			for(org.gcube_system.namespaces.application.aquamaps.types.File f: toLoad.getFileList())
				toReturn.add(fromStubs(f));
		return toReturn;
	}


	public static final Area fromStubs(org.gcube_system.namespaces.application.aquamaps.types.Area toLoad){
		return new Area(AreaType.valueOf(toLoad.getType()), toLoad.getCode(), toLoad.getName());
	}

	public static final Set<Area> fromStubs(org.gcube_system.namespaces.application.aquamaps.types.AreasArray toLoad){
		Set<Area> toReturn= new HashSet<Area>();
		if((toLoad!=null)&&(toLoad.getAreasList()!=null))
			for(org.gcube_system.namespaces.application.aquamaps.types.Area a:toLoad.getAreasList())
				toReturn.add(fromStubs(a));
		return toReturn;
	}


	public static final AquaMapsObject fromStubs(org.gcube_system.namespaces.application.aquamaps.types.AquaMap toLoad){
		AquaMapsObject obj=new AquaMapsObject();
		obj.setAuthor(toLoad.getAuthor());
		obj.getBoundingBox().parse(toLoad.getBoundingBox());
		obj.setDate(toLoad.getDate());
		obj.getImages().addAll(fromStubs(toLoad.getImages()));
		obj.getAdditionalFiles().addAll(fromStubs(toLoad.getAdditionalFiles()));
		obj.getSelectedSpecies().addAll(fromStubs(toLoad.getSelectedSpecies()));
		obj.setGis(toLoad.isGis());
		obj.setId(toLoad.getId());
		obj.setName(toLoad.getName());
		obj.setStatus(SubmittedStatus.valueOf(toLoad.getStatus()));
		obj.setThreshold(toLoad.getThreshold());
		obj.setType(ObjectType.valueOf(toLoad.getType()));
		obj.setLayers(fromStubs(toLoad.getLayers()));
		obj.setAlgorithmType(AlgorithmType.valueOf(toLoad.getAlgorithmType()));
		return obj;
	}






	public static final Species fromStubs(org.gcube_system.namespaces.application.aquamaps.types.Specie toLoad){
		Species spec=new Species(toLoad.getId()); 
		spec.getAttributesList().addAll(fromStubs(toLoad.getAdditionalField()));
		return spec;
	}

	public static final ArrayList<Species> fromStubs(org.gcube_system.namespaces.application.aquamaps.types.SpeciesArray toLoad){
		ArrayList<Species> toReturn = new ArrayList<Species>();
		if((toLoad!=null)&&(toLoad.getSpeciesList()!=null))
			for(org.gcube_system.namespaces.application.aquamaps.types.Specie s:toLoad.getSpeciesList())
				toReturn.add(fromStubs(s));
		return toReturn;
	}

	public static final ArrayList<AquaMapsObject> fromStubs(org.gcube_system.namespaces.application.aquamaps.types.AquaMapArray toLoad){
		ArrayList<AquaMapsObject> toReturn= new ArrayList<AquaMapsObject>();
		if((toLoad!=null)&&(toLoad.getAquaMapList()!=null))
			for(org.gcube_system.namespaces.application.aquamaps.types.AquaMap a: toLoad.getAquaMapList())
				toReturn.add(fromStubs(a));
		return toReturn;
	}



	public static final Filter fromStubs(org.gcube_system.namespaces.application.aquamaps.types.Filter toLoad){
		return new Filter(FilterType.valueOf(toLoad.getType()), new Field(toLoad.getName(),toLoad.getValue(),FieldType.valueOf(toLoad.getFieldType())));		
	}

	public static final ArrayList<Filter> fromStubs(org.gcube_system.namespaces.application.aquamaps.types.FilterArray toLoad){
		ArrayList<Filter> toReturn=new ArrayList<Filter>();
		if((toLoad!=null)&&(toLoad.getFilterList()!=null))
			for(org.gcube_system.namespaces.application.aquamaps.types.Filter f: toLoad.getFilterList())toReturn.add(fromStubs(f));
		return toReturn;
	}
	//************************* DM

	public static final Analysis fromStubs(org.gcube_system.namespaces.application.aquamaps.types.Analysis stubs){
		Analysis toReturn=new Analysis();
		toReturn.setArchiveLocation(stubs.getArchiveLocation());
		toReturn.setAuthor(stubs.getAuthor());
		toReturn.setCurrentphasepercent(stubs.getCurrentPhasePercent());
		toReturn.setDescription(stubs.getDescription());
		toReturn.setEndtime(stubs.getEndTime());
		toReturn.setId(stubs.getId());
		toReturn.setReportID(CSVUtils.CSVTOIntegerList(stubs.getReportIds()));
		toReturn.setSources(CSVUtils.CSVTOIntegerList(stubs.getSources()));
		toReturn.setStarttime(stubs.getStartTime());
		toReturn.setStatus(SubmittedStatus.valueOf(stubs.getStatus()));
		toReturn.setSubmissiontime(stubs.getSubmissionTime());
		toReturn.setTitle(stubs.getTitle());		
		toReturn.setType(CSVUtils.CSVToStringList(stubs.getType()));
		toReturn.setPerformedAnalysis(CSVUtils.CSVToStringList(stubs.getPerformedAnalysis()));
		return toReturn;
	}

	public static final SourceGenerationRequest fromStubs(HspecGroupGenerationRequestType request) throws JSONException{
		SourceGenerationRequest toReturn=new SourceGenerationRequest();
		ArrayList<AlgorithmType> parsed= new ArrayList<AlgorithmType>();
		for (String s:CSVUtils.CSVToStringList(request.getAlgorithms())) parsed.add(AlgorithmType.valueOf(s));
		toReturn.setAlgorithms(parsed);
		toReturn.setAuthor(request.getAuthor());
		toReturn.setBackendURL(request.getBackendUrl());
		toReturn.setDescription(request.getDescription());
		toReturn.setEnvironmentConfiguration((HashMap<String, String>) AquaMapsXStream.getXMLInstance().fromXML(request.getEnvironmentConfiguration()));
		toReturn.setExecutionEnvironment(request.getExecutionEnvironment());
		toReturn.setGenerationname(request.getGenerationName());
		toReturn.setHcafIds(CSVUtils.CSVTOIntegerList(request.getHcafIds()));
		toReturn.setHspenIds(CSVUtils.CSVTOIntegerList(request.getHspenIds()));
		toReturn.setOccurrenceCellIds(CSVUtils.CSVTOIntegerList(request.getOccurrenceCellsIds()));
		toReturn.setExecutionParameters(Field.fromJSONArray(new JSONArray(request.getExecutionParameters())));
		toReturn.setGenerationParameters(Field.fromJSONArray(new JSONArray(request.getGenerationParameters())));
		toReturn.setLogic(LogicType.valueOf(request.getLogic()));
		toReturn.setNumPartitions(request.getNumPartitions());
		toReturn.setSubmissionBackend(request.getSubmissionBackend());
		return toReturn;
	}


	public static final CustomQueryDescriptorStubs fromStubs(org.gcube.application.aquamaps.aquamapsservice.stubs.CustomQueryDescriptorStubs stubs){
		CustomQueryDescriptorStubs toReturn=new CustomQueryDescriptorStubs();
		toReturn.actualTableName(stubs.getActualTableName());
		toReturn.creationTime(stubs.getCreationTime());
		toReturn.errorMessage(stubs.getErrorMsg());
		toReturn.fields(new FieldArray(fromStubs(stubs.getFields())));
		toReturn.lastAccess(stubs.getLastAccess());
		toReturn.query(stubs.getQuery());
		toReturn.rows(stubs.getRows());
		toReturn.status(ExportStatus.valueOf(stubs.getStatus()+""));
		toReturn.user(stubs.getUser());
		return toReturn;
	}


	//************************* CORE 


	public static ArrayList<String> fromStubs(org.gcube.common.core.types.StringArray sources) {
		try {
			ArrayList<String> res = new ArrayList<String>();
			for (String style: sources.getItems()){
				res.add(style);
			}
			return res;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}





	//***************** GIS ******************

	public static final LayerInfo fromStubs(org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType toLoad){
		LayerInfo toReturn=new LayerInfo();
		toReturn.setName(toLoad.getName());
		toReturn.setTitle(toLoad.getTitle());
		toReturn.set_abstract(toLoad.get_abstract());
		toReturn.setUrl(toLoad.getUrl());
		toReturn.setServerLogin(toLoad.getServerLogin());
		toReturn.setServerPassword(toLoad.getServerPassword());
		toReturn.setServerProtocol(toLoad.getServerProtocol());
		toReturn.setServerType(toLoad.getServerType());
		toReturn.setSrs(toLoad.getSrs());
		if(toLoad.getType() != null && !toLoad.getType().getValue().contentEquals("")) toReturn.setType(LayerType.valueOf(toLoad.getType().getValue()));
		toReturn.setTrasparent(toLoad.isTrasparent());
		toReturn.setBaseLayer(toLoad.isBaseLayer());
		toReturn.setBuffer(toLoad.getBuffer());
		toReturn.setHasLegend(toLoad.isHasLegend());
		toReturn.setVisible(toLoad.isVisible());
		toReturn.setSelected(toLoad.isSelected());
		toReturn.setQueryable(toLoad.isQueryable());
		if(toLoad.getMaxExtent() != null) toReturn.setMaxExtent(fromStubs(toLoad.getMaxExtent()));
		if(toLoad.getMinExtent() != null) toReturn.setMinExtent(fromStubs(toLoad.getMinExtent()));
		toReturn.setDefaultStyle(toLoad.getDefaultStyle());
		toReturn.setOpacity(toLoad.getOpacity());
		if (toLoad.getStyles() != null) toReturn.setStyles(fromStubs(toLoad.getStyles()));
		if (toLoad.getTransect() != null) toReturn.setTransect(fromStubs(toLoad.getTransect()));
		return toReturn;
	}

	public static final BoundsInfo fromStubs(org.gcube_system.namespaces.application.aquamaps.gistypes.BoundsInfoType toLoad){
		BoundsInfo toReturn=new BoundsInfo();
		toReturn.setCrs(toLoad.getCrs());
		toReturn.setMaxx(toLoad.getMaxx());
		toReturn.setMaxy(toLoad.getMaxy());
		toReturn.setMinx(toLoad.getMinx());
		toReturn.setMiny(toLoad.getMiny());
		return toReturn;
	}


	public static final TransectInfo fromStubs(org.gcube_system.namespaces.application.aquamaps.gistypes.TransectInfoType toLoad){
		TransectInfo toReturn=new TransectInfo();
		toReturn.setFields(fromStubs(toLoad.getFields()));
		toReturn.setMaxelements(toLoad.getMaxelements());
		toReturn.setMinimumgap(toLoad.getMinimumgap());
		toReturn.setTable(toLoad.getTable());
		toReturn.setEnabled(toLoad.isEnabled());
		return toReturn;
	}
	public static ArrayList<LayerInfo> fromStubs(org.gcube_system.namespaces.application.aquamaps.gistypes.LayerArray sources) {


		ArrayList<LayerInfo> res = new ArrayList<LayerInfo>();

		if((sources!=null)&&(sources.getName()!=null))
			for (org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType layer: sources.getName())
				res.add(fromStubs(layer));

		return res;
	}






	//********************* TO Stubs


	public static final org.gcube_system.namespaces.application.aquamaps.types.Field toStubs(Field obj){
		return new org.gcube_system.namespaces.application.aquamaps.types.Field(obj.name(),org.gcube_system.namespaces.application.aquamaps.types.FieldType.fromString(obj.type()+""),obj.value());
	}





	public static org.gcube_system.namespaces.application.aquamaps.types.Resource toStubs(Resource obj) throws JSONException{
		org.gcube_system.namespaces.application.aquamaps.types.Resource toReturn=new org.gcube_system.namespaces.application.aquamaps.types.Resource();

		toReturn.setAlgorithm(obj.getAlgorithm()+"");
		toReturn.setAuthor(obj.getAuthor());
		toReturn.setDate(obj.getGenerationTime()!=null?obj.getGenerationTime():0);
		toReturn.setDescription(obj.getDescription());
		toReturn.setDisclaimer(obj.getDisclaimer());
		toReturn.setParameters(Field.toJSONArray(obj.getParameters()).toString());
		toReturn.setProvenance(obj.getProvenance());
		toReturn.setSearchId(obj.getSearchId());
		toReturn.setSourceHCAFIds(CSVUtils.listToCSV(obj.getSourceHCAFIds()));
		toReturn.setSourceHSPENIds(CSVUtils.listToCSV(obj.getSourceHSPENIds()));
		toReturn.setSourceHSPECIds(CSVUtils.listToCSV(obj.getSourceHSPECIds()));
		toReturn.setSourceHSPENTables(CSVUtils.listToCSV(obj.getSourceHSPENTables()));
		toReturn.setSourceHCAFTables(CSVUtils.listToCSV(obj.getSourceHCAFTables()));
		toReturn.setSourceHSPECTables(CSVUtils.listToCSV(obj.getSourceHSPECTables()));
		toReturn.setStatus(obj.getStatus()+"");
		toReturn.setTableName(obj.getTableName());
		toReturn.setTitle(obj.getTitle());
		toReturn.setType(obj.getType().toString());
		toReturn.setDefaultSource(obj.getDefaultSource());
		toReturn.setSourceOccurrenceCellsIds(CSVUtils.listToCSV(obj.getSourceOccurrenceCellsIds()));
		toReturn.setSourceOccurrenceCellsTables(CSVUtils.listToCSV(obj.getSourceOccurrenceCellsTables()));
		toReturn.setPercent(obj.getRowCount());
		return toReturn;
	}


	public static final org.gcube_system.namespaces.application.aquamaps.types.Submitted toStubs(Submitted obj){
		org.gcube_system.namespaces.application.aquamaps.types.Submitted toReturn=new org.gcube_system.namespaces.application.aquamaps.types.Submitted();
		toReturn.setAuthor(obj.getAuthor());
		toReturn.setSubmissionTime(obj.getSubmissionTime());
		toReturn.setEndTime(obj.getEndTime());
		toReturn.setStartTime(obj.getStartTime());
		toReturn.setGisEnabled(obj.getGisEnabled());
		toReturn.setIsAquaMap(obj.getIsAquaMap());
		toReturn.setJobId(obj.getJobId());
		toReturn.setSaved(obj.getSaved());
		toReturn.setSearchId(obj.getSearchId());
		toReturn.setSelectionCriteria(obj.getSelectionCriteria());
		toReturn.setSourceHCAF(obj.getSourceHCAF());
		toReturn.setSourceHSPEC(obj.getSourceHSPEC());
		toReturn.setSourceHSPEN(obj.getSourceHSPEN());
		toReturn.setStatus(obj.getStatus()+"");
		toReturn.setTitle(obj.getTitle());
		toReturn.setType(obj.getType()+"");	
		toReturn.setPublishedIds(obj.getGisPublishedId());
		toReturn.setSpeciesCoverage(obj.getSpeciesCoverage());
		toReturn.setFileSetId(obj.getFileSetId());
		toReturn.setCustomized(obj.getIsCustomized());
		toReturn.setForceRegeneration(obj.isForceRegeneration());
		return toReturn;		
	}


	public static final org.gcube_system.namespaces.application.aquamaps.types.AquaMap toStubs(AquaMapsObject obj){
		org.gcube_system.namespaces.application.aquamaps.types.AquaMap toReturn= new org.gcube_system.namespaces.application.aquamaps.types.AquaMap();
		toReturn.setAuthor(obj.getAuthor());
		toReturn.setBoundingBox(obj.getBoundingBox().toString());
		toReturn.setDate(obj.getDate()!=null?obj.getDate():0);
		toReturn.setGis(obj.getGis());
		toReturn.setId(obj.getId());
		toReturn.setName(obj.getName());
		toReturn.setAdditionalFiles(toFileArray(obj.getAdditionalFiles()));
		toReturn.setImages(toFileArray(obj.getImages()));
		toReturn.setSelectedSpecies(toSpeciesArray(obj.getSelectedSpecies()));
		toReturn.setStatus(obj.getStatus()+"");
		toReturn.setThreshold(obj.getThreshold());
		toReturn.setType(obj.getType().toString());
		toReturn.setLayers(toLayerArray(obj.getLayers()));
		toReturn.setAlgorithmType(obj.getAlgorithmType()+"");
		return toReturn;
	}


	public static final org.gcube_system.namespaces.application.aquamaps.types.File toStubs(File obj){
		return new org.gcube_system.namespaces.application.aquamaps.types.File(obj.getName(), obj.getType()+"", obj.getUuri());
	}

	public static final org.gcube_system.namespaces.application.aquamaps.types.Specie toStubs(Species obj){
		return new org.gcube_system.namespaces.application.aquamaps.types.Specie(toFieldArray(obj.getAttributesList()),obj.getId());
	}


	public static final org.gcube_system.namespaces.application.aquamaps.types.Analysis toStubs(Analysis obj){
		org.gcube_system.namespaces.application.aquamaps.types.Analysis toReturn=new org.gcube_system.namespaces.application.aquamaps.types.Analysis();
		toReturn.setArchiveLocation(obj.getArchiveLocation());
		toReturn.setAuthor(obj.getAuthor());
		toReturn.setCurrentPhasePercent(obj.getCurrentphasepercent());
		toReturn.setDescription(obj.getDescription());
		toReturn.setEndTime(obj.getEndtime());
		toReturn.setId(obj.getId());
		toReturn.setReportIds(CSVUtils.listToCSV(obj.getReportID()));
		toReturn.setSources(CSVUtils.listToCSV(obj.getSources()));
		toReturn.setStartTime(obj.getStarttime());
		toReturn.setStatus(obj.getStatus()+"");
		toReturn.setSubmissionTime(obj.getSubmissiontime());
		toReturn.setTitle(obj.getTitle());
		toReturn.setType(CSVUtils.listToCSV(obj.getType()));
		toReturn.setPerformedAnalysis(CSVUtils.listToCSV(obj.getPerformedAnalysis()));
		return toReturn;		
	}

	public static final org.gcube_system.namespaces.application.aquamaps.types.Map toStubs(AquaMap obj) throws JSONException{
		org.gcube_system.namespaces.application.aquamaps.types.Map toReturn=new org.gcube_system.namespaces.application.aquamaps.types.Map();
		toReturn.setAuthor(obj.getAuthor());
		toReturn.setCoverage(obj.getCoverage());
		toReturn.setCreationDate(obj.getCreationDate());
		toReturn.setFileSetIt(obj.getFileSetId());
		toReturn.setGis(obj.isGis());
		if(obj.isGis())	toReturn.setGisLayer(toStubs(obj.getLayer()));
		toReturn.setLayerId(obj.getLayerId());
		toReturn.setMapType(obj.getMapType()+"");		
		toReturn.setResource(toStubs(obj.getResource()));
		toReturn.setSpeciesListCSV(obj.getSpeciesCsvList());
		toReturn.setStaticImages(toFileArray((obj.getFiles())));
		toReturn.setTitle(obj.getTitle());
		toReturn.setCustom(obj.isCustom());
		return toReturn;
	}


	//*************** DM
	public static final org.gcube.application.aquamaps.aquamapsservice.stubs.CustomQueryDescriptorStubs toStubs(CustomQueryDescriptorStubs obj){
		org.gcube.application.aquamaps.aquamapsservice.stubs.CustomQueryDescriptorStubs toReturn=new org.gcube.application.aquamaps.aquamapsservice.stubs.CustomQueryDescriptorStubs();
		toReturn.setActualTableName(obj.actualTableName());
		toReturn.setErrorMsg(obj.errorMessage());
		toReturn.setFields(toFieldArray((obj.fields().theList())));
		toReturn.setQuery(obj.query());
		toReturn.setRows(obj.rows());
		toReturn.setStatus(org.gcube.application.aquamaps.aquamapsservice.stubs.ExportStatus.fromString(obj.status()+""));
		toReturn.setUser(obj.user());
		return toReturn;
	}

	// ************** GIS


	public static final org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType toStubs(LayerInfo obj){
		org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType res = new org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType();
		res.setName(obj.getName());
		res.setTitle(obj.getTitle());
		res.set_abstract(obj.get_abstract());
		res.setUrl(obj.getUrl());
		res.setServerLogin(obj.getServerLogin());
		res.setServerPassword(obj.getServerPassword());
		res.setServerProtocol(obj.getServerProtocol());
		res.setServerType(obj.getServerType());
		res.setSrs(obj.getSrs());
		if (obj.getType() != null && !obj.getType().name().contentEquals("")) res.setType(org.gcube_system.namespaces.application.aquamaps.gistypes.LayerType.fromString(obj.getType().name()));
		res.setTrasparent(obj.isTrasparent());
		res.setBaseLayer(obj.isBaseLayer());
		res.setBuffer(obj.getBuffer());
		res.setHasLegend(obj.isHasLegend());
		res.setVisible(obj.isVisible());
		res.setSelected(obj.isSelected());
		res.setQueryable(obj.isQueryable());
		if (obj.getMaxExtent() != null) res.setMaxExtent(toStubs(obj.getMaxExtent()));
		if (obj.getMinExtent() != null) res.setMinExtent(toStubs(obj.getMinExtent()));
		res.setDefaultStyle(obj.getDefaultStyle());
		res.setOpacity(obj.getOpacity());
		if (obj.getStyles() != null) res.setStyles(toStringArray(obj.getStyles()));
		if (obj.getTransect() != null) res.setTransect(toStubs(obj.getTransect()));

		return res;
	}

	public static final org.gcube_system.namespaces.application.aquamaps.gistypes.TransectInfoType toStubs(TransectInfo obj){
		return new org.gcube_system.namespaces.application.aquamaps.gistypes.TransectInfoType(obj.isEnabled(),toStringArray(obj.getFields()),obj.getMaxelements(),obj.getMinimumgap(),obj.getTable());
	}

	public static final org.gcube_system.namespaces.application.aquamaps.gistypes.BoundsInfoType toStubs(BoundsInfo obj){
		return new org.gcube_system.namespaces.application.aquamaps.gistypes.BoundsInfoType(obj.getCrs(), obj.getMaxx(), obj.getMaxy(), obj.getMinx(), obj.getMiny());
	}



	//************** COLLECTIONS


	public static final org.gcube.common.core.types.StringArray toStringArray(Collection<String> coll){
		return new org.gcube.common.core.types.StringArray(coll.toArray(new String[coll.size()]));
	}

	public static final org.gcube_system.namespaces.application.aquamaps.types.FileArray toFileArray(Collection<File> toConvert){
		ArrayList<org.gcube_system.namespaces.application.aquamaps.types.File> list=new ArrayList<org.gcube_system.namespaces.application.aquamaps.types.File>();
		if(toConvert!=null)
			for(File obj:toConvert)
				list.add(toStubs(obj));
		return new org.gcube_system.namespaces.application.aquamaps.types.FileArray(list.toArray(new org.gcube_system.namespaces.application.aquamaps.types.File[list.size()]));
	}

	public static final org.gcube_system.namespaces.application.aquamaps.types.FieldArray toFieldArray(Collection<Field> collection){
		ArrayList<org.gcube_system.namespaces.application.aquamaps.types.Field> translated=new ArrayList<org.gcube_system.namespaces.application.aquamaps.types.Field>();
		for(Field f:collection)translated.add(toStubs(f));		
		return new org.gcube_system.namespaces.application.aquamaps.types.FieldArray(translated.toArray(new org.gcube_system.namespaces.application.aquamaps.types.Field[translated.size()]));
	}

	public static final org.gcube_system.namespaces.application.aquamaps.types.SpeciesArray toSpeciesArray(Collection<Species> collection){
		ArrayList<org.gcube_system.namespaces.application.aquamaps.types.Specie> translated=new ArrayList<org.gcube_system.namespaces.application.aquamaps.types.Specie>();
		for(Species s:collection)translated.add(toStubs(s));		
		return new org.gcube_system.namespaces.application.aquamaps.types.SpeciesArray(translated.toArray(new org.gcube_system.namespaces.application.aquamaps.types.Specie[translated.size()]));
	}

	public static final org.gcube_system.namespaces.application.aquamaps.gistypes.LayerArray toLayerArray(Collection<LayerInfo> coll){
		ArrayList<org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType> list=new ArrayList<org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType>();
		if(coll!=null)
			for(LayerInfo l:coll)
				list.add(toStubs(l));
		return new org.gcube_system.namespaces.application.aquamaps.gistypes.LayerArray(list.toArray(new org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType[list.size()]));
	}


	public static final org.gcube_system.namespaces.application.aquamaps.types.MapArray toMapArray(Collection<AquaMap> collection){
		ArrayList<org.gcube_system.namespaces.application.aquamaps.types.Map> translated=new ArrayList<org.gcube_system.namespaces.application.aquamaps.types.Map>();
		for(AquaMap m:collection)
			try{
				translated.add(toStubs(m));		
			}catch(JSONException e){
				logger.debug("Skipping map "+m,e);
			}
		return new org.gcube_system.namespaces.application.aquamaps.types.MapArray(translated.toArray(new org.gcube_system.namespaces.application.aquamaps.types.Map[translated.size()]));
	} 

}

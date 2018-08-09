package org.gcube.dataanalysis.ecoengine.spatialdistributions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.SpatialProbabilityDistributionTable;
import org.gcube.dataanalysis.ecoengine.models.cores.aquamaps.MaxMinGenerator;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class AquamapsSuitable implements SpatialProbabilityDistributionTable{

	String selectAllSpeciesQuery = "select depthmin,meandepth,depthprefmin,pelagic,depthprefmax,depthmax,tempmin,layer,tempprefmin,tempprefmax,tempmax,salinitymin,salinityprefmin,salinityprefmax,salinitymax,primprodmin,primprodprefmin,primprodprefmax,primprodmax,iceconmin,iceconprefmin,iceconprefmax,iceconmax,landdistyn,landdistmin,landdistprefmin,landdistprefmax,landdistmax,nmostlat,smostlat,wmostlong,emostlong,faoareas,speciesid from %1$s;";
	String csquareCodeQuery = "select csquarecode,depthmean,depthmax,depthmin, sstanmean,sbtanmean,salinitymean,salinitybmean, primprodmean,iceconann,landdist,oceanarea,centerlat,centerlong,faoaream,eezall,lme from %1$s d where oceanarea>0";
	String createTableStatement = "CREATE TABLE %1$s ( speciesid character varying, csquarecode character varying, probability real, boundboxyn smallint, faoareayn smallint, faoaream integer, eezall character varying, lme integer) WITH (OIDS=FALSE ); CREATE INDEX CONCURRENTLY %1$s_idx ON %1$s USING btree (speciesid, csquarecode, faoaream, eezall, lme);";
	String destinationTable;
	String destinationTableLabel;
	String metainfo ="boundboxyn, faoareayn, faoaream, eezall, lme";
	String selectAllSpeciesObservationQuery = "SELECT speciesid,maxclat,minclat from %1$s;";
	String hspenMinMaxLat;
	AquamapsAlgorithmCore core;
	protected String currentFAOAreas;
	protected HashMap<String,String> currentSpeciesBoundingBoxInfo;
	protected HashMap<String, List<Object>> allSpeciesObservations;
	//to overwrite in case of 2050
	protected String type = null;
	
	public static String generateMaxMinHspec(String minmaxTableName, String hspenTable, String occurrencePointsTable, SessionFactory dbHibConnection){
		MaxMinGenerator maxmin = new MaxMinGenerator(dbHibConnection);
		if (occurrencePointsTable==null) 
			occurrencePointsTable = "occurrencecells";
		
		return maxmin.populatemaxminlat(minmaxTableName, hspenTable,occurrencePointsTable);
	}
	
	@Override
	public void init(AlgorithmConfiguration config,SessionFactory dbHibConnection) {
		selectAllSpeciesQuery = String.format(selectAllSpeciesQuery, config.getParam("EnvelopeTable"));
		csquareCodeQuery = String.format(csquareCodeQuery, config.getParam("CsquarecodesTable"));
		createTableStatement = String.format(createTableStatement,config.getParam("DistributionTable"));
		destinationTable = config.getParam("DistributionTable");
		destinationTableLabel = config.getParam("DistributionTableLabel");
		
		core = new AquamapsAlgorithmCore();
		
		if ((config.getParam("PreprocessedTable")!=null)&&(config.getParam("PreprocessedTable").length()>0))
			hspenMinMaxLat = config.getParam("PreprocessedTable");
		
		//if not preprocessed then generate a preprocessed table
		if (hspenMinMaxLat==null){
			//take the name of the hspen table
			String hspenTable = config.getParam("EnvelopeTable");
			//check if the table exists
			String supposedminmaxlattable = "maxminlat_"+hspenTable;
			List<Object> select = null;
			try{
				select = DatabaseFactory.executeSQLQuery("select * from "+supposedminmaxlattable+" limit 1",dbHibConnection);
			}catch(Exception ee){}
			//if it exists then set the table name
			if (select!=null){
				hspenMinMaxLat = supposedminmaxlattable;
				AnalysisLogger.getLogger().debug("Aquamaps Algorithm Init ->the min max latitudes table yet exists "+hspenMinMaxLat);
			}
			else{
				//otherwise create it by calling the creator
				AnalysisLogger.getLogger().debug("Aquamaps Algorithm Init ->the min max latitudes table does not exist! - generating");
				hspenMinMaxLat = generateMaxMinHspec(supposedminmaxlattable, hspenTable,config.getParam("OccurrencePointsTable"), dbHibConnection);
				AnalysisLogger.getLogger().debug("Aquamaps Algorithm Init ->min max latitudes table created in "+hspenMinMaxLat);
			}
		}
			
		
		AnalysisLogger.getLogger().trace("Aquamaps Algorithm Init ->getting min max latitudes from "+hspenMinMaxLat);
		
		allSpeciesObservations = new HashMap<String, List<Object>>();
		List<Object> SpeciesObservations = DatabaseFactory.executeSQLQuery(String.format(selectAllSpeciesObservationQuery, hspenMinMaxLat), dbHibConnection);
		int lenObservations = SpeciesObservations.size();
		for (int i=0;i<lenObservations;i++){
			Object[] maxminArray = (Object[])SpeciesObservations.get(i);
			String speciesid = (String)maxminArray[0];
			List<Object> maxminInfo = new ArrayList<Object>();
			maxminInfo.add(maxminArray);
			allSpeciesObservations.put((String)speciesid, maxminInfo);
		}
		
		AnalysisLogger.getLogger().trace("Aquamaps Algorithm Init ->init finished");
	}

	@Override
	public String getMainInfoQuery() {
		return selectAllSpeciesQuery;
	}

	@Override
	public String getGeographicalInfoQuery() {
		return csquareCodeQuery;
	}

	@Override
	public float calcProb(Object species, Object area) {
		return (float) core.getSpeciesProb((Object[]) species, (Object[]) area);
	}

	@Override
	public String getAdditionalMetaInformation() {
		return metainfo;
	}

	@Override
	public String getAdditionalInformation(Object species, Object area) {
		Object[] arearray = (Object[]) area;
		HashMap<String,Integer> boundingInfo = calculateBoundingBox(arearray);
		String addedInformation = "'"+boundingInfo.get("$InBox")+"','"+boundingInfo.get("$InFAO")+"','"+arearray[14]+"','"+arearray[15]+"','"+arearray[16]+"'";
		return addedInformation;
	}

	@Override
	public void postProcess() {
		
	}

	@Override
	public String getDistributionTableStatement() {
		return createTableStatement;
	}

	@Override
	public String getMainInfoID(Object speciesInfo) {
		String s = ""+ ((Object[])speciesInfo)[33];
		return  s;
	}

	@Override
	public String getGeographicalID(Object geoInfo) {
		String s = ""+ ((Object[])geoInfo)[0];
		return s;
	}
	
		
	public HashMap<String,Integer> calculateBoundingBox(Object[] csquarecode){
		HashMap<String,Integer> boundingInfo = core.calculateBoundingBox(
				""+csquarecode[0],
				currentSpeciesBoundingBoxInfo.get("$pass_NS"),
				currentSpeciesBoundingBoxInfo.get("$pass_N"),
				currentSpeciesBoundingBoxInfo.get("$pass_S"),
				AquamapsAlgorithmCore.getElement(csquarecode,12),//centerlat
				AquamapsAlgorithmCore.getElement(csquarecode,13),//centerlong
				AquamapsAlgorithmCore.getElement(csquarecode,14),//faoaream
				currentSpeciesBoundingBoxInfo.get("$paramData_NMostLat"),
				currentSpeciesBoundingBoxInfo.get("$paramData_SMostLat"),
				currentSpeciesBoundingBoxInfo.get("$paramData_WMostLong"),
				currentSpeciesBoundingBoxInfo.get("$paramData_EMostLong"),
				currentFAOAreas,
				currentSpeciesBoundingBoxInfo.get("$northern_hemisphere_adjusted"),
				currentSpeciesBoundingBoxInfo.get("$southern_hemisphere_adjusted")
				);
		
		return boundingInfo;
	}

	
	//initializes currentFAOAreas and currentSpeciesBoundingBoxInfo
			public void getBoundingBoxInformation(Object[] speciesInfoRow, Object[] speciesObservations){
				Object[]  row = speciesInfoRow;
				String $paramData_NMostLat = AquamapsAlgorithmCore.getElement(row,28);
				String $paramData_SMostLat = AquamapsAlgorithmCore.getElement(row,29);
				String $paramData_WMostLong = AquamapsAlgorithmCore.getElement(row,30);
				String $paramData_EMostLong = AquamapsAlgorithmCore.getElement(row,31);
				currentFAOAreas = AquamapsAlgorithmCore.getElement(row,32);
				//adjust FAO areas
				currentFAOAreas = core.procFAO_2050(currentFAOAreas);
				//get Bounding Box Information
//				AnalysisLogger.getLogger().trace("TYPE:"+type);
				currentSpeciesBoundingBoxInfo = core.getBoundingBoxInfo($paramData_NMostLat, $paramData_SMostLat, $paramData_WMostLong, $paramData_EMostLong, speciesObservations,type);
				//end of get BoundingBoxInformation
			}
			
	@Override
	public void singleStepPreprocess(Object species, Object allAreasInformation) {
		
		List<Object> speciesObservations = allSpeciesObservations.get(getMainInfoID(species));
		
		if( ((speciesObservations==null)||speciesObservations.size()==0)){
			Object[] defaultmaxmin = {"90","-90"};
			speciesObservations = new ArrayList<Object>();
			speciesObservations.add(defaultmaxmin);
		}
		
		getBoundingBoxInformation((Object[])species,(Object[])speciesObservations.get(0));
	}

	@Override
	public void singleStepPostprocess(Object species, Object area) {
			
	}

	@Override
	public boolean isSynchronousProbabilityWrite() {
		return true;
	}

	@Override
	public String filterProbabiltyRow(String probabiltyRow) {
		return probabiltyRow;
	}

	@Override
	//to overwrite in case of native generation in order to filer on the probabilities types
	public Queue<String> filterProbabilitySet(Queue<String> probabiltyRows) {
		return probabiltyRows;
	}

	@Override
	public float getInternalStatus() {
		return 100;
	}

	@Override
	public ALG_PROPS[] getProperties() {
//		ALG_PROPS [] p = {ALG_PROPS.SPECIES_VS_CSQUARE_FROM_DATABASE, ALG_PROPS.SPECIES_VS_CSQUARE_REMOTE_FROM_DATABASE};
		ALG_PROPS [] p = {ALG_PROPS.PARALLEL_SPECIES_VS_CSQUARE_FROM_DATABASE,ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON};
		return p;
	}

	@Override
	public String getName() {
		return "AQUAMAPS_SUITABLE";
	}

	@Override
	public String getDescription() {
		return "Algorithm for Suitable Distribution by AquaMaps. A distribution algorithm that generates a table containing  species distribution probabilities on half-degree cells according to the AquaMaps approach for suitable (potential) distributions.";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templatesOccurrence = new ArrayList<TableTemplates>();
		templatesOccurrence.add(TableTemplates.OCCURRENCE_AQUAMAPS);
		List<TableTemplates> templateHspen = new ArrayList<TableTemplates>();
		templateHspen.add(TableTemplates.HSPEN);
		List<TableTemplates> templateHcaf = new ArrayList<TableTemplates>();
		templateHcaf.add(TableTemplates.HCAF);
		
		InputTable p1 = new InputTable(templateHspen,"EnvelopeTable","The previous hspen table for regeneration","hspen");
		InputTable p2 = new InputTable(templateHcaf,"CsquarecodesTable","HCaf Table","hcaf_d");
		ServiceType p3 = new ServiceType(ServiceParameters.RANDOMSTRING, "DistributionTable","Table name of the distribution","hspec_");
		PrimitiveType p4 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "DistributionTableLabel","Name of the HSPEC probability distribution","hspec");
		InputTable p5 = new InputTable(templatesOccurrence,"OccurrencePointsTable","The Occurrence points table for calculating the bounding box","occurrencecells");
		PrimitiveType p6  = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.CONSTANT, "CreateTable","Create New Table for each computation","true");		
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
		
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	


	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.HSPEC);
		OutputTable p = new OutputTable(templateHspec,destinationTableLabel,destinationTable,"Output hspec table");
		return p;
	}
	

	
	
	
}

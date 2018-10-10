package org.gcube.dataanalysis.executor.nodes.algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.spatialdistributions.AquamapsAlgorithmCore;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.hibernate.SessionFactory;

import com.thoughtworks.xstream.XStream;

public class AquamapsSuitableFunctions {

	public static String countAllSpeciesQuery = "select count(*)  from %1$s;";
//	public static String countAll = "select count(*)  from %1$s;";
	public static String countAll = "EXPLAIN SELECT * FROM %1$s;";
	public static String countCsquareCodeQuery = "select count (*) from %1$s d where oceanarea>0";
	public static String selectAllSpeciesQuery = "select depthmin,meandepth,depthprefmin,pelagic,depthprefmax,depthmax,tempmin,layer,tempprefmin,tempprefmax,tempmax,salinitymin,salinityprefmin,salinityprefmax,salinitymax,primprodmin,primprodprefmin,primprodprefmax,primprodmax,iceconmin,iceconprefmin,iceconprefmax,iceconmax,landdistyn,landdistmin,landdistprefmin,landdistprefmax,landdistmax,nmostlat,smostlat,wmostlong,emostlong,faoareas,speciesid from %1$s order by speciesid limit %2$s offset %3$s;";
	public static String csquareCodeQuery = "select csquarecode,depthmean,depthmax,depthmin, sstanmean,sbtanmean,salinitymean,salinitybmean, primprodmean,iceconann,landdist,oceanarea,centerlat,centerlong,faoaream,eezall,lme from %1$s d where oceanarea>0  order by csquarecode limit %2$s offset %3$s";
	public static String createTableStatement = "CREATE TABLE %1$s ( speciesid character varying, csquarecode character varying, probability real, boundboxyn smallint, faoareayn smallint, faoaream integer, eezall character varying, lme integer) WITH (OIDS=FALSE ) #TABLESPACE#; CREATE INDEX CONCURRENTLY %1$s_idx ON %1$s USING btree (speciesid, csquarecode, faoaream, eezall, lme);";
	public static String metainfo = "boundboxyn, faoareayn, faoaream, eezall, lme";
	public static String selectAllSpeciesObservationQuery = "SELECT speciesid,maxclat,minclat from %1$s;";
	public static String probabilityInsertionStatement = "insert into %1$s (speciesid,csquarecode,probability %ADDEDINFORMATION%) VALUES %2$s";
	public static String deleteDuplicates = "delete from %1$s where speciesid='%2$s'"; 
	
	
	// Default Files
	private static String speciesFile = "species.dat";
	private static String csquaresFile = "csquares.dat";
	private static String maxminlatFile = "maxminlat.dat";
	private static String configFile = "config.dat";
	
	// file1
	public HashMap<String, List<Object>> allSpeciesObservations;
	// file2
	public List<Object> speciesVectors;
	// file3
	public List<Object> environmentVectors;
	public int numberOfSpecies;
	public int numberOfCells;
	
	
	//processing variables
	public AlgorithmConfiguration currentconfig;
	public HashMap<String, String> currentSpeciesBoundingBoxInfo;
	public String currentFAOAreas;
	public AquamapsAlgorithmCore core;
	public String type;
	public HashMap<String, Object> processedAreas;
	public ConcurrentHashMap<String, Map<String, Float>> completeDistribution;

	public AquamapsSuitableFunctions(AquamapsAlgorithmCore core, String type, AlgorithmConfiguration config) {
		this.core = core;
		this.type = type;
		this.currentconfig = config;
	}
	
	//PROBABILITY CALCULATION
	// calculates probability and takes into account the processes areas by this node
	public float calcProb(Object species, Object area) {
		float prob = (float) core.getSpeciesProb((Object[]) species, (Object[]) area);

		String speciesID = getMainInfoID(species);
		String csquareCode = getGeographicalID(area);
		if (completeDistribution == null)
			completeDistribution = new ConcurrentHashMap<String, Map<String, Float>>();

		Map<String, Float> geoDistrib = completeDistribution.get(speciesID);
		// if the map is null then generate a new map, otherwise update it
		if (geoDistrib == null) {
			geoDistrib = new ConcurrentHashMap<String, Float>();
			completeDistribution.put(speciesID, geoDistrib);
		}

		if (prob > 0.1) {
			// record the overall probability distribution
			geoDistrib.put(csquareCode, prob);
			if (processedAreas == null)
				processedAreas = new HashMap<String, Object>();
			processedAreas.put(csquareCode, area);
		}

		return prob;
	}
	
	
	//BOUNDING BOX CALCULATION
	// calculates the bounding box information
	public HashMap<String, Integer> calculateBoundingBox(Object[] csquarecode) {
		HashMap<String, Integer> boundingInfo = core.calculateBoundingBox("" + csquarecode[0], currentSpeciesBoundingBoxInfo.get("$pass_NS"), currentSpeciesBoundingBoxInfo.get("$pass_N"), currentSpeciesBoundingBoxInfo.get("$pass_S"), AquamapsAlgorithmCore.getElement(csquarecode, 12),// centerlat
				AquamapsAlgorithmCore.getElement(csquarecode, 13),// centerlong
				AquamapsAlgorithmCore.getElement(csquarecode, 14),// faoaream
				currentSpeciesBoundingBoxInfo.get("$paramData_NMostLat"), currentSpeciesBoundingBoxInfo.get("$paramData_SMostLat"), currentSpeciesBoundingBoxInfo.get("$paramData_WMostLong"), currentSpeciesBoundingBoxInfo.get("$paramData_EMostLong"), currentFAOAreas, currentSpeciesBoundingBoxInfo.get("$northern_hemisphere_adjusted"), currentSpeciesBoundingBoxInfo.get("$southern_hemisphere_adjusted"));

		return boundingInfo;
	}

	// initializes currentFAOAreas and currentSpeciesBoundingBoxInfo
	public void getBoundingBoxInformation(Object[] speciesInfoRow, Object[] speciesObservations) {
		Object[] row = speciesInfoRow;
		String $paramData_NMostLat = AquamapsAlgorithmCore.getElement(row, 28);
		String $paramData_SMostLat = AquamapsAlgorithmCore.getElement(row, 29);
		String $paramData_WMostLong = AquamapsAlgorithmCore.getElement(row, 30);
		String $paramData_EMostLong = AquamapsAlgorithmCore.getElement(row, 31);
		currentFAOAreas = AquamapsAlgorithmCore.getElement(row, 32);
		// adjust FAO areas
		currentFAOAreas = core.procFAO_2050(currentFAOAreas);
		// get Bounding Box Information
		// System.out.println("TYPE:"+type);
		currentSpeciesBoundingBoxInfo = core.getBoundingBoxInfo($paramData_NMostLat, $paramData_SMostLat, $paramData_WMostLong, $paramData_EMostLong, speciesObservations, type);
		// end of get BoundingBoxInformation
	}

	
	// DATABASE INTERACTION
		public static void writeOnDB(List<String> buffer, String destinationTable, SessionFactory dbHibConnection) {
			
			int endIndex = buffer.size();
			if (endIndex > 0) {
				System.out.println("\tWriting Buffer is not empty: "+endIndex);
				String $probabilityInsertionStatement = AquamapsSuitableFunctions.probabilityInsertionStatement.replace("%ADDEDINFORMATION%", ","+metainfo);

				StringBuffer sb = new StringBuffer();
				// System.out.println("writeOnDB()->PROBABILITIES BUFFER SIZE DELETION");
				for (int i = 0; i < endIndex; i++) {
					sb.append("(" + buffer.get(i) + ")");
					if (i < endIndex - 1) {
						sb.append(",");
					}
				}

				String insertionString = String.format($probabilityInsertionStatement, destinationTable, sb.toString());

				try {
//					 System.out.println(insertionString);
					DatabaseFactory.executeSQLUpdate(insertionString, dbHibConnection);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			else
				System.out.println("\tWarning : writing buffer is empty!");
			System.out.println("\tWriting on DB FINISHED");
		}
		
		
		//FILES MANAGEMENT
		public void dumpAll(String path) throws Exception {
			Transformations.dumpObjectToFile(path + configFile, currentconfig);
//			Transformations.dumpObjectToFile(path + csquaresFile, environmentVectors);
		}

		public void rebuildConfig(String configFile) throws Exception{
			FileInputStream fis = new FileInputStream(new File(configFile));
			currentconfig = (AlgorithmConfiguration) new XStream().fromXML(fis);
			fis.close();
		}
		// when uploaded the files will be local
		public void rebuildAll(int cellOrdinal, int chunksize, int speciesOrdinal, int speciesChunkSize, String pathToFiles) throws Exception {
//			currentconfig = (AlgorithmConfiguration) Transformations.getObjectFromFile(pathToFiles+configFile);
			
			/*
			try{
			environmentVectors = (List<Object>) Transformations.getObjectFromFile(pathToFiles+csquaresFile);
			}catch(Exception e){
				System.out.println("\tError in retrieving environmental vectors");
			}
			*/
		}

		public String getAdditionalInformation(Object species, Object area) {
			Object[] arearray = (Object[]) area;
			HashMap<String, Integer> boundingInfo = calculateBoundingBox(arearray);
			String addedInformation = "'" + boundingInfo.get("$InBox") + "','" + boundingInfo.get("$InFAO") + "','" + arearray[14] + "','" + arearray[15] + "','" + arearray[16] + "'";
			return addedInformation;
		}

		
		//AUXILIARY FUNCTIONS
		public static String getMainInfoID(Object speciesInfo) {
			String s = "" + ((Object[]) speciesInfo)[33];
			return s;
		}

		public static String getGeographicalID(Object geoInfo) {
			String s = "" + ((Object[]) geoInfo)[0];
			return s;
		}
		

}

package org.gcube.data.analysis.statisticalmanager.test.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationConfig;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntries;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMInputEntry;

public class TestExecutionProfile {
	
	/**
	 * Tables by GP 
	 * 
	 * http://goo.gl/VDzpch			used system's hcaf_d
	 * 
	 * http://goo.gl/5cnKKp			
	 * 
	 * 
	 */
	// url -> template
	public static Map<String,String> REFERENCED_TABLES=new HashMap<String, String>();
	public static Map<String,String> REFERENCED_FILES=new HashMap<String, String>();
	
	static{
		REFERENCED_TABLES.put("http://goo.gl/VDzpch", "GENERIC");
		REFERENCED_TABLES.put("http://goo.gl/5cnKKp", "OCCURRENCE_SPECIES");
		REFERENCED_TABLES.put("http://goo.gl/lWTvcw","GENERIC");
		REFERENCED_TABLES.put("http://goo.gl/f1Y11n","GENERIC");

		REFERENCED_FILES.put("http://goo.gl/i16kPw", "GENERIC");
		REFERENCED_FILES.put("http://goo.gl/3X3b8T", "GENERIC");
		REFERENCED_FILES.put("http://goo.gl/l5O75s", "GENERIC");
		REFERENCED_FILES.put("http://goo.gl/vGEbuZ", "GENERIC");
		REFERENCED_FILES.put("http://goo.gl/Y0FM5g", "GENERIC");
		REFERENCED_FILES.put("http://goo.gl/50U7hG", "GENERIC");
	}
	
	
	
	private static TestExecutionProfile DBSCAN=new TestExecutionProfile("DBSCAN_DEPTH","DBSCAN","OccurrencePointsClusterLabel=OccClustersTest;epsilon=10;min_points=1;OccurrencePointsTable=http://goo.gl/VDzpch;FeaturesColumnNames=depthmean|sstmnmax|salinitymean");
	private static TestExecutionProfile DBSCAN_DEPTH=new TestExecutionProfile("DBSCAN_DEPTH","DBSCAN","OccurrencePointsClusterLabel=OccClustersTest;epsilon=10;min_points=1;OccurrencePointsTable=http://goo.gl/VDzpch;FeaturesColumnNames=depthmean");
	private static TestExecutionProfile DBSCAN_SST=new TestExecutionProfile("DBSCAN_SST","DBSCAN","OccurrencePointsClusterLabel=OccClustersTest;epsilon=10;min_points=1;OccurrencePointsTable=http://goo.gl/VDzpch;FeaturesColumnNames=sstmnmax");
	private static TestExecutionProfile DBSCAN_SAL=new TestExecutionProfile("DBSCAN_SAL","DBSCAN","OccurrencePointsClusterLabel=OccClustersTest;epsilon=10;min_points=1;OccurrencePointsTable=http://goo.gl/VDzpch;FeaturesColumnNames=salinitymean");
	
	private static TestExecutionProfile BIONYM_LOCAL=new TestExecutionProfile("BIONYM_LOCAL", "BIONYM_LOCAL", "Matcher_1=LEVENSHTEIN;Matcher_4=NONE;Matcher_5=NONE;Matcher_2=NONE;Matcher_3=NONE;Threshold_1=0.6;Threshold_2=0.6;Accuracy_vs_Speed=MAX_ACCURACY;MaxResults_2=10;MaxResults_1=10;Threshold_3=0.4;Taxa_Authority_File=FISHBASE;Parser_Name=SIMPLE;MaxResults_4=0;Threshold_4=0;MaxResults_3=0;MaxResults_5=0;Threshold_5=0;Use_Stemmed_Genus_and_Species=false;Activate_Preparsing_Processing=true;SpeciesAuthorName=Gadus morhua");
	private static TestExecutionProfile MAX_ENT_NICHE_MODELLING=new TestExecutionProfile("MAX_ENT_NICHE_MODELLING", "MAX_ENT_NICHE_MODELLING", "LongitudeColumn=decimallongitude;LatitudeColumn=decimallatitude;Z=0;Layers=abea05ca-c9dc-43da-89d5-5fd3fa75023d|abea05ca-c9dc-43da-89d5-5fd3fa75023d;TimeIndex=0;MaxIterations=100;SpeciesName=Latimeria chalumnae;DefaultPrevalence=0.5;YResolution=0.5;OccurrencesTable=http://goo.gl/5cnKKp;XResolution=0.5;OutputTableLabel=wps_maxent;");
	
	private static TestExecutionProfile OBIS_MOST_OBSERVED_SPECIES=new TestExecutionProfile("OBIS_MOST_OBSERVED_SPECIES", "OBIS_MOST_OBSERVED_SPECIES", "Species_number=10;End_year=2015;Start_year=2000;");
	private static TestExecutionProfile OBIS_SPECIES_OBSERVATIONS_PER_MEOW_AREA=new TestExecutionProfile("OBIS_SPECIES_OBSERVATIONS_PER_MEOW_AREA","OBIS_SPECIES_OBSERVATIONS_PER_MEOW_AREA","Selected species=Gadus morhua;End_year=2015;Start_year=2000;Area_type=NORTH SEA;");
	private static TestExecutionProfile MAPS_COMPARISON=new TestExecutionProfile("MAPS_COMPARISON","MAPS_COMPARISON","TimeIndex_1=0;ValuesComparisonThreshold=0.1;TimeIndex_2=0;Z=0;KThreshold=0.5;Layer_1=3fb7fd88-33d4-492d-b241-4e61299c44bb;Layer_2=3fb7fd88-33d4-492d-b241-4e61299c44bb;");
	private static TestExecutionProfile XYEXTRACTOR=new TestExecutionProfile("XYEXTRACTOR", "XYEXTRACTOR", "OutputTableLabel=wps_xy_extractor;Layer=abea05ca-c9dc-43da-89d5-5fd3fa75023d;YResolution=0.5;XResolution=0.5;BBox_LowerLeftLong=-50;BBox_UpperRightLat=60;BBox_LowerLeftLat=-60;BBox_UpperRightLong=50;Z=0;TimeIndex=0;");
	private static TestExecutionProfile GENERIC_CHARTS=new TestExecutionProfile("GENERIC_CHARTS", "GENERIC_CHARTS", "Quantities=fvalue;InputTable=http://goo.gl/lWTvcw;TopElementsNumber=10;Attributes=x|y");
	private static TestExecutionProfile POLYGONS_TO_MAP=new TestExecutionProfile("POLYGONS_TO_MAP", "POLYGONS_TO_MAP", "MapName=Example polygon map generated for Test;xDimension=centerlong;yDimension=centerlat;Info=faoaream;InputTable=http://goo.gl/VDzpch;Resolution=0.5");
	private static TestExecutionProfile LIST_DB_NAMES=new TestExecutionProfile("Listdbnames", "LISTDBNAMES", "MaxNumber=-1");
	private static TestExecutionProfile LIST_DB_INFO=new TestExecutionProfile("Listdbinfo", "LISTDBINFO", "ResourceName=FishBase");
	
	// CLOUD
	
	private static TestExecutionProfile BIONYM=new TestExecutionProfile("BIONYM","BIONYM","Matcher_1=LEVENSHTEIN;Matcher_4=NONE;Matcher_5=NONE;Matcher_2=NONE;Matcher_3=NONE;Threshold_1=0.6;RawTaxaNamesTable=http://goo.gl/N9e3pC;Threshold_2=0.6;Accuracy_vs_Speed=MAX_ACCURACY;MaxResults_2=10;MaxResults_1=10;Threshold_3=0.4;RawNamesColumn=species;Taxa_Authority_File=FISHBASE;Parser_Name=SIMPLE;OutputTableLabel=bionymwps;MaxResults_4=0;Threshold_4=0;MaxResults_3=0;MaxResults_5=0;Threshold_5=0;Use_Stemmed_Genus_and_Species=false;Activate_Preparsing_Processing=true;");
	private static TestExecutionProfile ICCAT_VPA=new TestExecutionProfile("ICCAT_VPA", "ICCAT_VPA", "StartYear=1950;shortComment=no;EndYear=2013;CAAFile=http://goo.gl/3X3b8T;PCAAFile=http://goo.gl/l5O75s;CPUEFile=http://goo.gl/vGEbuZ;PwaaFile=http://goo.gl/Y0FM5g;waaFile=http://goo.gl/50U7hG;nCPUE=7;CPUE_cut=1;age_plus_group=10;");
	private static TestExecutionProfile SGVM_INTERPOLATION= new TestExecutionProfile("SGVM_INTERPOLATION", "SGVM_INTERPOLATION", "headingAdjustment=0;maxspeedThr=6;minspeedThr=2;fm=0.5;margin=10;distscale=20;res=100;sigline=0.2;interval=120;equalDist=true;InputFile=http://goo.gl/i16kPw;npoints=10;method=cHs;");
	private static TestExecutionProfile CMSY=new TestExecutionProfile("CMSY", "CMSY", "IDsFile=http://goo.gl/9rg3qK;StocksFile=http://goo.gl/Mp2ZLY;SelectedStock=HLH_M07");
	
	
	public static TestExecutionProfile[] PROFILES=new TestExecutionProfile[]{
//		DBSCAN,
//	DBSCAN_DEPTH,
//	DBSCAN_SAL,
//	DBSCAN_SST,
//		BIONYM_LOCAL,
//		MAX_ENT_NICHE_MODELLING, 
//		OBIS_MOST_OBSERVED_SPECIES, 
//		OBIS_SPECIES_OBSERVATIONS_PER_MEOW_AREA, 
//		MAPS_COMPARISON, 
//		XYEXTRACTOR,
//		GENERIC_CHARTS,	
//		POLYGONS_TO_MAP, 
//		LIST_DB_NAMES,
//		LIST_DB_INFO,
//		BIONYM,
//		ICCAT_VPA,
		SGVM_INTERPOLATION,
//		CMSY
	};
	
	private String name;
	private SMComputationConfig config;
	
	public SMComputationConfig getConfig() {
		return config;
	}
	
	public String getName() {
		return name;
	}

	public TestExecutionProfile(String name,String algorithmName, String parameters) {
		super();
		this.name = name;
		List<SMInputEntry> params=new ArrayList<>();
		for(String s:parameters.split(";")){
			String[] sParts=s.split("=");
			params.add(new SMInputEntry(sParts[0], sParts[1].replaceAll("\\|", "#")));
		}		
		this.config=new SMComputationConfig(algorithmName,new SMEntries(params.toArray(new SMInputEntry[params.size()])));
		System.out.println("Created Profile "+name+", Algorithm : "+algorithmName);
	}
	
	/**
	 * expects a Map table_uri -> tableId
	 * 
	 * @param importedUris
	 */
	public void updateReferences(Map<String,String> importedUris){
		for(SMInputEntry entry: config.parameters().list())
			if(importedUris.containsKey(entry.value())) entry.value(importedUris.get(entry.value()));
	}
	
}

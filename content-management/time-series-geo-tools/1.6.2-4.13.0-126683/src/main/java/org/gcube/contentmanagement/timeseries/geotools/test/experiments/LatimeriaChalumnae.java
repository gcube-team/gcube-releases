package org.gcube.contentmanagement.timeseries.geotools.test.experiments;

import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.tools.CreateLayerFromProbability;

public class LatimeriaChalumnae {
	
		static String presenceTable = "presence_data_latimeria";
		static String absenceStaticTable = "absence_data_latimeria";
		
		static String aquamapsSuitableTable = "hspec_suitable_latimeria_chalumnae";
		static String aquamapsNativeTable = "hspec_native_latimeria_chalumnae";
		static String nnsuitableTable = "hspec_suitable_neural_latimeria_chalumnae";
		static String nnnativeTable = "hspec_native_neural_latimeria_chalumnae";
		static String presenceLatimeriaTown = "presencelatimeriatown";
		
		static String geoserverURL = "jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb";
		static String geoserverUser = "postgres";
		static String geoserverPwd = "d4science2";
		
		static String originURL = "jdbc:postgresql://localhost/testdb";
		static String originUser = "gcube";
		static String originPwd = "d4science2";
		static String configPath = "./cfg/";
		
		static String csquareColumn = "csquarecode";
		static String probabilityColumn = "probability";
		static String salinityColumn = "salinitysd";
		static String speciesID = "Fis-30189";
		
		public static void main(String[] args) throws Exception{
			
			GISInformation gisInfo = new GISInformation();
			gisInfo.setGeoNetworkUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetwork");
			gisInfo.setGeoNetworkUserName("admin");
			gisInfo.setGeoNetworkPwd("admin");
			
			gisInfo.setGisDataStore("aquamapsdb");
			gisInfo.setGisPwd("gcube@geo2010");
			gisInfo.setGisWorkspace("aquamaps");
			gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
			gisInfo.setGisUserName("admin");
			
			CreateLayerFromProbability creatorProb = new CreateLayerFromProbability();
			creatorProb.init(configPath, geoserverURL, originURL, geoserverUser, geoserverPwd, originUser, originPwd);
			/*
			System.out.println("->"+aquamapsSuitableTable);
			creatorProb.produceLayer(aquamapsSuitableTable, speciesID, csquareColumn, probabilityColumn);
			System.out.println("->"+aquamapsNativeTable);
			creatorProb.produceLayer(aquamapsNativeTable, speciesID, csquareColumn, probabilityColumn);
			
			System.out.println("->"+nnsuitableTable);
			creatorProb.produceLayer(nnsuitableTable, speciesID, csquareColumn, probabilityColumn);
			System.out.println("->"+nnnativeTable);
			creatorProb.produceLayer(nnnativeTable, speciesID, csquareColumn, probabilityColumn);
			
			System.out.println("->"+presenceTable);
			creatorProb.produceLayer(presenceTable, speciesID, csquareColumn, salinityColumn);
			
			System.out.println("->"+absenceStaticTable);
			creatorProb.produceLayer(absenceStaticTable, speciesID, csquareColumn, salinityColumn);
			*/
			System.out.println("->"+presenceLatimeriaTown);
			creatorProb.produceLayer(presenceLatimeriaTown, speciesID, csquareColumn, probabilityColumn);
			//FUNZIONA: 
			/*
			creatorProb.generatePointGisMap(aquamapsSuitableTable, aquamapsSuitableTable.replace("_", ""), gisInfo,"Species_prob");
			creatorProb.generatePointGisMap(aquamapsNativeTable, aquamapsNativeTable.replace("_", ""), gisInfo,"Species_prob");
			creatorProb.generatePointGisMap(nnsuitableTable, nnsuitableTable.replace("_", ""), gisInfo,"Species_prob");
			creatorProb.generatePointGisMap(nnnativeTable, nnnativeTable.replace("_", ""), gisInfo,"Species_prob");
			creatorProb.generatePointGisMap(presenceTable, presenceTable.replace("_", ""), gisInfo,"point");
			creatorProb.generatePointGisMap(absenceStaticTable, absenceStaticTable.replace("_", ""), gisInfo,"point");
			*/
			creatorProb.generatePointGisMap(presenceLatimeriaTown, presenceLatimeriaTown.replace("_", ""), gisInfo,"point");
			
		}
}

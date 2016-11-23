package org.gcube.contentmanagement.timeseries.geotools.tools;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISGroupInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerSaver;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations.featuresTypes;
import org.gcube.contentmanagement.timeseries.geotools.representations.GISLayer;

public class CreateLayerFromProbability {
	
		private static String probabilityquery = "select %2$s,%3$s from %1$s";
		ConnectionsManager connectionsManager;
		
		public static void main(String[] args) throws Exception{
			
			CreateLayerFromProbability creatorProb = new CreateLayerFromProbability();
			String geoserverURL = "jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb";
			String geoserverUser = "postgres";
			String geoserverPwd = "d4science2";
			
			String originURL = "jdbc:postgresql://localhost/testdb";
			String originUser = "gcube";
			String originPwd = "d4science2";

			String configPath = "./cfg/";
			
//			String tableName = "hspec_native_baskingshark_aquamaps";
//			String csquareColumn = "csquarecode";
//			String probabilityColumn = "probability";
			
//			String tableName = "hspec_suitable_nn_Fis22747";
//			String tableName = "hspec_suitable_baskingshark_aquamaps";
//			String tableName = "hspec_suitable_nn_fis22747_random_100_2";
			String tableName = "hspec_nn_baskingshark_random_best2";
			String csquareColumn = "csquarecode";
			String probabilityColumn = "probability";
			String speciesID = "Fis-22747";
			
//			String tableName = "presence_data_baskingshark";
//			String tableName = "absence_data_baskingshark_random";
//			String csquareColumn = "csquarecode";
//			String probabilityColumn = "salinitysd";
//			String speciesID = "Fis-22747";
			
			creatorProb.init(configPath, geoserverURL, originURL, geoserverUser, geoserverPwd, originUser, originPwd);
			
			creatorProb.produceLayer(tableName, speciesID, csquareColumn, probabilityColumn);
			

		}
		
		public static void main1(String[] args){
			
			double[] subs = MathFunctions.logSubdivision(0, 1, 5);
			for (int i=0;i<subs.length;i++)
				System.out.print(subs[i]+" ");
		}
		
		public void init(String configPath, String geoserverDB, String originDBURL, String geoserverUser, String geoserverPwd,
									String originUser, String originPwd) throws Exception{
			
			TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
			
			//origin
			configuration.setConfigPath(configPath);
			configuration.setGeoServerDatabase(geoserverDB);
			configuration.setGeoServerUserName(geoserverUser);
			configuration.setGeoServerPassword(geoserverPwd);
			//destination
			configuration.setAquamapsDatabase(originDBURL);
			configuration.setAquamapsUserName(originUser);
			configuration.setAquamapsPassword(originPwd);
			
			AnalysisLogger.setLogger(configPath + "ALog.properties");
			AnalysisLogger.getLogger().debug("CreateLayerFromProbability -> initializing connections");

			connectionsManager = new ConnectionsManager(configPath);
			
			EngineConfiguration geocfg = new EngineConfiguration();
			geocfg.setConfigPath(configPath);
			geocfg.setDatabaseUserName(configuration.getGeoServerUserName());
			geocfg.setDatabasePassword(configuration.getGeoServerPassword());
			geocfg.setDatabaseURL(configuration.getGeoServerDatabase());
			
			EngineConfiguration destGeocfg = new EngineConfiguration();
			destGeocfg.setConfigPath(configPath);
			destGeocfg.setDatabaseUserName(configuration.getAquamapsUserName());
			destGeocfg.setDatabasePassword(configuration.getAquamapsPassword());
			destGeocfg.setDatabaseURL(configuration.getAquamapsDatabase());
			
			connectionsManager.initGeoserverConnection(geocfg);
			connectionsManager.initAquamapsConnection(destGeocfg);
			
		}
		
		public void produceLayer(String tableName, String speciesID, String csquareColumn, String probabilityColumn) throws Exception{
			
			GISLayerSaver gislayersav = new GISLayerSaver(connectionsManager);
			
			GISLayer layer = new GISLayer(tableName);
			layer.setValuesColumnName("probability");
			layer.setMin(0);
			layer.setMax(1);
			layer.setPreferredStyleName("Species_prob");
			
			List<Object> rows = DatabaseFactory.executeSQLQuery(String.format(probabilityquery,tableName,csquareColumn,probabilityColumn), connectionsManager.getAquamapsConnection());
			
			for(Object row:rows){
				Object[] information = (Object[]) row;
				List<String> csquares = new ArrayList<String>();
				csquares.add(""+information[0]);
				double informationd = 0;
				if (information[1]!=null)
					informationd = Double.parseDouble(""+information[1]);
				
				layer.appendListofSquares(csquares, informationd,"");
			}
			
			gislayersav.createLayerOnDB(layer, featuresTypes.real);
		}
		
		
		
		public String generatePointGisMap(String destinationTable, String mapName, GISInformation gisInfo, String stylename) throws Exception {

			AnalysisLogger.getLogger().trace("generateXYMaps->creating layer");
			// add the style
			GISStyleInformation style = new GISStyleInformation();
			style.setStyleName(stylename);

			GISLayerInformation gisLayer1 = new GISLayerInformation();
			gisLayer1.setDefaultStyle(style.getStyleName());
			gisLayer1.setLayerName(destinationTable);
			gisLayer1.setLayerTitle(mapName);

			// add layer to the previously generated group
			gisInfo.addLayer(gisLayer1);
			gisInfo.addStyle(gisLayer1.getLayerName(), style);
			AnalysisLogger.getLogger().trace("generateXYMaps->adding layers - done!");
			// create a GIS group for the the Layer
			String groupName = (destinationTable.replace("-", ""));
			GISOperations gisOperations = new GISOperations();
			
			GISGroupInformation gisGroup = new GISGroupInformation();
			gisGroup.setGroupName(groupName);
			gisGroup.setTemplateGroupName(GISOperations.TEMPLATEGROUP);
			gisGroup.setTemplateGroup(true);
			gisInfo.setGroup(gisGroup);
			
			boolean generated = gisOperations.generateGisMap(gisInfo,true);
			
			if (!generated)
				throw new Exception("Impossible to create layer on Geonetwork");
			
			List<String> layersList = new ArrayList<String>(); 
			layersList.add(destinationTable);
			
			boolean urlcoherence = true;
			
			if (!urlcoherence)
				return null;
			else {
				AnalysisLogger.getLogger().trace("generateXYMaps->GIS Layer created!");
				return destinationTable;
			}
		}
	
}

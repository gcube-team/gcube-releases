package org.gcube.contentmanagement.timeseries.geotools.vti.test.experiments;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.tools.PointsMapCreator;
import org.gcube.contentmanagement.timeseries.geotools.utils.OccurrencePointVector2D;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

public class ClusterMaps {

	ConnectionsManager connectionsManager;
	String configPath;
	String originDBURL;
	String originDBUser;
	String originDBPWD;
	TSGeoToolsConfiguration configuration;
	private String datastore;
	private String tablename;
	private String tablecontentdescription;
	private GISInformation gisInfo;
	private String workspace;
	private String clusteringAlgorithmName;
	private String clusteridField;
	private String geoServerDBURL;
	private String geoServerDBUser;
	private String geoServerDBPWD;
	private String destinationMapTable;
	private String outliersField;
	
	public ClusterMaps(String configFolder, String geoNetworkURL, String geoServerBackupURL, String geoNetworkUser, String geoNetworkPwd, String geoServerUser, String geoServerPwd, String geoServerDBURL, String geoServerDBUser, String geoServerDBPWD, String originDBURL, String originDBUser, String originDBPWD, String workspace, String datastore, String tablename, String tablecontentdescription, String clusteringAlgorithmName, String clusteridField, String outliersField) {
		configPath = configFolder;
		gisInfo = new GISInformation();
		gisInfo.setGeoNetworkUrl(geoNetworkURL);
		// gisInfo.setGeoNetworkUrl("http://146.48.87.49:8080/geonetwork");
		gisInfo.setGeoNetworkUserName(geoNetworkUser);
		gisInfo.setGeoNetworkPwd(geoNetworkPwd);

		gisInfo.setGisDataStore(datastore);
		gisInfo.setGisPwd(geoServerPwd);
		gisInfo.setGisWorkspace(workspace);
		gisInfo.setGisUrl(geoServerBackupURL);
		gisInfo.setGisUserName(geoServerUser);

		configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath(configPath);

		configuration.setGeoServerDatabase(geoServerDBURL);
		configuration.setGeoServerUserName(geoServerDBUser);
		configuration.setGeoServerPassword(geoServerDBPWD);
		
		this.geoServerDBURL = geoServerDBURL;
		this.geoServerDBUser = geoServerDBUser;
		this.geoServerDBPWD = geoServerDBPWD;
		
		this.originDBURL = originDBURL;
		this.originDBUser = originDBUser;
		this.originDBPWD = originDBPWD;
		this.datastore = datastore;
		this.tablename = tablename;
		this.tablecontentdescription = tablecontentdescription;
		this.workspace = workspace;
		this.clusteringAlgorithmName = clusteringAlgorithmName;
		this.clusteridField = clusteridField;
		this.outliersField = outliersField;
	}

	public String createClusteredMap() throws Exception {
		String mapName = null;
		boolean clustersuccess = createClusteredGeoTable();
		if (clustersuccess) {
			ClusterStylesGenerator styler = new ClusterStylesGenerator(
					configPath, 
					gisInfo.getGeoNetworkUrl(), 
					gisInfo.getGisUrl(), 
					gisInfo.getGeoNetworkUserName(), 
					gisInfo.getGeoNetworkPwd(), 
					gisInfo.getGisUserName(), 
					gisInfo.getGisPwd(), 
					geoServerDBURL, 
					geoServerDBUser,
					geoServerDBPWD, 
					workspace, 
					datastore);
			clustersuccess = styler.generateStyleMap(clusteringAlgorithmName, tablename, destinationMapTable, datastore, clusteridField, outliersField);
			mapName = destinationMapTable;
		}

		return mapName;

	}

	public boolean createClusteredGeoTable() throws Exception {
		try{
		AnalysisLogger.setLogger(configPath + AlgorithmConfiguration.defaultLoggerFile);
		connectionsManager = new ConnectionsManager(configPath);
		EngineConfiguration tscfg = null;
		tscfg = new EngineConfiguration();
		tscfg.setConfigPath(configPath);
		tscfg.setDatabaseUserName(originDBUser);
		tscfg.setDatabasePassword(originDBPWD);
		tscfg.setDatabaseURL(originDBURL);
		connectionsManager.initTimeSeriesConnection(tscfg);
		List<Object> pointsO = (List<Object>) DatabaseFactory.executeSQLQuery(String.format("select * from %1$s", tablename), connectionsManager.getTimeSeriesConnection());
		List<OccurrencePointVector2D> xyPoints = new ArrayList<OccurrencePointVector2D>();

		for (Object row : pointsO) {
			Object[] rowl = (Object[]) row;

			Float x = null;
			Float y = null;
			OccurrencePointVector2D pointsvector = null;
			for (int i = 0; i < rowl.length; i++) {
				if (i == 0)
					x = Float.parseFloat("" + rowl[i]);
				if (i == 1)
					y = Float.parseFloat("" + rowl[i]);
				if (i == 2) {
					pointsvector = new OccurrencePointVector2D(x, y);
					pointsvector.addMetadataToMap("clusterid", "" + rowl[i]);
				} else if (i == 3) {
					pointsvector.addMetadataToMap("outlier", "" + rowl[i]);
					xyPoints.add(pointsvector);
				}
			}
		}

		PointsMapCreator pmcreator = new PointsMapCreator(configuration);
		destinationMapTable = generateGeoTableName(tablename);

		AnalysisLogger.getLogger().trace("Producing MAP: " + destinationMapTable);

		String layerName = pmcreator.createMapFromPoints(xyPoints, destinationMapTable, tablecontentdescription, gisInfo);

		AnalysisLogger.getLogger().trace("PRODUCED layer name: " + layerName);
		if (layerName != null)
			return true;
		else
			return false;
	}catch(Exception e){
		throw e;
	}
	finally{
		AnalysisLogger.getLogger().trace("Connection closed");
		connectionsManager.getTimeSeriesConnection().close();
	}
	}

	public static void main(String[] args) throws Exception {
		String configFolder = "./cfg/";
		String geoNetworkURL = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetwork";
		String geoServerBackupURL = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
		String geoNetworkUser = "admin";
		String geoNetworkPwd = "admin";
		String geoServerUser = "admin";
		String geoServerPwd = "gcube@geo2010";
		String geoServerDBURL = "jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb";
		String geoServerDBUser = "postgres";
		String geoServerDBPWD = "d4science2";
		String originDBURL = "jdbc:postgresql://localhost/testdb";
		String originDBUser = "gcube";
		String originDBPWD = "d4science2";
		String workspace = "aquamaps";
		String datastore = "timeseriesgisdb";
//		String algorithm = "kmeans";
//		String algorithm = "xmeans";
		String algorithm = "dbscan";
		String tablename = "occcluster_" + algorithm;
		String tablecontentdescription = "basking shark with " + algorithm;
		String clusteridField = "clusterid";
		String outliersField = "outlier";
		ClusterMaps cm = new ClusterMaps(configFolder, geoNetworkURL, geoServerBackupURL, geoNetworkUser, geoNetworkPwd, geoServerUser, geoServerPwd, geoServerDBURL, geoServerDBUser, geoServerDBPWD, originDBURL, originDBUser, originDBPWD, workspace, datastore, tablename, tablecontentdescription, algorithm, clusteridField, outliersField);
		String resultMap = cm.createClusteredMap();
		System.out.println("Result Map:"+resultMap);
	}

	public static void main1(String[] args) throws Exception {
		String cfgpath = "./cfg/";
		// String algorithm = "xmeans";
		String algorithm = "kmeans";
		// String algorithm = "dbscan";
		String tablename = "occcluster_" + algorithm;
		String description = "basking shark with " + algorithm;
		AnalysisLogger.setLogger(cfgpath + AlgorithmConfiguration.defaultLoggerFile);
		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath(cfgpath);

		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");

		configuration.setTimeSeriesDatabase("jdbc:postgresql://localhost/testdb");
		configuration.setTimeSeriesUserName("gcube");
		configuration.setTimeSeriesPassword("d4science2");

		GISInformation gisInfo = new GISInformation();
		gisInfo.setGeoNetworkUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetworkssss");
		// gisInfo.setGeoNetworkUrl("http://146.48.87.49:8080/geonetwork");
		gisInfo.setGeoNetworkUserName("admin");
		gisInfo.setGeoNetworkPwd("admin");

		gisInfo.setGisDataStore("timeseriesgisdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");

		ConnectionsManager connectionsManager = new ConnectionsManager(cfgpath);
		EngineConfiguration tscfg = null;
		if (configuration.getTimeSeriesDatabase() != null) {
			tscfg = new EngineConfiguration();
			tscfg.setConfigPath(cfgpath);
			tscfg.setDatabaseUserName(configuration.getTimeSeriesUserName());
			tscfg.setDatabasePassword(configuration.getTimeSeriesPassword());
			tscfg.setDatabaseURL(configuration.getTimeSeriesDatabase());
		}
		connectionsManager.initTimeSeriesConnection(tscfg);
		List<Object> pointsO = (List<Object>) DatabaseFactory.executeSQLQuery(String.format("select * from %1$s", tablename), connectionsManager.getTimeSeriesConnection());
		List<OccurrencePointVector2D> xyPoints = new ArrayList<OccurrencePointVector2D>();

		
		//cambiare ... recuperare description e creare tabella
		for (Object row : pointsO) {
			Object[] rowl = (Object[]) row;

			Float x = null;
			Float y = null;
			OccurrencePointVector2D pointsvector = null;
			for (int i = 0; i < rowl.length; i++) {
				if (i == 0)
					x = Float.parseFloat("" + rowl[i]);
				if (i == 1)
					y = Float.parseFloat("" + rowl[i]);
				if (i == 2) {
					pointsvector = new OccurrencePointVector2D(x, y);
					pointsvector.addMetadataToMap("clusterid", "" + rowl[i]);
				} else if (i == 3) {
					pointsvector.addMetadataToMap("outlier", "" + rowl[i]);
					xyPoints.add(pointsvector);
				}
			}
		}

		PointsMapCreator pmcreator = new PointsMapCreator(configuration);
		String destinationMapTable = tablename.replace("_", "");
		String destinationMapName = "occurrence points for " + description;
		AnalysisLogger.getLogger().trace("Producing MAP: " + destinationMapTable);

		String layerName = pmcreator.createMapFromPoints(xyPoints, destinationMapTable, destinationMapName, gisInfo);

		AnalysisLogger.getLogger().trace("PRODUCED layer name: " + layerName);
	}

	public static String generateGeoTableName(String tablename) {
		String randomSuffix = (""+Math.random()).replace(".","").substring(0,3);
		String destinationMapTable = tablename.replace("_", "")+randomSuffix;
		return destinationMapTable;
	}

}

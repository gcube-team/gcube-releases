package org.gcube.contentmanagement.timeseries.geotools.tools;

import java.util.HashSet;
import java.util.List;

import org.gcube.common.geoserverinterface.GeoserverCaller;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;

public class GeoSvrCleaner {

	static String selectTablesToClean = "SELECT tablename FROM pg_tables where tablename <> 'all_world' and tablename <> 'area' and tablename <> 'biodiversity' and tablename <> 'connectiontesttable' and tablename <> 'depth' and tablename <> 'depthmean' and tablename <> 'eezall' and tablename <> 'dissolved_salinity' and tablename <> 'enviroments' and tablename <> 'ice' and tablename <> 'geometry_columns' and tablename <> 'lme' and tablename <> 'new_all_world' and tablename <> 'occurrence' and tablename <> 'primprod' and tablename <> 'salinity ' and tablename <> 'faoarea' and tablename <> 'spatial_ref_sys' and tablename <> 'sst' and tablename <> 'temp2' and tablename <> 'world' and tablename <> 'world_borders' and tablename <> 'world_intersects' and tablename <> 'world_intersects2' and tablename <> 'world_nations' and tablename <> 'world_terra' and tablename <> 'salinity' and tablename <> 'pg_type' and tablename <> 'sql_features' and tablename <> 'sql_implementation_info' and tablename <> 'sql_languages' and tablename <> 'pg_statistic' and tablename <> 'sql_packages' and tablename <> 'sql_parts' and tablename <> 'sql_sizing' and tablename <> 'sql_sizing_profiles' and tablename <> 'pg_authid'  and tablename <> 'pg_ts_parser' and tablename <> 'pg_database' and tablename <> 'pg_shdepend' and tablename <> 'pg_shdescription' and tablename <> 'pg_ts_config' and tablename <> 'pg_ts_config_map' and tablename <> 'pg_ts_dict' and tablename <> 'pg_ts_template' and tablename <> 'pg_auth_members' and tablename <> 'pg_attribute' and tablename <> 'pg_proc' and tablename <> 'pg_class' and tablename <> 'pg_autovacuum' and tablename <> 'pg_attrdef' and tablename <> 'pg_constraint' and tablename <> 'pg_inherits' and tablename <> 'pg_index' and tablename <> 'pg_operator' and tablename <> 'pg_opfamily' and tablename <> 'pg_opclass' and tablename <> 'pg_am' and tablename <> 'pg_amop' and tablename <> 'pg_amproc' and tablename <> 'pg_language' and tablename <> 'pg_largeobject' and tablename <> 'pg_aggregate' and tablename <> 'pg_rewrite' and tablename <> 'pg_trigger' and tablename <> 'pg_listener'  and tablename <> 'pg_description' and tablename <> 'pg_cast' and tablename <> 'pg_enum' and tablename <> 'pg_namespace' and tablename <> 'pg_conversion' and tablename <> 'pg_depend' and tablename <> 'pg_tablespace' and tablename <> 'pg_pltemplate' and tablename not like '%fifao_%' and tablename not like '%occurr%'";
	static String aquamapsdb = "aquamapsdb";
	static String GisPwd = "gcube@geo2010";
	static String GisWorkspace = "aquamaps";
//static String GisWorkspace = "timeseries";
	static String GisUrl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
	static String GisUserName = "admin";
	static String DatabaseURL = "jdbc:postgresql://geoserver.d4science-ii.research-infrastructures.eu/aquamapsdb";
//	static String DatabaseURL = "jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb";
	static String DatabaseUserName = "postgres";
	static String DatabasePassword = "d4science2";

	
	public static void cleanTables() throws Exception {
		System.out.println("INIT - clean tables");
		long t0 = System.currentTimeMillis();
		EngineConfiguration configuration = new EngineConfiguration();

		configuration.setDatabaseURL(DatabaseURL);
		configuration.setDatabaseUserName(DatabaseUserName);
		configuration.setDatabasePassword(DatabasePassword);
		
		ConnectionsManager connectionsManager;
		connectionsManager = new ConnectionsManager("./cfg/");
		connectionsManager.initGeoserverConnection(configuration);
		
		System.out.println("SELECTING");
		List<Object> tables2drop = connectionsManager.GeoserverQuery(selectTablesToClean);
		System.out.println("CLEANING");
		if (tables2drop != null) {
			for (Object t : tables2drop) {
				System.out.println(t);
				
				String q = "drop table \"" + t + "\"";
				try {
					connectionsManager.GeoserverUpdate(q);
				} catch (Exception e) {
					// e.printStackTrace();
					System.err.println("DID NOT CANCEL TABLE " + t + " - " + e.getMessage() + " - ");
				}
				
			}
		}

		long t1 = System.currentTimeMillis();
		System.out.println("FINISHED IN " + (t1 - t0) + " ms");
	}

	// list taken from geoserver at directory: /usr/share/jetty/geoserver_data/workspaces/aquamaps/aquamapsdb
	public static void cleanLayers() throws Exception {
		System.out.println("INIT - clean layers");

		String saveListF$ = FileTools.loadString("./cfg/IndispensableLayers.txt", "UTF-8");
		String[] savefiles = saveListF$.split("\n");
		HashSet<String> hs = new HashSet<String>();
		for (String savefile : savefiles) {
			hs.add(savefile);
		}

		// String files$ = FileTools.loadString("./cfg/layers.txt", "UTF-8");
		// String [] files = files$.split("\n");

		GISInformation gisInfo = new GISInformation();

		gisInfo.setGisDataStore(aquamapsdb);
		gisInfo.setGisPwd(GisPwd);
		gisInfo.setGisWorkspace(GisWorkspace);
		gisInfo.setGisUrl(GisUrl);
		gisInfo.setGisUserName(GisUserName);
		long t0 = System.currentTimeMillis();
		System.out.println("starting deletion: ");
		GeoserverCaller caller = new GeoserverCaller(gisInfo.getGisUrl(), gisInfo.getGisUserName(), gisInfo.getGisPwd());
		List<String> files = caller.listLayers();

		for (String filename : files) {
			filename = filename.trim();

			try {
				if (!hs.contains(filename)) {
					new GISOperations().deleteLayer(gisInfo, filename);
					System.out.println("deleted layer: " + filename);
					Thread.sleep(4000);
				} else
					System.out.println("DID NOT CANCEL " + filename);

			} catch (Exception e) {
				System.err.println("could not delete layer: " + filename);
			}
			
			// System.out.println("deleted layer: "+filename);
		}

		long t1 = System.currentTimeMillis();
		System.out.println("finished in : " + (t1 - t0) + " ms");
	}

	
	// list taken from geoserver at directory: /usr/share/jetty/geoserver_data/workspaces/aquamaps/aquamapsdb
	public static void cleanSelectedStyles() throws Exception {
		System.out.println("INIT - clean Styles");
		String saveListF$ = FileTools.loadString("./cfg/layers.txt", "UTF-8");
		String[] savefiles = saveListF$.split("\n");
		HashSet<String> hs = new HashSet<String>();
		for (String savefile : savefiles) {
			hs.add(savefile);
		}

		GISInformation gisInfo = new GISInformation();

		gisInfo.setGisDataStore(aquamapsdb);
		gisInfo.setGisPwd(GisPwd);
		gisInfo.setGisWorkspace(GisWorkspace);
		gisInfo.setGisUrl(GisUrl);
		gisInfo.setGisUserName(GisUserName);
		long t0 = System.currentTimeMillis();
		System.out.println("starting deletion: ");
		GeoserverCaller caller = new GeoserverCaller(gisInfo.getGisUrl(), gisInfo.getGisUserName(), gisInfo.getGisPwd());
		List<String> files = caller.listStyles();

		for (String filename : files) {
			filename = filename.trim();

			try {
				if (hs.contains(filename)) {
					new GISOperations().deleteStyle(gisInfo, filename);
					System.out.println("deleted style: " + filename);
				} else
				{
//					System.out.println("DID NOT CANCEL " + filename);
				}
			} catch (Exception e) {
				System.err.println("could not delete layer: " + filename);
			}
			// System.out.println("deleted layer: "+filename);
		}

		long t1 = System.currentTimeMillis();
		System.out.println("finished in : " + (t1 - t0) + " ms");
	}
	
	public static void deleteStyle(String styleName) throws Exception{
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore(aquamapsdb);
		gisInfo.setGisPwd(GisPwd);
		gisInfo.setGisWorkspace(GisWorkspace);
		gisInfo.setGisUrl(GisUrl);
		gisInfo.setGisUserName(GisUserName);
		GeoserverCaller caller = new GeoserverCaller(gisInfo.getGisUrl(), gisInfo.getGisUserName(), gisInfo.getGisPwd());
		new GISOperations().deleteStyle(gisInfo, styleName);
	}
	
	// list taken from geoserver at directory: /usr/share/jetty/geoserver_data/workspaces/aquamaps/aquamapsdb
	public static void cleanStyles() throws Exception {
		System.out.println("INIT - clean Styles");
		String saveListF$ = FileTools.loadString("./cfg/IndispensableStyles.txt", "UTF-8");
		String[] savefiles = saveListF$.split("\n");
		HashSet<String> hs = new HashSet<String>();
		for (String savefile : savefiles) {
			hs.add(savefile);
		}

		GISInformation gisInfo = new GISInformation();

		gisInfo.setGisDataStore(aquamapsdb);
		gisInfo.setGisPwd(GisPwd);
		gisInfo.setGisWorkspace(GisWorkspace);
		gisInfo.setGisUrl(GisUrl);
		gisInfo.setGisUserName(GisUserName);
		long t0 = System.currentTimeMillis();
		System.out.println("starting deletion: ");
		GeoserverCaller caller = new GeoserverCaller(gisInfo.getGisUrl(), gisInfo.getGisUserName(), gisInfo.getGisPwd());
		List<String> files = caller.listStyles();

		for (String filename : files) {
			filename = filename.trim();

			try {
				if (!hs.contains(filename)) {
					new GISOperations().deleteStyle(gisInfo, filename);
					System.out.println("deleted style: " + filename);
				} else
				{
					System.out.println("DID NOT CANCEL " + filename);
				}
			} catch (Exception e) {
				System.err.println("could not delete layer: " + filename);
			}
			// System.out.println("deleted layer: "+filename);
		}

		long t1 = System.currentTimeMillis();
		System.out.println("finished in : " + (t1 - t0) + " ms");
	}

	// list taken from geoserver at directory: /usr/share/jetty/geoserver_data/workspaces/aquamaps/aquamapsdb
	public static void cleanGroups() throws Exception {
		System.out.println("INIT - clean Groups");
		String saveListF$ = FileTools.loadString("./cfg/IndispensableGroups.txt", "UTF-8");
		String[] savefiles = saveListF$.split("\n");
		HashSet<String> hs = new HashSet<String>();
		for (String savefile : savefiles) {
			hs.add(savefile);
		}

		GISInformation gisInfo = new GISInformation();

		gisInfo.setGisDataStore(aquamapsdb);
		gisInfo.setGisPwd(GisPwd);
		gisInfo.setGisWorkspace(GisWorkspace);
		gisInfo.setGisUrl(GisUrl);
		gisInfo.setGisUserName(GisUserName);
		long t0 = System.currentTimeMillis();
		System.out.println("starting deletion: ");
		GeoserverCaller caller = new GeoserverCaller(gisInfo.getGisUrl(), gisInfo.getGisUserName(), gisInfo.getGisPwd());
		List<String> files = caller.listLayerGroups();

		for (String filename : files) {
			filename = filename.trim();

			try {
				if (!hs.contains(filename)) {
					new GISOperations().deleteGroup(gisInfo, filename);
					System.out.println("deleted group: " + filename);
				} else
					System.out.println("DID NOT CANCEL " + filename);

			} catch (Exception e) {
				System.err.println("could not delete layer: " + filename);
			}
			// System.out.println("deleted layer: "+filename);
		}

		long t1 = System.currentTimeMillis();
		System.out.println("finished in : " + (t1 - t0) + " ms");
	}

	public static void main(String[] args) throws Exception {
//		cleanSelectedStyles();
		/*
		cleanLayers();
		cleanStyles();
		cleanGroups();
		*/
		deleteStyle("testGP4");
	//	cleanTables();
	}

}

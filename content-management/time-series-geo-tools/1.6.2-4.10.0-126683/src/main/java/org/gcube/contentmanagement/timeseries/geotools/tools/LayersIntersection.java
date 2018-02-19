package org.gcube.contentmanagement.timeseries.geotools.tools;

import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation;

public class LayersIntersection {
	ConnectionsManager connectionsManager;

	public LayersIntersection(TSGeoToolsConfiguration configuration) throws Exception {

		String configPath = configuration.getConfigPath();

		if (!configPath.endsWith("/"))
			configPath += "/";

		AnalysisLogger.setLogger(configPath + "ALog.properties");
		AnalysisLogger.getLogger().debug("LayersIntersection-> initializing connections");

		connectionsManager = new ConnectionsManager(configPath);

		EngineConfiguration geocfg = null;

		if (configuration.getGeoServerDatabase() != null) {
			geocfg = new EngineConfiguration();
			geocfg.setConfigPath(configPath);
			geocfg.setDatabaseUserName(configuration.getGeoServerUserName());
			geocfg.setDatabasePassword(configuration.getGeoServerPassword());
			geocfg.setDatabaseURL(configuration.getGeoServerDatabase());
		}
		connectionsManager.initGeoserverConnection(geocfg);
		AnalysisLogger.getLogger().debug("LayersIntersection-> connected to Geo Server");

	}

	private static String buildTempTable = "create table  %1$s (%2$s) WITH ( OIDS=FALSE ); ";
	private static String getGeometries = "select ST_AsText(%2$s) from %1$s";
	private static String getAll = "select %1$s from %2$s";
	private static String getAllFromSquares = "select %1$s from %2$s where csquarecode in (%3$s)";
	
	private static String getCsquaresPolygons = "select ST_AsText(the_geom) from new_all_world where csquarecode in (%1$s)";
	
	// private static String getDifference = "SELECT ST_AsText(ST_Difference( 'POLYGON(%1$s)'::geometry, 'POLYGON(%2$s)'::geometry ));";
	private static String getDifference = "SELECT ST_AsText(ST_Difference( '%1$s', '%2$s'));";

	private static String getDifferenceChunks = "SELECT distinct ST_AsText(ST_Difference( ST_SetSRID('%1$s',4326), ST_SetSRID(a.%3$s,4326))) from %2$s as a";
	private static String getDifferenceAll = "SELECT  %1$s, ST_AsText(ST_Difference( ST_SetSRID(a.%2$s,4326), ST_SetSRID(b.%3$s,4326))) from %4$s as a, %5$s as b";
	
	private static String checkEquality = "SELECT ST_Geometry('%1$s') = ST_Geometry('%2$s')";
	private static String getTableStructure = "SELECT column_name,data_type FROM information_schema.COLUMNS WHERE table_name ='%1$s'";
	private static String rawInsert = "INSERT INTO %1$s (%2$s) VALUES (%3$s)";
	private static String copyTables = "select * into %1$s from %2$s";
	private static String alterValueUpdate = "UPDATE %1$s SET %2$s = %3$s WHERE %4$s='%5$s'";

	

  // input to this procedure is : 
	//select d.csquarecode, d.c from (select a.csquarecode, ST_Intersects( ST_SetSRID(a.the_geom,4326), ST_SetSRID(b.the_geom,4326)) as c from depth as a, "fifao_UN_CONTINENT" as b) d where d.c = true
	public void Subtract4(String firstLayer, String secondLayer, String resultingTable, String firstLayerGeometryColumn, String secondLayerGeometryColumn, String secondLayerKey) throws Exception {

		try {
			String dropTableStatem = "drop table " + resultingTable;
			connectionsManager.GeoserverUpdate(dropTableStatem);
		} catch (Exception e) {
			System.out.println("did not drop table");
		}

		// copy table from the original
		System.out.println("copying table");
		long t0 = System.currentTimeMillis();
		String dropTableStatem = String.format(copyTables, resultingTable, secondLayer);
		connectionsManager.GeoserverUpdate(dropTableStatem);
		long t1 = System.currentTimeMillis();
		System.out.println("copying table - elapsed " + (t1 - t0));

		// System.out.println("taking first geoms");
		// List<Object> firstGeoms = connectionsManager.GeoserverQuery(String.format(getGeometries, firstLayer, firstLayerGeometryColumn));
		
		String allsquares = FileTools.loadString("./cfg/csquares.txt", "UTF-8").replace("\n","");
		System.out.println("taking second geoms " + String.format(getAllFromSquares, secondLayerKey + ",ST_AsText(" + secondLayerGeometryColumn + ")", secondLayer,allsquares));
		
		t0 = System.currentTimeMillis();
		List<Object> secondGeoms = connectionsManager.GeoserverQuery(String.format(getAllFromSquares, secondLayerKey + ",ST_AsText(" + secondLayerGeometryColumn + ")", secondLayer,allsquares));
		
		t1 = System.currentTimeMillis();
		System.out.println("elapsed " + (t1 - t0));

		int size = secondGeoms.size();

		int g = 0;
		System.out.println("searching for subtractions: size of search " + size);
		long t00 = System.currentTimeMillis(); 
		int dd = 0;
		
		for (Object sgeom : secondGeoms) {

			String $sgeom = (String) ((Object[]) sgeom)[1];
			String $cutGeom = $sgeom;
			String key = "" + (Integer) ((Object[]) sgeom)[0];
			boolean found = false;

			/*
			 * for (Object fgeom : firstGeoms) {
			 * 
			 * String $fgeom = (String) fgeom; String $difference = String.format(getDifference, $sgeom, $fgeom); List<Object> differenceRes = connectionsManager.GeoserverQuery($difference); String diff = (String) differenceRes.get(0); List<Object> check = connectionsManager.GeoserverQuery(String.format(checkEquality, diff, $sgeom));
			 * 
			 * if (!(Boolean) check.get(0)) { System.out.println("GEOMETRIES ARE DIFFERENT! " + String.format(checkEquality, diff, $sgeom)); $cutGeom = diff; found = true; }
			 * 
			 * }
			 */

			// System.out.println("get "+String.format(getDifferenceChunks,$sgeom,firstLayer,firstLayerGeometryColumn));
			List<Object> differences = connectionsManager.GeoserverQuery(String.format(getDifferenceChunks, $sgeom, firstLayer, firstLayerGeometryColumn));
			if (differences.size() > 1) {
				for (Object difference : differences) {
					List<Object> check = connectionsManager.GeoserverQuery(String.format(checkEquality, (String) difference, $sgeom));
					if (!(Boolean) check.get(0)) {
						System.out.println("GEOMETRIES ARE DIFFERENT! " + String.format(checkEquality, (String) difference, $sgeom)+" - "+dd);
//						System.out.println("Size: "+ differences.size()+" - "+dd);
						$cutGeom = (String) difference;
						found = true;
						dd++;
						break;
					}
				}
			}

//			System.out.print(g + " ");
			if (g%100==0)
				System.out.println(MathFunctions.roundDecimal(((double)g*100/(double)size),2)+"% - "+(g+1));
			
			// if ((g!=0)&&(size%g == 0))
			// System.out.println("status: "+((double)g/(double)size));

			if (found) {
				String alterValue = String.format(alterValueUpdate, resultingTable, secondLayerGeometryColumn, "ST_SetSRID(ST_Geometry('" + $cutGeom + "'),4326)", secondLayerKey, key);
//				System.out.println("inserting values " + alterValue);
				connectionsManager.GeoserverUpdate(alterValue);
//				System.out.println("insert ok!");
			}

			g++;
		}
		
		long t01 = System.currentTimeMillis();
		System.out.println("Elapsed Time: "+(t01-t00));
	}
	
	//this is alla automatic procedure
	public void Subtract2(String firstLayer, String secondLayer, String resultingTable, String firstLayerGeometryColumn, String secondLayerGeometryColumn, String secondLayerKey) throws Exception {

		try {
			String dropTableStatem = "drop table " + resultingTable;
			connectionsManager.GeoserverUpdate(dropTableStatem);
		} catch (Exception e) {
			System.out.println("did not drop table");
		}

		// copy table from the original
		System.out.println("copying table");
		long t0 = System.currentTimeMillis();
		String dropTableStatem = String.format(copyTables, resultingTable, secondLayer);
		connectionsManager.GeoserverUpdate(dropTableStatem);
		long t1 = System.currentTimeMillis();
		System.out.println("copying table - elapsed " + (t1 - t0));

		// System.out.println("taking first geoms");
		// List<Object> firstGeoms = connectionsManager.GeoserverQuery(String.format(getGeometries, firstLayer, firstLayerGeometryColumn));

		System.out.println("taking second geoms " + String.format(getAll, secondLayerKey + ",ST_AsText(" + secondLayerGeometryColumn + ")", secondLayer));
		t0 = System.currentTimeMillis();
		List<Object> secondGeoms = connectionsManager.GeoserverQuery(String.format(getAll, secondLayerKey + ",ST_AsText(" + secondLayerGeometryColumn + ")", secondLayer));
		t1 = System.currentTimeMillis();
		System.out.println("elapsed " + (t1 - t0));

		int size = secondGeoms.size();

		int g = 0;
		System.out.println("searching for subtractions: size of search " + size);
		long t00 = System.currentTimeMillis(); 
		int dd = 0;
		
		for (Object sgeom : secondGeoms) {

			String $sgeom = (String) ((Object[]) sgeom)[1];
			String $cutGeom = $sgeom;
			String key = "" + (Integer) ((Object[]) sgeom)[0];
			boolean found = false;

			/*
			 * for (Object fgeom : firstGeoms) {
			 * 
			 * String $fgeom = (String) fgeom; String $difference = String.format(getDifference, $sgeom, $fgeom); List<Object> differenceRes = connectionsManager.GeoserverQuery($difference); String diff = (String) differenceRes.get(0); List<Object> check = connectionsManager.GeoserverQuery(String.format(checkEquality, diff, $sgeom));
			 * 
			 * if (!(Boolean) check.get(0)) { System.out.println("GEOMETRIES ARE DIFFERENT! " + String.format(checkEquality, diff, $sgeom)); $cutGeom = diff; found = true; }
			 * 
			 * }
			 */

			// System.out.println("get "+String.format(getDifferenceChunks,$sgeom,firstLayer,firstLayerGeometryColumn));
			List<Object> differences = connectionsManager.GeoserverQuery(String.format(getDifferenceChunks, $sgeom, firstLayer, firstLayerGeometryColumn));
			if (differences.size() > 1) {
				for (Object difference : differences) {
					List<Object> check = connectionsManager.GeoserverQuery(String.format(checkEquality, (String) difference, $sgeom));
					if (!(Boolean) check.get(0)) {
						System.out.println("GEOMETRIES ARE DIFFERENT! " + String.format(checkEquality, (String) difference, $sgeom)+" - "+dd);
//						System.out.println("Size: "+ differences.size()+" - "+dd);
						$cutGeom = (String) difference;
						found = true;
						dd++;
						break;
					}
				}
			}

//			System.out.print(g + " ");
			if (g%100==0)
				System.out.println(MathFunctions.roundDecimal(((double)g/(double)size),2)+"% - "+(g+1));
			
			// if ((g!=0)&&(size%g == 0))
			// System.out.println("status: "+((double)g/(double)size));

			if (found) {
				String alterValue = String.format(alterValueUpdate, resultingTable, secondLayerGeometryColumn, "ST_SetSRID(ST_Geometry('" + $cutGeom + "'),4326)", secondLayerKey, key);
//				System.out.println("inserting values " + alterValue);
				connectionsManager.GeoserverUpdate(alterValue);
//				System.out.println("insert ok!");
			}

			g++;
		}
		
		long t01 = System.currentTimeMillis();
		System.out.println("Elapsed Time: "+(t01-t00));
	}

	public void generateLayer(String styleName, String tableName) {

		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");

		GISStyleInformation style = new GISStyleInformation();
		style.setStyleName(styleName);

		GISLayerInformation gisLayer1 = new GISLayerInformation();
		gisLayer1 = new GISLayerInformation();
		gisLayer1.setDefaultStyle(style.getStyleName());
		gisLayer1.setLayerName(tableName);
		gisInfo.addLayer(gisLayer1);
		gisInfo.addStyle(gisLayer1.getLayerName(), style);
		try {
			new GISOperations().createLayers(gisInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath("./cfg/");
//		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver.d4science-ii.research-infrastructures.eu/aquamapsdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");

		String firstLayer = "\"fifao_UN_CONTINENT\"";
		String secondLayer = "depth";
		String resultingTable = "depthmean";
		String firstLayerGeometryColumn = "the_geom";
		String secondLayerGeometryColumn = "the_geom";
		String secondLayerKey = "depth_fid";

		LayersIntersection li = new LayersIntersection(configuration);
		// li.Subtract(firstLayer, secondLayer, resultingTable,firstLayerGeometryColumn,secondLayerGeometryColumn);

//		li.Subtract4(firstLayer, secondLayer, resultingTable, firstLayerGeometryColumn, secondLayerGeometryColumn, secondLayerKey);
		 li.generateLayer("environments", "Env");

		// depth_style va associato a depthCut
	}

}

package org.gcube.contentmanagement.timeseries.geotools.vti.connectors;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;

public class FAOAreaExtractor {

	/*
	 * public static void main(String[] args) { Double d = new Double(0.25, 0.75); Double [] dd = new Double[3]; for (int i=0;i<3;i++) dd[i] = d;
	 */
	/*
	 * Tuple<String> [] tt = getAreas(dd); for (Tuple<String> t : tt) System.out.println("tuple "+t);
	 */
	// }
	public String geoServerURL;
	ConnectionsManager connectionsManager;

	public FAOAreaExtractor(String geoServerURL, ConnectionsManager connectionsManager) {
		this.geoServerURL = geoServerURL;
		this.connectionsManager = connectionsManager;
	}

	private static String dropTable = "drop table %1$s";
	private static String createTable = "create table %1$s (gid serial NOT NULL)";
	private static String addPointsColumn = "select AddGeometryColumn('%1$s','the_geom',4326,'POINT',2)";
	private static String insertInto = "insert into %1$s(gid, the_geom) values %2$s;";
	private static String insertionStatement = "(%1$s, ST_SetSRID(ST_MakePoint(%2$s,%3$s),4326))";
	// private static String intersectionQuery = "SELECT  a.%1$s, b.%2$s ST_AsText(ST_Intersects( ST_SetSRID(a.%3$s,4326), ST_SetSRID(b.%4$s,4326))) from %5$s as a, %6$s as b";
	// TO-DO: insert this query
	// private static String intersectionQuery = "select * from (SELECT  distinct a.gid, b.f_area, ST_Intersects( ST_SetSRID(a.the_geom,4326), ST_SetSRID(b.the_geom,4326)) as r from points_88f3dfcd_d9b2_4e71_8f62_57cab5062006 as a, "fifao_FAO_SUB_DIV" as b) g where r = 't'"
	private static String intersectionQuery = "select * from (SELECT  distinct a.gid, b.%1$s, ST_Intersects( ST_SetSRID(a.the_geom,4326), ST_SetSRID(b.%2$s,4326)) as r from %3$s as a, \"%4$s\" as b) g where r = 't'";
	private static String faoTableGeoCol = "the_geom";
	private static String faoTableInfoCol = "f_area";

	private String[] faoTables = new String[] { "fifao_FAO_SUB_UNITPolygon", "fifao_FAO_SUB_AREAPolygon", "fifao_FAO_SUB_DIV","fifao_FAO_DIV", "fifao_FAO_MAJORPolygon" };

	public String createTempTable(Double[] points) throws Exception {
		String tempTable = ("points_" + UUID.randomUUID()).replace("-", "_");

		// create temporary table on geoserver
		connectionsManager.GeoserverUpdate(String.format(createTable, tempTable));
		// add geometry column
		connectionsManager.GeoserverQuery(String.format(addPointsColumn, tempTable));
		// insert all the points
		int m = points.length;
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < m; i++) {
			buff.append(String.format(insertionStatement, i, points[i].x, points[i].y));
			if ((i < m - 1) && (i % 100 != 0))
				buff.append(",");
			else if (i % 100 == 0) {
//				System.out.println("inserting: " + String.format(insertInto, tempTable, buff.toString()));
				connectionsManager.GeoserverUpdate(String.format(insertInto, tempTable, buff.toString()));
				buff = null;
				buff = new StringBuffer();
			}
		}
		
		if (buff.length()>0)
			connectionsManager.GeoserverUpdate(String.format(insertInto, tempTable, buff.toString()));
		
		return tempTable;
	}

	public void deleteTable(String table) {
		try {
			connectionsManager.GeoserverUpdate(String.format(dropTable, table));
		} catch (Exception e) {
			AnalysisLogger.getLogger().warn("deleteTable->Could not delete the table " + e.getMessage());
		}
	}

	public List<Object> intersects(String tempTable, String faoTable, String faoTablegeoColumn, String faoTableInfoColumn) throws Exception {
//		System.out.println("executing query: " + String.format(intersectionQuery, faoTableInfoColumn, faoTablegeoColumn, tempTable, faoTable));
		List<Object> intersections = connectionsManager.GeoserverQuery(String.format(intersectionQuery, faoTableInfoColumn, faoTablegeoColumn, tempTable, faoTable));
		return intersections;
	}

	private String[] createDefaultInitialization() {
		String[] array$ = new String[faoTables.length];

		for (int i = 0; i < faoTables.length; i++) {
			array$[i] = "";
		}
		return array$;
	}

	public Tuple<String>[] getAreas(Double[] points) {

		String tempTab = "";
		Tuple<String>[] tupleArray = new Tuple[points.length];
		// tuples initialization
		tupleArray = setDefaults(points);
		try {
			AnalysisLogger.getLogger().trace("FAOAreaExtractor->generating temp table");
			tempTab = createTempTable(points);
			// for each fao area
			for (int j = 0; j < faoTables.length; j++) {
				AnalysisLogger.getLogger().trace("FAOAreaExtractor->performing intersection ");
				// perform intersection
				List<Object> results = intersects(tempTab, faoTables[j], faoTableGeoCol, faoTableInfoCol);
				if (results != null) {
					int nInters = results.size();
					AnalysisLogger.getLogger().trace("FAOAreaExtractor->updating tuples for FAO AREA: "+faoTables[j]);
					for (int i = 0; i < nInters; i++) {
						Object[] row = (Object[]) results.get(i);
						Integer index = (Integer) row[0];
						String faoArea = (String) row[1];
//						AnalysisLogger.getLogger().trace("FAOAreaExtractor->index " + index + " faoArea " + faoArea);
						// get the tuple number 'index'
						Tuple<String> tupleToUpdate = tupleArray[index];
						// update the tuple at the j-th position
						tupleToUpdate.getElements().set(j, faoArea);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().trace("FAOAreaExtractor->Error was found " + e.getMessage());
			tupleArray = setDefaults(points);
		} finally {
			AnalysisLogger.getLogger().trace("FAOAreaExtractor->deleting table");
			deleteTable(tempTab);
		}
		return tupleArray;
	}

	public Tuple<String>[] setDefaults(Double[] points) {

		Tuple<String>[] tupleArray = new Tuple[points.length];
		for (int i = 0; i < points.length; i++) {
			tupleArray[i] = new Tuple<String>(createDefaultInitialization());
		}
		return tupleArray;
	}

	public Tuple<String>[] getAreasOLD(Double[] points) {

		Tuple<String>[] tupleArray = new Tuple[points.length];

		/*
		 * String area1 =""+ (int)(Math.random()*70f); String area2 =""+ (int)(Math.random()*70f); String area3 =""+ (int)(Math.random()*70f); String area4 =""+ (int)(Math.random()*70f); String area5 =""+ (int)(Math.random()*70f);
		 */
		String area1 = "-9999";
		String area2 = "-9999";
		String area3 = "-9999";
		String area4 = "-9999";
		String area5 = "-9999";

		/*
		 * //code added in date 23-08-2011 //TO-DO : add layers for fao areas!!! String[] types = new String[] {"aquamaps:fifao_FAO_SUB_UNIT","aquamaps:fifao_FAO_SUB_DIV","aquamaps:fifao_FAO_SUB_AREA","aquamaps:fifao_FAO_DIV","aquamaps:fifao_FAO_MAJOR"}; // String[] types = new String[] {"aquamaps:fifao_FAO_DIV"}; FishingAreaLocator fal = new FishingAreaLocator(geoServerURL+"/geoserver/wfs?request=GetCapabilities&version=1.0.0", types); fal.setNamePropertyName("F_AREA"); String[][] matrix = fal.getAreas(points);
		 * 
		 * for (int i = 0; i < points.length; i++) { // Print points and all their matching features by feature type (one point per line) Point2D.Double p = points[i];
		 * 
		 * System.out.print("Point (" + p.x + "," + p.y + "): "); for (int j = 0; j < fal.getTypeNames().length; j++) { if (j != 0) System.out.print(" "); System.out.print(matrix[i][j] == null ? "[-]" : "[" + matrix[i][j] + "]");
		 * 
		 * }
		 * 
		 * System.out.println();
		 * 
		 * Tuple<String> tuple = new Tuple<String>(matrix[i]); tupleArray[i] = tuple; }
		 */
		for (int i = 0; i < points.length; i++) {

			tupleArray[i] = new Tuple<String>(area1, area2, area3, area4, area5);

		}

		return tupleArray;
	}

	public static void main1(String[] args) {
		System.out.println("GO!");

		Point2D.Double[] points = new Point2D.Double[] { new Point2D.Double(-20.303, 66.537), new Point2D.Double(-21.710, 63.837) };
		FAOAreaExtractor fae = new FAOAreaExtractor("http://geoserver.d4science-ii.research-infrastructures.eu", null);
		fae.getAreas(points);
	}

	public static void main(String[] args) throws Exception {

		EngineConfiguration configurationTS = new EngineConfiguration();
		configurationTS.setConfigPath("./cfg/");
		configurationTS.setDatabaseURL("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdatedOLD");
		configurationTS.setDatabaseUserName("utente");
		configurationTS.setDatabasePassword("d4science");

		EngineConfiguration configurationGEO = new EngineConfiguration();
		configurationGEO.setConfigPath("./cfg/");
		configurationGEO.setDatabaseURL("jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
		configurationGEO.setDatabaseUserName("postgres");
		configurationGEO.setDatabasePassword("d4science2");

		ConnectionsManager connectionsManager = new ConnectionsManager(configurationTS.getConfigPath());
		connectionsManager.initTimeSeriesConnection(configurationTS);

		connectionsManager.initGeoserverConnection(configurationGEO);

		String url = "http://geoserver.d4science-ii.research-infrastructures.eu";

		FAOAreaExtractor fao = new FAOAreaExtractor(url, connectionsManager);
		AnalysisLogger.setLogger("./cfg/ALog.properties");
//		Point2D.Double[] points = new Point2D.Double[] { new Point2D.Double(-20.303, 66.537), new Point2D.Double(-21.710, 63.837) };
		Point2D.Double[] points = new Point2D.Double[] { new Point2D.Double(-129.023,16.720)};
		/*
		 * String faoTable = "fifao_FAO_SUB_DIV"; String faoTableGeoCol = "the_geom"; String faoTableInfoCol = "f_area";
		 */

		Tuple<String>[] tuple = fao.getAreas(points);
		for (int i = 0; i < tuple.length; i++)
			System.out.println(tuple[i]);

	}

	public static void main2(String[] args) {
		System.out.println("GO!");
		String[] types = new String[] { "aquamaps:fifao_FAO_SUB_UNIT", "aquamaps:fifao_FAO_SUB_DIV", "aquamaps:fifao_FAO_SUB_AREA", "aquamaps:fifao_FAO_DIV", "aquamaps:fifao_FAO_MAJOR" };
		/*
		 * FishingAreaLocator fal = new FishingAreaLocator("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/wfs?request=GetCapabilities&version=1.0.0", types); fal.setNamePropertyName("F_AREA");
		 * 
		 * Point2D.Double[] points = new Point2D.Double[] { new Point2D.Double(-20.303, 66.537), new Point2D.Double(-21.710, 63.837) };
		 * 
		 * String[][] matrix = fal.getAreas(points);
		 * 
		 * for (int i = 0; i < points.length; i++) { // Print points and all their matching features by feature type (one point per line) Point2D.Double p = points[i]; System.out.print("Point (" + p.x + "," + p.y + "): "); for (int j = 0; j < fal.getTypeNames().length; j++) { if (j != 0) System.out.print(" "); System.out.print(matrix[i][j] == null ? "[-]" : "[" + matrix[i][j] + "]"); } System.out.println(); }
		 */
	}

}

package org.gcube.dataanalysis.geo.matrixmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.utils.CSquareCodesConverter;
import org.gcube.dataanalysis.geo.utils.VectorOperations;
import org.hibernate.SessionFactory;

/**
 * transforms a raster map into a table
 * 
 * @author coro
 * 
 */
public class RasterTable {

	private double valuesMatrix[][];
	public HashMap<Double, Map<String, String>> valuesPropertiesMap;
	double x1;
	double x2;
	double y1;
	double y2;
	double z;
	double time;
	double xResolution;
	double yResolution;
	List<Tuple<Double>> coordinates;

	private AlgorithmConfiguration configuration;
	private String tablename = "rstr" + ("" + UUID.randomUUID()).replace("-", "");
	// static String createTableStatement = "CREATE TABLE %1$s (id serial, csquarecode character varying, x real, y real, z real, t real, fvalue real)";
	static String createTableStatementStandard = "CREATE TABLE %1$s (id serial, csquarecode character varying, x real, y real, z real, time real, fvalue character varying)";
	static String createTableStatementWithFields = "CREATE TABLE %1$s (id serial,  csquarecode character varying,  approx_x real, approx_y real, z real, time real, %2$s)";

	static String columnsnamesStandard = "csquarecode, x , y , z , time, fvalue";
	static String columnsnamesWithFields = "csquarecode, approx_x , approx_y , z , time , %1$s";

	public static String csquareColumn = "csquarecode";
	public static String valuesColumn = "fvalue";
	public static String idColumn = "id";

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public List<Tuple<Double>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Tuple<Double>> coordinates) {
		this.coordinates = coordinates;
	}

	public RasterTable(double x1, double x2, double y1, double y2, double z, double xResolution, double yResolution, double[][] values, AlgorithmConfiguration configuration) {
		init(x1, x2, y1, y2, z, 0, xResolution, yResolution, values, null, configuration);
	}

	public RasterTable(double x1, double x2, double y1, double y2, double z, double time, double xResolution, double yResolution, double[][] values, AlgorithmConfiguration configuration) {
		init(x1, x2, y1, y2, z, time, xResolution, yResolution, values, null, configuration);
	}

	public RasterTable(double x1, double x2, double y1, double y2, double z, double xResolution, double yResolution, double[][] values, HashMap<Double, Map<String, String>> valuesPropertiesMap, AlgorithmConfiguration configuration) {
		init(x1, x2, y1, y2, z, 0, xResolution, yResolution, values, valuesPropertiesMap, configuration);
	}

	public RasterTable(double x1, double x2, double y1, double y2, double z, double time, double xResolution, double yResolution, double[][] values, HashMap<Double, Map<String, String>> valuesPropertiesMap, AlgorithmConfiguration configuration) {
		init(x1, x2, y1, y2, z, time, xResolution, yResolution, values, valuesPropertiesMap, configuration);
	}

	public void init(double x1, double x2, double y1, double y2, double z, double time, double xResolution, double yResolution, double[][] values, HashMap<Double, Map<String, String>> valuesPropertiesMap, AlgorithmConfiguration configuration) {
		this.valuesMatrix = values;
		if (valuesPropertiesMap != null && valuesPropertiesMap.size() > 0)
			this.valuesPropertiesMap = valuesPropertiesMap;
		this.configuration = configuration;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z = z;
		this.time = time;
		this.xResolution = xResolution;
		this.yResolution = yResolution;
	}

	public void dumpGeoTable() {

		// open the connection to the db
		SessionFactory dbconnection = DatabaseUtils.initDBSession(configuration);
		try {
			AnalysisLogger.getLogger().debug("Database Initialized");
			// create a table
			String columnNames = columnsnamesStandard;
			String emptycolumns = "";
			if (valuesPropertiesMap == null) {
				AnalysisLogger.getLogger().debug("Rasterization->No properties to associate");
				DatabaseFactory.executeSQLUpdate(String.format(createTableStatementStandard, tablename), dbconnection);
			} else {
				AnalysisLogger.getLogger().debug("Managing Table with Custom Fields");
				Map<String, String> valuesMap = valuesPropertiesMap.values().iterator().next();
				AnalysisLogger.getLogger().debug("Rasterization->Sample of properties: " + valuesMap);
				emptycolumns = generateEmptyValues(valuesMap.size());
				DatabaseFactory.executeSQLUpdate(String.format(createTableStatementWithFields, tablename, propertiesMapToColumnString(valuesMap, true)), dbconnection);
				columnNames = String.format(columnsnamesWithFields, propertiesMapToColumnString(valuesMap, false));
				AnalysisLogger.getLogger().debug("Column names: " + columnNames);
			}
			AnalysisLogger.getLogger().debug("Table " + tablename + " created");
			if (coordinates == null)
				coordinates = VectorOperations.generateCoordinateTripletsInBoundingBox(x1, x2, y1, y2, z, xResolution, yResolution);

			int triplets = coordinates.size();
			AnalysisLogger.getLogger().debug("Generated " + triplets + " coordinates triples");
			List<Double> values = associateValueToCoordinates(coordinates, valuesMatrix);
			AnalysisLogger.getLogger().debug("Association to values completed - fulfilling buffer");
			// for each element in the matrix, build the corresponding csquare code
			StringBuffer sb = new StringBuffer();
			int rowcounter = 1;
			for (int i = 0; i < triplets; i++) {
				// save the string in a buffer
				Tuple<Double> cset = coordinates.get(i);
				double x = cset.getElements().get(0);
				double y = cset.getElements().get(1);
				String csquare = CSquareCodesConverter.convertAtResolution(y, x, xResolution);
				String valueForTable = "";
				// if we have fields insert fields, otherwise insert double numbers
				Double value = values.get(i);

				if (valuesPropertiesMap == null) {
					// we do not use NaNs in this case every value will be filled
					if (value.isNaN()) {
						value = 0d;
						valueForTable = null;
					} else
						valueForTable = "'" + value + "'";
				} else {
					// we do not use NaNs in this case every value will be filled
					if (value.isNaN())
						valueForTable = null;
					else
						valueForTable = propertiesMapToDatabaseString(valuesPropertiesMap.get(values.get(i)));
				}

				double zVal = z;
				if (cset.getElements().size() > 2)
					zVal = cset.getElements().get(2);

				String tVal = "" + time;
				if (cset.getElements().size() > 3) {

					tVal = "" + cset.getElements().get(3);
					if (Double.isNaN(cset.getElements().get(3)) || (Double.isInfinite(cset.getElements().get(3))))
						tVal = "NULL";
				}

				if (valueForTable != null) {
					rowcounter++;
					if (valuesPropertiesMap == null)
						sb.append("('" + csquare + "'," + x + "," + y + "," + zVal + "," + tVal + "," + valueForTable + ")");
					else
						sb.append("('" + csquare + "'," + x + "," + y + "," + zVal + "," + tVal + "," + valueForTable + ")");
				}
				if (rowcounter % 5000 == 0) {
					// AnalysisLogger.getLogger().debug("Partial Inserting Buffer of " + sb.length() + " Values");
					if (sb.length() > 0) {
						String insertStatement = DatabaseUtils.insertFromBuffer(tablename, columnNames, sb);
						// AnalysisLogger.getLogger().debug("Inserting Buffer " + insertStatement);
						DatabaseFactory.executeSQLUpdate(insertStatement, dbconnection);
					}
					// AnalysisLogger.getLogger().debug("Partial Insertion completed with Success!");
					sb = new StringBuffer();
				} else if (valueForTable != null)
					sb.append(",");

			}

			AnalysisLogger.getLogger().debug("Inserting Final Buffer of " + sb.length() + " Values");
			// AnalysisLogger.getLogger().debug("Inserting Final Buffer " + sb);
			// save all the strings on the table
			if (sb.length() > 0) {
				String insertStatement = DatabaseUtils.insertFromString(tablename, columnNames, sb.substring(0, sb.length() - 1));
//				 AnalysisLogger.getLogger().debug("Inserting Buffer " + insertStatement);
				DatabaseFactory.executeSQLUpdate(insertStatement, dbconnection);
				AnalysisLogger.getLogger().debug("Insertion completed with Success!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Error in dumping table: " + e.getLocalizedMessage());
		} finally {
			// close the connection
			DatabaseUtils.closeDBConnection(dbconnection);
			AnalysisLogger.getLogger().debug("Raster Geo Table DB closed!");
		}
	}

	public static String propertiesMapToDatabaseString(Map<String, String> valuesMap) {
		StringBuffer sb = new StringBuffer();
		int m = valuesMap.size();
		int i = 0;
		
		for (String value : valuesMap.values()) {
			if (value.equals("NULL"))
				sb.append(value);
			else
				sb.append("'" + value.replace("'", "" + (char) 96) + "'");
			if (i < m - 1)
				sb.append(",");

			i++;
		}

		return sb.toString();
	}

	public static String propertiesMapToColumnString(Map<String, String> valuesMap, boolean withtype) {
		StringBuffer sb = new StringBuffer();
		int m = valuesMap.size();
		int i = 0;
		for (String keys : valuesMap.keySet()) {
			sb.append("f_" + keys);
			if (withtype)
				sb.append(" character varying");
			if (i < m - 1)
				sb.append(",");

			i++;
		}

		return sb.toString();
	}

	public static String generateEmptyValues(int nValues) {
		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < nValues; j++) {
			sb.append("NULL");
			if (j < nValues - 1)
				sb.append(",");
		}
		return sb.toString();
	}

	public void deleteTable() {
		SessionFactory dbconnection = null;
		try {
			dbconnection = DatabaseUtils.initDBSession(configuration);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(tablename), dbconnection);
		} catch (Exception e) {
			// e.printStackTrace();
			AnalysisLogger.getLogger().debug("Impossible to delete table " + tablename + " : " + e.getLocalizedMessage());
		} finally {
			DatabaseUtils.closeDBConnection(dbconnection);
		}
	}

	public static List<Double> associateValueToCoordinates(List<Tuple<Double>> coordinates, double[][] data) {
		List<Double> values = new ArrayList<Double>();

		int k = 0;
		int g = 0;
		int ntriplets = coordinates.size();
		int xsteps = data[0].length - 1;
		for (int t = 0; t < ntriplets; t++) {
			values.add(data[k][g]);
			if (g == xsteps) {
				g = 0;
				k++;
			} else
				g++;
		}

		return values;
	}

}

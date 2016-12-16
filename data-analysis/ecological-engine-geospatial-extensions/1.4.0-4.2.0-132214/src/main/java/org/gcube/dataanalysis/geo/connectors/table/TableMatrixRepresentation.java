package org.gcube.dataanalysis.geo.connectors.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.utils.VectorOperations;
import org.hibernate.SessionFactory;

public class TableMatrixRepresentation {

	public static String xDimensionColumnParameter = "xColumn";
	public static String yDimensionColumnParameter = "yColumn";
	public static String zDimensionColumnParameter = "zColumn";
	public static String timeDimensionColumnParameter = "timeColumn";
	public static String valueDimensionColumnParameter = "valueColumn";
	public static String tableNameParameter = "geoReferencedTableName";
	public static String filterParameter = "filter";
	
	public HashMap<String, Integer> currentTimes = null;
	public HashMap<Double,List<Tuple<Double>>> currentcoordinates5d=null;
	public double maxZ = 0;
	public double minZ = 0;
	
	public void build5DTuples(AlgorithmConfiguration configuration, boolean cacheElements) throws Exception {

		currentTimes = new HashMap<String, Integer>();

		SessionFactory dbconnection = DatabaseUtils.initDBSession(configuration);
		try {
			String xField = configuration.getParam(xDimensionColumnParameter);
			String yField = configuration.getParam(yDimensionColumnParameter);
			String zField = configuration.getParam(zDimensionColumnParameter);
			String tField = configuration.getParam(timeDimensionColumnParameter);
			String valueField = configuration.getParam(valueDimensionColumnParameter);
			String tableName = configuration.getParam(tableNameParameter);

			if (tableName == null)
				throw new Exception("TableMatrixRepresentation: Error in retrieving elements from table, table name is null");

			String dbtuple = "";

			if (xField != null && xField.trim().length()>0)
				dbtuple += "\""+xField + "\" as x,";
			else
				dbtuple += "0 as x,";
			if (yField != null && yField.trim().length()>0)
				dbtuple += "\""+yField + "\" as y,";
			else
				dbtuple += "0 as y,";
			if (zField != null && zField.trim().length()>0)
				dbtuple += "\""+zField + "\" as z,";
			else
				dbtuple += "0 as z,";
			if (tField != null && tField.trim().length()>0)
				dbtuple += "\""+tField + "\" as time,";
			else
				dbtuple += "0 as time,";
			if (valueField != null && valueField.trim().length()>0)
				dbtuple += "\""+valueField+"\" as v";
			else
				dbtuple += "0 as v";

			List<Object> rows = null;
			
			if ((currentcoordinates5d == null) || !cacheElements){
				currentcoordinates5d = new HashMap<Double, List<Tuple<Double>>>();
				
				//find maxZ
				if (zField!=null && zField.trim().length()>0){
					String maxzq = "select max("+zField+") as max,min("+zField+") as min from "+tableName;
					Object [] maxzr = (Object [] )DatabaseFactory.executeSQLQuery(maxzq, dbconnection).get(0);
					maxZ = Double.parseDouble(""+maxzr[0]);
					minZ = Double.parseDouble(""+maxzr[1]);
				}
				
				String query = "select " + dbtuple + " from " + tableName;
				String filter=configuration.getParam(filterParameter);
				
				if (filter!=null && filter.trim().length()>0)
					query+=" where "+filter;
				
				query += " order by time";
				
				AnalysisLogger.getLogger().debug("TableMatrixRepresentation-> Query to execute: " + query);
				rows = DatabaseFactory.executeSQLQuery(query, dbconnection);
				AnalysisLogger.getLogger().debug("TableMatrixRepresentation-> Returned " + rows.size() + " rows");
				for (Object row : rows) {
					Object[] orow = (Object[]) row;
					Tuple<Double> t = build5DTuple(orow);
					double time = t.getElements().get(3);
					List<Tuple<Double>> coordinates5d = currentcoordinates5d.get(time);
					if (coordinates5d==null){
						coordinates5d=new ArrayList<Tuple<Double>>();
						currentcoordinates5d.put(time, coordinates5d);
					}
					//else
						//AnalysisLogger.getLogger().debug("TableMatrixRepresentation-> yet found time "+time+"->"+orow[3]);
					coordinates5d.add(t);
				}
				AnalysisLogger.getLogger().debug("TableMatrixRepresentation-> Association complete");
				AnalysisLogger.getLogger().debug("TableMatrixRepresentation-> coordinates set complete: "+currentcoordinates5d.size());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Error in getting elements from DB: " + e.getLocalizedMessage());
			throw e;
		} finally {
			if (dbconnection != null)
				dbconnection.close();
		}

	}

	private Tuple<Double> build5DTuple(Object[] row) {

		double x = Double.parseDouble("" + row[0]);
		double y = Double.parseDouble("" + row[1]);
		double z = Double.parseDouble("" + row[2]);
		double value = Double.NaN;
		if (row[4]!=null && ((""+row[4]).trim().length()>0))
			value = Double.parseDouble("" + row[4]);

		// transform time into a sequence
		String time = "" + row[3];
		Integer yetTime = currentTimes.get(time);
		if (yetTime == null) {
			int size = currentTimes.size();
			currentTimes.put(time, size);
			yetTime = size;
		}
		return new Tuple<Double>(x, y, z, (double)yetTime, value);
		
	}

	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// vessels
		config.setParam(tableNameParameter, "generic_id037d302d_2ba0_4e43_b6e4_1a797bb91728");

//		config.setParam(xDimensionColumnParameter, "x");
//		config.setParam(yDimensionColumnParameter, "y");
		// config.setParam(zDimensionColumnParameter,"");
		config.setParam(timeDimensionColumnParameter, "datetime");
		config.setParam(valueDimensionColumnParameter, "speed");

		config.setParam(filterParameter, "speed<2");
		
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		TableMatrixRepresentation tmr = new TableMatrixRepresentation();
		tmr.build5DTuples(config,false);

		List<Tuple<Double>> tuples = tmr.currentcoordinates5d.get(0d);
		
		AnalysisLogger.getLogger().debug("TUPLES:" + tuples);

		List<Tuple<Double>> grid = VectorOperations.generateCoordinateTripletsInBoundingBox(-47.14, -46.00, 44.52, 45.55, 0, 0.5, 0.5);
		List<Double> values = VectorOperations.assignPointsValuesToGrid(grid, 0, tuples, 0.5);

		AnalysisLogger.getLogger().debug("VALUES:" + values);

	}

}

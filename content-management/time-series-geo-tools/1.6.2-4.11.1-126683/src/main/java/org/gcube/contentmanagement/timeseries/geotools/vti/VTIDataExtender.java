package org.gcube.contentmanagement.timeseries.geotools.vti;

import java.awt.geom.Point2D.Double;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;
import org.gcube.contentmanagement.timeseries.geotools.vti.connectors.Bathymetry;
import org.gcube.contentmanagement.timeseries.geotools.vti.connectors.FAOAreaExtractor;
import org.gcube.contentmanagement.timeseries.geotools.vti.connectors.FishingHoursCalculator;
import org.gcube.contentmanagement.timeseries.geotools.vti.connectors.SurfaceTemperatureExtractor;
import org.gcube.contentmanagement.timeseries.geotools.vti.connectors.VTIClassificator;
import org.gcube.contentmanagement.timeseries.geotools.vti.connectors.VTIDateFormatConverter;

public class VTIDataExtender {

	ConnectionsManager connectionsManager;
	Bathymetry bathymetryObj;
	private String bathymetryFile;
	private String geoserverURL;
	private VTIColumnsBuilder columnProvider;
	
	public static enum DataExtenderFunctionalities {
		bathymetry, fao_areas, sst, vti_dates, fishing_hours, classify
	};

	private float status;
	private int chunkSize = 1000;

	public VTIDataExtender(TSGeoToolsConfiguration configuration) throws Exception {

		String configPath = configuration.getConfigPath();

		if (!configPath.endsWith("/"))
			configPath += "/";

		AnalysisLogger.setLogger(configPath + "ALog.properties");
		
		long t00 = System.currentTimeMillis();
		AnalysisLogger.getLogger().trace("DataExtender-> initializing objects");
		// init Bathymetry singleton
		bathymetryFile = configPath + "gebco_08.nc";
		Bathymetry.initInstance(bathymetryFile);
		long t01 = System.currentTimeMillis();
		AnalysisLogger.getLogger().trace("DataExtender-> initialized in "+(t01-t00)+"ms");
		
		AnalysisLogger.getLogger().trace("DataExtender-> initializing connections");
		connectionsManager = new ConnectionsManager(configPath);

		EngineConfiguration tscfg = null;
		EngineConfiguration geocfg = null;

		if (configuration.getTimeSeriesDatabase() != null) {
			tscfg = new EngineConfiguration();
			tscfg.setConfigPath(configPath);
			tscfg.setDatabaseUserName(configuration.getTimeSeriesUserName());
			tscfg.setDatabasePassword(configuration.getTimeSeriesPassword());
			tscfg.setDatabaseURL(configuration.getTimeSeriesDatabase());
		}
		if (configuration.getGeoServerDatabase() != null) {
			geocfg = new EngineConfiguration();
			geocfg.setConfigPath(configPath);
			geocfg.setDatabaseUserName(configuration.getGeoServerUserName());
			geocfg.setDatabasePassword(configuration.getGeoServerPassword());
			geocfg.setDatabaseURL(configuration.getGeoServerDatabase());
		}

		connectionsManager.initTimeSeriesConnection(tscfg);
		AnalysisLogger.getLogger().trace("EffortAggregator-> connected to Time Series");
		connectionsManager.initGeoserverConnection(geocfg);
		AnalysisLogger.getLogger().trace("EffortAggregator-> connected to Geo Server");
		columnProvider = new VTIColumnsBuilder();
		long t02 = System.currentTimeMillis();
		AnalysisLogger.getLogger().trace("DataExtender-> connections initialized in "+(t02-t01)+"ms");
		
		/*
		try{
			org.apache.log4j.Logger.getLogger("net.sf.hibernate").setLevel(org.apache.log4j.Level.FATAL);
			org.apache.log4j.Logger.getLogger("org.hibernate").setLevel(org.apache.log4j.Level.FATAL);
			}
			catch(Exception e){ e.printStackTrace();}
			*/
	}

	// initializes connections
	public void initConnections(EngineConfiguration configuration) throws Exception {
		String configPath = configuration.getConfigPath();

		if (!configPath.endsWith("/"))
			configPath += "/";

		connectionsManager = new ConnectionsManager(configPath);
		connectionsManager.initTimeSeriesConnection(configuration);
		AnalysisLogger.getLogger().trace("DataExtender-> connected to Database ");
	}

	// shuts down connections
	public void shutDownConnections() throws Exception {
		connectionsManager.shutdownAll();
	}

	// shuts down all
	public void shutDown() throws Exception {
		connectionsManager.shutdownAll();
		if (bathymetryObj != null)
			Bathymetry.close();
	}

	private static String buildTempTable = "create table  %1$s (%2$s) WITH ( OIDS=FALSE ); ";
	private static String addColumn = "alter table %1$s add %2$s %3$s;";
	private static String dropColumn = "alter table %1$s drop %2$s;";
	private static String getAllCoordinates = "select %1$s,%2$s, %3$s from %4$s;";
	private static String getInfoOrdered = "select %1$s,%2$s, %3$s from %4$s order by %2$s, %3$s;";
	// private static String alterTableNewValue = "UPDATE %1$s SET  %2$s= '%3$s' WHERE %4$s = '%5$s'";
	private static String tempValuesInsert = "INSERT INTO %1$s (%2$s) VALUES %3$s";
	private static String timeseriesValuesUpdate = "UPDATE %1$s SET %2$s=%3$s WHERE %4$s=%5$s";
	private static String tempValuesUpdate = "UPDATE %1$s SET %2$s = %3$s.%2$s FROM %3$s WHERE %1$s.%4$s = %3$s.%4$s";
	private static String tempTableDrop = "drop table %1$s";

	
	
	public List<Tuple<String>> getColumnsAType(DataExtenderFunctionalities functionality) {
		return columnProvider.getColumnInfo(functionality);
	}
	
	// useful for bathymetry, dates, hours calculation and for fishery classifications: simple and bathymetry classes
	public void extendTable(String tableName, List<Tuple<String>> newColumns, String tableKey, String tableKeyType, String firstDimension, String secondDimension, DataExtenderFunctionalities functionality) throws Exception {
		extendTable(tableName, newColumns, tableKey, tableKeyType, firstDimension, secondDimension, functionality, false);
	}

	public void extendTable(String tableName, String tableKey, String tableKeyType, String firstDimension, String secondDimension, DataExtenderFunctionalities functionality) throws Exception {
		List<Tuple<String>> newColumns = columnProvider.getColumnInfo(functionality);
		extendTable(tableName, newColumns, tableKey, tableKeyType, firstDimension, secondDimension, functionality, false);
	}
	
	// for unary relations
	public void extendTable(String tableName, List<Tuple<String>> newColumns, String tableKey, String tableKeyType, String firstDimension, DataExtenderFunctionalities functionality) throws Exception {
		extendTable(tableName, newColumns, tableKey, tableKeyType, firstDimension, null, functionality, false);
	}
	
	public void extendTable(String tableName, String tableKey, String tableKeyType, String firstDimension, DataExtenderFunctionalities functionality) throws Exception {
		List<Tuple<String>> newColumns = columnProvider.getColumnInfo(functionality);
		extendTable(tableName, newColumns, tableKey, tableKeyType, firstDimension, null, functionality, false);
	}
	
	// creates a new column on the table
	private void recreateColumn(String tableName, String columnName, String columnType) {

		AnalysisLogger.getLogger().trace("extendTable->Dropping column " + columnName + " type " + columnType);
		try {
			connectionsManager.TimeSeriesUpdate(String.format(dropColumn, tableName, columnName));
		} catch (Exception e0) {
			AnalysisLogger.getLogger().trace("extendTable->Impossible to drop column");
		}
		AnalysisLogger.getLogger().trace("extendTable->Adding column " + columnName + " type " + columnType);
		try {
			String alterTableQuery = String.format(addColumn, tableName, columnName, columnType);
			connectionsManager.TimeSeriesUpdate(alterTableQuery);
		} catch (Exception e) {
			AnalysisLogger.getLogger().trace("extendTable->Column added yet - or error " + e.getMessage());
		}
	}

	// transforms a map into a string formatted for DB
	private String map2String(HashMap<String, List<String>> map) {
		int numOfElements = map.size();
		StringBuffer allChunkBuffer = new StringBuffer();
		int counter = 0;
		// perform the update of the table - keys are the whole chunk
		for (String key : map.keySet()) {
			List<String> values = map.get(key);
			int valuesize = values.size();
			StringBuffer valuesString = new StringBuffer();

			for (int j = 0; j < valuesize; j++) {
				String value = values.get(j);
				valuesString.append("'" + value + "'");
				if (j < valuesize - 1)
					valuesString.append(",");
			}

			allChunkBuffer.append("(" + key + "," + valuesString + ")");
			if (counter < numOfElements - 1)
				allChunkBuffer.append(",");

			counter++;
		}

		return allChunkBuffer.toString();
	}

	// extends the table with new dimensions
	public void extendTable(String tableName, List<Tuple<String>> newColumns, String tableKey, String tableKeyType, String firstDimension, String secondDimension, DataExtenderFunctionalities functionality, boolean ordered) throws Exception {
		status = 0;
		long t00 = System.currentTimeMillis();
		
		// add a new column
		StringBuffer columnsDefinitions = new StringBuffer();
		StringBuffer columnsNames = new StringBuffer();
		int ncols = newColumns.size();
		int k = 0;

		// cycle on the columns to add
		for (Tuple<String> column : newColumns) {

			String columnName = column.getElements().get(0);
			String columnType = column.getElements().get(1);

			columnsDefinitions.append(columnName + " " + columnType);
			columnsNames.append(columnName);
			if (k < ncols - 1) {
				columnsDefinitions.append(",");
				columnsNames.append(",");
			}
			k++;
		}

		AnalysisLogger.getLogger().trace("extendTable->Column added");

		// create a temporary table
		String tempTableName = ("temp_" + UUID.randomUUID()).replace("-", "_");
		try {
			String tempTableQuery = String.format(buildTempTable, tempTableName, tableKey + " " + tableKeyType + "," + columnsDefinitions);
			connectionsManager.TimeSeriesUpdate(tempTableQuery);
			AnalysisLogger.getLogger().trace("extendTable->Temporary table created " + tempTableQuery);

			// build a temporary table key , newCol1,newCol2 ...
			if (secondDimension == null)
				secondDimension = firstDimension;

			AnalysisLogger.getLogger().trace("extendTable->Getting All Coordinates ... ");

			List<Object> allInfo = null;
			if (ordered)
				// take ordered info
				allInfo = connectionsManager.TimeSeriesQuery(String.format(getInfoOrdered, tableKey, firstDimension, secondDimension, tableName));
			else
				// get all cooordinates
				allInfo = connectionsManager.TimeSeriesQuery(String.format(getAllCoordinates, tableKey, firstDimension, secondDimension, tableName));

			AnalysisLogger.getLogger().trace("extendTable->All Coordinates Got... ");

			int coordinatesNumber = allInfo.size();
			int numberOfChunks = coordinatesNumber / chunkSize;
			if ((coordinatesNumber % chunkSize) != 0) {
				numberOfChunks += 1;
			}

			AnalysisLogger.getLogger().trace("extendTable->Processing");

			for (int i = 0; i < numberOfChunks; i++) {
				// update status
				float s = (float) ((int) (((float) i * 100f / (float) numberOfChunks) * 100f)) / 100f;

				status = (s == 100) ? 99 : s;
				AnalysisLogger.getLogger().trace("extendTable->Status " + status + "%");

				// process a chunk of data
				int startIndex = i * chunkSize;
				int maxIndex = Math.min(startIndex + chunkSize, coordinatesNumber);
				long t0 = System.currentTimeMillis();
				// output format: key->valueforColumn1,valueforColumn2,valueforColumn3...
				HashMap<String, List<String>> map = transformData(allInfo, startIndex, maxIndex, functionality);
				long t1 = System.currentTimeMillis();
				AnalysisLogger.getLogger().trace("extendTable->Updating Time Series with the results for chunk " + i + " elapsed " + (t1 - t0) + "ms");
				// transform map to a string for insertion into DB
				String allChunkBuffer = map2String(map);
				AnalysisLogger.getLogger().trace("extendTable->Inserting Chunk " + i);

				// chunk insertion
				String insertionQuery = String.format(tempValuesInsert, tempTableName, tableKey + "," + columnsNames, allChunkBuffer);
//				AnalysisLogger.getLogger().trace("extendTable->" + insertionQuery);
				AnalysisLogger.getLogger().trace("extendTable->");
				connectionsManager.TimeSeriesUpdate(insertionQuery);
			}

			// update table
			AnalysisLogger.getLogger().trace("extendTable->Updating Original Table");

			for (Tuple<String> column : newColumns) {
				String columnName = column.getElements().get(0);
				String columnType = column.getElements().get(1);
				// create new column
				recreateColumn(tableName, columnName, columnType);
				// update original table
				AnalysisLogger.getLogger().trace(tableName + "," + column.getElements().get(0) + "," + tempTableName + "," + tableKey);
				String originalTableUpdateQuery = String.format(tempValuesUpdate, tableName, column.getElements().get(0), tempTableName, tableKey);

				connectionsManager.TimeSeriesUpdate(originalTableUpdateQuery);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// deleting temp table
			AnalysisLogger.getLogger().trace("extendTable->Deleting Temporary Table");
			try{
				connectionsManager.TimeSeriesUpdate(String.format(tempTableDrop, tempTableName));
			}catch(Exception e1){AnalysisLogger.getLogger().trace("extendTable->Could not drop Temporary Table");}
			status = 100;
			long t01 = System.currentTimeMillis();
			AnalysisLogger.getLogger().trace("extendTable->Process Finished in "+((t01-t00)/60000)+"min");
		}
		
	}

	String[] currentKeys;
	Double[] currentPoints;
	String[] currentStrings;
	Timestamp[] currentDates;
	Tuple<String>[] currentCouples;

	// build up parallel lists of 2D points and keys
	private void extractPoints(List<Object> coordinates, int startIndex, int endIndex) {
		int size = endIndex - startIndex;
		currentKeys = new String[size];
		currentPoints = new Double[size];
		int k = 0;
		for (int i = startIndex; i < endIndex; i++) {
			Object[] record = (Object[]) coordinates.get(i);
			String key = "" + record[0];
			String x = "" + record[1];
			String y = "" + record[2];
			currentKeys[k] = key;
			currentPoints[k] = new Double(java.lang.Double.parseDouble(x), java.lang.Double.parseDouble(y));

			k++;
		}
	}

	// build up parallel lists strings and dates
	private void extractDatesStrings(List<Object> coordinates, int startIndex, int endIndex) {
		int size = endIndex - startIndex;
		currentKeys = new String[size];
		currentStrings = new String[size];
		currentDates = new Timestamp[size];
		int k = 0;
		for (int i = startIndex; i < endIndex; i++) {
			Object[] record = (Object[]) coordinates.get(i);
			String key = "" + record[0];
			String f = "" + record[1];

			currentKeys[k] = key;
			currentStrings[k] = f;
			currentDates[k] = (Timestamp) record[2];

			k++;
		}
	}

	// build up parallel lists of key and strings
	private void extractStrings(List<Object> coordinates, int startIndex, int endIndex) {
		int size = endIndex - startIndex;
		currentKeys = new String[size];
		currentStrings = new String[size];
		int k = 0;
		for (int i = startIndex; i < endIndex; i++) {
			Object[] record = (Object[]) coordinates.get(i);
			String key = "" + record[0];
			String f = "" + record[1];

			currentKeys[k] = key;
			currentStrings[k] = f;

			k++;
		}
	}

	// build up parallel lists of keys and couples of coordinates
	private void extractStringCouples(List<Object> coordinates, int startIndex, int endIndex) {
		int size = endIndex - startIndex;
		currentKeys = new String[size];
		currentCouples = new Tuple[size];
		int k = 0;
		for (int i = startIndex; i < endIndex; i++) {
			Object[] record = (Object[]) coordinates.get(i);
			String key = "" + record[0];
			String e1 = "" + record[1];
			String e2 = "" + record[2];
			currentKeys[k] = key;
			currentCouples[k] = new Tuple<String>(e1, e2);

			k++;
		}
	}

	private HashMap<String, List<String>> transformData(List<Object> coordinates, int startIndex, int endIndex, DataExtenderFunctionalities functionality) throws Exception {

		HashMap<String, List<String>> coordinatesMap = new HashMap<String, List<String>>();
		AnalysisLogger.getLogger().trace("\ttransformData->Analysis of the function " + functionality + " indexes " + startIndex + " - " + endIndex);

		// treatment of the functionalities - the results will be a map of keys -> list of values
		switch (functionality) {
		case bathymetry:
			extractPoints(coordinates, startIndex, endIndex);
			bathymetryObj = new Bathymetry(bathymetryFile);
			short[] bathymetries = bathymetryObj.compute(currentPoints);
			int i = 0;
			for (short bath : bathymetries) {
				List<String> baths = new ArrayList<String>();
				baths.add("" + bath);
				coordinatesMap.put(currentKeys[i], baths);
				i++;
			}
			break;
		case fao_areas:
			extractPoints(coordinates, startIndex, endIndex);
			FAOAreaExtractor faoareaextractor = new FAOAreaExtractor(geoserverURL, connectionsManager);
			Tuple<String>[] faotuples = faoareaextractor.getAreas(currentPoints);
			i = 0;
			for (Tuple<String> singleSet : faotuples) {
				List<String> singleList = singleSet.getElements();
				coordinatesMap.put(currentKeys[i], singleList);
				i++;
			}
			break;
		case sst:
			extractPoints(coordinates, startIndex, endIndex);
			SurfaceTemperatureExtractor sstObj = new SurfaceTemperatureExtractor();
			float[] temps = sstObj.getSST(currentPoints);
			i = 0;
			for (float temp : temps) {
				List<String> tempList = new ArrayList<String>();
				tempList.add("" + temp);
				coordinatesMap.put(currentKeys[i], tempList);
				i++;
			}
			break;
		case vti_dates:
			extractStrings(coordinates, startIndex, endIndex);
			for (int j = 0; j < currentStrings.length; j++) {
				List<String> tempvtidatesList = new ArrayList<String>();
				tempvtidatesList.add(VTIDateFormatConverter.convert2VTIFormat(currentStrings[j]));
				coordinatesMap.put(currentKeys[j], tempvtidatesList);
			}
			break;
		case fishing_hours:
			extractDatesStrings(coordinates, startIndex, endIndex);
			double[] hours = FishingHoursCalculator.calculateFishingHours(currentStrings, currentDates);
			for (int j = 0; j < currentStrings.length; j++) {
				List<String> tempvtidatesList = new ArrayList<String>();
				tempvtidatesList.add("" + hours[j]);
				coordinatesMap.put(currentKeys[j], tempvtidatesList);
			}
			break;
		case classify:
			extractStringCouples(coordinates, startIndex, endIndex);
			Tuple<Integer>[] classifications = VTIClassificator.classify(currentCouples);
			for (int j = 0; j < currentCouples.length; j++) {
				List<String> tempvticlassificationList = new ArrayList<String>();
				tempvticlassificationList.add("" + classifications[j].getElements().get(0));
				tempvticlassificationList.add("" + classifications[j].getElements().get(1));
				coordinatesMap.put(currentKeys[j], tempvticlassificationList);
			}
		default:
			break;
		}

		return coordinatesMap;
	}

	public double getStatus() {
		return status;
	}

	public void setStatus(float status) {
		this.status = status;
	}

	public void setGeoserverURL(String url) {
		this.geoserverURL = url;
	}

	public String setGeoserverURL() {
		return this.geoserverURL;
	}

}

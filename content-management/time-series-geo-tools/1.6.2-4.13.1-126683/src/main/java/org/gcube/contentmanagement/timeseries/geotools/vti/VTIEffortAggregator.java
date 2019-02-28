package org.gcube.contentmanagement.timeseries.geotools.vti;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISGroupInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerSaver;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation;
import org.gcube.contentmanagement.timeseries.geotools.representations.GISLayer;
import org.gcube.contentmanagement.timeseries.geotools.tools.VTICache;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;
import org.gcube.contentmanagement.timeseries.geotools.vti.connectors.MonthlyFishingEffortCalculator;

public class VTIEffortAggregator {

	ConnectionsManager connectionsManager;

//	private String groupName;
	private static int maxTries = 3;
	private float status = 0;
	private boolean cached;
	public static String persistenceFile = "vtiCache.dat";
	private String persistencePath;

	private static String getMinMaxQuery = "select min(%1$s), max(%1$s) from %2$s";
	private static String getAllInfo = "select %1$s from %2$s;";
	private static String tempTableDrop = "drop table %1$s";
	private static String buildTempTable = "create table  %1$s (%2$s) WITH ( OIDS=FALSE ); ";
	private static String tempValuesInsert = "INSERT INTO %1$s (%2$s) VALUES %3$s";
	private static String rawInsert = "INSERT INTO %1$s (%2$s) VALUES %3$s";
	private static String rawSelect = "select %1$s from %2$s order by %3$s limit %4$s offset %5$s";
	private static String referenceValue = "monthlyfishinghours";
	private static String addGeometriesColumn = "alter table %1$s add the_geom geometry;";
	private static String getTableStructure = "SELECT column_name,data_type FROM information_schema.COLUMNS WHERE table_name ='%1$s'";
	private static String createPointsGeometriesColumn = "Select AddGeometryColumn('%1$s','the_geom',4326,'POINT',2)";

	public static String MONTHPREFIX = "m";
	public static String VTIPREFIX = "v";

	public static String MONTHLAYERTITLE = "Fishing_Monthly_Effort";
	public static String VTILAYERTITLE = "Vessel Routes";

	public VTIEffortAggregator(TSGeoToolsConfiguration configuration) throws Exception {
		try {
			String configPath = configuration.getConfigPath();

			if (!configPath.endsWith("/"))
				configPath += "/";

			// persistence file setup
			if (configuration.getPersistencePath() != null)
				persistencePath = configuration.getPersistencePath();
			else
				persistencePath = configPath;

			if (!persistencePath.endsWith("/"))
				persistencePath += "/";

			persistencePath += persistenceFile;

			AnalysisLogger.setLogger(configPath + "ALog.properties");
			AnalysisLogger.getLogger().trace("EffortAggregator-> initializing connections");

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
		} catch (Exception e) {
			connectionsManager.shutdownAll();
			throw e;
		}
	}

	public List<String> produceMontlyEffort(String tableName, String outputTableName, String tableKey, String xDimension, String yDimension, String datesDimension, String fishingHoursDimension, String speedDimension) throws Exception {
		return produceMontlyEffort(tableName, tableKey, xDimension, yDimension, datesDimension, fishingHoursDimension, speedDimension, null, false, false, null);
	}

	public List<String> produceMontlyEffort(String tableName, String tableKey, String xDimension, String yDimension, String datesDimension, String fishingHoursDimension, String speedDimension, GISInformation gisInfo, boolean produceVTI, boolean appendMode) throws Exception {
		return produceMontlyEffort(tableName, tableKey, xDimension, yDimension, datesDimension, fishingHoursDimension, speedDimension, gisInfo, produceVTI, appendMode, null);
	}

	public float getStatus() {

		return status;
	}

	/*
	public String getGroupName() {
		return groupName;
	}
*/
	
	private String getTSIdentifier(String timeseriesName, String xDimension, String yDimension, String datesDimension, String fishingHoursDimension, String speedDimension) {
		return timeseriesName + ":" + xDimension + ":" + yDimension + ":" + datesDimension + ":" + fishingHoursDimension + ":" + speedDimension;
	}

	public List<String> checkGroupingInCache(GISInformation gisInfo, String timeseriesName, String xDimension, String yDimension, String datesDimension, String fishingHoursDimension, String speedDimension) {
		
		cached = false;
		timeseriesName = ConnectionsManager.getTableName(timeseriesName);
		List<String> cachedLayers = null;
		
		long t000 = System.currentTimeMillis();
		try {
			VTICache cache = VTICache.getInstance(persistencePath);
			long t001 = System.currentTimeMillis();
			AnalysisLogger.getLogger().trace("vtiGIS->Cache Loaded in " + (t001 - t000) + "ms");
			Tuple<String> layers = cache.getCachedElement(getTSIdentifier(timeseriesName, xDimension, yDimension, datesDimension, fishingHoursDimension, speedDimension));
			AnalysisLogger.getLogger().trace("vtiGIS->Time Series: " + timeseriesName);
			if (layers != null) {
				// check if the group still exists
				if (GISGroupInformation.checkLayers(gisInfo, layers.getElements(), 1)) {
					cached = true;
					AnalysisLogger.getLogger().warn("vtiGIS->the Time Series was present in cache!");
					AnalysisLogger.getLogger().warn("vtiGIS->Found Grouping in cache");
					cachedLayers=layers.getElements();
				} else {
					AnalysisLogger.getLogger().trace("vtiGIS->Group doesn't really exist...Removing from Cache");
					cache.removeCachedElement(getTSIdentifier(timeseriesName, xDimension, yDimension, datesDimension, fishingHoursDimension, speedDimension));
				}
			} else
				AnalysisLogger.getLogger().trace("vtiGIS->Time Series: " + timeseriesName + " was not present in cache");
		} catch (Exception e) {
			AnalysisLogger.getLogger().warn("vtiGIS->ERROR in checking group " + e.getMessage());
		}
		
		return cachedLayers;
	}

	public List<String> produceMontlyEffort(String tableName, String tableKey, String xDimension, String yDimension, String datesDimension, String fishingHoursDimension, String speedDimension, GISInformation gisInfo, boolean produceVTI, boolean appendMode, HashMap<String, String> field2user) throws Exception {
		long t0 = System.currentTimeMillis();
		List<String> createdLayers = null;
		
		GISInformation localGISInfo = new GISInformation();

		localGISInfo.setGeoNetworkUrl(gisInfo.getGeoNetworkUrl());
		localGISInfo.setGeoNetworkUserName(gisInfo.getGeoNetworkUserName());
		localGISInfo.setGeoNetworkPwd(gisInfo.getGeoNetworkPwd());

		localGISInfo.setGisDataStore(gisInfo.getGisDataStore());
		localGISInfo.setGisPwd(gisInfo.getGisPwd());
		localGISInfo.setGisUrl(gisInfo.getGisUrl());
		localGISInfo.setGisUserName(gisInfo.getGisUserName());
		localGISInfo.setGisWorkspace(gisInfo.getGisWorkspace());
		AnalysisLogger.getLogger().trace("produceMontlyEffort->GIS INFORMATION " + gisInfo.getGisUserName() + "," + gisInfo.getGisWorkspace() + "," + gisInfo.getGisDataStore() + "," + gisInfo.getGisUrl() + "," + gisInfo.getGisPwd());

		tableName = ConnectionsManager.getTableName(tableName);
		GISLayerSaver gislayersav = null;

		createdLayers = checkGroupingInCache(gisInfo, tableName, xDimension, yDimension, datesDimension, fishingHoursDimension, speedDimension);
		
		try {
			status = 0;
			if (!cached) {
				createdLayers = new ArrayList<String>();
				AnalysisLogger.getLogger().trace("produceMontlyEffort->Calculation Started on " + tableName);
				AnalysisLogger.getLogger().trace("produceMontlyEffort->Parameters: " + tableName + "," + tableKey + "," + xDimension + "," + yDimension + "," + datesDimension + "," + fishingHoursDimension + "," + speedDimension + "," + produceVTI + "," + appendMode);
				String outputTableName = MONTHPREFIX + tableName;
				// get max and min dates
				AnalysisLogger.getLogger().trace("produceMontlyEffort->Getting max and min dates " + String.format(getMinMaxQuery, datesDimension, tableName));
				Object[] minmax = (Object[]) (connectionsManager.TimeSeriesQuery(String.format(getMinMaxQuery, datesDimension, tableName))).get(0);
				Date minDate = (Date) minmax[0];
				Date maxDate = (Date) minmax[1];
				AnalysisLogger.getLogger().trace("produceMontlyEffort->Min date: " + minDate + " Max date: " + maxDate);
				// get all necessary info
				AnalysisLogger.getLogger().trace("produceMontlyEffort->Getting all table info " + String.format(getAllInfo, (yDimension + "," + xDimension + "," + datesDimension + "," + fishingHoursDimension + "," + speedDimension), tableName));
				List<Object> allInfo = connectionsManager.TimeSeriesQuery(String.format(getAllInfo, (yDimension + "," + xDimension + "," + datesDimension + "," + fishingHoursDimension + "," + speedDimension), tableName));
				status = 25f;
				AnalysisLogger.getLogger().trace("produceMontlyEffort->Calculation monthly effort - status " + status);
				// calculate monthly effort
				MonthlyFishingEffortCalculator mfcalc = new MonthlyFishingEffortCalculator();
				Map<String, java.lang.Double> mfe = mfcalc.calculateMonthlyFishingEffor(allInfo, minDate, maxDate);
				AnalysisLogger.getLogger().trace("produceMontlyEffort->Dropping output table - status " + status);
				// drop outputTable if exists
				try {
					connectionsManager.GeoserverUpdate(String.format(tempTableDrop, outputTableName));
				} catch (Exception e) {
					AnalysisLogger.getLogger().trace("produceMontlyEffort->Impossible to drop table " + tableName);
				}

				AnalysisLogger.getLogger().trace("produceMontlyEffort->Creating new table");
				// create outputTable if exists
				connectionsManager.GeoserverUpdate(String.format(buildTempTable, outputTableName, "csquarecode character varying, " + referenceValue + " real"));
				AnalysisLogger.getLogger().trace("produceMontlyEffort->Saving elements");
				// save effort table: build a buffer containing the entries
				int mflen = mfe.size();
				StringBuffer insertionQuery = new StringBuffer();
				int i = 0;
				for (String key : mfe.keySet()) {
					insertionQuery.append("('" + key + "','" + mfe.get(key) + "')");

					if (i < mflen - 1)
						insertionQuery.append(",");

					i++;
				}
				// write the buffer into the DB
				connectionsManager.GeoserverUpdate(String.format(tempValuesInsert, outputTableName, "csquarecode, " + referenceValue, insertionQuery.toString()));
				status = 50f;
				AnalysisLogger.getLogger().trace("produceMontlyEffort->status " + status);
				if (gisInfo != null) {
					// creating GIS group on GeoServer
					AnalysisLogger.getLogger().trace("produceMontlyEffort->Adding geometries to table " + outputTableName);
					connectionsManager.GeoserverUpdate(String.format(addGeometriesColumn, outputTableName));
					connectionsManager.GeoserverUpdate(GISOperations.createFulfilGeometriesStatement(outputTableName));

					AnalysisLogger.getLogger().trace("produceMontlyEffort->Creating new GIS layers set ");
					gislayersav = new GISLayerSaver(connectionsManager);
					// create a GIS Layer on GeoServer
					GISLayer fishingEffortGISLayer = new GISLayer(outputTableName);
					fishingEffortGISLayer.setLayerTitle(MONTHLAYERTITLE);
					fishingEffortGISLayer.setValuesColumnName(referenceValue);
					if (mfcalc.getMinEffort() == mfcalc.getMaxEffort())
						mfcalc.setMaxEffort(mfcalc.getMaxEffort() + 1);

					fishingEffortGISLayer.setMin(mfcalc.getMinEffort());
					fishingEffortGISLayer.setMax(mfcalc.getMaxEffort());
					fishingEffortGISLayer.forceNotEmpty();
					fishingEffortGISLayer.setPreferredStyleName("montly_fishing_hours_" + UUID.randomUUID());
					ArrayList<GISLayer> singletonlayers = new ArrayList<GISLayer>();
					singletonlayers.add(fishingEffortGISLayer);
					
					// call VTI layers generator only if necessary and required by outside
					if (produceVTI)
						addVTILayers(tableName, tableKey, xDimension, yDimension, null, localGISInfo, appendMode, field2user);

					status = 75f;
					AnalysisLogger.getLogger().trace("produceMontlyEffort->status " + status);

					// create a GIS group for the the Layer and possibly the VTI
					String groupingName = gislayersav.createGISLayers(singletonlayers, localGISInfo, null, true, true);
					if (groupingName == null)
						throw new Exception("Impossible to create layers on Geoserver");
					else{
						//create layer names list to be returned
						for (GISLayerInformation layer : localGISInfo.getLayers()){
							AnalysisLogger.getLogger().trace("produceMontlyEffort->Adding Layer: "+layer.getLayerName());
							createdLayers.add(layer.getLayerName());
						}
					}
					AnalysisLogger.getLogger().trace("produceMontlyEffort->GIS Layers created!");
				}
			}
			AnalysisLogger.getLogger().trace("produceMontlyEffort->Calculation Finished");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			localGISInfo.clean();
			connectionsManager.shutdownAll();
			// check if geoserver group has been really generated: http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/rest/layergroups/group4c8cbbdfb-7e33-4ae0-9db4-7916d38e906d.json
			if ((createdLayers!=null) && (createdLayers.size()>0)){ 
			boolean urlcoherence = GISGroupInformation.checkLayers(gisInfo, createdLayers, maxTries);

			if (!urlcoherence) {
				AnalysisLogger.getLogger().trace("...Removing from Cache");
				VTICache cache = VTICache.getInstance(persistencePath);
				cache.removeCachedElement(getTSIdentifier(tableName,xDimension,yDimension,datesDimension,fishingHoursDimension,speedDimension));
				throw new Exception("Error in GIS Group Generation - GIS Group was not really created");
			}
			// insert into the cache
			if (!cached) {
				AnalysisLogger.getLogger().trace("...Caching");
				VTICache cache = VTICache.getInstance(persistencePath);
				String[] t = new String [createdLayers.size()];
				createdLayers.toArray(t);
				if ((createdLayers!=null) && (createdLayers.size()>0))
					cache.addCacheElement(getTSIdentifier(tableName,xDimension,yDimension,datesDimension,fishingHoursDimension,speedDimension), t);
			}
			// end insertion in the cache
			}
			status = 100f;
			long t2 = System.currentTimeMillis();
			AnalysisLogger.getLogger().warn("Computation Finished - Elapsed Time: " + (t2 - t0) + " ms");
			AnalysisLogger.getLogger().trace("ProduceMonthlyEffort->status: " + status);
		}
		return createdLayers;
	}

	private String getFinalName(String name, HashMap<String, String> field2user) {
		String finalName = name;
		if (field2user != null) {
			finalName = field2user.get(name);
			if (finalName == null) {
				AnalysisLogger.getLogger().trace("getFinalName->WARNING- unable to transform name " + name);
				finalName = name;
			}
		}
		return finalName;
	}

	public void addVTILayers(String tableName, String tableKey, String xDimension, String yDimension, String previousGroupName, GISInformation gisInfo, boolean appendMode, HashMap<String, String> field2user) throws Exception {

		String outputTableName = VTIPREFIX + tableName;

		// perform create table operation only if we are not in append mode

		AnalysisLogger.getLogger().trace("addVTILayers->dropping table if exists " + outputTableName);
		// drop outputTable if exists
		try {
			connectionsManager.GeoserverUpdate(String.format(tempTableDrop, outputTableName));
		} catch (Exception e) {
			AnalysisLogger.getLogger().trace("addVTILayers->Impossible to drop table " + outputTableName);
		}

		AnalysisLogger.getLogger().trace("addVTILayers->getting table structure for " + outputTableName);
		// select info from table
		List<Object> tableStructure = connectionsManager.TimeSeriesQuery(String.format(getTableStructure, tableName));
		StringBuffer tabledimensions = new StringBuffer();
		int tsize = tableStructure.size();
		ArrayList<String> dimensions = new ArrayList<String>();
		ArrayList<String> userfriendlydimensions = new ArrayList<String>();
		for (int j = 0; j < tsize; j++) {
			Object[] row = (Object[]) tableStructure.get(j);
			String dimName = "" + row[0];
			String type = "" + row[1];
			// substitute the right, final, name
			tabledimensions.append(getFinalName(dimName, field2user) + " " + type);
			userfriendlydimensions.add(getFinalName(dimName, field2user));
			dimensions.add(dimName);
			if (j < tsize - 1)
				tabledimensions.append(",");
		}

		// order dimensions for further queries - x and y are put at the first and second position
		String dimensionsList = orderDimensions(dimensions, xDimension, yDimension);
		String userdimensionsList = orderDimensions(userfriendlydimensions, getFinalName(xDimension, field2user), getFinalName(yDimension, field2user));

		if (!appendMode) {
			AnalysisLogger.getLogger().trace("addVTILayers->creating table structure for " + outputTableName);
			AnalysisLogger.getLogger().trace("addVTILayers->building " + String.format(buildTempTable, outputTableName, tabledimensions.toString()));
			connectionsManager.GeoserverUpdate(String.format(buildTempTable, outputTableName, tabledimensions.toString()));

			// add geometries column
			AnalysisLogger.getLogger().trace("addVTILayers->adding geometries column");
			try {
				String addGeometriesTable = String.format(createPointsGeometriesColumn, outputTableName);
				connectionsManager.GeoserverQuery(addGeometriesTable);
			} catch (Exception e2) {
				AnalysisLogger.getLogger().trace("column was yet created");
				e2.printStackTrace();
			}
		}

		// put info on GeoServer DB
		AnalysisLogger.getLogger().trace("produceMontlyEffort->getting table elements from " + outputTableName);

		// perform this operation in chunks
		int chunkSize = 100000;
		// insert elements in chunks
		int startIndex = 0;

		List<Object> rows = connectionsManager.TimeSeriesQuery(String.format(rawSelect, dimensionsList, tableName, tableKey, "" + chunkSize, "" + startIndex));

		// take rows and build a buffer for insertion
		while (rows != null) {
			// build up insert statement
			int rowsNumber = rows.size();
			int i = 0;
			StringBuffer allInfo = new StringBuffer();

			for (Object row : rows) {
				Object[] elements = (Object[]) row;
				allInfo.append("(");
				String x = "";
				String y = "";
				for (int j = 0; j < elements.length; j++) {
					allInfo.append("'" + elements[j] + "',");
					if (j == 0)
						x = "" + elements[j];
					else if (j == 1)
						y = "" + elements[j];
				}

				// add geometry
				allInfo.append("ST_SetSRID(ST_MakePoint(" + x + "," + y + "),4326)");

				allInfo.append(")");

				if (i < rowsNumber - 1)
					allInfo.append(",");
				i++;
			}

			// write the insertion statement on DB
			startIndex = startIndex + chunkSize;
			AnalysisLogger.getLogger().trace("produceMontlyEffort->putting table elements into " + outputTableName);

			AnalysisLogger.getLogger().trace("produceMontlyEffort->insertion details " + String.format(rawInsert, outputTableName, userdimensionsList + ",the_geom", ""));

			connectionsManager.GeoserverUpdate(String.format(rawInsert, outputTableName, userdimensionsList + ",the_geom", allInfo.toString()));

			// proceed with the next chunk
			rows = connectionsManager.TimeSeriesQuery(String.format(rawSelect, dimensionsList, tableName, tableKey, "" + chunkSize, "" + startIndex));
		}

		// transform all points into geometries
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO point_geometries_example(gid, the_geom, type) VALUES");

		// add layers for the VTI
		AnalysisLogger.getLogger().trace("produceMontlyEffort->adding VTI layers");
		// create layer for VTI
		GISStyleInformation style = new GISStyleInformation();
		style.setStyleName("VTISimpleClassification");
		GISStyleInformation style2 = new GISStyleInformation();
		style2.setStyleName("VTIBathymetryClassification");

		GISLayerInformation gisLayer1 = new GISLayerInformation();
		gisLayer1.setDefaultStyle(style.getStyleName());
		gisLayer1.setLayerName(outputTableName);
		gisLayer1.setLayerTitle(VTILAYERTITLE);

		// add layer to the previously generated group
		gisInfo.addLayer(gisLayer1);
		gisInfo.addStyle(gisLayer1.getLayerName(), style2);
		gisInfo.addStyle(gisLayer1.getLayerName(), style);
		AnalysisLogger.getLogger().trace("produceMontlyEffort->adding VTI layers - done!");

	}

	private String orderDimensions(List<String> dimensions, String xDimension, String yDimension) {

		// order the dimensions list for futher queries

		dimensions.remove(xDimension);
		dimensions.remove(yDimension);
		dimensions.add(0, xDimension);
		dimensions.add(1, yDimension);

		StringBuffer dimensionsBuffer = new StringBuffer();
		int k = 0;
		int dimSize = dimensions.size();
		for (String dim : dimensions) {
			dimensionsBuffer.append(dim);
			if (k < dimSize - 1)
				dimensionsBuffer.append(",");
			k++;
		}

		return dimensionsBuffer.toString();

	}

}

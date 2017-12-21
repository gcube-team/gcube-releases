package org.gcube.contentmanagement.timeseries.geotools.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISGroupInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation;
import org.gcube.contentmanagement.timeseries.geotools.utils.OccurrencePointVector2D;

public class PointsMapCreator {

	private static String tempTableDrop = "drop table %1$s";
	private static String buildTempTable = "create table  %1$s (pointid serial);";
	private static String createPointsGeometriesColumn = "Select AddGeometryColumn('%1$s','the_geom',4326,'POINT',2)";
	private static String addColumnStatement = "ALTER TABLE %1$s ADD %2$s %3$s;";
	private static String rawSelect = "select %1$s from %2$s limit %3$s offset %4$s";
	private static String rawInsert = "INSERT INTO %1$s VALUES %2$s";

	public static void main(String[] args) {

	}

	private String persistencePath;
	ConnectionsManager connectionsManager;

	public PointsMapCreator(TSGeoToolsConfiguration configuration) throws Exception {
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

			AnalysisLogger.setLogger(configPath + "ALog.properties");
			AnalysisLogger.getLogger().trace("Create Points Map-> initializing connections");

			connectionsManager = new ConnectionsManager(configPath);

			EngineConfiguration tscfg = null;
			EngineConfiguration geocfg = null;

			if (configuration.getTimeSeriesDatabase() != null) {
				tscfg = new EngineConfiguration();
				tscfg.setConfigPath(configPath);
				tscfg.setDatabaseUserName(configuration.getTimeSeriesUserName());
				tscfg.setDatabasePassword(configuration.getTimeSeriesPassword());
				tscfg.setDatabaseURL(configuration.getTimeSeriesDatabase());
				AnalysisLogger.getLogger().trace("Create Points Map-> connected to Time Series");
				connectionsManager.initTimeSeriesConnection(tscfg);
			}
			if (configuration.getGeoServerDatabase() != null) {
				geocfg = new EngineConfiguration();
				geocfg.setConfigPath(configPath);
				geocfg.setDatabaseUserName(configuration.getGeoServerUserName());
				geocfg.setDatabasePassword(configuration.getGeoServerPassword());
				geocfg.setDatabaseURL(configuration.getGeoServerDatabase());
				AnalysisLogger.getLogger().trace("Create Points Map-> connected to Geo Server");
				connectionsManager.initGeoserverConnection(geocfg);
			}

		} catch (Exception e) {
			connectionsManager.shutdownAll();
			throw e;
		}
	}

	public GISInformation duplicateGISInformation(GISInformation gisInfo) {

		GISInformation localGISInfo = new GISInformation();
		localGISInfo.setGeoNetworkUrl(gisInfo.getGeoNetworkUrl());
		localGISInfo.setGeoNetworkUserName(gisInfo.getGeoNetworkUserName());
		localGISInfo.setGeoNetworkPwd(gisInfo.getGeoNetworkPwd());

		localGISInfo.setGisDataStore(gisInfo.getGisDataStore());
		localGISInfo.setGisPwd(gisInfo.getGisPwd());
		localGISInfo.setGisUrl(gisInfo.getGisUrl());
		localGISInfo.setGisUserName(gisInfo.getGisUserName());
		localGISInfo.setGisWorkspace(gisInfo.getGisWorkspace());
		return localGISInfo;
	}

	public String generateXYMapFromPoints(List<OccurrencePointVector2D> xyPoints, String destinationTable, boolean createMap, String mapName, GISInformation gisInfo) throws Exception {
		// copy a local version of the GIS information
		GISInformation localGISInfo = duplicateGISInformation(gisInfo);

		int numberOfPoints = xyPoints.size();
		StringBuffer allInfo = new StringBuffer();
		// take rows and build a buffer for insertion
		for (int k = 0; k < numberOfPoints; k++) {
			allInfo.append("(");
			OccurrencePointVector2D row = xyPoints.get(k);
			
			try{
				double xd = row.getX();
				double yd = row.getY();
//				AnalysisLogger.getLogger().trace("---" + xd + "," + yd + "---");
				// add geometry
				allInfo.append((k+1)+",ST_SetSRID(ST_MakePoint(" + xd + "," + yd + "),4326)");
				
				Map<String,String> metadata = row.getmetadata();

				for (String metadataColumn:metadata.keySet()){
					allInfo.append(",");
					if (metadataColumn==null)
						metadataColumn="";
					else if (metadataColumn.length()>250)
						metadataColumn = metadataColumn.substring(0, 250);
					
					String elToInsert = new String(metadata.get(metadataColumn).getBytes(),"UTF-8").replace("'", "");
					elToInsert = elToInsert.replace("{", "").replace("}", "");
					
					allInfo.append("'"+elToInsert+"'");
				}

				
				allInfo.append(")");

				if (k < numberOfPoints - 1)
					allInfo.append(",");
				
			}catch(Exception e){
				
				AnalysisLogger.getLogger().info("generateXYMap->ERROR IN POINTS "+e.getLocalizedMessage());
			}
			
		}

		// write the insertion statement on DB
		AnalysisLogger.getLogger().trace("generateXYMap->putting table elements into " + destinationTable);
		AnalysisLogger.getLogger().trace("generateXYMap->query to execute: " + String.format(rawInsert, destinationTable, allInfo.toString()));
		connectionsManager.GeoserverUpdate(String.format(rawInsert, destinationTable, allInfo.toString()));

		if (createMap)
			return generatePointGisMap(destinationTable, mapName, localGISInfo);
		else
			return null;
	}
	
	
	public String createMapFromPoints(List<OccurrencePointVector2D> xyPoints, String destinationTableName, String mapName, GISInformation gisInfo) throws Exception {
		try {
			// create the table
			if ((xyPoints!=null) && (xyPoints.size()>0)){
				createTable(destinationTableName,xyPoints.get(0));
				return generateXYMapFromPoints(xyPoints, destinationTableName, true, mapName, gisInfo);
			}
			else 
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().trace("Error: " + e.getLocalizedMessage());
			throw e;
		} finally {
			connectionsManager.shutdownAll();
		}

	}
	
	
	private void createTable(String destinationTableName, OccurrencePointVector2D row) {

		// drop outputTable if exists
		AnalysisLogger.getLogger().trace("createMap->dropping table if exists " + destinationTableName);
		try {
			connectionsManager.GeoserverUpdate(String.format(tempTableDrop, destinationTableName));
		} catch (Exception e1) {
			AnalysisLogger.getLogger().trace("createMap->Impossible to drop table " + destinationTableName);
		}

		try {
			AnalysisLogger.getLogger().trace("createMap->Creating new table");
			connectionsManager.GeoserverUpdate(String.format(buildTempTable, destinationTableName));
			// add geometries column
			connectionsManager.GeoserverQuery(String.format(createPointsGeometriesColumn, destinationTableName));
			AnalysisLogger.getLogger().trace("createMap->Adding Columns");
			Map<String,String> metadata = row.getmetadata();
			for (String metadataColumn:metadata.keySet()){
				AnalysisLogger.getLogger().trace("createMap->Adding Column "+metadataColumn);
				AnalysisLogger.getLogger().trace("createMap->Statement "+String.format(addColumnStatement, destinationTableName,metadataColumn,"character varying"));
				connectionsManager.GeoserverUpdate(String.format(addColumnStatement, destinationTableName,metadataColumn,"character varying"));
			}

			AnalysisLogger.getLogger().trace("createMap->Creating new table - OK");
		} catch (Exception e2) {
			AnalysisLogger.getLogger().trace("createMap->Impossible create table " + destinationTableName);
		}
	}
	
	
	private void createTable(String destinationTableName) {

		// drop outputTable if exists
		AnalysisLogger.getLogger().trace("createMap->dropping table if exists " + destinationTableName);
		try {
			connectionsManager.GeoserverUpdate(String.format(tempTableDrop, destinationTableName));
		} catch (Exception e1) {
			AnalysisLogger.getLogger().trace("createMap->Impossible to drop table " + destinationTableName);
		}

		try {
			AnalysisLogger.getLogger().trace("createMap->Creating new table");
			connectionsManager.GeoserverUpdate(String.format(buildTempTable, destinationTableName));
			// add geometries column
			connectionsManager.GeoserverQuery(String.format(createPointsGeometriesColumn, destinationTableName));
			AnalysisLogger.getLogger().trace("createMap->Creating new table - OK");
		} catch (Exception e2) {
			AnalysisLogger.getLogger().trace("createMap->Impossible create table " + destinationTableName);
		}
	}

	public String createMap(String originTableName, String destinationTableName, String xDimension, String yDimension, String mapName, GISInformation gisInfo) throws Exception {

		try {
			// create the table
			createTable(destinationTableName);
			// take all x,y and write then get the group name
			return generateXYMap(originTableName, destinationTableName, xDimension, yDimension, true, mapName, gisInfo);
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().trace("Error: " + e.getLocalizedMessage());
			throw e;
		} finally {
			connectionsManager.shutdownAll();
		}

	}

	public String generateXYMap(String originTable, String destinationTable, String xDimension, String yDimension, boolean createMap, String mapName, GISInformation gisInfo) throws Exception {
		// copy a local version of the GIS information
		GISInformation localGISInfo = duplicateGISInformation(gisInfo);

		// perform this operation in chunks
		int chunkSize = 100000;
		// insert elements in chunks
		int startIndex = 0;
		// take all the x y points
		List<Object> rows = connectionsManager.TimeSeriesQuery(String.format(rawSelect, xDimension + "," + yDimension, originTable, "" + chunkSize, "" + startIndex));

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
					String elToInsert =  ""+elements[j];
					elToInsert="'" + elToInsert + "',";
					allInfo.append(elToInsert);
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
			AnalysisLogger.getLogger().info("generateXYMap->putting treated table elements into " + destinationTable);

			// write
			connectionsManager.GeoserverUpdate(String.format(rawInsert, destinationTable, "the_geom", allInfo.toString()));

			// proceed with the next chunk
			rows = connectionsManager.TimeSeriesQuery(String.format(rawSelect, xDimension + "," + yDimension, originTable, "" + chunkSize, "" + startIndex));
		} // end in writing rows

		if (createMap)
			return generatePointGisMap(destinationTable, mapName, localGISInfo);
		else
			return null;
	}

	public String generatePointGisMap(String destinationTable, String mapName, GISInformation gisInfo) throws Exception {

		AnalysisLogger.getLogger().trace("generateXYMaps->creating layer");
		// add the style
		GISStyleInformation style = new GISStyleInformation();
		style.setStyleName("point");

		GISLayerInformation gisLayer1 = new GISLayerInformation();
		gisLayer1.setDefaultStyle(style.getStyleName());
		gisLayer1.setLayerName(destinationTable);
		gisLayer1.setLayerTitle(mapName);

		// add layer to the previously generated group
		gisInfo.addLayer(gisLayer1);
		gisInfo.addStyle(gisLayer1.getLayerName(), style);
		AnalysisLogger.getLogger().trace("generateXYMaps->adding layers - done!");
		// create a GIS group for the the Layer
		String groupName = ("occpoints" + destinationTable.replace("-", ""));
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
		
		boolean urlcoherence = GISGroupInformation.checkLayers(gisInfo, layersList, 3);
		
		if (!urlcoherence)
			return null;
		else {
			AnalysisLogger.getLogger().trace("generateXYMaps->GIS Layer created!");
			return destinationTable;
		}
	}

}

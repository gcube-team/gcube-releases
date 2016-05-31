package org.gcube.contentmanagement.timeseries.geotools.vti.test;

import java.util.HashMap;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIEffortAggregator;

public class RegressionTestEffortAggregatorTS {

	public static void main (String[] args) throws Exception{
		
		//PREMESSA: cancellare i layer v_point_geometries_example e m_point_geometries_example da geoserver_dev prima di lanciare questo test
		//cancellare anche il file della cache
		
		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath("./cfg/");
		
		configuration.setTimeSeriesDatabase("jdbc:postgresql://localhost/testdb");
		configuration.setTimeSeriesUserName("gcube");
		configuration.setTimeSeriesPassword("d4science2");

		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");
			
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore("timeseriesgisdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");

		String tableName = "point_geometries_example";
		
		
		VTIEffortAggregator ea = new VTIEffortAggregator(configuration);
		GISOperations operations = new GISOperations();
		operations.deleteLayer(gisInfo, "v_"+tableName);
		operations.deleteLayer(gisInfo, "m_"+tableName);
		
		String tableKey = "gid";
		String xDimension = "x";
		String yDimension = "y";
		String datesDimension = "formatted_date";
		String fishingHoursDimension = "fishing_hours";
		String speedDimension = "speed";
		
		boolean appendMode = false;
		boolean produceVTI = true;
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("x", "xdime");
		
		ea.produceMontlyEffort(tableName, tableKey, xDimension, yDimension, datesDimension, fishingHoursDimension,speedDimension,gisInfo,produceVTI,appendMode,map);
		
	}
	
}

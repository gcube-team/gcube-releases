package org.gcube.contentmanagement.timeseries.geotools.vti.test.old;

import java.util.HashMap;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIEffortAggregator;

public class LocalTestEffortAggregatorTS {

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

		String tableName = "ts_1c77eda0_ffb5_11e0_8a23_9f275c9e23c9";
		
		
		VTIEffortAggregator ea = new VTIEffortAggregator(configuration);
		new GISOperations().deleteLayer(gisInfo, "v_"+tableName);
		new GISOperations().deleteLayer(gisInfo, "m_"+tableName);
		
		String tableKey = "field0";
		String xDimension = "field1";
		String yDimension = "field2";
		String datesDimension = "formatted_date";
		String fishingHoursDimension = "fishing_hours";
		String speedDimension = "field6";
		
		boolean appendMode = false;
		boolean produceVTI = true;
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("x", "xdime");
		
		ea.produceMontlyEffort(tableName, tableKey, xDimension, yDimension, datesDimension, fishingHoursDimension,speedDimension,gisInfo,produceVTI,appendMode,map);
		
	}
	
}

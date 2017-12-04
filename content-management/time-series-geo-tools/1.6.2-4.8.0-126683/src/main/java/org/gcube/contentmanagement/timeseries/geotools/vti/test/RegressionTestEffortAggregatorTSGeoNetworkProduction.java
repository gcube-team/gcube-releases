package org.gcube.contentmanagement.timeseries.geotools.vti.test;

import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIEffortAggregator;

public class RegressionTestEffortAggregatorTSGeoNetworkProduction {

	public static void main (String[] args) throws Exception{
		
		//PREMESSA: cancellare i layer v_point_geometries_example e m_point_geometries_example da geoserver_dev prima di lanciare questo test
		//cancellare anche il file della cache
		
		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath("./cfg/");
		
		configuration.setTimeSeriesDatabase("jdbc:postgresql://localhost/testdb");
		configuration.setTimeSeriesUserName("gcube");
		configuration.setTimeSeriesPassword("d4science2");

		configuration.setGeoServerDatabase("jdbc:postgresql://node50.p.d4science.research-infrastructures.eu/timeseriesgisdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");
			
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGeoNetworkUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geonetwork");
//		gisInfo.setGeoNetworkUrl("http://146.48.87.49:8080/geonetwork");
		gisInfo.setGeoNetworkUserName("admin");
		gisInfo.setGeoNetworkPwd("admin");
		
		gisInfo.setGisDataStore("timeseriesgisdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
//		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");

		String tableName = "point_geometries_example";
		
		
		VTIEffortAggregator ea = new VTIEffortAggregator(configuration);
//		new GISOperations().deleteLayer(gisInfo, "v_"+tableName);
//		new GISOperations().deleteLayer(gisInfo, "m_"+tableName);
		
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
		
		List<String> layersnames = ea.produceMontlyEffort(tableName, tableKey, xDimension, yDimension, datesDimension, fishingHoursDimension,speedDimension,gisInfo,produceVTI,appendMode,map);
		
	}
	
}

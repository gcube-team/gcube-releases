package org.gcube.contentmanagement.timeseries.geotools.vti.test.old;

import java.util.HashMap;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIEffortAggregator;

public class TestEffortAggregator {

	public static void main (String[] args) throws Exception{
		
		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath("./cfg/");
		
		configuration.setTimeSeriesDatabase("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		configuration.setTimeSeriesUserName("utente");
		configuration.setTimeSeriesPassword("d4science");
		
		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");
			
		GISInformation gisInfo = new GISInformation();
		
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");
		
		VTIEffortAggregator ea = new VTIEffortAggregator(configuration);
		
		String tableName = "point_geometries_example";
		
		String tableKey = "gid";
		String xDimension = "x";
		String yDimension = "y";
		String datesDimension = "date";
		String fishingHoursDimension = "fishing_hours";
		String speedDimension = "speed";
		boolean appendMode = false;
		boolean produceVTI = true;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("x", "xdime");
		ea.produceMontlyEffort(tableName, tableKey, xDimension, yDimension, datesDimension, fishingHoursDimension,speedDimension,gisInfo,produceVTI,appendMode,map);
		
	}
	
}

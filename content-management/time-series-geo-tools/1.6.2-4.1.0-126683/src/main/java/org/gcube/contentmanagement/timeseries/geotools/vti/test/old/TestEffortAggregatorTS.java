package org.gcube.contentmanagement.timeseries.geotools.vti.test.old;

import java.util.HashMap;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIEffortAggregator;

public class TestEffortAggregatorTS {

	public static void main (String[] args) throws Exception{
		
		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath("./cfg/");
		
		configuration.setTimeSeriesDatabase("jdbc:postgresql://dbtest.next.research-infrastructures.eu/timeseries");
		configuration.setTimeSeriesUserName("utente");
		configuration.setTimeSeriesPassword("d4science");
		
//		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");
			
		GISInformation gisInfo = new GISInformation();
		
		gisInfo.setGisDataStore("timeseriesgisdb");
//		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");
		
		VTIEffortAggregator ea = new VTIEffortAggregator(configuration);
		
		String tableName = "e96dcd2f0daf211e0b52eaa2809b73059";
		
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

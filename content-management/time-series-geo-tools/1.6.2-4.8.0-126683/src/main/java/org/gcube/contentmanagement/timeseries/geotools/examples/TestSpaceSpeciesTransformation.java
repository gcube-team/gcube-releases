package org.gcube.contentmanagement.timeseries.geotools.examples;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.engine.TimeSeriesGISConverter;
import org.gcube.contentmanagement.timeseries.geotools.filters.AFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpaceFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpeciesFilter;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;

public class TestSpaceSpeciesTransformation {


	public static void main(String[] args) {
		//TEST1 -  TS
		String timeSeriesName = "ts_abramis_brama";
		String aggregationColumn = "field1";
		String informationColumn = "field4";
		String quantitiesColumn = "field4_id";
		
		GISInformation gisInfo = new GISInformation();
		
//		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisDataStore("timeseriesgisdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");
		
		
		AFilter filter = new SpeciesFilter(timeSeriesName,aggregationColumn,informationColumn,quantitiesColumn);
		ArrayList<AFilter> filters = new ArrayList<AFilter>();
		filters.add(filter);
		try{
			TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
			configuration.setConfigPath("./cfg/");
			
//			configuration.setAquamapsDatabase("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdatedOLD");
			configuration.setAquamapsDatabase("jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/essentialaquamaps");
			
			configuration.setTimeSeriesDatabase("jdbc:mysql://localhost/timeseries");
			configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
			
			configuration.setAquamapsUserName("postgres");
			configuration.setTimeSeriesUserName("root");
			configuration.setGeoServerUserName("postgres");
			
			configuration.setAquamapsPassword("d4science2");
			configuration.setTimeSeriesPassword("ash_ash80");
			configuration.setGeoServerPassword("d4science2");
			
			configuration.setReferenceCountriesTable("ref_country");
			configuration.setReferenceSpeciesTable("ref_species");
			
			TimeSeriesGISConverter converter = new TimeSeriesGISConverter(configuration);
			
			long t0 = System.currentTimeMillis();
			List<String> groupName = converter.TimeSeriesToGIS(filters,gisInfo,false);
			System.out.println("GROUP NAME : "+groupName);
			long t1 = System.currentTimeMillis();
			System.out.println("ELAPSED TIME : "+(t1-t0));
		}catch(Exception e){e.printStackTrace();}
	}
	
}

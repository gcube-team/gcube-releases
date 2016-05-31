package org.gcube.contentmanagement.timeseries.geotools.test.old;

import java.util.ArrayList;

import org.gcube.contentmanagement.timeseries.geotools.engine.TimeSeriesGISConverter;
import org.gcube.contentmanagement.timeseries.geotools.filters.AFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpaceFilter;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;

public class TestCountriesConversion2 {


	public static void main(String[] args) {
		//TEST1 -  TS
		String timeSeriesName = "ts_abramis_brama";
		String aggregationColumn = "field1";
		String quantitiesColumn = "field4_id";
	
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");
		
		
		AFilter filter = new SpaceFilter(timeSeriesName,aggregationColumn,quantitiesColumn);
		ArrayList<AFilter> filters = new ArrayList<AFilter>();
		filters.add(filter);
		try{
			TimeSeriesGISConverter converter = new TimeSeriesGISConverter("./cfg/");
			long t0 = System.currentTimeMillis();
			converter.TimeSeriesToGIS(filters,gisInfo,false);
			long t1 = System.currentTimeMillis();
			System.out.println("ELAPSED TIME : "+(t1-t0));
		}catch(Exception e){e.printStackTrace();}
	}
	
}

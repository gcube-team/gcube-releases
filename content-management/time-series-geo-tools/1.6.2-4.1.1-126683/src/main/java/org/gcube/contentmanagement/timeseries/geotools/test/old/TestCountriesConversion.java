package org.gcube.contentmanagement.timeseries.geotools.test.old;

import java.util.ArrayList;

import org.gcube.contentmanagement.timeseries.geotools.engine.TimeSeriesGISConverter;
import org.gcube.contentmanagement.timeseries.geotools.filters.AFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpaceFilter;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;

public class TestCountriesConversion {


	public static void main(String[] args) {
		//TEST1 - Small TS
		String timeSeriesName = "ts_79fae010_310b_11df_b8b3_aa10916debe6";
		String aggregationColumn = "field0";
		String quantitiesColumn = "field3_id";
		/*
		//TEST2 - Medium TS
		timeSeriesName = "ts_7dfe5860_7321_11df_923c_835b6e1d12d4";
		aggregationColumn = "field0";
		informationColumn = "";
		quantitiesColumn = "field3_id";
		//TEST3 - Large TS
		timeSeriesName = "ts_ba08c4b0_f0c3_11df_8827_de008e0850ff";
		aggregationColumn = "field1";
		informationColumn = "";
		quantitiesColumn = "field4_id";
		*/
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

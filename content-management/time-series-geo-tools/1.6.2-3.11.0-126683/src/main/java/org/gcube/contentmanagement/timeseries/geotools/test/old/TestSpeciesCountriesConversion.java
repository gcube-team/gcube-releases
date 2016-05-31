package org.gcube.contentmanagement.timeseries.geotools.test.old;

import java.util.ArrayList;

import org.gcube.contentmanagement.timeseries.geotools.engine.TimeSeriesGISConverter;
import org.gcube.contentmanagement.timeseries.geotools.filters.AFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpaceFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpeciesFilter;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;

public class TestSpeciesCountriesConversion {


	public static void main(String[] args) {
		//complex
		/*
		String timeSeriesName = "ts_ba08c4b0_f0c3_11df_8827_de008e0850ff";
		String aggregationColumn = "field1";
		String informationColumn = "field4";
		String quantitiesColumn = "field4_id";
		*/
		//LARGE TS
		/*
		String timeSeriesName = "ts_82ce8dc0_2d07_11df_b8b3_aa10916debe6";
		String aggregationColumn = "field0";
		String informationColumn = "field3";
		String quantitiesColumn = "field6";
		*/
		//SMALL TS
		/*
		String timeSeriesName = "ts_79fae010_310b_11df_b8b3_aa10916debe6";
		String aggregationColumn = "field0";
		String informationColumn = "field3";
		String quantitiesColumn = "field3_id";
		*/
		
		String timeSeriesName = "ts_abramis_brama";
		String aggregationColumn = "field1";
		String informationColumn = "field4";
		String quantitiesColumn = "field4_id";
		
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");
		
		
		AFilter filter = new SpeciesFilter(timeSeriesName,aggregationColumn,informationColumn,quantitiesColumn);
		
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

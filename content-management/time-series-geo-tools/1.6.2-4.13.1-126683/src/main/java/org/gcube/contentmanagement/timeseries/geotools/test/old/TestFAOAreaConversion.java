package org.gcube.contentmanagement.timeseries.geotools.test.old;

import java.util.ArrayList;

import org.gcube.contentmanagement.timeseries.geotools.engine.TimeSeriesGISConverter;
import org.gcube.contentmanagement.timeseries.geotools.filters.AFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.custom.FAOAreaFilter;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;

public class TestFAOAreaConversion {


	public static void main(String[] args) {

		String timeSeriesName = "ts_e395b040_2c2e_11df_b8b3_aa10916debe6";
		String aggregationColumn = "field3_id";
		String informationColumn = "";
		String quantitiesColumn = "field7";
		
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");
		
		AFilter filter = new FAOAreaFilter(timeSeriesName,aggregationColumn,quantitiesColumn);
		
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

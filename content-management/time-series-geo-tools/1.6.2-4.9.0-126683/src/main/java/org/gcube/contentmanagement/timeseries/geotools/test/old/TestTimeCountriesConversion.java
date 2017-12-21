package org.gcube.contentmanagement.timeseries.geotools.test.old;

import java.util.ArrayList;

import org.gcube.contentmanagement.timeseries.geotools.engine.TimeSeriesGISConverter;
import org.gcube.contentmanagement.timeseries.geotools.filters.AFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpaceFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpeciesFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.custom.CountryYearFilter;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;

public class TestTimeCountriesConversion {


	public static void main(String[] args) {
		
		String timeSeriesName = "ts_958673d0_2dc8_11df_b8b3_aa10916debe6";
		String aggregationColumn = "field0";
		String informationColumn = "field5";
		String quantitiesColumn = "field6";
		
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");
		
		
		AFilter filter = new CountryYearFilter(timeSeriesName,aggregationColumn,informationColumn,quantitiesColumn);
		
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

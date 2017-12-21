package org.gcube.contentmanagement.timeseries.geotools.examples;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.engine.TimeSeriesGISConverter;
import org.gcube.contentmanagement.timeseries.geotools.filters.AFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpaceFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.SpeciesFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.custom.CountryYearFilter;
import org.gcube.contentmanagement.timeseries.geotools.filters.custom.FAOAreaFilter;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;

public class TestTimeCountriesTransformation {


	public static void main(String[] args) {
		//TEST1 -  TS
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
			TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
			configuration.setConfigPath("./cfg/");
			
			configuration.setAquamapsDatabase("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdatedOLD");
			configuration.setTimeSeriesDatabase("jdbc:mysql://localhost/timeseries");
			configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
			
			configuration.setAquamapsUserName("utente");
			configuration.setTimeSeriesUserName("root");
			configuration.setGeoServerUserName("postgres");
			
			configuration.setAquamapsPassword("d4science");
			configuration.setTimeSeriesPassword("ash_ash80");
			configuration.setGeoServerPassword("d4science2");
			
			configuration.setReferenceCountriesTable("ref_country");
			
			TimeSeriesGISConverter converter = new TimeSeriesGISConverter(configuration);
			
			long t0 = System.currentTimeMillis();
			List<String> groupName = converter.TimeSeriesToGIS(filters,gisInfo,false);
			System.out.println("GROUP NAME : "+groupName);
			long t1 = System.currentTimeMillis();
			System.out.println("ELAPSED TIME : "+(t1-t0));
		}catch(Exception e){e.printStackTrace();}
	}

}

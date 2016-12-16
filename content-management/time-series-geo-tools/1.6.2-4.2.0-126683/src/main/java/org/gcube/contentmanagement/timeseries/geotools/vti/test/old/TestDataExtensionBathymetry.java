package org.gcube.contentmanagement.timeseries.geotools.vti.test.old;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIDataExtender;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIDataExtender.DataExtenderFunctionalities;

public class TestDataExtensionBathymetry {

	public static void main (String[] args) throws Exception{
		
		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath("./cfg/");
		//setup DB connection
		configuration.setTimeSeriesDatabase("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdatedOLD");
		configuration.setTimeSeriesUserName("utente");
		configuration.setTimeSeriesPassword("d4science");
		
		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");
		
		VTIDataExtender extender = new VTIDataExtender(configuration);
		String tableName = "point_geometries_example";
		
		List<Tuple<String>> newColumns = new ArrayList<Tuple<String>>();
		Tuple<String> singlenewcolumn = new Tuple<String> ("bathymetry","real");
		newColumns.add(singlenewcolumn);
		
		String tableKey = "gid";		
		String tableKeyType = "serial";	
		String firstDimension = "x";
		String secondDimension = "y";
		DataExtenderFunctionalities functionality = DataExtenderFunctionalities.bathymetry;
		
		extender.extendTable(tableName, newColumns, tableKey,tableKeyType, firstDimension, secondDimension, functionality);
	}
	
}

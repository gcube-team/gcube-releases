package org.gcube.contentmanagement.timeseries.geotools.vti.test.old;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIDataExtender;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIDataExtender.DataExtenderFunctionalities;

public class TestVTIDimensions {
	
	
		public static void main (String [] args) throws Exception{
			
			//setup the database to use  where the table to expand is present: eg. Timeseries
			TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
			configuration.setConfigPath("./cfg/");

			//setup DB connection
			configuration.setTimeSeriesDatabase("jdbc:postgresql://localhost/testdb");
			configuration.setTimeSeriesUserName("gcube");
			configuration.setTimeSeriesPassword("d4science2");
			
			//table to be extended
			String tableName = "point_geometries_example";
			//table information
			//pre-existing fields
			String tablePrimaryKey = "gid";		
			String tablePrimaryKeyType = "serial";	
			String xDimension = "x";
			String yDimension = "y";
			String speedDimension = "speed";
			String rawDatesDimension = "time";
			String vesselIDDimension = "vesselid";
			
			//setup of the extender object : it will initialize some singleton objects
			VTIDataExtender extender = new VTIDataExtender(configuration);
			String bathymetryColumnName = (extender.getColumnsAType(DataExtenderFunctionalities.bathymetry)).get(0).getElements().get(0);
			extender.extendTable(tableName,tablePrimaryKey,tablePrimaryKeyType, xDimension, yDimension, DataExtenderFunctionalities.bathymetry);
			//only when the object is no more needed - shutdown the connection
			extender.shutDown();
			
			
			
		}
}

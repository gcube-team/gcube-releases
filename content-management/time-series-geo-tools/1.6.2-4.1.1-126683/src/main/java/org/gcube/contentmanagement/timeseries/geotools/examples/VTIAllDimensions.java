package org.gcube.contentmanagement.timeseries.geotools.examples;

import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIDataExtender;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIDataExtender.DataExtenderFunctionalities;

public class VTIAllDimensions {
	
	
		public static void main (String [] args) throws Exception{
			
			//setup the database to use  where the table to expand is present: eg. Timeseries
			TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
			configuration.setConfigPath("./cfg/");
			//setup DB connection
			configuration.setTimeSeriesDatabase("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
			configuration.setTimeSeriesUserName("utente");
			configuration.setTimeSeriesPassword("d4science");
			
			configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
			configuration.setGeoServerUserName("postgres");
			configuration.setGeoServerPassword("d4science2");
			
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
			String formattedDatesColumn = (extender.getColumnsAType(DataExtenderFunctionalities.vti_dates)).get(0).getElements().get(0);
			
			//CASE 1 : BATHYMETRY CALCULATION
			extender.extendTable(tableName,tablePrimaryKey,tablePrimaryKeyType, xDimension, yDimension, DataExtenderFunctionalities.bathymetry);
			
			//CASE 2: FISHERY CLASSIFICATION
			extender.extendTable(tableName,tablePrimaryKey,tablePrimaryKeyType, speedDimension, bathymetryColumnName, DataExtenderFunctionalities.classify);
			
			//CASE 3: FORMATTED DATES
			extender.extendTable(tableName, tablePrimaryKey,tablePrimaryKeyType,rawDatesDimension,DataExtenderFunctionalities.vti_dates);
			
			//CASE 4: FISHING HOURS
			extender.extendTable(tableName, tablePrimaryKey,tablePrimaryKeyType,vesselIDDimension,formattedDatesColumn, DataExtenderFunctionalities.fishing_hours);

			//CASE 5: FAO AREAS REPORTING
			extender.extendTable(tableName, tablePrimaryKey,tablePrimaryKeyType, xDimension, yDimension, DataExtenderFunctionalities.fao_areas);
			
			//CASE 6: SEA SURFACE TEMPERATURE - OPTIONAL
			extender.extendTable(tableName, tablePrimaryKey,tablePrimaryKeyType, xDimension, yDimension, DataExtenderFunctionalities.sst);
			
			//EXAMPLE TO TAKE GENERATED COLUMNS:
			//take a list of couples associated to the fao areas: couples contain (columnName, columnType)
			List<Tuple<String>> couples = extender.getColumnsAType(DataExtenderFunctionalities.fao_areas);
			System.out.println("COLUMNS NAMES AND TYPES");
			for (Tuple<String> couple: couples){
				System.out.println("column name:"+couple.getElements().get(0));
				System.out.println("column type:"+couple.getElements().get(1));
			}
			
			
			//only when the object is no more needed - shutdown the connection
			extender.shutDown();
			
			
			
		}
}

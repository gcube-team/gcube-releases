package org.gcube.contentmanagement.timeseries.geotools.examples;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIDataExtender;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIDataExtender.DataExtenderFunctionalities;

public class VTIAllDimensionsWithColums {
	
	
		public static void main (String [] args) throws Exception{
			
			//setup the database to use  where the table to expand is present: eg. Timeseries
			TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
			configuration.setConfigPath("./cfg/");
			//setup DB connection
			configuration.setTimeSeriesDatabase("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdatedOLD");
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
			
			//newly generated fields
			String formattedDatesDimension = "date";
			String bathymetryDimension = "bathymetry";
			String simpleClassDimension = "simple_class"; 
			String bathymetryClassDimension = "bathymetry_class";
			String fishingHoursDimension = "fishing_hours";
			String faoArea1 = "FAO_SUB_UNIT";
			String faoArea2 = "FAO_SUB_AREA";
			String faoArea3 = "FAO_SUB_DIV";
			String faoArea4 = "FAO_DIV";
			String faoArea5 = "FAO_MAJOR";
			String sst = "sst";
			
			//setup of the extender object : it will initialize some singleton objects
			VTIDataExtender extender = new VTIDataExtender(configuration);

			//CASE 1 : BATHYMETRY CALCULATION
			List<Tuple<String>> newColumns = new ArrayList<Tuple<String>>();
			//setup of the new column to add with its type
			Tuple<String> singlenewcolumn = new Tuple<String> (bathymetryDimension,"real");
			newColumns.add(singlenewcolumn);
			//functionality to be called
			DataExtenderFunctionalities functionality = DataExtenderFunctionalities.bathymetry;
			//perform extension 
			extender.extendTable(tableName, newColumns, tablePrimaryKey,tablePrimaryKeyType, xDimension, yDimension, functionality);
			
			//CASE 2: FISHERY CLASSIFICATION
			//setup the new columns you want to create
			newColumns = new ArrayList<Tuple<String>>();
			singlenewcolumn = new Tuple<String> (simpleClassDimension,"real");
			Tuple<String> singlenewcolumn2 = new Tuple<String> (bathymetryClassDimension,"real");
			newColumns.add(singlenewcolumn);
			newColumns.add(singlenewcolumn2);
			//functionality to be called
			functionality = DataExtenderFunctionalities.classify;
			//perform extension
			extender.extendTable(tableName, newColumns, tablePrimaryKey,tablePrimaryKeyType, speedDimension, bathymetryDimension, functionality);
			
			//CASE 3: FORMATTED DATES
			newColumns = new ArrayList<Tuple<String>>();
			singlenewcolumn = new Tuple<String> (formattedDatesDimension,"timestamp without time zone");
			newColumns.add(singlenewcolumn);
			functionality = DataExtenderFunctionalities.vti_dates;
			extender.extendTable(tableName, newColumns, tablePrimaryKey,tablePrimaryKeyType,rawDatesDimension,functionality);
			
			//CASE 4: FISHING HOURS
			newColumns = new ArrayList<Tuple<String>>();
			singlenewcolumn = new Tuple<String> (fishingHoursDimension,"real");
			newColumns.add(singlenewcolumn);
			functionality = DataExtenderFunctionalities.fishing_hours;
			extender.extendTable(tableName, newColumns, tablePrimaryKey,tablePrimaryKeyType,vesselIDDimension,formattedDatesDimension, functionality);
			
			//CASE 5: FAO AREAS REPORTING
			newColumns = new ArrayList<Tuple<String>>();
			Tuple<String> singlenewcolumn1 = new Tuple<String> (faoArea1,"character varying");
								   singlenewcolumn2 = new Tuple<String> (faoArea2,"character varying");
			Tuple<String> singlenewcolumn3 = new Tuple<String> (faoArea3,"character varying");
			Tuple<String> singlenewcolumn4 = new Tuple<String> (faoArea4,"character varying");
			Tuple<String> singlenewcolumn5 = new Tuple<String> (faoArea5,"character varying");
			
			newColumns.add(singlenewcolumn1);
			newColumns.add(singlenewcolumn2);
			newColumns.add(singlenewcolumn3);
			newColumns.add(singlenewcolumn4);
			newColumns.add(singlenewcolumn5);
			functionality = DataExtenderFunctionalities.fao_areas;
			extender.extendTable(tableName, newColumns, tablePrimaryKey,tablePrimaryKeyType, xDimension, yDimension, functionality);
			
			//CASE 6: SEA SURFACE TEMPERATURE
			newColumns = new ArrayList<Tuple<String>>();
			singlenewcolumn = new Tuple<String> (sst,"real");
			newColumns.add(singlenewcolumn);
			functionality = DataExtenderFunctionalities.sst;
			extender.extendTable(tableName, newColumns, tablePrimaryKey,tablePrimaryKeyType, xDimension, yDimension, functionality);
			
			//only when the object is no more needed - shutdown the connection
			extender.shutDown();
			
		}
}

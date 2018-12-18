package org.gcube.contentmanagement.timeseries.geotools.vti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;
import org.gcube.contentmanagement.timeseries.geotools.vti.VTIDataExtender.DataExtenderFunctionalities;

public class VTIColumnsBuilder {

	
	HashMap<String,List<Tuple<String>>> defaultColumnMap;
	
	public VTIColumnsBuilder(){
		buildMap();
	}
	
	private void buildMap(){
		
		defaultColumnMap = new HashMap<String, List<Tuple<String>>>();
		
		//bathymetry
		List<Tuple<String>> newColumns = new ArrayList<Tuple<String>>();
		Tuple<String> singlenewcolumn = new Tuple<String> ("bathymetry","real");
		newColumns.add(singlenewcolumn);
		defaultColumnMap.put(DataExtenderFunctionalities.bathymetry.name(), newColumns);
		
		//classification
		newColumns = new ArrayList<Tuple<String>>();
		singlenewcolumn = new Tuple<String> ("simple_class","real");
		Tuple<String> singlenewcolumn_1 = new Tuple<String> ("bathymetry_class","real");
		newColumns.add(singlenewcolumn);
		newColumns.add(singlenewcolumn_1);
		defaultColumnMap.put(DataExtenderFunctionalities.classify.name(), newColumns);
		
		//dates
		newColumns = new ArrayList<Tuple<String>>();
		singlenewcolumn = new Tuple<String> ("formatted_date","timestamp without time zone");
		newColumns.add(singlenewcolumn);
		defaultColumnMap.put(DataExtenderFunctionalities.vti_dates.name(), newColumns);
		
		//fishing hours
		newColumns = new ArrayList<Tuple<String>>();
		singlenewcolumn = new Tuple<String> ("fishing_hours","real");
		newColumns.add(singlenewcolumn);
		defaultColumnMap.put(DataExtenderFunctionalities.fishing_hours.name(), newColumns);
		
		//fao areas
		newColumns = new ArrayList<Tuple<String>>();
		Tuple<String> singlenewcolumn1 = new Tuple<String> ("fao_sub_unit","character varying");
		Tuple<String> singlenewcolumn2 = new Tuple<String> ("fao_sub_area","character varying");
		Tuple<String> singlenewcolumn3 = new Tuple<String> ("fao_sub_div","character varying");
		Tuple<String> singlenewcolumn4 = new Tuple<String> ("fao_div","character varying");
		Tuple<String> singlenewcolumn5 = new Tuple<String> ("fao_major","character varying");
		newColumns.add(singlenewcolumn1);
		newColumns.add(singlenewcolumn2);
		newColumns.add(singlenewcolumn3);
		newColumns.add(singlenewcolumn4);
		newColumns.add(singlenewcolumn5);
		defaultColumnMap.put(DataExtenderFunctionalities.fao_areas.name(), newColumns);
		
		//sst
		newColumns = new ArrayList<Tuple<String>>();
		singlenewcolumn = new Tuple<String> ("sst","real");
		newColumns.add(singlenewcolumn);
		defaultColumnMap.put(DataExtenderFunctionalities.sst.name(), newColumns);
		
	}
	
	public List<Tuple<String>> getColumnInfo(DataExtenderFunctionalities function){
			return defaultColumnMap.get(function.name());
	}
}

package org.gcube.contentmanagement.timeseries.geotools.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class FeaturesBag {
	Map<String,String> features;
	
	public static enum feature{
		timeSeriesTableName,
		speciesColumnName,
		placeColumnName,
		quantitiesColumnName
	}
	
	public FeaturesBag(){
		features = new LinkedHashMap<String,String>();
	}
	
	public String get(String key){
		if (key == null) return null;
		return features.get(key);
	}
	
	public void add(String key,String value){
		features.put(key,value);
	}
	
	public void remove(String key){
		features.remove(key);
	}
}

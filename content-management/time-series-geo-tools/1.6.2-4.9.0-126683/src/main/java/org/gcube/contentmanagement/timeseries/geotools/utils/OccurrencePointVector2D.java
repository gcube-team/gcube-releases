package org.gcube.contentmanagement.timeseries.geotools.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class OccurrencePointVector2D {

	
	public float x;
	public float y;
	public LinkedHashMap<String, String> metadata;
	
	public OccurrencePointVector2D(float x,float y){
		this.x = x;
		this.y=y;
		metadata=new LinkedHashMap<String, String>();
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public void addMetadataToMap(String name,String value){
		metadata.put(name, value);
	}
	
	
	public void setMap(LinkedHashMap<String, String> metadataMap){
		this.metadata = metadataMap;
	}
	
	public Map<String, String> getmetadata(){
		return metadata;
	}
	
	
}

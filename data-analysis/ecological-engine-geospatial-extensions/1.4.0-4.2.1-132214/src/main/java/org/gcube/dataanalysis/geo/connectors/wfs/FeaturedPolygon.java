package org.gcube.dataanalysis.geo.connectors.wfs;

import java.util.LinkedHashMap;

import com.vividsolutions.jts.geom.Geometry;

public class FeaturedPolygon {
	public Geometry p;
	public LinkedHashMap<String, String> features;
	public Double value;
	public FeaturedPolygon(){
		
	}
	
	public void setPolygon(Geometry p){
		this.p=p;
	}
	
	public void setValue(Double v){
		this.value=v;
	}
	
	public void addFeature(String key,String value){
		if (features==null)
			features = new LinkedHashMap<String, String>();
		features.put(key,value);
	}
}

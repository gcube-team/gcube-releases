package org.gcube.dataanalysis.ecoengine.connectors.livemonitor;

public class ResourceLoad {
	public long timestamp;
	public double value;
	
	
	public ResourceLoad (long time,double val){
		timestamp = time;
		value = val;
	}
	public String toString(){
		return "["+timestamp+", "+value+"]";
	}
	
	public static String defaultResourceLoad(){
		long tk = System.currentTimeMillis();
		return new ResourceLoad(tk, 1).toString();
	} 
}

package org.gcube.application.aquamaps.ecomodelling.generators.connectors.livemonitor;

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
	
	
}

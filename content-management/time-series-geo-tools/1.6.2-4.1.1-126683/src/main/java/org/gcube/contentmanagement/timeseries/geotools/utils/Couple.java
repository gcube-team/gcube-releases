package org.gcube.contentmanagement.timeseries.geotools.utils;

public class Couple {
	private String first;
	private String second;
	public void setFirst(String first) {
		this.first = first;
	}
	public String getFirst() {
		return first;
	}
	public void setSecond(String second) {
		this.second = second;
	}
	public String getSecond() {
		return second;
	}
	
	public Couple(String first,String second){
		
		this.first = first;
		this.second = second;
	}
}

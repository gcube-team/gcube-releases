package org.gcube.common.dbinterface;

public class Limit {

	private int numberOfElement,startElement;
	
	public Limit(int offset){
		this.numberOfElement=0;
		this.startElement=offset;
	}
	
	public Limit(int startElement, int numberOfElement){
		this.numberOfElement=numberOfElement;
		this.startElement=startElement;
	}
	
	public String getLimits(){
		return " LIMIT "+numberOfElement+" OFFSET "+startElement+" ";
	}
	
}

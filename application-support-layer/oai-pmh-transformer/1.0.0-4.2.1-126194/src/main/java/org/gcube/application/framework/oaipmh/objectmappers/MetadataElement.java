package org.gcube.application.framework.oaipmh.objectmappers;

/**
* for minOccurs and maxOccurs, values are from 0 to "unbounded"
* @author nikolas
*
*/
public class MetadataElement{
	private String name;
	private String minOccurs; //values from "0" to "unbounded"
	private String maxOccurs; //values from "0" to "unbounded"
	private String type; //type is usually string
	
	public MetadataElement(String name, String minOccurs, String maxOccurs, String type){
		this.name = name;
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
		this.type = type;
	}
	
	public String getName(){
		return name;
	}
	
	public String getMinOccurs(){
		return minOccurs;
	}
	
	public String getMaxOccurs(){
		return maxOccurs;
	}
	
	public String getType(){
		return type;
	}
}

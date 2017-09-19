package org.gcube.dataanalysis.ecoengine.datatypes;

public class StatisticalType {

	protected String defaultValue;
	protected String description;
	protected String name;

	protected boolean optional;
	
	public StatisticalType(String name, String description,String defaultValue, boolean optional){
		this.name=name;
		this.description=description;
		this.defaultValue=defaultValue;
		this.optional=optional;
	}
	
	public StatisticalType(String name, String description,String defaultValue){
		this.name=name;
		this.description=description;
		this.defaultValue=defaultValue;
		this.optional=true;
	}
	
	public StatisticalType(String name, String description){
		this.name=name;
		this.description=description;
		this.defaultValue="";
		this.optional=true;
	}
	
	
	public StatisticalType(String name, String description, boolean optional) {
		this.name=name;
		this.description=description;
		this.defaultValue="";
		this.optional=optional;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isOptional() {
		return optional;
	}
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		return name+":"+description+":"+defaultValue+":"+optional;
	}
}

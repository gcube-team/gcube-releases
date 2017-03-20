package org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors;

public class OccurrencePoint {
	private String speciesID;
	private String cSquareCode;
	private Double value;
	
	public OccurrencePoint(String speciesID,String cSquareCode,Double value){
		this.speciesID=speciesID;
		this.cSquareCode= cSquareCode;
		this.value = value;
	}
	
	public OccurrencePoint(String cSquareCode, Double value){
		this.cSquareCode= cSquareCode;
		this.value = value;
	}
	
	public OccurrencePoint(Double value){
		this.speciesID="";
		this.cSquareCode= "";
		this.value = value;
	}
	
	public void setSpeciesID(String speciesID) {
		this.speciesID = speciesID;
	}
	public String getSpeciesID() {
		return speciesID;
	}
	public void setCsquareCode(String csquareCode) {
		this.cSquareCode = csquareCode;
	}
	public String getCsquareCode() {
		return cSquareCode;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getValue() {
		return value;
	}
	
	
	
	public Object[] toObjectArray(){
		Object[] array = new Object[3];
		array [0] = cSquareCode;
		array [1] = speciesID;
		array [2] = value;
		
		return array;
	}
}

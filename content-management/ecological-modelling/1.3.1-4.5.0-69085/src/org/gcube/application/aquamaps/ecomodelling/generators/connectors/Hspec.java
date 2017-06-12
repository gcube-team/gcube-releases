package org.gcube.application.aquamaps.ecomodelling.generators.connectors;

public class Hspec {

	private String speciesID;
	private String csquarecode;
	private double probability;
	private String faoaream;
	private String eezall;
	private String lme;
	
	private BoundingBoxInformation boundingBox;
	
	public void setSpeciesID(String speciesID) {
		this.speciesID = speciesID;
	}
	public String getSpeciesID() {
		return speciesID;
	}
	public void setCsquareCode(String csquarecode) {
		this.csquarecode = csquarecode;
	}
	public String getCsquarecode() {
		return csquarecode;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
	public double getProbability() {
		return probability;
	}
	public void setBoundingBox(BoundingBoxInformation boundingBox) {
		this.boundingBox = boundingBox;
	}
	public BoundingBoxInformation getBoundingBox() {
		return boundingBox;
	}
	public void setFaoaream(String faoaream) {
		this.faoaream = faoaream;
	}
	public String getFaoaream() {
		return faoaream;
	}
	public void setEezall(String eezall) {
		this.eezall = eezall;
	}
	public String getEezall() {
		return eezall;
	}
	public void setLme(String lme) {
		this.lme = lme;
	}
	public String getLme() {
		return lme;
	}
	
}

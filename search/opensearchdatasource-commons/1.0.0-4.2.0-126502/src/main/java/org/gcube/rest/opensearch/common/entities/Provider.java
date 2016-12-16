package org.gcube.rest.opensearch.common.entities;

import java.util.List;

public class Provider {
	private String collectionID;
	private String openSearchResourceID;
	private List<String> fixedParameters;
	
	public Provider(){
		
	}
	
	public String getCollectionID() {
		return collectionID;
	}
	public void setCollectionID(String collectionID) {
		this.collectionID = collectionID;
	}
	public String getOpenSearchResourceID() {
		return openSearchResourceID;
	}
	public void setOpenSearchResourceID(String openSearchResourceID) {
		this.openSearchResourceID = openSearchResourceID;
	}
	public List<String> getFixedParameters() {
		return fixedParameters;
	}
	public void setFixedParameters(List<String> fixedParameters) {
		this.fixedParameters = fixedParameters;
	}
	
	
}

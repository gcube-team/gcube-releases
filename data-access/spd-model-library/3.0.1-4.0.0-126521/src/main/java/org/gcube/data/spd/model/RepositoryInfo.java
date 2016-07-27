package org.gcube.data.spd.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RepositoryInfo {

	private String logoUrl;
	private String pageReferenceUrl;
	private String description;
	
	private Map<String, String> properties= new HashMap<String, String>();
	
	protected RepositoryInfo(){};
		
	public RepositoryInfo(String logoUrl, String pageReferenceUrl, String description) {
		this.logoUrl = logoUrl;
		this.pageReferenceUrl = pageReferenceUrl;
		this.description = description;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public String getPageReferenceUrl() {
		return pageReferenceUrl;
	}
	
	public String getDescription() {
		return description;
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public void addProperty(String key, String value){
		this.properties.put(key, value);
	}
	
	public void resetProperties(){
		this.properties = new HashMap<String, String>();
	}

	@Override
	public String toString() {
		return "RepositoryInfo [logoUrl=" + logoUrl + ", pageReferenceUrl="
				+ pageReferenceUrl + ", description=" + description
				+ ", properties=" + properties + "]";
	}
	
	
	
}

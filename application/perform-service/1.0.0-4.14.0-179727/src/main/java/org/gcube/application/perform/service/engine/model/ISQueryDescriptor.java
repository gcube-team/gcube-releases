package org.gcube.application.perform.service.engine.model;

public class ISQueryDescriptor {


	private String resourceName;
	private String platformName;
	private String category;
	
	public ISQueryDescriptor() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public ISQueryDescriptor(String resourceName, String platformName, String category) {
		super();
		this.resourceName = resourceName;
		this.platformName = platformName;
		this.category = category;
	}



	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getPlatformName() {
		return platformName;
	}
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}



	@Override
	public String toString() {
		return "ISQueryDescriptor [resourceName=" + resourceName + ", platformName=" + platformName + ", category="
				+ category + "]";
	}

	
	
}

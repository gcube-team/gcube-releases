package org.gcube.data.analysis.statisticalmanager.persistence;

public enum DatabaseType {

	OPERATIONAL("jdbc"),
	HIBERNATE("hibernate");
	
	
	private String accessPointName;
	
	DatabaseType(String accessPointName){
		this.accessPointName=accessPointName;
	}

	/**
	 * @return the accessPointName
	 */
	public String getAccessPointName() {
		return accessPointName;
	}

	/**
	 * @param accessPointName the accessPointName to set
	 */
	public void setAccessPointName(String accessPointName) {
		this.accessPointName = accessPointName;
	}
	
	
}

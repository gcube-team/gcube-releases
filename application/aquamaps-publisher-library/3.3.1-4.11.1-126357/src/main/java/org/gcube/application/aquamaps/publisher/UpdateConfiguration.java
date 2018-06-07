package org.gcube.application.aquamaps.publisher;

public class UpdateConfiguration {

	//***************** UPDATES SERIALIZED INFORMATIONS
	private Boolean updateObjectMeta=true;
	
	//USED BY JOBS AND OBJECTS
	private Boolean updateGIS=true;
	private Boolean updateFile=true;
	
	public UpdateConfiguration(Boolean updateObjectMeta, Boolean updateGIS,
			Boolean updateFile) {
		super();
		this.updateObjectMeta = updateObjectMeta;
		this.updateGIS = updateGIS;
		this.updateFile = updateFile;
	}
	
	public Boolean getUpdateFile() {
		return updateFile;
	}
	public Boolean getUpdateGIS() {
		return updateGIS;
	}
	public Boolean getUpdateObjectMeta() {
		return updateObjectMeta;
	}
}

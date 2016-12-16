package org.gcube.datapublishing.sdmx.api.model;

public enum SDMXDatasourceInterfaceType {
	RESTV1("REST","1"), 
	RESTV2("REST","2"),
	RESTV2_1("REST","2.1");
	
	private String type;
	private String modelVersion;

	private SDMXDatasourceInterfaceType(String type, String modelVersion) {
		this.type = type;
		this.modelVersion=modelVersion;
	}

	public String getName() {
		return type + " v"+modelVersion;
	}
	
	public String getModelVersion(){
		return modelVersion;
	}
}

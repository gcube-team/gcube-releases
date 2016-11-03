package org.gcube.datapublishing.sdmx.api.model;

public enum SDMXRegistryInterfaceType {
	RESTV1("REST","1"), 
	RESTV2("REST","2"),
	RESTV2_1("REST","2.1"), 
	SOAPV1("SOAP v1","1"), 
	SOAPV2("SOAP v2","2"), 
	SOAPV2_1("SOAP v2.1","2.1");

	private String type;
	private String modelVersion;

	private SDMXRegistryInterfaceType(String type, String modelVersion) {
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

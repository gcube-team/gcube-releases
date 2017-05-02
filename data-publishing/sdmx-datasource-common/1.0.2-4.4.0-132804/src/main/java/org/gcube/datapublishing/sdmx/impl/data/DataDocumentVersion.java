package org.gcube.datapublishing.sdmx.impl.data;

public enum DataDocumentVersion {
	V1("1.0"),V2("2.0"),V2_1("2.1");
	
	String version;
	
	private DataDocumentVersion(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
	
}

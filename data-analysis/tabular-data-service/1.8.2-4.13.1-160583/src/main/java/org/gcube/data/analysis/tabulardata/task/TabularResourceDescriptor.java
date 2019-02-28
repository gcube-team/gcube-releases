package org.gcube.data.analysis.tabulardata.task;

public class TabularResourceDescriptor {

	private String name;
	private String version;
	private long refId;
	
	public TabularResourceDescriptor(String name, String version, long refId) {
		super();
		this.name = name;
		this.version = version;
		this.refId = refId;
	}
	
	public String getName() {
		return name;
	}
	public String getVersion() {
		return version;
	}
	public long getRefId() {
		return refId;
	}
	
	
	
}

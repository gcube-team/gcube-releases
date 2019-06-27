package org.gcube.application.perform.service.engine.model.importer;



public class ImportRequest {

	private String source;
	private String version;
	private String batchType;
	private Long farmId;
	
	
	public ImportRequest() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ImportRequest(String source, String version, String batchType, Long farmId) {
		super();
		this.source = source;
		this.version = version;
		this.batchType = batchType;
		this.farmId = farmId;
	}


	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getBatchType() {
		return batchType;
	}
	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}
	public Long getFarmId() {
		return farmId;
	}
	public void setFarmId(Long farmId) {
		this.farmId = farmId;
	}


	@Override
	public String toString() {
		return "ImportRequest [source=" + source + ", version=" + version + ", batchType=" + batchType + ", farmId="
				+ farmId + "]";
	}
	
	
}

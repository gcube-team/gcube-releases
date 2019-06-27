package org.gcube.application.perform.service.engine.model.importer;

import java.time.Instant;


public class ImportRoutineDescriptor {

	
	public ImportRoutineDescriptor() {
		// TODO Auto-generated constructor stub
	}
	
	
	


	public ImportRoutineDescriptor(Long id, Long farmId, String batch_type, String sourceUrl, String sourceVersion,
			Instant startTime, Instant endTime, ImportStatus status, String lock, String caller, String computationId,
			String computationUrl, String computationOperator, String computationOperatorName,
			String computationRequest, String submitterIdentity) {
		super();
		this.id = id;
		this.farmId = farmId;
		this.batch_type = batch_type;
		this.sourceUrl = sourceUrl;
		this.sourceVersion = sourceVersion;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.lock = lock;
		this.caller = caller;
		this.computationId = computationId;
		this.computationUrl = computationUrl;
		this.computationOperator = computationOperator;
		this.computationOperatorName = computationOperatorName;
		this.computationRequest = computationRequest;
		this.submitterIdentity = submitterIdentity;
	}





	private Long id;
	private Long farmId;
	private String batch_type;
	private String sourceUrl;
	private String sourceVersion;
	
	private Instant startTime;
	private Instant endTime;
	
	private ImportStatus status;
	private String lock;
	private String caller;
	
	private String computationId;
	private String computationUrl;
	private String computationOperator;
	private String computationOperatorName;
	private String computationRequest;
	
	private String submitterIdentity;
	public String getSubmitterIdentity() {
		return submitterIdentity;
	}
	public void setSubmitterIdentity(String submitterIdentity) {
		this.submitterIdentity = submitterIdentity;
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFarmId() {
		return farmId;
	}
	public void setFarmId(Long farmId) {
		this.farmId = farmId;
	}
	public String getBatch_type() {
		return batch_type;
	}
	public void setBatch_type(String batch_type) {
		this.batch_type = batch_type;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getSourceVersion() {
		return sourceVersion;
	}
	public void setSourceVersion(String sourceVersion) {
		this.sourceVersion = sourceVersion;
	}
	public Instant getStartTime() {
		return startTime;
	}
	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}
	public Instant getEndTime() {
		return endTime;
	}
	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}
	public ImportStatus getStatus() {
		return status;
	}
	public void setStatus(ImportStatus status) {
		this.status = status;
	}
	public String getLock() {
		return lock;
	}
	public void setLock(String lock) {
		this.lock = lock;
	}
	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public String getComputationId() {
		return computationId;
	}
	public void setComputationId(String computationId) {
		this.computationId = computationId;
	}
	public String getComputationUrl() {
		return computationUrl;
	}
	public void setComputationUrl(String computationUrl) {
		this.computationUrl = computationUrl;
	}
	public String getComputationOperator() {
		return computationOperator;
	}
	public void setComputationOperator(String computationOperator) {
		this.computationOperator = computationOperator;
	}
	public String getComputationOperatorName() {
		return computationOperatorName;
	}
	public void setComputationOperatorName(String computationOperatorName) {
		this.computationOperatorName = computationOperatorName;
	}
	public String getComputationRequest() {
		return computationRequest;
	}
	public void setComputationRequest(String computationRequest) {
		this.computationRequest = computationRequest;
	}


	@Override
	public String toString() {
		return "ImportRoutineDescriptor [id=" + id + ", farmId=" + farmId + ", batch_type=" + batch_type
				+ ", sourceUrl=" + sourceUrl + ", sourceVersion=" + sourceVersion + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", status=" + status + ", lock=" + lock + ", caller=" + caller
				+ ", computationId=" + computationId + ", computationUrl=" + computationUrl + ", computationOperator="
				+ computationOperator + ", computationOperatorName=" + computationOperatorName + ", computationRequest="
				+ computationRequest + ", submitterIdentity=" + submitterIdentity + "]";
	}


	
	
}

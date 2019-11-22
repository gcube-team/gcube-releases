package org.gcube.data.publishing.gCatFeeder.service.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


public class ExecutionDescriptor {

	private Long id;
	private Set<String> collectors=new HashSet<>();
	private Set<String> catalogues=new HashSet<>();
	
	
	private String callerEncryptedToken;
	private String callerIdentity;
	private String callerContext;
	
	private ExecutionStatus status;
	private String reportUrl;
	
	private Instant startTime;
	private Instant endTime;
	
	
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Set<String> getCollectors() {
		return collectors;
	}
	public void setCollectors(Set<String> collectors) {
		this.collectors = collectors;
	}
	public Set<String> getCatalogues() {
		return catalogues;
	}
	public void setCatalogues(Set<String> catalogues) {
		this.catalogues = catalogues;
	}
	
	public String getCallerEncryptedToken() {
		return callerEncryptedToken;
	}
	public void setCallerEncryptedToken(String callerEncryptedToken) {
		this.callerEncryptedToken = callerEncryptedToken;
	}
	public String getCallerIdentity() {
		return callerIdentity;
	}
	public void setCallerIdentity(String callerIdentity) {
		this.callerIdentity = callerIdentity;
	}
	public String getCallerContext() {
		return callerContext;
	}
	public void setCallerContext(String callerContext) {
		this.callerContext = callerContext;
	}
	public ExecutionStatus getStatus() {
		return status;
	}
	public void setStatus(ExecutionStatus status) {
		this.status = status;
	}
	public String getReportUrl() {
		return reportUrl;
	}
	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecutionDescriptor other = (ExecutionDescriptor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ExecutionDescriptor [id=" + id + ", collectors=" + collectors + ", catalogues=" + catalogues
				+ ", callerEncryptedToken=" + callerEncryptedToken + ", callerIdentity=" + callerIdentity
				+ ", callerContext=" + callerContext + ", status=" + status + ", reportUrl=" + reportUrl + "]";
	}
	
	
	
}

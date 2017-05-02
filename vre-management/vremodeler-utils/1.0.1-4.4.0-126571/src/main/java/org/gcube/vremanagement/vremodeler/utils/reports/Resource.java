package org.gcube.vremanagement.vremodeler.utils.reports;

import java.io.Serializable;

public class Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4929870516367802531L;
	private String resourceId;
	private String resourceType;
	private Status status;
	
	public Resource(String resourceId, String resourceType) {
		super();
		this.resourceId = resourceId;
		this.resourceType = resourceType;
		this.status=Status.Running;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
	public int hashCode(){
		return resourceId.hashCode();
	}
}

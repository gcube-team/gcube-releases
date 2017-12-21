package org.gcube.vremanagement.vremodeler.utils.reports;

import java.io.Serializable;

public class ServiceReport implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6061025649581168168L;

	private Status status;
	
	private String serviceClass;
	private String serviceName;
	private String serviceVersion;
	
	public ServiceReport(){
		this.status= Status.Waiting;
	}
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getServiceClass() {
		return serviceClass;
	}
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceVersion() {
		return serviceVersion;
	}
	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
	
}